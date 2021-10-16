/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkBusinessTripService
 * @Description: 出差申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CheckWorkBusinessTripService {
    public void queryMyCheckWorkBusinessTripList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertCheckWorkBusinessTripMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCheckWorkBusinessTripToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkBusinessTripById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCheckWorkBusinessTripDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkBusinessTripByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCheckWorkBusinessTripToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateCheckWorkBusinessTripToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCheckWorkBusinessTripToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryStateIsSuccessBusinessTripDayByUserIdAndMonths(String userId, String timeId,
        List<String> months) throws Exception;
    
}
