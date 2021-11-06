/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MQUserEmailDao;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.mq.job.JobMateService;
import com.skyeye.service.JobMateMationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
    * @ClassName: MailDraftsSendServiceImpl
    * @Description: 草稿箱邮件发送
    * @author 卫志强
    * @date 2020年8月22日
    *
 */
@Service("mailDraftsSendService")
public class MailDraftsSendServiceImpl implements JobMateService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailDraftsSendServiceImpl.class);
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	@Autowired
	private MQUserEmailDao mqUserEmailDao;

	@Autowired
	private JobMateMationService jobMateMationService;

	@Autowired
	private SystemFoundationSettingsService systemFoundationSettingsService;

	@SuppressWarnings("unchecked")
	@Override
	public void call(String data) throws Exception {
		Map<String, Object> map = JSONUtil.toBean(data, null);
		String jobId = map.get("jobMateId").toString();
		try {
			// 任务开始
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, "");
			//获取服务器信息
			Map<String, Object> emailServer = systemFoundationSettingsService.getSystemFoundationSettings();
	        String title = map.get("title").toString();//标题
	        String content = map.get("content").toString();//邮件内容
	        String toPeople = map.get("toPeople").toString();//收件人
	        String toCc = map.get("toCc").toString();//抄送人
	        String toBCc = map.get("toBcc").toString();//暗送人
	        String username = map.get("userAddress").toString();//登录邮箱账号
	        String password = map.get("userPassword").toString();//密码
	        String emailEnclosure = map.get("emailEnclosure").toString();
	        String emailId = map.get("emailId").toString();
			List<Map<String, Object>> emailEnclosureList = JSONUtil.toList(emailEnclosure, null);
	        List<Map<String, Object>> beans = new ArrayList<>();
	        Map<String, Object> bean = null;
	        for(int i = 0; i < emailEnclosureList.size(); i++){
				Map<String, Object> j = emailEnclosureList.get(i);
	            bean = new HashMap<>();
	            bean.put("fileName", j.get("fileName"));
	            bean.put("filePath", j.get("filePath"));
	            beans.add(bean);
	        }
	        //获取邮件的messageid
	        Map<String, Object> message = mqUserEmailDao.queryEmailMessageIdByEmailId(map);
	        // 删除之前的草稿件
			if(!ToolUtil.isBlank(message.get("messageId").toString())){
				new MailUtil(username, password, emailServer.get("emailReceiptServer").toString())
						.deleteEmail(username, title, message.get("messageId").toString());
			}
	        //发送邮件
	        String messageId = new MailUtil(username, password, emailServer.get("emailSendServer").toString())
	            .send(toPeople, toCc, toBCc, title, content, tPath.replace("images", ""), beans);
	        if(!ToolUtil.isBlank(messageId)){
	            Map<String, Object> emailEditMessageId = new HashMap<>();
	            emailEditMessageId.put("id", emailId);
	            emailEditMessageId.put("messageId", messageId);
	            mqUserEmailDao.editEmailMessageIdByEmailId(emailEditMessageId);
	        }
	        // 任务完成
 			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
 		} catch (Exception e) {
 			LOGGER.warn("Draft email sending failed, reason is {}.", e);
 			// 任务失败
 			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
 		}
	}

}
