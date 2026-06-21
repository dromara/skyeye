/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserStaffTime;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserStaffTimeService
 * @Description: 员工考勤时间管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/12 22:15
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserStaffTimeService extends SkyeyeBusinessService<SysEveUserStaffTime> {

    void querySysEveUserStaffTimeListByTimeId(InputObject inputObject, OutputObject outputObject);

    void deleteByStaffId(String staffId);

    void saveUserStaffCheckWorkTime(List<String> timeIdList, String staffId);

    void getStaffCheckWorkTimeByStaffId(InputObject inputObject, OutputObject outputObject);

    List<Map<String, Object>> getStaffCheckWorkTimeByStaffId(String staffId);

    /**
     * 批量统计各班次绑定员工数
     */
    void countSysEveUserStaffTimeByTimeIds(InputObject inputObject, OutputObject outputObject);

}
