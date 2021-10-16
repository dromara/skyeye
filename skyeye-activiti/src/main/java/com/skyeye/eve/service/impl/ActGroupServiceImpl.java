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
import com.skyeye.eve.dao.ActGroupDao;
import com.skyeye.eve.service.ActGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActGroupServiceImpl implements ActGroupService {

	@Autowired
	private ActGroupDao actGroupDao;

	/**
	 * 
	     * @Title: insertActGroupMation
	     * @Description: 新增用户组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertActGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", inputObject.getLogParams().get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		actGroupDao.insertActGroupMation(map);
		outputObject.setBean(map);
	}

	/**
	 * 
	     * @Title: selectAllActGroupMation
	     * @Description: 遍历所有的用户组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllActGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = actGroupDao.selectAllActGroupMation(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertActGroupUserByGroupId
	     * @Description: 给用户组新增用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		// 查询该用户组中是否已经存在这些用户
		Map<String, Object> bs = actGroupDao.queryUserIsInActGroup(map);
		// 把字符串以","分截成字符数组
		String[] userId = map.get("userId").toString().split(",");
		String user = bs.get("userId").toString();
		if(userId.length > 0){//如果数组长度大于0
			for(String str : userId){//遍历数组
				if(user.contains(str)){//如果该用户组已经存在这个用户，则跳过
					continue;
				}
				if(!ToolUtil.isBlank(str)){
					Map<String, Object> item = new HashMap<>();
					item.put("id", ToolUtil.getSurFaceId());
					item.put("groupId", map.get("groupId"));
					item.put("userId", str);
					item.put("createId", inputObject.getLogParams().get("id"));
					item.put("createTime", DateUtil.getTimeAndToString());
					beans.add(item);//把一个个item对象放入集合beans
				}
			}
			if(!beans.isEmpty()){
				actGroupDao.insertActGroupUserByGroupId(beans); //在数据库中插入集合beans
			}
		}else{
			outputObject.setreturnMessage("请选择要新增进组的用户！");
		}
	}

	/**
	 * 
	     * @Title: editActGroupNameByGroupId
	     * @Description: 编辑用户组名
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editActGroupNameByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		actGroupDao.editActGroupNameByGroupId(map);
	}

	/**
	 * 
	     * @Title: deleteActGroupByGroupId
	     * @Description: 删除用户组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteActGroupByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		actGroupDao.deleteActGroupByGroupId(map);
		actGroupDao.deleteActGroupUserByGroupId(map);
	}

	/**
	 * 
	     * @Title: deleteActGroupUserByGroupIdAndUserId
	     * @Description: 移除用户组中的某个用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteActGroupUserByGroupIdAndUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		actGroupDao.deleteActGroupUserByGroupIdAndUserId(map);
	}

	/**
	 * 
	     * @Title: selectUserInfoOnActGroup
	     * @Description: 展示用户组的用户信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectUserInfoOnActGroup(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = actGroupDao.selectUserInfoOnActGroup(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: deleteAllActGroupUserByGroupId
	     * @Description: 一键移除指定用户组下的所有用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteAllActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		actGroupDao.deleteActGroupUserByGroupId(map);
	}
}
