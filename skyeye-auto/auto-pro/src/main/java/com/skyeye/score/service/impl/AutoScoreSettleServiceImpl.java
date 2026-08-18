/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.score.dao.AutoScoreSettleDao;
import com.skyeye.score.entity.AutoScoreSettle;
import com.skyeye.score.service.AutoScoreSettleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoScoreSettleServiceImpl
 * @Description: 积分结算记录服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "积分结算", groupName = "需求积分")
public class AutoScoreSettleServiceImpl extends SkyeyeBusinessServiceImpl<AutoScoreSettleDao, AutoScoreSettle> implements AutoScoreSettleService {

    @Override
    protected QueryWrapper<AutoScoreSettle> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoScoreSettle> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreSettle::getObjectId), commonPageInfo.getObjectId());
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScoreSettle::getObjectKey), commonPageInfo.getObjectKey());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AutoScoreSettle::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setNameForMap(beans, "userId", "userName");
        return beans;
    }

}
