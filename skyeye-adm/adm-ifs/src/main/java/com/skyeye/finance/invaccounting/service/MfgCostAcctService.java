package com.skyeye.finance.invaccounting.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 完工入库 / 制造费用结转 —— 复用存货核算 + 业务事项事件。
 * 生产领料/补料由仓库出库审批自动推凭证，不再提供手工领料记账接口。
 */
public interface MfgCostAcctService {

    void postProdFinish(InputObject inputObject, OutputObject outputObject);

    void postMfgOverhead(InputObject inputObject, OutputObject outputObject);
}
