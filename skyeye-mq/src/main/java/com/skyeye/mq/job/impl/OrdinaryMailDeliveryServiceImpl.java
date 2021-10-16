/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.mq.job.JobMateService;
import com.skyeye.service.JobMateMationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 
    * @ClassName: OrdinaryMailDeliveryServiceImpl
    * @Description: 普通邮件发送任务
    * @author 卫志强
    * @date 2020年8月22日
    *
 */
@Service("ordinaryMailDeliveryService")
public class OrdinaryMailDeliveryServiceImpl implements JobMateService{
	
	private static Logger LOGGER = LoggerFactory.getLogger(OrdinaryMailDeliveryServiceImpl.class);
	
	@Autowired
	private JobMateMationService jobMateMationService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void call(String data) throws Exception {
		Map<String, Object> map = JSONUtil.toBean(data, null);
		String jobId = map.get("jobMateId").toString();
		try {
			// 任务开始
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, "");
			String email  = map.get("email").toString();
			String content = map.get("content").toString();
			LOGGER.info("resever is {}, content is {}", email, content);
			new MailUtil().send(email, map.get("title").toString(), content);
			// 任务完成
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
		} catch (Exception e) {
			LOGGER.warn("Normal mail sending task failed, reason is {}.", e);
			// 任务失败
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
		}
	}

}
