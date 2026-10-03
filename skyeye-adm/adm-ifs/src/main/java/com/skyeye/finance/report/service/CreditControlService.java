package com.skyeye.finance.report.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 财务反哺业务：信用校验（超期应收控制）。
 */
public interface CreditControlService {

    /**
     * 校验客户是否允许发货/下单。参数：customerId、amount(可选)、maxOverdueDays(可选默认30)
     */
    void checkCustomerCredit(InputObject inputObject, OutputObject outputObject);
}
