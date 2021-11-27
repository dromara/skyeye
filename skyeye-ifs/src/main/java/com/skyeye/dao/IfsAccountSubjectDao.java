/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsAccountSubjectDao
 * @Description: 会计科目管理数据层
 * @author: skyeye云系列
 * @date: 2021/11/27 12:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsAccountSubjectDao {

    public List<Map<String, Object>> queryIfsAccountSubjectList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryIfsAccountSubjectMationByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryIfsAccountSubjectMationByNum(Map<String, Object> map) throws Exception;

    public int insertIfsAccountSubject(Map<String, Object> map) throws Exception;

    public int deleteIfsAccountSubjectById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryIfsAccountSubjectToEditById(Map<String, Object> map) throws Exception;

    public int editIfsAccountSubjectById(Map<String, Object> map) throws Exception;

}
