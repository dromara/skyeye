/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.dao.MaterialNormsDao;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsStock;
import com.skyeye.material.entity.NormsCalcStock;
import com.skyeye.material.entity.unit.MaterialUnit;
import com.skyeye.material.entity.unit.MaterialUnitGroup;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialNormsStockService;
import com.skyeye.material.service.MaterialUnitGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialNormsServiceImpl
 * @Description: ERP商品规格参数服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/21 15:39
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品规格", groupName = "商品管理", manageShow = false)
public class MaterialNormsServiceImpl extends SkyeyeBusinessServiceImpl<MaterialNormsDao, MaterialNorms> implements MaterialNormsService {

    @Autowired
    private MaterialUnitGroupService materialUnitGroupService;

    @Autowired
    private MaterialNormsStockService materialNormsStockService;

    @Override
    public void saveMaterialNorms(String userId, Material material) {
        if (CollectionUtil.isEmpty(material.getMaterialNorms())) {
            throw new CustomException("商品规格信息不能为空.");
        }

        // 商品规格
        setNormsName(material);
        List<MaterialNorms> materialNormsList = save(userId, material.getId(), material.getMaterialNorms());

        // 初始化库存信息
        List<MaterialNormsStock> normsStockList = new ArrayList<>();
        for (MaterialNorms materialNorms : materialNormsList) {
            materialNorms.getNormsStock().forEach(normsStock -> {
                normsStock.setMaterialId(material.getId());
                normsStock.setNormsId(materialNorms.getId());
                normsStock.setType(MaterialNormsStockType.INIT_STOCK.getKey());
            });
            normsStockList.addAll(materialNorms.getNormsStock());
        }
        materialNormsStockService.saveMaterialNormsInitStock(material.getId(), normsStockList, userId);

        // 刷新规格的缓存
        List<String> normsIds = materialNormsList.stream().map(MaterialNorms::getId).collect(Collectors.toList());
        refreshCache(normsIds);
    }

    private void setNormsName(Material material) {
        Map<String, String> normsKeyToName = new HashMap<>();
        material.getNormsSpec().forEach(normsSpec -> {
            String title = normsSpec.get("title").toString();
            List<Map<String, Object>> options = (List<Map<String, Object>>) normsSpec.get("options");
            Map<String, String> collect = options.stream().collect(Collectors.toMap(bean -> bean.get("rowNum").toString(),
                item -> String.format(Locale.ROOT, "%s：%s", title.trim(), item.get("title").toString())));
            normsKeyToName.putAll(collect);
        });
        material.getMaterialNorms().forEach(materialNorms -> {
            if (material.getUnit().equals(com.skyeye.material.classenum.MaterialUnit.SINGLE_SPECIFICATION.getKey())) {
                // 单规格
                materialNorms.setName(material.getUnitName());
            } else {
                MaterialUnitGroup materialUnitGroupMation = materialUnitGroupService.selectById(material.getUnitGroupId());
                Map<String, String> unitToName = materialUnitGroupMation.getUnitList()
                    .stream().collect(Collectors.toMap(MaterialUnit::getId, MaterialUnit::getName));
                String[] tableNum = materialNorms.getTableNum().split(CommonCharConstants.HORIZONTAL_LINE_MARK);
                String materialNormsName = String.format(Locale.ROOT, "计量单位：%s", unitToName.get(tableNum[0]));
                for (int ii = 0; ii < tableNum.length; ii++) {
                    if (ii != 0) {
                        materialNormsName += '；' + normsKeyToName.get(tableNum[ii]);
                    }
                }
                materialNorms.setName(materialNormsName);
            }
        });
    }

    private List<MaterialNorms> save(String userId, String materialId, List<MaterialNorms> materialNormsList) {
        for (MaterialNorms materialNorms : materialNormsList) {
            materialNorms.setMaterialId(materialId);
        }
        List<MaterialNorms> result = new ArrayList<>();
        List<MaterialNorms> oldMaterialNorms = queryNormsUnitListByMaterialId(materialId);
        List<String> oldKeys = oldMaterialNorms.stream().map(bean -> bean.getTableNum()).collect(Collectors.toList());
        List<String> newKeys = materialNormsList.stream().map(bean -> bean.getTableNum()).collect(Collectors.toList());

        // (旧数据 - 新数据) 从数据库删除
        List<MaterialNorms> deleteBeans = oldMaterialNorms.stream()
            .filter(item -> !newKeys.contains(item.getTableNum())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteBeans)) {
            List<String> ids = deleteBeans.stream().map(MaterialNorms::getId).collect(Collectors.toList());
            deleteById(ids);
        }

        // (新数据 - 旧数据) 添加到数据库
        List<MaterialNorms> addBeans = materialNormsList.stream()
            .filter(item -> !oldKeys.contains(item.getTableNum())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(addBeans)) {
            createEntity(addBeans, userId);
            result.addAll(addBeans);
        }

        // 新数据与旧数据取交集 编辑
        List<MaterialNorms> editBeans = materialNormsList.stream()
            .filter(item -> oldKeys.contains(item.getTableNum())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(editBeans)) {
            Map<String, MaterialNorms> collect = oldMaterialNorms.stream().collect(Collectors.toMap(bean -> bean.getTableNum(), item -> item));
            if (CollectionUtil.isNotEmpty(collect)) {
                editBeans.forEach(bean -> {
                    MaterialNorms materialNorms = collect.get(bean.getTableNum());
                    bean.setId(materialNorms.getId());
                });
                updateEntity(editBeans, userId);
                result.addAll(editBeans);
            }
        }
        return result;
    }

    /**
     * 根据商品id删除规格信息
     *
     * @param materialId 商品id
     */
    @Override
    public void deleteMaterialNormsByMaterialId(String materialId) {
        List<MaterialNorms> materialNorms = queryNormsUnitListByMaterialId(materialId);
        List<String> ids = materialNorms.stream().map(MaterialNorms::getId).collect(Collectors.toList());
        deleteById(ids);
        // 根据商品id删除规格库存信息
        materialNormsStockService.deleteNormsInitStockByMaterialId(materialId);
    }

    @Override
    public Map<String, List<MaterialNorms>> queryMaterialNormsList(String depotId, String... materialIds) {
        List<String> materialIdList = Arrays.asList(materialIds);
        if (CollectionUtil.isEmpty(materialIdList)) {
            return new HashMap<>();
        }
        Map<String, List<MaterialNorms>> materialNormsMap = queryMaterialNorms(depotId, materialIds);
        return materialNormsMap;
    }

    @Override
    public MaterialNorms queryMaterialNorms(String normsId, String depotId) {
        MaterialNorms materialNorms = selectById(normsId);
        calcDepotStock(materialNorms, depotId);
        return materialNorms;
    }

    @Override
    public MaterialNorms selectById(String id) {
        MaterialNorms materialNorms = super.selectById(id);
        // 查询现有库存信息
        Map<String, List<MaterialNormsStock>> initStockMap = materialNormsStockService.queryNormsStockByNormsId(Arrays.asList(id),
            MaterialNormsStockType.ORDER_STOCK.getKey());
        materialNorms.setOrderStock(initStockMap.get(materialNorms.getId()));
        calcAllStock(materialNorms);
        return materialNorms;
    }

    @Override
    public MaterialNorms getDataFromDb(String id) {
        MaterialNorms materialNorms = super.getDataFromDb(id);
        // 查询初始化库存信息
        Map<String, List<MaterialNormsStock>> initStockMap = materialNormsStockService.queryNormsStockByNormsId(Arrays.asList(id),
            MaterialNormsStockType.INIT_STOCK.getKey());
        materialNorms.setNormsStock(initStockMap.get(materialNorms.getId()));
        return materialNorms;
    }

    @Override
    public List<MaterialNorms> selectByIds(String... ids) {
        List<MaterialNorms> materialNormsList = super.selectByIds(ids);
        // 查询现有库存信息
        Map<String, List<MaterialNormsStock>> initStockMap = materialNormsStockService.queryNormsStockByNormsId(Arrays.asList(ids),
            MaterialNormsStockType.ORDER_STOCK.getKey());
        for (MaterialNorms norm : materialNormsList) {
            norm.setOrderStock(initStockMap.get(norm.getId()));
            calcAllStock(norm);
        }
        return materialNormsList;
    }

    @Override
    @IgnoreTenant
    public List<MaterialNorms> getDataFromDb(List<String> ids) {
        List<MaterialNorms> materialNormsList = super.getDataFromDb(ids);
        // 查询初始化库存信息
        Map<String, List<MaterialNormsStock>> initStockMap = materialNormsStockService.queryNormsStockByNormsId(ids,
            MaterialNormsStockType.INIT_STOCK.getKey());
        for (MaterialNorms norm : materialNormsList) {
            norm.setNormsStock(initStockMap.get(norm.getId()));
        }
        return materialNormsList;
    }

    /**
     * 计算产品规格的总库存
     *
     * @param materialNorms
     */
    private static void calcAllStock(MaterialNorms materialNorms) {
        List<MaterialNormsStock> allNormsStock = getAllNormsStocks(materialNorms);
        if (CollectionUtil.isNotEmpty(allNormsStock)) {
            // 总库存
            String allStock = allNormsStock.stream()
                .map(MaterialNormsStock::getStock)
                .reduce(CommonNumConstants.NUM_ZERO.toString(), 
                    (sum, stock) -> {
                        String stockValue = StrUtil.isEmpty(stock) ? CommonNumConstants.NUM_ZERO.toString() : stock;
                        String sumValue = StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum;
                        return CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, sumValue, stockValue);
                    });
            // 初始总库存
            String initialTock = allNormsStock.stream()
                .filter(bean -> bean.getType().equals(MaterialNormsStockType.INIT_STOCK.getKey()))
                .map(MaterialNormsStock::getStock)
                .reduce(CommonNumConstants.NUM_ZERO.toString(), 
                    (sum, stock) -> {
                        String stockValue = StrUtil.isEmpty(stock) ? CommonNumConstants.NUM_ZERO.toString() : stock;
                        String sumValue = StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum;
                        return CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, sumValue, stockValue);
                    });
            // 可盘点总库存 = 总库存 - 初始库存
            String inventoryTock = CalculationUtil.subtract(allStock, initialTock, CommonNumConstants.NUM_ZERO);
            NormsCalcStock calcStock = new NormsCalcStock(allStock, initialTock, inventoryTock);
            materialNorms.setOverAllStock(calcStock);
        }
    }

    private static List<MaterialNormsStock> getAllNormsStocks(MaterialNorms materialNorms) {
        List<MaterialNormsStock> allNormsStock = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(materialNorms.getNormsStock())) {
            allNormsStock.addAll(materialNorms.getNormsStock());
        }
        if (CollectionUtil.isNotEmpty(materialNorms.getOrderStock())) {
            allNormsStock.addAll(materialNorms.getOrderStock());
        }
        return allNormsStock;
    }

    @Override
    public List<MaterialNorms> queryNormsUnitListByMaterialId(String materialId) {
        QueryWrapper<MaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNorms::getMaterialId), materialId);
        queryWrapper.select(CommonConstants.ID);
        List<MaterialNorms> materialNormsList = list(queryWrapper);
        // 获取规格id
        List<String> normsIdList = materialNormsList.stream().map(MaterialNorms::getId).collect(Collectors.toList());
        return selectByIds(normsIdList.toArray(new String[]{}));
    }

    public Map<String, List<MaterialNorms>> queryMaterialNorms(String depotId, String... materialIds) {
        Map<String, List<MaterialNorms>> materialNormsMap = queryNormsUnitListByMaterialId(Arrays.asList(materialIds));
        if (StrUtil.isNotEmpty(depotId)) {
            // 设置指定仓库的库存信息
            materialNormsMap.forEach((materialId, materialNormsList) -> {
                for (MaterialNorms materialNorms : materialNormsList) {
                    calcDepotStock(materialNorms, depotId);
                }
            });
        }
        return materialNormsMap;
    }

    public Map<String, List<MaterialNorms>> queryNormsUnitListByMaterialId(List<String> materialIds) {
        QueryWrapper<MaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MaterialNorms::getMaterialId), materialIds);
        queryWrapper.select(CommonConstants.ID);
        List<MaterialNorms> materialNormsList = list(queryWrapper);
        // 获取规格id
        List<String> normsIdList = materialNormsList.stream().map(MaterialNorms::getId).collect(Collectors.toList());
        List<MaterialNorms> materialNorms = selectByIds(normsIdList.toArray(new String[]{}));
        return materialNorms.stream().collect(Collectors.groupingBy(MaterialNorms::getMaterialId));
    }

    /**
     * 计算产品规格的指定仓库的库存
     *
     * @param materialNorms
     */
    @Override
    public void calcDepotStock(MaterialNorms materialNorms, String depotId) {
        List<MaterialNormsStock> allNormsStock = getAllNormsStocks(materialNorms);
        if (CollectionUtil.isNotEmpty(allNormsStock)) {
            // 指定仓库的库存
            String allStock = allNormsStock.stream()
                .filter(bean -> StrUtil.equals(bean.getDepotId(), depotId))
                .map(MaterialNormsStock::getStock)
                .reduce(CommonNumConstants.NUM_ZERO.toString(), 
                    (sum, stock) -> {
                        String stockValue = StrUtil.isEmpty(stock) ? CommonNumConstants.NUM_ZERO.toString() : stock;
                        String sumValue = StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum;
                        return CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, sumValue, stockValue);
                    });
            // 指定仓库的初始库存
            String initialTock = allNormsStock.stream()
                .filter(bean -> bean.getType().equals(MaterialNormsStockType.INIT_STOCK.getKey()) && StrUtil.equals(bean.getDepotId(), depotId))
                .map(MaterialNormsStock::getStock)
                .reduce(CommonNumConstants.NUM_ZERO.toString(), 
                    (sum, stock) -> {
                        String stockValue = StrUtil.isEmpty(stock) ? CommonNumConstants.NUM_ZERO.toString() : stock;
                        String sumValue = StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum;
                        return CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, sumValue, stockValue);
                    });
            // 指定仓库的可盘点库存
            String inventoryTock = CalculationUtil.subtract(allStock, initialTock, CommonNumConstants.NUM_ZERO);
            NormsCalcStock calcStock = new NormsCalcStock(allStock, initialTock, inventoryTock);
            materialNorms.setDepotTock(calcStock);
        }
    }

    /**
     * 根据产品id获取规格信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryNormsListByMaterialId(InputObject inputObject, OutputObject outputObject) {
        String materialId = inputObject.getParams().get("materialId").toString();
        if (StrUtil.isEmpty(materialId)) {
            return;
        }
        QueryWrapper<MaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNorms::getMaterialId), materialId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNorms::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(MaterialNorms::getOrderBy));
        List<MaterialNorms> materialNormsList = list(queryWrapper);
        outputObject.setBeans(materialNormsList);
        outputObject.settotal(materialNormsList.size());
    }

    /**
     * 获取所有启用规格（无入参）：规格启用，且所属商品启用、未删除；联表时显式带租户条件。
     */
    @Override
    public void queryAllNormsList(InputObject inputObject, OutputObject outputObject) {
        MPJLambdaWrapper<MaterialNorms> wrapper = JoinWrappers.lambda("norms", MaterialNorms.class)
            .selectAll(MaterialNorms.class)
            .innerJoin(Material.class, "ma", Material::getId, MaterialNorms::getMaterialId)
            .eq(MaterialNorms::getEnabled, EnableEnum.ENABLE_USING.getKey())
            .eq(Material::getEnabled, EnableEnum.ENABLE_USING.getKey())
            .eq(Material::getDeleteFlag, DeleteFlagEnum.NOT_DELETE.getKey())
            .orderByAsc(MaterialNorms::getOrderBy);
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            wrapper.eq("norms." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("ma." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<MaterialNorms> materialNormsList = skyeyeBaseMapper.selectJoinList(MaterialNorms.class, wrapper);
        outputObject.setBeans(materialNormsList);
        outputObject.settotal(materialNormsList.size());
    }

    @Override
    @IgnoreTenant
    public <M> void setDataMation(M bean, SFunction<M, ?> sFunction) {
        super.setDataMation(bean, sFunction);
    }

    @Override
    @IgnoreTenant
    public <M> void setDataMation(List<M> beans, SFunction<M, ?> sFunction) {
        super.setDataMation(beans, sFunction);
    }
}
