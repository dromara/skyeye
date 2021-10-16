/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 *
 * @ClassName: ProConstants
 * @Description: 项目管理系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 22:00
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProConstants {

	// 文档分类在redis中存储的key
	public static final String SYS_PRO_FILETYPE_UP_STATE_LIST = "sys_pro_filetype_up_state_list";
	public static String sysProFileTypeUpStateList() {
		return SYS_PRO_FILETYPE_UP_STATE_LIST;
	}
	
	// 项目分类在redis中存储的key
	public static final String SYS_PRO_PROJECTTYPE_UP_STATE_LIST = "sys_pro_projecttype_up_state_list";
	public static String sysProProjectTypeUpStateList() {
		return SYS_PRO_PROJECTTYPE_UP_STATE_LIST;
	}
	
	// 成本费用支出分类在redis中存储的key
	public static final String SYS_PRO_COSTEXPENSETYPE_UP_STATE_LIST = "sys_pro_costexpensetype_up_state_list";
	public static String sysProCostExpenseTypeUpStateList() {
		return SYS_PRO_COSTEXPENSETYPE_UP_STATE_LIST;
	}
	
	// 项目管理讨论板分类在redis中存储的key
	public static final String SYS_PRO_PROJECTDISCUSSTYPE_UP_STATE_LIST = "sys_pro_projectdiscusstype_up_state_list";
	public static String sysProProjectDiscussTypeUpStateList() {
		return SYS_PRO_PROJECTDISCUSSTYPE_UP_STATE_LIST;
	}
	
	// 任务分类在redis中存储的key
	public static final String SYS_PRO_TASKTYPE_UP_STATE_LIST = "sys_pro_tasktype_up_state_list";
	public static String sysProTaskTypeUpStateList() {
		return SYS_PRO_TASKTYPE_UP_STATE_LIST;
	}

}
