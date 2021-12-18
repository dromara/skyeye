/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ProTaskDao;
import com.skyeye.eve.constants.ScheduleDayConstants;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.ScheduleDayService;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Locale;
import java.util.Map;

/**
 *
 * @ClassName: ProTaskNodeListener
 * @Description: 项目任务审核监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 22:06
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProTaskNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 项目任务申请在工作流中的key
	 */
	private static final String PRO_TASK_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_TASK_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		ProTaskDao proTaskDao = SpringUtils.getBean(ProTaskDao.class);
		SysEnclosureDao sysEnclosureDao = SpringUtils.getBean(SysEnclosureDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取任务工作流关联表信息
		Map<String, Object> map = proTaskDao.queryTaskIdByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取数据");
		}
		String taskId = map.get("taskId").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equalsIgnoreCase(value1)){
			// 通过
			Map<String, Object> m = proTaskDao.queryRestWorkloadById(taskId);
			map.put("estimatedWorkload", m.get("estimatedWorkload").toString());
			if("0".equals(m.get("isFather").toString())){
				if(Integer.parseInt(m.get("estimatedWorkload").toString()) > Integer.parseInt(m.get("restWorkload").toString())){
					map.put("estimatedWorkload", m.get("restWorkload").toString());
				}
			}
			//修改任务表状态
			map.put("state", 11);
			proTaskDao.editProTaskStateAndWorkloadById(map);
			//修改任务工作流关联表状态
			map.put("processState", 1);
			proTaskDao.editProTaskProcessStateById(map);
			synchronizationSchedule(proTaskDao, taskId);
		}else{
			// 修改任务表状态
			map.put("state", 12);
			proTaskDao.editProTaskStateById(map);
			// 修改任务工作流关联表状态
			map.put("processState", 2);
			proTaskDao.editProTaskProcessStateById(map);
		}
		ActivitiRunFactory.run(PRO_TASK_PAGE_KEY).editApplyMationInActiviti(taskId);
	}

	/**
	 * 获取项目任务信息并同步到日程
	 *
	 * @param proTaskDao 项目任务数据操作对象
	 * @param taskId 项目任务id
	 */
	private void synchronizationSchedule(ProTaskDao proTaskDao, String taskId) throws Exception {
		// 获取任务信息
		Map<String, Object> params = proTaskDao.queryProTaskMationByTaskId(taskId);
		ScheduleDayService scheduleDayService = SpringUtils.getBean(ScheduleDayService.class);
		String[] executorIds = params.get("executorIds").toString().split(",");
		for (String executorId : executorIds){
            scheduleDayService.synchronizationSchedule(params.get("taskName").toString(),
                params.get("taskInstructions").toString(),
                String.format(Locale.ROOT, "%s 00:00:00", params.get("startTime").toString()),
                String.format(Locale.ROOT, "%s 23:59:59", params.get("endTime").toString()), executorId, taskId,
                ScheduleDayConstants.ScheduleDayObjectType.OBJECT_TYPE_IS_PRO_TASK);
		}
	}

}
