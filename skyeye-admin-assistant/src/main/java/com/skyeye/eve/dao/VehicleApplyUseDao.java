/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VehicleApplyUseDao
 * @Description: 用车申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VehicleApplyUseDao {

    public int insertVehicleMationToUse(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryMyUseVehicleMation(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryVehicleUseMationShowById(@Param("id") String id) throws Exception;

    public int updateVehicleUseStateISInAudit(@Param("id") String id, @Param("processInId") String processInId) throws Exception;

    public Map<String, Object> queryVehicleUseByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

    public int editVehicleUseById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryVehicleUseMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryTheSuitableVehicleToUse(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryVehicleUseDetails(@Param("id") String id) throws Exception;

    public int updateVehicleUseToCancellation(Map<String, Object> map) throws Exception;

    public int updateVehicleUseMationToEdit(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryVehicleUseMationToEdit(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryVehicleIsHasUseById(Map<String, Object> map) throws Exception;

    public int updateVehicleUseToRevoke(Map<String, Object> map) throws Exception;

}
