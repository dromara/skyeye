/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.object;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

public class InputObject extends PutObject implements Serializable{
	
    /**
     * @Fields serialVersionUID : 标识
     */
	private static final long serialVersionUID = 1L;
	
	public static Set<String> keySet;
	
	private static final String REQUEST_ID_KEY = "request_id_key";
	
	/**
	 * 网页请求content-type为application/x-www-form-urlencoded
	 * @throws Exception
	 */
	public static String setParams() throws Exception{
		// 以Map集合存储页面表单传递过来的所有参数的键值对
		String sessionKey = getRequest().getParameter("sessionKey");
		String method = getRequest().getMethod().toUpperCase();
		String contentType = getRequest().getContentType();
		if(ObjectConstant.MethodType.POST.getType().equals(method)
				|| ObjectConstant.MethodType.DELETE.getType().equals(method)
				|| ObjectConstant.MethodType.PUT.getType().equals(method)){
			// POST，DELETE，PUT请求
			if(!ToolUtil.isBlank(contentType) && contentType.indexOf(Constants.CONENT_TYPE_JSON_REQ) != -1){
				// DELETE，PUT请求---json数据请求
				return checkParamsByJson(sessionKey);
			}else {
				// 网页端请求
				return checkParamsWebPost(sessionKey);
			}
		}else if(ObjectConstant.MethodType.GET.getType().equals(method)){
			// GET请求
			if(!ToolUtil.isBlank(contentType) && contentType.indexOf(Constants.CONENT_TYPE_JSON_REQ) != -1){
				// json数据请求
				return checkParamsByJson(sessionKey);
			}else {
				// 网页端请求
				return checkParamsWebGet(sessionKey);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @Title: checkParamsByJson
	 * @Description: json数据请求
	 * @param sessionKey
	 * @return
	 * @throws Exception
	 * @return: String
	 * @throws
	 */
	private static String checkParamsByJson(String sessionKey) throws Exception{
		Map<String, Object> params = getRequestParams();
		if(params == null){
			String str = GetRequestJsonUtils.getRequestJsonString(getRequest());
			try{
				Map<String, Object> map = JSONUtil.toBean(str, null);
				return setParamsObjToMapReStr(sessionKey, map);
			}catch(Exception e){
				Map<String, Object> formMap = ToolUtil.getUrlParams(str);
				return setParamsObjToMapReStr(sessionKey, formMap);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @Title: checkParamsWebPost
	 * @Description: 网页端请求POST获取
	 * @param sessionKey
	 * @return
	 * @throws Exception
	 * @return: String
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private static String checkParamsWebPost(String sessionKey) throws Exception{
		// 获取接口API参数信息
		List<Map<String, Object>> propertys = (List<Map<String, Object>>) Constants.REQUEST_MAPPING.get(sessionKey).get("list");
		// 校验通过的入参集合
		Map<String, Object> resultParams = new HashMap<>();
		for(Map<String, Object> property : propertys){
			// 获取API参数前台传递的key
			String key = property.get("id").toString();
			// 获取前台传递的value
			String value = setDefault((String) getRequest().getParameter(key), property);
			// 获取校验条件
			String[] ref = property.get("ref").toString().split(",");
			// 开始校验
			String resultStr = ObjectConstant.containsBoolean(ref, value);
			if(!ToolUtil.isBlank(resultStr)){
				return String.format(Locale.ROOT, "%s%s%s", "参数：", key, resultStr);
			}
			resultParams.put(property.get("name").toString(), value);
		}
		setRequestParams(sessionKey, resultParams);
		return null;
	}
	
	/**
	 * 
	 * @Title: checkParamsWebGet
	 * @Description: 网页端请求GET请求
	 * @param sessionKey
	 * @return
	 * @throws Exception
	 * @return: String
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private static String checkParamsWebGet(String sessionKey) throws Exception{
		// 获取接口API参数信息
		List<Map<String, Object>> propertys = (List<Map<String, Object>>) Constants.REQUEST_MAPPING.get(sessionKey).get("list");
		// 校验通过的入参集合
		Map<String, Object> resultParams = new HashMap<>();
		Map<String, String[]> params = getRequest().getParameterMap();
		for(Map<String, Object> property : propertys){
			// 获取API参数的key
			String key = property.get("id").toString();
			// 获取前台传递的value
			String value = setDefault(arrayToString(params.get(key)), property);
			// 获取校验条件
			String[] ref = property.get("ref").toString().split(",");
			// 开始校验
			String resultStr = ObjectConstant.containsBoolean(ref, value);
			if(!ToolUtil.isBlank(resultStr)){
				return String.format(Locale.ROOT, "%s%s%s", "参数：", key, resultStr);
			}
			resultParams.put(property.get("name").toString(), value);
		}
		setRequestParams(sessionKey, resultParams);
		return null;
	}
	
	/**
	 * 
	 * @Title: setDefault
	 * @Description: 设置默认值
	 * @param value
	 * @param property
	 * @return
	 * @return: String
	 * @throws
	 */
	private static String setDefault(String value, Map<String, Object> property){
		// 只有这个值传""时才会设置默认值
		if(ToolUtil.isBlank(value) && !ToolUtil.isBlank(property.get("default").toString())){
			value = property.get("default").toString();
		}
		return value;
	}
	
	private static String arrayToString(String[] value){
		if(value == null){
			return "";
		}
		StringBuffer stb = new StringBuffer();
		for(int i = 0; i < value.length; i++){
			stb.append(value[i]);
		}
		return stb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private static String setParamsObjToMapReStr(String sessionKey, Map<String, Object> formMap) throws Exception{
		List<Map<String, Object>> propertys = (List<Map<String, Object>>) Constants.REQUEST_MAPPING.get(sessionKey).get("list");
		// 校验通过的入参集合
		Map<String, Object> resultParams = new HashMap<>();
		for(Map<String, Object> property : propertys){
			String key = property.get("id").toString();
			String value = null;
			if(!formMap.containsKey(key)){
				value = "";
			}else{
				value = java.net.URLDecoder.decode(formMap.get(key).toString(), "UTF-8").toString();
			}
			value = setDefault(value, property);
			String[] ref = property.get("ref").toString().split(",");
			String resultStr = ObjectConstant.containsBoolean(ref, value);
			if(!ToolUtil.isBlank(resultStr)){
				return String.format(Locale.ROOT, "%s%s%s", "参数：", key, resultStr);
			}
			resultParams.put(property.get("name").toString(), value);
		}
		setRequestParams(sessionKey, resultParams);
		return null;
	}
	
	private static String getRequestId(){
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
		HttpServletRequest request = attributes.getRequest();
		return String.format(Locale.ROOT, "%s-%s%s-params", sessionId, request.getServletPath(), DateUtil.getTimeStrAndToString());
	}
	
	private static void setRequestParams(String sessionKey, Map<String, Object> resultParams) throws Exception{
		// 获取唯一id作为该次请求的标识符
		String requestId = getRequestId();
		getRequest().setAttribute(REQUEST_ID_KEY, requestId);
		// 获取用户信息以及其他参数
		String allUse = Constants.REQUEST_MAPPING.get(sessionKey).get("allUse").toString();
		if("1".equals(allUse) || "2".equals(allUse)){
			// 需要登录才能访问
			resultParams.put("userToken", GetUserToken.getUserToken(getRequest()));
		}
		resultParams.put("urlUseJurisdiction", allUse);//URL访问权限参数
		JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
		jedisClient.set(requestId, JSONUtil.toJsonStr(resultParams), 10);
	}
	
	private static Map<String, Object> getRequestParams(){
		String requestId = (String) getRequest().getAttribute(REQUEST_ID_KEY);
		if(ToolUtil.isBlank(requestId)){
			return null;
		}
		JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
		return JSONUtil.toBean(jedisClient.get(requestId), null);
	}
	
	public static Map<String, Object> getMap() throws Exception{
		return getRequestParams();
	}

	public Map<String, Object> getParams() throws Exception {
		return getRequestParams();
	}
	
	/**
	 * 获取登录信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLogParams() throws Exception {
		String userToken = GetUserToken.getUserToken(getRequest());
		JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
		// 用户信息
		return JSONUtil.toBean(jedisClient.get("userMation:" + userToken), null);
	}
	
	/**
	 * 获取登录信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getLogParamsStatic() throws Exception {
		String userToken = GetUserToken.getUserToken(getRequest());
		JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
		// 用户信息
		return JSONUtil.toBean(jedisClient.get("userMation:" + userToken), null);
	}
	
	/**
	 * 获取桌面菜单信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLogDeskTopMenuParams() throws Exception {
		String userToken = GetUserToken.getUserToken(getRequest());
		JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
		//桌面菜单信息
		return JSONUtil.toList(jedisClient.get("deskTopsMation:" + userToken), null);
	}
	
	/**
	 * 获取所有菜单信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLogAllMenuParams() throws Exception {
		String userToken = GetUserToken.getUserToken(getRequest());
		JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
		//所有菜单信息
		return JSONUtil.toList(jedisClient.get("allMenuMation:" + userToken), null);
	}
	
	public void removeSession() throws Exception {
		getRequest().getSession().invalidate();
	}
	
	public Set<String> getKeyForRequestMap(){
		return keySet;
	}

	public static Set<String> getKeySet() {
		return keySet;
	}

	public static void setKeySet(Set<String> keySet) {
		InputObject.keySet = keySet;
	}
	
}
