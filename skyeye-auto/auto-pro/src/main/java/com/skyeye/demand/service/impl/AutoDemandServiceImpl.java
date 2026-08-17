/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.classenum.AutoDemandAuthEnum;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.demand.dao.AutoDemandDao;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandService;
import com.skyeye.exception.CustomException;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.version.service.AutoVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoDemandServiceImpl
 * @Description: 需求表服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "需求管理", groupName = "需求管理", teamAuth = true, allowDynamicAttrKey = false)
public class AutoDemandServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoDemandDao, AutoDemand> implements AutoDemandService {

    @Autowired
    private AutoVersionService autoVersionService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoDemandService autoDemandService;

    @Override
    public Class getAuthEnumClass() {
        return AutoDemandAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoDemandAuthEnum.ADD.getKey(), AutoDemandAuthEnum.EDIT.getKey(), AutoDemandAuthEnum.DELETE.getKey());
    }

    @Override
    public void createPrepose(AutoDemand autoDemand) {
        Map<String, Object> business = BeanUtil.beanToMap(autoDemand);
        String no = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        autoDemand.setNo(no);
    }


    @Override
    public void deletePreExecution(AutoDemand autoDemand) {
        String state = autoDemand.getState();
        if (state.equals(AutoDemandStateEnum.INVALID.getKey()) || state.equals(AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可删除");
        }
    }

    @Override
    protected QueryWrapper<AutoDemand> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoDemand> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectId), commonPageInfo.getObjectId());
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("moduleId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getModuleId), commonPageInfo.getCustomParamsMapStr("moduleId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("versionId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getVersionId), commonPageInfo.getCustomParamsMapStr("versionId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        autoVersionService.setMationForMap(beans, "versionId", "versionMation");
        autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        iAuthUserService.setMationForMap(beans, "handleId", "handleMation");
        return beans;
    }

    @Override
    public AutoDemand selectById(String id) {
        AutoDemand autoDemand = super.selectById(id);
        autoVersionService.setDataMation(autoDemand, AutoDemand::getVersionId);
        autoModuleService.setDataMation(autoDemand, AutoDemand::getModuleId);
        iAuthUserService.setDataMation(autoDemand, AutoDemand::getHandleId);
        return autoDemand;
    }


    @Override
    public void updateStateAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        AutoDemand autoDemand = this.selectById(id);
        String state = autoDemand.getState();
        if (state.equals(AutoDemandStateEnum.INVALID.getKey()) || state.equals(AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可修改");
        } else if (state.equals(AutoDemandStateEnum.WAIT_RESEARCH.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.RESEARCH.getKey());
        } else if (state.equals(AutoDemandStateEnum.RESEARCH.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.WAIT_TEST.getKey());
        } else if (state.equals(AutoDemandStateEnum.WAIT_TEST.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.FINISH.getKey());
        } else throw new CustomException("false");
        autoDemandService.updateEntity(autoDemand, userId);
        this.refreshCache(id);
        outputObject.setBean(autoDemand);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void invalidAutoDemandById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        AutoDemand autoDemand = this.selectById(id);
        String state = autoDemand.getState();
        if (state.equals(AutoDemandStateEnum.INVALID.getKey()) || state.equals(AutoDemandStateEnum.FINISH.getKey())) {
            throw new CustomException("已完成或已作废，不可修改");
        } else if (state.equals(AutoDemandStateEnum.WAIT_RESEARCH.getKey()) || state.equals(AutoDemandStateEnum.RESEARCH.getKey()) || state.equals(AutoDemandStateEnum.WAIT_TEST.getKey())) {
            autoDemand.setState(AutoDemandStateEnum.INVALID.getKey());
        } else throw new CustomException("false");
        autoDemandService.updateEntity(autoDemand, userId);
        this.refreshCache(id);
        outputObject.setBean(autoDemand);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}
