/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysScheduleCommonDao
 * @Description: 节假日公共部分
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/28 9:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysScheduleCommonDao {

    /**
     * 根据指定天判断是否属于节假日
     *
     * @param day 指定天，格式为yyyy-mm-dd
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryWhetherIsHolidayByDate(@Param("day") String day) throws Exception;

}
