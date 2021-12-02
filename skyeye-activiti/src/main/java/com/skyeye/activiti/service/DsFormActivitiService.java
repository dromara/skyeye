/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: DsFormActivitiService
 * @Description: 动态表单类型的工作流相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormActivitiService {

    void insertDSFormProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

    void editDsFormContentToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
