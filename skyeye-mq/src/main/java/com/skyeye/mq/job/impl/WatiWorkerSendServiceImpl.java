/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MQUserEmailDao;
import com.skyeye.mq.job.JobMateService;
import com.skyeye.service.JobMateMationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: WatiWorkerSendServiceImpl
 * @Description: 派工通知
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:56
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service("watiWorkerSendService")
public class WatiWorkerSendServiceImpl implements JobMateService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WatiWorkerSendServiceImpl.class);

	@Autowired
	private MQUserEmailDao mqUserEmailDao;
	
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
			//工单id
			String serviceId = map.get("serviceId").toString();
			//获取工单接收人和协助人id
			Map<String, Object> mation = mqUserEmailDao.queryServiceMationBySericeId(serviceId);
			//如果工单信息不为空
			if(mation != null){
				//调用消息系统添加通知
				List<Map<String, Object>> notices = new ArrayList<>();
				Map<String, Object> notice = null;
				String content;
				//1.接收人通知
				if(!ToolUtil.isBlank(mation.get("userId").toString())){
					//1.1内部消息
					content = Constants.getNoticeServiceUserContent(mation.get("orderNum").toString(), mation.get("userName").toString());
					notice = new HashMap<>();
					notice.put("id", ToolUtil.getSurFaceId());
					notice.put("title", "工单派工提醒");
					notice.put("noticeDesc", "您有一条新的派工信息，请及时阅读。");
					notice.put("content", content);
					notice.put("state", "1");//未读消息
					notice.put("userId", mation.get("userId").toString());
					notice.put("type", "3");//消息类型
					notice.put("createId", "0dc9dd4cd4d446ae9455215fe753c44e");//默认系统管理员
					notice.put("createTime", DateUtil.getTimeAndToString());
					notices.add(notice);
					//1.2发送邮件
					String email = mation.get("email").toString();
					if(ToolUtil.isEmail(email) && !ToolUtil.isBlank(email)){
						new MailUtil().send(email, "工单派工提醒", content);
					}
				}
				//2.协助人通知
				if(!ToolUtil.isBlank(mation.get("cooperationUserId").toString())){
					//获取协助人
					List<Map<String, Object>> cooperationUser = mqUserEmailDao.queryCooperationUserNameById(serviceId);
					
					for(Map<String, Object> user: cooperationUser){
						//2.1内部消息
						content = Constants.getNoticeCooperationUserContent(mation.get("orderNum").toString(), user.get("name").toString());
						notice = new HashMap<>();
						notice.put("id", ToolUtil.getSurFaceId());
						notice.put("title", "工单派工提醒");
						notice.put("noticeDesc", "您有一条新的派工信息，请及时阅读。");
						notice.put("content", content);
						notice.put("state", "1");//未读消息
						notice.put("userId", user.get("id").toString());
						notice.put("type", "3");//消息类型
						notice.put("createId", "0dc9dd4cd4d446ae9455215fe753c44e");//默认系统管理员
						notice.put("createTime", DateUtil.getTimeAndToString());
						notices.add(notice);
						//2.2发送邮件
						String email = user.get("email").toString();
						if(ToolUtil.isEmail(email) && !ToolUtil.isBlank(email)){
							new MailUtil().send(email, "工单派工提醒", content);
						}
					}
				}
				if(!notices.isEmpty())
					mqUserEmailDao.insertNoticeListMation(notices);
			}
			// 任务完成
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
		} catch (Exception e) {
			LOGGER.warn("Dispatch notice failed, reason is {}.", e);
			// 任务失败
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
		}
	}
	
}
