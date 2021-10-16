/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.LightAppTypeDao;
import com.skyeye.eve.service.LightAppTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: LightAppTypeServiceImpl
 * @Description: 轻应用类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class LightAppTypeServiceImpl implements LightAppTypeService {

	@Autowired
	private LightAppTypeDao lightAppTypeDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: queryLightAppTypeList
	     * @Description: 获取轻应用类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryLightAppTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = lightAppTypeDao.queryLightAppTypeList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertLightAppTypeMation
	     * @Description: 新增轻应用类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertLightAppTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppTypeDao.queryLightAppTypeMationByTypeName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该类型名称已存在，不能重复添加！");
		}else{
			Map<String, Object> orderBy = lightAppTypeDao.queryLightAppTypeAfterOrderBum(map);
			if(orderBy == null){
				map.put("orderBy", 1);
			}else{
				if(orderBy.containsKey("orderBy")){
					map.put("orderBy", Integer.parseInt(orderBy.get("orderBy").toString()) + 1);
				}else{
					map.put("orderBy", 1);
				}
			}
			Map<String, Object> user = inputObject.getLogParams();
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", "1");
			lightAppTypeDao.insertLightAppTypeMation(map);
		}
	}

	/**
	 * 
	     * @Title: queryLightAppTypeMationToEditById
	     * @Description: 编辑轻应用类型时进行信息回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryLightAppTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppTypeDao.queryLightAppTypeMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editLightAppTypeMationById
	     * @Description: 编辑轻应用类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> beans = lightAppTypeDao.queryLightAppTypeMationStateById(map);
		if("1".equals(beans.get("state").toString()) || "3".equals(beans.get("state").toString())){//新建或者下线的状态可以编辑
			Map<String, Object> bean = lightAppTypeDao.queryLightAppTypeMationByTypeNameAndId(map);
			if(bean == null){
				lightAppTypeDao.editLightAppTypeMationById(map);
			}else{
				outputObject.setreturnMessage("该应用名称已存在，不可进行二次保存");
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeSortTopById
	     * @Description: 轻应用类型展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = lightAppTypeDao.queryLightAppTypeISTopByThisId(map);//根据同一级排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前轻应用类型，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			lightAppTypeDao.editLightAppTypeSortTopById(map);
			lightAppTypeDao.editLightAppTypeSortTopById(topBean);
			jedisClient.del(Constants.checkAppLightAppTypeUpList());//删除上线轻应用类型的redis
		}
	}

	/**
	 * 
	     * @Title: editLightAppTypeSortLowerById
	     * @Description: 轻应用类型展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = lightAppTypeDao.queryLightAppTypeISLowerByThisId(map);//根据同一级排序获取这条数据的下一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后轻应用类型，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			lightAppTypeDao.editLightAppTypeSortLowerById(map);
			lightAppTypeDao.editLightAppTypeSortLowerById(topBean);
			jedisClient.del(Constants.checkAppLightAppTypeUpList());//删除上线轻应用类型的redis
		}
	}
	
	/**
	 * 
	     * @Title: deleteLightAppTypeById
	     * @Description: 删除轻应用类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteLightAppTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppTypeDao.queryLightAppTypeMationStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线的状态可以删除
			lightAppTypeDao.deleteLightAppTypeById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeUpTypeById
	     * @Description: 轻应用类型上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppTypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppTypeDao.queryLightAppTypeMationStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线的状态可以上线
			lightAppTypeDao.editLightAppTypeUpTypeById(map);
			jedisClient.del(Constants.checkAppLightAppTypeUpList());//删除上线轻应用类型的redis
			jedisClient.del(Constants.checkAppLightAppUpListById(""));//删除上线轻应用的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeDownTypeById
	     * @Description: 轻应用类型下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLightAppTypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = lightAppTypeDao.queryLightAppTypeMationStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			lightAppTypeDao.editLightAppTypeDownTypeById(map);
			jedisClient.del(Constants.checkAppLightAppTypeUpList());//删除上线轻应用类型的redis
			jedisClient.del(Constants.checkAppLightAppUpListById(""));//删除上线轻应用的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: queryLightAppTypeUpList
	     * @Description: 获取轻应用类型上线列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryLightAppTypeUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.checkAppLightAppTypeUpList()))){
			beans = lightAppTypeDao.queryLightAppTypeUpList(map);
			jedisClient.set(Constants.checkAppLightAppTypeUpList(), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.checkAppLightAppTypeUpList()), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
