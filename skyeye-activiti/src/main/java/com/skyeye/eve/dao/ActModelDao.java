/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActModelDao
 * @Description: 流程配置数据库层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 12:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface ActModelDao {

    public List<Map<String, Object>> selectActModleTypeMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModelMationByNameOrUrl(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModelMationByNameOrUrlAndId(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCountOrderbyInModle(Map<String, Object> map) throws Exception;

    public int insertActModle(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModleDetailsById(@Param("id") String id) throws Exception;

    public int editActModleMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModleUpMationById(Map<String, Object> map) throws Exception;

    public int editActModleMationOrderNumUpById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModleDownMationById(Map<String, Object> map) throws Exception;

    public int editActModleStateById(@Param("id") String id, @Param("state") int state) throws Exception;

    public List<Map<String, Object>> queryActModleUpStateByUpStateTypeId(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModelMationByPageUrl(@Param("pageUrl") String pageUrl) throws Exception;

    public List<Map<String, Object>> queryHasModelList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryActModelByActKey(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryHotActModleDetailsById() throws Exception;
}
