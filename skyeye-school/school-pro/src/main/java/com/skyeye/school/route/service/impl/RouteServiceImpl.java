package com.skyeye.school.route.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.route.dao.RoutesDao;
import com.skyeye.school.route.entity.RouteStop;
import com.skyeye.school.route.entity.Routes;
import com.skyeye.school.route.service.RouteStopService;
import com.skyeye.school.route.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName: RouteServiceImpl
 * @Description: 路线服务管理实现类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "路线管理", groupName = "路线管理")
public class RouteServiceImpl extends SkyeyeBusinessServiceImpl<RoutesDao, Routes> implements RoutesService {

    @Autowired
    private RouteStopService routeStopService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void deleteById(String id) {
        super.deleteById(id);
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        routeStopService.remove(queryWrapper);
    }

    @Override
    protected void writePostpose(Routes entity, String userId) {
        super.writePostpose(entity, userId);
        // 删除缓存
        jedisClientService.del(getCacheKey(entity.getSchoolId()));
    }

    @Override
    protected void deletePostpose(Routes entity) {
        // 删除缓存
        jedisClientService.del(getCacheKey(entity.getSchoolId()));
    }

    @Override
    public Routes selectById(String id) {
        Routes routes = super.selectById(id);
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id)
            .orderByAsc(MybatisPlusUtil.toColumns(RouteStop::getStopOrder));
        List<RouteStop> routeStops = routeStopService.list(queryWrapper);
        schoolService.setDataMation(routes, Routes::getSchoolMation);
        routes.setRouteStopList(routeStops);
        iAuthUserService.setName(routes, "createId", "createName");
        iAuthUserService.setName(routes, "lastUpdateId", "lastUpdateName");
        return routes;
    }

    @Override
    public void writeRouteStopList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Routes routes = selectById(id);
        if (ObjectUtil.isEmpty(routes) || StrUtil.isEmpty(routes.getId())) {
            throw new CustomException("未找到路线信息");
        }
        List<RouteStop> routeStopList = JSONUtil.toList(params.get("routeStopList").toString(), RouteStop.class);
        // 长度由前端按节点计算（米），后端只落库
        Object lengthRaw = params.get("routeLength");
        if (lengthRaw == null || StrUtil.isBlank(lengthRaw.toString())) {
            throw new CustomException("路线长度不能为空");
        }
        double totalLength;
        try {
            totalLength = Double.parseDouble(lengthRaw.toString());
        } catch (NumberFormatException e) {
            throw new CustomException("路线长度格式不正确");
        }
        if (totalLength < 0) {
            throw new CustomException("路线长度不能为负数");
        }
        totalLength = Math.round(totalLength * 100.0) / 100.0;
        UpdateWrapper<Routes> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Routes::getRouteLength), totalLength);
        update(updateWrapper);

        // 删除原有路线站点信息
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        routeStopService.remove(queryWrapper);
        // 新增路线站点信息
        Integer stopOrder = CommonNumConstants.NUM_ONE;
        for (RouteStop routeStop : routeStopList) {
            routeStop.setRouteId(id);
            routeStop.setStopOrder(stopOrder);
            stopOrder++;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        routeStopService.createEntity(routeStopList, userId);
        // 刷新缓存
        refreshCache(id);
        // 删除缓存
        jedisClientService.del(getCacheKey(routes.getSchoolId()));
    }

    @Override
    public void queryRouteList(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        if (StrUtil.isEmpty(tableSelectInfo.getHolderId())) {
            return;
        }
        String cacheKey = getCacheKey(tableSelectInfo.getHolderId());
        List<Routes> routes = redisCache.getList(cacheKey, key -> {
            QueryWrapper<Routes> queryWrapper = getQueryWrapper(tableSelectInfo);
            queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getSchoolId), tableSelectInfo.getHolderId());
            List<Routes> routesList = list(queryWrapper);
            if (CollectionUtil.isEmpty(routesList)) {
                return new ArrayList<>();
            }

            List<String> ids = routesList.stream().map(Routes::getId).collect(Collectors.toList());
            Map<String, List<RouteStop>> routeStopMap = routeStopService.queryStopListGroupByRoteIds(ids);
            for (Routes bean : routesList) {
                bean.setRouteStopList(routeStopMap.get(bean.getId()));
            }
            return routesList;
        }, RedisConstants.A_YEAR_SECONDS, Routes.class);
        outputObject.setBeans(routes);
        outputObject.settotal(routes.size());
    }

    private String getCacheKey(String schoolId) {
        return String.format(Locale.ROOT, "school:route:all:%s", schoolId);
    }
}
