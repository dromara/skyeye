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
					map = JSON.parseObject(str);
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
		for(Map<String, Object> item : propertys){
			if(!formMap.containsKey(item.get("id"))){//键不存在
				return "缺失参数";
			}else{
				String [] ref = item.get("ref").toString().split(",");
				String [] value = (String[]) formMap.get(item.get("id").toString());
				StringBuffer stb = new StringBuffer();
				for(int i = 0;i<value.length;i++){
					stb.append(value[i]);
				}
				String resultStr = ToolUtil.containsBoolean(ref, stb.toString());
				if(resultStr==null){
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLogParams() throws Exception {
		return (Map<String, Object>) getRequest().getSession().getAttribute("admTsyUser");
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLogMenuParams() throws Exception {
		return (List<Map<String, Object>>) getRequest().getSession().getAttribute("admTsyUserMenu");
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLogPermissionParams() throws Exception {
		return (List<Map<String, Object>>) getRequest().getSession().getAttribute("admTsyPermissionMenu");
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
