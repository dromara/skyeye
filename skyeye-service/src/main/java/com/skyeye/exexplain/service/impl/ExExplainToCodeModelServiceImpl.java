package com.skyeye.exexplain.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skyeye.exexplain.dao.ExExplainToCodeModelDao;
import com.skyeye.exexplain.service.ExExplainToCodeModelService;
import com.skyeye.jedis.JedisClient;
import net.sf.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class ExExplainToCodeModelServiceImpl implements ExExplainToCodeModelService{
	
	@Autowired
	private ExExplainToCodeModelDao exExplainToCodeModelDao;
	
	@Autowired
	public JedisClient jedisClient;

	/**
	 * 
	     * @Title: insertExExplainToCodeModelMation
	     * @Description: 添加代码生成器说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToCodeModelDao.queryExExplainToCodeModelMation(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			String id = ToolUtil.getSurFaceId();
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			exExplainToCodeModelDao.insertExExplainToCodeModelMation(map);
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTOCODEMODEL);
			bean = new HashMap<>();
			bean.put("id", id);
			outputObject.setBean(bean);
		}else{
			outputObject.setreturnMessage("该代码生成器说明已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToCodeModelMation
	     * @Description: 编辑代码生成器说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToCodeModelDao.queryExExplainToCodeModelMation(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editExExplainToCodeModelMationById
	     * @Description: 编辑代码生成器说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editExExplainToCodeModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToCodeModelDao.queryExExplainToCodeModelMation(map);
		if(bean == null){
			outputObject.setreturnMessage("该代码生成器说明不存在，不可进行编辑");
		}else{
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTOCODEMODEL);
			exExplainToCodeModelDao.editExExplainToCodeModelMationById(map);
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToCodeModelMationToShow
	     * @Description: 获取代码生成器说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryExExplainToCodeModelMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(jedisClient.exists(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTOCODEMODEL)){
			map = JSONObject.fromObject(jedisClient.get(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTOCODEMODEL));
		}else{
			Map<String, Object> bean = exExplainToCodeModelDao.queryExExplainToCodeModelMation(map);
			if(bean == null){
				map.put("title", "标题");
				map.put("content", "等待发布说明。");
			}else{
				jedisClient.set(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTOCODEMODEL, JSON.toJSONString(bean));
				map = bean;
			}
		}
		outputObject.setBean(map);
	}
	
}
