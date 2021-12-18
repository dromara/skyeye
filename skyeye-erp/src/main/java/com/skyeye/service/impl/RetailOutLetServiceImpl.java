/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.dao.RetailOutLetDao;
import com.skyeye.factory.ErpRunFactory;
import com.skyeye.service.RetailOutLetService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: RetailOutLetServiceImpl
 * @Description: 零售出库单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:15
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class RetailOutLetServiceImpl implements RetailOutLetService{
	
	@Autowired
	private RetailOutLetDao retailOutLetDao;
	
	/**
	 * 零售出库单类型
	 */
	private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.OUT_IS_RETAIL.getType();

	/**
     * 获取零售出库列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryRetailOutLetToList(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderList();
	}

	/**
     * 新增零售出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@ActivitiAndBaseTransaction(value = {"transactionManager"})
	public void insertRetailOutLetMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).insertOrderMation();
	}

	/**
     * 编辑零售出库信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryRetailOutLetMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderMationToEditById();
	}

	/**
     * 编辑零售出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@ActivitiAndBaseTransaction(value = {"transactionManager"})
	public void editRetailOutLetMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		ErpRunFactory.run(inputObject, outputObject, ORDER_TYPE).editOrderMationById();
	}

	/**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = retailOutLetDao.queryMationToExcel(params);
        String[] key = new String[]{"defaultNumber", "supplierName", "materialNames", "totalPrice", "changeAmount", "operPersonName", "operTime"};
        String[] column = new String[]{"单据编号", "会员", "关联产品", "合计金额", "收款", "操作人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data", "data", "data"};
        //零售出库单信息导出
        ExcelUtil.createWorkBook("零售出库单", "零售出库单详细", beans, key, column, dataType, inputObject.getResponse());
	}

}
