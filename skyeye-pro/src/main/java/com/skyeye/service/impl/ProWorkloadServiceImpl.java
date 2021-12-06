/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProWorkloadDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.ProWorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProWorkloadServiceImpl
 * @Description: 项目工作量服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 12:59
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ProWorkloadServiceImpl implements ProWorkloadService {
	
	@Autowired
    private ProWorkloadDao proWorkloadDao;

	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	private static final String PRO_WORKLOAD_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_WORKLOAD_PAGE.getKey();

    /**
    *
    * @Title: queryProWorkloadList
    * @Description: 获取项目工作量列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProWorkloadList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proWorkloadDao.queryProWorkloadList(map);
		String taskType = ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).getActModelTitle();
		for(Map<String, Object> bean : beans){
			bean.put("taskType", taskType);
			Integer state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: insertProWorkloadMation
    * @Description: 新增项目工作量
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void insertProWorkloadMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = ToolUtil.getSurFaceId();//工作量主表id
		// 处理数据
		List<Map<String, Object>> entitys = getWorkloadTaskList(outputObject, map.get("workloadTaskStr").toString(), workloadId);
		if (entitys == null) return;
		map.put("id", workloadId);
		map.put("state", 0);//状态  默认草稿
		Map<String, Object> user = inputObject.getLogParams();//用户信息
		map.put("createId", user.get("id").toString());
		map.put("createTime", DateUtil.getTimeAndToString());
		proWorkloadDao.insertProWorkloadMation(map);
		proWorkloadDao.insertProWorkloadRelatedTasksMation(entitys);
		// 判断是否提交审批
		if("2".equals(map.get("subType").toString())){
			// 提交审批
			ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).submitToActivi(workloadId, ActivitiConstants.APPROVAL_ID);
		}
	}

	private List<Map<String, Object>> getWorkloadTaskList(OutputObject outputObject, String workloadTaskStr, String workloadId) {
		List<Map<String, Object>> jArray = JSONUtil.toList(workloadTaskStr, null);
		Map<String, Object> bean;
		// 工作量相关任务集合信息
		List<Map<String, Object>> entitys = new ArrayList<>();
		for (int i = 0; i < jArray.size(); i++) {
			bean = jArray.get(i);
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("workloadId", workloadId);//工作量主表id
			entitys.add(bean);
		}
		if (entitys.size() == 0) {
			outputObject.setreturnMessage("请填写项目工作量相关任务");
			return null;
		}
		return entitys;
	}

	/**
    *
    * @Title: queryProWorkloadList
    * @Description: 获取所有的项目工作量列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryAllProWorkloadList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proWorkloadDao.queryAllProWorkloadList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
    *
    * @Title: editProWorkloadToApprovalById
    * @Description: 提交审批
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void editProWorkloadToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = map.get("id").toString();
		// 获取工作量信息
		Map<String, Object> bean = proWorkloadDao.queryProWorkloadMationById(workloadId);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "3".equals(bean.get("state").toString())){
			// 草稿、审核不通过、撤销状态下可以提交审批
			ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).submitToActivi(workloadId, ActivitiConstants.APPROVAL_ID);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
    *
    * @Title: editProWorkloadProcessToRevoke
    * @Description: 撤销工作量审批申请
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void editProWorkloadProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).revokeActivi();
	}

	/**
    *
    * @Title: queryProWorkloadMationToDetails
    * @Description: 工作量详情
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProWorkloadMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = map.get("id").toString();
		// 获取工作量主表数据
		Map<String, Object> jsonBean = proWorkloadDao.queryProWorkloadMationById(workloadId);
		// 获取任务信息
		jsonBean.put("tasks", proWorkloadDao.queryProWorkloadRelatedTasksById(workloadId));
		// 获取附件信息
        jsonBean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(jsonBean.get("enclosureInfo").toString()));
		outputObject.setBean(jsonBean);
		outputObject.settotal(1);
	}

	/**
    *
    * @Title: queryProWorkloadMationToEdit
    * @Description: 获取工作量信息用以编辑
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProWorkloadMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取工作量主表数据
		Map<String, Object> bean = proWorkloadDao.queryProWorkloadMationToEdit(map);
		// 获取任务信息
		List<Map<String, Object>> tasks = proWorkloadDao.queryRelatedTasksById(map);
		bean.put("tasks", tasks);
		// 获取附件信息
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
    *
    * @Title: editProWorkloadMation
    * @Description: 编辑工作量信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void editProWorkloadMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = map.get("id").toString();//工作量主表id
		// 处理数据
		List<Map<String, Object>> entitys = getWorkloadTaskList(outputObject, map.get("workloadTaskStr").toString(), workloadId);
		if (entitys == null) return;
		proWorkloadDao.editProWorkloadMation(map);
		proWorkloadDao.deleteProWorkloadRelatedTasksById(map);
		proWorkloadDao.insertProWorkloadRelatedTasksMation(entitys);
		// 判断是否提交审批
		if("2".equals(map.get("subType").toString())){
			// 提交审批
			ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).submitToActivi(workloadId, ActivitiConstants.APPROVAL_ID);
		}
	}

	/**
    *
    * @Title: editProWorkloadMationInProcess
    * @Description: 在工作流中编辑工作量信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void editProWorkloadMationInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = map.get("id").toString();//工作量主表id
		// 处理数据
		List<Map<String, Object>> entitys = getWorkloadTaskList(outputObject, map.get("workloadTaskStr").toString(), workloadId);
		if (entitys == null) return;
		proWorkloadDao.editProWorkloadMation(map);
		proWorkloadDao.deleteProWorkloadRelatedTasksById(map);
		proWorkloadDao.insertProWorkloadRelatedTasksMation(entitys);
		// 编辑流程表参数
		ActivitiRunFactory.run(inputObject, outputObject, PRO_WORKLOAD_PAGE_KEY).editApplyMationInActiviti(workloadId);
	}

	/**
    *
    * @Title: deleteProWorkloadMationById
    * @Description: 删除工作量
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void deleteProWorkloadMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = map.get("id").toString();
		// 获取工作量信息
		Map<String, Object> bean = proWorkloadDao.queryProWorkloadMationById(workloadId);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "3".equals(bean.get("state").toString())){
			// 草稿、审核不通过、撤销状态下可以删除
			proWorkloadDao.deleteAllProWorkloadById(map);// 根据工作量ID删除相关关联表中的数据
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
    *
    * @Title: updateProWorkloadToCancellation
    * @Description: 作废工作量
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void updateProWorkloadToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String workloadId = map.get("id").toString();
		// 获取工作量信息
		Map<String, Object> bean = proWorkloadDao.queryProWorkloadMationById(workloadId);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "3".equals(bean.get("state").toString())){
			// 草稿、审核不通过、撤销状态下可以作废
			proWorkloadDao.updateProWorkloadToCancellation(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

}
