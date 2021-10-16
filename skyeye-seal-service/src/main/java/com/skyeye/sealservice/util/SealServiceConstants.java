/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.sealservice.util;

public class SealServiceConstants {
	
	//获取已经上线的售后服务方式列表的redis的key
	public static final String SYS_SERVICE_MODE_UP_STATE_LIST = "sys_service_mode_up_state_list";
	public static String sysSealSeServiceModeUpStateList() {
		return SYS_SERVICE_MODE_UP_STATE_LIST;
	}
	
	//获取已经上线的售后服务类型列表的redis的key
	public static final String SYS_SERVICE_TPYE_UP_STATE_LIST = "sys_service_tpye_up_state_list";
	public static String sysSealSeServiceTypeUpStateList() {
		return SYS_SERVICE_TPYE_UP_STATE_LIST;
	}
	
	//获取已经上线的售后服务评价类型列表的redis的key
	public static final String SYS_SERVICE_EVALUATE_TPYE_UP_STATE_LIST = "sys_service_evaluate_tpye_up_state_list";
	public static String sysSealSeServiceEvaluateTypeUpStateList() {
		return SYS_SERVICE_EVALUATE_TPYE_UP_STATE_LIST;
	}
	
	//获取已经上线的售后服务故障类型列表的redis的key
	public static final String SYS_SERVICE_FAULT_TPYE_UP_STATE_LIST = "sys_service_fault_tpye_up_state_list";
	public static String sysSealSeServiceFaultTypeUpStateList() {
		return SYS_SERVICE_FAULT_TPYE_UP_STATE_LIST;
	}
	
	//获取已经上线的售后服务工单紧急程度列表的redis的key
	public static final String SYS_SERVICE_URGENCY_UP_STATE_LIST = "sys_service_urgency_up_state_list";
	public static String sysSealSeServiceUrgencyUpStateList() {
		return SYS_SERVICE_URGENCY_UP_STATE_LIST;
	}
	
	//获取已经上线的售后服务工单反馈类型列表的redis的key
	public static final String SYS_SERVICE_FEED_BACK_TYPE_UP_STATE_LIST = "sys_service_feed_back_type_up_state_list";
	public static String sysCrmServiceFeedbackTypeUpStateList() {
		return SYS_SERVICE_FEED_BACK_TYPE_UP_STATE_LIST;
	}
	
}
