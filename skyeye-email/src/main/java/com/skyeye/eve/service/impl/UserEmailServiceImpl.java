/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.UserEmailDao;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import com.skyeye.eve.service.UserEmailService;
import com.skyeye.service.JobMateMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: UserEmailServiceImpl
 * @Description: 邮箱模块
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 21:13
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class UserEmailServiceImpl implements UserEmailService{
	
	@Autowired
	private UserEmailDao userEmailDao;
	
	@Value("${IMAGES_PATH}")
    private String tPath;
	
	@Autowired
	private JobMateMationService jobMateMationService;

	@Autowired
	private SystemFoundationSettingsService systemFoundationSettingsService;

	/**
	 * 
	     * @Title: queryEmailListByUserId
	     * @Description: 根据用户获取该用户绑定的邮箱信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryEmailListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> emailList = userEmailDao.queryEmailListByUserId(map);
		outputObject.setBeans(emailList);
		outputObject.settotal(emailList.size());
	}

	/**
	 * 
	     * @Title: insertEmailListByUserId
	     * @Description: 用户新增邮箱
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertEmailListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		// 获取服务器信息
		Map<String, Object> emailServer = systemFoundationSettingsService.getSystemFoundationSettings();
		boolean login = new MailUtil(map.get("emailAddress").toString(), map.get("emailPassword").toString(), 
				emailServer.get("emailSendServer").toString()).authLogin();
		if(login){
			Map<String, Object> userEmail = userEmailDao.queryEmailISInByEmailAddressAndUserId(map);
			if(userEmail != null && !userEmail.isEmpty()){
				outputObject.setreturnMessage("该邮箱已存在，无法重复添加");
			}else{
				//判断用户是否拥有默认的邮箱
				Map<String, Object> emailCheck = userEmailDao.queryEmailCheckByUserId(map);
				if(emailCheck != null && !emailCheck.isEmpty()){
					map.put("emailCheck", '2');
				}else{
					map.put("emailCheck", '1');
				}
				map.put("id", ToolUtil.getSurFaceId());
				map.put("createTime", DateUtil.getTimeAndToString());
				userEmailDao.insertEmailListByUserId(map);
				outputObject.setBean(map);
			}
		}else{
			outputObject.setreturnMessage("邮箱登录失败，请检查账号密码是否正确。");
		}
	}
	
	/**
	 * 
	     * @Title: insertEmailListFromServiceByUserId
	     * @Description: 从服务器上获取收件箱里的邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		Map<String, Object> email = userEmailDao.queryEmailMationById(map);
		if(email != null && !email.isEmpty()){
			//消息队列参数对象
			Map<String, Object> emailNotice = new HashMap<>();
			emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_ACCESS_INBOX.getJobType());//消息队列任务类型
			emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
			emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
			emailNotice.put("userId", userId);//创建人id
			jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
		}else{
			outputObject.setreturnMessage("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
		}
	}
	
	/**
	 * 
	     * @Title: queryInboxEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取收件箱内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryInboxEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = userEmailDao.queryInboxEmailListByEmailId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: queryEmailMationByEmailId
	     * @Description: 获取邮件内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> email = userEmailDao.queryEmailMationByEmailId(map);
		if(email != null && !email.isEmpty()){
			if("1".equals(email.get("isContainAttach").toString())){//包含附件
				List<Map<String, Object>> enclosureBeans = userEmailDao.queryEnclosureBeansMationByEmailId(map);
				email.put("enclosureBeans", enclosureBeans);
			}
		}
		outputObject.setBean(email);
	}
	
	/**
	 * 
	     * @Title: insertSendedEmailListFromServiceByUserId
	     * @Description: 从服务器上获取已发送邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSendedEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		Map<String, Object> email = userEmailDao.queryEmailMationById(map);
		if(email != null && !email.isEmpty()){
			//消息队列参数对象
			Map<String, Object> emailNotice = new HashMap<>();
			emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_ACCESS_SENDED.getJobType());//消息队列任务类型
			emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
			emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
			emailNotice.put("userId", userId);//创建人id
			jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
		}else{
			outputObject.setreturnMessage("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
		}
	}

	/**
	 * 
	     * @Title: querySendedEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取已发送邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySendedEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = userEmailDao.querySendedEmailListByEmailId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertDelsteEmailListFromServiceByUserId
	     * @Description: 从服务器上获取已删除邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertDelsteEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		Map<String, Object> email = userEmailDao.queryEmailMationById(map);
		if(email != null && !email.isEmpty()){
			//消息队列参数对象
			Map<String, Object> emailNotice = new HashMap<>();
			emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_ACCESS_DELETE.getJobType());//消息队列任务类型
			emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
			emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
			emailNotice.put("userId", userId);//创建人id
			jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
		}else{
			outputObject.setreturnMessage("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
		}
	}

	/**
	 * 
	     * @Title: queryDeleteEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取已删除邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDeleteEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = userEmailDao.queryDeleteEmailListByEmailId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertDraftsEmailListFromServiceByUserId
	     * @Description: 从服务器上获取草稿箱邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertDraftsEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		map.put("userId", userId);
		Map<String, Object> email = userEmailDao.queryEmailMationById(map);
		if(email != null && !email.isEmpty()){
			//消息队列参数对象
			Map<String, Object> emailNotice = new HashMap<>();
			emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_ACCESS_DRAFTS.getJobType());//消息队列任务类型
			emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
			emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
			emailNotice.put("userId", userId);//创建人id
			jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
		}else{
			outputObject.setreturnMessage("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
		}
	}

	/**
	 * 
	     * @Title: queryDraftsEmailListByEmailId
	     * @Description: 根据绑定邮箱id获取草稿箱邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDraftsEmailListByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = userEmailDao.queryDraftsEmailListByEmailId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertToSendEmailMationByUserId
	     * @Description: 发送邮件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//接收人邮箱校验
		String[] toPeopleEmails = map.get("toPeople").toString().split(",");
		String toPeople = "";
		for(int i = 0; i < toPeopleEmails.length; i++){
			if(ToolUtil.isEmail(toPeopleEmails[i])){//如果是邮箱
				if(i == 0)
					toPeople = toPeopleEmails[i];
				else
					toPeople += "," + toPeopleEmails[i];
			}
		}
		if(ToolUtil.isBlank(toPeople)){
			outputObject.setreturnMessage("请选择收件人");
			return;
		}
		
		//抄送人邮箱校验
		String[] toCcEmails = map.get("toCc").toString().split(",");
		String toCc = "";
		for(int i = 0; i < toCcEmails.length; i++){
			if(ToolUtil.isEmail(toCcEmails[i])){//如果是邮箱
				if(i == 0)
					toCc = toCcEmails[i];
				else
					toCc += "," + toCcEmails[i];
			}
		}
		//暗送人邮箱校验
		String[] toBccEmails = map.get("toBcc").toString().split(",");
		String toBcc = "";
		for(int i = 0; i < toBccEmails.length; i++){
			if(ToolUtil.isEmail(toBccEmails[i])){//如果是邮箱
				if(i == 0)
					toBcc = toBccEmails[i];
				else
					toBcc += "," + toBccEmails[i];
			}
		}
		Map<String, Object> user = inputObject.getLogParams();
        String id = ToolUtil.getSurFaceId();//邮件id
        String userId = user.get("id").toString();
        map.put("userId", userId);
        //附件处理
        List<Map<String, Object>> emailEnclosureList = userEmailDao.queryMyEnclusureListByUserIdAndIds(map);
        for(Map<String, Object> bean : emailEnclosureList){
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
        }
        //获取发件箱信息
        Map<String, Object> email = userEmailDao.queryEmailMationById(map);
        //插入邮件附件
        if(!emailEnclosureList.isEmpty())
            userEmailDao.insertEmailEnclosureListToServer(emailEnclosureList);
        //邮件处理
        Map<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("id", id);
        sendEmail.put("title", map.get("typeName"));
        sendEmail.put("sendDate", DateUtil.getTimeAndToString());
        sendEmail.put("replaySign", 1);
        sendEmail.put("isNew", 2);
        //是否包含附件  1.是  2.否
        if(emailEnclosureList != null && !emailEnclosureList.isEmpty()){
            sendEmail.put("isContainAttach", 1);
        }else{
            sendEmail.put("isContainAttach", 2);
        }
        sendEmail.put("fromPeople", email.get("emailAddress"));
        sendEmail.put("toPeople", toPeople);
        sendEmail.put("toCc", toCc);
        sendEmail.put("toBcc", toBcc);
        sendEmail.put("content", map.get("content"));//邮件内容
        sendEmail.put("createTime", DateUtil.getTimeAndToString());
        sendEmail.put("emailState", 1);//邮件状态 0.草稿  1.正常  2.已删除
        userEmailDao.insertToSendEmailMationByUserId(sendEmail);
        
		//消息队列参数对象
		Map<String, Object> emailNotice = new HashMap<>();
		emailNotice.put("type", MqConstants.JobMateMationJobType.COMPLEX_MAIL_DELIVERY.getJobType());//消息队列任务类型
		emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
		emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
		emailNotice.put("title", map.get("typeName"));//邮件标题
		emailNotice.put("content", map.get("content"));//邮件内容
		emailNotice.put("toPeople", toPeople);//邮件接收人
		emailNotice.put("toCc", toCc);//邮件抄送人
		emailNotice.put("emailId", id);//邮件id
		emailNotice.put("toBcc", toBcc);//邮件暗送人
		emailNotice.put("emailEnclosure", JSONUtil.toJsonStr(emailEnclosureList));//邮件附件
		jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
	}
	
	/**
     * 
         * @Title: insertToDraftsEmailMationByUserId
         * @Description: 保存邮件为草稿
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        //接收人邮箱校验
        String[] toPeopleEmails = map.get("toPeople").toString().split(",");
        String toPeople = "";
        for(int i = 0; i < toPeopleEmails.length; i++){
            if(ToolUtil.isEmail(toPeopleEmails[i])){//如果是邮箱
                if(i == 0)
                    toPeople = toPeopleEmails[i];
                else
                    toPeople += "," + toPeopleEmails[i];
            }
        }
        if(ToolUtil.isBlank(toPeople)){
            outputObject.setreturnMessage("请选择收件人");
            return;
        }
		
        //抄送人邮箱校验
        String[] toCcEmails = map.get("toCc").toString().split(",");
        String toCc = "";
        for(int i = 0; i < toCcEmails.length; i++){
            if(ToolUtil.isEmail(toCcEmails[i])){//如果是邮箱
                if(i == 0)
                    toCc = toCcEmails[i];
                else
                    toCc += "," + toCcEmails[i];
            }
        }
        //暗送人邮箱校验
        String[] toBccEmails = map.get("toBcc").toString().split(",");
        String toBcc = "";
        for(int i = 0; i < toBccEmails.length; i++){
            if(ToolUtil.isEmail(toBccEmails[i])){//如果是邮箱
                if(i == 0)
                    toBcc = toBccEmails[i];
                else
                    toBcc += "," + toBccEmails[i];
            }
        }
        Map<String, Object> user = inputObject.getLogParams();
        String id = ToolUtil.getSurFaceId();//邮件id
        String userId = user.get("id").toString();
        map.put("userId", userId);
        //附件处理
        List<Map<String, Object>> emailEnclosureList = userEmailDao.queryMyEnclusureListByUserIdAndIds(map);
        for(Map<String, Object> bean : emailEnclosureList){
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
        }
        //获取发件箱信息
        Map<String, Object> email = userEmailDao.queryEmailMationById(map);
        //插入邮件附件
        if(!emailEnclosureList.isEmpty())
            userEmailDao.insertEmailEnclosureListToServer(emailEnclosureList);
        //邮件处理
        Map<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("id", id);
        sendEmail.put("title", map.get("typeName"));
        sendEmail.put("sendDate", DateUtil.getTimeAndToString());
        sendEmail.put("replaySign", 1);
        sendEmail.put("isNew", 2);
        //是否包含附件  1.是  2.否
        if(emailEnclosureList != null && !emailEnclosureList.isEmpty()){
            sendEmail.put("isContainAttach", 1);
        }else{
            sendEmail.put("isContainAttach", 2);
        }
        sendEmail.put("fromPeople", email.get("emailAddress"));
        sendEmail.put("toPeople", toPeople);
        sendEmail.put("toCc", toCc);
        sendEmail.put("toBcc", toBcc);
        sendEmail.put("content", map.get("content"));//邮件内容
        sendEmail.put("createTime", DateUtil.getTimeAndToString());
        sendEmail.put("emailState", 0);//邮件状态 0.草稿  1.正常  2.已删除
        userEmailDao.insertToSendEmailMationByUserId(sendEmail);
		
        //消息队列参数对象
        Map<String, Object> emailNotice = new HashMap<>();
        emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_DRAFTS_SAVE.getJobType());//消息队列任务类型
        emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
        emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
        emailNotice.put("title", map.get("typeName"));//邮件标题
        emailNotice.put("content", map.get("content"));//邮件内容
        emailNotice.put("toPeople", toPeople);//邮件接收人
        emailNotice.put("toCc", toCc);//邮件抄送人
        emailNotice.put("emailId", id);//邮件id
        emailNotice.put("toBcc", toBcc);//邮件暗送人
        emailNotice.put("emailEnclosure", JSONUtil.toJsonStr(emailEnclosureList));//邮件附件
        jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
    }
    
    /**
     * 
         * @Title: queryDraftsEmailMationToEditByUserId
         * @Description: 编辑草稿箱内容展示时回显使用
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryDraftsEmailMationToEditByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = userEmailDao.queryDraftsEmailMationToEditByUserId(map);//获取邮件基础信息
        if(bean != null && !bean.isEmpty()){
            List<Map<String, Object>> toPeople = userEmailDao.queryDraftsEmailToPeopleMationToEditByUserId(bean);//获取收件人列表
            List<Map<String, Object>> toCc = userEmailDao.queryDraftsEmailToCcMationToEditByUserId(bean);//获取抄送人列表
            List<Map<String, Object>> toBcc = userEmailDao.queryDraftsEmailToBccMationToEditByUserId(bean);//获取暗送人列表
            List<Map<String, Object>> emailEnclosure = userEmailDao.queryDraftsEmailEnclosureMationToEditByUserId(bean);//获取附件人列表
            bean.put("toPeopleList", toPeople);
            bean.put("toCcList", toCc);
            bean.put("toBccList", toBcc);
            bean.put("emailEnclosureList", emailEnclosure);
            outputObject.setBean(bean);
        }else{
            outputObject.setreturnMessage("该草稿已不存在或者已经被发送。");
        }
    }
	
    /**
     * 
         * @Title: editToDraftsEmailMationByUserId
         * @Description: 草稿邮件修改
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        //接收人邮箱校验
        String[] toPeopleEmails = map.get("toPeople").toString().split(",");
        String toPeople = "";
        for(int i = 0; i < toPeopleEmails.length; i++){
            if(ToolUtil.isEmail(toPeopleEmails[i])){//如果是邮箱
                if(i == 0)
                    toPeople = toPeopleEmails[i];
                else
                    toPeople += "," + toPeopleEmails[i];
            }
        }
        if(ToolUtil.isBlank(toPeople)){
            outputObject.setreturnMessage("请选择收件人");
            return;
        }
		
        //抄送人邮箱校验
        String[] toCcEmails = map.get("toCc").toString().split(",");
        String toCc = "";
        for(int i = 0; i < toCcEmails.length; i++){
            if(ToolUtil.isEmail(toCcEmails[i])){//如果是邮箱
                if(i == 0)
                    toCc = toCcEmails[i];
                else
                    toCc += "," + toCcEmails[i];
            }
        }
        //暗送人邮箱校验
        String[] toBccEmails = map.get("toBcc").toString().split(",");
        String toBcc = "";
        for(int i = 0; i < toBccEmails.length; i++){
            if(ToolUtil.isEmail(toBccEmails[i])){//如果是邮箱
                if(i == 0)
                    toBcc = toBccEmails[i];
                else
                    toBcc += "," + toBccEmails[i];
            }
        }
        Map<String, Object> user = inputObject.getLogParams();
        String id = map.get("rowId").toString();//邮件id
        String userId = user.get("id").toString();
        map.put("userId", userId);
        //附件处理
        userEmailDao.deleteEmailEnclosureListByEmailId(map);//删除之前的附件
        List<Map<String, Object>> emailEnclosureList = userEmailDao.queryMyEnclusureListByUserIdAndIds(map);
        for(Map<String, Object> bean : emailEnclosureList){
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
        }
        //如果邮件是从别的服务器同步来的，那么包含的附件可能在我们的附件库上没有，这个对象负责存储那些我们的附件库上没有的附件
		List<Map<String, Object>> emailEnclosureBeans = JSONUtil.toList(map.get("emailEnclosureList").toString(), null);
        Map<String, Object> bean = null;
        File f = null;
        //文件名，文件后缀
        String fileName, fileExtName;
        for(int i = 0; i < emailEnclosureBeans.size(); i++){
			Map<String, Object> j = emailEnclosureBeans.get(i);
            fileName = j.get("name").toString();
            fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//文件后缀
            bean = new HashMap<>();
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
            bean.put("fileName", fileName);
            bean.put("filePath", j.get("fileAddress").toString());
            bean.put("fileExtName", fileExtName);
            f = new File(tPath.replace("images", "") + j.get("fileAddress").toString());
            bean.put("fileSize", f.length());
            emailEnclosureList.add(bean);
        }
        //获取发件箱信息
        Map<String, Object> email = userEmailDao.queryEmailMationById(map);
        //插入邮件附件
        if(!emailEnclosureList.isEmpty())
            userEmailDao.insertEmailEnclosureListToServer(emailEnclosureList);
        //邮件处理
        Map<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("id", id);
        sendEmail.put("title", map.get("typeName"));
        //是否包含附件  1.是  2.否
        if(emailEnclosureList != null && !emailEnclosureList.isEmpty()){
            sendEmail.put("isContainAttach", 1);
        }else{
            sendEmail.put("isContainAttach", 2);
        }
        sendEmail.put("toPeople", toPeople);
        sendEmail.put("toCc", toCc);
        sendEmail.put("toBcc", toBcc);
        sendEmail.put("content", map.get("content"));//邮件内容
        userEmailDao.editToSendEmailMationByUserId(sendEmail);
		
        //消息队列参数对象
        Map<String, Object> emailNotice = new HashMap<>();
        emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_DRAFTS_EDIT.getJobType());//消息队列任务类型
        emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
        emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
        emailNotice.put("title", map.get("typeName"));//邮件标题
        emailNotice.put("content", map.get("content"));//邮件内容
        emailNotice.put("toPeople", toPeople);//邮件接收人
        emailNotice.put("toCc", toCc);//邮件抄送人
        emailNotice.put("emailId", id);//邮件id
        emailNotice.put("toBcc", toBcc);//邮件暗送人
        emailNotice.put("emailEnclosure", JSONUtil.toJsonStr(emailEnclosureList));//邮件附件
        jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
    }
    /**
     * 
         * @Title: insertToSendEmailMationByEmailId
         * @Description: 草稿箱邮件发送
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertToSendEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        //接收人邮箱校验
        String[] toPeopleEmails = map.get("toPeople").toString().split(",");
        String toPeople = "";
        for(int i = 0; i < toPeopleEmails.length; i++){
            if(ToolUtil.isEmail(toPeopleEmails[i])){//如果是邮箱
                if(i == 0)
                    toPeople = toPeopleEmails[i];
                else
                    toPeople += "," + toPeopleEmails[i];
            }
        }
        if(ToolUtil.isBlank(toPeople)){
            outputObject.setreturnMessage("请选择收件人");
            return;
        }
		
        //抄送人邮箱校验
        String[] toCcEmails = map.get("toCc").toString().split(",");
        String toCc = "";
        for(int i = 0; i < toCcEmails.length; i++){
            if(ToolUtil.isEmail(toCcEmails[i])){//如果是邮箱
                if(i == 0)
                    toCc = toCcEmails[i];
                else
                    toCc += "," + toCcEmails[i];
            }
        }
        //暗送人邮箱校验
        String[] toBccEmails = map.get("toBcc").toString().split(",");
        String toBcc = "";
        for(int i = 0; i < toBccEmails.length; i++){
            if(ToolUtil.isEmail(toBccEmails[i])){//如果是邮箱
                if(i == 0)
                    toBcc = toBccEmails[i];
                else
                    toBcc += "," + toBccEmails[i];
            }
        }
        Map<String, Object> user = inputObject.getLogParams();
        String id = map.get("rowId").toString();//邮件id
        String userId = user.get("id").toString();
        map.put("userId", userId);
        //附件处理
        userEmailDao.deleteEmailEnclosureListByEmailId(map);//删除之前的附件
        List<Map<String, Object>> emailEnclosureList = userEmailDao.queryMyEnclusureListByUserIdAndIds(map);
        for(Map<String, Object> bean : emailEnclosureList){
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
        }
        //如果邮件是从别的服务器同步来的，那么包含的附件可能在我们的附件库上没有，这个对象负责存储那些我们的附件库上没有的附件
		List<Map<String, Object>> emailEnclosureBeans = JSONUtil.toList(map.get("emailEnclosureList").toString(), null);
        Map<String, Object> bean = null;
        File f = null;
        //文件名，文件后缀
        String fileName, fileExtName;
        for(int i = 0; i < emailEnclosureBeans.size(); i++){
			Map<String, Object> j = emailEnclosureBeans.get(i);
            fileName = j.get("name").toString();
            fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//文件后缀
            bean = new HashMap<>();
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
            bean.put("fileName", fileName);
            bean.put("filePath", j.get("fileAddress"));
            bean.put("fileExtName", fileExtName);
            f = new File(tPath.replace("images", "") + j.get("fileAddress").toString());
            bean.put("fileSize", f.length());
            emailEnclosureList.add(bean);
        }
        //获取发件箱信息
        Map<String, Object> email = userEmailDao.queryEmailMationById(map);
        //插入邮件附件
        if(!emailEnclosureList.isEmpty())
            userEmailDao.insertEmailEnclosureListToServer(emailEnclosureList);
        //邮件处理
        Map<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("id", id);
        sendEmail.put("title", map.get("typeName"));
        //是否包含附件  1.是  2.否
        if(emailEnclosureList != null && !emailEnclosureList.isEmpty()){
            sendEmail.put("isContainAttach", 1);
        }else{
            sendEmail.put("isContainAttach", 2);
        }
        sendEmail.put("toPeople", toPeople);
        sendEmail.put("toCc", toCc);
        sendEmail.put("toBcc", toBcc);
        sendEmail.put("emailState", "1");
        sendEmail.put("content", map.get("content"));//邮件内容
        userEmailDao.editToSendEmailMationByUserId(sendEmail);
		
        //消息队列参数对象
        Map<String, Object> emailNotice = new HashMap<>();
        emailNotice.put("type", MqConstants.JobMateMationJobType.MAIL_DRAFTS_SEND.getJobType());//消息队列任务类型
        emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
        emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
        emailNotice.put("title", map.get("typeName"));//邮件标题
        emailNotice.put("content", map.get("content"));//邮件内容
        emailNotice.put("toPeople", toPeople);//邮件接收人
        emailNotice.put("toCc", toCc);//邮件抄送人
        emailNotice.put("emailId", id);//邮件id
        emailNotice.put("toBcc", toBcc);//邮件暗送人
        emailNotice.put("emailEnclosure", JSONUtil.toJsonStr(emailEnclosureList));//邮件附件
        jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
    }
    
    /**
     * 
         * @Title: queryForwardEmailMationToEditByUserId
         * @Description: 转发时进行信息回显
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryForwardEmailMationToEditByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = userEmailDao.queryForwardEmailMationToEditByUserId(map);//获取邮件基础信息
        if(bean != null && !bean.isEmpty()){
            List<Map<String, Object>> fromPeople = userEmailDao.queryForwardEmailFromPeopleMationToEditByUserId(bean);//获取发件人列表
            List<Map<String, Object>> toCc = userEmailDao.queryForwardEmailToCcMationToEditByUserId(bean);//获取抄送人列表
            List<Map<String, Object>> toBcc = userEmailDao.queryForwardEmailToBccMationToEditByUserId(bean);//获取暗送人列表
            List<Map<String, Object>> emailEnclosure = userEmailDao.queryForwardEmailEnclosureMationToEditByUserId(bean);//获取附件人列表
            bean.put("toPeopleList", fromPeople);
            bean.put("toCcList", toCc);
            bean.put("toBccList", toBcc);
            bean.put("emailEnclosureList", emailEnclosure);
            outputObject.setBean(bean);
        }else{
            outputObject.setreturnMessage("该草稿已不存在或者已经被发送。");
        }
    }
    
    /**
     * 
         * @Title: insertForwardToSendEmailMationByUserId
         * @Description: 转发邮件
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertForwardToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        //接收人邮箱校验
        String[] toPeopleEmails = map.get("toPeople").toString().split(",");
        String toPeople = "";
        for(int i = 0; i < toPeopleEmails.length; i++){
            if(ToolUtil.isEmail(toPeopleEmails[i])){//如果是邮箱
                if(i == 0)
                    toPeople = toPeopleEmails[i];
                else
                    toPeople += "," + toPeopleEmails[i];
            }
        }
        if(ToolUtil.isBlank(toPeople)){
            outputObject.setreturnMessage("请选择收件人");
            return;
        }
		
        //抄送人邮箱校验
        String[] toCcEmails = map.get("toCc").toString().split(",");
        String toCc = "";
        for(int i = 0; i < toCcEmails.length; i++){
            if(ToolUtil.isEmail(toCcEmails[i])){//如果是邮箱
                if(i == 0)
                    toCc = toCcEmails[i];
                else
                    toCc += "," + toCcEmails[i];
            }
        }
        //暗送人邮箱校验
        String[] toBccEmails = map.get("toBcc").toString().split(",");
        String toBcc = "";
        for(int i = 0; i < toBccEmails.length; i++){
            if(ToolUtil.isEmail(toBccEmails[i])){//如果是邮箱
                if(i == 0)
                    toBcc = toBccEmails[i];
                else
                    toBcc += "," + toBccEmails[i];
            }
        }
        Map<String, Object> user = inputObject.getLogParams();
        String id = ToolUtil.getSurFaceId();//邮件id
        String userId = user.get("id").toString();
        map.put("userId", userId);
        //附件处理
        List<Map<String, Object>> emailEnclosureList = userEmailDao.queryMyEnclusureListByUserIdAndIds(map);
        for(Map<String, Object> bean : emailEnclosureList){
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
        }
        //如果邮件是从别的服务器同步来的，那么包含的附件可能在我们的附件库上没有，这个对象负责存储那些我们的附件库上没有的附件
		List<Map<String, Object>> emailEnclosureBeans = JSONUtil.toList(map.get("emailEnclosureList").toString(), null);
        Map<String, Object> bean = null;
        File f = null;
        //文件名，文件后缀
        String fileName, fileExtName;
        for(int i = 0; i < emailEnclosureBeans.size(); i++){
			Map<String, Object> j = emailEnclosureBeans.get(i);
            fileName = j.get("name").toString();
            fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//文件后缀
            bean = new HashMap<>();
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("parentId", id);
            bean.put("fileName", fileName);
            bean.put("filePath", j.get("fileAddress"));
            bean.put("fileExtName", fileExtName);
            f = new File(tPath.replace("images", "") + j.get("fileAddress").toString());
            bean.put("fileSize", f.length());
            emailEnclosureList.add(bean);
        }
        //获取发件箱信息
        Map<String, Object> email = userEmailDao.queryEmailMationById(map);
        //插入邮件附件
        if(!emailEnclosureList.isEmpty())
            userEmailDao.insertEmailEnclosureListToServer(emailEnclosureList);
        //邮件处理
        Map<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("id", id);
        sendEmail.put("title", map.get("typeName"));
        sendEmail.put("sendDate", DateUtil.getTimeAndToString());
        sendEmail.put("replaySign", 1);
        sendEmail.put("isNew", 2);
        //是否包含附件  1.是  2.否
        if(emailEnclosureList != null && !emailEnclosureList.isEmpty()){
            sendEmail.put("isContainAttach", 1);
        }else{
            sendEmail.put("isContainAttach", 2);
        }
        sendEmail.put("fromPeople", email.get("emailAddress"));
        sendEmail.put("toPeople", toPeople);
        sendEmail.put("toCc", toCc);
        sendEmail.put("toBcc", toBcc);
        sendEmail.put("content", map.get("content"));//邮件内容
        sendEmail.put("createTime", DateUtil.getTimeAndToString());
        sendEmail.put("emailState", 1);//邮件状态 0.草稿  1.正常  2.已删除
        userEmailDao.insertToSendEmailMationByUserId(sendEmail);
		
        //消息队列参数对象
        Map<String, Object> emailNotice = new HashMap<>();
        emailNotice.put("type", MqConstants.JobMateMationJobType.COMPLEX_MAIL_DELIVERY.getJobType());//消息队列任务类型
        emailNotice.put("userAddress", email.get("emailAddress"));//邮箱地址
        emailNotice.put("userPassword", email.get("emailPassword"));//邮箱密码
        emailNotice.put("title", map.get("typeName"));//邮件标题
        emailNotice.put("content", map.get("content"));//邮件内容
        emailNotice.put("toPeople", toPeople);//邮件接收人
        emailNotice.put("toCc", toCc);//邮件抄送人
        emailNotice.put("emailId", id);//邮件id
        emailNotice.put("toBcc", toBcc);//邮件暗送人
        emailNotice.put("emailEnclosure", JSONUtil.toJsonStr(emailEnclosureList));//邮件附件
        jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
    }

}
