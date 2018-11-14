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
	 * IP过滤
	 */
	public static final String[] FILTER_FILE_IP_OPTION = { "0:0:0:0:0:0:0:1", "127.0.0.1"};

	/**
	 * 过滤器过滤请求类型项
	 */
	public static final String[] FILTER_FILE_REQUEST_OPTION = { "/post" };

	/**
	 * 登录页面
	 */
	public static final String LOGIN_PAGE = "/tpl/index/login.html";
	
	/**
	 * 控制页面
	 */
	public static final String INDEX_PAGE = "/tpl/index/index.html";

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
	
	/**
	 * 菜单类型
	 */
	public static final String SYS_MENU_TYPE_IS_IFRAME = "iframe";
	public static final String SYS_MENU_TYPE_IS_HTML = "html";
	
	/**
	 * 菜单链接打开类型，父菜单默认为1.1：打开iframe，2：打开html。
	 */
	public static final String SYS_MENU_OPEN_TYPE_IS_IFRAME = "1";
	public static final String SYS_MENU_OPEN_TYPE_IS_HTML = "2";
	
	/**
	 * 项目web层名称
	 */
	public static final String PROJECT_WEB = "\\skyeye\\skyeye-web";
	
	/**
	 * 保存模板说明的redis的key
	 */
	public static final String REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTOCODEMODEL = "exexplaintocodemodel";//代码生成器模板规范说明key
	public static final String REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTODSFORMCONTENT = "exexplaintodsformcontent";//动态表单内容项模板规范说明key
	public static final String REDIS_CODEMODEL_EXPLAIN_EXEXPLAINTORMPROPERTY = "exexplaintormproperty";//小程序标签属性模板规范说明key
	
	/**
	 * 微信小程序页面id的序列号
	 */
	public static final String REDIS_PROJECT_PAGE_FILE_PATH = "projectpagefilepath";//页面路径的序列号key
	public static final String REDIS_PROJECT_PAGE_FILE_NAME = "projectpagefilename";//页面名称的序列号key
	public static final String REDIS_PROJECT_PAGE_FILE_PATH_NUM = "1000";//页面路径的序列号默认值
	public static final String REDIS_PROJECT_PAGE_FILE_NAME_NUM = "1000";//页面名称的序列号默认值
	

}
