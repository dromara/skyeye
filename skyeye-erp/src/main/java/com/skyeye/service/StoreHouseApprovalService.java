/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface StoreHouseApprovalService {

	/**
	 * 采购、零售、销售出入库单据审核修改库存以及订单信息
	 *
	 * @param orderId 出入库单据id
	 * @param approvalResult 审批结果：pass:同意；其他:不同意
	 * @throws Exception
	 */
	void approvalOrder(String orderId, String approvalResult) throws Exception;

	public void queryNotApprovedOtherOrderList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOtherOrderStateToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPassApprovedOtherOrderList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
