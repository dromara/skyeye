/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.quartz.config;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.eve.entity.quartz.SysQuartz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskRunner implements ApplicationRunner, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);
	
    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Autowired
    private QuartzService quartzService;
    
    @Value("${quartz.ip}")  
    private String quartzIp;
	
	@Value("${quartz.port}")  
    private String quartzPort;

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		LOGGER.info("定时任务程序启动");
		Map<String, Object> map = new HashMap<>();
		map.put("quartzIp", quartzIp);
		map.put("quartzPort", quartzPort);
		map.put("quartzType", "1");
		List<SysQuartz> sysQuartzList = sysQuartzDao.selectAll(map);
		for (SysQuartz qz : sysQuartzList) {
			LOGGER.info("resrt task old time is {}.", JSONUtil.toJsonStr(qz));
			// 检查日期，如果超了，就重置为当前日期+2分钟
			if (!quartzService.checkCron(qz.getCron())) {
				Date date = DateUtil.getAfDate(new Date(), 2, "m");
				qz.setCron(DateUtil.getTime(date, "s") + " "
						+ DateUtil.getTime(date, "m") + " "
						+ DateUtil.getTime(date, "H") + " "
						+ DateUtil.getTime(date, "d") + " "
						+ DateUtil.getTime(date, "M") + " ? "
						+ DateUtil.getTime(date, "y"));
				LOGGER.info("resrt task new time is {}.", JSONUtil.toJsonStr(qz));
			}
			quartzService.startJob(qz);
		}
	}
	
    @Override
    public int getOrder() {
        return 0;
    }
}
