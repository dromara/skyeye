/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.menu.classenum.MenuType;
import com.skyeye.menu.entity.AppWorkPage;
import com.skyeye.menu.service.AppWorkPageService;
import com.skyeye.personnel.dao.SysEveUserMenuFavoriteDao;
import com.skyeye.personnel.entity.SysEveUserMenuFavorite;
import com.skyeye.personnel.service.SysEveUserMenuFavoriteService;
import com.skyeye.personnel.service.SysEveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveUserMenuFavoriteServiceImpl
 * @Description: 用户APP菜单收藏服务层
 */
@Service
@SkyeyeService(name = "用户APP菜单收藏", groupName = "用户个人配置信息", tenant = TenantEnum.NO_ISOLATION)
public class SysEveUserMenuFavoriteServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserMenuFavoriteDao, SysEveUserMenuFavorite>
    implements SysEveUserMenuFavoriteService {

    @Autowired
    private SysEveUserService sysEveUserService;

    @Autowired
    private AppWorkPageService appWorkPageService;

    @Autowired
    private RedisCache redisCache;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void queryUserFavoriteMenuList(InputObject inputObject, OutputObject outputObject) {
        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();
        List<Map<String, Object>> favoriteList = getFavoriteMenuListWithCache(userId, tenantId);
        outputObject.setBeans(favoriteList);
        outputObject.settotal(favoriteList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveUserFavoriteMenu(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String menuId = params.get("menuId").toString();
        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();
        validateMenuPermission(menuId);

        QueryWrapper<SysEveUserMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getMenuId), menuId);
        if (count(queryWrapper) > 0) {
            throw new CustomException("该菜单已收藏.");
        }

        QueryWrapper<SysEveUserMenuFavorite> countWrapper = buildUserQueryWrapper(userId, tenantId);
        int nextOrder = (int) count(countWrapper);

        SysEveUserMenuFavorite favorite = new SysEveUserMenuFavorite();
        favorite.setUserId(userId);
        favorite.setMenuId(menuId);
        favorite.setTenantId(tenantId);
        favorite.setOrderBy(nextOrder);
        createEntity(favorite, userId);

        clearFavoriteCache(userId, tenantId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteUserFavoriteMenuByMenuId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String menuId = params.get("menuId").toString();
        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();

        QueryWrapper<SysEveUserMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getMenuId), menuId);
        remove(queryWrapper);

        reorderFavorites(userId, tenantId);
        clearFavoriteCache(userId, tenantId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveUserFavoriteMenuOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String menuIdsStr = params.get("menuIds").toString();
        List<String> menuIds = JSON.parseArray(menuIdsStr, String.class);
        if (CollectionUtil.isEmpty(menuIds)) {
            throw new CustomException("排序数据不能为空.");
        }

        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();
        Set<String> authorizedMenuIds = getAuthorizedPageMenuIds();
        for (String menuId : menuIds) {
            if (!authorizedMenuIds.contains(menuId)) {
                throw new CustomException("存在无权限的菜单，无法保存排序.");
            }
        }

        QueryWrapper<SysEveUserMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        List<SysEveUserMenuFavorite> favoriteList = list(queryWrapper);
        Map<String, SysEveUserMenuFavorite> favoriteMap = favoriteList.stream()
            .collect(Collectors.toMap(SysEveUserMenuFavorite::getMenuId, item -> item, (a, b) -> a));

        List<SysEveUserMenuFavorite> updateList = new ArrayList<>();
        for (int i = 0; i < menuIds.size(); i++) {
            String menuId = menuIds.get(i);
            SysEveUserMenuFavorite favorite = favoriteMap.get(menuId);
            if (favorite != null) {
                favorite.setOrderBy(i);
                updateList.add(favorite);
            }
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            updateEntity(updateList, userId);
        }

        clearFavoriteCache(userId, tenantId);
    }

    private List<Map<String, Object>> getFavoriteMenuListWithCache(String userId, String tenantId) {
        String cacheKey = getCacheKey(userId, tenantId);
        List<Map<String, Object>> favoriteList = redisCache.getList(cacheKey,
            key -> queryFavoriteMenuListFromDb(userId, tenantId), RedisConstants.THIRTY_DAY_SECONDS);
        return favoriteList == null ? new ArrayList<>() : favoriteList;
    }

    private List<Map<String, Object>> queryFavoriteMenuListFromDb(String userId, String tenantId) {
        QueryWrapper<SysEveUserMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getOrderBy));
        List<SysEveUserMenuFavorite> favoriteList = list(queryWrapper);
        if (CollectionUtil.isEmpty(favoriteList)) {
            return new ArrayList<>();
        }

        Set<String> authorizedMenuIds = getAuthorizedPageMenuIds();
        List<String> invalidMenuIds = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();

        for (SysEveUserMenuFavorite favorite : favoriteList) {
            if (!authorizedMenuIds.contains(favorite.getMenuId())) {
                invalidMenuIds.add(favorite.getMenuId());
                continue;
            }
            AppWorkPage menu = appWorkPageService.selectById(favorite.getMenuId());
            if (menu == null || !MenuType.PAGE.getKey().equals(menu.getType())) {
                invalidMenuIds.add(favorite.getMenuId());
                continue;
            }
            Map<String, Object> menuMap = buildMenuMap(menu, favorite.getOrderBy());
            result.add(menuMap);
        }

        if (CollectionUtil.isNotEmpty(invalidMenuIds)) {
            QueryWrapper<SysEveUserMenuFavorite> removeWrapper = buildUserQueryWrapper(userId, tenantId);
            removeWrapper.in(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getMenuId), invalidMenuIds);
            remove(removeWrapper);
            reorderFavorites(userId, tenantId);
        }
        return result;
    }

    private Map<String, Object> buildMenuMap(AppWorkPage menu, Integer orderBy) {
        Map<String, Object> menuMap = new LinkedHashMap<>();
        menuMap.put("id", menu.getId());
        menuMap.put("name", menu.getName());
        menuMap.put("logo", menu.getLogo());
        menuMap.put("url", menu.getUrl());
        menuMap.put("pageType", menu.getPageType());
        menuMap.put("pageId", menu.getPageId());
        menuMap.put("orderBy", orderBy);
        menuMap.put("type", "page");
        return menuMap;
    }

    private void validateMenuPermission(String menuId) {
        Set<String> authorizedMenuIds = getAuthorizedPageMenuIds();
        if (!authorizedMenuIds.contains(menuId)) {
            throw new CustomException("无权限收藏该菜单.");
        }
        AppWorkPage menu = appWorkPageService.selectById(menuId);
        if (menu == null || !MenuType.PAGE.getKey().equals(menu.getType())) {
            throw new CustomException("只能收藏页面类型的菜单.");
        }
    }

    private Set<String> getAuthorizedPageMenuIds() {
        String userIdAndType = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        if (!userIdAndType.endsWith(SysUserAuthConstants.APP_IDENTIFYING)) {
            userIdAndType = userIdAndType + SysUserAuthConstants.APP_IDENTIFYING;
        }
        List<Map<String, Object>> menuTree = sysEveUserService.queryAppMenuTreeBySession(userIdAndType);
        return collectPageMenuIds(menuTree);
    }

    @SuppressWarnings("unchecked")
    private Set<String> collectPageMenuIds(List<Map<String, Object>> menuTree) {
        Set<String> menuIds = new LinkedHashSet<>();
        if (CollectionUtil.isEmpty(menuTree)) {
            return menuIds;
        }
        for (Map<String, Object> node : menuTree) {
            collectPageMenuIdsRecursive(node, menuIds);
        }
        return menuIds;
    }

    @SuppressWarnings("unchecked")
    private void collectPageMenuIdsRecursive(Map<String, Object> node, Set<String> menuIds) {
        Object type = node.get("type");
        if ("page".equals(String.valueOf(type))) {
            menuIds.add(node.get("id").toString());
        }
        Object children = node.get("children");
        if (children instanceof List) {
            for (Object child : (List<?>) children) {
                if (child instanceof Map) {
                    collectPageMenuIdsRecursive((Map<String, Object>) child, menuIds);
                }
            }
        }
    }

    private void reorderFavorites(String userId, String tenantId) {
        QueryWrapper<SysEveUserMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getOrderBy));
        List<SysEveUserMenuFavorite> favoriteList = list(queryWrapper);
        if (CollectionUtil.isEmpty(favoriteList)) {
            return;
        }
        List<SysEveUserMenuFavorite> updateList = new ArrayList<>();
        for (int i = 0; i < favoriteList.size(); i++) {
            SysEveUserMenuFavorite favorite = favoriteList.get(i);
            favorite.setOrderBy(i);
            updateList.add(favorite);
        }
        updateEntity(updateList, userId);
    }

    private QueryWrapper<SysEveUserMenuFavorite> buildUserQueryWrapper(String userId, String tenantId) {
        QueryWrapper<SysEveUserMenuFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserMenuFavorite::getTenantId), tenantId);
        return queryWrapper;
    }

    private String getBaseUserId(InputObject inputObject) {
        Map<String, Object> user = inputObject.getLogParams();
        return user.get("id").toString();
    }

    private String getTenantId() {
        return tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
    }

    private String getCacheKey(String userId, String tenantId) {
        return String.format(Locale.ROOT, CacheConstants.USER_MENU_FAVORITE_CACHE_KEY, userId, tenantId);
    }

    private void clearFavoriteCache(String userId, String tenantId) {
        jedisClientService.del(getCacheKey(userId, tenantId));
    }

}
