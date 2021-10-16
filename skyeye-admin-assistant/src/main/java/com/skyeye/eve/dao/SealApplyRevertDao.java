/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealApplyRevertDao
 * @Description: 印章归还申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealApplyRevertDao {

    public List<Map<String, Object>> selectMySealRevertList(Map<String, Object> map) throws Exception;

    public int insertSealRevertMation(Map<String, Object> bean) throws Exception;

    public int insertSealRevertGoodsMation(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> querySealRevertMationById(@Param("id") String id) throws Exception;

    public Map<String, Object> querySealRevertByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int updateSealRevertStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public int editSealRevertById(@Param("id") String id, @Param("state") Integer state) throws Exception;

    public int editSealRevertToRevokeById(@Param("id") String id) throws Exception;

    public Map<String, Object> querySealRevertMationToEditById(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> querySealMationListByRevertId(@Param("revertId") String revertId) throws Exception;

    public List<Map<String, Object>> querySealRevertGoodsMationToEditById(Map<String, Object> map) throws Exception;

    public int updateSealRevertMation(Map<String, Object> map) throws Exception;

    public int deleteSealRevertGoodsMationById(Map<String, Object> map) throws Exception;

    public int updateSealRevertToCancellation(Map<String, Object> map) throws Exception;

    public int updateSealRevertGoodsStateByRevertId(@Param("revertId") String revertId, @Param("state") Integer state) throws Exception;

}
