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
import com.skyeye.eve.dao.ReportTypeDao;
import com.skyeye.eve.service.ReportTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ReportTypeServiceImpl
 * @Description: 论坛举报类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:52
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ReportTypeServiceImpl implements ReportTypeService {

	@Autowired
	private ReportTypeDao reportTypeDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: queryReportTypeList
	     * @Description: 获取举报类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReportTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = reportTypeDao.queryReportTypeList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertReportTypeMation
	     * @Description: 新增举报类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertReportTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = reportTypeDao.queryReportTypeMationByTypeName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该类型名称已存在，不能重复添加！");
		}else{
			Map<String, Object> orderBy = reportTypeDao.queryReportTypeAfterOrderBum(map);
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
			reportTypeDao.insertReportTypeMation(map);
		}
	}

	/**
	 * 
	     * @Title: queryReportTypeMationToEditById
	     * @Description: 编辑举报类型时进行信息回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReportTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = reportTypeDao.queryReportTypeMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editReportTypeMationById
	     * @Description: 编辑举报类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editReportTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> beans = reportTypeDao.queryReportTypeMationStateById(map);
		if("1".equals(beans.get("state").toString()) || "3".equals(beans.get("state").toString())){//新建或者下线的状态可以编辑
			Map<String, Object> bean = reportTypeDao.queryReportTypeMationByTypeNameAndId(map);
			if(bean == null){
				reportTypeDao.editReportTypeMationById(map);
			}else{
				outputObject.setreturnMessage("该应用名称已存在，不可进行二次保存");
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editReportTypeSortTopById
	     * @Description: 举报类型展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editReportTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = reportTypeDao.queryReportTypeISTopByThisId(map);//根据同一级排序获取这条数据的上一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠前举报类型，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			reportTypeDao.editReportTypeSortTopById(map);
			reportTypeDao.editReportTypeSortTopById(topBean);
			jedisClient.del(Constants.forumReportTypeUpList());//删除上线举报类型的redis
		}
	}

	/**
	 * 
	     * @Title: editReportTypeSortLowerById
	     * @Description: 举报类型展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editReportTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> topBean = reportTypeDao.queryReportTypeISLowerByThisId(map);//根据同一级排序获取这条数据的下一条数据
		if(topBean == null){
			outputObject.setreturnMessage("已经是最靠后举报类型，无法移动。");
		}else{
			map.put("orderBy", topBean.get("orderBy"));
			topBean.put("orderBy", topBean.get("thisOrderBy"));
			reportTypeDao.editReportTypeSortLowerById(map);
			reportTypeDao.editReportTypeSortLowerById(topBean);
			jedisClient.del(Constants.forumReportTypeUpList());//删除上线举报类型的redis
		}
	}
	
	/**
	 * 
	     * @Title: deleteReportTypeById
	     * @Description: 删除举报类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteReportTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = reportTypeDao.queryReportTypeMationStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线的状态可以删除
			reportTypeDao.deleteReportTypeById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editReportTypeUpTypeById
	     * @Description: 举报类型上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editReportTypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = reportTypeDao.queryReportTypeMationStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线的状态可以上线
			reportTypeDao.editReportTypeUpTypeById(map);
			jedisClient.del(Constants.forumReportTypeUpList());//删除上线举报类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editReportTypeDownTypeById
	     * @Description: 举报类型下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editReportTypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = reportTypeDao.queryReportTypeMationStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			reportTypeDao.editReportTypeDownTypeById(map);
			jedisClient.del(Constants.forumReportTypeUpList());//删除上线举报类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: queryReportTypeUpList
	     * @Description: 获取举报类型上线列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReportTypeUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.forumReportTypeUpList()))){
			beans = reportTypeDao.queryReportTypeUpList(map);
			jedisClient.set(Constants.forumReportTypeUpList(), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.forumReportTypeUpList()), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
