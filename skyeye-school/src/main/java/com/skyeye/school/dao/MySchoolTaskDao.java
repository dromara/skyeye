/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MySchoolTaskDao
 * @Description: 学校模块我的。。。数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:49
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MySchoolTaskDao {

	public List<Map<String, Object>> queryMyNowLeadClassList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyWaitMarkingList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMyEndMarkingList(Map<String, Object> map) throws Exception;

}
