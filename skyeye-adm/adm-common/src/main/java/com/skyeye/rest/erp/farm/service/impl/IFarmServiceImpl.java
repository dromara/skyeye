package com.skyeye.rest.erp.farm.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.farm.rest.IFarmRest;
import com.skyeye.rest.erp.farm.service.IFarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IFarmServiceImpl extends IServiceImpl implements IFarmService {

    @Autowired
    private IFarmRest iFarmRest;

    @Override
    public List<Map<String, Object>> queryFarmByIds(String farmIds) {
        return ExecuteFeignClient.get(() -> iFarmRest.queryFarmByIds(farmIds)).getRows();
    }
}
