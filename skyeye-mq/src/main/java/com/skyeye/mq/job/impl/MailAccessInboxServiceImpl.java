/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.util.ShowMail;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MQUserEmailDao;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import com.skyeye.mq.job.JobMateService;
import com.skyeye.service.JobMateMationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
    * @ClassName: MailAccessInboxServiceImpl
    * @Description: 收件箱邮件获取
    * @author 卫志强
    * @date 2020年8月22日
    *
 */
@Service("mailAccessInboxService")
public class MailAccessInboxServiceImpl implements JobMateService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailAccessInboxServiceImpl.class);
	
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
			LOGGER.info("get mail start");
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, "");
			LOGGER.info("jobId id is {}", jobId);
			try{
				//获取服务器信息
				Map<String, Object> emailServer = systemFoundationSettingsService.getSystemFoundationSettings();
				
				String storeType = emailServer.get("emailType").toString();//邮箱类型
				String host = emailServer.get("emailReceiptServer").toString();//邮箱收件服务器
				String username = map.get("userAddress").toString();//登录邮箱账号
				String password = map.get("userPassword").toString();//密码
				String basePath = tPath + "upload/emailenclosure/";//附件存储路径
				
				// 创建一个数值格式化对象   
				NumberFormat numberFormat = NumberFormat.getInstance();
				// 设置精确到小数点后0位   
				numberFormat.setMaximumFractionDigits(0);
				
				Folder folder = ToolUtil.getFolderByServer(host, username, password, storeType, "INBOX");
				if (!folder.exists()) {//如果文件夹不存在，则创建
	                folder.create(Folder.HOLDS_MESSAGES);
	            }
				folder.open(Folder.READ_ONLY);
				Message[] message = folder.getMessages();//获取邮件信息
				ShowMail re = null;
				//邮件集合
				List<Map<String, Object>> beans = new ArrayList<>();
				Map<String, Object> bean = null;
				//附件集合
				List<Map<String, Object>> enclosureBeans = new ArrayList<>();
				//获取当前邮箱已有的邮件
				List<Map<String, Object>> emailHasMail = mqUserEmailDao.queryEmailListByEmailAddress(map);
				
				//创建目录
				ToolUtil.createFolder(basePath);
				
				//遍历邮件数据
				for (int i = 0; i < message.length; i++) {
					if(!message[i].getFolder().isOpen()) //判断是否open  
		                message[i].getFolder().open(Folder.READ_ONLY); //如果close，就重新open
					re = new ShowMail((MimeMessage) message[i]);
					try{
						LOGGER.info("get message mation is {}", JSONUtil.toJsonStr(re));
					} catch (Exception e) {
						continue;
					}
					// 如果该邮件在本地数据库中不存在并且messageId不为空 ||收件人或者抄送人或者暗送人是当前账号
					if(!ToolUtil.judgeInListByMessage(emailHasMail, re.getMessageId()) 
							&& !ToolUtil.isBlank(re.getMessageId())
							&& (re.getMailAddress("to").indexOf(username) > -1 || re.getMailAddress("cc").indexOf(username) > -1 || re.getMailAddress("bcc").indexOf(username) > -1)){
						bean = ToolUtil.getEmailMationByUtil(re, message[i]);
						String rowId = ToolUtil.getSurFaceId();
						bean.put("id", rowId);//id
						bean.put("emailState", "1");//邮件状态 0.草稿  1.正常  2.已删除
						re.setAttachPath(basePath);//设置附件保存基础路径
						enclosureBeans.addAll(re.saveAttachMent((Part) message[i], rowId));//保存附件
						beans.add(bean);
					}
					if(beans.size() >= 20){//每20条数据保存一次
						mqUserEmailDao.insertEmailListToServer(beans);
						if(!enclosureBeans.isEmpty())
							mqUserEmailDao.insertEmailEnclosureListToServer(enclosureBeans);
						beans.clear();
						enclosureBeans.clear();
						//重置当前邮箱已有的邮件
						emailHasMail = mqUserEmailDao.queryEmailListByEmailAddress(map);
					}
				}
				if(!beans.isEmpty())
					mqUserEmailDao.insertEmailListToServer(beans);
				if(!enclosureBeans.isEmpty())
					mqUserEmailDao.insertEmailEnclosureListToServer(enclosureBeans);
			} catch (Exception e) {
				LOGGER.warn("Inbox mail acquisition failed, reason is {}.", e);
				// 任务失败
				jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
				return;
			} finally{
			}
			LOGGER.info("get mail end");
			// 任务完成
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
		} catch (Exception e) {
			LOGGER.warn("Inbox mail acquisition failed, reason is {}.", e);
			// 任务失败
			jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
		}
	}
	
}
