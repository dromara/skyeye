/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: VehicleApplyUseService
 * @Description: 用车申请服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VehicleApplyUseService {

    public void insertVehicleMationToUse(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyUseVehicleMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryVehicleUseDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editVehicleUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateVehicleUseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateVehicleUseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryVehicleUseMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateVehicleUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
