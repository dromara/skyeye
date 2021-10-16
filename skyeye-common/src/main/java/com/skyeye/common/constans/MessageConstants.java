/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 *
 * @ClassName: MessageConstants
 * @Description: 消息通知系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 21:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class MessageConstants {
	
	// 获取已经上线的公告类型列表的redis的key
	public static final String SYS_SECOND_NOTICE_TYPE_UP_STATE_LIST = "sys_second_notice_type_up_state_list_";
	public static String sysSecondNoticeTypeUpStateList(String id) {
		return SYS_SECOND_NOTICE_TYPE_UP_STATE_LIST + id;
	}

}
