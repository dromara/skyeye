/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ActivitiUserService
 * @Description: 工作流用户相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActivitiUserService {

    void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception;

    void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 工作流相关的涉及到新增/编辑去操作工作流时的操作
     *
     * @param inputObject inputObject
     * @param outputObject outputObject
     * @param subType 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
     * @param key com.skyeye.common.constans.ActivitiConstants.ActivitiObjectType的key
     * @param dataId 数据的id
     * @param approvalId 审批人id
     * @throws Exception
     */
    void addOrEditToSubmit(InputObject inputObject, OutputObject outputObject,
        int subType, String key, String dataId, String approvalId) throws Exception;

}
