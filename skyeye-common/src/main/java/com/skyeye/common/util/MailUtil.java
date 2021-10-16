/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 阿里云邮件发送
 */
@Component
public class MailUtil {
	
	//配置文件中的发件服务器
	private static String smtpHost;
	@Value("${EMAIL_SMTP_HOST}")
	public void setSmtpHost(String smtpHost) {
		MailUtil.smtpHost = smtpHost;
	}
	
	//配置文件中的发件账号
	private static String userPz;
	@Value("${EMAIL_SMTP_SEND_USER}")
	public void setUserPz(String userPz) {
		MailUtil.userPz = userPz;
	}
	
	//配置文件中的发件账号密码
	private static String passwordPz;
	@Value("${EMAIL_SMTP_SEND_PASSWORD}")
	public void setPasswordPz(String passwordPz) {
		MailUtil.passwordPz = passwordPz;
	}
	
	//发件服务器
	private String ALIDM_SMTP_HOST;

	// 发件人的账号 和 密码
	private String user;
	private String password;

	public MailUtil() {
		this(userPz, passwordPz, smtpHost);
	}
	
	public MailUtil(String user, String password) {
		this.user = user;
		this.password = password;
		this.ALIDM_SMTP_HOST = smtpHost;
	}

	public MailUtil(String user, String password, String smtpHost) {
		this.user = user;
		this.password = password;
		this.ALIDM_SMTP_HOST = smtpHost;
	}

	public static void main(String[] args) {
		 new MailUtil().send("weizhiqiang@gzwpinfo.com", "测试1", "nihao显示");
	}
	
	/**
	 * 邮箱登录验证
	 * @throws MessagingException
	 */
	public boolean authLogin() throws MessagingException{
		Session session = loadMailSession();
        Transport transport = session.getTransport();
        try {
            transport.connect(user, password);
            return true;
        } catch (MessagingException e) {
            return false;
        } finally {
            transport.close();
        }
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param toEmail
	 *            收件人邮箱地址
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容 可以是html内容
	 */
	public void send(String toEmail, String subject, String content) {
		Session session = loadMailSession();
		// 创建邮件消息
		MimeMessage message = new MimeMessage(session);
		try {
			// 设置发件人
			message.setFrom(new InternetAddress(user));
			Address[] a = new Address[1];
			a[0] = new InternetAddress(user);
			message.setReplyTo(a);
			// 设置收件人
			InternetAddress to = new InternetAddress(toEmail);
			message.setRecipient(MimeMessage.RecipientType.TO, to);
			// 设置邮件标题
			message.setSubject(subject);
			// 设置邮件的内容体
			message.setContent(content, "text/html;charset=UTF-8");
			// 发送邮件
			Transport.send(message);
		} catch (MessagingException e) {
			String err = e.getMessage();
			// 在这里处理message内容， 格式是固定的
			System.out.println(err);
		}
	}

	/**
	 * 发送邮件 带附件
	 * 
	 * @param toEmail
	 *            收件人邮箱地址
	 * @param toCc
	 *            抄送人邮箱地址
	 * @param toBcc
	 *            暗送人邮箱地址
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容 可以是html内容
	 * @param basePath
	 *            文件基础路径
	 * @param files
	 *            附件
	 */
	@SuppressWarnings("static-access")
	public String send(String toEmail, String toCc, String toBcc, String subject, String content, String basePath, List<Map<String, Object>> files) {
		Session session = loadMailSession();

		MimeMessage mm = new MimeMessage(session);
		try {
			// 发件人
			mm.setFrom(new InternetAddress(user));
			// 收件人
			InternetAddress[] to = new InternetAddress().parse(toEmail);
			mm.setRecipients(Message.RecipientType.TO, to); // 设置收件人
			
			// 抄送地址
            if (null != toCc && toCc.trim().length() > 0) {
                InternetAddress[] cc = new InternetAddress().parse(toCc);
                mm.setRecipients(Message.RecipientType.CC, cc);
            }
            
            // 密送地址
            if (null != toBcc && toBcc.trim().length() > 0) {
                InternetAddress[] bcc = new InternetAddress().parse(toBcc);
                mm.setRecipients(Message.RecipientType.BCC, bcc);
            }
            
			// 标题
			mm.setSubject(subject);
			// 内容
			Multipart multipart = new MimeMultipart();
			// body部分
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(content, "text/html;charset=utf-8");
			multipart.addBodyPart(contentPart);

			// 附件部分
			if(files != null && !files.isEmpty()){
				for(Map<String, Object> file: files){
					BodyPart attachPart = new MimeBodyPart();
					FileDataSource fileDataSource = new FileDataSource(basePath + file.get("filePath").toString());
					attachPart.setDataHandler(new DataHandler(fileDataSource));
					attachPart.setFileName(MimeUtility.encodeText(file.get("fileName").toString(), "utf-8", null));
					multipart.addBodyPart(attachPart);
				}
			}

			mm.setContent(multipart);
			Transport.send(mm);
			String messageID = mm.getMessageID();
            if(messageID == null){
                return "";
            }
            return messageID.substring(1, messageID.length() - 1);
		} catch (Exception e) {
			String err = e.getMessage();
			// 在这里处理message内容， 格式是固定的
			System.out.println(err);
		}
		return "";
	}
	
	/**
     * 保存邮件为草稿 带附件
     * 
     * @param toEmail
     *            收件人邮箱地址
     * @param toCc
     *            抄送人邮箱地址
     * @param toBcc
     *            暗送人邮箱地址
     * @param subject
     *            邮件标题
     * @param content
     *            邮件内容 可以是html内容
     * @param basePath
     *            文件基础路径
     * @param files
     *            附件
     */
    @SuppressWarnings("static-access")
    public String saveDraftsEmail(String toEmail, String toCc, String toBcc, String subject, String content, String basePath, List<Map<String, Object>> files) {
        Session session = loadMailSession();
        MimeMessage mm = new MimeMessage(session);
        try {
            Store store = session.getStore("imap");
            store.connect(ALIDM_SMTP_HOST, user, password);
            Folder folder = store.getFolder("Drafts");
            // 发件人
            mm.setFrom(new InternetAddress(user));
            // 收件人
            InternetAddress[] to = new InternetAddress().parse(toEmail);
            mm.setRecipients(Message.RecipientType.TO, to); // 设置收件人
			
            // 抄送地址
            if (null != toCc && toCc.trim().length() > 0) {
                InternetAddress[] cc = new InternetAddress().parse(toCc);
                mm.setRecipients(Message.RecipientType.CC, cc);
            }
            
            // 密送地址
            if (null != toBcc && toBcc.trim().length() > 0) {
                InternetAddress[] bcc = new InternetAddress().parse(toBcc);
                mm.setRecipients(Message.RecipientType.BCC, bcc);
            }
            
            // 标题
            mm.setSubject(subject);
            // 内容
            Multipart multipart = new MimeMultipart();
            // body部分
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);
            // 附件部分
            if(files != null && !files.isEmpty()){
                for(Map<String, Object> file: files){
                    BodyPart attachPart = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(basePath + file.get("filePath").toString());
                    attachPart.setDataHandler(new DataHandler(fileDataSource));
                    attachPart.setFileName(MimeUtility.encodeText(file.get("fileName").toString(), "utf-8", null));
                    multipart.addBodyPart(attachPart);
                }
            }
            mm.setContent(multipart);
            mm.saveChanges();
            mm.setFlag(Flags.Flag.DRAFT, true);
            MimeMessage[] draftMessages = {mm};
            folder.appendMessages(draftMessages);
            String messageID = mm.getMessageID();
            if(messageID == null){
                return "";
            }
            return messageID.substring(1, messageID.length() - 1);
        } catch (Exception e) {
            String err = e.getMessage();
            // 在这里处理message内容， 格式是固定的
            System.out.println(err);
        }
        return "";
    }
    
    /**
     * 删除草稿箱邮件
     * 
     * @param sendEmail
     *            发送人邮箱
     * @param title
     *            邮件标题
     * @param messageId
     *            消息id
     */
    public void deleteEmail(String sendEmail, String title, String messageId) {
        Session session = loadMailSession();
        try {
            Store store = session.getStore("imap");
            store.connect(ALIDM_SMTP_HOST, user, password);
            Folder folder = store.getFolder("Drafts");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            for (int i = 0; i < messages.length; i++) {
                MimeMessage re = (MimeMessage) messages[i];
                String msgId = re.getMessageID();
                msgId = msgId.substring(1, msgId.length() - 1);
                if(messageId.equals(msgId)){
                    if(!messages[i].isSet(Flags.Flag.DELETED))
                        messages[i].setFlag(Flags.Flag.DELETED, true);
                }
            }
            folder.close(true);
            store.close();
        } catch (Exception e) {
            String err = e.getMessage();
            // 在这里处理message内容， 格式是固定的
            System.out.println(err);
        }
    }

	private Session loadMailSession() {
		try {
			// 配置发送邮件的环境属性
			final Properties props = new Properties();
			// 表示SMTP发送邮件，需要进行身份验证
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", ALIDM_SMTP_HOST);
			// 如果使用ssl，则去掉使用25端口的配置，进行如下配置,
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.port", "465");
			// 发件人的账号
			props.put("mail.user", user);
			// 访问SMTP服务时需要提供的密码
			props.put("mail.password", password);
			// 构建授权信息，用于进行SMTP进行身份验证
			Authenticator authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// 用户名、密码
					String userName = props.getProperty("mail.user");
					String password = props.getProperty("mail.password");
					return new PasswordAuthentication(userName, password);
				}
			};
			// 使用环境属性和授权信息，创建邮件会话
			return Session.getInstance(props, authenticator);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("mail session is null");
		}
		return null;
	}

}
