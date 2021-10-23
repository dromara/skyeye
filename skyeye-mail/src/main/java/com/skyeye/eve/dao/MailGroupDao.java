/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MailGroupDao
 * @Description: 通讯录分组管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/23 12:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MailGroupDao {

    public List<Map<String, Object>> queryMailMationTypeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryMailMationTypeByName(Map<String, Object> map) throws Exception;

    public int insertMailMationType(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryMailMationTypeByIdAndUserId(Map<String, Object> map) throws Exception;

    public int deleteMailMationTypeById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryMailMationTypeToEditById(Map<String, Object> map) throws Exception;

    public int editMailMationTypeById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryMailMationTypeListToSelect(Map<String, Object> map) throws Exception;

}
