/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface CheckWorkLeaveService {
    public void queryMyCheckWorkLeaveList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertCheckWorkLeaveMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCheckWorkLeaveToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkLeaveById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCheckWorkLeaveDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCheckWorkLeaveToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkLeaveToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCheckWorkLeaveToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months) throws Exception;
}
