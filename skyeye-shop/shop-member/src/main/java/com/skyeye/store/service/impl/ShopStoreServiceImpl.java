/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.shopmaterialnorms.rest.IShopMaterialNormsRest;
import com.skyeye.store.dao.ShopStoreDao;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.ShopStoreStaff;
import com.skyeye.store.service.ShopAreaService;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.store.service.ShopStoreStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: StoreServiceImpl
 * @Description: 门店管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 12:35
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "门店管理", groupName = "门店管理")
public class ShopStoreServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreDao, ShopStore> implements ShopStoreService {

    @Autowired
    private ShopAreaService shopAreaService;

    @Autowired
    private ShopStoreStaffService shopStoreStaffService;

    @Autowired
    private IShopMaterialNormsRest iShopMaterialNormsRest;

    @Override
    public QueryWrapper<ShopStore> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopStore> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getEnabled), commonPageInfo.getEnabled());
        }
        return queryWrapper;
    }

    @Override
    public void setDefaultOrderBy(CommonPageInfo commonPageInfo, QueryWrapper<ShopStore> wrapper) {
        if (StrUtil.isNotEmpty(commonPageInfo.getLatitude()) && StrUtil.isNotEmpty(commonPageInfo.getLongitude())) {
            // 使用ST_Distance_Sphere函数计算距离并按距离排序
            String orderByClause = "ORDER BY CASE WHEN longitude IS NULL THEN 1 ELSE 0 END, ST_Distance_Sphere(point(longitude, latitude), " +
                "point(" + commonPageInfo.getLongitude() + ", " + commonPageInfo.getLatitude() + ")) ASC";
            wrapper.last(orderByClause);
        } else {
            wrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopStore::getCreateTime));
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        shopAreaService.setMationForMap(beans, "shopAreaId", "shopAreaMation");
        if (StrUtil.isNotEmpty(commonPageInfo.getLatitude()) && StrUtil.isNotEmpty(commonPageInfo.getLongitude())) {
            // 计算距离并添加距离字段
            beans.forEach(bean -> {
                String longitude = bean.getOrDefault("longitude", CommonNumConstants.NUM_ZERO).toString();
                String latitude = bean.getOrDefault("latitude", CommonNumConstants.NUM_ZERO).toString();
                if (StrUtil.equals("undefined", longitude) || StrUtil.equals("undefined", latitude)
                    || StrUtil.isEmpty(longitude) || StrUtil.isEmpty(latitude)) {
                    bean.put("distance", CommonNumConstants.NUM_ZERO);
                } else {
                    double distance = ToolUtil.calculateDistance(Double.parseDouble(commonPageInfo.getLatitude()), Double.parseDouble(commonPageInfo.getLongitude()),
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude));
                    bean.put("distance", distance);
                }
            });
        }
        return beans;
    }

    @Override
    @IgnoreTenant
    public void queryStoreListFoServer(InputObject inputObject, OutputObject outputObject) {
        queryPageList(inputObject, outputObject);
    }

    @Override
    public void updatePrepose(ShopStore entity) {
        ShopStore oldShopStore = selectById(entity.getId());
        entity.setStartTime(oldShopStore.getStartTime());
        entity.setEndTime(oldShopStore.getEndTime());
        entity.setOnlineBookAppoint(oldShopStore.getOnlineBookAppoint());
        entity.setOnlineBookRadix(oldShopStore.getOnlineBookRadix());
        entity.setOnlineBookType(oldShopStore.getOnlineBookType());
        entity.setOnlineBookJson(oldShopStore.getOnlineBookJson());
    }

    @Override
    protected void writePostpose(ShopStore entity, String userId) {
        super.writePostpose(entity, userId);
        if (WhetherEnum.DISABLE_USING.getKey().equals(entity.getEnabled())) {
            // 禁用状态
            ExecuteFeignClient.get(() -> iShopMaterialNormsRest.deleteShopMaterialStoreByStoreIds(entity.getId()));
        } else if (WhetherEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            // 启用状态，新增门店商品
            ExecuteFeignClient.get(() -> iShopMaterialNormsRest.saveShopMaterialStore(entity.getId()));
        }
    }

    @Override
    @IgnoreTenant
    public ShopStore selectById(String id) {
        ShopStore shopStore = super.selectById(id);
        shopAreaService.setDataMation(shopStore, ShopStore::getShopAreaId);
        return shopStore;
    }

    @Override
    public List<ShopStore> selectByIds(String... ids) {
        List<ShopStore> shopStores = super.selectByIds(ids);
        shopAreaService.setDataMation(shopStores, ShopStore::getShopAreaId);
        return shopStores;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除门店下的商品
        ExecuteFeignClient.get(() -> iShopMaterialNormsRest.deleteShopMaterialStoreByStoreIds(id));
    }

    /**
     * 获取门店列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryStoreListByParams(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String shopAreaId = params.get("shopAreaId").toString();
        String enabled = params.get("enabled").toString();
        QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(shopAreaId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getShopAreaId), shopAreaId);
        }
        if (StrUtil.isNotEmpty(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getEnabled), enabled);
        }
        List<ShopStore> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    /**
     * 根据门店ID获取门店设置的线上预约信息(已结合当前登陆用户)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryStoreOnlineById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        ShopStore shopStore = selectById(id);

        // 获取该门店的成员
        List<ShopStoreStaff> shopStoreStaffs = shopStoreStaffService.getShopStoresByStoreId(id);
        Map<String, Map<String, Object>> userStaffMap = iAuthUserService.queryUserMationListByStaffIds(
            shopStoreStaffs.stream().map(ShopStoreStaff::getStaffId).collect(Collectors.toList()));
        shopStoreStaffs.forEach(shopStoreStaff -> {
            shopStoreStaff.setStaffMation(userStaffMap.get(shopStoreStaff.getStaffId()));
        });
        shopStore.setStoreStaffList(shopStoreStaffs);

        outputObject.setBean(shopStore);
        outputObject.settotal(1);
    }

    /**
     * 保存门店线上预约信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void saveStoreOnlineMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        UpdateWrapper<ShopStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.get("id").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getStartTime), params.get("startTime").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getEndTime), params.get("endTime").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookAppoint), params.get("onlineBookAppoint").toString());
        if (Integer.parseInt(params.get("onlineBookAppoint").toString()) == WhetherEnum.ENABLE_USING.getKey()) {
            // 开启线上预约
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookRadix), params.get("onlineBookRadix").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookType), params.get("onlineBookType").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookJson), params.get("onlineBookJson").toString());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookRadix), null);
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookType), null);
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookJson), null);
        }
        update(updateWrapper);
        refreshCache(params.get("id").toString());
    }

    /**
     * 获取门店指定日期的预约信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryStoreOnlineMationPointDay(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String onlineDay = params.get("onlineDay").toString();
        ShopStore shopStore = selectById(id);

        // 是否开启线上预约
        if (shopStore.getOnlineBookAppoint() == WhetherEnum.ENABLE_USING.getKey()) {
            // 获取预约时间段信息
            List<String> onlineTime = shopStore.getOnlineBookJson().stream().map(bean -> bean.get("time").toString()).collect(Collectors.toList());
            // 获取现有的预约信息
            List<Map<String, Object>> onlineAppointmentMation = skyeyeBaseMapper.queryOnlineAppointmentMation(onlineDay, onlineTime);
            Map<String, String> onlineAppointmentMap = onlineAppointmentMation
                .stream().collect(Collectors.toMap(bean -> bean.get("onlineTime").toString(), bean -> bean.get("onlineOrderNum").toString()));
            // 循环遍历预约时间点，减去预约日期指定时间段内已有的预约单数
            shopStore.getOnlineBookJson().forEach(bean -> {
                if (onlineAppointmentMap.containsKey(bean.get("time").toString())) {
                    bean.put("value", CalculationUtil.subtract(bean.get("onlineOrderNum").toString(), onlineAppointmentMap.get(bean.get("time").toString())));
                }
            });
        }
        outputObject.setBean(shopStore);
        outputObject.settotal(1);
    }

}
