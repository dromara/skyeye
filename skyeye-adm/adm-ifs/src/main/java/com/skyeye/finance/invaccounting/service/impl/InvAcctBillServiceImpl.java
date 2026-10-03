package com.skyeye.finance.invaccounting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.constants.IfsConstants;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.invaccounting.classenum.InvAcctBillState;
import com.skyeye.finance.invaccounting.dao.InvAcctBillDao;
import com.skyeye.finance.invaccounting.entity.InvAcctBill;
import com.skyeye.finance.invaccounting.entity.InvAcctItem;
import com.skyeye.finance.invaccounting.service.InvAcctBillService;
import com.skyeye.finance.invaccounting.service.InvAcctItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存货核算单：暂估/入库/出库/销售成本等。
 * 计价后通过 BizAcctEvent（eventType=billType）生成并过账凭证。
 */
@Service
@SkyeyeService(name = "存货核算管理", groupName = "财务中枢")
public class InvAcctBillServiceImpl extends SkyeyeBusinessServiceImpl<InvAcctBillDao, InvAcctBill> implements InvAcctBillService {

    @Autowired
    private InvAcctItemService invAcctItemService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Override
    public void validatorEntity(InvAcctBill entity) {
        super.validatorEntity(entity);
        if (CollectionUtil.isEmpty(entity.getItems())) {
            throw new CustomException("存货核算明细不能为空");
        }
        // 明细金额缺省时用单价×数量；汇总为单据总金额
        String total = "0";
        for (InvAcctItem item : entity.getItems()) {
            if (StrUtil.isBlank(item.getAmount())) {
                String price = StrUtil.blankToDefault(item.getPrice(), "0");
                String qty = StrUtil.blankToDefault(item.getQty(), "0");
                item.setAmount(CalculationUtil.multiply(price, qty, IfsConstants.NUM_AFTER_DOT));
            }
            total = CalculationUtil.add(total, item.getAmount(), IfsConstants.NUM_AFTER_DOT);
        }
        entity.setTotalAmount(total);
    }

    @Override
    protected void createPrepose(InvAcctBill entity) {
        entity.setState(InvAcctBillState.DRAFT.getKey());
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        entity.setOddNumber(oddNumber);
        if (StrUtil.isBlank(entity.getName())) {
            entity.setName(oddNumber);
        }
    }

    @Override
    public void writePostpose(InvAcctBill entity, String userId) {
        invAcctItemService.saveLinkList(entity.getId(), entity.getItems());
        super.writePostpose(entity, userId);
    }

    @Override
    public InvAcctBill getDataFromDb(String id) {
        InvAcctBill bill = super.getDataFromDb(id);
        bill.setItems(invAcctItemService.selectByPId(id));
        return bill;
    }

    @Override
    public void valueAndPost(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        InvAcctBill bill = doValueAndPost(id);
        outputObject.setBean(bill);
    }

    /**
     * 计价 → 抛业务事件生成凭证 → 单据 VOUCHERED。
     * billType 即 eventType，须先配置对应凭证模板。
     */
    private InvAcctBill doValueAndPost(String id) {
        InvAcctBill bill = selectById(id);
        if (InvAcctBillState.VOUCHERED.getKey().equals(bill.getState())) {
            throw new CustomException("已生成凭证，勿重复处理");
        }
        UpdateWrapper<InvAcctBill> valued = new UpdateWrapper<>();
        valued.eq(CommonConstants.ID, id);
        valued.set(MybatisPlusUtil.toColumns(InvAcctBill::getState), InvAcctBillState.VALUED.getKey());
        update(valued);

        Map<String, Object> event = new HashMap<>();
        event.put("eventType", bill.getBillType());
        event.put("sourceType", StrUtil.blankToDefault(bill.getSourceType(), "INV_ACCT"));
        event.put("sourceId", StrUtil.blankToDefault(bill.getSourceId(), bill.getId()));
        event.put("sourceNo", bill.getSourceNo());
        event.put("setOfBooksId", bill.getSetOfBooksId());
        event.put("voucherDate", bill.getBizDate());
        event.put("amount", bill.getTotalAmount());
        event.put("costAmount", bill.getTotalAmount());
        // 辅助核算：取首行物料/仓库写入事件（模板行开关开启时才落入分录）
        if (CollectionUtil.isNotEmpty(bill.getItems())) {
            InvAcctItem first = bill.getItems().get(0);
            event.put("materialId", first.getMaterialId());
            event.put("depotId", first.getDepotId());
        }
        event.put("summary", "存货核算-" + bill.getOddNumber());
        event.put("version", "inv-" + bill.getId());
        Map<String, Object> result = bizAcctEventService.acceptEvent(event);

        UpdateWrapper<InvAcctBill> posted = new UpdateWrapper<>();
        posted.eq(CommonConstants.ID, id);
        posted.set(MybatisPlusUtil.toColumns(InvAcctBill::getState), InvAcctBillState.VOUCHERED.getKey());
        if (result.get("journalVoucherId") != null) {
            posted.set(MybatisPlusUtil.toColumns(InvAcctBill::getJournalVoucherId), result.get("journalVoucherId").toString());
        }
        update(posted);
        refreshCache(id);
        return selectById(id);
    }

    /**
     * 采销/物流等业务回调：建核算单并立即计价过账。
     * params 需含 billType、costDomainId、setOfBooksId、items。
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> createFromBizEvent(Map<String, Object> params) {
        InvAcctBill bill = new InvAcctBill();
        bill.setId(ToolUtil.getSurFaceId());
        bill.setBillType(required(params, "billType"));
        bill.setCostDomainId(required(params, "costDomainId"));
        bill.setSetOfBooksId(required(params, "setOfBooksId"));
        bill.setBizDate(StrUtil.blankToDefault(str(params.get("bizDate")), DateUtil.getYmdTimeAndToString()));
        bill.setSourceType(str(params.get("sourceType")));
        bill.setSourceId(str(params.get("sourceId")));
        bill.setSourceNo(str(params.get("sourceNo")));
        bill.setState(InvAcctBillState.DRAFT.getKey());

        List<InvAcctItem> items = new ArrayList<>();
        Object itemsObj = params.get("items");
        if (itemsObj instanceof List) {
            List<?> raw = (List<?>) itemsObj;
            for (Object o : raw) {
                Map<String, Object> m = o instanceof Map ? (Map<String, Object>) o : JSONUtil.toBean(JSONUtil.toJsonStr(o), Map.class);
                InvAcctItem item = new InvAcctItem();
                item.setMaterialId(str(m.get("materialId")));
                item.setNormsId(str(m.get("normsId")));
                item.setDepotId(str(m.get("depotId")));
                item.setDirection(m.get("direction") == null ? 1 : Integer.parseInt(m.get("direction").toString()));
                item.setQty(str(m.get("qty")));
                item.setPrice(str(m.get("price")));
                item.setAmount(str(m.get("amount")));
                items.add(item);
            }
        }
        bill.setItems(items);
        String userId = "";
        if (InputObject.getLogParamsStatic() != null) {
            userId = String.valueOf(InputObject.getLogParamsStatic().getOrDefault("id", ""));
        }
        createEntity(bill, userId);

        Map<String, Object> out = new HashMap<>();
        out.put("invAcctBillId", bill.getId());
        out.put("bill", doValueAndPost(bill.getId()));
        return out;
    }

    private String required(Map<String, Object> params, String key) {
        String v = str(params.get(key));
        if (StrUtil.isBlank(v)) {
            throw new CustomException("缺少参数：" + key);
        }
        return v;
    }

    private String str(Object o) {
        return o == null ? null : o.toString();
    }
}
