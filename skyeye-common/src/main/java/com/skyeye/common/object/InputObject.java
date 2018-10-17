package com.skyeye.common.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.util.ToolUtil;


public class InputObject extends PutObject implements Serializable{

	
    /**
     * @Fields serialVersionUID : 标识
     */
	private static final long serialVersionUID = 1L;
	
	public static Map<String, Object> map;
	
	public static Set<String> keySet;
	
	private static Map<String, Object> USER_MATION = null;//用户信息
	
	private static List<Map<String, Object>> USER_DESKTOP_MENU_MATION = null;//用户桌面菜单信息
	
	private static List<Map<String, Object>> USER_ALL_MENU_MATION = null;//用户全部菜单信息
	
	public InputObject() throws Exception{
		map = new HashMap<String, Object>();
	}
	
	/**
	 * 网页请求content-type为application/x-www-form-urlencoded
	 * @throws Exception
	 */
	public static String setParams() throws Exception{
		//以Map集合存储页面表单传递过来的所有参数的键值对
		String key = getRequest().getParameter("sessionKey");
		String contentType = getRequest().getContentType();
		if(contentType == null){
			Map<String, String[]> formMap = getRequest().getParameterMap();
			return setParamsToMap(key, formMap);
		}else{
			if(contentType.contains(Constants.CONENT_TYPE_WEB_REQ)){//网页端请求
				Map<String, String[]> formMap = getRequest().getParameterMap();
				return setParamsToMap(key, formMap);
			}else if(contentType.indexOf(Constants.CONENT_TYPE_JSON_REQ)!=-1){//json数据请求
				String str = GetRequestJsonUtils.getRequestJsonString(getRequest());
				try{
					String allUse = Constants.REQUEST_MAPPING.get(key).get("allUse").toString();
					map = JSON.parseObject(str);
					if("1".equals(allUse)){//需要登录才能访问
						if(!map.containsKey("userToken")){//usertoken键不存在
							return "缺失重要参数";
						}else{
							String [] value = (String[]) map.get("userToken");
							StringBuffer stb = new StringBuffer();
							for(int i = 0; i < value.length; i++){
								stb.append(value[i]);
							}
							map.put("userToken", stb.toString());
						}
					}
					map.put("urlUseJurisdiction", allUse);//URL访问权限参数
				}catch(Exception e){
					Map<String, Object> formMap = ToolUtil.getUrlParams(str);
					return setParamsObjToMap(key, formMap);
				}
			}else{
				Map<String, String[]> formMap = getRequest().getParameterMap();
				return setParamsToMap(key, formMap);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static String setParamsToMap(String key, Map<String, String[]> formMap){
		List<Map<String, Object>> propertys = (List<Map<String, Object>>) Constants.REQUEST_MAPPING.get(key).get("list");
		String allUse = Constants.REQUEST_MAPPING.get(key).get("allUse").toString();
		if("1".equals(allUse)){//需要登录才能访问
			if(!formMap.containsKey("userToken")){//usertoken键不存在
				return "缺失重要参数";
			}else{
				String [] value = (String[]) formMap.get("userToken");
				StringBuffer stb = new StringBuffer();
				for(int i = 0; i < value.length; i++){
					stb.append(value[i]);
				}
				map.put("userToken", stb.toString());
			}
		}
		map.put("urlUseJurisdiction", allUse);//URL访问权限参数
		for(Map<String, Object> item : propertys){
			if(!formMap.containsKey(item.get("id"))){//键不存在
				return "缺失参数";
			}else{
				String [] ref = item.get("ref").toString().split(",");
				String [] value = (String[]) formMap.get(item.get("id").toString());
				StringBuffer stb = new StringBuffer();
				for(int i = 0; i < value.length; i++){
					stb.append(value[i]);
				}
				String resultStr = ToolUtil.containsBoolean(ref, stb.toString());
				if(resultStr == null){
					map.put(item.get("name").toString(), stb.toString());
				} else{
					return "参数：" + item.get("id").toString() + resultStr;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static String setParamsObjToMap(String key, Map<String, Object> formMap) throws Exception{
		List<Map<String, Object>> propertys = (List<Map<String, Object>>) Constants.REQUEST_MAPPING.get(key).get("list");
		String allUse = Constants.REQUEST_MAPPING.get(key).get("allUse").toString();
		if("1".equals(allUse)){//需要登录才能访问
			if(!formMap.containsKey("userToken")){//usertoken键不存在
				return "缺失重要参数";
			}else{
				String [] value = (String[]) formMap.get("userToken");
				StringBuffer stb = new StringBuffer();
				for(int i = 0; i < value.length; i++){
					stb.append(value[i]);
				}
				map.put("userToken", stb.toString());
			}
		}
		map.put("urlUseJurisdiction", allUse);//URL访问权限参数
		for(Map<String, Object> item : propertys){
			if(!formMap.containsKey(item.get("id"))){//键不存在
				return "缺失参数";
			}else{
				String [] ref = item.get("ref").toString().split(",");
				String value = java.net.URLDecoder.decode(formMap.get(item.get("id").toString()).toString(), "UTF-8").toString();
				String resultStr = ToolUtil.containsBoolean(ref, value);
				if(resultStr == null){
					map.put(item.get("name").toString(), value);
				} else{
					return "参数：" + item.get("id").toString() + resultStr;
				}
			}
		}
		return null;
	}

	public static void setParams(Map<String, Object> map){
		setMap(map);
	}

	public Map<String, Object> getParams() {
		return map;
	}
	
	/**
	 * 获取登录信息
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLogParams() throws Exception {
		return USER_MATION;
	}
	
	/**
	 * 获取桌面菜单信息
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getLogDeskTopMenuParams() throws Exception {
		return USER_DESKTOP_MENU_MATION;
	}
	
	/**
	 * 获取所有菜单信息
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getLogAllMenuParams() throws Exception {
		return USER_ALL_MENU_MATION;
	}
	
	public static Map<String, Object> getUSER_MATION() {
		return USER_MATION;
	}

	public static void setUSER_MATION(Map<String, Object> uSER_MATION) {
		USER_MATION = uSER_MATION;
	}

	public static List<Map<String, Object>> getUSER_DESKTOP_MENU_MATION() {
		return USER_DESKTOP_MENU_MATION;
	}

	public static void setUSER_DESKTOP_MENU_MATION(List<Map<String, Object>> uSER_DESKTOP_MENU_MATION) {
		USER_DESKTOP_MENU_MATION = uSER_DESKTOP_MENU_MATION;
	}

	public static List<Map<String, Object>> getUSER_ALL_MENU_MATION() {
		return USER_ALL_MENU_MATION;
	}

	public static void setUSER_ALL_MENU_MATION(List<Map<String, Object>> uSER_ALL_MENU_MATION) {
		USER_ALL_MENU_MATION = uSER_ALL_MENU_MATION;
	}

	public void removeSession() throws Exception {
		getRequest().getSession().invalidate();
	}
	
	public Set<String> getKeyForRequestMap(){
		return keySet;
	}

	public static Map<String, Object> getMap() {
		return map;
	}

	public static void setMap(Map<String, Object> map) {
		InputObject.map = map;
	}

	public static Set<String> getKeySet() {
		return keySet;
	}

	public static void setKeySet(Set<String> keySet) {
		InputObject.keySet = keySet;
	}
	
}
