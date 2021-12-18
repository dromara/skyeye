/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ProWorkloadDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProWorkloadNodeListener
 * @Description: 工作量审核监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/5 22:03
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProWorkloadNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 项目工作量在工作流中的key
	 */
	private static final String PRO_WORKLOAD_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_WORKLOAD_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		ProWorkloadDao proWorkloadDao = SpringUtils.getBean(ProWorkloadDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取工作量工作流关联表信息
		Map<String, Object> map = proWorkloadDao.queryWorkloadIdByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取工作量数据");
		}
		String workloadId = map.get("workloadId").toString();
		// 服务工作量状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			//修改任务表的实际工作量
			List<Map<String, Object>> m = proWorkloadDao.queryWorkloadTaskMationByWorkloadId(map);
			proWorkloadDao.editProTaskByWorkloadId(m);
			
			//修改工作量表状态
			map.put("state", 11);
			proWorkloadDao.editProWorkloadStateAndWorkloadById(map);
			//修改工作量工作流关联表状态
			map.put("processState", 1);
			proWorkloadDao.editProWorkloadProcessStateById(map);
		}else{
			//修改工作量表状态
			map.put("state", 12);
			proWorkloadDao.editProWorkloadStateAndWorkloadById(map);
			//修改工作量工作流关联表状态
			map.put("processState", 2);
			proWorkloadDao.editProWorkloadProcessStateById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(PRO_WORKLOAD_PAGE_KEY).editApplyMationInActiviti(workloadId);
	}

}
