/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEveModelTypeDao;
import com.skyeye.eve.service.SysEveModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveModelTypeServiceImpl
 * @Description: 系统模板分类业务实现层
 * @author: skyeye云系列
 * @date: 2021/11/13 10:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysEveModelTypeServiceImpl implements SysEveModelTypeService {

	@Autowired
	private SysEveModelTypeDao sysEveModelTypeDao;

	@Override
	public void querySysEveModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveModelTypeDao.querySysEveModelTypeList(inputParams);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	@Override
	public void insertSysEveModelType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		if (checkParentIdExists(inputParams, outputObject)) {
			return;
		}
		inputParams.put("id", ToolUtil.getSurFaceId());
		inputParams.put("createTime", DateUtil.getTimeAndToString());
		inputParams.put("userId", inputObject.getLogParams().get("id"));
		sysEveModelTypeDao.insertSysEveModelType(inputParams);
	}

	@Override
	public void querySysEveModelTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		Map<String, Object> sysEveModelTypeBean = sysEveModelTypeDao.querySysEveModelTypeById(inputParams.get("id").toString());
		outputObject.setBean(sysEveModelTypeBean);
		outputObject.settotal(1);
	}

	@Override
	public void querySysEveModelTypeByParentId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		List<Map<String, Object>> sysEveModelTypeList = sysEveModelTypeDao.querySysEveModelTypeByParentId(inputParams.get("parentId").toString());
		outputObject.setBeans(sysEveModelTypeList);
		outputObject.settotal(sysEveModelTypeList.size());
	}

	@Override
	public void updateSysEveModelTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		if (checkParentIdExists(inputParams, outputObject)) {
			return;
		}
		inputParams.put("lastUpdateTime", DateUtil.getTimeAndToString());
		inputParams.put("userId", inputObject.getLogParams().get("id"));
		sysEveModelTypeDao.updateSysEveModelTypeById(inputParams);
	}

	/**
	 * 校验parentId是否存在
	 *
	 * @param inputParams
	 * @param outputObject
	 * @return true: parentId+typeName存在, 反之false
	 */
	private boolean checkParentIdExists(Map<String, Object> inputParams, OutputObject outputObject) {
		String tempId = sysEveModelTypeDao.querySysEveModelTypeByParentIdAndTypeName(inputParams);
		if (tempId == null || tempId.equals(inputParams.get("id"))) {
			return false;
		}
		outputObject.setreturnMessage("父节点下已存在该分类名称.");
		return true;
	}

	@Override
	@Transactional(value="transactionManager")
	public void delSysEveModelTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> inputParams = inputObject.getParams();
		String id = inputParams.get("id").toString();
		// 根据Id删除该节点id被作为parentId使用的其他页面分类数据
		sysEveModelTypeDao.delSysEveModelTypeByParentId(id);
		sysEveModelTypeDao.delSysEveModelTypeById(id);
	}
}
