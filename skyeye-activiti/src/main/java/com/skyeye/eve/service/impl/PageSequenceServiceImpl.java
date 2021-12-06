/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.DsFormPageDataDao;
import com.skyeye.eve.dao.DsFormPageSequenceDao;
import com.skyeye.eve.service.DsFormPageService;
import com.skyeye.eve.service.PageSequenceService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PageSequenceServiceImpl
 * @Description: 动态表单工作流服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class PageSequenceServiceImpl implements PageSequenceService{
	
	@Autowired
	private DsFormPageSequenceDao dsFormPageSequenceDao;

	@Autowired
	private DsFormPageDataDao dsFormPageDataDao;
	
	@Autowired
	private ActivitiModelService activitiModelService;
	
	@Autowired
	private DsFormPageService dsFormPageService;
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: queryDsFormISDraftListByUser
	     * @Description: 获取所有草稿状态的动态表单提交项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormISDraftListByUser(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = dsFormPageSequenceDao.queryDsFormISDraftListByUser(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: deleteDsFormISDraftByUser
	     * @Description: 删除草稿状态的动态表单提交项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteDsFormISDraftByUser(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Map<String, Object> bean = dsFormPageSequenceDao.queryDsFormStateById(map);
		if(bean != null && !bean.isEmpty()){
			// 删除表单提交序列表数据
			dsFormPageSequenceDao.deleteDsFormISDraftByUser(map);
			// 删除表单数据提交数据
			String sequenceId = map.get("id").toString();
			dsFormPageDataDao.deleteDsFormPageDataBySequenceId(sequenceId);
		}else{
			outputObject.setreturnMessage("该数据状态已改变或不属于当前登录账号.");
		}
	}

	/**
	 * 
	     * @Title: queryDsFormISDraftToEditById
	     * @Description: 编辑动态表单时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormISDraftToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormPageSequenceDao.queryDsFormISDraftToEditById(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: editDsFormISDraftById
	     * @Description: 编辑动态表单(无工作流)
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDsFormISDraftById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String str = map.get("jsonStr").toString();//前端传来的数据json串
		List<Map<String, Object>> json = JSONUtil.toList(str, null);
		json.forEach(bean -> {
			dsFormPageSequenceDao.editDsFormISDraftById(bean);
		});
	}

	/**
	 * 
	     * @Title: editDsFormISDraftToSubApprovalById
	     * @Description: 提交审批
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDsFormISDraftToSubApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		// 查询为草稿状态的提交项
		Map<String, Object> bean = dsFormPageSequenceDao.queryDsFormStateById(map);
		if(bean != null && !bean.isEmpty()){
			if(!bean.containsKey("actKey") || ToolUtil.isBlank(bean.get("actKey").toString())){
				outputObject.setreturnMessage("该表单还未绑定工作流，请联系管理员.");
				return;
			}
			// 获取表单项
			List<Map<String, Object>> items = dsFormPageService.getDsFormPageContentByFormId(map.get("pageId").toString());
			// 获取数据
			Map<String, Object> subFormData = new HashMap<>();
			List<Map<String, Object>> rows = dsFormPageSequenceDao.queryDsFormContentBySequenceId(map);
			for(Map<String, Object> row : rows){
				for(Map<String, Object> item : items){
					if(row.get("rowId").toString().equals(item.get("id").toString())){
						row.put("formItem", item);
						break;
					}
				}
				subFormData.put(row.get("keyId").toString(), row);
			}
			map.put("jsonStr", JSONUtil.toJsonStr(subFormData));
			map.put("keyName", bean.get("actKey"));
			//请求工作流接口获取数据
			activitiModelService.editActivitiModelToStartProcessByMap(map, user, map.get("id").toString(), ActivitiConstants.APPROVAL_ID);
			if("0".equals(map.get("code").toString())){
				//请求成功
				map.put("processInId", map.get("message"));
				dsFormPageSequenceDao.editDsFormISDraftToSubApprovalById(map);
			}else{
				outputObject.setreturnMessage(map.get("message").toString());
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变或不属于当前登录账号.");
		}
	}

	/**
	 * 
	     * @Title: queryDsFormISDraftDetailsById
	     * @Description: 表单详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormISDraftDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String sequenceId = map.get("sequenceId").toString();
		List<Map<String, Object>> rows = dsFormPageDataDao.queryDsFormPageDataListBySequenceId(Arrays.asList(sequenceId));
		outputObject.setBeans(rows);
		outputObject.settotal(rows.size());
	}
	
}
