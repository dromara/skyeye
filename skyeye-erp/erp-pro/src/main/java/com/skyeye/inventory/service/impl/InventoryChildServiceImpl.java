/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.inventory.classenum.InventoryChildState;
import com.skyeye.inventory.dao.InventoryChildDao;
import com.skyeye.inventory.entity.Inventory;
import com.skyeye.inventory.entity.InventoryChild;
import com.skyeye.inventory.entity.InventoryChildCode;
import com.skyeye.inventory.service.InventoryChildCodeService;
import com.skyeye.inventory.service.InventoryChildService;
import com.skyeye.inventory.service.InventoryService;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsCodeType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.rest.ifs.bizacct.service.IfsBizAcctEventService;
import com.skyeye.service.ErpCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: InventoryChildServiceImpl
 * @Description: 盘点任务表-子单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 16:55
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "盘点任务表-子单据", groupName = "盘点任务单")
public class InventoryChildServiceImpl extends SkyeyeLinkDataServiceImpl<InventoryChildDao, InventoryChild> implements InventoryChildService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialNormsCodeService materialNormsCodeService;

    @Autowired
    private InventoryChildCodeService inventoryChildCodeService;

    @Autowired
    protected MaterialService materialService;

    @Autowired
    private ErpCommonService erpCommonService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private IfsBizAcctEventService ifsBizAcctEventService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Override
    public QueryWrapper<InventoryChild> getQueryWrapper(CommonPageInfo commonPageInfo) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<InventoryChild> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(InventoryChild::getOperatorId), userId);
        List<String> stateList = Arrays.asList(FlowableChildStateEnum.ADEQUATE.getKey(), InventoryChildState.COMPLATE.getKey());
        queryWrapper.in(MybatisPlusUtil.toColumns(InventoryChild::getState), stateList);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        erpDepotService.setMationForMap(beans, "depotId", "depotMation");
        beans.forEach(bean -> {
            Integer type = Integer.parseInt(bean.get("type").toString());
            bean.put("typeMation", MaterialNormsCodeType.getMation(type));
        });
        return beans;
    }

    @Override
    public InventoryChild selectById(String id) {
        InventoryChild inventoryChild = super.selectById(id);
        materialService.setDataMation(inventoryChild, InventoryChild::getMaterialId);
        materialNormsService.setDataMation(inventoryChild, InventoryChild::getNormsId);
        erpDepotService.setDataMation(inventoryChild, InventoryChild::getDepotId);
        inventoryChild.setTypeMation(MaterialNormsCodeType.getMation(inventoryChild.getType()));
        return inventoryChild;
    }

    @Override
    public String calcAllPlanInventoryNum(List<InventoryChild> inventoryChildList) {
        String allPlanInventoryNum = CommonNumConstants.NUM_ZERO.toString();
        for (InventoryChild inventoryChild : inventoryChildList) {
            // 查询规格库存信息
            MaterialNorms materialNorms = materialNormsService.queryMaterialNorms(inventoryChild.getNormsId(), inventoryChild.getDepotId());
            String depotAllStock = ObjectUtil.isNotEmpty(materialNorms.getDepotTock()) ? materialNorms.getDepotTock().getAllStock() : CommonNumConstants.NUM_ZERO.toString();
            inventoryChild.setPlanNumber(depotAllStock);
            inventoryChild.setRealNumber(CommonNumConstants.NUM_ZERO.toString());
            inventoryChild.setProfitNum(CommonNumConstants.NUM_ZERO.toString());
            inventoryChild.setLossNum(CommonNumConstants.NUM_ZERO.toString());
            inventoryChild.setProfitPrice(CommonNumConstants.NUM_ZERO.toString());
            inventoryChild.setLossPrice(CommonNumConstants.NUM_ZERO.toString());

            allPlanInventoryNum += depotAllStock;
        }
        return allPlanInventoryNum;
    }

    @Override
    public void saveLinkList(String pId, List<InventoryChild> beans) {
        beans.forEach(bean -> {
            String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), BeanUtil.beanToMap(bean));
            bean.setOddNumber(oddNumber);
        });
        super.saveLinkList(pId, beans);
        List<InventoryChildCode> inventoryChildCodeList = new ArrayList<>();
        for (InventoryChild inventoryChild : beans) {
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCode(inventoryChild.getDepotId(),
                inventoryChild.getNormsId(), inventoryChild.getType(), MaterialNormsCodeInDepot.WAREHOUSING.getKey());
            for (MaterialNormsCode materialNormsCode : materialNormsCodeList) {
                InventoryChildCode inventoryChildCode = new InventoryChildCode();
                inventoryChildCode.setCodeNum(materialNormsCode.getCodeNum());
                inventoryChildCode.setParentId(inventoryChild.getId());
                inventoryChildCode.setType(materialNormsCode.getType());
                inventoryChildCode.setInDepot(MaterialNormsCodeInDepot.WAREHOUSING.getKey());
                inventoryChildCode.setOrderId(pId);
                inventoryChildCode.setMaterialId(inventoryChild.getMaterialId());
                inventoryChildCode.setNormsId(inventoryChild.getNormsId());
                inventoryChildCodeList.add(inventoryChildCode);
            }
        }
        inventoryChildCodeService.saveList(pId, inventoryChildCodeList);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void complateInventoryChild(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String realNumber = params.get("realNumber").toString();
        String profitNormsCode = params.get("profitNormsCode").toString();
        String lossNormsCode = params.get("lossNormsCode").toString();
        // 盘盈信息
        String profitNum = params.get("profitNum").toString();
        List<String> profitNormsCodeList = Arrays.asList(profitNormsCode.split("\n")).stream()
            .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
        // 盘亏信息
        String lossNum = params.get("lossNum").toString();
        List<String> lossNormsCodeList = Arrays.asList(lossNormsCode.split("\n")).stream()
            .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
        // 查询盘点子单据
        InventoryChild inventoryChild = selectById(id);

        // 校验盘点数量 计划数量 + 盘盈数量 - 盘亏数量
        String checkNumber = CalculationUtil.subtract(CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, inventoryChild.getPlanNumber(), profitNum), lossNum, ErpConstants.NUM_AFTER_DOT);
        if (CalculationUtil.compareTo(realNumber, checkNumber, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
            throw new CustomException("盘点后数量错误，须遵守计划数量 + 盘盈数量 - 盘亏数量 = 实际盘点数量，请确认");
        }

        Material material = materialService.selectById(inventoryChild.getMaterialId());
        MaterialNorms norms = materialNormsService.selectById(inventoryChild.getNormsId());
        if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
            // 单品盘点-一物一码
            // 如果开启了一物一码，那么这个数量就默认是整型的
            String profitNumValue = StrUtil.isEmpty(profitNum) ? CommonNumConstants.NUM_ZERO.toString() : profitNum;
            String lossNumValue = StrUtil.isEmpty(lossNum) ? CommonNumConstants.NUM_ZERO.toString() : lossNum;
            
            // 检查盘盈数量与明细数量是否一致
            if (CalculationUtil.compareTo(profitNumValue, CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) != 0) {
                if (Integer.parseInt(profitNumValue) != profitNormsCodeList.size()) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】【%s】的盘盈数量与明细数量不一致，请确认", material.getName(), norms.getName()));
                }
            }
            // 检查盘亏数量与明细数量是否一致
            if (CalculationUtil.compareTo(lossNumValue, CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) != 0) {
                if (Integer.parseInt(lossNumValue) != lossNormsCodeList.size()) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】【%s】的盘亏数量与明细数量不一致，请确认", material.getName(), norms.getName()));
                }
            }
            // 获取本次盘点的商品条形码
            List<InventoryChildCode> inventoryChildCodeList = inventoryChildCodeService.selectByParentId(inventoryChild.getId());
            List<String> inventoryChildCodeNumList = inventoryChildCodeList.stream().map(InventoryChildCode::getCodeNum)
                .distinct().collect(Collectors.toList());
            // 处理盘盈信息
            handleProfitNorms(profitNormsCodeList, inventoryChild, inventoryChildCodeNumList);
            // 处理盘亏信息
            handleLossNorms(lossNormsCodeList, inventoryChild, inventoryChildCodeNumList);
        }

        // 变化数量 = 实际盘点数量(实盘后的数量) - 计划判断数量
        String changeNumber = CalculationUtil.subtract(realNumber, inventoryChild.getPlanNumber(), ErpConstants.NUM_AFTER_DOT);
        // 使用工具函数比较String类型的数值
        if (CalculationUtil.compareTo(changeNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
            // 盘点数量减少，库存数量减少（changeNumber为负数，需要转为正数）
            String absChangeNumber = CalculationUtil.subtract(CommonNumConstants.NUM_ZERO.toString(), changeNumber, ErpConstants.NUM_AFTER_DOT);
            erpCommonService.editMaterialNormsDepotStock(inventoryChild.getDepotId(), inventoryChild.getMaterialId(),
                inventoryChild.getNormsId(), absChangeNumber, DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
        } else if (CalculationUtil.compareTo(changeNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
            // 盘点数量增加，库存数量增加
            erpCommonService.editMaterialNormsDepotStock(inventoryChild.getDepotId(), inventoryChild.getMaterialId(),
                inventoryChild.getNormsId(), changeNumber, DepotPutOutType.PUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
        }

        // 更新盘点子单据信息
        updateInventory(inventoryChild, realNumber, profitNum, lossNum, profitNormsCode, lossNormsCode);
        pushStocktakeEvent(selectById(id));
    }

    private void handleProfitNorms(List<String> profitNormsCodeList, InventoryChild inventoryChild, List<String> inventoryChildCodeNumList) {
        if (CollectionUtil.isNotEmpty(profitNormsCodeList)) {
            // 1. 和库存作对比
            // 从数据库查询未入库/入库状态的条形码信息
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY,
                profitNormsCodeList, MaterialNormsCodeInDepot.NOT_IN_STOCK.getKey(), MaterialNormsCodeInDepot.WAREHOUSING.getKey());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            List<String> diffList = profitNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】已出库或者编码不存在，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 2. 和本次盘点的商品条形码作对比
            diffList = profitNormsCodeList.stream()
                .filter(num -> !inventoryChildCodeNumList.contains(num)).collect(Collectors.toList());
            if (diffList.size() != profitNormsCodeList.size()) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】存在于本次盘点商品明细中，不属于盘盈类型，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 3. 更新库存状态
            String warehousingTime = DateUtil.getTimeAndToString();
            materialNormsCodeList.forEach(materialNormsCode -> {
                materialNormsCode.setInDepot(MaterialNormsCodeInDepot.WAREHOUSING.getKey());
                materialNormsCode.setDepotId(inventoryChild.getDepotId());
                materialNormsCode.setWarehousingTime(warehousingTime);
                materialNormsCode.setFromObjectId(inventoryChild.getId());
                materialNormsCode.setFromObjectKey(getServiceClassName());
            });
            materialNormsCodeService.updateEntity(materialNormsCodeList, StrUtil.EMPTY);
        }
    }

    private void handleLossNorms(List<String> lossNormsCodeList, InventoryChild inventoryChild, List<String> inventoryChildCodeNumList) {
        if (CollectionUtil.isNotEmpty(lossNormsCodeList)) {
            // 1. 和库存作对比
            // 从数据库查询入库状态的条形码信息
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(inventoryChild.getDepotId(),
                lossNormsCodeList, MaterialNormsCodeInDepot.WAREHOUSING.getKey());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            List<String> diffList = lossNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在该仓库中/未入库/已经出库，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 2. 和本次盘点的商品条形码作对比
            diffList = lossNormsCodeList.stream()
                .filter(num -> !inventoryChildCodeNumList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在于本次盘点商品明细中，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 3. 更新库存状态
            String outboundTime = DateUtil.getTimeAndToString();
            materialNormsCodeList.forEach(materialNormsCode -> {
                materialNormsCode.setInDepot(MaterialNormsCodeInDepot.OUTBOUND.getKey());
                materialNormsCode.setOutboundTime(outboundTime);
                materialNormsCode.setToObjectId(inventoryChild.getId());
                materialNormsCode.setToObjectKey(getServiceClassName());
            });
            materialNormsCodeService.updateEntity(materialNormsCodeList, StrUtil.EMPTY);
        }
    }

    private void updateInventory(InventoryChild inventoryChild, String realNumber, String profitNum, String lossNum, String profitNormsCode, String lossNormsCode) {
        // 根据id查询盘点子单据并更新相关信息
        inventoryChild.setRealNumber(realNumber);
        inventoryChild.setProfitNum(profitNum);
        inventoryChild.setProfitNormsCode(profitNormsCode);
        inventoryChild.setLossNum(lossNum);
        inventoryChild.setLossNormsCode(lossNormsCode);
        inventoryChild.setProfitPrice(CalculationUtil.multiply(CommonNumConstants.NUM_TWO, profitNum, inventoryChild.getUnitPrice()));
        inventoryChild.setLossPrice(CalculationUtil.multiply(CommonNumConstants.NUM_TWO, lossNum, inventoryChild.getUnitPrice()));
        inventoryChild.setState(InventoryChildState.COMPLATE.getKey());
        updateEntity(inventoryChild, StrUtil.EMPTY);
        // 更新盘点任务单据的已盘点数量
        inventoryService.setInventoriedNum(inventoryChild.getParentId(), inventoryChild.getPlanNumber());
    }

    private void pushStocktakeEvent(InventoryChild inventoryChild) {
        String profit = StrUtil.blankToDefault(inventoryChild.getProfitPrice(), CommonNumConstants.NUM_ZERO.toString());
        String loss = StrUtil.blankToDefault(inventoryChild.getLossPrice(), CommonNumConstants.NUM_ZERO.toString());
        if (CalculationUtil.compareTo(profit, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) == 0
            && CalculationUtil.compareTo(loss, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) == 0) {
            return;
        }
        Inventory parent = inventoryService.selectById(inventoryChild.getParentId());
        Map<String, Object> acctEvent = new HashMap<>();
        acctEvent.put("eventType", "stocktake");
        acctEvent.put("sourceType", "ERP_INVENTORY_CHILD");
        acctEvent.put("sourceId", inventoryChild.getId());
        acctEvent.put("sourceNo", inventoryChild.getOddNumber());
        acctEvent.put("profitAmount", profit);
        acctEvent.put("lossAmount", loss);
        acctEvent.put("amount", CalculationUtil.add(profit, loss, ErpConstants.NUM_AFTER_DOT));
        acctEvent.put("depotId", inventoryChild.getDepotId());
        acctEvent.put("materialId", inventoryChild.getMaterialId());
        acctEvent.put("voucherDate", parent == null ? DateUtil.getYmdTimeAndToString() : parent.getOperTime());
        acctEvent.put("summary", "盘点盈亏-" + inventoryChild.getOddNumber());
        ifsBizAcctEventService.pushIfsBizAcctEvent(acctEvent);
    }
}
