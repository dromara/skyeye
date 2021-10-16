/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.start.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

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
	
	private static Logger log = LoggerFactory.getLogger(InitServlet.class);
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		BlockingQueue<Runnable> services = new ArrayBlockingQueue<>(1);
		services.add(new TokenThread());
		
		// 启动线程读取配置文件
		int size = services.size();
		// 创建线程池对象,,包含1个线程对象
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-read-mapping-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
        for(final Runnable v : services) {
        	executorService.execute(v);
        }
		log.info("启动线程读取配置文件成功");
	}

}
