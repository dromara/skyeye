/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 *
 * @ClassName: CrmConstants
 * @Description: CRM系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 20:38
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CrmConstants {
	
	// 获取已经上线的客户类型列表的redis的key
	public static final String SYS_CUSTOMER_TYPE_UP_STATE_LIST = "sys_customer_type_up_state_list";
	public static String sysCustomerTypeUpStateList() {
		return SYS_CUSTOMER_TYPE_UP_STATE_LIST;
	}
	
	// 获取已经上线的客户分组列表的redis的key
	public static final String SYS_CUSTOMER_GROUP_UP_STATE_LIST = "sys_customer_group_up_state_list";
	public static String sysCustomerGroupUpStateList() {
		return SYS_CUSTOMER_GROUP_UP_STATE_LIST;
	}
	
	// 获取已经上线的跟单分类列表的redis的key
	public static final String SYS_DOCUMENTARY_TYPE_UP_STATE_LIST = "sys_documentary_type_up_state_list";
	public static String sysCrmDocumentaryTypeUpStateList() {
		return SYS_DOCUMENTARY_TYPE_UP_STATE_LIST;
	}
	
	// 获取已经上线的客户来源列表的redis的key
	public static final String SYS_CUSTOMER_FROM_UP_STATE_LIST = "sys_customer_from_up_state_list";
	
	// 获取已经上线的客户所属行业列表的redis的key
	public static final String SYS_CUSTOMER_INDUSTRY_UP_STATE_LIST = "sys_customer_industry_up_state_list";
	public static String sysCrmCustomerIndustryUpStateList() {
		return SYS_CUSTOMER_INDUSTRY_UP_STATE_LIST;
	}
	
	// 获取已经上线的商机来源列表的redis的key
	public static final String SYS_OPPORTUNITY_FROM_UP_STATE_LIST = "sys_opportunity_from_up_state_list";
	public static String sysCrmOpportunityFromUpStateList() {
		return SYS_OPPORTUNITY_FROM_UP_STATE_LIST;
	}
	
	// 获取所有的客户信息列表的redis的key
	public static final String SYS_ALL_CUSTOMER_LIST = "sys_all_customer_list";
	public static String sysAllCustomerList() {
		return SYS_ALL_CUSTOMER_LIST;
	}
	
	// 根据客户id获取合同列表的redis的key
	public static final String SYS_CONTRACT_LIST_BY_ID = "sys_contract_list_by_id_";
	public static String sysContractListById(String id) {
		return SYS_CONTRACT_LIST_BY_ID + id;
	}
	
}
