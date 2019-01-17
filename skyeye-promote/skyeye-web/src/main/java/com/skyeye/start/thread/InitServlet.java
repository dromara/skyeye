package com.skyeye.start.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.skyeye.common.util.ToolUtil;

/**
 * 
     * @ClassName: InitServlet
     * @Description: 读取配置文件
     * @author 卫志强
     * @date 2018年6月8日
     *
 */
@Component
public class InitServlet implements ApplicationRunner{
	
	@Value("${java.request.url.mappimg}")
	private String REQUEST_URL;

	private static Logger log = LoggerFactory.getLogger(InitServlet.class);

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		String basePath = InitServlet.class.getClassLoader().getResource("./").getPath();
		String[] filePath = REQUEST_URL.split(",");
		REQUEST_URL = "";
		for(String str : filePath){
			if(!ToolUtil.isBlank(str)){
				REQUEST_URL = REQUEST_URL + basePath + str + ",";
			}
		}
		//启动线程读取配置文件
		new Thread(new TokenThread(REQUEST_URL)).start();
		log.info("启动线程读取配置文件成功");
	}
	
	

}
