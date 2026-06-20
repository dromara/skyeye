/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.team.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.clazz.service.SkyeyeClassEnumService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.team.dao.TeamBusinessDao;
import com.skyeye.team.entity.TeamBusiness;
import com.skyeye.team.entity.TeamRoleUser;
import com.skyeye.team.entity.TeamTemplate;
import com.skyeye.team.service.ITeamBusinessService;
import com.skyeye.team.service.TeamBusinessService;
import com.skyeye.team.service.TeamTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TeamBusinessServiceImpl
 * @Description: 团队管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/13 19:37
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "业务团队管理", groupName = "团队管理")
public class TeamBusinessServiceImpl extends AbstractTeamServiceImpl<TeamBusinessDao, TeamBusiness> implements TeamBusinessService {

    @Autowired
    private TeamTemplateService teamTemplateService;

    @Autowired
    private ITeamBusinessService iTeamBusinessService;

    @Autowired
    private SkyeyeClassEnumService skyeyeClassEnumService;

    /**
     * 根据团队模板生成团队信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String teamTemplateId = params.get("teamTemplateId").toString();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        TeamTemplate teamTemplate = teamTemplateService.selectById(teamTemplateId);
        if (teamTemplate == null) {
            throw new CustomException("该团队模板不存在.");
        }
        TeamBusiness teamBusiness = Convert.convert(TeamBusiness.class, teamTemplate);
        teamBusiness.setTeamTemplateId(teamTemplateId);
        teamBusiness.setObjectId(objectId);
        teamBusiness.setObjectKey(objectKey);
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(teamBusiness, userId);
        // 设置该模板为启用中
        teamTemplateService.setUsed(teamTemplateId);
        outputObject.setBean(teamBusiness);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 根据业务对象id获取团队信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        TeamBusiness teamBusiness = selectById(objectId);
        if (teamBusiness == null) {
            throw new CustomException("该团队不存在.");
        }
        setOtherName(teamBusiness);
        // 设置名称
        TeamTemplate teamTemplate = teamTemplateService.selectById(teamBusiness.getTeamTemplateId());
        teamBusiness.setName(teamTemplate.getName());
        teamBusiness.setChargeUserMation(iAuthUserService.queryDataMationById(teamBusiness.getChargeUser()));
        outputObject.setBean(teamBusiness);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 根据业务对象id删除团队信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        TeamBusiness teamBusiness = selectById(objectId);
        if (teamBusiness != null) {
            deleteById(teamBusiness.getId());
        }
    }

    /**
     * 校验团队权限信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void checkTeamBusinessAuthPermission(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String enumKey = params.get("enumKey").toString();
        String enumClassName = params.get("enumClassName").toString();
        // 获取枚举类的数据
        List<Map<String, Object>> enumDataList = skyeyeClassEnumService.queryEnumDataList(enumClassName, StrUtil.EMPTY, StrUtil.EMPTY);
        List<String> enumDataId = enumDataList.stream().map(bean -> bean.get(CommonConstants.ID).toString()).collect(Collectors.toList());

        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        Map<String, Boolean> checkAuthPermission = iTeamBusinessService.checkAuthPermission(objectId, enumKey, enumDataId, userId);
        outputObject.setBean(checkAuthPermission);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 获取我所在的团队对应的团队模板id
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void getMyTeamIds(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        List<String> teamIds = teamRoleUserService.getTeamIdsByUserId(userId);
        // 根据团队id或者团队经理（当前登录用户）查询团队模板id
        QueryWrapper<TeamBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(MybatisPlusUtil.toColumns(TeamBusiness::getTeamTemplateId));
        queryWrapper.and(wrapper -> {
            wrapper.eq(MybatisPlusUtil.toColumns(TeamBusiness::getChargeUser), userId);
            if (CollectionUtil.isNotEmpty(teamIds)) {
                wrapper.or().in(CommonConstants.ID, teamIds);
            }
        });
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(TeamBusiness::getTeamTemplateId));
        List<TeamBusiness> teamBusinessList = list(queryWrapper);
        if (CollectionUtil.isEmpty(teamBusinessList)) {
            return;
        }
        List<String> teamTemplateIds = teamBusinessList.stream()
            .map(TeamBusiness::getTeamTemplateId).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("teamTemplateIds", teamTemplateIds);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public void queryMyBusinessTeamList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isEmpty(commonPageInfo.getObjectKey())) {
            throw new CustomException("objectKey不能为空.");
        }
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        Page page = null;
        if (commonPageInfo.getIsPaging()) {
            page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        }
        // 查询TeamRoleUser::getUserId是当前登录用户的团队id 和 TeamBusiness::chargeUser是当前登录用户的团队经理的id
        MPJLambdaWrapper<TeamBusiness> wrapper = JoinWrappers.lambda("tb", TeamBusiness.class)
            .innerJoin(TeamRoleUser.class, "tru", TeamRoleUser::getTeamId, TeamBusiness::getId)
            .eq(TeamBusiness::getObjectKey, commonPageInfo.getObjectKey());
        if (tenantEnable) {
            wrapper.eq("tb." + CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
        }
        wrapper.and(itemWrapper -> itemWrapper.or().eq(TeamRoleUser::getUserId, userId).or().eq(TeamBusiness::getChargeUser, userId))
            .groupBy(TeamBusiness::getId);
        List<TeamBusiness> teamBusinessList = skyeyeBaseMapper.selectJoinList(TeamBusiness.class, wrapper);
        outputObject.setBeans(teamBusinessList);
        if (commonPageInfo.getIsPaging()) {
            outputObject.settotal(page.getTotal());
        } else {
            outputObject.settotal(teamBusinessList.size());
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void transferAllChargeUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String fromUserId = params.get("fromUserId").toString();
        String toUserId = params.get("toUserId").toString();
        if (StrUtil.equals(fromUserId, toUserId)) {
            throw new CustomException("交接人不能为本人.");
        }
        QueryWrapper<TeamBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeamBusiness::getChargeUser), fromUserId);
        List<TeamBusiness> teamList = list(queryWrapper);
        if (CollectionUtil.isEmpty(teamList)) {
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }
        List<String> chargeTeamIds = teamList.stream().map(TeamBusiness::getId).collect(Collectors.toList());
        List<String> memberTeamIds = teamRoleUserService.getTeamIdsByUserId(fromUserId);
        Set<String> clearTeamIdSet = new HashSet<>(chargeTeamIds);
        clearTeamIdSet.addAll(memberTeamIds);
        List<String> clearTeamIds = new ArrayList<>(clearTeamIdSet);

        UpdateWrapper<TeamBusiness> businessWrapper = new UpdateWrapper<>();
        businessWrapper.in(CommonConstants.ID, chargeTeamIds);
        businessWrapper.set(MybatisPlusUtil.toColumns(TeamBusiness::getChargeUser), toUserId);
        update(businessWrapper);

        QueryWrapper<TeamRoleUser> removeWrapper = new QueryWrapper<>();
        removeWrapper.in(MybatisPlusUtil.toColumns(TeamRoleUser::getTeamId), clearTeamIds);
        removeWrapper.eq(MybatisPlusUtil.toColumns(TeamRoleUser::getUserId), fromUserId);
        teamRoleUserService.remove(removeWrapper);

        clearCache(clearTeamIds);
        outputObject.settotal(teamList.size());
    }
}

