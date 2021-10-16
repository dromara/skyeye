/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.start.thread;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.ObjectConstant;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: TokenThread
 * @Description: 通过线程去读取系统中的请求
 * @author 卫志强
 * @date 2018年6月8日
 *
 */
public class TokenThread implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(TokenThread.class);

	private static final String API_PLACEHOLDER = "-";
	
	private static final String EMPTY = "";
	
	public TokenThread() {
	}

	@Override
	public void run() {
		try {
			logger.info("start loading xml [{}]", "线程读取配置信息路径开始");
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath*:reqmapping/mapping/*.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			Constants.REQUEST_MAPPING = new HashMap<>();
			for (Resource resource : resources) {
				logger.info("loading resource name is [{}]", resource.getFilename());
				Document doc = db.parse(resource.getInputStream());
				Element controller = doc.getDocumentElement();
				NodeList controllerNodes = controller.getElementsByTagName("url");
				// 单个API接口入参集
				List<Map<String, Object>> APIParamsBeans;
				Map<String, Object> controllerBean;
				// action节点信息
				Element action;
				for (int i = 0; i < controllerNodes.getLength(); i++) {
					action = (Element) controllerNodes.item(i);
					APIParamsBeans = new ArrayList<>();
					controllerBean = new HashMap<>();
					// 1.获取API信息
					getRequestPath(controllerBean, action, ToolUtil.isBlank(controller.getAttribute("modelName")) ? resource.getFilename() : controller.getAttribute("modelName"));
					// 2.获取API入参信息
					getRequestParams(action.getElementsByTagName("property"), APIParamsBeans);
					controllerBean.put("list", APIParamsBeans);
					// 3.加入系统API集合中
					Constants.REQUEST_MAPPING.put(action.getAttribute("id").toString(), controllerBean);
				}
			}

			logger.info("end loading xml [{}]", "线程读取配置信息路径结束");
			logger.info("Read request path file completed: [{}] interfaces in total", Constants.REQUEST_MAPPING.size());
			JedisClientService jedisClientService = SpringUtils.getBean(JedisClientService.class);
			jedisClientService.set(Constants.SYS_EVE_MAIN_REQMAPPING_KEY, JSONUtil.toJsonStr(Constants.REQUEST_MAPPING));
		} catch (Exception e) {
			logger.error("init request mation is wrong {}", e);
		}
	}

	/**
	 * 
	 * @Title: getRequestPath 
	 * @Description: 获取接口信息 
	 * @param controllerBean 
	 * @param action 
	 * @param modelName 所属模块名
	 * @return: void @throws
	 */
	private void getRequestPath(Map<String, Object> controllerBean, Element action, String modelName) {
		// 工程模块
		controllerBean.put("modelName", modelName);
		// 是否需要登录才能使用 1是 0否 2需要登陆才能访问，但无需授权 默认为否
		if (!ToolUtil.isBlank(action.getAttribute("allUse"))) {
			controllerBean.put("allUse", action.getAttribute("allUse"));
		} else {
			controllerBean.put("allUse", 0);
		}
		controllerBean.put("groupName", action.getAttribute("groupName"));
		// 请求地址
		controllerBean.put("path", action.getAttribute("path"));
		// 请求方式
		controllerBean.put("method", ToolUtil.isBlank(action.getAttribute("method"))
				? ObjectConstant.MethodType.POST.getType() : action.getAttribute("method"));
		// API描述
		controllerBean.put("val", action.getAttribute("val"));
		// API分组
		controllerBean.put("groupName", action.getAttribute("groupName"));
	}

	/**
	 * 
	 * @Title: getRequestParams 
	 * @Description: 获取每个接口的入参参数 
	 * @param paramsNodes 接口入参节点集合 
	 * @param APIParamsBeans 整理后的接口入参节点集合
	 * @return: void @throws
	 */
	private void getRequestParams(NodeList paramsNodes, List<Map<String, Object>> APIParamsBeans) {
		Map<String, Object> actionBean;
		// property节点信息
		Element property;
		for (int j = 0; j < paramsNodes.getLength(); j++) {
			actionBean = new HashMap<>();
			property = (Element) paramsNodes.item(j);
			actionBean.put("number", (j + 1));
			// 前端传递的key
			actionBean.put("id", property.getAttribute("id"));
			// 后端接收的key
			actionBean.put("name", property.getAttribute("name"));
			// 参数要求：require、num等
			actionBean.put("ref",
					ToolUtil.isBlank(property.getAttribute("ref")) ? API_PLACEHOLDER : property.getAttribute("ref"));
			// 参数描述
			actionBean.put("var",
					ToolUtil.isBlank(property.getAttribute("var")) ? API_PLACEHOLDER : property.getAttribute("var"));
			// 示例默认值
			actionBean.put("exampleDefault", ToolUtil.isBlank(property.getAttribute("exampleDefault")) ? API_PLACEHOLDER
					: property.getAttribute("exampleDefault"));
			// 默认值
			actionBean.put("default", ToolUtil.isBlank(property.getAttribute("default")) ? EMPTY : property.getAttribute("default"));
			APIParamsBeans.add(actionBean);
		}
	}
	
}
