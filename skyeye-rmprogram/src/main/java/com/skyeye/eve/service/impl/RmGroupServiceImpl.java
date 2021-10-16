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
import com.skyeye.eve.dao.RmGroupDao;
import com.skyeye.eve.service.RmGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RmGroupServiceImpl implements RmGroupService{
	
	@Autowired
	private RmGroupDao rmGroupDao;

	/**
	 * 
	     * @Title: queryRmGroupList
	     * @Description: 获取小程序分组列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmGroupList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = rmGroupDao.queryRmGroupList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertRmGroupMation
	     * @Description: 添加小程序分组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertRmGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmGroupDao.queryRmGroupByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			Map<String, Object> item = rmGroupDao.queryRmGroupISTop(map);//获取最靠前的小程序分类
			if(item == null){
				map.put("sort", 1);
			}else{
				map.put("sort", Integer.parseInt(item.get("sort").toString()) + 1);
			}
			rmGroupDao.insertRmGroupMation(map);
		}else{
			outputObject.setreturnMessage("该分组名称已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteRmGroupById
	     * @Description: 删除小程序分组信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteRmGroupById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmGroupDao.queryRmGroupMemberNumById(map);
		if(bean == null){
			rmGroupDao.deleteRmGroupById(map);
		}else{
			if(Integer.parseInt(bean.get("groupMemberNum").toString()) == 0){//该小程序分组下没有组件
				rmGroupDao.deleteRmGroupById(map);
			}else{
				outputObject.setreturnMessage("该分组下存在小程序组件，无法删除。");
			}
		}
	}

	/**
	 * 
	     * @Title: queryRmGroupMationToEditById
	     * @Description: 编辑小程序分组信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmGroupDao.queryRmGroupMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmGroupMationById
	     * @Description: 编辑小程序分组信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editRmGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmGroupDao.queryRmGroupMationByIdAndName(map);
		if(bean == null){
			rmGroupDao.editRmGroupMationById(map);
		}else{
			outputObject.setreturnMessage("该分组名称已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: editRmGroupSortTopById
	     * @Description: 小程序分组展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editRmGroupSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = rmGroupDao.queryRmGroupISTopByThisId(map);//根据排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前分组，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			rmGroupDao.editRmGroupSortTopById(map);
			rmGroupDao.editRmGroupSortTopById(topBean);
		}
	}

	/**
	 * 
	     * @Title: editRmGroupSortLowerById
	     * @Description: 小程序分组展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editRmGroupSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = rmGroupDao.queryRmGroupISLowerByThisId(map);//根据排序获取这条数据的下一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后分组，无法移动。");
		}else{
			map.put("sort", topBean.get("sort"));
			topBean.put("sort", topBean.get("thisSort"));
			rmGroupDao.editRmGroupSortTopById(map);
			rmGroupDao.editRmGroupSortTopById(topBean);
		}
	}

	/**
	 * 
	     * @Title: queryRmGroupAllList
	     * @Description: 获取所有小程序分组根据小程序分类ID
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmGroupAllList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = rmGroupDao.queryRmGroupAllList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	
	
}
