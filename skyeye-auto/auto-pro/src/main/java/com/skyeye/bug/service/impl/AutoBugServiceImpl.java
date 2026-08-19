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
import com.skyeye.bug.classenum.BugState;
import com.skyeye.bug.dao.AutoBugDao;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.bug.service.AutoBugService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.service.AutoDemandService;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.score.service.AutoScoreRecordService;
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
@SkyeyeService(name = "bug管理", groupName = "bug管理", teamAuth = true, allowDynamicAttrKey = false)
public class AutoBugServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoBugDao, AutoBug> implements AutoBugService {

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoVersionService autoVersionService;

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Autowired
    private AutoDemandService autoDemandService;

    @Autowired
    private AutoScoreRecordService autoScoreRecordService;

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
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("versionId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getVersionId), commonPageInfo.getCustomParamsMapStr("versionId"));
        }
        String type = commonPageInfo.getType();
        if (StrUtil.isEmpty(type)) {
            throw new CustomException("类型不能为空");
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (StrUtil.equals(type, "myWaitResolved")) {
            // 待我解决的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getHandleId), userId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getState), BugState.UNRESOLVED.getKey());
        } else if (StrUtil.equals(type, "myResolved")) {
            // 我已解决的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getHandleId), userId);
            queryWrapper.in(MybatisPlusUtil.toColumns(AutoBug::getState),
                Arrays.asList(BugState.TO_BE_RETURNED.getKey(), BugState.RESOLVED.getKey()));
        } else if (StrUtil.equals(type, "myHandle")) {
            // 我处理的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getHandleId), userId);
        } else if (StrUtil.equals(type, "unResolved")) {
            // 所有未解决的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getState), BugState.UNRESOLVED.getKey());
        } else if (StrUtil.equals(type, "allToBeReturned")) {
            // 所有待回归的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getState), BugState.TO_BE_RETURNED.getKey());
        } else if (StrUtil.equals(type, "toBeReturned")) {
            // 待我回归的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getState), BugState.TO_BE_RETURNED.getKey());
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getCreateId), userId);
        } else if (StrUtil.equals(type, "resolved")) {
            // 所有已解决的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getState), BugState.RESOLVED.getKey());
        } else if (StrUtil.equals(type, "myCreate")) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getCreateId), userId);
        } else if (StrUtil.equals(type, "all")) {
            // 全部 bug
        } else if (StrUtil.equals(type, "mine")) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getHandleId), userId);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        autoVersionService.setMationForMap(beans, "versionId", "versionMation");
        autoDemandService.setMationForMap(beans, "demandId", "demandMation");
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
    protected void writePostpose(AutoBug entity, String userId) {
        super.writePostpose(entity, userId);
        autoScoreRecordService.settleResolvedBug(entity, userId);
    }

    @Override
    public AutoBug selectById(String id) {
        AutoBug autoBug = super.selectById(id);
        autoModuleService.setDataMation(autoBug, AutoBug::getModuleId);
        autoVersionService.setDataMation(autoBug, AutoBug::getVersionId);
        autoDemandService.setDataMation(autoBug, AutoBug::getDemandId);
        autoEnvironmentService.setDataMation(autoBug, AutoBug::getEnvironmentId);
        iAuthUserService.setDataMation(autoBug, AutoBug::getHandleId);
        return autoBug;
    }
}
