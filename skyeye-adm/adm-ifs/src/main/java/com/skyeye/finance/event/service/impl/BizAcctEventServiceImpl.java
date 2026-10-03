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
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.event.classenum.BizAcctEventState;
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

        QueryWrapper<BizAcctEvent> existsQ = new QueryWrapper<>();
        existsQ.eq(MybatisPlusUtil.toColumns(BizAcctEvent::getIdempotencyKey), idempotencyKey);
        BizAcctEvent exists = getOne(existsQ);
        if (ObjectUtil.isNotEmpty(exists)) {
            Map<String, Object> cached = new HashMap<>();
            cached.put("eventId", exists.getId());
            cached.put("journalVoucherId", exists.getJournalVoucherId());
            cached.put("state", exists.getState());
            cached.put("duplicated", true);
            return cached;
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

        Map<String, Object> result = new HashMap<>();
        result.put("eventId", event.getId());
        result.put("duplicated", false);
        try {
            // 按 eventType 取启用模板（优先账套专属），再按分录行表达式从 payload 取金额
            VoucherTemplate template = voucherTemplateService.getEnabledTemplate(eventType, event.getSetOfBooksId());
            Map<String, Object> payload = new HashMap<>(params);
            payload.put("idempotencyKey", idempotencyKey);
            JournalVoucher voucher = voucherTemplateService.buildVoucherFromTemplate(template, payload);
            boolean autoPost = Integer.valueOf(1).equals(template.getAutoPost());
            JournalVoucher saved = journalVoucherService.createAndOptionallyPost(voucher, autoPost);

            UpdateWrapper<BizAcctEvent> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, event.getId());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getState), BizAcctEventState.SUCCESS.getKey());
            uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getJournalVoucherId), saved.getId());
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
            // 不抛异常，保证业务链路可继续；调用方可据 state=FAILED 重试
        }
        return result;
    }

    /**
     * 失败重试：用原 payload 换新 version 再走 acceptEvent（避开旧幂等键）。
     */
    @Override
    public void retryFailedEvent(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        BizAcctEvent event = selectById(id);
        if (!BizAcctEventState.FAILED.getKey().equals(event.getState())) {
            throw new CustomException("仅失败事件可重试");
        }
        Map<String, Object> payload = JSONUtil.toBean(event.getPayload(), Map.class);
        // 删除旧幂等记录后重建：更新 key 版本
        int retry = event.getRetryCount() == null ? 0 : event.getRetryCount();
        payload.put("version", String.valueOf(retry + 2));
        payload.put("idempotencyKey", null);
        Map<String, Object> result = acceptEvent(payload);
        UpdateWrapper<BizAcctEvent> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, id);
        uw.set(MybatisPlusUtil.toColumns(BizAcctEvent::getRetryCount), retry + 1);
        update(uw);
        outputObject.setBean(result);
    }

    private String required(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null || StrUtil.isBlank(val.toString())) {
            throw new CustomException("缺少参数：" + key);
        }
        return val.toString();
    }
}
