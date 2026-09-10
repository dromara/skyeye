/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.menu.dao.AppWorkPageDao;
import com.skyeye.menu.dao.SysEveMenuDao;
import com.skyeye.menu.entity.AuthPoint;
import com.skyeye.menu.service.AuthPointService;
import com.skyeye.role.dao.SysEveRoleDao;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.SysEveRoleAppPageAuthService;
import com.skyeye.role.service.SysEveRoleAppPageService;
import com.skyeye.role.service.SysEveRoleMenuService;
import com.skyeye.role.service.SysEveRoleService;
import com.skyeye.tenant.classenum.TenantAppMenuType;
import com.skyeye.tenant.service.TenantService;
import com.skyeye.win.service.SysEveDesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveRoleServiceImpl
 * @Description: 角色管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "角色管理", groupName = "角色管理")
public class SysEveRoleServiceImpl extends SkyeyeBusinessServiceImpl<SysEveRoleDao, Role> implements SysEveRoleService {

    @Autowired
    private SysEveMenuDao sysEveMenuDao;

    @Autowired
    private AppWorkPageDao appWorkPageDao;

    @Autowired
    private SysEveDesktopService sysEveDesktopService;

    @Autowired
    private SysEveRoleMenuService sysEveRoleMenuService;

    @Autowired
    private SysEveRoleAppPageService sysEveRoleAppPageService;

    @Autowired
    private SysEveRoleAppPageAuthService sysEveRoleAppPageAuthService;

    @Autowired
    private AuthPointService authPointService;

    @Autowired
    private TenantService tenantService;

    /**
     * 查询全部角色列表（不分页），供适用对象等下拉选择使用。
     */
    @Override
    public void queryAllSysRoleList(InputObject inputObject, OutputObject outputObject) {
        List<Role> roleList = queryAllData();
        outputObject.setBeans(roleList);
        outputObject.settotal(roleList.size());
    }

    @Override
    public Role getDataFromDb(String id) {
        Role role = super.getDataFromDb(id);
        // 获取桌面信息
        List<Map<String, Object>> desktopList = sysEveDesktopService.queryAllDataForMap();
        // 1. 获取PC端菜单权限--强隔离
        List<String> menuAuthPointIds = sysEveRoleMenuService.querySysRoleMenuIdByRoleId(id);
        role.setMenuIds(menuAuthPointIds);
        // 根据menuIds从desktopList获取桌面信息的id集合
        List<String> pcDesktopIds = desktopList.stream().filter(desktop -> menuAuthPointIds.contains(desktop.get("id").toString()))
            .map(desktop -> desktop.get("id").toString()).collect(Collectors.toList());
        role.setPcDesktopId(pcDesktopIds);
        // 获取权限点信息
        List<AuthPoint> authPoints = authPointService.selectByIds(menuAuthPointIds.toArray(new String[]{}));
        if (CollectionUtil.isNotEmpty(authPoints)) {
            List<String> ids = authPoints.stream().map(AuthPoint::getId).collect(Collectors.toList());
            role.setPcAuthId(ids);
            role.setPcAuthNum(JSONUtil.toList(JSONUtil.toJsonStr(authPoints), null));
        }

        // 2. 获取APP端菜单权限--强隔离
        List<String> appMenuIds = new ArrayList<>();
        List<String> appMenuIdList = sysEveRoleAppPageService.querySysRoleAppPageIdByRoleId(id);
        role.setAppMenuId(appMenuIdList);
        // 根据appMenuIdList从desktopList获取APP端菜单信息的id集合
        List<String> appDesktopIds = desktopList.stream().filter(desktop -> appMenuIdList.contains(desktop.get("id").toString()))
            .map(desktop -> desktop.get("id").toString()).collect(Collectors.toList());
        role.setAppDesktopId(appDesktopIds);
        List<String> appAuthIdList = sysEveRoleAppPageAuthService.queryRoleAppPageAuthByRoleId(id);
        role.setAppAuthId(appAuthIdList);
        if (CollectionUtil.isNotEmpty(appMenuIdList)) {
            appMenuIds.addAll(appMenuIdList);
        }
        if (CollectionUtil.isNotEmpty(appAuthIdList)) {
            appMenuIds.addAll(appAuthIdList);
        }
        role.setAppMenuIds(appMenuIds);
        // 获取权限点信息
        List<AuthPoint> appAauthPoints = authPointService.selectByIds(appAuthIdList.toArray(new String[]{}));
        if (CollectionUtil.isNotEmpty(appAauthPoints)) {
            role.setAppAuthNum(JSONUtil.toList(JSONUtil.toJsonStr(appAauthPoints), null));
        }
        return role;
    }

    @Override
    public List<Role> getDataFromDb(List<String> idList) {
        List<Role> roles = super.getDataFromDb(idList);
        // 获取桌面信息
        List<Map<String, Object>> desktopList = sysEveDesktopService.queryAllDataForMap();
        // 获取PC端菜单权限
        Map<String, List<String>> pcListMap = sysEveRoleMenuService.querySysRoleMenuIdByRoleIds(idList);
        // 获取APP端菜单权限
        Map<String, List<String>> appListMap = sysEveRoleAppPageService.querySysRoleAppPageIdByRoleIds(idList);
        // 获取APP端权限点
        Map<String, List<String>> pointListMap = sysEveRoleAppPageAuthService.queryRoleAppPageAuthByRoleIds(idList);

        for (Role role : roles) {
            // 1. 设置PC端菜单权限
            if (CollectionUtil.isNotEmpty(pcListMap.get(role.getId()))) {
                role.setMenuIds(pcListMap.get(role.getId()));
                // 根据menuIds从desktopList获取桌面信息的id集合
                List<String> pcDesktopIds = desktopList.stream().filter(desktop -> pcListMap.get(role.getId()).contains(desktop.get("id").toString()))
                    .map(desktop -> desktop.get("id").toString()).collect(Collectors.toList());
                role.setPcDesktopId(pcDesktopIds);

                // 获取权限点信息
                List<AuthPoint> authPoints = authPointService.selectByIds(pcListMap.get(role.getId()).toArray(new String[]{}));
                if (CollectionUtil.isNotEmpty(authPoints)) {
                    List<String> ids = authPoints.stream().map(AuthPoint::getId).collect(Collectors.toList());
                    role.setPcAuthId(ids);
                    role.setPcAuthNum(JSONUtil.toList(JSONUtil.toJsonStr(authPoints), null));
                }
            }
            // 2. 设置APP端菜单权限
            // 合并APP端菜单权限和权限点
            List<String> appMenuIds = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(appListMap.get(role.getId()))) {
                role.setAppMenuId(appListMap.get(role.getId()));
                appMenuIds.addAll(appListMap.get(role.getId()));
                // 根据appMenuIdList从desktopList获取APP端菜单信息的id集合
                List<String> appDesktopIds = desktopList.stream().filter(desktop -> appListMap.get(role.getId()).contains(desktop.get("id").toString()))
                    .map(desktop -> desktop.get("id").toString()).collect(Collectors.toList());
                role.setAppDesktopId(appDesktopIds);
            }
            if (CollectionUtil.isNotEmpty(pointListMap.get(role.getId()))) {
                role.setAppAuthId(pointListMap.get(role.getId()));
                appMenuIds.addAll(pointListMap.get(role.getId()));
                // 获取权限点信息
                List<AuthPoint> appAauthPoints = authPointService.selectByIds(pointListMap.get(role.getId()).toArray(new String[]{}));
                if (CollectionUtil.isNotEmpty(appAauthPoints)) {
                    role.setAppAuthNum(JSONUtil.toList(JSONUtil.toJsonStr(appAauthPoints), null));
                }
            }
            role.setAppMenuIds(appMenuIds);
        }
        return roles;
    }

    @Override
    @IgnoreTenant
    public void querySysRoleBandMenuList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = sysEveMenuDao.queryAllMenuList();
        // 获取桌面信息
        List<Map<String, Object>> desktopList = sysEveDesktopService.queryAllDataForMap();
        beans.addAll(desktopList);

        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
                // 开启租户功能，并且不是平台租户
                List<String> ids = tenantService.queryAllMenuListByTenantId(tenantId, TenantAppMenuType.PC.getKey());
                if (CollectionUtil.isEmpty(ids)) {
                    return;
                }
                beans = beans.stream().filter(bean -> ids.contains(bean.get("id").toString())).collect(Collectors.toList());
            }
        }

        String[] str;
        for (Map<String, Object> bean : beans) {
            str = bean.get("pId").toString().split(",");
            bean.put("pId", str[str.length - 1]);
        }
        outputObject.setBeans(beans);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSysRolePCAuth(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String roleId = map.get("id").toString();
        // 保存角色菜单关联表信息
        sysEveRoleMenuService.createRoleMenu(roleId, (List<String>) map.get("menuIds"),
            user.get("id").toString(), DateUtil.getTimeAndToString());
        refreshCache(roleId);
    }

    @Override
    public void deletePostpose(String id) {
        // 删除角色菜单关联表信息
        sysEveRoleMenuService.deleteByRoleId(id);
        // 删除角色APP菜单关联表信息
        sysEveRoleAppPageService.deleteByRoleId(id);
        // 删除角色权限点关联表信息
        sysEveRoleAppPageAuthService.deleteByRoleId(id);
    }

    @Override
    public void querySysRoleBandAppMenuList(InputObject inputObject, OutputObject outputObject) {
        // 获取APP端菜单信息--默认弱隔离
        List<Map<String, Object>> beans = appWorkPageDao.queryAllAppMenuList();
        // 获取桌面信息
        List<Map<String, Object>> desktopList = sysEveDesktopService.queryAllDataForMap();
        beans.addAll(desktopList);

        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
                List<String> ids = tenantService.queryAllMenuListByTenantId(tenantId, TenantAppMenuType.APP.getKey());
                if (CollectionUtil.isEmpty(ids)) {
                    return;
                }
                beans = beans.stream().filter(bean -> ids.contains(bean.get("id").toString())).collect(Collectors.toList());
            }
        }

        outputObject.setBeans(beans);
    }

    /**
     * 手机端菜单授权
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSysRoleAppMenuById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String roleId = map.get("id").toString();
        // 保存角色APP菜单关联表信息
        String[] menuIds = map.get("menuIds").toString().split(",");
        if (menuIds.length == 0) {
            throw new CustomException("请选择该角色即将拥有的权限！");
        }
        sysEveRoleAppPageService.createRoleAppPage(roleId, Arrays.asList(menuIds));

        // 保存角色权限点关联表信息
        sysEveRoleAppPageAuthService.createRoleAppPageAuth(roleId, Arrays.asList(map.get("pointIds").toString().split(",")));

        refreshCache(roleId);
    }

}
