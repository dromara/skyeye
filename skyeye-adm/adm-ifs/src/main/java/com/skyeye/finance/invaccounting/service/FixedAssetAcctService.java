package com.skyeye.finance.invaccounting.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 固定资产折旧手工记账（业务侧尚无月折旧任务时的补账入口）。
 */
public interface FixedAssetAcctService {

    void postDepreciation(InputObject inputObject, OutputObject outputObject);
}
