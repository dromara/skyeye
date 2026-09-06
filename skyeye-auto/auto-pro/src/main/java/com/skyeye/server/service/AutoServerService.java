/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service;

import com.skyeye.base.business.service.SkyeyeTeamAuthService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.server.entity.AutoServer;

/**
 * @ClassName: AutoServerService
 * @Description: 服务器管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoServerService extends SkyeyeTeamAuthService<AutoServer> {

    void queryAutoServerListByEnvironmentId(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询服务器监控大屏（返回最近一次探测结果，不主动探测）。
     */
    void queryServerMonitorDashboard(InputObject inputObject, OutputObject outputObject);

    /**
     * 中心探测：探测当前项目下全部服务器，并返回大屏数据。
     */
    void probeAutoServersByObjectId(InputObject inputObject, OutputObject outputObject);

    /**
     * 中心探测：探测单台服务器。
     */
    void probeAutoServerById(InputObject inputObject, OutputObject outputObject);

    /**
     * SSH 资源采集：采集当前项目下已启用 SSH 的服务器，并返回大屏数据。
     */
    void collectServerMetricsByObjectId(InputObject inputObject, OutputObject outputObject);

    /**
     * SSH 资源采集：采集单台服务器 CPU/内存/磁盘/负载。
     */
    void collectServerMetricsById(InputObject inputObject, OutputObject outputObject);

    /**
     * 定时/批量：采集全部已启用 SSH 的服务器，返回处理台数。
     */
    int collectAllEnabledServerMetrics();

}
