/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.DsFormPageTypeDao;
import com.skyeye.eve.service.DsFormPageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DsFormPageTypeServiceImpl
 * @Description: 动态表单页面分类业务实现层
 * @author: skyeye云系列--郑杰
 * @date: 2021/11/6 23:33
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class DsFormPageTypeServiceImpl implements DsFormPageTypeService {

	@Autowired
	private DsFormPageTypeDao dsFormPageTypeDao;

	@Override
	public void queryDsFormPageTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(inputParams.get("page").toString()), Integer.parseInt(inputParams.get("limit").toString()));
		List<Map<String, Object>> emailSendModelList = dsFormPageTypeDao.queryDsFormPageTypeList(inputParams);
		outputObject.setBeans(emailSendModelList);
		outputObject.settotal(pages.getTotal());
	}

	@Override
	public void insertDsFormPageType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		if (checkParentIdExists(inputParams, outputObject)) {
			return;
		}
		inputParams.put("id", ToolUtil.getSurFaceId());
		inputParams.put("createTime", DateUtil.getTimeAndToString());
		inputParams.put("userId", inputObject.getLogParams().get("id"));
		dsFormPageTypeDao.insertDsFormPageType(inputParams);
	}

	@Override
	public void queryDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		Map<String, Object> dsFormPageTypeBean = dsFormPageTypeDao.queryDsFormPageTypeById(inputParams.get("id").toString());
		outputObject.setBean(dsFormPageTypeBean);
		outputObject.settotal(1);
	}

	@Override
	public void queryDsFormPageTypeByParentId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		List<Map<String, Object>> dsFormPageTypeList = dsFormPageTypeDao.queryDsFormPageTypeByParentId(inputParams.get("parentId").toString());
		outputObject.setBeans(dsFormPageTypeList);
		outputObject.settotal(dsFormPageTypeList.size());
	}

	@Override
	public void updateDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		if (checkParentIdExists(inputParams, outputObject)) {
			return;
		}
		inputParams.put("lastUpdateTime", DateUtil.getTimeAndToString());
		inputParams.put("userId", inputObject.getLogParams().get("id"));
		dsFormPageTypeDao.updateDsFormPageTypeById(inputParams);
	}

	/**
	 * 校验parentId是否存在
	 *
	 * @param inputParams
	 * @return true: parentId+typeName存在, 反之false
	 */
	private boolean checkParentIdExists(Map<String, Object> inputParams, OutputObject outputObject) {
		Object parentId = inputParams.get("parentId");
		if (parentId != null) {
			String tempId = dsFormPageTypeDao.queryDsFormPageTypeByParentIdAndTypeName(inputParams);
			if (tempId == null || tempId.equals(inputParams.get("id"))) {
				return false;
			}
			outputObject.setreturnMessage("父节点下已存在该分类名称.");
			return true;
		}
		// 父节点不存在默认赋值0
		inputParams.put("parentId", 0);
		return false;
	}

	@Override
	public void delDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		dsFormPageTypeDao.delDsFormPageTypeById(inputParams.get("id").toString());
	}
}
