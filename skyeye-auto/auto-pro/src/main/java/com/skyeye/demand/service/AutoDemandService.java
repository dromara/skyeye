/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.demand.service;

import com.skyeye.base.business.service.SkyeyeTeamAuthService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.demand.entity.AutoDemand;

/**
 * @ClassName: AutoDemandService
 * @Description: 需求类服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface AutoDemandService extends SkyeyeTeamAuthService<AutoDemand> {


    void updateStateAutoDemandById(InputObject inputObject, OutputObject outputObject);

    void invalidAutoDemandById(InputObject inputObject, OutputObject outputObject);

    void updateAutoDemandEstimateTime(InputObject inputObject, OutputObject outputObject);

    void aiGenerateDemandDraft(InputObject inputObject, OutputObject outputObject);

    void aiParseDemandDraft(InputObject inputObject, OutputObject outputObject);

}


