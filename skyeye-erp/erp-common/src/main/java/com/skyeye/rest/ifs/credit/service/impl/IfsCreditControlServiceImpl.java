package com.skyeye.rest.ifs.credit.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.ifs.credit.rest.IFsCreditControlRest;
import com.skyeye.rest.ifs.credit.service.IfsCreditControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IfsCreditControlServiceImpl extends IServiceImpl implements IfsCreditControlService {

    @Autowired
    private IFsCreditControlRest iFsCreditControlRest;

    @Override
    public Map<String, Object> checkCustomerCredit(Map<String, Object> map) {
        return ExecuteFeignClient.get(() -> iFsCreditControlRest.checkCustomerCredit(map)).getBean();
    }
}
