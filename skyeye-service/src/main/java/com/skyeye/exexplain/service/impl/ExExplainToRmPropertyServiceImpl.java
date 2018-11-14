package com.skyeye.exexplain.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skyeye.exexplain.dao.ExExplainToRmPropertyDao;
import com.skyeye.exexplain.service.ExExplainToRmPropertyService;
import com.skyeye.jedis.JedisClient;
import net.sf.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class ExExplainToRmPropertyServiceImpl implements ExExplainToRmPropertyService{
	
	@Autowired
	private ExExplainToRmPropertyDao exExplainToRmPropertyDao;
	
	@Autowired
	public JedisClient jedisClient;

	/**
	 * 
	     * @Title: insertExExplainToRmPropertyMation
	     * @Description: 添加标签属性说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertExExplainToRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToRmPropertyDao.queryExExplainToRmPropertyMation(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			String id = ToolUtil.getSurFaceId();
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			exExplainToRmPropertyDao.insertExExplainToRmPropertyMation(map);
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTORMPROPERTY);
			bean = new HashMap<>();
			bean.put("id", id);
			outputObject.setBean(bean);
		}else{
			outputObject.setreturnMessage("该标签属性说明已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToRmPropertyMation
	     * @Description: 编辑标签属性说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExExplainToRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToRmPropertyDao.queryExExplainToRmPropertyMation(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editExExplainToRmPropertyMationById
	     * @Description: 编辑标签属性说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editExExplainToRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToRmPropertyDao.queryExExplainToRmPropertyMation(map);
		if(bean == null){
			outputObject.setreturnMessage("该标签属性说明不存在，不可进行编辑");
		}else{
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTORMPROPERTY);
			exExplainToRmPropertyDao.editExExplainToRmPropertyMationById(map);
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToRmPropertyMationToShow
	     * @Description: 获取标签属性说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryExExplainToRmPropertyMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(jedisClient.exists(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTORMPROPERTY)){
			map = JSONObject.fromObject(jedisClient.get(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTORMPROPERTY));
		}else{
			Map<String, Object> bean = exExplainToRmPropertyDao.queryExExplainToRmPropertyMation(map);
			if(bean == null){
				map.put("title", "标题");
				map.put("content", "等待发布说明。");
			}else{
				jedisClient.set(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTORMPROPERTY, JSON.toJSONString(bean));
				map = bean;
			}
		}
		outputObject.setBean(map);
	}
	
}
