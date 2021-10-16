/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.quartz.consumer.impl;

import com.skyeye.eve.dao.SysQuartzDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.quartz.consumer.TaskMateService;
import com.skyeye.eve.entity.quartz.SysQuartz;

/**
 * 公告在定时任务中
 */
@Service("quartzNoticeMationService")
public class QuartzNoticeMationService implements TaskMateService {

    @Autowired
    private SysQuartzDao sysQuartzDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzNoticeMationService.class);

    @Override
    public void call(SysQuartz sysQuartz) throws Exception {
        LOGGER.info("start quartz notice, quartz id is: {}", sysQuartz.getId());
        String noticeId = sysQuartz.getName();
        // 上线状态
        String state = "2";
        sysQuartzDao.editNoticeStateById(noticeId, state);
        LOGGER.info("end quartz notice, quartz id is: {}", sysQuartz.getId());
    }

}
