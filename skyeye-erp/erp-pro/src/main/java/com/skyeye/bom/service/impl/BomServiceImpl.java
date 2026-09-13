/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.bom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.dao.BomDao;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.entity.BomProcedureConsumables;
import com.skyeye.bom.service.BomChildService;
import com.skyeye.bom.service.BomProcedureConsumablesService;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialFromType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.service.WayProcedureService;
import com.skyeye.procedure.service.WorkProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ErpBomServiceImpl
 * @Description: bom清单服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "bom清单管理", groupName = "bom清单管理", allowDynamicAttrKey = false)
public class BomServiceImpl extends SkyeyeBusinessServiceImpl<BomDao, Bom> implements BomService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private BomChildService bomChildService;

    @Autowired
    private BomProcedureConsumablesService bomProcedureConsumablesService;

    @Autowired
    private WayProcedureService wayProcedureService;

    @Autowired
    private WorkProcedureService workProcedureService;

    @Override
    public QueryWrapper<Bom> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Bom> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);

        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        return beans;
    }

    @Override
    public void queryBomHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String versionNo = commonPageInfo.getCustomParamsMapStr("versionNo");
        if (StrUtil.isEmpty(versionNo)) {
            return;
        }

        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Bom> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getVersionNo), versionNo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Bom::getLargeVersion));
        List<Bom> bomList = list(queryWrapper);
        outputObject.setBeans(bomList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public String createEntity(Bom entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(Bom entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    protected void createPrepose(Bom entity) {
        if (StrUtil.isNotEmpty(entity.getFromId()) && StrUtil.isEmpty(entity.getCadContent())) {
            Bom from = selectById(entity.getFromId());
            if (ObjectUtil.isNotEmpty(from)) {
                entity.setCadContent(from.getCadContent());
            }
        }
    }

    @Override
    protected void updatePrepose(Bom entity) {
        entity.setCadContent(selectById(entity.getId()).getCadContent());
    }

    @Override
    public void editBomCadContentById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Bom bom = selectById(id);
        if (ObjectUtil.isEmpty(bom)) {
            throw new CustomException("BOM方案不存在");
        }
        bom.setCadContent(params.get("cadContent").toString());
        DataCommonUtil.setCommonLastUpdateDataByGenericity(bom, InputObject.getLogParamsStatic().get("id").toString());
        updateById(bom);
        refreshCache(id);
    }

    @Override
    public void validatorEntity(Bom entity) {
        super.validatorEntity(entity);
        BomChild checkMaterial = entity.getBomChildList().stream().filter(bomChild -> StrUtil.equals(entity.getMaterialId(), bomChild.getMaterialId())).findFirst().orElse(null);
        if (ObjectUtil.isNotEmpty(checkMaterial)) {
            throw new CustomException("子件清单中不能包含父件信息");
        }

        // 批量查询所有子件的商品信息，用于判断商品类型
        List<String> bomChildMaterialIds = entity.getBomChildList().stream()
            .map(BomChild::getMaterialId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Material> materialMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(bomChildMaterialIds)) {
            materialMap = materialService.selectMapByIds(bomChildMaterialIds);
        }

        for (BomChild bomChild : entity.getBomChildList()) {
            String needNum = StrUtil.isEmpty(bomChild.getNeedNum())
                ? CommonNumConstants.NUM_ZERO.toString()
                : bomChild.getNeedNum();
            if (CalculationUtil.compareTo(needNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) == 0) {
                throw new CustomException("子件数量不能为0");
            }

            // 获取子件的商品信息
            Material material = materialMap.get(bomChild.getMaterialId());
            if (ObjectUtil.isNotEmpty(material) && material.getFromType() != null) {
                if (material.getFromType().equals(MaterialFromType.SELF_PRODUCED.getKey())) {
                    // 自产商品：必须配置工艺，耗材可以不配
                    if (StrUtil.isEmpty(bomChild.getWayProcedureId())) {
                        throw new CustomException("自产商品子件必须配置工艺");
                    }
                } else if (material.getFromType().equals(MaterialFromType.OUTSOURCING.getKey())) {
                    // 外购商品：清空工艺和耗材
                    bomChild.setWayProcedureId(null);
                    bomChild.setProcedureConsumablesList(null);
                }
            }
        }

        entity.setConsumablesPrice(calcConsumablesPrice(entity));
        entity.setProcedurePrice(calcProcedurePrice(entity));
        entity.setAllPrice(CalculationUtil.add(entity.getConsumablesPrice(), entity.getProcedurePrice(), CommonNumConstants.NUM_TWO));
    }

    /**
     * 计算耗材总费用
     * 包括：
     * 1. BOM子件本身的耗材费用（仅外购商品：子件数量 * 成本价）
     * 2. BOM子件绑定的工序耗材费用（仅自产商品：工序耗材数量 * 成本价）
     * 3. BOM层面的工序耗材费用（工序耗材数量 * 成本价）
     *
     * @param bom BOM实体
     * @return 耗材总费用
     */
    public String calcConsumablesPrice(Bom bom) {
        String allConsumablesPrice = "0";
        List<BomChild> bomChildList = bom.getBomChildList();

        if (CollectionUtil.isEmpty(bomChildList)) {
            bomChildList = new ArrayList<>();
        }

        // 批量查询所有子件的商品信息，用于判断商品类型
        List<String> bomChildMaterialIds = bomChildList.stream()
            .map(BomChild::getMaterialId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(bomChildMaterialIds)) {
            materialMap = materialService.selectMapByIds(bomChildMaterialIds);
        }

        // 1. 计算BOM子件本身的耗材费用（仅外购商品）
        List<String> normsIds = bomChildList.stream()
            .map(BomChild::getNormsId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());

        Map<String, MaterialNorms> materialNormsMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(normsIds)) {
            materialNormsMap = materialNormsService.selectMapByIds(normsIds);
        }

        for (BomChild bomChild : bomChildList) {
            MaterialNorms materialNorms = materialNormsMap.get(bomChild.getNormsId());
            if (ObjectUtil.isEmpty(materialNorms)) {
                continue;
            }

            // 获取子件的商品信息
            Material material = materialMap.get(bomChild.getMaterialId());
            if (ObjectUtil.isEmpty(material) || material.getFromType() == null) {
                continue;
            }

            // 只有外购商品才计算子件本身的耗材费用
            if (material.getFromType().equals(MaterialFromType.OUTSOURCING.getKey())) {
                // 单个子件耗材总费用 = 所需要的数量 * 成本价
                String needNum = StrUtil.isEmpty(bomChild.getNeedNum()) ? CommonNumConstants.NUM_ZERO.toString() : bomChild.getNeedNum();
                String estimatePrice = StrUtil.isEmpty(materialNorms.getEstimatePurchasePrice()) ? CommonNumConstants.NUM_ZERO.toString() : materialNorms.getEstimatePurchasePrice();

                String consumablesPrice = CalculationUtil.multiply(needNum, estimatePrice, ErpConstants.NUM_AFTER_DOT);
                bomChild.setConsumablesPrice(consumablesPrice);
                allConsumablesPrice = CalculationUtil.add(allConsumablesPrice, consumablesPrice, ErpConstants.NUM_AFTER_DOT);
            } else {
                // 自产商品不计算子件本身的耗材费用
                bomChild.setConsumablesPrice(CommonNumConstants.NUM_ZERO.toString());
            }
        }

        // 2. 计算BOM子件绑定的工序耗材费用（仅自产商品）
        for (BomChild bomChild : bomChildList) {
            // 获取子件的商品信息
            Material material = materialMap.get(bomChild.getMaterialId());
            if (ObjectUtil.isEmpty(material) || material.getFromType() == null) {
                continue;
            }

            // 只有自产商品才计算绑定的工序耗材费用
            if (!material.getFromType().equals(MaterialFromType.SELF_PRODUCED.getKey())) {
                continue;
            }

            if (CollectionUtil.isEmpty(bomChild.getProcedureConsumablesList())) {
                continue;
            }

            // 获取子件绑定的工序耗材的规格ID
            List<String> consumablesNormsIds = bomChild.getProcedureConsumablesList().stream()
                .map(BomProcedureConsumables::getNormsId)
                .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());

            if (CollectionUtil.isEmpty(consumablesNormsIds)) {
                continue;
            }

            Map<String, MaterialNorms> consumablesNormsMap = materialNormsService.selectMapByIds(consumablesNormsIds);

            for (BomProcedureConsumables consumable : bomChild.getProcedureConsumablesList()) {
                MaterialNorms consumableNorms = consumablesNormsMap.get(consumable.getNormsId());
                if (ObjectUtil.isEmpty(consumableNorms)) {
                    continue;
                }

                // 工序耗材费用 = 耗材数量 * 成本价
                String consumableNeedNum = StrUtil.isEmpty(consumable.getNeedNum()) ? CommonNumConstants.NUM_ZERO.toString() : consumable.getNeedNum();
                String consumableEstimatePrice = StrUtil.isEmpty(consumableNorms.getEstimatePurchasePrice()) ? CommonNumConstants.NUM_ZERO.toString() : consumableNorms.getEstimatePurchasePrice();

                String consumablePrice = CalculationUtil.multiply(consumableNeedNum, consumableEstimatePrice, ErpConstants.NUM_AFTER_DOT);
                allConsumablesPrice = CalculationUtil.add(allConsumablesPrice, consumablePrice, ErpConstants.NUM_AFTER_DOT);
            }
        }

        // 3. 计算BOM层面的工序耗材费用
        if (CollectionUtil.isNotEmpty(bom.getProcedureConsumablesList())) {
            List<String> bomConsumablesNormsIds = bom.getProcedureConsumablesList().stream()
                .map(BomProcedureConsumables::getNormsId)
                .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(bomConsumablesNormsIds)) {
                Map<String, MaterialNorms> bomConsumablesNormsMap = materialNormsService.selectMapByIds(bomConsumablesNormsIds);

                for (BomProcedureConsumables consumable : bom.getProcedureConsumablesList()) {
                    MaterialNorms consumableNorms = bomConsumablesNormsMap.get(consumable.getNormsId());
                    if (ObjectUtil.isEmpty(consumableNorms)) {
                        continue;
                    }

                    // 工序耗材费用 = 耗材数量 * 成本价
                    String consumableNeedNum = StrUtil.isEmpty(consumable.getNeedNum()) ? CommonNumConstants.NUM_ZERO.toString() : consumable.getNeedNum();
                    String consumableEstimatePrice = StrUtil.isEmpty(consumableNorms.getEstimatePurchasePrice()) ? CommonNumConstants.NUM_ZERO.toString() : consumableNorms.getEstimatePurchasePrice();

                    String consumablePrice = CalculationUtil.multiply(consumableNeedNum, consumableEstimatePrice, ErpConstants.NUM_AFTER_DOT);
                    allConsumablesPrice = CalculationUtil.add(allConsumablesPrice, consumablePrice, ErpConstants.NUM_AFTER_DOT);
                }
            }
        }

        return allConsumablesPrice;
    }

    /**
     * 计算工序总费用
     * 包括：
     * 1. BOM子件绑定的工艺费用（仅自产商品）
     * 2. BOM层面的工艺费用
     *
     * @param bom BOM实体
     * @return 工序总费用
     */
    public String calcProcedurePrice(Bom bom) {
        String allProcedurePrice = "0";
        List<BomChild> bomChildList = bom.getBomChildList();

        if (CollectionUtil.isEmpty(bomChildList)) {
            bomChildList = new ArrayList<>();
        }

        // 批量查询所有子件的商品信息，用于判断商品类型
        List<String> bomChildMaterialIds = bomChildList.stream()
            .map(BomChild::getMaterialId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(bomChildMaterialIds)) {
            materialMap = materialService.selectMapByIds(bomChildMaterialIds);
        }

        // 1. 计算BOM子件绑定的工艺费用（仅自产商品）
        List<String> wayProcedureIdList = bomChildList.stream()
            .filter(bean -> StrUtil.isNotEmpty(bean.getWayProcedureId()))
            .map(BomChild::getWayProcedureId).distinct().collect(Collectors.toList());

        Map<String, WayProcedure> wayProcedureMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(wayProcedureIdList)) {
            wayProcedureMap = wayProcedureService.selectMapByIds(wayProcedureIdList);
        }

        for (BomChild bomChild : bomChildList) {
            String allPrice = StrUtil.isEmpty(bomChild.getConsumablesPrice()) ? CommonNumConstants.NUM_ZERO.toString() : bomChild.getConsumablesPrice();

            // 获取子件的商品信息
            Material material = materialMap.get(bomChild.getMaterialId());
            if (ObjectUtil.isEmpty(material) || material.getFromType() == null) {
                // 如果没有商品信息，设置总价为耗材费用
                bomChild.setAllPrice(allPrice);
                continue;
            }

            // 只有自产商品才计算工艺费用
            if (!material.getFromType().equals(MaterialFromType.SELF_PRODUCED.getKey())) {
                // 外购商品：总价 = 耗材费用（子件本身）
                bomChild.setAllPrice(allPrice);
                continue;
            }

            // 自产商品：需要计算工艺费用
            if (StrUtil.isEmpty(bomChild.getWayProcedureId())) {
                bomChild.setAllPrice(allPrice);
                continue;
            }

            WayProcedure wayProcedure = wayProcedureMap.get(bomChild.getWayProcedureId());
            if (ObjectUtil.isEmpty(wayProcedure)) {
                bomChild.setAllPrice(allPrice);
                continue;
            }

            // 累加工艺费用
            String procedurePrice = StrUtil.isEmpty(wayProcedure.getAllPrice()) ? CommonNumConstants.NUM_ZERO.toString() : wayProcedure.getAllPrice();
            allProcedurePrice = CalculationUtil.add(allProcedurePrice, procedurePrice, ErpConstants.NUM_AFTER_DOT);

            // 子件清单总价 = 耗材费用 + 工艺费用
            allPrice = CalculationUtil.add(allPrice, procedurePrice, ErpConstants.NUM_AFTER_DOT);
            bomChild.setAllPrice(allPrice);
        }

        // 2. 计算BOM层面的工艺费用
        if (StrUtil.isNotEmpty(bom.getWayProcedureId())) {
            WayProcedure bomWayProcedure = wayProcedureService.selectById(bom.getWayProcedureId());
            if (ObjectUtil.isNotEmpty(bomWayProcedure)) {
                String bomProcedurePrice = StrUtil.isEmpty(bomWayProcedure.getAllPrice()) ? CommonNumConstants.NUM_ZERO.toString() : bomWayProcedure.getAllPrice();
                allProcedurePrice = CalculationUtil.add(allProcedurePrice, bomProcedurePrice, ErpConstants.NUM_AFTER_DOT);
            }
        }

        return allProcedurePrice;
    }

    @Override
    protected void writePostpose(Bom entity, String userId) {
        super.writePostpose(entity, userId);

        // 在保存子件清单得工序耗材时，已经把（BOM层面的 + BOM子件下的）所有得工序耗材都先删除了，所以下面保存工序耗材列表（BOM层面的）无需在进行删除
        entity.getBomChildList().forEach(bomChild -> bomChild.setBomId(entity.getId()));
        bomChildService.createEntity(entity.getBomChildList(), userId);

        // 保存工序耗材列表（BOM层面的）
        if (CollectionUtil.isNotEmpty(entity.getProcedureConsumablesList())) {
            entity.getProcedureConsumablesList().forEach(consumables -> {
                consumables.setBomId(entity.getId());
                consumables.setBomChildId(null); // BOM层面的耗材，bomChildId为空
            });
            bomProcedureConsumablesService.createEntity(entity.getProcedureConsumablesList(), userId);
        }
    }

    @Override
    public Bom getDataFromDb(String id) {
        Bom bom = super.getDataFromDb(id);
        // 设置子件清单信息
        bom.setBomChildList(bomChildService.queryBomChildByBomId(bom.getId()));
        // 设置工序耗材列表信息（BOM层面的）
        bom.setProcedureConsumablesList(bomProcedureConsumablesService.queryListByBomId(bom.getId()));
        return bom;
    }

    @Override
    protected List<Bom> getDataFromDb(List<String> idList) {
        List<Bom> bomList = super.getDataFromDb(idList);
        List<String> ids = bomList.stream().map(Bom::getId).collect(Collectors.toList());
        // 设置子件清单信息
        Map<String, List<BomChild>> bomChildMap = bomChildService.queryBomChildByBomId(ids);
        // 设置工序耗材列表信息（BOM层面的）
        Map<String, List<BomProcedureConsumables>> consumablesMap = bomProcedureConsumablesService.queryListByBomIds(ids);
        bomList.forEach(bom -> {
            String id = bom.getId();
            bom.setBomChildList(bomChildMap.get(id));
            bom.setProcedureConsumablesList(consumablesMap.get(id));
        });
        return bomList;
    }

    @Override
    public Bom selectById(String id) {
        Bom bom = super.selectById(id);

        // 查询工艺信息
        wayProcedureService.setDataMation(bom, Bom::getWayProcedureId);
        wayProcedureService.setDataMation(bom.getBomChildList(), BomChild::getWayProcedureId);
        bom.getBomChildList().forEach(bomChild -> {
            bomChild.setOpen(true);
        });
        // 设置产品/规格信息
        materialService.setDataMation(bom.getBomChildList(), BomChild::getMaterialId);
        materialNormsService.setDataMation(bom.getBomChildList(), BomChild::getNormsId);
        materialService.setDataMation(bom, Bom::getMaterialId);
        materialNormsService.setDataMation(bom, Bom::getNormsId);
        // 查询工序耗材信息（BOM子件下的）
        bom.getBomChildList().forEach(bomChild -> {
            if (CollectionUtil.isNotEmpty(bomChild.getProcedureConsumablesList())) {
                materialService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
                materialNormsService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
                workProcedureService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getProcedureId);
            }
        });
        // 查询工序耗材信息（BOM层面的）
        if (CollectionUtil.isNotEmpty(bom.getProcedureConsumablesList())) {
            materialService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
            materialNormsService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
        }
        // 设置工序信息
        workProcedureService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getProcedureId);
        return bom;
    }

    @Override
    public List<Bom> selectByIds(String... ids) {
        List<Bom> bomList = super.selectByIds(ids);

        // 查询工艺信息
        wayProcedureService.setDataMation(bomList, Bom::getWayProcedureId);

        // 查询工艺信息
        bomList.forEach(bom -> {
            wayProcedureService.setDataMation(bom.getBomChildList(), BomChild::getWayProcedureId);
            bom.getBomChildList().forEach(bomChild -> {
                bomChild.setOpen(true);
            });
            // 设置产品/规格信息
            materialService.setDataMation(bom.getBomChildList(), BomChild::getMaterialId);
            materialNormsService.setDataMation(bom.getBomChildList(), BomChild::getNormsId);
            // 查询工序耗材信息（BOM子件下的）
            bom.getBomChildList().forEach(bomChild -> {
                if (CollectionUtil.isNotEmpty(bomChild.getProcedureConsumablesList())) {
                    materialService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
                    materialNormsService.setDataMation(bomChild.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
                }
            });
            // 查询工序耗材信息（BOM层面的）
            if (CollectionUtil.isNotEmpty(bom.getProcedureConsumablesList())) {
                materialService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getMaterialId);
                materialNormsService.setDataMation(bom.getProcedureConsumablesList(), BomProcedureConsumables::getNormsId);
            }
        });
        materialService.setDataMation(bomList, Bom::getMaterialId);
        materialNormsService.setDataMation(bomList, Bom::getNormsId);

        return bomList;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子件清单以及所有工序耗材
        bomChildService.deleteBomChildByBomId(id);
    }

    /**
     * 根据规格id获取方案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryBomListByNormsId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String normsId = map.get("normsId").toString();
        if (StrUtil.isEmpty(normsId)) {
            return;
        }

        // 查询符合条件的BOM：未删除、已发布
        QueryWrapper<Bom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getNormsId), normsId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Bom::getLargeVersion));
        List<Bom> bomList = list(queryWrapper);

        if (CollectionUtil.isEmpty(bomList)) {
            return;
        }

        // 按versionNo分组，每组取largeVersion最大的（已按largeVersion降序排序）
        Map<String, Bom> versionNoMaxBomMap = new HashMap<>();
        for (Bom bom : bomList) {
            String versionNo = bom.getVersionNo();
            if (StrUtil.isEmpty(versionNo)) {
                continue;
            }
            // 如果该versionNo还没有记录，或者当前BOM的largeVersion更大，则更新
            Bom existingBom = versionNoMaxBomMap.get(versionNo);
            if (existingBom == null || bom.getLargeVersion() > existingBom.getLargeVersion()) {
                versionNoMaxBomMap.put(versionNo, bom);
            }
        }

        // 转换为List
        List<Bom> filteredBomList = new ArrayList<>(versionNoMaxBomMap.values());

        filteredBomList.forEach(bom -> {
            // bom名称展示的时候同时显示版本
            bom.setName(String.format(Locale.ROOT, "%s(V%s)", bom.getName(), bom.getLargeVersion()));
        });

        materialNormsService.setDataMation(filteredBomList, Bom::getNormsId);

        outputObject.setBeans(filteredBomList);
        outputObject.settotal(filteredBomList.size());
    }

    @Override
    public Map<String, List<Bom>> getBomListByNormsId(String... normsId) {
        List<String> normsIdList = Arrays.asList(normsId);
        if (CollectionUtil.isEmpty(normsIdList)) {
            return cn.hutool.core.map.MapUtil.newHashMap();
        }
        QueryWrapper<Bom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.in(MybatisPlusUtil.toColumns(Bom::getNormsId), normsIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Bom::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Bom::getCreateTime));
        List<Bom> bomList = list(queryWrapper);

        bomList.forEach(bom -> {
            // bom名称展示的时候同时显示版本
            bom.setName(String.format(Locale.ROOT, "%s(V%s)", bom.getName(), bom.getLargeVersion()));
        });
        return bomList.stream().collect(Collectors.groupingBy(Bom::getNormsId));
    }

    /**
     * 根据商品信息以及bom方案信息获取商品树---用于生产模块
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String proList = map.get("proList").toString();
        // 处理数据
        List<Map<String, Object>> beans = JSONUtil.toList(proList, null);
        // 设置产品/规格信息
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        // 获取方案下的子件
        List<String> bomIds = beans.stream()
            .filter(bean -> !MapUtil.checkKeyIsNull(bean, "bomId") && StrUtil.isNotEmpty(bean.get("bomId").toString()))
            .map(bean -> bean.get("bomId").toString()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bomIds)) {
            Map<String, Bom> bomMap = selectMapByIds(bomIds);
            wayProcedureService.setDataMation(new ArrayList<>(bomMap.values()), Bom::getWayProcedureId);
            List<BomChild> tempList = new ArrayList<>();
            beans.forEach(bean -> {
                String bomId = MapUtil.checkKeyIsNull(bean, "bomId") ? null : bean.get("bomId") != null ? bean.get("bomId").toString() : null;
                if (StrUtil.isNotEmpty(bomId) && bomMap.containsKey(bomId)) {
                    Bom bom = bomMap.get(bomId);
                    if (StrUtil.isNotEmpty(bom.getWayProcedureId())) {
                        bean.put("wayProcedureId", bom.getWayProcedureId());
                        if (bom.getWayProcedureMation() != null) {
                            bean.put("wayProcedureMation", bom.getWayProcedureMation());
                        }
                    }
                    tempList.addAll(bom.getBomChildList());
                }
            });
            if (CollectionUtil.isNotEmpty(tempList)) {
                beans.addAll(tempList.stream()
                    .map(temp -> BeanUtil.beanToMap(temp)).collect(Collectors.toList()));
            }
        }
        outputObject.setBeans(beans);
    }

}
