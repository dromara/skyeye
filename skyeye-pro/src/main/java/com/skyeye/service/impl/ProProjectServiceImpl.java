/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProProjectDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.ProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProProjectServiceImpl
 * @Description: 项目管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ProProjectServiceImpl implements ProProjectService {

    @Autowired
    private ProProjectDao proProjectDao;
    
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	@Autowired
	private ActivitiUserService activitiUserService;

	/**
	 * 项目立项在工作流中的key
	 */
	private static final String PRO_PROJECT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_PROJECT_PAGE.getKey();

	/**
	 *
	 * @Title: queryAllProProjectList
	 * @Description: 获取全部项目管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllProProjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("pageUrl", PRO_PROJECT_PAGE_KEY);
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proProjectDao.queryAllProProjectList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryMyProProjectList
	 * @Description: 获取我的项目管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMyProProjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		map.put("pageUrl", PRO_PROJECT_PAGE_KEY);
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proProjectDao.queryMyProProjectList(map);
		for (Map<String, Object> bean : beans){
			Integer state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, PRO_PROJECT_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	
	/**
	 *
	 * @Title: insertProProjectMation
	 * @Description: 新增项目信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void insertProProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = proProjectDao.queryProProjectMationByNameAndNum(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setreturnMessage("项目名称或编号已存在！");
		}else{
			setOtherParams(map);
			Map<String, Object> user = inputObject.getLogParams();
			String proId = ToolUtil.getSurFaceId();
			map.put("id", proId);
			map.put("state", 0);
			map.put("speedOfProgress", 0);
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			proProjectDao.insertProProjectMation(map);
			// 操作工作流数据
			activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
				PRO_PROJECT_PAGE_KEY, proId, map.get("approvalId").toString());
		}
	}

	/**
	 * 设置一些为空的参数
	 *
	 * @param map map
	 */
	private void setOtherParams(Map<String, Object> map){
		if(ToolUtil.isBlank(map.get("startTime").toString())){
			map.put("startTime", null);
		}
		if(ToolUtil.isBlank(map.get("endTime").toString())){
			map.put("endTime", null);
		}
		if(ToolUtil.isBlank(map.get("estimatedWorkload").toString())){
			map.put("estimatedWorkload", 0);
		}
		if(ToolUtil.isBlank(map.get("estimatedCost").toString())){
			map.put("estimatedCost", 0);
		}
	}
	
	/**
	 *
	 * @Title: queryAllProProjectToChoose
	 * @Description: 获取全部指定项目用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllProProjectToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> maps = proProjectDao.queryAllProProjectToChoose(map);
		outputObject.setBeans(maps);
		outputObject.settotal(maps.size());
	}
	
	/**
	 *
	 * @Title: queryProProjectMationToDetail
	 * @Description: 获取项目详细信息展示详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryProProjectMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String proId = map.get("id").toString();
		Map<String, Object> bean = proProjectDao.queryProProjectMationToDetail(proId);
		
		// 获取业务需求和目标的附件
		bean.put("businessEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("businessEnclosureInfo").toString()));
		// 获取项目组织和分工的附件
		bean.put("projectEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("projectEnclosureInfo").toString()));
		// 获取实施计划的附件
		bean.put("planEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("planEnclosureInfo").toString()));
		// 获取实施计划的附件
		bean.put("resultsEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("resultsEnclosureInfo").toString()));
		
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 *
	 * @Title: queryProProjectMationToEdit
	 * @Description: 获取项目详细信息用于编辑回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryProProjectMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProProjectMationToEdit(map);
		if(!ToolUtil.isBlank(bean.get("businessEnclosureInfo").toString())){
			bean.put("businessEnclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("businessEnclosureInfo").toString()));
		}
		Map<String, Object> user = inputObject.getLogParams();
		Integer state = Integer.parseInt(bean.get("state").toString());
		ActivitiRunFactory.run(inputObject, outputObject, PRO_PROJECT_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, user.get("id").toString());
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 *
	 * @Title: editProProjectMationById
	 * @Description: 编辑项目信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editProProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String proId = map.get("id").toString();
		setOtherParams(map);
		List<Map<String, Object>> beans = proProjectDao.queryProProjectMationByNameAndId(map);
		if(beans != null && beans.size() > 0){
			outputObject.setreturnMessage("项目名称或编号已存在！");
		}else{
			proProjectDao.editProProjectMationById(map);
			// 操作工作流数据
			activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
				PRO_PROJECT_PAGE_KEY, proId, map.get("approvalId").toString());
		}
	}

	/**
	 *
	 * @Title: editProProjectMationToSubApproval
	 * @Description: 项目提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editProProjectMationToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String proId = map.get("id").toString();
		Map<String, Object> bean = proProjectDao.queryProProjectStateById(map);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "5".equals(bean.get("state").toString())){
			// 草稿、审核不通过或者撤销状态下可以提交审批
			activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
				PRO_PROJECT_PAGE_KEY, proId, map.get("approvalId").toString());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: deleteProProjectMationById
	 * @Description: 删除项目信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteProProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProProjectStateById(map);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "5".equals(bean.get("state").toString())){
			// 草稿、审核不通过或者撤销状态下可以删除
			proProjectDao.deleteProProjectMationById(map);
		}else{
			outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: deleteProProjectMationById
	 * @Description: 撤销项目审批申请
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@ActivitiAndBaseTransaction(value = {"activitiTransactionManager", "transactionManager"})
	public void editProjectProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, PRO_PROJECT_PAGE_KEY).revokeActivi();
	}

	/**
	 *
	 * @Title: editProjectProcessToNullify
	 * @Description: 作废项目
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editProjectProcessToNullify(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProProjectStateById(map);
		if("0".equals(bean.get("state").toString())
				|| "12".equals(bean.get("state").toString())
				|| "5".equals(bean.get("state").toString())
				|| "2".equals(bean.get("state").toString())){
			// 草稿、审核不通过、撤销或者执行中状态下可以删除
			proProjectDao.editProjectProcessToNullify(map);
		}else{
			outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: editProjectProcessToExecute
	 * @Description: 开始执行项目
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editProjectProcessToExecute(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProProjectStateById(map);
		// 审核通过状态下可以开始执行
		if("11".equals(bean.get("state").toString())){
			// 判断是否符合开始执行的条件---即：项目经理、项目组成员、分工明细不为空
			if(!ToolUtil.isBlank(bean.get("projectManager").toString())
					&& !ToolUtil.isBlank(bean.get("projectMembers").toString())
					&& !ToolUtil.isBlank(bean.get("projectContent").toString())){
				proProjectDao.editProjectProcessToExecute(map);
			}else{
				outputObject.setreturnMessage("您还未进行项目任命！");
			}
		}else{
			outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: editProjectProcessToProAppointShowById
	 * @Description: 项目任命数据回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryProjectProcessToProAppointShowById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProjectProcessToProAppointShowById(map);
		// 获取业务需求和目标的附件
		bean.put("businessEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("businessEnclosureInfo").toString()));
		// 获取项目组织和分工的附件
		bean.put("projectEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("projectEnclosureInfo").toString()));
		// 获取实施计划的附件
		bean.put("planEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("planEnclosureInfo").toString()));
		// 获取项目经理
		bean.put("projectManagerList", proProjectDao.queryProjectManagerInfo(bean));
		// 获取项目赞助人
		bean.put("projectSponsorList", proProjectDao.queryProjectSponsorInfo(bean));
		// 获取项目组成员
		bean.put("projectMembersList", proProjectDao.queryProjectMembersInfo(bean));
		
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editProjectProcessToProAppointById
	 * @Description: 项目任命
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editProjectProcessToProAppointById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProProjectStateById(map);
		// 审核通过状态下可以项目任命
		if("11".equals(bean.get("state").toString())){
			proProjectDao.editProjectProcessToProAppointById(map);
		}else{
			outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: queryProjectProcessToPerFectShowById
	 * @Description: 信息完善数据回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryProjectProcessToPerFectShowById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProjectProcessToPerFectShowById(map);
		
		// 获取业务需求和目标的附件
		bean.put("businessEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("businessEnclosureInfo").toString()));
		// 获取项目组织和分工的附件
		bean.put("projectEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("projectEnclosureInfo").toString()));
		// 获取实施计划的附件
		bean.put("planEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("planEnclosureInfo").toString()));
		// 获取实施计划的附件
		bean.put("resultsEnclosureInfoList", sysEnclosureDao.queryEnclosureInfo(bean.get("resultsEnclosureInfo").toString()));
		
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editProjectProcessToPerFectById
	 * @Description: 信息完善
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editProjectProcessToPerFectById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proProjectDao.queryProProjectStateById(map);
		// 执行中状态下可以完善成果与总结信息
		if("2".equals(bean.get("state").toString())){
			if("2".equals(map.get("subType").toString())){
				// 完结
				map.put("state", "3");
			}
			proProjectDao.editProjectProcessToPerFectById(map);
		}else{
			outputObject.setreturnMessage("数据状态已改变，请刷新页面！");
		}
	}

}
