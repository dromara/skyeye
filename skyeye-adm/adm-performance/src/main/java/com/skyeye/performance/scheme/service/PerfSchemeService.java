package com.skyeye.performance.scheme.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.performance.scheme.entity.PerfScheme;

import java.util.List;

public interface PerfSchemeService extends SkyeyeBusinessService<PerfScheme> {

    /**
     * 根据方案适用对象解析出员工id列表
     */
    List<String> resolveStaffIds(String schemeId);
}
