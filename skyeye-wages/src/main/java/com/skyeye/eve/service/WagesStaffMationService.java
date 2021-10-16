/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface WagesStaffMationService {

    public void queryWagesStaffWaitAllocatedMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryStaffWagesModelFieldMationListByStaffId(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void saveStaffWagesModelFieldMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryWagesStaffDesignMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryWagesStaffPaymentDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 设置应出勤的班次以及小时
     *
     * @param staffWorkTime 员工对应的考勤班次
     * @param staffModelFieldMap 员工拥有的所有薪资要素字段以及对应的值
     * @param lastMonthDate 指定年月，格式为yyyy-MM
     * @throws Exception
     */
    void setLastMonthBe(List<Map<String, Object>> staffWorkTime, Map<String, String> staffModelFieldMap, String lastMonthDate) throws Exception;

    /**
     * 判断指定日期是否是节假日
     *
     * @param yesterdayTime 昨天的日期，格式为yyyy-mm-dd
     * @return
     * @throws Exception
     */
    boolean judgeISHoliday(String yesterdayTime) throws Exception;

}
