package com.skyeye.eve.assets.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.assets.entity.AssetDepreciationRun;

public interface AssetDepreciationRunService extends SkyeyeBusinessService<AssetDepreciationRun> {

    /** 预览本月可计提资产及金额（不落库） */
    void previewMonthlyDeprec(InputObject inputObject, OutputObject outputObject);

    /** 月结批量计提并推会计事件 */
    void runMonthlyDeprec(InputObject inputObject, OutputObject outputObject);
}
