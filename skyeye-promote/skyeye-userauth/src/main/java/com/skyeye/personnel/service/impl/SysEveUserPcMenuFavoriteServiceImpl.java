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
import com.skyeye.menu.entity.SysMenu;
import com.skyeye.menu.service.SysEveMenuService;
import com.skyeye.personnel.dao.SysEveUserPcMenuFavoriteDao;
import com.skyeye.personnel.entity.SysEveUserPcMenuFavorite;
import com.skyeye.personnel.service.SysEveUserPcMenuFavoriteService;
import com.skyeye.personnel.service.SysEveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveUserPcMenuFavoriteServiceImpl
 * @Description: 用户PC菜单收藏服务层
 */
@Service
@SkyeyeService(name = "用户PC菜单收藏", groupName = "用户个人配置信息", tenant = TenantEnum.NO_ISOLATION)
public class SysEveUserPcMenuFavoriteServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserPcMenuFavoriteDao, SysEveUserPcMenuFavorite>
    implements SysEveUserPcMenuFavoriteService {

    @Autowired
    private SysEveUserService sysEveUserService;

    @Autowired
    private SysEveMenuService sysEveMenuService;

    @Autowired
    private RedisCache redisCache;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void queryUserPcFavoriteMenuList(InputObject inputObject, OutputObject outputObject) {
        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();
        List<Map<String, Object>> favoriteList = getFavoriteMenuListWithCache(userId, tenantId);
        outputObject.setBeans(favoriteList);
        outputObject.settotal(favoriteList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveUserPcFavoriteMenu(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String menuId = params.get("menuId").toString();
        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();
        validateMenuPermission(menuId);

        QueryWrapper<SysEveUserPcMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getMenuId), menuId);
        if (count(queryWrapper) > 0) {
            throw new CustomException("该菜单已收藏.");
        }

        QueryWrapper<SysEveUserPcMenuFavorite> countWrapper = buildUserQueryWrapper(userId, tenantId);
        int nextOrder = (int) count(countWrapper);

        SysEveUserPcMenuFavorite favorite = new SysEveUserPcMenuFavorite();
        favorite.setUserId(userId);
        favorite.setMenuId(menuId);
        favorite.setTenantId(tenantId);
        favorite.setOrderBy(nextOrder);
        createEntity(favorite, userId);

        clearFavoriteCache(userId, tenantId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteUserPcFavoriteMenuByMenuId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String menuId = params.get("menuId").toString();
        String userId = getBaseUserId(inputObject);
        String tenantId = getTenantId();

        QueryWrapper<SysEveUserPcMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getMenuId), menuId);
        remove(queryWrapper);

        reorderFavorites(userId, tenantId);
        clearFavoriteCache(userId, tenantId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveUserPcFavoriteMenuOrder(InputObject inputObject, OutputObject outputObject) {
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

        QueryWrapper<SysEveUserPcMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        List<SysEveUserPcMenuFavorite> favoriteList = list(queryWrapper);
        Map<String, SysEveUserPcMenuFavorite> favoriteMap = favoriteList.stream()
            .collect(Collectors.toMap(SysEveUserPcMenuFavorite::getMenuId, item -> item, (a, b) -> a));

        List<SysEveUserPcMenuFavorite> updateList = new ArrayList<>();
        for (int i = 0; i < menuIds.size(); i++) {
            String menuId = menuIds.get(i);
            SysEveUserPcMenuFavorite favorite = favoriteMap.get(menuId);
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
        QueryWrapper<SysEveUserPcMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getOrderBy));
        List<SysEveUserPcMenuFavorite> favoriteList = list(queryWrapper);
        if (CollectionUtil.isEmpty(favoriteList)) {
            return new ArrayList<>();
        }

        Set<String> authorizedMenuIds = getAuthorizedPageMenuIds();
        List<String> invalidMenuIds = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();

        for (SysEveUserPcMenuFavorite favorite : favoriteList) {
            if (!authorizedMenuIds.contains(favorite.getMenuId())) {
                invalidMenuIds.add(favorite.getMenuId());
                continue;
            }
            SysMenu menu = sysEveMenuService.selectById(favorite.getMenuId());
            if (menu == null || !isFavoritableMenu(menu)) {
                invalidMenuIds.add(favorite.getMenuId());
                continue;
            }
            result.add(buildMenuMap(menu, favorite.getOrderBy()));
        }

        if (CollectionUtil.isNotEmpty(invalidMenuIds)) {
            QueryWrapper<SysEveUserPcMenuFavorite> removeWrapper = buildUserQueryWrapper(userId, tenantId);
            removeWrapper.in(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getMenuId), invalidMenuIds);
            remove(removeWrapper);
            reorderFavorites(userId, tenantId);
        }
        return result;
    }

    private Map<String, Object> buildMenuMap(SysMenu menu, Integer orderBy) {
        Map<String, Object> menuMap = new LinkedHashMap<>();
        menuMap.put("id", menu.getId());
        menuMap.put("name", menu.getName());
        menuMap.put("path", menu.getPath());
        menuMap.put("pageType", menu.getPageType());
        menuMap.put("pageUrl", menu.getPageUrl());
        menuMap.put("pageURL", menu.getPageUrl());
        menuMap.put("openType", menu.getOpenType());
        menuMap.put("iconType", menu.getIconType());
        menuMap.put("icon", menu.getIcon());
        menuMap.put("iconPic", menu.getIconPic());
        menuMap.put("iconColor", menu.getIconColor());
        menuMap.put("iconBg", menu.getIconBg());
        menuMap.put("type", menu.getType());
        menuMap.put("orderBy", orderBy);
        return menuMap;
    }

    private void validateMenuPermission(String menuId) {
        Set<String> authorizedMenuIds = getAuthorizedPageMenuIds();
        if (!authorizedMenuIds.contains(menuId)) {
            throw new CustomException("无权限收藏该菜单.");
        }
        SysMenu menu = sysEveMenuService.selectById(menuId);
        if (menu == null || !isFavoritableMenu(menu)) {
            throw new CustomException("只能收藏可打开的页面菜单.");
        }
    }

    private boolean isFavoritableMenu(SysMenu menu) {
        if (menu == null) {
            return false;
        }
        if ("desktop".equals(String.valueOf(menu.getType()))) {
            return false;
        }
        return StrUtil.isNotBlank(menu.getPath()) || StrUtil.isNotBlank(menu.getPageUrl());
    }

    private Set<String> getAuthorizedPageMenuIds() {
        String userIdAndType = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        // PC端收藏必须使用 PC token（去掉 APP 后缀）
        if (userIdAndType.endsWith(SysUserAuthConstants.APP_IDENTIFYING)) {
            userIdAndType = userIdAndType.substring(0, userIdAndType.length() - SysUserAuthConstants.APP_IDENTIFYING.length());
        }
        List<Map<String, Object>> menuTree = sysEveUserService.queryAppMenuTreeBySession(userIdAndType);
        return collectFavoritableMenuIds(menuTree);
    }

    @SuppressWarnings("unchecked")
    private Set<String> collectFavoritableMenuIds(List<Map<String, Object>> menuTree) {
        Set<String> menuIds = new LinkedHashSet<>();
        if (CollectionUtil.isEmpty(menuTree)) {
            return menuIds;
        }
        for (Map<String, Object> node : menuTree) {
            collectFavoritableMenuIdsRecursive(node, menuIds);
        }
        return menuIds;
    }

    @SuppressWarnings("unchecked")
    private void collectFavoritableMenuIdsRecursive(Map<String, Object> node, Set<String> menuIds) {
        if (node == null) {
            return;
        }
        Object childsObj = node.get("childs");
        List<?> childs = childsObj instanceof List ? (List<?>) childsObj : Collections.emptyList();
        if (CollectionUtil.isNotEmpty(childs)) {
            for (Object child : childs) {
                if (child instanceof Map) {
                    collectFavoritableMenuIdsRecursive((Map<String, Object>) child, menuIds);
                }
            }
            return;
        }
        String type = String.valueOf(node.get("type"));
        if ("desktop".equals(type)) {
            return;
        }
        String path = node.get("path") == null ? StrUtil.EMPTY : node.get("path").toString();
        Object pageUrlObj = node.get("pageUrl") != null ? node.get("pageUrl") : node.get("pageURL");
        String pageUrl = pageUrlObj == null ? StrUtil.EMPTY : pageUrlObj.toString();
        if (StrUtil.isNotBlank(path) || StrUtil.isNotBlank(pageUrl)) {
            Object id = node.get("id");
            if (id != null) {
                menuIds.add(id.toString());
            }
        }
    }

    private void reorderFavorites(String userId, String tenantId) {
        QueryWrapper<SysEveUserPcMenuFavorite> queryWrapper = buildUserQueryWrapper(userId, tenantId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getOrderBy));
        List<SysEveUserPcMenuFavorite> favoriteList = list(queryWrapper);
        if (CollectionUtil.isEmpty(favoriteList)) {
            return;
        }
        List<SysEveUserPcMenuFavorite> updateList = new ArrayList<>();
        for (int i = 0; i < favoriteList.size(); i++) {
            SysEveUserPcMenuFavorite favorite = favoriteList.get(i);
            favorite.setOrderBy(i);
            updateList.add(favorite);
        }
        updateEntity(updateList, userId);
    }

    private QueryWrapper<SysEveUserPcMenuFavorite> buildUserQueryWrapper(String userId, String tenantId) {
        QueryWrapper<SysEveUserPcMenuFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserPcMenuFavorite::getTenantId), tenantId);
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
        return String.format(Locale.ROOT, CacheConstants.USER_PC_MENU_FAVORITE_CACHE_KEY, userId, tenantId);
    }

    private void clearFavoriteCache(String userId, String tenantId) {
        jedisClientService.del(getCacheKey(userId, tenantId));
    }

}
