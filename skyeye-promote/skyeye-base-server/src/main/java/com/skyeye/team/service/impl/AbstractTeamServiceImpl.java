/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.team.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.team.classenum.ObjectPermissionFromType;
import com.skyeye.team.entity.AbstractTeam;
import com.skyeye.team.entity.TeamObjectPermission;
import com.skyeye.team.entity.TeamRole;
import com.skyeye.team.entity.TeamRoleUser;
import com.skyeye.team.service.AbstractTeamService;
import com.skyeye.team.service.TeamObjectPermissionService;
import com.skyeye.team.service.TeamRoleService;
import com.skyeye.team.service.TeamRoleUserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AbstractTeamServiceImpl
 * @Description: 团队管理抽象类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/13 19:24
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AbstractTeamServiceImpl<D extends SkyeyeBaseMapper<T>, T extends AbstractTeam> extends SkyeyeBusinessServiceImpl<D, T> implements AbstractTeamService<T> {

    @Autowired
    protected TeamRoleService teamRoleService;

    @Autowired
    protected TeamRoleUserService teamRoleUserService;

    @Autowired
    protected TeamObjectPermissionService teamObjectPermissionService;

    @Override
    public void createPostpose(T entity, String userId) {
        String teamId = entity.getId();
        List<TeamRole> teamRoleList = entity.getTeamRoleList();
        String serviceClassName = getServiceClassName();
        if (CollectionUtil.isNotEmpty(teamRoleList)) {
            saveRole(userId, teamId, teamRoleList, serviceClassName);
        }
        // 修改团队用户信息
        updateRoleUser(userId, teamId, teamRoleList, serviceClassName, new ArrayList<>());

        if (CollectionUtil.isNotEmpty(entity.getTeamObjectPermissionList())) {
            saveTeamOwnerPermission(teamId, serviceClassName, userId, entity.getTeamObjectPermissionList());
        }
    }

    @Override
    public void updatePostpose(T entity, String userId) {
        String teamId = entity.getId();
        List<TeamRole> newTeamRoleList = entity.getTeamRoleList();
        if (CollectionUtil.isNotEmpty(newTeamRoleList)) {
            String serviceClassName = getServiceClassName();
            T oldTeam = selectById(teamId);
            List<TeamRole> oldTeamRoleList = CollectionUtil.isEmpty(oldTeam.getTeamRoleList()) ? new ArrayList<>() : oldTeam.getTeamRoleList();
            List<String> oldRoleKeys = oldTeamRoleList.stream().map(bean -> bean.getRoleId()).collect(Collectors.toList());
            // 修改角色信息
            updateRole(userId, teamId, newTeamRoleList, serviceClassName, oldTeamRoleList, oldRoleKeys);

            // 修改团队用户信息
            updateRoleUser(userId, teamId, newTeamRoleList, serviceClassName, oldTeamRoleList);

            // 修改权限信息
            updatePermission(entity.getTeamObjectPermissionList(), oldTeam.getTeamObjectPermissionList(), teamId, serviceClassName, userId);
        } else {
            // 如果团队角色集合为空,则删除旧的团队角色和用户数据
            // 1. 删除团队模板与角色的关系
            teamRoleService.deleteRoleByTeamIds(teamId);
            // 2. 删除角色下的用户
            teamRoleUserService.deleteRoleUserByTeamIds(teamId);
            // 3. 删除团队下的权限信息
            teamObjectPermissionService.deletePermissionByTeamIds(teamId);
        }
    }

    private void updateRole(String userId, String teamId, List<TeamRole> newTeamRoleList, String serviceClassName,
                            List<TeamRole> oldTeamRoleList, List<String> oldRoleKeys) {
        List<String> newRoleKeys = newTeamRoleList.stream().map(bean -> bean.getRoleId()).collect(Collectors.toList());
        // (新数据 - 旧数据) 添加到数据库
        List<TeamRole> addTeamRoleMaps = newTeamRoleList.stream()
            .filter(item -> !oldRoleKeys.contains(item.getRoleId())).collect(Collectors.toList());
        // 新增
        if (CollectionUtil.isNotEmpty(addTeamRoleMaps)) {
            saveRole(userId, teamId, addTeamRoleMaps, serviceClassName);
        }
        // (旧数据 - 新数据) 从数据库删除
        List<TeamRole> deleteTeamRole = oldTeamRoleList.stream()
            .filter(item -> !newRoleKeys.contains(item.getRoleId())).collect(Collectors.toList());
        List<String> removeRoleIds = deleteTeamRole.stream().map(teamRole -> teamRole.getRoleId()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(removeRoleIds)) {
            teamRoleService.deleteRoleByRoleIds(removeRoleIds.toArray(new String[]{}));
            teamRoleUserService.deleteRoleUserByRoleIds(teamId, removeRoleIds.toArray(new String[]{}));
        }
    }

    private void updateRoleUser(String userId, String teamId, List<TeamRole> newTeamRoleList, String serviceClassName, List<TeamRole> oldTeamRole) {
        List<TeamRoleUser> newRoleUser = getTeamRoleUserList(newTeamRoleList);
        List<TeamRoleUser> oldRoleUser = getTeamRoleUserList(oldTeamRole);
        // 同一团队下，一个人只能属于一个角色，比较键使用 teamId + userId
        Map<String, TeamRoleUser> newRoleUserMap = newRoleUser.stream()
            .collect(Collectors.toMap(bean -> teamId + bean.getUserId(), bean -> bean, (a, b) -> b));
        Map<String, TeamRoleUser> oldRoleUserMap = oldRoleUser.stream()
            .collect(Collectors.toMap(bean -> teamId + bean.getUserId(), bean -> bean, (a, b) -> a));

        List<TeamRoleUser> addTeamRoleUser = newRoleUserMap.values().stream()
            .filter(item -> !oldRoleUserMap.containsKey(teamId + item.getUserId())).collect(Collectors.toList());
        addTeamRoleUser.forEach(p -> {
            p.setId(null);
            p.setTeamId(teamId);
            p.setTeamKey(serviceClassName);
        });
        if (CollectionUtil.isNotEmpty(addTeamRoleUser)) {
            teamRoleUserService.createEntity(addTeamRoleUser, userId);
        }

        List<TeamRoleUser> deleteRoleUser = oldRoleUserMap.values().stream()
            .filter(item -> !newRoleUserMap.containsKey(teamId + item.getUserId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(deleteRoleUser)) {
            List<String> deleteRoleUserLinkIds = deleteRoleUser.stream().map(TeamRoleUser::getId).collect(Collectors.toList());
            teamRoleUserService.deleteById(deleteRoleUserLinkIds);
        }

        List<TeamRoleUser> updateTeamRoleUser = new ArrayList<>();
        newRoleUserMap.forEach((key, newItem) -> {
            TeamRoleUser oldItem = oldRoleUserMap.get(key);
            if (oldItem != null && !Objects.equals(oldItem.getRoleId(), newItem.getRoleId())) {
                oldItem.setRoleId(newItem.getRoleId());
                updateTeamRoleUser.add(oldItem);
            }
        });
        if (CollectionUtil.isNotEmpty(updateTeamRoleUser)) {
            teamRoleUserService.updateEntity(updateTeamRoleUser, userId);
        }
    }

    private void updatePermission(List<TeamObjectPermission> newPermissionList, List<TeamObjectPermission> oldPermissionList, String teamId,
                                  String serviceClassName, String userId) {
        // 只处理 团队统一赋权 操作的数据
        oldPermissionList = oldPermissionList.stream()
            .filter(oldPermission -> oldPermission.getFromType().equals(ObjectPermissionFromType.TEAM_LINK.getKey())).collect(Collectors.toList());
        List<String> newPermissionKeys = newPermissionList.stream().map(bean -> getPermissionKey(bean)).collect(Collectors.toList());
        List<String> oldPermissionKeys = oldPermissionList.stream().map(bean -> getPermissionKey(bean)).collect(Collectors.toList());
        // 需要新增的
        List<TeamObjectPermission> addPermission = newPermissionList.stream()
            .filter(item -> !oldPermissionKeys.contains(getPermissionKey(item))).collect(Collectors.toList());
        saveTeamOwnerPermission(teamId, serviceClassName, userId, addPermission);

        // (旧数据 - 新数据) 从数据库删除
        List<TeamObjectPermission> deletePermission = oldPermissionList.stream()
            .filter(item -> !newPermissionKeys.contains(getPermissionKey(item))).collect(Collectors.toList());
        List<String> removePermissionIds = deletePermission.stream().map(TeamObjectPermission::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(removePermissionIds)) {
            teamObjectPermissionService.deleteById(removePermissionIds);
        }
    }

    private String getPermissionKey(TeamObjectPermission permission) {
        return String.format(Locale.ROOT, "%s_%s_%s_%s", permission.getPermissionValue(), permission.getPermissionKey(),
            permission.getOwnerId(), permission.getOwnerKey());
    }

    private List<TeamRoleUser> getTeamRoleUserList(List<TeamRole> teamRoleList) {
        if (CollectionUtil.isEmpty(teamRoleList)) {
            return CollectionUtil.newArrayList();
        }
        return teamRoleList.stream()
            .filter(teamRole -> CollectionUtil.isNotEmpty(teamRole.getTeamRoleUserList()))
            .flatMap(teamRole -> {
                teamRole.getTeamRoleUserList().forEach(teamRoleUser -> {
                    teamRoleUser.setRoleId(teamRole.getRoleId());
                });
                return teamRole.getTeamRoleUserList().stream();
            })
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void saveTeamOwnerPermission(String teamId, String serviceClassName, String userId, List<TeamObjectPermission> addPermission) {
        addPermission.forEach(bean -> {
            bean.setTeamId(teamId);
            bean.setTeamKey(serviceClassName);
            bean.setFromType(ObjectPermissionFromType.TEAM_LINK.getKey());
        });
        teamObjectPermissionService.createEntity(addPermission, userId);
    }

    private void saveRole(String userId, String teamId, List<TeamRole> teamRoleList, String serviceClassName) {
        teamRoleList.forEach(teamRole -> {
            teamRole.setTeamId(teamId);
            teamRole.setTeamKey(serviceClassName);
        });
        teamRoleService.createEntity(teamRoleList, userId);
    }

    @Override
    public T getDataFromDb(String id) {
        T team = super.getDataFromDb(id);
        Map<String, List<TeamRole>> teamRoleMap = teamRoleService.queryTeamRoleByTeamIds(team.getId());
        team.setTeamRoleList(teamRoleMap.get(team.getId()));
        List<TeamObjectPermission> teamObjectPermissionList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(team.getTeamRoleList())) {
            // 查询角色权限
            List<String> ownerIds = team.getTeamRoleList().stream().map(TeamRole::getRoleId).collect(Collectors.toList());
            // 查询用户权限
            List<String> userIds = team.getTeamRoleList().stream()
                .filter(teamRole -> CollectionUtil.isNotEmpty(teamRole.getTeamRoleUserList()))
                .flatMap(teamRole -> teamRole.getTeamRoleUserList().stream())
                .filter(bean -> !ToolUtil.isBlank(bean.getUserId()))
                .map(bean -> bean.getUserId()).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(userIds)) {
                ownerIds.addAll(userIds);
            }
            List<TeamObjectPermission> authermissionList = teamObjectPermissionService.queryPermissionByTeamId(team.getId(), ownerIds);
            teamObjectPermissionList.addAll(authermissionList);
        }
        team.setTeamObjectPermissionList(teamObjectPermissionList);
        return team;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除团队下的角色信息
        teamRoleService.deleteRoleByTeamIds(id);
        // 删除团队下的用户信息
        teamRoleUserService.deleteRoleUserByTeamIds(id);
        // 删除团队下的权限信息
        teamObjectPermissionService.deletePermissionByTeamIds(id);
    }

    /**
     * 查询团队模板详情信息
     *
     * @param inputObject
     * @param outputObject
     */
    @Override
    public void queryTeamMation(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        T team = super.selectById(id);
        setOtherName(team);
        team.setChargeUserMation(iAuthUserService.queryDataMationById(team.getChargeUser()));
        outputObject.setBean(team);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    public void setOtherName(T team) {
        teamRoleService.setUserRoleName(team.getTeamRoleList());
        teamRoleUserService.setUserName(team.getTeamRoleList());
    }

}
