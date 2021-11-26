/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InoutitemDao
 * @Description: 收支项目信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:28
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface InoutitemDao {

    List<Map<String, Object>> queryInoutitemByList(Map<String, Object> params) throws Exception;

    Map<String, Object> queryInoutitemByName(Map<String, Object> params) throws Exception;

    int insertInoutitem(Map<String, Object> params) throws Exception;

    Map<String, Object> queryInoutitemById(Map<String, Object> params) throws Exception;

    int editInoutitemByDeleteFlag(Map<String, Object> params) throws Exception;

    Map<String, Object> queryInoutitemByIdAndName(Map<String, Object> params) throws Exception;

    int editInoutitemById(Map<String, Object> params) throws Exception;

    Map<String, Object> queryInoutitemByIdAndInfo(Map<String, Object> params) throws Exception;

	List<Map<String, Object>> queryInoutitemListToSelect(Map<String, Object> params) throws Exception;

}
