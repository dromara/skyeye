/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.browse.dao.MemberBrowseHistoryDao;
import com.skyeye.browse.dao.ShopStoreBrowseStatDailyDao;
import com.skyeye.browse.entity.MemberBrowseHistory;
import com.skyeye.browse.entity.ShopStoreBrowseStatDaily;
import com.skyeye.browse.service.ShopStoreBrowseStatDailyService;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 门店浏览日汇总：由定时任务按日落库，查询历史区间走本表，避免扫足迹明细
 */
@Slf4j
@Service
@SkyeyeService(name = "门店浏览日汇总", groupName = "会员管理", tenant = TenantEnum.NO_ISOLATION, manageShow = false)
public class ShopStoreBrowseStatDailyServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreBrowseStatDailyDao, ShopStoreBrowseStatDaily> implements ShopStoreBrowseStatDailyService {

    /**
     * 日汇总保留月数
     */
    private static final int RETAIN_MONTHS = 6;

    @Autowired
    private MemberBrowseHistoryDao memberBrowseHistoryDao;

    @Override
    @IgnoreTenant
    public void aggregateYesterdayAndClean() {
        SimpleDateFormat ymd = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        Date yesterday = DateUtil.getAfDate(new Date(), -1, "d");
        String statDate = ymd.format(yesterday);
        String dayStart = statDate + " 00:00:00";
        String dayEnd = DateUtil.getYmdTimeAndToString() + " 00:00:00";

        String storeCol = MybatisPlusUtil.toColumns(MemberBrowseHistory::getStoreId);
        String memberCol = MybatisPlusUtil.toColumns(MemberBrowseHistory::getMemberId);
        String timeCol = MybatisPlusUtil.toColumns(MemberBrowseHistory::getLastViewTime);

        QueryWrapper<MemberBrowseHistory> aggWrapper = new QueryWrapper<>();
        aggWrapper.select(storeCol + " AS storeId",
            "COUNT(DISTINCT " + memberCol + ") AS visitorCount",
            "COUNT(1) AS pvCount");
        aggWrapper.isNotNull(storeCol);
        aggWrapper.ne(storeCol, StrUtil.EMPTY);
        aggWrapper.ge(timeCol, dayStart);
        aggWrapper.lt(timeCol, dayEnd);
        aggWrapper.groupBy(storeCol);
        List<Map<String, Object>> rows = memberBrowseHistoryDao.selectMaps(aggWrapper);

        String dailyStatDateCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getStatDate);
        QueryWrapper<ShopStoreBrowseStatDaily> deleteDay = new QueryWrapper<>();
        deleteDay.eq(dailyStatDateCol, statDate);
        remove(deleteDay);

        if (CollectionUtil.isNotEmpty(rows)) {
            String now = DateUtil.getTimeAndToString();
            List<ShopStoreBrowseStatDaily> entities = new ArrayList<>(rows.size());
            for (Map<String, Object> row : rows) {
                String storeId = row.get("storeId").toString();
                if (StrUtil.isBlank(storeId)) {
                    continue;
                }
                ShopStoreBrowseStatDaily entity = new ShopStoreBrowseStatDaily();
                entity.setStoreId(storeId);
                entity.setStatDate(statDate);
                entity.setVisitorCount(row.get("visitorCount").toString());
                entity.setPvCount(row.get("pvCount").toString());
                entity.setCreateTime(now);
                entities.add(entity);
            }
            if (CollectionUtil.isNotEmpty(entities)) {
                createEntity(entities, StrUtil.EMPTY);
            }
            log.info("门店浏览日汇总完成, statDate={}, storeCount={}", statDate, entities.size());
        } else {
            log.info("门店浏览日汇总完成, statDate={}, 无数据", statDate);
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -RETAIN_MONTHS);
        String expireDate = ymd.format(cal.getTime());
        QueryWrapper<ShopStoreBrowseStatDaily> cleanWrapper = new QueryWrapper<>();
        cleanWrapper.lt(dailyStatDateCol, expireDate);
        remove(cleanWrapper);
        log.info("门店浏览日汇总清理完成, expireBefore={}", expireDate);
    }

    @Override
    @IgnoreTenant
    public Map<String, Object> sumByStoreAndDateRange(String storeId, String fromDate, String toDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("visitorCount", 0L);
        result.put("pvCount", 0L);
        if (StrUtil.isBlank(storeId) || StrUtil.isBlank(fromDate) || StrUtil.isBlank(toDate)) {
            return result;
        }
        String storeCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getStoreId);
        String dateCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getStatDate);
        String visitorCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getVisitorCount);
        String pvCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getPvCount);
        QueryWrapper<ShopStoreBrowseStatDaily> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(" + visitorCol + "),0) AS visitorCount",
            "IFNULL(SUM(" + pvCol + "),0) AS pvCount");
        wrapper.eq(storeCol, storeId);
        wrapper.ge(dateCol, fromDate);
        wrapper.le(dateCol, toDate);
        List<Map<String, Object>> rows = listMaps(wrapper);
        if (CollectionUtil.isEmpty(rows)) {
            return result;
        }
        Map<String, Object> row = rows.get(0);
        result.put("visitorCount", row.get("visitorCount"));
        result.put("pvCount", row.get("pvCount"));
        return result;
    }

    @Override
    @IgnoreTenant
    public List<Map<String, Object>> listDailyByStoreAndDateRange(String storeId, String fromDate, String toDate) {
        if (StrUtil.isBlank(storeId) || StrUtil.isBlank(fromDate) || StrUtil.isBlank(toDate)) {
            return Collections.emptyList();
        }
        String storeCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getStoreId);
        String dateCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getStatDate);
        String visitorCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getVisitorCount);
        String pvCol = MybatisPlusUtil.toColumns(ShopStoreBrowseStatDaily::getPvCount);
        QueryWrapper<ShopStoreBrowseStatDaily> wrapper = new QueryWrapper<>();
        wrapper.select(dateCol + " AS statDate",
            "IFNULL(" + visitorCol + ",0) AS visitorCount",
            "IFNULL(" + pvCol + ",0) AS pvCount");
        wrapper.eq(storeCol, storeId);
        wrapper.ge(dateCol, fromDate);
        wrapper.le(dateCol, toDate);
        wrapper.orderByAsc(dateCol);
        List<Map<String, Object>> rows = listMaps(wrapper);
        return CollectionUtil.isEmpty(rows) ? Collections.emptyList() : rows;
    }
}
