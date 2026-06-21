/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTeamMember;

/**
 * @ClassName: EquipmentInspectionTeamMemberService
 * @Description: 设备巡检班组人员服务接口层
 */
public interface EquipmentInspectionTeamMemberService extends SkyeyeBusinessService<EquipmentInspectionTeamMember> {

    void deleteMemberListByTeamId(String teamId);

}
