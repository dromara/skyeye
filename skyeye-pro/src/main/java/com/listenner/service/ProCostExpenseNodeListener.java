/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ProCostExpenseDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * 
 * @ClassName: ProCostExpenseNodeListener
 * @Description: 费用报销审核监听
 * @author: skyeye云系列--卫志强
 * @date: 2020年10月2日 下午4:52:05
 *   
 * @Copyright: 2020 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class ProCostExpenseNodeListener implements JavaDelegate {

	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 项目费用报销提交到工作流中的key
	 */
	private static final String PRO_COST_EXPENSE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_COST_EXPENSE_PAGE.getKey();

	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ProCostExpenseDao proCostExpenseDao = SpringUtils.getBean(ProCostExpenseDao.class);
		String processInstanceId = execution.getProcessInstanceId();// 流程实例id
		// 获取费用报销工作流关联表信息
		Map<String, Object> map = proCostExpenseDao.queryExpenseIdByProcessInstanceId(processInstanceId);
		if (map == null || map.isEmpty()) {
			throw new Exception("无法获取费用报销数据");
		}
		String expenseId = map.get("expenseId").toString();
		// 服务费用报销状态值
		String value1 = (String) state.getValue(execution);
		if ("pass".equals(value1.toLowerCase())) {// 通过
			// 修改费用报销表状态
			map.put("state", 11);
			proCostExpenseDao.editProCostExpenseStateById(map);
			// 修改费用报销工作流关联表状态
			map.put("processState", 1);
			proCostExpenseDao.editProCostExpenseProcessStateById(map);
		} else {
			// 修改费用报销表状态
			map.put("state", 12);
			proCostExpenseDao.editProCostExpenseStateById(map);
			// 修改费用报销工作流关联表状态
			map.put("processState", 2);
			proCostExpenseDao.editProCostExpenseProcessStateById(map);
		}
		ActivitiRunFactory.run(PRO_COST_EXPENSE_PAGE_KEY).editApplyMationInActiviti(expenseId);
	}

}
