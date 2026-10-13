package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.dao.AssetDepreciationDetailDao;
import com.skyeye.eve.assets.entity.Asset;
import com.skyeye.eve.assets.entity.AssetDepreciationDetail;
import com.skyeye.eve.assets.entity.AssetReport;
import com.skyeye.eve.assets.service.AssetDepreciationDetailService;
import com.skyeye.eve.assets.service.AssetReportService;
import com.skyeye.eve.assets.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "固定资产月结折旧明细", groupName = "资产模块")
public class AssetDepreciationDetailServiceImpl
    extends SkyeyeBusinessServiceImpl<AssetDepreciationDetailDao, AssetDepreciationDetail>
    implements AssetDepreciationDetailService {

    @Autowired
    private AssetReportService assetReportService;

    @Autowired
    private AssetService assetService;

    @Override
    public List<AssetDepreciationDetail> selectByRunId(String runId) {
        QueryWrapper<AssetDepreciationDetail> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationDetail::getRunId), runId);
        List<AssetDepreciationDetail> details = list(qw);
        enrichAssetInfo(details);
        return details;
    }

    @Override
    public void deleteByRunId(String runId) {
        QueryWrapper<AssetDepreciationDetail> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationDetail::getRunId), runId);
        remove(qw);
    }

    /** 按资产明细回填名称、规格、存放区域等展示字段（与预览列对齐） */
    private void enrichAssetInfo(List<AssetDepreciationDetail> details) {
        if (CollectionUtil.isEmpty(details)) {
            return;
        }
        List<String> reportIds = details.stream()
            .map(AssetDepreciationDetail::getAssetReportId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(reportIds)) {
            return;
        }
        Map<String, AssetReport> reportMap = assetReportService.selectMapByIds(reportIds);
        List<String> assetIds = reportMap.values().stream()
            .map(AssetReport::getAssetId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Asset> assetMap = CollectionUtil.isEmpty(assetIds)
            ? Collections.emptyMap() : assetService.selectMapByIds(assetIds);
        for (AssetDepreciationDetail detail : details) {
            AssetReport report = reportMap.get(detail.getAssetReportId());
            if (report == null) {
                continue;
            }
            Asset asset = assetMap.get(report.getAssetId());
            detail.setAssetId(report.getAssetId());
            detail.setAssetName(asset == null ? StrUtil.EMPTY : StrUtil.blankToDefault(asset.getName(), StrUtil.EMPTY));
            detail.setSpecifications(asset == null ? StrUtil.EMPTY : StrUtil.blankToDefault(asset.getSpecifications(), StrUtil.EMPTY));
            detail.setStorageArea(StrUtil.blankToDefault(report.getStorageArea(), StrUtil.EMPTY));
            detail.setWarehousingTime(StrUtil.blankToDefault(report.getWarehousingTime(), StrUtil.EMPTY));
            if (StrUtil.isBlank(detail.getAssetNum())) {
                detail.setAssetNum(StrUtil.blankToDefault(report.getAssetNum(), StrUtil.EMPTY));
            }
        }
    }
}
