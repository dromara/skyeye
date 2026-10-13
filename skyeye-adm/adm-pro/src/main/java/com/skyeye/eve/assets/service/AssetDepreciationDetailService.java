package com.skyeye.eve.assets.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.assets.entity.AssetDepreciationDetail;

import java.util.List;

public interface AssetDepreciationDetailService extends SkyeyeBusinessService<AssetDepreciationDetail> {

    List<AssetDepreciationDetail> selectByRunId(String runId);

    void deleteByRunId(String runId);
}
