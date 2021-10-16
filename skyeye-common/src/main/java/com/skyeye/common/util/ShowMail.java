/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author 卫志强
 *
 */
public class ShowMail {

	private MimeMessage mimeMessage = null;
	private String saveAttachPath = ""; // 附件下载后的存放目录
	private StringBuffer bodyText = new StringBuffer(); // 存放邮件内容的StringBuffer对象
	private String dateFormat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

	/**
	 * 构造函数,初始化一个MimeMessage对象
	 */
	public ShowMail() {
	}

	public ShowMail(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * * 获得发件人的地址和姓名
	 */
	public String getFrom() throws Exception {
		InternetAddress[] address = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		String personal = address[0].getPersonal();
		if (personal == null) {
			personal = "";
		}
		String fromAddr = null;
		if (personal != null || from != null) {
			fromAddr = personal + "<" + from + ">";
		}
		return fromAddr;
	}
	
	/**
	 * * 获得发件人的地址
	 */
	public String getAddressFrom() throws Exception {
		InternetAddress[] address = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		String fromAddr = null;
		if (from != null) {
			fromAddr = from;
		}
		return fromAddr;
	}

	/**
	 * * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 * "to"----收件人 "cc"---抄送人地址
	 * "bcc"---密送人地址
	 */
	public String getMailAddressAndName(String type) throws Exception {
		String mailAddr = "";
		String addType = type.toUpperCase();

		InternetAddress[] address = null;
		if (addType.equals("TO") || addType.equals("CC") || addType.equals("BCC")) {
			if (addType.equals("TO")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
			} else if (addType.equals("CC")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
			}

			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String emailAddr = address[i].getAddress();
					if (emailAddr == null) {
						emailAddr = "";
					} else {
						emailAddr = MimeUtility.decodeText(emailAddr);
					}
					String personal = address[i].getPersonal();
					if (personal == null) {
						personal = "";
					} else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + emailAddr + ">";
					mailAddr += "," + compositeto;
				}
				mailAddr = mailAddr.substring(1);
			}
		} else {
			throw new Exception("错误的电子邮件类型!");
		}
		return mailAddr;
	}
	
	/**
	 * * 获得邮件的收件人，抄送，和密送的地址，根据所传递的参数的不同 * "to"----收件人 "cc"---抄送人地址
	 * "bcc"---密送人地址
	 */
	public String getMailAddress(String type) throws Exception {
		String mailAddr = "";
		String addType = type.toUpperCase();

		InternetAddress[] address = null;
		if (addType.equals("TO") || addType.equals("CC") || addType.equals("BCC")) {
			if (addType.equals("TO")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
			} else if (addType.equals("CC")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
			}

			if (address != null && address.length > 0) {
				for (int i = 0; i < address.length; i++) {
					String emailAddr = address[i].getAddress();
					if (emailAddr == null) {
						emailAddr = "";
					} else {
						emailAddr = MimeUtility.decodeText(emailAddr);
					}
					mailAddr += "," + emailAddr;
				}
				mailAddr = mailAddr.substring(1);
			}
		} else {
			throw new Exception("错误的电子邮件类型!");
		}
		return mailAddr;
	}

	/**
	 * * 获得邮件主题
	 */
	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			if(mimeMessage.getSubject() == null){
				subject = "(无主题)";
			}else{
				subject = MimeUtility.decodeText(mimeMessage.getSubject());
			}
			if (subject == null) {
				subject = "";
			}
		} catch (Exception exce) {
			exce.printStackTrace();
		}
		return subject;
	}

	/**
	 * * 获得邮件发送日期
	 */
	public String getSentDate() throws Exception {
		Date sentDate = mimeMessage.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		String strSentDate = format.format(sentDate);
		return strSentDate;
	}

	/**
	 * * 获得邮件正文内容
	 */
	public String getBodyText() {
		return bodyText.toString();
	}

	/**
	 * * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 *
	 * 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
	 */
	public void getMailContent(Part part, boolean plainFlag) throws Exception {
		// 获得邮件的MimeType类型
		boolean conName = part.getContentType().indexOf("name") > 0;

		if (part.isMimeType("text/plain") && !conName && plainFlag) {
			// text/plain 类型
			bodyText.append((String) part.getContent());
			plainFlag = false;
		} else if (part.isMimeType("text/html") && !conName && plainFlag == false) {
			// text/html 类型
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			// multipart/*
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i), plainFlag);
			}
		} else if (part.isMimeType("message/rfc822")) {
			// message/rfc822
			getMailContent((Part) part.getContent(), plainFlag);
		} else {

		}
	}

	/**
	 * * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replySign = false;
		String[] needReply = mimeMessage.getHeader("Disposition-Notification-To");
		if (needReply != null) {
			replySign = true;
		}
		return replySign;
	}

	/**
	 * 获得此邮件的Message-ID
	 */
	public String getMessageId() throws MessagingException {
		String messageID = mimeMessage.getMessageID();
		if(messageID == null){
			return "";
		}
		return messageID.substring(1, messageID.length() - 1);
	}

	/**
	 * 判断此邮件是否已读，如果未读返回false,反之返回true
	 */
	public boolean isNew() throws MessagingException {
		boolean isNew = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isNew = true;
			}
		}
		return isNew;
	}

	/**
	 * 判断此邮件是否包含附件
	 */
	public boolean isContainAttach(Part part) throws Exception {
		boolean attachFlag = false;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mPart = mp.getBodyPart(i);
				String disposition = mPart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE))))
					attachFlag = true;
				else if (mPart.isMimeType("multipart/*")) {
					attachFlag = isContainAttach((Part) mPart);
				} else {
					String conType = mPart.getContentType();
					if (conType.toLowerCase().indexOf("application") != -1)
						attachFlag = true;
					if (conType.toLowerCase().indexOf("name") != -1)
						attachFlag = true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachFlag = isContainAttach((Part) part.getContent());
		}
		return attachFlag;
	}

	/**
	 * * 保存附件
	 */
	public List<Map<String, Object>> saveAttachMent(Part part, String rowId) throws Exception {
		String fileName = "";
		List<Map<String, Object>> beans = new ArrayList<>();
		Map<String, Object> bean = null;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mPart = mp.getBodyPart(i);
				String disposition = mPart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					if(mPart.getFileName() != null){
						fileName = MimeUtility.decodeText(mPart.getFileName());
						if (fileName.toLowerCase().indexOf("gb2312") != -1) {
							fileName = MimeUtility.decodeText(fileName);
						}
						
						String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);//获取文件后缀
						String newFileName = String.valueOf(System.currentTimeMillis()) + "." + fileExtName;//获取文件新名称
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("fileName", fileName);
						bean.put("filePath", "/images/upload/emailenclosure/" + newFileName);
						bean.put("parentId", rowId);
						bean.put("fileExtName", fileExtName);
						bean.put("fileSize", mPart.getSize());
						beans.add(bean);
						
						saveFile(newFileName, mPart.getInputStream());//保存文件
					}
				} else if (mPart.isMimeType("multipart/*")) {
					beans.addAll(saveAttachMent(mPart, rowId));
				} else {
					fileName = mPart.getFileName();
					if ((fileName != null) && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
						fileName = MimeUtility.decodeText(fileName);
						
						String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);//获取文件后缀
						String newFileName = String.valueOf(System.currentTimeMillis()) + "." + fileExtName;//获取文件新名称
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("fileName", fileName);
						bean.put("filePath", "/images/upload/emailenclosure/" + newFileName);
						bean.put("parentId", rowId);
						bean.put("fileExtName", fileExtName);
						bean.put("fileSize", mPart.getSize());
						beans.add(bean);
						
						saveFile(newFileName, mPart.getInputStream());//保存文件
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			beans.addAll(saveAttachMent((Part) part.getContent(), rowId));
		}
		return beans;
	}

	/**
	 * 设置附件存放路径
	 */
	public void setAttachPath(String attachPath) {
		this.saveAttachPath = attachPath;
	}

	/**
	 * * 设置日期显示格式
	 */
	public void setDateFormat(String format) throws Exception {
		this.dateFormat = format;
	}

	/**
	 * * 获得附件存放路径
	 */
	public String getAttachPath() {
		return saveAttachPath;
	}

	/**
	 * * 真正的保存附件到指定目录里
	 */
	private void saveFile(String fileName, InputStream in) throws Exception {
		String osName = System.getProperty("os.name");
		String storeDir = getAttachPath();
		String separator = "";
		if (osName == null) {
			osName = "";
		}
		if (osName.toLowerCase().indexOf("win") != -1) {
			separator = "\\";
			if (storeDir == null || storeDir.equals(""))
				storeDir = "D:\\";
		} else {
			separator = "/";
			storeDir = "/tmp";
		}
		File storeFile = new File(storeDir + separator + fileName);
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storeFile));
			bis = new BufferedInputStream(in);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("文件保存失败!");
		} finally {
			if(bos != null)
				bos.close();
			if(bis != null)
				bis.close();
		}
	}

}
