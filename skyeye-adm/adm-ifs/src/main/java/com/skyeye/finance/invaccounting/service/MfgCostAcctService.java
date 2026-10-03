package com.skyeye.finance.invaccounting.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 生产领料/完工/制费结转 —— 复用存货核算 + 业务事项事件。
 */
public interface MfgCostAcctService {

    void postProdPick(InputObject inputObject, OutputObject outputObject);

    void postProdFinish(InputObject inputObject, OutputObject outputObject);

    void postMfgOverhead(InputObject inputObject, OutputObject outputObject);
}
