/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.worktime.entity.CheckWorkTimePoint;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkTimePointService
 * @Description: 考勤班次线上打卡点位服务接口层
 */
public interface CheckWorkTimePointService extends SkyeyeBusinessService<CheckWorkTimePoint> {

    void saveCheckWorkTimePointList(String timeId, List<CheckWorkTimePoint> beans, String userId);

    void deleteByTimeId(String timeId);

    List<CheckWorkTimePoint> selectByTimeId(String timeId);

    Map<String, List<CheckWorkTimePoint>> selectByTimeId(List<String> timeIds);

}
