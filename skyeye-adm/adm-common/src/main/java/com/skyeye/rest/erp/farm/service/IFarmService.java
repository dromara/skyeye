package com.skyeye.rest.erp.farm.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface IFarmService extends IService {

    List<Map<String, Object>> queryFarmByIds(String farmIds);
}
