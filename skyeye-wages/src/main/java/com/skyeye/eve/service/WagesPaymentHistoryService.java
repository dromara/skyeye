package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface WagesPaymentHistoryService {

    public void queryAllGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAllNotGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
