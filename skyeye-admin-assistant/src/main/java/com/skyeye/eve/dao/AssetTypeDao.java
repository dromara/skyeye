/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetTypeDao
 * @Description: 资产类型管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/17 23:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetTypeDao {

    public List<Map<String, Object>> selectAllAssettypeMation(Map<String, Object> map) throws Exception;

    public int insertAssettypeMation(Map<String, Object> map) throws Exception;

    public int deleteAssettypeById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeMationById(Map<String, Object> map) throws Exception;

    public int editAssettypeMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeMationByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeMationByNameAndId(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeAfterOrderBum(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeMationStateById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> selectAllAssettypeToChoose(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeISTopByThisId(Map<String, Object> map) throws Exception;

    public int editAssettypeSortTopById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryAssettypeISLowerByThisId(Map<String, Object> map) throws Exception;

    public int editAssettypeSortLowerById(Map<String, Object> map) throws Exception;

    public int editAssettypeUpTypeById(Map<String, Object> map) throws Exception;

    public int editAssettypeDownTypeById(Map<String, Object> map) throws Exception;

}
