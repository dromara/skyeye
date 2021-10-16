/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 *
 * @ClassName: KnowlgConstants
 * @Description: 论坛系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 20:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class KnowlgConstants {
	
	//获取已经上线的知识库类型列表的redis的key
  	public static final String SYS_SECOND_KNOWLEDGE_TYPE_UP_STATE_LIST = "sys_knowledge_type_up_state_list";
  	public static String sysSecondKnowledgeTypeUpStateList() {
  		return SYS_SECOND_KNOWLEDGE_TYPE_UP_STATE_LIST;
  	}
  	
  	//word文档分块上传时的分块集合存储key
  	public static final String SYS_WORD_FILE_MODULE_MD5 = "sys_word_file_module_md5_";
  	public static String getSysWordFileModuleByMd5(String md5) {
  		return SYS_WORD_FILE_MODULE_MD5 + md5;
  	}
	
}
