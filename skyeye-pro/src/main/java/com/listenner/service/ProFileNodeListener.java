/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ProFileDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * 文档审核监听
 * @author Lenovo
 *
 */
public class ProFileNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 项目文档审核在工作流中配置的key
	 */
	private static final String PRO_FILE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_FILE_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		ProFileDao proFileDao = SpringUtils.getBean(ProFileDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取文档工作流关联表信息
		Map<String, Object> map = proFileDao.queryFileIdByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取资产数据");
		}
		String proFileId = map.get("fileId").toString();
		// 服务文档状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			// 修改文档表状态
			map.put("state", 11);
			proFileDao.editProFileStateById(map);
			// 修改文档工作流关联表状态
			map.put("processState", 1);
			proFileDao.editProFileProcessStateById(map);
		}else{
			// 修改文档表状态
			map.put("state", 12);
			proFileDao.editProFileStateById(map);
			// 修改文档工作流关联表状态
			map.put("processState", 2);
			proFileDao.editProFileProcessStateById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(PRO_FILE_PAGE_KEY).editApplyMationInActiviti(proFileId);
	}

}
