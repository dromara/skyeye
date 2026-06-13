/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.portal.dao.PortalPageVisitStatDailyDao;
import com.skyeye.portal.entity.PortalPageVisitStatDaily;
import com.skyeye.portal.service.PortalPageVisitStatDailyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 官网页面访问日 PV 汇总服务实现
 * <p>表：portal_page_visit_stat_daily，按 tenant_id + stat_date + page_path 唯一</p>
 */
@Service
@SkyeyeService(name = "官网访问日PV汇总", groupName = "门户管理", tenant = TenantEnum.PLATE, manageShow = false, allowDynamicAttrKey = false)
public class PortalPageVisitStatDailyServiceImpl extends SkyeyeBusinessServiceImpl<PortalPageVisitStatDailyDao, PortalPageVisitStatDaily> implements PortalPageVisitStatDailyService {

    @Override
    public void incrPagePv(String statDate, String pagePath, String pageName, String createTime) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getStatDate);
        String pagePathColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPagePath);
        String pvCountColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPvCount);
        String pageNameColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPageName);
        // 已存在则 pv_count + 1
        UpdateWrapper<PortalPageVisitStatDaily> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(statDateColumn, statDate)
            .eq(pagePathColumn, pagePath)
            .setSql(pvCountColumn + " = " + pvCountColumn + " + 1");
        if (StrUtil.isNotBlank(pageName)) {
            updateWrapper.set(pageNameColumn, pageName);
        }
        Boolean updated = update(updateWrapper);
        if (updated) {
            return;
        }
        // 首次访问该页面：插入 pv_count = 1
        PortalPageVisitStatDaily entity = new PortalPageVisitStatDaily();
        entity.setStatDate(statDate);
        entity.setPagePath(pagePath);
        entity.setPageName(pageName);
        entity.setPvCount(1L);
        entity.setCreateTime(createTime);
        createEntity(entity, StrUtil.EMPTY);
    }

    @Override
    public long sumPvByDateRange(String fromDate, String toDate) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getStatDate);
        String pvCountColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPvCount);
        QueryWrapper<PortalPageVisitStatDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ifnull(sum(" + pvCountColumn + "), 0) as totalCount")
            .ge(statDateColumn, fromDate)
            .le(statDateColumn, toDate);
        return parseAggregateLong(queryWrapper, "totalCount");
    }

    @Override
    public long sumPvByStatDate(String statDate) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getStatDate);
        String pvCountColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPvCount);
        QueryWrapper<PortalPageVisitStatDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ifnull(sum(" + pvCountColumn + "), 0) as todayCount")
            .eq(statDateColumn, statDate);
        return parseAggregateLong(queryWrapper, "todayCount");
    }

    /**
     * 安全解析聚合查询结果，避免 selectMaps 空结果时 stream().findFirst() 触发 NPE
     */
    private long parseAggregateLong(QueryWrapper<PortalPageVisitStatDaily> queryWrapper, String alias) {
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        if (rows == null || rows.isEmpty()) {
            return 0L;
        }
        Map<String, Object> row = rows.get(0);
        if (row == null || row.isEmpty()) {
            return 0L;
        }
        Object val = row.get(alias);
        if (val == null) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(alias)) {
                    val = entry.getValue();
                    break;
                }
            }
        }
        return NumberParseUtil.parseLong(val);
    }

    @Override
    public List<Map<String, Object>> queryDailyPvTrend(String fromDate, String toDate) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getStatDate);
        String pvCountColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPvCount);
        QueryWrapper<PortalPageVisitStatDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(statDateColumn + " as statDate", "sum(" + pvCountColumn + ") as pvCount")
            .ge(statDateColumn, fromDate)
            .le(statDateColumn, toDate)
            .groupBy(statDateColumn)
            .orderByAsc(statDateColumn);
        return baseMapper.selectMaps(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryTopPagePv(String fromDate, String toDate, int topN) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getStatDate);
        String pagePathColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPagePath);
        String pageNameColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPageName);
        String pvCountColumn = MybatisPlusUtil.toColumns(PortalPageVisitStatDaily::getPvCount);
        QueryWrapper<PortalPageVisitStatDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                pagePathColumn + " as pagePath",
                "max(" + pageNameColumn + ") as pageName",
                "sum(" + pvCountColumn + ") as totalCount")
            .ge(statDateColumn, fromDate)
            .le(statDateColumn, toDate)
            .isNotNull(pagePathColumn)
            .groupBy(pagePathColumn)
            .orderByDesc("totalCount")
            .last("limit " + topN);
        return baseMapper.selectMaps(queryWrapper);
    }

}
