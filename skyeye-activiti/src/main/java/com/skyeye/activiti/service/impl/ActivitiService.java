/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.dao.ActUserProcessInstanceIdDao;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 流程撤回所需工具类
 * @author 卫志强
 *
 */
@Component
public class ActivitiService {

	@Autowired
    private ActUserProcessInstanceIdDao actUserProcessInstanceIdDao;
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	@Autowired
	private ActivitiModelService activitiModelService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private HistoryService historyService;;

	@Autowired
	private TaskService taskService;

	/**
	 * 撤销已经申请的流程
	 * @param map 必须包含processInstanceId和userId
	 * @return
	 * @throws Exception
	 */
	public void editDsFormContentToRevokeByProcessInstanceId(Map<String, Object> map) throws Exception{
		//判断是否删除成功的参数
		String code = "0";
		String message = "";
		//判断是否是该流程创建人
		Map<String, Object> processMation = actUserProcessInstanceIdDao.queryProcessMationByProcessInstanceId(map);
		if(processMation == null){
			code = "-1";
			message = "该流程不属于当前登录人.";
		}else{
			String processInstanceId = map.get("processInstanceId").toString();
			String deleteFilePath = String.format("%supload/activiti/%s.png", tPath, processInstanceId);
			FileUtil.deleteFile(deleteFilePath);
			String userId = map.get("userId").toString();
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			if(task == null){
				code = "-1";
				message = "该流程未启动或者已结束.";
			}else{
				Map<String, Object> variables = taskService.getVariables(task.getId());
				Object o = variables.get("leaveOpinionList");
				boolean edit = true;
				if (o != null) {
					//获取历史审核信息
					List<Map<String, Object>> leaveList = (List<Map<String, Object>>) o;
					for(Map<String, Object> leave : leaveList){
						if(!userId.equals(leave.get("opId").toString())){
							edit = false;//不可编辑
							break;
						}
					}
				}
				if(edit){
					// 可以编辑
					runtimeService.deleteProcessInstance(processInstanceId, "用户撤销");
					// 删除流程历史信息
					historyService.deleteHistoricProcessInstance(processInstanceId);
					// 删除数据库流程信息
					actUserProcessInstanceIdDao.deleteProcessMationByProcessInstanceId(map);
					// 删除流程在redis中的缓存
					activitiModelService.deleteProcessInRedisMation(task.getProcessInstanceId());
				}else{
					code = "-1";
					message = "流程已有审批，不能撤回.";
				}
			}
		}
		map.put("code", code);
		map.put("message", message);
	}
	
}
