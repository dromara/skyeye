package com.skyeye.finance.event.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.event.classenum.BizAcctEventState;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.finance.event.dao.BizAcctEventDao;
import com.skyeye.finance.event.entity.BizAcctEvent;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.journal.service.JournalVoucherService;
import com.skyeye.finance.template.entity.VoucherTemplate;
import com.skyeye.finance.template.service.VoucherTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务会计事件：收付/存货/关账等统一入口。
 * 幂等键防重复记账 → 查启用模板 → 生成凭证 → 按模板决定是否自动过账。
 * 失败只记事件状态，不向上抛，避免打断业务主链路；可重试。
 */
@Service
@SkyeyeService(name = "业务会计事件", groupName = "财务中枢")
public class BizAcctEventServiceImpl extends SkyeyeBusinessServiceImpl<BizAcctEventDao, BizAcctEvent>
    implements BizAcctEventService {

    @Autowired
    private VoucherTemplateService voucherTemplateService;

    @Autowired
    private JournalVoucherService journalVoucherService;

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        ifsSetOfBooksService.setMationForMap(beans, "setOfBooksId", "setOfBooksMation");
        return beans;
    }

    @Override
    public void acceptEvent(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> result = acceptEvent(inputObject.getParams());
        outputObject.setBean(result);
    }

    /**
     * 核心受理流程：
     * 1) 幂等：同一 idempotencyKey 直接返回已有结果
     * 2) 落 PENDING 事件 + payload
     * 3) 模板生成凭证并 createAndOptionallyPost
     * 4) SUCCESS / FAILED
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> acceptEvent(Map<String, Object> params) {
        String eventType = required(params, "eventType");
        String sourceType = required(params, "sourceType");
        String sourceId = required(params, "sourceId");
        String version = params.get("version") == null ? "1" : params.get("version").toString();
        // 默认幂等键：事项+来源+单据+版本，业务方可自行传入 idempotencyKey
        String idempotencyKey = params.get("idempotencyKey") == null
            ? eventType + ":" + sourceType + ":" + sourceId + ":" + version
            : params.get("idempotencyKey").toString();

        BizAcctEvent success = findSuccessBySource(eventType, sourceType, sourceId);
        if (ObjectUtil.isNotEmpty(success)) {
            return duplicatedResult(success);
        }
        QueryWrapper<BizAcctEvent> existsQ = new QueryWrapper<>();
        existsQ.eq(MybatisPlusUtil.toColumns(BizAcctEvent::getIdempotencyKey), idempotencyKey);
        BizAcctEvent exists = getOne(existsQ, false);
        if (ObjectUtil.isNotEmpty(exists)) {
            return duplicatedResult(exists);
        }

        BizAcctEvent event = new BizAcctEvent();
        event.setId(ToolUtil.getSurFaceId());
        event.setName(eventType + "-" + sourceId);
        event.setEventType(eventType);
        event.setSourceType(sourceType);
        event.setSourceId(sourceId);
        event.setSourceNo(params.get("sourceNo") == null ? null : params.get("sourceNo").toString());
        event.setIdempotencyKey(idempotencyKey);
        event.setSetOfBooksId(params.get("setOfBooksId") == null ? null : params.get("setOfBooksId").toString());
        event.setPayload(JSONUtil.toJsonStr(params));
        event.setState(BizAcctEventState.PENDING.getKey());
        event.setRetryCount(0);

        String userId = "";
        if (InputObject.getLogParamsStatic() != null) {
            userId = String.valueOf(InputObject.getLogParamsStatic().getOrDefault("id", ""));
        }
        createEntity(event, userId);

        return generateVoucher(event, params);
    }

    /**
     * 失败重试：在原事件上重新生成，不新建事件、不换幂等键。
     * 同一来源已成功出凭证、或达到重试上限时拒绝。
     */
    @Override
    public void retryFailedEvent(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        BizAcctEvent event = selectById(id);
        if (!BizAcctEventState.FAILED.getKey().equals(event.getState())) {
            throw new CustomException("仅失败事件可重试");
        }
        int retry = event.getRetryCount() == null ? 0 : event.getRetryCount();
        if (retry >= IfsConstants.MAX_BIZ_ACCT_RETRY) {
            throw new CustomException("已达最大重试次数（" + IfsConstants.MAX_BIZ_ACCT_RETRY + "），请人工处理");
        }
        if (ObjectUtil.isNotEmpty(findSuccessBySource(event.getEventType(), event.getSourceType(), event.getSourceId()))) {
            throw new CustomException("该来源单据已生成凭证，不能再重试");
        }
        Map<String, Object> payload = JSONUtil.toBean(event.getPayload(), Map.class);
        UpdateWrapper<BizAcctEvent> pendingUw = new UpdateWrapper<>();
        pendingUw.eq(CommonConstants.ID, id);
        pendingUw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getState), BizAcctEventState.PENDING.getKey());
        pendingUw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getRetryCount), retry + 1);
        pendingUw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getErrorMsg), "");
        update(pendingUw);
        refreshCache(id);
        event.setRetryCount(retry + 1);
        outputObject.setBean(generateVoucher(event, payload));
    }

    private Map<String, Object> generateVoucher(BizAcctEvent event, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("eventId", event.getId());
        result.put("duplicated", false);
        try {
            if (BizAcctEventType.SALES_INVOICE.getKey().equals(event.getEventType())
                && isZeroAmount(params.get("taxAmount"))) {
                // 收入已在应收事项确认，开票无税时只回写台账
                return skipVoucher(event, result, "销售开票不重复确认收入，无税额时不出凭证");
            }
            VoucherTemplate template = voucherTemplateService.getEnabledTemplate(
                event.getEventType(), event.getSetOfBooksId());
            String booksId = ifsSetOfBooksService.resolveSetOfBooksId(
                StrUtil.blankToDefault(event.getSetOfBooksId(), template.getSetOfBooksId()),
                params.get("voucherDate") == null ? null : params.get("voucherDate").toString());
            event.setSetOfBooksId(booksId);
            params.put("setOfBooksId", booksId);
            UpdateWrapper<BizAcctEvent> booksUw = new UpdateWrapper<>();
            booksUw.eq(CommonConstants.ID, event.getId());
            booksUw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getSetOfBooksId), booksId);
            update(booksUw);

            Map<String, Object> payload = new HashMap<>(params);
            payload.put("idempotencyKey", event.getIdempotencyKey());
            if (BizAcctEventType.SALES_INVOICE.getKey().equals(event.getEventType())) {
                // 旧模板若仍带 1122/收入行，金额置 0 跳过，避免重复记应收
                payload.put("amount", "0");
                payload.put("amountExTax", "0");
            }
            if ((BizAcctEventType.SALES_OUT.getKey().equals(event.getEventType())
                || BizAcctEventType.SALES_RETURN.getKey().equals(event.getEventType())
                || BizAcctEventType.TRANSFER.getKey().equals(event.getEventType()))
                && isZeroAmount(payload.get("costAmount"))) {
                payload.put("costAmount", payload.get("amount"));
            }
            JournalVoucher voucher = voucherTemplateService.buildVoucherFromTemplate(template, payload);
            boolean autoPost = Integer.valueOf(1).equals(template.getAutoPost());
            JournalVoucher saved = journalVoucherService.createAndOptionallyPost(voucher, autoPost);

            UpdateWrapper<BizAcctEvent> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, event.getId());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getState), BizAcctEventState.SUCCESS.getKey());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getJournalVoucherId), saved.getId());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getErrorMsg), "");
            update(uw);
            refreshCache(event.getId());

            result.put("journalVoucherId", saved.getId());
            result.put("oddNumber", saved.getOddNumber());
            result.put("state", BizAcctEventState.SUCCESS.getKey());
        } catch (Exception ex) {
            UpdateWrapper<BizAcctEvent> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, event.getId());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getState), BizAcctEventState.FAILED.getKey());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getErrorMsg),
                StrUtil.sub(ex.getMessage(), 0, 900));
            update(uw);
            refreshCache(event.getId());
            result.put("state", BizAcctEventState.FAILED.getKey());
            result.put("errorMsg", ex.getMessage());
        }
        return result;
    }

    private BizAcctEvent findSuccessBySource(String eventType, String sourceType, String sourceId) {
        QueryWrapper<BizAcctEvent> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(BizAcctEvent::getEventType), eventType);
        qw.eq(MybatisPlusUtil.toColumns(BizAcctEvent::getSourceType), sourceType);
        qw.eq(MybatisPlusUtil.toColumns(BizAcctEvent::getSourceId), sourceId);
        qw.eq(MybatisPlusUtil.toColumns(BizAcctEvent::getState), BizAcctEventState.SUCCESS.getKey());
        qw.last("LIMIT 1");
        return getOne(qw, false);
    }

    private Map<String, Object> duplicatedResult(BizAcctEvent exists) {
        Map<String, Object> cached = new HashMap<>();
        cached.put("eventId", exists.getId());
        cached.put("journalVoucherId", exists.getJournalVoucherId());
        cached.put("state", exists.getState());
        cached.put("duplicated", true);
        return cached;
    }

    /** 事件记成功但不生成凭证，原因写入 errorMsg 便于事件台查看 */
    private Map<String, Object> skipVoucher(BizAcctEvent event, Map<String, Object> result, String reason) {
        UpdateWrapper<BizAcctEvent> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, event.getId());
        uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getState), BizAcctEventState.SUCCESS.getKey());
        uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getJournalVoucherId), "");
        uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getErrorMsg), StrUtil.sub(reason, 0, 900));
        update(uw);
        refreshCache(event.getId());
        result.put("journalVoucherId", "");
        result.put("skipped", true);
        result.put("state", BizAcctEventState.SUCCESS.getKey());
        result.put("errorMsg", reason);
        return result;
    }

    private boolean isZeroAmount(Object val) {
        if (val == null || StrUtil.isBlank(val.toString())) {
            return true;
        }
        return CalculationUtil.compareTo(val.toString(), "0", IfsConstants.NUM_AFTER_DOT, RoundingMode.HALF_UP) == 0;
    }

    private String required(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null || StrUtil.isBlank(val.toString())) {
            throw new CustomException("缺少参数：" + key);
        }
        return val.toString();
    }
}
