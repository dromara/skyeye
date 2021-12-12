/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProTaskDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.ProTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProTaskServiceImpl
 * @Description: 项目任务管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:01
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ProTaskServiceImpl implements ProTaskService {
	
	@Autowired
	private ProTaskDao proTaskDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	@Autowired
	private ActivitiUserService activitiUserService;

	/**
	 * 项目任务申请在工作流中的key
	 */
	private static final String PRO_TASK_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_TASK_PAGE.getKey();

    /**
    *
    * @Title: queryProTaskList
    * @Description: 获取任务管理表列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProTaskList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = proTaskDao.queryProTaskList(map);
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, PRO_TASK_PAGE_KEY).getActModelTitle();
		for(Map<String, Object> bean : beans){
			bean.put("taskType", taskType);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
    *
    * @Title: insertProTaskMation
    * @Description: 新增任务
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void insertProTaskMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proTaskDao.queryProTaskByTaskName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该任务名称已存在，不能重复添加！");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			if(map.get("endTime").toString().isEmpty()){
				map.put("endTime", null);
			}
			String id = ToolUtil.getSurFaceId();//任务id
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			proTaskDao.insertProTaskMation(map);
			// 操作工作流数据
			activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
				PRO_TASK_PAGE_KEY, id, map.get("approvalId").toString());
		}
	}
	
	/**
    *
    * @Title: queryProTaskMationToDetails
    * @Description: 任务详情
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProTaskMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proTaskDao.queryProTaskMationToDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		if(!ToolUtil.isBlank(bean.get("executionEnclosureInfo").toString())){
			bean.put("executionEnclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("executionEnclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
    *
    * @Title: queryProTaskMationToEdit
    * @Description: 获取任务信息用以编辑
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProTaskMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proTaskDao.queryProTaskMationToEdit(map);
		List<Map<String,Object>> beans = null;
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		//集合中放入执行人信息
		beans = proTaskDao.queryPerformIdByProTaskId(map);
        bean.put("performId", beans);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
    *
    * @Title: editProTaskMation
    * @Description: 编辑任务信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editProTaskMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proTaskDao.queryProTaskByTaskName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该任务名称已存在，不可进行二次保存！");
		}else{
			String taskId = map.get("id").toString();
			if(map.get("endTime").toString().isEmpty()){
				map.put("endTime", null);
			}
			proTaskDao.editProTaskMation(map);
			// 操作工作流数据
			activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
				PRO_TASK_PAGE_KEY, taskId, map.get("approvalId").toString());
		}
	}
	
	/**
	*
	* @Title: deleteProTaskMationById
	* @Description: 根据id删除任务
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	@Transactional(value = "transactionManager")
	public void deleteProTaskMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proTaskDao.queryProTaskStateAndPidById(map);//查询任务的状态
		//草稿、审核不通过、撤销状态下可以删除
		if("0".equals(bean.get("state").toString()) || "12".equals(bean.get("state").toString()) || "5".equals(bean.get("state").toString())){
			if("0".equals(bean.get("pId").toString())){//该任务为主任务
				proTaskDao.deleteAllProTaskProcessMationByPid(map);//根据主任务ID删除审批表中的任务
				proTaskDao.deleteAllProTaskMationByPid(map);//根据主任务ID任务表中的任务
			}else{//该任务为子任务
				proTaskDao.deleteProTaskMationById(map);//删除任务表中的任务
				proTaskDao.deleteProTaskProcessMationById(map);//删除任务审批表中的任务
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
    *
    * @Title: editProTaskProcessToRevoke
    * @Description: 撤销任务审批申请
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editProTaskProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, PRO_TASK_PAGE_KEY).revokeActivi();
	}

	/**
    *
    * @Title: editProTaskToApprovalById
    * @Description: 根据任务Id提交审批
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editProTaskToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询任务的信息
		Map<String, Object> bean = proTaskDao.queryProTaskMationByTaskId(id);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "5".equals(bean.get("state").toString())){
			// 草稿、审核不通过、撤销状态下可以提交审批
			activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
				PRO_TASK_PAGE_KEY, id, map.get("approvalId").toString());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
     * 
     * @Title: updateProTaskToCancellation
     * @Description: 作废任务
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
	@Override
	@Transactional(value="transactionManager")
	public void updateProTaskToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询任务的信息
		Map<String, Object> bean = proTaskDao.queryProTaskMationByTaskId(id);
		if("0".equals(bean.get("state").toString())
				|| "11".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "2".equals(bean.get("state").toString())
				|| "5".equals(bean.get("state").toString())){
			// 草稿、审核通过、审核不通过、执行中或者撤销状态下可以作废
			proTaskDao.updateProTaskToCancellation(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
     * 
     * @Title: queryMyProTaskList
     * @Description: 获取我的任务列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
	@Override
	public void queryMyProTaskList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		map.put("userId", userId);
		List<Map<String, Object>> beans = proTaskDao.queryMyProTaskList(map);
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, PRO_TASK_PAGE_KEY).getActModelTitle();
		for(Map<String, Object> bean : beans){
			bean.put("taskType", taskType);
			Integer state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, PRO_TASK_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
    *
    * @Title: updateProTaskToExecutionBegin
    * @Description: 任务开始执行
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void updateProTaskToExecutionBegin(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询任务的信息
		Map<String, Object> bean = proTaskDao.queryProTaskMationByTaskId(id);
		if("11".equals(bean.get("state").toString())){//审核通过状态下可以开始执行
			proTaskDao.updateProTaskToExecutionBegin(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
    *
    * @Title: updateProTaskToExecutionOver
    * @Description: 任务执行完成
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void updateProTaskToExecutionOver(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询任务的信息
		Map<String, Object> bean = proTaskDao.queryProTaskMationByTaskId(id);
		if("2".equals(bean.get("state").toString())
				|| "3".equals(bean.get("state").toString())){
			// 执行中、执行完成状态下可以执行完成
			proTaskDao.updateProTaskToExecutionOver(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
    *
    * @Title: updateProTaskToExecutionClose
    * @Description: 任务关闭
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void updateProTaskToExecutionClose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询任务的信息
		Map<String, Object> bean = proTaskDao.queryProTaskMationByTaskId(id);
		if("3".equals(bean.get("state").toString())){
			// 执行完成状态下可以关闭
			proTaskDao.updateProTaskToExecutionClose(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
    *
    * @Title: queryProTaskInExecution
    * @Description: 登录人的执行中的任务
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProTaskInExecution(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		map.put("userId", userId);
		List<Map<String, Object>> beans = proTaskDao.queryProTaskInExecution(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

}
