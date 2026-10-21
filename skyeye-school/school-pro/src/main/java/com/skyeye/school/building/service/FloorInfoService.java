package com.skyeye.school.building.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.FloorInfo;

/**
 * @ClassName: FloorInfoService
 * @Description: 楼层教室服务管理接口层
 * @author: skyeye云系列--lqy
 * @date: 2023/9/5 17:12
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FloorInfoService extends SkyeyeBusinessService<FloorInfo> {
    void queryFloorInfosByLocationId(InputObject inputObject, OutputObject outputObject);

    void queryFloorClassList(InputObject inputObject, OutputObject outputObject);

    /**
     * 将楼梯/电梯范围同步到同楼栋其他楼层（同 linkGroupId）
     */
    void syncVerticalSpaceToFloors(InputObject inputObject, OutputObject outputObject);
}
