/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsSetOfBooksDao
 * @Description: 系统编辑器模板数据层
 * @author: skyeye云系列
 * @date: 2021/11/21 14:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsSetOfBooksDao {

    public List<Map<String, Object>> queryIfsSetOfBooksByList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryIfsSetOfBooksMationByName(Map<String, Object> map) throws Exception;

    public int insertIfsSetOfBooks(Map<String, Object> map) throws Exception;

    public int deleteIfsSetOfBooksById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryIfsSetOfBooksDetailById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryIfsSetOfBooksToEditById(Map<String, Object> map) throws Exception;

    public int editIfsSetOfBooksById(Map<String, Object> map) throws Exception;

}
