/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.CrmContractDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * 
 * @ClassName: ContractCheckNodeListener
 * @Description: 客户合同审批
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 20:37
 *   
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ContractCheckNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 客户合同提交到工作流中的key
	 */
	private static final String CRM_CONTRACT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CRM_CONTRACT_PAGE.getKey();
	
	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		CrmContractDao crmContractDao = SpringUtils.getBean(CrmContractDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取合同关联表信息
		Map<String, Object> map = crmContractDao.queryCrmContractRelationByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取合同关联表数据");
		}
		String contractId = map.get("contractId").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//审核通过
			map.put("state", 11);
			map.put("approvalState", 1);
			crmContractDao.updateCrmContractState(map);//将合同表的状态更改为审核通过
			crmContractDao.updateCrmContractRelationState(map);//将合同关联表的状态更改为审核通过
		}else{//审核不通过
			map.put("state", 12);
			map.put("approvalState", 2);
			crmContractDao.updateCrmContractState(map);//将合同表的状态更改为审核不通过
			crmContractDao.updateCrmContractRelationState(map);//将合同关联表的状态更改为审核不通过
		}
		// 编辑工作流中的数据
		ActivitiRunFactory.run(CRM_CONTRACT_PAGE_KEY).editApplyMationInActiviti(contractId);
	}

}
