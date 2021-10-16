/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.KnowlgConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.KnowledgeTypeDao;
import com.skyeye.eve.service.KnowledgeTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: KnowledgeTypeServiceImpl
 * @Description: 知识库类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class KnowledgeTypeServiceImpl implements KnowledgeTypeService {

	@Autowired
	private KnowledgeTypeDao knowledgeTypeDao;
	
	@Autowired
	public JedisClientService jedisClient;

	public static enum state{
		START_NEW(1, "新建"),
		START_UP(2, "上线"),
		START_DOWN(3, "下线"),
		START_DELETE(4, "删除");
		private int state;
		private String name;
		state(int state, String name){
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
	     * @Title: queryKnowledgeTypeList
	     * @Description: 查出所有知识库类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryKnowledgeTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = knowledgeTypeDao.queryKnowledgeTypeList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: insertKnowledgeTypeMation
	     * @Description: 新增知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertKnowledgeTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 查询是否已经存在该知识库类型名称
		Map<String, Object> bean = knowledgeTypeDao.queryKnowledgeTypeMationByName(map.get("name").toString(), map.get("parentId").toString(), "");
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该名称已存在，请更换");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", state.START_NEW.getState());//默认新建
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			knowledgeTypeDao.insertKnowledgeTypeMation(map);
		}
	}
	
	/**
	 * 
	     * @Title: deleteKnowledgeTypeById
	     * @Description: 删除知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgeTypeDao.queryKnowledgeTypeStateById(map);
		int nowState = Integer.parseInt(bean.get("state").toString());
		if(nowState == state.START_NEW.getState() || nowState == state.START_DOWN.getState()){
			// 新建或者下线可以删除
			knowledgeTypeDao.editKnowledgeTypeStateById(map.get("id").toString(), state.START_DELETE.getState(),
					inputObject.getLogParams().get("id").toString(), DateUtil.getTimeAndToString());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
		
	}

	/**
	 * 
	     * @Title: updateUpKnowledgeTypeById
	     * @Description: 上线知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgeTypeDao.queryKnowledgeTypeStateById(map);
		int nowState = Integer.parseInt(bean.get("state").toString());
		if(nowState == state.START_NEW.getState() || nowState == state.START_DOWN.getState()){
			// 新建或者下线可以上线
			knowledgeTypeDao.editKnowledgeTypeStateById(map.get("id").toString(), state.START_UP.getState(),
					inputObject.getLogParams().get("id").toString(), DateUtil.getTimeAndToString());
			// 删除上线知识库类型的redis
			jedisClient.del(KnowlgConstants.sysSecondKnowledgeTypeUpStateList());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownKnowledgeTypeById
	     * @Description: 下线知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = knowledgeTypeDao.queryKnowledgeTypeStateById(map);
		int nowState = Integer.parseInt(bean.get("state").toString());
		if(nowState == state.START_UP.getState()){
			// 上线状态可以下线
			knowledgeTypeDao.editKnowledgeTypeStateById(map.get("id").toString(), state.START_DOWN.getState(),
					inputObject.getLogParams().get("id").toString(), DateUtil.getTimeAndToString());
			// 删除上线知识库类型的redis
			jedisClient.del(KnowlgConstants.sysSecondKnowledgeTypeUpStateList());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectKnowledgeTypeById
	     * @Description: 通过id查找对应的知识库类型信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = knowledgeTypeDao.selectKnowledgeTypeById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editKnowledgeTypeMationById
	     * @Description: 编辑知识库类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editKnowledgeTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 查询这条知识库类型的状态
		Map<String, Object> bean = knowledgeTypeDao.queryKnowledgeTypeStateById(map);
		int nowState = Integer.parseInt(bean.get("state").toString());
		if(nowState == state.START_NEW.getState() || nowState == state.START_DOWN.getState()){
			// 新建或者下线可以编辑
			bean = knowledgeTypeDao.queryKnowledgeTypeMationByName(map.get("name").toString(), "", map.get("id").toString());
			// 查询知识库类型名称是否存在
			if(bean != null && !bean.isEmpty()){
				outputObject.setreturnMessage("该名称已存在，请更换");
			}else{
				map.put("lastUpdateId", inputObject.getLogParams().get("id"));
				map.put("lastUpdateTime", DateUtil.getTimeAndToString());
				knowledgeTypeDao.editKnowledgeTypeMationById(map);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: queryUpKnowledgeTypeTreeMation
	 * @Description: 获取已经上线的知识库类型，数据为tree格式
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@Override
	public void queryUpKnowledgeTypeTreeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = new ArrayList<>();
		String key = KnowlgConstants.sysSecondKnowledgeTypeUpStateList();
		if(ToolUtil.isBlank(jedisClient.get(key))){
			beans = knowledgeTypeDao.queryUpKnowledgeTypeTreeMation();
			// 转为树
			beans = ToolUtil.listToTree(beans, "id", "pId", "children");
			jedisClient.set(key, JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(key).toString(), null);
		}
		outputObject.setBeans(beans);
	}

}
