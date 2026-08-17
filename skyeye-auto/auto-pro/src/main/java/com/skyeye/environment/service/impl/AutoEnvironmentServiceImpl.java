/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.environment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.classenum.AutoEnvironmentAuthEnum;
import com.skyeye.environment.dao.AutoEnvironmentDao;
import com.skyeye.environment.entity.AutoEnvironment;
import com.skyeye.environment.service.AutoEnvironmentService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoEnvironmentServiceImpl
 * @Description: 环境管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "环境管理", groupName = "环境管理", teamAuth = true)
public class AutoEnvironmentServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoEnvironmentDao, AutoEnvironment> implements AutoEnvironmentService {

    @Override
    public Class getAuthEnumClass() {
        return AutoEnvironmentAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoEnvironmentAuthEnum.ADD.getKey(), AutoEnvironmentAuthEnum.EDIT.getKey(), AutoEnvironmentAuthEnum.DELETE.getKey());
    }

    @Override
    protected QueryWrapper<AutoEnvironment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoEnvironment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoEnvironment::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoEnvironment::getObjectKey), commonPageInfo.getObjectKey());
        return queryWrapper;
    }

    @Override
    public void queryAllAutoEnvironmentList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        QueryWrapper<AutoEnvironment> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoEnvironment::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoEnvironment::getObjectKey), objectKey);
        List<AutoEnvironment> result = list(queryWrapper);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }
}