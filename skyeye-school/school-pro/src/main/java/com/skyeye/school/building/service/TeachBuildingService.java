/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.building.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.TeachBuilding;

/**
 * @ClassName: TeachBuildingService
 * @Description: 地点管理服务接口层
 * @author: xqz
 * @date: 2023/8/9 9:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TeachBuildingService extends SkyeyeBusinessService<TeachBuilding> {

    void queryTeachBuildingBySchoolId(InputObject inputObject, OutputObject outputObject);

    void queryTeachBuildingByHolderId(InputObject inputObject, OutputObject outputObject);

    void editTeachBuildingLocationById(InputObject inputObject, OutputObject outputObject);

    void queryCampusTwinBySchoolId(InputObject inputObject, OutputObject outputObject);

    void queryBuildingTwinByLocationId(InputObject inputObject, OutputObject outputObject);
}
