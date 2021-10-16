/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface CheckWorkOvertimeService {
    public void queryMyCheckWorkOvertimeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertCheckWorkOvertimeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCheckWorkOvertimeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkOvertimeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCheckWorkOvertimeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkOvertimeByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCheckWorkOvertimeToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkOvertimeToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCheckWorkOvertimeToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 获取指定员工在指定月份的所有审核通过的加班申请数据
     *
     * @param userId 用户id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(String userId, List<String> months) throws Exception;
}
