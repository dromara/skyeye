package com.skyeye.common.constans;

import java.util.Map;

/**
 * 
 * @ClassName: Constans
 * @Description: 常量类
 * @author 卫志强
 * @date 2018年6月7日
 *
 */
public class Constants {

	/**
	 * 过滤器过滤文件目录请求项
	 */
	public static final String[] FILTER_FILE_CATALOG_OPTION = { "/html",
			"/css", "/js", "/assets", "/tpl" };

	/**
	 * 过滤器过滤文件后缀请求项
	 */
	public static final String[] FILTER_FILE_SUFFIX_OPTION = { ".json", ".css",
			".js", ".gif", ".jpg", ".eot", ".svg", ".ttf", ".woff", ".woff2",
			".mp4", ".rmvb", ".avi", "3gp", ".html", ".less", ".otf", ".scss",
			".ico" };

	/**
	 * 过滤器过滤请求类型项
	 */
	public static final String[] FILTER_FILE_REQUEST_OPTION = { "/post" };

	/**
	 * 登录页面
	 */
	public static final String LOGIN_PAGE = "/tpl/index/index.html";

	/**
	 * 系统请求参数集合
	 */
	public static Map<String, Map<String, Object>> REQUEST_MAPPING = null;

	/**
	 * 网页请求发送的contentType格式
	 */
	public static final String CONENT_TYPE_WEB_REQ = "application/x-www-form-urlencoded";

	/**
	 * json数据请求发送的数据格式
	 */
	public static final String CONENT_TYPE_JSON_REQ = "application/json";
	
	/**
	 * 用户状态
	 */
	public static final String SYS_USER_LOCK_STATE_ISUNLOCK = "0";//未锁定
	public static final String SYS_USER_LOCK_STATE_ISLOCK = "1";//锁定

}
