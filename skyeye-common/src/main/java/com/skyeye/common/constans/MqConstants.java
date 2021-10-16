/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 *
 * @ClassName: MqConstants
 * @Description: MQ消息队列常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 21:56
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class MqConstants {
	
	/**
	 * 等待处理
	 */
	public static final String JOB_TYPE_IS_WAIT = "1";
	
	/**
	 * 处理中
	 */
	public static final String JOB_TYPE_IS_PROCESSING = "2";
	
	/**
	 * 执行失败
	 */
	public static final String JOB_TYPE_IS_FAIL = "3";
	
	/**
	 * 执行成功
	 */
	public static final String JOB_TYPE_IS_SUCCESS = "4";
	
	/**
	 * 部分成功
	 */
	public static final String JOB_TYPE_IS_PARTIAL_SUCCESS = "5";
	
	public static enum JobMateMationJobType {
		ORDINARY_MAIL_DELIVERY(2, 1, false, "邮件通知", "ordinaryMailDeliveryService"),
		MAIL_ACCESS_INBOX(3, 2, true, "收件箱邮件获取", "mailAccessInboxService"),
		MAIL_ACCESS_SENDED(3, 3, true, "已发送邮件获取", "mailAccessSendedService"),
		MAIL_ACCESS_DELETE(3, 4, true, "已删除邮件获取", "mailAccessDeleteService"),
		MAIL_ACCESS_DRAFTS(3, 5, true, "草稿箱邮件获取", "mailAccessDraftsService"),
		COMPLEX_MAIL_DELIVERY(2, 6, false, "邮件发送", "complexMailDeliveryService"),
		MAIL_DRAFTS_SAVE(2, 7, false, "保存草稿同步", "mailDraftsSaveService"),
		MAIL_DRAFTS_EDIT(2, 8, false, "草稿编辑同步", "mailDraftsEditService"),
		MAIL_DRAFTS_SEND(2, 9, false, "草稿箱邮件发送", "mailDraftsSendService"),
		NOTICE_SEND(2, 10, false, "消息通知", "noticeSendService"),
		WATI_WORKER_SEND(2, 11, false, "派工通知", "watiWorkerSendService"),
		OUTPUT_NOTES_IS_ZIP(1, 100, true, "笔记输出压缩包", "outputNotesIsZipService");
		
		// 所属大类  1.我的输出  2.我的发送  3.我的获取
		private int bigType;
        private int jobType;
        private boolean sendMsgToPage;
        private String typeName;
        private String serviceName;
		
        JobMateMationJobType(int bigType, int jobType, boolean sendMsgToPage, String typeName, String serviceName){
        	this.bigType = bigType;
            this.jobType = jobType;
            this.sendMsgToPage = sendMsgToPage;
            this.typeName = typeName;
            this.serviceName = serviceName;
        }
	
        public static String getTypeNameByJobType(String jobType){
            for (JobMateMationJobType bean : JobMateMationJobType.values()){
                if(bean.getJobType() == Integer.parseInt(jobType)){
                    return bean.getTypeName();
                }
            }
            return "";
        }
        
        public static String getServiceNameByJobType(String jobType){
            for (JobMateMationJobType bean : JobMateMationJobType.values()){
                if(bean.getJobType() == Integer.parseInt(jobType)){
                    return bean.getServiceName();
                }
            }
            return "";
        }
        
        public static int getBigTypeByJobType(String jobType){
            for (JobMateMationJobType bean : JobMateMationJobType.values()){
                if(bean.getJobType() == Integer.parseInt(jobType)){
                    return bean.getBigType();
                }
            }
            return 0;
        }
        
        public static boolean getSendMsgToPageByJobType(String jobType){
            for (JobMateMationJobType bean : JobMateMationJobType.values()){
                if(bean.getJobType() == Integer.parseInt(jobType)){
                    return bean.isSendMsgToPage();
                }
            }
            return false;
        }
        
		public int getBigType() {
			return bigType;
		}

		public int getJobType() {
			return jobType;
		}

		public String getTypeName() {
			return typeName;
		}

		public String getServiceName() {
			return serviceName;
		}

		public boolean isSendMsgToPage() {
			return sendMsgToPage;
		}
    }
	
}
