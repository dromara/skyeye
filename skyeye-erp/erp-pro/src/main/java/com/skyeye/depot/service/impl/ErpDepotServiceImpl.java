/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.dao.ErpDepotDao;
import com.skyeye.depot.entity.Depot;
import com.skyeye.depot.service.ErpDepotService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ErpDepotServiceImpl
 * @Description: 仓库信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库管理", groupName = "仓库管理", allowDynamicAttrKey = false)
public class ErpDepotServiceImpl extends SkyeyeBusinessServiceImpl<ErpDepotDao, Depot> implements ErpDepotService {

    /**
     * 企业仓列表：排除个人门店商家仓（store_id 有值）
     */
    @Override
    public QueryWrapper<Depot> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Depot> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.and(w -> w.isNull(MybatisPlusUtil.toColumns(Depot::getStoreId))
            .or().eq(MybatisPlusUtil.toColumns(Depot::getStoreId), StrUtil.EMPTY));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        return beans;
    }

    @Override
    protected void writePostpose(Depot entity, String userId) {
        if (entity.getIsDefault() != null && entity.getIsDefault().equals(IsDefaultEnum.IS_DEFAULT.getKey())) {
            // 企业仓设默认时，只清其他企业仓的默认标记（不含个人门店商家仓）
            QueryWrapper<Depot> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne(CommonConstants.ID, entity.getId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Depot::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Depot::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
            queryWrapper.and(w -> w.isNull(MybatisPlusUtil.toColumns(Depot::getStoreId))
                .or().eq(MybatisPlusUtil.toColumns(Depot::getStoreId), StrUtil.EMPTY));
            Depot defaultDepot = getOne(queryWrapper, false);

            UpdateWrapper<Depot> updateWrapper = new UpdateWrapper<>();
            updateWrapper.ne(CommonConstants.ID, entity.getId());
            updateWrapper.eq(MybatisPlusUtil.toColumns(Depot::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
            updateWrapper.and(w -> w.isNull(MybatisPlusUtil.toColumns(Depot::getStoreId))
                .or().eq(MybatisPlusUtil.toColumns(Depot::getStoreId), StrUtil.EMPTY));
            updateWrapper.set(MybatisPlusUtil.toColumns(Depot::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
            update(updateWrapper);

            if (defaultDepot != null) {
                refreshCache(defaultDepot.getId());
            }
        }
    }

    @Override
    public Depot selectById(String id) {
        Depot depot = super.selectById(id);
        if (depot != null && CollectionUtil.isNotEmpty(depot.getPrincipal())) {
            depot.setPrincipalMation(iAuthUserService.queryDataMationByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(depot.getPrincipal())));
        }
        return depot;
    }

    @Override
    public List<Depot> selectByIds(String... ids) {
        List<Depot> depots = super.selectByIds(ids);
        List<String> principalIds = depots.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getPrincipal()))
            .flatMap(norms -> norms.getPrincipal().stream()).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> userMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(principalIds));
        depots.forEach(depot -> {
            if (CollectionUtil.isEmpty(depot.getPrincipal())) {
                return;
            }
            List<Map<String, Object>> userMation = new ArrayList<>();
            depot.getPrincipal().forEach(principalId -> {
                if (!userMap.containsKey(principalId)) {
                    return;
                }
                userMation.add(userMap.get(principalId));
            });
            depot.setPrincipalMation(userMation);
        });
        return depots;
    }

    @Override
    public void queryAllStoreHouseList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();
        QueryWrapper<Depot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Depot::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.and(w -> w.isNull(MybatisPlusUtil.toColumns(Depot::getStoreId))
            .or().eq(MybatisPlusUtil.toColumns(Depot::getStoreId), StrUtil.EMPTY));
        if (StrUtil.isNotBlank(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Depot::getEnabled), enabled);
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Depot::getIsDefault));
        List<Depot> depotList = list(queryWrapper);
        outputObject.setBeans(depotList);
        outputObject.settotal(depotList.size());
    }

    @Override
    public void queryStoreHouseListByCurrentUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();
        String currentUserId = inputObject.getLogParams().get("id").toString();
        List<Depot> depotList = queryDepotListByChargePerson(currentUserId, enabled);
        outputObject.setBeans(depotList);
        outputObject.settotal(depotList.size());
    }

    @Override
    public List<Depot> queryDepotListByChargePerson(String userId, String enabled) {
        QueryWrapper<Depot> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("INSTR(CONCAT(',', REPLACE(REPLACE(" + MybatisPlusUtil.toColumns(Depot::getPrincipal) + ", '[', ''), ']', ''), ','), CONCAT(',\"', {0}, '\",'))", userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Depot::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.and(w -> w.isNull(MybatisPlusUtil.toColumns(Depot::getStoreId))
            .or().eq(MybatisPlusUtil.toColumns(Depot::getStoreId), StrUtil.EMPTY));
        if (StrUtil.isNotBlank(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Depot::getEnabled), enabled);
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Depot::getIsDefault));
        List<Depot> depotList = list(queryWrapper);
        return depotList;
    }

}
