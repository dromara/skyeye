/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProFileDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.service.ProFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProFileServiceImpl
 * @Description: 项目文档管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ProFileServiceImpl implements ProFileService {

    @Autowired
    private ProFileDao proFileDao;
    
    @Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 项目文档审核在工作流中配置的key
	 */
    private static final String PRO_FILE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.PRO_FILE_PAGE.getKey();

    /**
    *
    * @Title: queryProFileList
    * @Description: 我的文档列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	public void queryProFileList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		map.put("userId", userId);
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proFileDao.queryProFileList(map);
		for(Map<String, Object> bean : beans){
			Integer state = Integer.parseInt(bean.get("state").toString());
			ActivitiRunFactory.run(inputObject, outputObject, PRO_FILE_PAGE_KEY).setDataStateEditRowWhenInExamine(bean, state, userId);
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

    /**
    *
    * @Title: insertProFileMation
    * @Description: 新增文档
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@Override
	@Transactional(value="transactionManager")
	public void insertProFileMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proFileDao.queryProFileByTitle(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该文档名称已存在，不能重复添加！");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			String time = DateUtil.getTimeAndToString();//新增时更新时间即为创建时间
			String id = ToolUtil.getSurFaceId();//文档id
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("state", "0");//默认状态为草稿
			map.put("createTime", time);
			map.put("updateTime", time);
			proFileDao.insertProFileMation(map);
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, PRO_FILE_PAGE_KEY).submitToActivi(id);
			}
		}
	}
	
	/**
	*
	* @Title: queryProFileMationToDetails
	* @Description: 文档详情
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryProFileMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 获取工作量主表数据
		Map<String, Object> jsonBean = proFileDao.queryProFileMationById(id);
		// 获取附件信息
        jsonBean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(jsonBean.get("enclosureInfo").toString()));
		outputObject.setBean(jsonBean);
		outputObject.settotal(1);
	}

	/**
	*
	* @Title: queryProFileMationToEdit
	* @Description: 获取文档信息用以编辑
	* @param inputObject
	* @param outputObject
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	*/
	@Override
	public void queryProFileMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 获取工作量主表数据
		Map<String, Object> bean = proFileDao.queryProFileMationToEdit(map);
		// 获取附件信息
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editProFileMation
	 * @Description: 编辑文档信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProFileMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proFileDao.queryProFileByTitle(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该文档名称已存在，不可进行二次保存！");
		}else{
			String id = map.get("id").toString();//文档id
			proFileDao.editProFileMation(map);
			// 判断是否提交审批
			if("2".equals(map.get("subType").toString())){
				// 提交审批
				ActivitiRunFactory.run(inputObject, outputObject, PRO_FILE_PAGE_KEY).submitToActivi(id);
			}
		}
	}

	/**
	 *
	 * @Title: editProFileToApprovalById
	 * @Description: 提交审批
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProFileToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询项目文档信息
		Map<String, Object> bean = proFileDao.queryProFileMationById(id);
		Integer state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 3 == state){
			// 草稿、审核不通过、撤销状态下可以提交审批
			ActivitiRunFactory.run(inputObject, outputObject, PRO_FILE_PAGE_KEY).submitToActivi(id);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: editProFileProcessToRevoke
	 * @Description: 撤销文档审批申请
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProFileProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		ActivitiRunFactory.run(inputObject, outputObject, PRO_FILE_PAGE_KEY).revokeActivi();
	}

	/**
	 *
	 * @Title: editProFileMationInProcess
	 * @Description: 在工作流中编辑文档信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void editProFileMationInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = proFileDao.queryProFileByTitle(map);
		if(bean == null){
			String id = map.get("id").toString();
			proFileDao.editProFileMation(map);
			// 编辑流程表参数
			ActivitiRunFactory.run(inputObject, outputObject, PRO_FILE_PAGE_KEY).editApplyMationInActiviti(id);
		}else{
			outputObject.setreturnMessage("该文档名称已存在，不可进行二次保存");
		}
	}

	/**
	 *
	 * @Title: deleteProFileMationById
	 * @Description: 删除文档
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void deleteProFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询项目文档信息
		Map<String, Object> bean = proFileDao.queryProFileMationById(id);
		Integer state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 3 == state){
			// 草稿、审核不通过、撤销状态下可以删除
			proFileDao.deleteProFileMationById(map);// 删除该文档相关信息
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: updateProFileToCancellation
	 * @Description: 作废文档
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void updateProFileToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		// 查询项目文档信息
		Map<String, Object> bean = proFileDao.queryProFileMationById(id);
		Integer state = Integer.parseInt(bean.get("state").toString());
		if(0 == state || 12 == state || 3 == state){
			proFileDao.updateProFileToCancellation(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: queryAllProFileList
	 * @Description: 获取全部的项目文档的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void queryAllProFileList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proFileDao.queryAllProFileList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: queryAllStateupProFileByProId
	 * @Description: 获取某个项目下的所有已经审核通过的文档
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void queryAllStateupProFileByProId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = proFileDao.queryAllStateupProFileByProId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
