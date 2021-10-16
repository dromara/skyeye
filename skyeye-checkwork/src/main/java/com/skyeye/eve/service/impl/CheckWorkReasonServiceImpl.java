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
import com.skyeye.eve.dao.CheckWorkReasonDao;
import com.skyeye.eve.service.CheckWorkReasonService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CheckWorkReasonServiceImpl implements CheckWorkReasonService {

	@Autowired
	private CheckWorkReasonDao checkWorkReasonDao;
	
	@Autowired
	private JedisClientService jedisClient;

	/**
	 * 
	     * @Title: queryCheckWorkReasonList
	     * @Description: 获取申诉原因名称列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckWorkReasonList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = checkWorkReasonDao.queryCheckWorkReasonList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertCheckWorkReasonMation
	     * @Description: 添加申诉原因名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCheckWorkReasonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkReasonDao.queryCheckWorkReasonByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该申诉原因名称已存在，请更换");
		}else{
			Map<String, Object> itemCount = checkWorkReasonDao.queryCheckWorkReasonBySimpleLevel(map);
			Map<String, Object> user = inputObject.getLogParams();
			int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
			map.put("orderBy", thisOrderBy);
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", "0");//默认新建
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			checkWorkReasonDao.insertCheckWorkReasonMation(map);
			jedisClient.del(Constants.checkWorkReasonUpStateList());//删除上线类型的redis
		}
	}

	/**
	 * 
	     * @Title: deleteCheckWorkReasonById
	     * @Description: 删除申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		checkWorkReasonDao.deleteCheckWorkReasonById(map);
	}

	/**
	 * 
	     * @Title: updateUpCheckWorkReasonById
	     * @Description: 上线申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkReasonDao.queryCheckWorkReasonStateById(map);
		if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线可以上线
			checkWorkReasonDao.updateUpCheckWorkReasonById(map);
			jedisClient.del(Constants.checkWorkReasonUpStateList());//删除上线类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: updateDownCheckWorkReasonById
	     * @Description: 下线申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkReasonDao.queryCheckWorkReasonStateById(map);
		if("1".equals(bean.get("state").toString())){//上线状态可以下线
			checkWorkReasonDao.updateDownCheckWorkReasonById(map);
			jedisClient.del(Constants.checkWorkReasonUpStateList());//删除上线图片类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: selectCheckWorkReasonById
	     * @Description: 通过id查找对应的申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = checkWorkReasonDao.selectCheckWorkReasonById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCheckWorkReasonMationById
	     * @Description: 通过id编辑对应的申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editCheckWorkReasonMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		checkWorkReasonDao.editCheckWorkReasonMationById(map);
	}

	/**
	 * 
	     * @Title: queryCheckWorkReasonUpMationById
	     * @Description: 上移申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void queryCheckWorkReasonUpMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkReasonDao.queryCheckWorkReasonUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			checkWorkReasonDao.editCheckWorkReasonMationOrderNumUpById(map);
			checkWorkReasonDao.editCheckWorkReasonMationOrderNumUpById(bean);
			jedisClient.del(Constants.checkWorkReasonUpStateList());//删除上线图片类型的redis
		}
	}

	/**
	 * 
	     * @Title: queryCheckWorkReasonDownMationById
	     * @Description: 下移申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void queryCheckWorkReasonDownMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkReasonDao.queryCheckWorkReasonDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			checkWorkReasonDao.editCheckWorkReasonMationOrderNumUpById(map);
			checkWorkReasonDao.editCheckWorkReasonMationOrderNumUpById(bean);
			jedisClient.del(Constants.checkWorkReasonUpStateList());//删除上线图片类型的redis
		}
	}

	/**
	 * 
	     * @Title: queryCheckWorkReasonUpStateList
	     * @Description: 获取已经上线的申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryCheckWorkReasonUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.checkWorkReasonUpStateList()))){
			beans = checkWorkReasonDao.queryCheckWorkReasonUpStateList(map);
			jedisClient.set(Constants.checkWorkReasonUpStateList(), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.checkWorkReasonUpStateList()), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
}
