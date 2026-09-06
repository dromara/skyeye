/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.server.classenum.AutoServerMetricStatusEnum;
import com.skyeye.server.dao.AutoServerMetricHistoryDao;
import com.skyeye.server.entity.AutoServer;
import com.skyeye.server.entity.AutoServerMetricHistory;
import com.skyeye.server.service.AutoServerMetricHistoryService;
import com.skyeye.server.util.AutoServerSshMetricCollector;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务器资源采集历史服务
 */
@Service
@SkyeyeService(name = "服务器资源采集历史", groupName = "服务器管理")
public class AutoServerMetricHistoryServiceImpl
    extends SkyeyeBusinessServiceImpl<AutoServerMetricHistoryDao, AutoServerMetricHistory>
    implements AutoServerMetricHistoryService {

    @Override
    public void saveCollectHistory(AutoServer server, AutoServerSshMetricCollector.MetricResult result, String collectTime) {
        if (server == null || StrUtil.isBlank(server.getId()) || result == null) {
            return;
        }
        AutoServerMetricHistory history = new AutoServerMetricHistory();
        history.setId(ToolUtil.getSurFaceId());
        history.setServerId(server.getId());
        history.setObjectId(server.getObjectId());
        history.setObjectKey(server.getObjectKey());
        history.setCollectTime(StrUtil.blankToDefault(collectTime, DateUtil.getTimeAndToString()));
        history.setCreateTime(DateUtil.getTimeAndToString());
        history.setMetricStatus(result.isSuccess()
            ? AutoServerMetricStatusEnum.OK.getKey()
            : AutoServerMetricStatusEnum.FAIL.getKey());
        history.setMetricMsg(StrUtil.maxLength(result.getMessage(), 500));
        if (result.isSuccess()) {
            history.setCpuUsage(result.getCpuUsage());
            history.setMemUsage(result.getMemUsage());
            history.setDiskUsage(result.getDiskUsage());
            history.setLoadAvg(result.getLoadAvg());
        }
        save(history);
    }

    @Override
    public void queryServerMetricHistory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String serverId = String.valueOf(params.get("serverId"));
        if (StrUtil.isBlank(serverId) || "null".equalsIgnoreCase(serverId)) {
            throw new CustomException("请选择服务器。");
        }
        int hours = 24;
        Object hoursObj = params.get("hours");
        if (hoursObj != null && StrUtil.isNotBlank(String.valueOf(hoursObj))) {
            try {
                hours = Math.max(1, Math.min(168, Integer.parseInt(String.valueOf(hoursObj))));
            } catch (Exception ignored) {
                hours = 24;
            }
        }
        // 按采集频率估算上限：2 分钟一条，最多取 hours*30 条
        int limit = Math.min(Math.max(hours * 30, 50), 1000);
        QueryWrapper<AutoServerMetricHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoServerMetricHistory::getServerId), serverId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AutoServerMetricHistory::getCollectTime));
        queryWrapper.last("LIMIT " + limit);
        List<AutoServerMetricHistory> list = list(queryWrapper);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            AutoServerMetricHistory item = list.get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("collectTime", item.getCollectTime());
            row.put("cpuUsage", item.getCpuUsage());
            row.put("memUsage", item.getMemUsage());
            row.put("diskUsage", item.getDiskUsage());
            row.put("loadAvg", item.getLoadAvg());
            row.put("metricStatus", item.getMetricStatus());
            row.put("metricMsg", item.getMetricMsg());
            rows.add(row);
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("serverId", serverId);
        bean.put("hours", hours);
        outputObject.setBean(bean);
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    @Override
    public void cleanExpiredHistory(int keepDays) {
        int days = keepDays <= 0 ? 30 : keepDays;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        QueryWrapper<AutoServerMetricHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt(MybatisPlusUtil.toColumns(AutoServerMetricHistory::getCollectTime), expireTime);
        remove(queryWrapper);
    }
}
