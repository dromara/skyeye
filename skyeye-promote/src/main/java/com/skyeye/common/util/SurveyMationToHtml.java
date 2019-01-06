package com.skyeye.common.util;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * 
     * @ClassName: SurveyMationToHtml
     * @Description: 问卷内容转html
     * @author 卫志强
     * @date 2019年1月6日
     *
 */
public class SurveyMationToHtml {
	
	/**
	 * 
	     * @Title: postJspToHtml
	     * @Description: 问卷内容转html
	     * @param @param postUrl 
	     * @param @param filePath 文件输出地址
	     * @param @param fileName 文件名
	     * @param @param request
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void postJspToHtml(String postUrl, String filePath, String fileName, HttpServletRequest request) throws Exception {
		String reqTarget = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath();
		reqTarget = reqTarget + "/toHtml";
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", postUrl);
		map.put("filePath", filePath);
		map.put("fileName", fileName);
		Connection connection = Jsoup.connect(reqTarget);
		connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		connection.data(map);
		connection.timeout(8000).get();
	}

}
