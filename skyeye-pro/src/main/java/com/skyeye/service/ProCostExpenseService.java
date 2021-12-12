/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: ProCostExpenseService
 * @Description: 项目费用报销管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/11 22:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProCostExpenseService {

	public void queryProCostExpenseList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertProCostExpenseMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProCostExpenseMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProCostExpenseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProCostExpenseMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProCostExpenseToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProCostExpenseProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteProCostExpenseMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProCostExpenseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
