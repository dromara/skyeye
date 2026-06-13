/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.portal.dao.PortalPageVisitUvDailyDao;
import com.skyeye.portal.entity.PortalPageVisitUvDaily;
import com.skyeye.portal.service.PortalPageVisitUvDailyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 官网页面访问日 UV 去重服务实现
 * <p>表：portal_page_visit_uv_daily，按 tenant_id + stat_date + visitor_id 唯一</p>
 */
@Service
@SkyeyeService(name = "官网访问日UV去重", groupName = "门户管理", tenant = TenantEnum.PLATE, manageShow = false, allowDynamicAttrKey = false)
public class PortalPageVisitUvDailyServiceImpl extends SkyeyeBusinessServiceImpl<PortalPageVisitUvDailyDao, PortalPageVisitUvDaily> implements PortalPageVisitUvDailyService {

    @Override
    public void recordDailyUv(String statDate, String visitorId, String createTime) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitUvDaily::getStatDate);
        String visitorIdColumn = MybatisPlusUtil.toColumns(PortalPageVisitUvDaily::getVisitorId);
        QueryWrapper<PortalPageVisitUvDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(statDateColumn, statDate).eq(visitorIdColumn, visitorId);
        // 当日该访客已记录则跳过
        if (count(queryWrapper) > 0) {
            return;
        }
        PortalPageVisitUvDaily entity = new PortalPageVisitUvDaily();
        entity.setStatDate(statDate);
        entity.setVisitorId(visitorId);
        entity.setCreateTime(createTime);
        createEntity(entity, StrUtil.EMPTY);
    }

    @Override
    public long countUvByStatDate(String statDate) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitUvDaily::getStatDate);
        QueryWrapper<PortalPageVisitUvDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ifnull(count(1), 0) as todayUv").eq(statDateColumn, statDate);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        if (rows == null || rows.isEmpty()) {
            return 0L;
        }
        Map<String, Object> row = rows.get(0);
        if (row == null || row.isEmpty()) {
            return 0L;
        }
        Object val = row.get("todayUv");
        if (val == null) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("todayUv")) {
                    val = entry.getValue();
                    break;
                }
            }
        }
        return NumberParseUtil.parseLong(val);
    }

    @Override
    public List<Map<String, Object>> queryDailyUvTrend(String fromDate, String toDate) {
        String statDateColumn = MybatisPlusUtil.toColumns(PortalPageVisitUvDaily::getStatDate);
        QueryWrapper<PortalPageVisitUvDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(statDateColumn + " as statDate", "count(1) as uvCount")
            .ge(statDateColumn, fromDate)
            .le(statDateColumn, toDate)
            .groupBy(statDateColumn)
            .orderByAsc(statDateColumn);
        return baseMapper.selectMaps(queryWrapper);
    }

}
