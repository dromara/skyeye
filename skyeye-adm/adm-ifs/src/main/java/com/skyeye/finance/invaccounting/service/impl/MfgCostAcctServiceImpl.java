package com.skyeye.finance.invaccounting.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.invaccounting.service.InvAcctBillService;
import com.skyeye.finance.invaccounting.service.MfgCostAcctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * 完工入库 / 制造费用结转。
 * 生产领料/补料、加工入库(完工)由仓库出入库审批自动推凭证；
 * 本页保留手工补记账：完工补推与制造费用归集。
 * 有明细走存货核算单；无明细直接抛业务事件（须配置对应模板）。
 */
@Service
public class MfgCostAcctServiceImpl implements MfgCostAcctService {

    @Autowired
    private InvAcctBillService invAcctBillService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    /** 完工入库 */
    @Override
    public void postProdFinish(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = fillSource(inputObject.getParams());
        String eventType = BizAcctEventType.PROD_FINISH.getKey();
        params.put("billType", eventType);
        params.put("sourceType", eventType);
        if (hasItems(params.get("items"))) {
            outputObject.setBean(invAcctBillService.createFromBizEvent(params));
            return;
        }
        outputObject.setBean(directEvent(eventType, params));
    }

    /** 制造费用结转：仅事件+金额，无存货明细 */
    @Override
    public void postMfgOverhead(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = fillSource(inputObject.getParams());
        String eventType = BizAcctEventType.MFG_OVERHEAD.getKey();
        params.put("sourceType", eventType);
        outputObject.setBean(directEvent(eventType, params));
    }

    private Map<String, Object> fillSource(Map<String, Object> params) {
        Object sourceId = params.get("sourceId");
        Object sourceNo = params.get("sourceNo");
        if ((sourceId == null || StrUtil.isBlank(sourceId.toString())) && sourceNo != null && StrUtil.isNotBlank(sourceNo.toString())) {
            params.put("sourceId", sourceNo.toString());
        }
        if (params.get("sourceId") == null || StrUtil.isBlank(params.get("sourceId").toString())) {
            throw new CustomException("请填写来源单号");
        }
        return params;
    }

    private Map<String, Object> directEvent(String eventType, Map<String, Object> params) {
        if (params.get("amount") == null || StrUtil.isBlank(params.get("amount").toString())) {
            throw new CustomException("无明细时金额不能为空");
        }
        params.put("eventType", eventType);
        params.put("voucherDate", DateUtil.getYmdTimeAndToString());
        params.put("costAmount", params.get("amount"));
        return bizAcctEventService.acceptEvent(params);
    }

    /** 入参绑定会带上空 items，不能当有明细走存货核算（会强制成本域）。 */
    private boolean hasItems(Object itemsObj) {
        if (itemsObj == null) {
            return false;
        }
        if (itemsObj instanceof Collection) {
            return CollectionUtil.isNotEmpty((Collection<?>) itemsObj);
        }
        String s = itemsObj.toString().trim();
        return StrUtil.isNotBlank(s) && !"[]".equals(s) && !"null".equalsIgnoreCase(s);
    }
}
