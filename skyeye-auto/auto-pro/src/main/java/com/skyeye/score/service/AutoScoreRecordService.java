/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.score.entity.AutoScoreRecord;

/**
 * @ClassName: AutoScoreRecordService
 * @Description: 需求积分流水服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoScoreRecordService extends SkyeyeBusinessService<AutoScoreRecord> {

    void grantDemandScoreByState(AutoDemand demand, String userId);

    void settleResolvedBug(AutoBug bug, String userId);

    void queryMyAutoScore(InputObject inputObject, OutputObject outputObject);

    void queryAutoScoreBoard(InputObject inputObject, OutputObject outputObject);

    void settleAutoScore(InputObject inputObject, OutputObject outputObject);

}
