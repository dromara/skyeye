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
import com.skyeye.eve.dao.ExExplainToDsFormContentDao;
import com.skyeye.eve.service.ExExplainToDsFormContentService;

@Service
public class ExExplainToDsFormContentServiceImpl implements ExExplainToDsFormContentService{
	
	@Autowired
	private ExExplainToDsFormContentDao exExplainToDsFormContentDao;
	
	@Autowired
	public JedisClientService jedisClient;

	/**
	 * 
	     * @Title: insertExExplainToDsFormContentMation
	     * @Description: 添加动态表单内容项说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToDsFormContentDao.queryExExplainToDsFormContentMation(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			String id = ToolUtil.getSurFaceId();
			map.put("id", id);
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			exExplainToDsFormContentDao.insertExExplainToDsFormContentMation(map);
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMCONTENT);
          	bean = new HashMap<>();
			bean.put("id", id);
			outputObject.setBean(bean);
		}else{
			outputObject.setreturnMessage("该动态表单内容项说明已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToDsFormContentMation
	     * @Description: 编辑动态表单内容项说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToDsFormContentDao.queryExExplainToDsFormContentMation(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editExExplainToDsFormContentMationById
	     * @Description: 编辑动态表单内容项说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editExExplainToDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = exExplainToDsFormContentDao.queryExExplainToDsFormContentMation(map);
		if(bean == null){
          	outputObject.setreturnMessage("该动态表单内容项说明不存在，不可进行编辑");
		}else{
			jedisClient.del(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMCONTENT);
			exExplainToDsFormContentDao.editExExplainToDsFormContentMationById(map);
		}
	}

	/**
	 * 
	     * @Title: queryExExplainToDsFormContentMationToShow
	     * @Description: 获取动态表单内容项说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryExExplainToDsFormContentMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(jedisClient.exists(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMCONTENT)){
			map = JSONObject.fromObject(jedisClient.get(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMCONTENT));
		}else{
			Map<String, Object> bean = exExplainToDsFormContentDao.queryExExplainToDsFormContentMation(map);
			if(bean == null){
				map.put("title", "标题");
				map.put("content", "等待发布说明。");
			}else{
				jedisClient.set(Constants.REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMCONTENT, JSON.toJSONString(bean));
				map = bean;
			}
		}
		outputObject.setBean(map);
	}
	
}
