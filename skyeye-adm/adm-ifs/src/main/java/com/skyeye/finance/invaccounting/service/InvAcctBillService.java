package com.skyeye.finance.invaccounting.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.invaccounting.entity.InvAcctBill;

import java.util.Map;

public interface InvAcctBillService extends SkyeyeBusinessService<InvAcctBill> {

    void valueAndPost(InputObject inputObject, OutputObject outputObject);

    Map<String, Object> createFromBizEvent(Map<String, Object> params);
}
