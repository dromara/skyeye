package com.skyeye.start.thread;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
     * @ClassName: InitServlet
     * @Description: 读取配置文件
     * @author 卫志强
     * @date 2018年6月8日
     *
 */
public class InitServlet extends HttpServlet {
	
	private String REQUEST_URL;

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(InitServlet.class);

	public void init(ServletConfig config) throws ServletException {
		String basePath = InitServlet.class.getClassLoader().getResource("./").getPath();
		REQUEST_URL = config.getInitParameter("REQUEST_URL");
		REQUEST_URL = basePath + REQUEST_URL;
		//启动线程读取配置文件
		new Thread(new TokenThread(REQUEST_URL)).start();
		log.info("启动线程读取配置文件成功");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}
	
	

}
