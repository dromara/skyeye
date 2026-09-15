/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.demand.service;

import com.skyeye.base.business.service.SkyeyeTeamAuthService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.demand.entity.AutoDemand;

import java.util.List;

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

    void aiGenerateCaseDraft(InputObject inputObject, OutputObject outputObject);

    void aiParseCaseDraft(InputObject inputObject, OutputObject outputObject);

    /**
     * 将被移出成员负责的需求转交给项目经理
     *
     * @param projectId     项目id
     * @param removeUserIds 被移出成员
     * @param chargeUserId  项目经理
     */
    void reassignHandlerToChargeUser(String projectId, List<String> removeUserIds, String chargeUserId);

}


