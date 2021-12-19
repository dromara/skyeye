/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.util;

import javax.mail.Message;
import javax.mail.Part;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: EmailUtil
 * @Description: 邮件工具类
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/19 13:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class EmailUtil {

    /**
     * 获取邮件内容-使用工具类对邮件进行解析
     * @param re 工具对象
     * @param message 邮件内容体
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getEmailMationByUtil(ShowMail re, Message message) throws Exception{
        Map<String, Object> bean = new HashMap<>();
        re.setDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
        bean.put("title", re.getSubject());//标题
        bean.put("sendDate", re.getSentDate());//发送时间
        //是否需要回复   1.需要   2.不需要
        if(re.getReplySign()){
            bean.put("replaySign", 1);
        }else{
            bean.put("replaySign", 2);
        }
        //是否已读   1.已读  2.未读
        if(re.isNew()){
            bean.put("isNew", 1);
        }else{
            bean.put("isNew", 2);
        }
        //是否包含附件  1.是  2.否
        if(re.isContainAttach((Part) message)){
            bean.put("isContainAttach", 1);
        }else{
            bean.put("isContainAttach", 2);
        }
        bean.put("fromPeople", re.getAddressFrom());//发件人
        bean.put("toPeople", re.getMailAddress("to"));//收件人
        bean.put("toCc", re.getMailAddress("cc"));//抄送人
        bean.put("toBcc", re.getMailAddress("bcc"));//暗送
        bean.put("messageId", re.getMessageId());//消息id
        String contentType = ((Part) message).getContentType();
        boolean plainFlag;
        if (contentType.startsWith("text/plain")) {
            plainFlag = true;
        }else{
            plainFlag = false;
        }
        re.getMailContent((Part) message, plainFlag);//设置内容
        bean.put("content", re.getBodyText());//邮件内容
        bean.put("createTime", re.getSentDate());//创建时间
        return bean;
    }

}
