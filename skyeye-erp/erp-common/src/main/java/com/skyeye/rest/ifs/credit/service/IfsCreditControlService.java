package com.skyeye.rest.ifs.credit.service;

import java.util.Map;

public interface IfsCreditControlService {

    Map<String, Object> checkCustomerCredit(Map<String, Object> map);
}
