package com.skyeye.start.thread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.skyeye.common.constans.Constants;

/**
 * 
     * @ClassName: TokenThread
     * @Description: 通过线程去读取系统中的请求
     * @author 卫志强
     * @date 2018年6月8日
     *
 */
public class TokenThread implements Runnable{
	
	private String REQUEST_URL;
	
	private static Logger log = LoggerFactory.getLogger(TokenThread.class);
	
	public TokenThread(String REQUEST_URL) {
		this.REQUEST_URL = REQUEST_URL;
	}

	@Override
	public void run() {
		try {
			log.info("线程读取配置信息路径开始");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			String filePath = REQUEST_URL;
			Document doc = db.parse(new File(filePath));
			Element controller = doc.getDocumentElement();
			NodeList controllerNodes = controller.getElementsByTagName("url");
			Constants.REQUEST_MAPPING = new HashMap<>();
			List<Map<String, Object>> controllerBeans;
			Map<String, Object> controllerBean;
			Map<String,Object> actionBean;
			for (int i = 0; i < controllerNodes.getLength(); i++) {
				controllerBeans = new ArrayList<Map<String,Object>>();
				controllerBean = new HashMap<>();
				Element action = (Element)controllerNodes.item(i);
				NodeList actionNodes = action.getElementsByTagName("property");
				for(int j = 0;j < actionNodes.getLength(); j++){
					actionBean = new HashMap<String, Object>();
					Element property = (Element)actionNodes.item(j);
					actionBean.put("id", property.getAttribute("id"));
					actionBean.put("name", property.getAttribute("name"));
					actionBean.put("ref", property.getAttribute("ref"));
					controllerBeans.add(actionBean);
				}
				controllerBean.put("list", controllerBeans);
				controllerBean.put("path", action.getAttribute("path"));
				System.out.println(("请求信息："+ i + JSONObject.fromObject(controllerBean).toString()));
				Constants.REQUEST_MAPPING.put(action.getAttribute("id").toString(), controllerBean);
			}
			System.out.println("请求路径文件读取完毕");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
