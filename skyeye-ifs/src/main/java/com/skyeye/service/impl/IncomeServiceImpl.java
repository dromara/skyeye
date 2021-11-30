/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.dao.IncomeDao;
import com.skyeye.factory.IfsOrderRunFactory;
import com.skyeye.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: IncomeServiceImpl
 * @Description: 记账收入管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:31
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeDao incomeDao;
    
    /**
     * 记账收入类型
     */
    private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.INCOME_ORDER.getType();

    /**
     * 查询记账收入列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryIncomeByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderList();
    }

    /**
     * 添加记账收入
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    @Transactional(value="transactionManager")
    public void insertIncome(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).insertOrderMation();
    }

    /**
     * 查询记账收入用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryIncomeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderMationToEditById();
    }

    /**
     * 编辑记账收入信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    @Transactional(value="transactionManager")
    public void editIncomeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).editOrderMationById();
    }

    /**
     * 删除记账收入信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteIncomeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).deleteOrderMationById();
    }

    /**
     * 查看记账收入详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryIncomeByDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderMationDetailsById();
    }

    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception {
        List<Map<String, Object>> beans = IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderList();
        String[] key = new String[]{"billNo", "customerName", "totalPrice", "hansPersonName", "billTime"};
        String[] column = new String[]{"单据编号", "往来单位", "合计金额", "经手人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data"};
        //记账收入信息导出
        ExcelUtil.createWorkBook("记账收入", "记账收入详细", beans, key, column, dataType, inputObject.getResponse());
	}
}
