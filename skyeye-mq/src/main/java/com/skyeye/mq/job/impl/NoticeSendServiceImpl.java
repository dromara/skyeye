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

import java.util.List;
import java.util.Map;

/**
 * 
    * @ClassName: NoticeSendServiceImpl
    * @Description: 消息通知
    * @author 卫志强
    * @date 2020年8月22日
    *
 */
@Service("noticeSendService")
public class NoticeSendServiceImpl implements JobMateService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeSendServiceImpl.class);
	
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
			String email = map.get("email").toString();
			List<Map<String, Object>> beans = JSONUtil.toList(email, null);	//把字符串转成json数组
			for(int i = 0; i < beans.size(); i++){
				Map<String, Object> bean = beans.get(i);
				if(bean != null && !bean.isEmpty()){	//邮件账号不为空
					new MailUtil().send(bean.get("userEmail").toString(), map.get("title").toString(), map.get("content").toString());	//发送邮件
				}
			}
			// 任务完成
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
		} catch (Exception e) {
			LOGGER.warn("notification failed, reason is {}.", e);
			// 任务失败
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
		}
	}

}
