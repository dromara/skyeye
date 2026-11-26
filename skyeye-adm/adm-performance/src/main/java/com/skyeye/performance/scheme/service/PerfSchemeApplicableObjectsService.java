package com.skyeye.performance.scheme.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.performance.scheme.entity.PerfSchemeApplicableObjects;

import java.util.List;
import java.util.Map;

public interface PerfSchemeApplicableObjectsService extends SkyeyeBusinessService<PerfSchemeApplicableObjects> {

    void deleteBySchemeId(String schemeId);

    void saveApplicableObjects(String schemeId, List<PerfSchemeApplicableObjects> list);

    List<PerfSchemeApplicableObjects> queryBySchemeId(String schemeId);

    Map<String, List<PerfSchemeApplicableObjects>> queryBySchemeIds(List<String> schemeIds);
}
