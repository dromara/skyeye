/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.AnalysisDataToMapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.echarts.dao.ReportModelAttrDao;
import com.skyeye.echarts.entity.ReportModelAttr;
import com.skyeye.echarts.service.ReportModelAttrService;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportModelAttrServiceImpl
 * @Description: Echarts报表模型属性服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 15:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "Echarts报表模型属性", groupName = "Echarts报表模型属性", manageShow = false)
public class ReportModelAttrServiceImpl extends SkyeyeBusinessServiceImpl<ReportModelAttrDao, ReportModelAttr> implements ReportModelAttrService {

    @Override
    public void saveList(String reportModelId, List<ReportModelAttr> beans) {
        deleteByReportModelId(reportModelId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (int i = 0; i < beans.size(); i++) {
                ReportModelAttr reportModelAttr = beans.get(i);
                reportModelAttr.setReportModelId(reportModelId);
                if (reportModelAttr.getOrderBy() == null) {
                    reportModelAttr.setOrderBy(i + 1);
                }
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public Map<String, List<ReportModelAttr>> queryReportModelAttrMapByModelIds(List<String> reportModelIds) {
        QueryWrapper<ReportModelAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ReportModelAttr::getReportModelId), reportModelIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ReportModelAttr::getOrderBy));
        List<ReportModelAttr> reportModelAttrList = list(queryWrapper);
        reportModelAttrList.forEach(reportModelAttr -> {
            if (AnalysisDataToMapUtil.isJsonStringArray(reportModelAttr.getDefaultValue())) {
                reportModelAttr.setDefaultValue(JSONUtil.toJsonStr(JSONArray.fromObject(reportModelAttr.getDefaultValue())));
            }
        });
        return reportModelAttrList.stream().collect(Collectors.groupingBy(ReportModelAttr::getReportModelId));
    }

    @Override
    public List<ReportModelAttr> queryReportModelAttrMapByModelId(String reportModelId) {
        QueryWrapper<ReportModelAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReportModelAttr::getReportModelId), reportModelId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ReportModelAttr::getOrderBy));
        List<ReportModelAttr> reportModelAttrList = list(queryWrapper);
        reportModelAttrList.forEach(reportModelAttr -> {
            if (AnalysisDataToMapUtil.isJsonStringArray(reportModelAttr.getDefaultValue())) {
                reportModelAttr.setDefaultValue(JSONUtil.toJsonStr(JSONArray.fromObject(reportModelAttr.getDefaultValue())));
            }
        });
        return reportModelAttrList;
    }

    @Override
    public void deleteByReportModelId(String reportModelId) {
        QueryWrapper<ReportModelAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReportModelAttr::getReportModelId), reportModelId);
        remove(queryWrapper);
    }

    @Override
    public void deleteByReportModelIds(List<String> reportModelIds) {
        if (CollectionUtil.isEmpty(reportModelIds)) {
            return;
        }
        QueryWrapper<ReportModelAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ReportModelAttr::getReportModelId), reportModelIds);
        remove(queryWrapper);
    }

}
