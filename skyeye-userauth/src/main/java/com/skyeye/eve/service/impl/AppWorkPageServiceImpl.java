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
import com.skyeye.eve.dao.AppWorkPageDao;
import com.skyeye.eve.service.AppWorkPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AppWorkPageServiceImpl
 * @Description: 手机端菜单以及目录功能服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 23:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class AppWorkPageServiceImpl implements AppWorkPageService {

	@Autowired
	private AppWorkPageDao appWorkPageDao;

	public static enum State{
		START_NEW(1, "新建"),
		START_UP(2, "上线"),
		START_DOWN(3, "下线"),
		START_DELETE(4, "删除");
		private int state;
		private String name;
		State(int state, String name){
			this.state = state;
			this.name = name;
		}
		public int getState() {
			return state;
		}

		public String getName() {
			return name;
		}
	}
	
	/**
	 * 
	     * @Title: queryAppWorkPageList
	     * @Description: 获取手机端菜单目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAppWorkPageList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = appWorkPageDao.queryAppWorkPageList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: insertAppWorkPageMation
	     * @Description: 新增手机端菜单目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertAppWorkPageMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("id", ToolUtil.getSurFaceId());
		map.put("title", "新建目录");
		map.put("state", State.START_NEW.getState());
		map.put("type", 1);
		map.put("parentId", 0);
		Map<String, Object> orderBy = appWorkPageDao.queryAppWorkPageAfterOrderBum(map);
		if(orderBy == null){
			map.put("orderBy", 1);
		}else{
			if(orderBy.containsKey("orderBy")){
				map.put("orderBy", Integer.parseInt(orderBy.get("orderBy").toString()) + 1);
			}else{
				map.put("orderBy", 1);
			}
		}
		appWorkPageDao.insertAppWorkPageMation(map);
		outputObject.setBean(map);
	}
	
	/**
	 * 
	     * @Title: queryAppWorkPageListById
	     * @Description: 根据目录id获取菜单列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAppWorkPageListById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = appWorkPageDao.queryAppWorkPageListById(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertAppWorkPageMationById
	     * @Description: 新增手机端菜单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("id", ToolUtil.getSurFaceId());
		map.put("state", State.START_NEW.getState());
		map.put("type", 2);
		Map<String, Object> orderBy = appWorkPageDao.queryAppWorkPageTAfterOrderBum(map);
		if(orderBy == null){
			map.put("orderBy", 1);
		}else{
			if(orderBy.containsKey("orderBy")){
				map.put("orderBy", Integer.parseInt(orderBy.get("orderBy").toString()) + 1);
			}else{
				map.put("orderBy", 1);
			}
		}
		appWorkPageDao.insertAppWorkPageMation(map);
		outputObject.setBean(map);
	}
	
	/**
	 * 
	     * @Title: queryAppWorkPageMationById
	     * @Description: 获取菜单信息进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageMationById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageMationById
	     * @Description: 保存编辑后的菜单信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_NEW.getState() == state || State.START_DOWN.getState() == state){
			// 新建或者下线的状态可以编辑
			Map<String, Object> m = appWorkPageDao.queryAppWorkPageMationById(map);
			setUpdateUserMation(inputObject, map);
			if(!m.get("parentId").toString().equals(map.get("parentId").toString())){
				// 如果用户更改了菜单目录，则根据选择的目录将排序重新赋值
				Map<String, Object> orderBy = appWorkPageDao.queryAppWorkPageTAfterOrderBum(map);
				if(orderBy == null){
					map.put("orderBy", 1);
				}else{
					if(orderBy.containsKey("orderBy")){
						map.put("orderBy", Integer.parseInt(orderBy.get("orderBy").toString()) + 1);
					}else{
						map.put("orderBy", 1);
					}
				}
			}
			appWorkPageDao.editAppWorkPageMationById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	private void setUpdateUserMation(InputObject inputObject, Map<String, Object> map) throws Exception {
		map.put("last_update_id", inputObject.getLogParams().get("id"));
		map.put("last_update_time", DateUtil.getTimeAndToString());
	}
	
	/**
	 * 
	     * @Title: deleteAppWorkPageMationById
	     * @Description: 删除菜单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_NEW.getState() == state || State.START_DOWN.getState() == state){
			setUpdateUserMation(inputObject, map);
			// 新建或者下线的状态可以删除
			map.put("state", State.START_DELETE.getState());
			appWorkPageDao.editAppWorkPageStateById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageSortTopById
	     * @Description: 菜单上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 根据同一级排序获取这条数据的上一条数据
		Map<String, Object> topBean = appWorkPageDao.queryAppWorkPageISTopByThisId(map);
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前的菜单，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			setUpdateUserMation(inputObject, map);
			appWorkPageDao.editAppWorkPageSortById(map);
			setUpdateUserMation(inputObject, topBean);
			appWorkPageDao.editAppWorkPageSortById(topBean);
		}
	}

	/**
	 * 
	     * @Title: editAppWorkPageSortLowerById
	     * @Description: 菜单下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 根据同一级排序获取这条数据的下一条数据
		Map<String, Object> topBean = appWorkPageDao.queryAppWorkPageISLowerByThisId(map);
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后的菜单，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			setUpdateUserMation(inputObject, map);
			appWorkPageDao.editAppWorkPageSortById(map);
			setUpdateUserMation(inputObject, topBean);
			appWorkPageDao.editAppWorkPageSortById(topBean);
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageUpTypeById
	     * @Description: 菜单上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkPageUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_NEW.getState() == state || State.START_DOWN.getState() == state){
			setUpdateUserMation(inputObject, map);
			// 新建或者下线的状态可以上线
			map.put("state", State.START_UP.getState());
			appWorkPageDao.editAppWorkPageStateById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageDownTypeById
	     * @Description: 菜单下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkPageDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_UP.getState() == state){
			setUpdateUserMation(inputObject, map);
			// 上线状态可以下线
			map.put("state", State.START_DOWN.getState());
			appWorkPageDao.editAppWorkPageStateById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageTitleById
	     * @Description: 编辑菜单目录名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkPageTitleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_NEW.getState() == state || State.START_DOWN.getState() == state || State.START_UP.getState() == state){
			setUpdateUserMation(inputObject, map);
			// 新建/下线/上线的状态可以编辑
			appWorkPageDao.editAppWorkPageTitleById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: deleteAppWorkPageById
	     * @Description: 删除菜单目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteAppWorkPageById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_NEW.getState() == state || State.START_DOWN.getState() == state){
			// 新建或者下线的状态可以删除
			setUpdateUserMation(inputObject, map);
			map.put("state", State.START_DELETE.getState());
			appWorkPageDao.editAppWorkPageStateById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkUpTypeById
	     * @Description: 目录上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_NEW.getState() == state || State.START_DOWN.getState() == state){
			// 新建或者下线的状态可以上线
			setUpdateUserMation(inputObject, map);
			map.put("state", State.START_UP.getState());
			appWorkPageDao.editAppWorkPageStateById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkDownTypeById
	     * @Description: 目录下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = appWorkPageDao.queryAppWorkPageStateById(map);
		int state = Integer.parseInt(bean.get("state").toString());
		if(State.START_UP.getState() == state){
			// 上线状态可以下线
			setUpdateUserMation(inputObject, map);
			map.put("state", State.START_DOWN.getState());
			appWorkPageDao.editAppWorkPageStateById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editAppWorkSortTopById
	     * @Description: 目录上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = appWorkPageDao.queryAppWorkISTopByThisId(map);//根据同一级排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前的目录，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			setUpdateUserMation(inputObject, map);
			appWorkPageDao.editAppWorkPageSortById(map);
			setUpdateUserMation(inputObject, topBean);
			appWorkPageDao.editAppWorkPageSortById(topBean);
		}
	}

	/**
	 * 
	     * @Title: editAppWorkSortLowerById
	     * @Description: 目录下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editAppWorkSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = appWorkPageDao.queryAppWorkISLowerByThisId(map);//根据同一级排序获取这条数据的下一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后的目录，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			setUpdateUserMation(inputObject, map);
			appWorkPageDao.editAppWorkPageSortById(map);
			setUpdateUserMation(inputObject, topBean);
			appWorkPageDao.editAppWorkPageSortById(topBean);
		}
	}
	
}
