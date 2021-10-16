/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ProProjectDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * 项目立项审核监听
 * @author 卫志强
 *
 */
public class ProProjectNodeListener  implements JavaDelegate{
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 项目立项在工作流中的key
	 */
	private static final String PRO_PROJECT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_PROJECT_PAGE.getKey();

	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ProProjectDao proProjectDao = SpringUtils.getBean(ProProjectDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 根据流程id获取对应的项目信息
		Map<String, Object> map = proProjectDao.queryProProjectByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("获取项目数据失败");
		}
		String proId = map.get("id").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){
			// 通过
			// 修改项目立项状态，state='1'为立项成功
			proProjectDao.editProProjectToPassByUseId(map);
			// 修改项目立项状态
			map.put("state", 11);
			proProjectDao.editProProjectStateResultById(map);
		}else{
			// 不通过
			// 修改项目立项状态，state='2'为立项失败
			proProjectDao.editProProjectToNoPassByUseId(map);
			// 修改项目立项状态
			map.put("state", 12);
			proProjectDao.editProProjectStateResultById(map);
		}
		
		// 编辑流程表参数
		ActivitiRunFactory.run(PRO_PROJECT_PAGE_KEY).editApplyMationInActiviti(proId);
	}
}
