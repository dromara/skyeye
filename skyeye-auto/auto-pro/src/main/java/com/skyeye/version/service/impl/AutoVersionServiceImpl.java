/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.version.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.version.classenum.AutoVersionAuthEnum;
import com.skyeye.version.classenum.AutoVersionState;
import com.skyeye.version.dao.AutoVersionDao;
import com.skyeye.version.entity.AutoVersion;
import com.skyeye.version.service.AutoVersionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: AutoVersionServiceImpl
 * @Description: 版本管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 9:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "版本管理", groupName = "版本管理", teamAuth = true, allowDynamicAttrKey = false)
public class AutoVersionServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoVersionDao, AutoVersion> implements AutoVersionService {

    @Override
    public Class getAuthEnumClass() {
        return AutoVersionAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoVersionAuthEnum.ADD.getKey(), AutoVersionAuthEnum.EDIT.getKey(), AutoVersionAuthEnum.DELETE.getKey());
    }

    @Override
    protected QueryWrapper<AutoVersion> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoVersion> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVersion::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVersion::getObjectKey), commonPageInfo.getObjectKey());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(AutoVersion entity) {
        if (StrUtil.equals(entity.getState(), AutoVersionState.PROGRESS.getKey())) {
            QueryWrapper<AutoVersion> queryWrapper = new QueryWrapper();
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVersion::getObjectId), entity.getObjectId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVersion::getState), entity.getState());
            if (StringUtils.isNotEmpty(entity.getId())) {
                queryWrapper.ne(CommonConstants.ID, entity.getId());
            }
            AutoVersion autoVersion = getOne(queryWrapper, false);
            if (ObjectUtil.isNotEmpty(autoVersion)) {
                throw new CustomException("该项目存在进行中的版本，请先结束该版本。");
            }
        }
    }

    @Override
    public void queryAutoVersionByObjectId(InputObject inputObject, OutputObject outputObject) {
        String objectId = inputObject.getParams().get("objectId").toString();
        QueryWrapper<AutoVersion> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVersion::getObjectId), objectId);
        List<AutoVersion> result = list(queryWrapper);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }
}