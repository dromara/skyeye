package com.skyeye.performance.scheme.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.performance.scheme.entity.PerfSchemeItem;

import java.util.List;
import java.util.Map;

public interface PerfSchemeItemService extends SkyeyeBusinessService<PerfSchemeItem> {

    void deleteBySchemeId(String schemeId);

    void saveSchemeItems(String schemeId, List<PerfSchemeItem> itemList);

    List<PerfSchemeItem> queryBySchemeId(String schemeId);

    Map<String, List<PerfSchemeItem>> queryBySchemeIds(List<String> schemeIds);
}
