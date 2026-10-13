package com.skyeye.eve.assets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.dao.AssetDepreciationDetailDao;
import com.skyeye.eve.assets.entity.AssetDepreciationDetail;
import com.skyeye.eve.assets.service.AssetDepreciationDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "固定资产月结折旧明细", groupName = "资产模块")
public class AssetDepreciationDetailServiceImpl
    extends SkyeyeBusinessServiceImpl<AssetDepreciationDetailDao, AssetDepreciationDetail>
    implements AssetDepreciationDetailService {

    @Override
    public List<AssetDepreciationDetail> selectByRunId(String runId) {
        QueryWrapper<AssetDepreciationDetail> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationDetail::getRunId), runId);
        return list(qw);
    }

    @Override
    public void deleteByRunId(String runId) {
        QueryWrapper<AssetDepreciationDetail> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(AssetDepreciationDetail::getRunId), runId);
        remove(qw);
    }
}
