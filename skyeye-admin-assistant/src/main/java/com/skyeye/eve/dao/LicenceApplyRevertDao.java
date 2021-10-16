/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LicenceApplyRevertDao
 * @Description: 证照归还数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 10:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LicenceApplyRevertDao {

    public List<Map<String, Object>> selectMyLicenceRevertList(Map<String, Object> map) throws Exception;

    public int insertLicenceRevertMation(Map<String, Object> bean) throws Exception;

    public int insertLicenceRevertGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryLicenceRevertMationById(@Param("id") String id) throws Exception;

    public Map<String, Object> queryLicenceRevertByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int updateLicenceRevertStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public int editLicenceRevertGoodsStateById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public int editLicenceRevertGoodsStateByRevertId(@Param("revertId") String revertId, @Param("state") Integer state) throws Exception;

    public int editLicenceRevertStateById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public int editLicenceRevertStateAndProcessInIdIsNullById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public List<Map<String, Object>> queryLicenceRevertChildListById(@Param("revertId") String revertId) throws Exception;

    public int updateLicenceRevertMation(Map<String, Object> map) throws Exception;

    public int deleteLicenceRevertGoodsMationById(Map<String, Object> map) throws Exception;

}
