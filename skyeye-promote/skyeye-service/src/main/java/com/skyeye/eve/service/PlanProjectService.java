/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: PlanProjectService
 * @Description: 项目规划服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PlanProjectService {

	public void queryPlanProjectList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPlanProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deletePlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPlanProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
