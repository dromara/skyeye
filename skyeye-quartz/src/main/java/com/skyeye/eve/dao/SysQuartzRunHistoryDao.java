/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysQuartzRunHistoryDao
 * @Description: 系统定时任务启动历史信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/29 11:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysQuartzRunHistoryDao {

    void insertSysQuartzRunHistoryMation(SysQuartzRunHistory sysQuartzRunHistory) throws Exception;

    /**
     * 修改定时任务完成信息
     *
     * @param id 启动历史表id
     * @param state 状态
     * @param endTime 完成时间
     * @throws Exception
     */
    void updateSysQuartzRunHistoryComplateState(@Param("id") String id, @Param("state") Integer state, @Param("endTime") String endTime) throws Exception;

    List<Map<String, Object>> querySysQuartzRunHistoryByQuartzId(Map<String, Object> map) throws Exception;

    SysQuartzRunHistory querySysQuartzRunHistoryById(@Param("id") String id) throws Exception;

}
