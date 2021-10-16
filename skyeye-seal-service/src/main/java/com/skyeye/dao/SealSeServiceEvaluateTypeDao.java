/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceEvaluateTypeDao
 * @Description: 售后服务评价类型管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealSeServiceEvaluateTypeDao {

    public int insertSealSeServiceEvaluateType(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> querySealSeServiceEvaluateTypeList(Map<String, Object> map) throws Exception;

    public Map<String, Object> querySealSeServiceEvaluateTypeMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> querySealSeServiceEvaluateTypeByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> querySealSeServiceEvaluateTypeByIdAndName(Map<String, Object> map) throws Exception;

    public int editSealSeServiceEvaluateTypeById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteSealSeServiceEvaluateTypeById(Map<String, Object> map) throws Exception;

}
