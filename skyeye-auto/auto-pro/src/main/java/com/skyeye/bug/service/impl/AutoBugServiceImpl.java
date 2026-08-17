/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.bug.classenum.BugAuthEnum;
import com.skyeye.bug.dao.AutoBugDao;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.bug.service.AutoBugService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.version.service.AutoVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoBugServiceImpl
 * @Description: bug管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/18 22:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "bug管理", groupName = "bug管理", teamAuth = true)
public class AutoBugServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoBugDao, AutoBug> implements AutoBugService {

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoVersionService autoVersionService;

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Override
    public Class getAuthEnumClass() {
        return BugAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(BugAuthEnum.ADD.getKey(), BugAuthEnum.EDIT.getKey(), BugAuthEnum.DELETE.getKey());
    }

    @Override
    protected QueryWrapper<AutoBug> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoBug> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getObjectKey), commonPageInfo.getObjectKey());
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("moduleId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getModuleId), commonPageInfo.getCustomParamsMapStr("moduleId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("environmentId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getEnvironmentId), commonPageInfo.getCustomParamsMapStr("environmentId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("versionId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getVersionId), commonPageInfo.getCustomParamsMapStr("versionId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        autoVersionService.setMationForMap(beans, "versionId", "versionMation");
        autoEnvironmentService.setMationForMap(beans, "environmentId", "environmentMation");
        iAuthUserService.setMationForMap(beans, "handleId", "handleMation");
        return beans;
    }

    @Override
    public void createPrepose(AutoBug entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String no = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        entity.setNo(no);
    }

    @Override
    public AutoBug selectById(String id) {
        AutoBug autoBug = super.selectById(id);
        // 设置模块信息
        autoModuleService.setDataMation(autoBug, AutoBug::getModuleId);
        // 设置处理人信息
        iAuthUserService.setDataMation(autoBug, AutoBug::getHandleId);
        return autoBug;
    }
}
