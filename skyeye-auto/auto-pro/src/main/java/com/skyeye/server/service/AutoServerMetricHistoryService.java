/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.server.entity.AutoServer;
import com.skyeye.server.entity.AutoServerMetricHistory;
import com.skyeye.server.util.AutoServerSshMetricCollector;

/**
 * 服务器资源采集历史
 */
public interface AutoServerMetricHistoryService extends SkyeyeBusinessService<AutoServerMetricHistory> {

    /**
     * 写入一条采集历史（成功/失败都记）。
     */
    void saveCollectHistory(AutoServer server, AutoServerSshMetricCollector.MetricResult result, String collectTime);

    /**
     * 查询某台服务器最近 N 小时的历史，供折线图使用。
     */
    void queryServerMetricHistory(InputObject inputObject, OutputObject outputObject);

    /**
     * 清理过期历史（默认保留 30 天）。
     */
    void cleanExpiredHistory(int keepDays);
}
