/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.brand.service.BrandService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsUsedEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.entity.Depot;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialShelvesState;
import com.skyeye.material.classenum.MaterialUnit;
import com.skyeye.material.dao.MaterialDao;
import com.skyeye.material.entity.*;
import com.skyeye.material.service.*;
import com.skyeye.procedure.entity.WorkProcedure;
import com.skyeye.procedure.service.WorkProcedureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialServiceImpl
 * @Description: 商品信息管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:44
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品信息", groupName = "商品管理", allowDynamicAttrKey = false)
public class MaterialServiceImpl extends SkyeyeBusinessServiceImpl<MaterialDao, Material> implements MaterialService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialNormsStockService materialNormsStockService;

    @Autowired
    private MaterialProcedureService materialProcedureService;

    @Autowired
    private MaterialUnitGroupService materialUnitGroupService;

    @Autowired
    private MaterialUnitService materialUnitService;

    @Autowired
    private WorkProcedureService workProcedureService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private BrandService brandService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setMationForMap(beans, "categoryId", "categoryMation");
        return beans;
    }

    @Override
    public void validatorEntity(Material entity) {
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Material::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(Material::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(Material::getModel), entity.getModel()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Material checkMaterial = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkMaterial)) {
            throw new CustomException("同种型号/名称的商品已经存在.");
        }
        if (entity.getUnit().equals(MaterialUnit.MULTI_SPECIFICATION.getKey())) {
            // 多单位
            if (StrUtil.isBlank(entity.getUnitGroupId())) {
                throw new CustomException("请选择单位.");
            }
            if (StrUtil.isBlank(entity.getFirstOutUnit())) {
                throw new CustomException("请选择首选出库单位.");
            }
            if (StrUtil.isBlank(entity.getFirstInUnit())) {
                throw new CustomException("请选择首选入库单位.");
            }
        }
    }

    @Override
    public void createPrepose(Material entity) {
        if (entity.getItemCode() == null) {
            throw new CustomException("请选择条形码开启类型.");
        }
        entity.setIsUsed(IsUsedEnum.NOT_USED.getKey());
        entity.setShelvesState(MaterialShelvesState.NOT_ON_SHELVE.getKey());
    }

    @Override
    public void writePostpose(Material entity, String userId) {
        super.writePostpose(entity, userId);

        // 保存商品规格信息以及初始化库存信息
        materialNormsService.saveMaterialNorms(userId, entity);
        // 保存商品的工序信息
        materialProcedureService.saveMaterialProcedure(entity.getId(), entity.getMaterialProcedure(), userId);
    }

    @Override
    public void deletePreExecution(Material entity) {
        if (entity.getIsUsed() == IsUsedEnum.IN_USE.getKey()) {
            throw new CustomException("该商品已被使用，不能删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除商品规格
        materialNormsService.deleteMaterialNormsByMaterialId(id);
        // 删除商品关联的工序信息
        materialProcedureService.deleteMaterialProcedureByMaterialId(id);
    }

    @Override
    public Material selectById(String id) {
        Material material = super.selectById(id);
        if (material.getUnit().equals(MaterialUnit.MULTI_SPECIFICATION.getKey())) {
            // 计量单位分组信息
            material.setUnitGroupMation(materialUnitGroupService.selectById(material.getUnitGroupId()));
            // 首选入库单位信息
            material.setFirstInUnitMation(materialUnitService.selectById(material.getFirstInUnit()));
            // 首选出入单位信息
            material.setFirstOutUnitMation(materialUnitService.selectById(material.getFirstOutUnit()));
        }
        // 产品工序信息
        if (CollectionUtil.isNotEmpty(material.getMaterialProcedure())) {
            workProcedureService.setDataMation(material.getMaterialProcedure(), MaterialProcedure::getProcedureId);
        }
        // 仓库信息
        List<MaterialNormsStock> normsStocks = material.getMaterialNorms().stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getNormsStock()))
            .flatMap(norms -> norms.getNormsStock().stream()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(normsStocks)) {
            List<String> depotIds = normsStocks.stream().map(MaterialNormsStock::getDepotId).distinct().collect(Collectors.toList());
            Map<String, Depot> depotMap = erpDepotService.selectMapByIds(depotIds);
            material.getMaterialNorms().forEach(norms -> {
                if (CollectionUtil.isEmpty(norms.getNormsStock())) {
                    return;
                }
                for (MaterialNormsStock normsStock : norms.getNormsStock()) {
                    normsStock.setDepotMation(depotMap.get(normsStock.getDepotId()));
                }
            });
        }
        // 品牌信息
        brandService.setDataMation(material, Material::getBrandId);

        return material;
    }

    @Override
    public Material getDataFromDb(String id) {
        Material material = super.getDataFromDb(id);
        // 商品规格信息
        List<MaterialNorms> materialNorms = materialNormsService.queryNormsUnitListByMaterialId(id);
        material.setMaterialNorms(materialNorms);
        // 商品关联工序信息
        List<MaterialProcedure> materialProcedures = materialProcedureService.queryMaterialProcedureByMaterialId(id);
        material.setMaterialProcedure(materialProcedures);

        return material;
    }

    @Override
    public List<Material> selectByIds(String... ids) {
        List<Material> materialList = super.selectByIds(ids);
        materialList.forEach(material -> {
            if (material.getUnit().equals(MaterialUnit.MULTI_SPECIFICATION.getKey())) {
                // 计量单位分组信息
                material.setUnitGroupMation(materialUnitGroupService.selectById(material.getUnitGroupId()));
                // 首选入库单位信息
                material.setFirstInUnitMation(materialUnitService.selectById(material.getFirstInUnit()));
                // 首选出入单位信息
                material.setFirstOutUnitMation(materialUnitService.selectById(material.getFirstOutUnit()));
            }
        });

        // 产品工序信息
        List<MaterialProcedure> materialProcedureList = materialList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getMaterialProcedure()))
            .flatMap(bean -> bean.getMaterialProcedure().stream()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(materialProcedureList)) {
            List<String> procedureIds = materialProcedureList.stream().map(MaterialProcedure::getProcedureId).distinct().collect(Collectors.toList());
            Map<String, WorkProcedure> procedureMap = workProcedureService.selectMapByIds(procedureIds);
            materialList.forEach(material -> {
                if (CollectionUtil.isEmpty(material.getMaterialProcedure())) {
                    return;
                }
                material.getMaterialProcedure().forEach(materialProcedure -> {
                    if (!procedureMap.containsKey(materialProcedure.getProcedureId())) {
                        return;
                    }
                    materialProcedure.setProcedureMation(procedureMap.get(materialProcedure.getProcedureId()));
                });
            });
        }

        List<MaterialNorms> materialNorms = materialList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getMaterialNorms()))
            .flatMap(norms -> norms.getMaterialNorms().stream()).collect(Collectors.toList());
        // 仓库信息
        List<MaterialNormsStock> normsStocks = materialNorms.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getNormsStock()))
            .flatMap(norms -> norms.getNormsStock().stream()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(normsStocks)) {
            List<String> depotIds = normsStocks.stream().map(MaterialNormsStock::getDepotId).distinct().collect(Collectors.toList());
            Map<String, Depot> depotMap = erpDepotService.selectMapByIds(depotIds);
            materialList.forEach(material -> {
                material.getMaterialNorms().forEach(norms -> {
                    if (CollectionUtil.isEmpty(norms.getNormsStock())) {
                        return;
                    }
                    for (MaterialNormsStock normsStock : norms.getNormsStock()) {
                        normsStock.setDepotMation(depotMap.get(normsStock.getDepotId()));
                    }
                });
            });
        }
        // 品牌信息
        brandService.setDataMation(materialList, Material::getBrandId);

        return materialList;
    }

    @Override
    protected List<Material> getDataFromDb(List<String> idList) {
        List<Material> materialList = super.getDataFromDb(idList);
        // 商品规格信息
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(StrUtil.EMPTY, idList.toArray(new String[]{}));
        // 商品关联工序信息
        Map<String, List<MaterialProcedure>> materialProcedureMap = materialProcedureService.queryMaterialProcedureByMaterialIds(idList);
        materialList.forEach(material -> {
            material.setMaterialProcedure(materialProcedureMap.get(material.getId()));
            material.setMaterialNorms(normsMap.get(material.getId()));
        });
        return materialList;
    }

    /**
     * 获取商品列表信息展示为表格方便选择
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMaterialListToTable(InputObject inputObject, OutputObject outputObject) {
        MaterialChooseQueryDo queryDo = inputObject.getParams(MaterialChooseQueryDo.class);
        QueryWrapper<Material> queryWrapper = super.getQueryWrapper(queryDo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Material::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryMaterialList(outputObject, queryWrapper, queryDo);
    }

    /**
     * 根据商品规格id以及仓库id获取库存
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMaterialTockByNormsId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 获取多个商品规格id
        List<String> normsIds = Arrays.asList(params.get("normsIds").toString().split(CommonCharConstants.COMMA_MARK));
        // 仓库id
        String depotId = params.get("depotId").toString();
        // 获取所有库存信息
        Map<String, String> bean = materialNormsStockService.queryMaterialNormsStock(normsIds, depotId);
        outputObject.setBean(bean);
    }

    /**
     * 获取商品库存信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMaterialReserveList(InputObject inputObject, OutputObject outputObject) {
        MaterialChooseQueryDo queryDo = inputObject.getParams(MaterialChooseQueryDo.class);
        QueryWrapper<Material> queryWrapper = super.getQueryWrapper(queryDo);
        queryMaterialList(outputObject, queryWrapper, queryDo);
    }

    private void queryMaterialList(OutputObject outputObject, QueryWrapper<Material> queryWrapper, MaterialChooseQueryDo queryDo) {
        Page pages = PageHelper.startPage(queryDo.getPage(), queryDo.getLimit());
        if (StrUtil.isNotBlank(queryDo.getCategoryId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Material::getCategoryId), queryDo.getCategoryId());
        }
        List<Material> beans = list(queryWrapper);

        // 获取规格单位信息
        List<String> materialIdList = beans.stream().map(Material::getId).collect(Collectors.toList());
        Map<String, List<MaterialNorms>> materialNormsMap = materialNormsService.queryMaterialNormsList(queryDo.getDepotId(),
            materialIdList.toArray(new String[]{}));
        for (Material bean : beans) {
            bean.setMaterialNorms(materialNormsMap.get(bean.getId()));
        }
        iSysDictDataService.setDataMation(beans, Material::getCategoryId);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryMaterialInventoryWarningList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        pageInfo.setEnabled(EnableEnum.ENABLE_USING.getKey());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMaterialInventoryWarningList(pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取所有商品列表
     *
     * @param inputObject
     * @param outputObject
     */
    @Override
    public void queryAllMaterialList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Material::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Material::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<Material> materialList = list(queryWrapper);
        outputObject.setBeans(materialList);
        outputObject.settotal(materialList.size());
    }

    @Override
    public void setUsed(String id) {
        Material material = super.selectById(id);
        if (material.getIsUsed() == null || material.getIsUsed() == IsUsedEnum.NOT_USED.getKey()) {
            UpdateWrapper<Material> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Material::getIsUsed), IsUsedEnum.IN_USE.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    @Override
    public void setShelvesState(String id, Integer shelvesState) {
        UpdateWrapper<Material> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Material::getShelvesState), shelvesState);
        updateWrapper.set(MybatisPlusUtil.toColumns(Material::getIsUsed), IsUsedEnum.IN_USE.getKey());
        update(updateWrapper);
        refreshCache(id);
    }

}
