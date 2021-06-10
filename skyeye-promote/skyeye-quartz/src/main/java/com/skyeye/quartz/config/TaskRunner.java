/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */

package com.skyeye.quartz.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.quartz.entity.SysQuartz;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: TaskRunner
 * @Description: 任务启动配置类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/10 20:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public class TaskRunner implements ApplicationRunner, Ordered {

    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Autowired
    private QuartzService quartzService;

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		log.info("程序启动");
		List<SysQuartz> sysQuartzList = sysQuartzDao.selectAll();
		for (SysQuartz qz : sysQuartzList) {
			log.debug("重启任务:" + qz);
			// 检查日期，如果超了，就重置为当前日期+2分钟
			if (!quartzService.checkCron(qz.getCron())) {
				Date date = ToolUtil.getAfDate(new Date(), 2, "m");
				qz.setCron(ToolUtil.getTime(date, "s") + " "
						+ ToolUtil.getTime(date, "m") + " "
						+ ToolUtil.getTime(date, "H") + " "
						+ ToolUtil.getTime(date, "d") + " "
						+ ToolUtil.getTime(date, "M") + " ? "
						+ ToolUtil.getTime(date, "y"));
				log.debug("重置任务时间:" + qz);
			}
			quartzService.addJob(qz);
		}
	}
	
    @Override
    public int getOrder() {
        return 0;
    }
}
