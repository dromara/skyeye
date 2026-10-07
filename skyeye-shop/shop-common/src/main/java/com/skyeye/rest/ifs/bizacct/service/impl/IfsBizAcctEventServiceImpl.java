package com.skyeye.rest.ifs.bizacct.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.ifs.bizacct.rest.IFsBizAcctEventRest;
import com.skyeye.rest.ifs.bizacct.service.IfsBizAcctEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IfsBizAcctEventServiceImpl extends IServiceImpl implements IfsBizAcctEventService {

    @Autowired
    private IFsBizAcctEventRest iFsBizAcctEventRest;

    @Override
    public Map<String, Object> acceptBizAcctEvent(Map<String, Object> map) {
        return ExecuteFeignClient.get(() -> iFsBizAcctEventRest.acceptBizAcctEvent(map)).getBean();
    }
}
