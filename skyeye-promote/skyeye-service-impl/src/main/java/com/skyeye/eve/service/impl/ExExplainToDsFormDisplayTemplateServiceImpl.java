package com.skyeye.eve.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.jedis.JedisClientService;

import net.sf.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ExExplainToDsFormDisplayTemplateDao;
import com.skyeye.eve.service.ExExplainToDsFormDisplayTemplateService;

@Service
public class ExExplainToDsFormDisplayTemplateServiceImpl implements ExExplainToDsFormDisplayTemplateService{
	
	@Autowired
	private ExExplainToDsFormDisplayTemplateDao exExplainToDsFormDisplayTemplateDao;
	
	@Autowired
	public JedisClientService jedisClient;

	/**
	 * 
	     * @Title: insertExExplainToDsFormDisplayTemplateMation
	     * @Description: 添加动态表单数据展示模板说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertExExplainToDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToDsFormDisplayTemplateDao.queryExExplainToDsFormDisplayTemplateMation(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			String id = ToolUtil.getSurFaceId();
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			exExplainToDsFormDisplayTemplateDao.insertExExplainToDsFormDisplayTemplateMation(map);
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMDISPLAYTEMPLATE);
          	bean = new HashMap<>();
			bean.put("id", id);
			outputObject.setBean(bean);
		}else{
			outputObject.setreturnMessage("该动态表单数据展示模板说明已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToDsFormDisplayTemplateMation
	     * @Description: 编辑动态表单数据展示模板说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExExplainToDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToDsFormDisplayTemplateDao.queryExExplainToDsFormDisplayTemplateMation(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editExExplainToDsFormDisplayTemplateMationById
	     * @Description: 编辑动态表单数据展示模板说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editExExplainToDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToDsFormDisplayTemplateDao.queryExExplainToDsFormDisplayTemplateMation(map);
		if(bean == null){
          	outputObject.setreturnMessage("该动态表单数据展示模板说明不存在，不可进行编辑");
		}else{
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMDISPLAYTEMPLATE);
			exExplainToDsFormDisplayTemplateDao.editExExplainToDsFormDisplayTemplateMationById(map);
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToDsFormDisplayTemplateMationToShow
	     * @Description: 获取动态表单数据展示模板说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryExExplainToDsFormDisplayTemplateMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(jedisClient.exists(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMDISPLAYTEMPLATE)){
			map = JSONObject.fromObject(jedisClient.get(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMDISPLAYTEMPLATE));
		}else{
			Map<String, Object> bean = exExplainToDsFormDisplayTemplateDao.queryExExplainToDsFormDisplayTemplateMation(map);
			if(bean == null){
				map.put("title", "标题");
				map.put("DisplayTemplate", "等待发布说明。");
			}else{
				jedisClient.set(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMDISPLAYTEMPLATE, JSON.toJSONString(bean));
				map = bean;
			}
		}
		outputObject.setBean(map);
	}
	
}
