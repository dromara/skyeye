/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: EmailSendModelDao
 * @Description: 邮件发送模板数据层
 * @author: skyeye云系列--郑杰
 * @date: 2021/10/31 22:51
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface EmailSendModelDao {

	List<Map<String, Object>> queryEmailSendModelList(Map<String, Object> map) throws Exception;

	void insertEmailSendModel(Map<String, Object> map);

	Map<String, Object> queryEmailSendModelInfoById(@Param("id") String id);

	void delEmailSendModelById(@Param("id") String id);

	void updateEmailSendModelById(Map<String, Object> map);

}
