/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.worktime.entity.CheckWorkTime;

import java.util.List;

/**
 * @ClassName: CheckWorkTimeService
 * @Description: 考勤班次管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CheckWorkTimeService extends SkyeyeBusinessService<CheckWorkTime> {

    void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject);

    void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject);

    void getAllCheckWorkTime(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据指定年月获取所有的考勤班次的信息以及工作日信息等
     *
     * @param pointMonthDate 指定年月，格式为yyyy-MM
     * @return
     */
    List<CheckWorkTime> getAllCheckWorkTime(String pointMonthDate);

    void setOnlineCheckWorkTime(InputObject inputObject, OutputObject outputObject);

    void copyCheckWorkTime(InputObject inputObject, OutputObject outputObject);

    void queryCheckWorkTimeStaffListByTimeId(InputObject inputObject, OutputObject outputObject);
}
