/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.portal.dao.PortalPageVisitLogDao;
import com.skyeye.portal.entity.PortalPageVisitLog;
import com.skyeye.portal.service.PortalPageVisitLogService;
import com.skyeye.portal.service.PortalPageVisitStatDailyService;
import com.skyeye.portal.service.PortalPageVisitUvDailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 官网页面访问统计（租户平台隔离）
 * <p>上报：明细表 portal_page_visit_log + 日汇总表（PV/UV）</p>
 * <p>报表：概览/趋势/热门页读日汇总表，明细分页读明细表</p>
 */
@Slf4j
@Service
@SkyeyeService(name = "官网访问统计", groupName = "门户管理", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class PortalPageVisitLogServiceImpl extends SkyeyeBusinessServiceImpl<PortalPageVisitLogDao, PortalPageVisitLog> implements PortalPageVisitLogService {

    /**
     * 统计默认天数
     */
    private static final int DEFAULT_STAT_DAYS = 7;
    /**
     * 热门页默认 Top 条数
     */
    private static final int DEFAULT_TOP_N = 10;
    /**
     * 热门页最大 Top 条数
     */
    private static final int MAX_TOP_N = 100;

    @Autowired
    private PortalPageVisitStatDailyService portalPageVisitStatDailyService;

    @Autowired
    private PortalPageVisitUvDailyService portalPageVisitUvDailyService;

    @Override
    public QueryWrapper<PortalPageVisitLog> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<PortalPageVisitLog> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(PortalPageVisitLog::getCreateTime));
        return queryWrapper;
    }

    /**
     * 记录官网页面访问：
     * - 补全 visitTime、IP、UA、Referer
     * - 同步累加日 PV、记录日 UV（去重）
     * - 写入访问明细供列表查询
     */
    @Override
    public void recordPortalPageVisit(InputObject inputObject, OutputObject outputObject) {
        PortalPageVisitLog entity = inputObject.getParams(PortalPageVisitLog.class);
        String now = DateUtil.getTimeAndToString();
        entity.setVisitTime(now);
        entity.setCreateTime(now);
        HttpServletRequest request = InputObject.getRequest();
        if (request != null) {
            entity.setClientIp(ToolUtil.getIpByRequest(request));
            if (StrUtil.isBlank(entity.getUserAgent())) {
                entity.setUserAgent(request.getHeader("User-Agent"));
            }
            if (StrUtil.isBlank(entity.getReferrer())) {
                entity.setReferrer(request.getHeader("Referer"));
            }
        }
        String statDate = now.length() >= 10 ? now.substring(0, 10) : LocalDateTime.now().toLocalDate().toString();
        if (StrUtil.isNotBlank(entity.getPagePath())) {
            portalPageVisitStatDailyService.incrPagePv(statDate, entity.getPagePath(), entity.getPageName(), now);
        }
        if (StrUtil.isNotBlank(entity.getVisitorId())) {
            portalPageVisitUvDailyService.recordDailyUv(statDate, entity.getVisitorId(), now);
        }
        createEntity(entity, StrUtil.EMPTY);
        outputObject.setBean(entity);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 概览统计：
     * - 输入：startTime/endTime（可选），为空时默认最近7天
     * - 输出：totalCount/todayCount/todayUv
     */
    @Override
    public void queryPortalVisitOverviewStat(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        // 统计时间窗口：前端传 startTime/endTime；缺失时默认最近7天
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);
        String today = LocalDateTime.now().toLocalDate().toString();
        long totalCount = portalPageVisitStatDailyService.sumPvByDateRange(fromDate, toDate);
        long todayCount = portalPageVisitStatDailyService.sumPvByStatDate(today);
        long todayUv = portalPageVisitUvDailyService.countUvByStatDate(today);
        Map<String, Object> result = new LinkedHashMap<>();
        // startTime: 统计开始时间
        result.put("startTime", fromTime);
        // endTime: 统计结束时间
        result.put("endTime", endTime);
        // totalCount: 时间窗口内全站 PV 总量（日汇总表）
        result.put("totalCount", totalCount);
        // todayCount: 今日全站 PV
        result.put("todayCount", todayCount);
        // todayUv: 今日全站 UV（按 visitor_id 去重）
        result.put("todayUv", todayUv);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 趋势统计（按天）：
     * - 输入：startTime/endTime（可选），为空时默认最近7天
     * - 输出：xAxisData(日期)、seriesData(PV)、uvSeriesData(UV)
     */
    @Override
    public void queryPortalVisitTrendStat(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);
        List<Map<String, Object>> pvRows = portalPageVisitStatDailyService.queryDailyPvTrend(fromDate, toDate);
        List<Map<String, Object>> uvRows = portalPageVisitUvDailyService.queryDailyUvTrend(fromDate, toDate);
        Map<String, Map<String, Object>> pvDayMap = new HashMap<>();
        if (pvRows != null) {
            for (Map<String, Object> row : pvRows) {
                pvDayMap.put(String.valueOf(row.get("statDate")), row);
            }
        }
        Map<String, Map<String, Object>> uvDayMap = new HashMap<>();
        if (uvRows != null) {
            for (Map<String, Object> row : uvRows) {
                uvDayMap.put(String.valueOf(row.get("statDate")), row);
            }
        }
        // 补齐无数据日期为 0，保证与 xAxisData 一一对应
        List<String> dayList = DateUtil.getDays(fromDate, toDate);
        List<Long> pvSeriesData = new ArrayList<>();
        List<Long> uvSeriesData = new ArrayList<>();
        long total = 0L;
        for (String day : dayList) {
            Map<String, Object> pvRow = pvDayMap.get(day);
            Map<String, Object> uvRow = uvDayMap.get(day);
            long pvCount = pvRow == null ? 0L : NumberParseUtil.parseLong(pvRow.get("pvCount"));
            long uvCount = uvRow == null ? 0L : NumberParseUtil.parseLong(uvRow.get("uvCount"));
            total += pvCount;
            pvSeriesData.add(pvCount);
            uvSeriesData.add(uvCount);
        }
        Map<String, Object> result = new HashMap<>();
        // total: 当前趋势窗口内 PV 总量
        result.put("total", total);
        // xAxisData: 横轴日期列表（yyyy-MM-dd）
        result.put("xAxisData", dayList);
        // seriesData: 每日 PV 序列（与 xAxisData 一一对应）
        result.put("seriesData", pvSeriesData);
        // uvSeriesData: 每日 UV 序列
        result.put("uvSeriesData", uvSeriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 热门页面统计（按页面路径聚合）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(页面名)、seriesData(浏览量)、detailList
     */
    @Override
    public void queryPortalVisitTopPageStat(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        // topN: 返回前 N 个页面，默认10，范围 [1, 100]
        int topN = NumberParseUtil.parseInt(params.getCustomParamsMapStr("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);
        List<Map<String, Object>> rows = portalPageVisitStatDailyService.queryTopPagePv(fromDate, toDate, topN);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String pagePath = String.valueOf(row.get("pagePath"));
                if (StrUtil.isBlank(pagePath) || "null".equalsIgnoreCase(pagePath)) {
                    pagePath = "其他";
                }
                String pageName = row.get("pageName") == null ? null : String.valueOf(row.get("pageName"));
                String label = StrUtil.isNotBlank(pageName) && !"null".equalsIgnoreCase(pageName)
                    ? pageName : pagePath;
                long totalCount = NumberParseUtil.parseLong(row.get("totalCount"));
                total += totalCount;
                xAxisData.add(label);
                seriesData.add(totalCount);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top 列表中的 PV 合计（非全站总量）
        result.put("total", total);
        // xAxisData: 页面展示名称（pageName 为空时回退 pagePath）
        result.put("xAxisData", xAxisData);
        // seriesData: 各页面 PV 序列
        result.put("seriesData", seriesData);
        // detailList: 聚合明细（pagePath/pageName/totalCount）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
