/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.CrmOpportunityDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 *
 * @ClassName: OpportunityTypeOneNodeListener
 * @Description: 商机审核一阶段监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 20:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class OpportunityTypeOneNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 客户商机申请到工作流中的key
	 */
	private static final String CRM_OPPORTUNITY_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CRM_OPPORTUNITY_PAGE.getKey();
	
	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		CrmOpportunityDao crmOpportunityDao = SpringUtils.getBean(CrmOpportunityDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取商机工作流关联表信息
		Map<String, Object> map = crmOpportunityDao.queryOpportunityIdByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取资产数据");
		}
		String opportunityId = map.get("opportunityId").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equalsIgnoreCase(value1)){//通过
			//修改商机表状态
			map.put("state", 11);
			crmOpportunityDao.editOpportunityStateById(map);
			//修改商机工作流关联表状态
			map.put("processState", 1);
			crmOpportunityDao.editOpportunityProcessStateById(map);
		}else{
			//修改商机表状态
			map.put("state", 12);
			crmOpportunityDao.editOpportunityStateById(map);
			//修改商机工作流关联表状态
			map.put("processState", 2);
			crmOpportunityDao.editOpportunityProcessStateById(map);
		}

		ActivitiRunFactory.run(CRM_OPPORTUNITY_PAGE_KEY).editApplyMationInActiviti(opportunityId);
	}

}
