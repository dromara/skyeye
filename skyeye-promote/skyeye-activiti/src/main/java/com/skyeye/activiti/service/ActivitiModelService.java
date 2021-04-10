/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ActivitiModelService
 * @Description: 工作流服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface ActivitiModelService {

	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToStartProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryReleasedActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
