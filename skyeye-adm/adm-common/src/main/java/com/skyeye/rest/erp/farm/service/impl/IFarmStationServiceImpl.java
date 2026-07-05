package com.skyeye.rest.erp.farm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.farm.rest.IFarmStationRest;
import com.skyeye.rest.erp.farm.service.IFarmStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class IFarmStationServiceImpl extends IServiceImpl implements IFarmStationService {

    @Autowired
    private IFarmStationRest iFarmStationRest;

    @Override
    public List<Map<String, Object>> queryFarmStationById(String workId) {
        return ExecuteFeignClient.get(() -> iFarmStationRest.queryFarmStationById(workId)).getRows();
    }

    @Override
    public List<Map<String, Object>> queryFarmStationByIds(String workIds) {
        if (StrUtil.isBlank(workIds)) {
            return Collections.emptyList();
        }
        return ExecuteFeignClient.get(() -> iFarmStationRest.queryFarmStationByIds(workIds)).getRows();
    }
}
