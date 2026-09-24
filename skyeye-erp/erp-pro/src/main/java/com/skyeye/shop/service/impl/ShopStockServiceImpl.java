/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shop.classenum.StoreNormsCodeUseState;
import com.skyeye.shop.dao.ShopStockDao;
import com.skyeye.shop.entity.*;
import com.skyeye.shop.service.ShopStockService;
import com.skyeye.shop.service.ShopStoreInventoryCheckService;
import com.skyeye.shopmaterial.entity.ShopMaterialStore;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopStockServiceImpl
 * @Description: 门店物料库存信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/31 16:58
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "门店物料库存信息", groupName = "门店物料库存", manageShow = false)
public class ShopStockServiceImpl extends SkyeyeBusinessServiceImpl<ShopStockDao, ShopStock> implements ShopStockService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Autowired
    private ShopMaterialStoreService shopMaterialStoreService;

    @Autowired
    private ShopStoreInventoryCheckService shopStoreInventoryCheckService;

    @Autowired
    private MaterialNormsCodeService materialNormsCodeService;

    @Override
    @IgnoreTenant
    public void queryShopStockList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 商品名称，型号，门店，品牌
        MPJLambdaWrapper<ShopStock> wrapper = JoinWrappers.lambda("stock", ShopStock.class)
            .innerJoin(Material.class, "ma", Material::getId, ShopStock::getMaterialId);
        if (StrUtil.equals(commonPageInfo.getType(), "store")) {
            // 门店id
            wrapper.eq(MybatisPlusUtil.toColumns(ShopStock::getStoreId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            wrapper.and(wra -> {
                wra.or().like(Material::getName, commonPageInfo.getKeyword());
                wra.or().like(Material::getModel, commonPageInfo.getKeyword());
            });
        }
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            wrapper.eq("stock." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("ma." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<ShopStock> shopStockList = skyeyeBaseMapper.selectJoinList(ShopStock.class, wrapper);
        materialService.setDataMation(shopStockList, ShopStock::getMaterialId);
        materialNormsService.setDataMation(shopStockList, ShopStock::getNormsId);
        iShopStoreService.setDataMation(shopStockList, ShopStock::getStoreId);

        outputObject.setBeans(shopStockList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryStoreInventoryCheckList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getCustomParamsMapStr("storeId");
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        // 分页按门店商品查，再展开规格；无 shop_stock 时账面为 0，便于首次盘点建账
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<ShopMaterialStore> relations = shopMaterialStoreService.selectByStoreId(
            storeId, WhetherEnum.ENABLE_USING.getKey(), null, pageInfo.getKeyword());
        if (CollectionUtil.isEmpty(relations)) {
            return;
        }
        List<String> materialIds = relations.stream().map(ShopMaterialStore::getMaterialId)
            .filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(
            StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        List<String> normsIds = new ArrayList<>();
        for (List<MaterialNorms> normsList : normsMap.values()) {
            if (CollectionUtil.isEmpty(normsList)) {
                continue;
            }
            for (MaterialNorms norms : normsList) {
                if (StrUtil.isNotBlank(norms.getId())) {
                    normsIds.add(norms.getId());
                }
            }
        }
        Map<String, String> stockMap = queryNormsShopStock(storeId, normsIds);
        List<ShopStock> rows = new ArrayList<>();
        for (ShopMaterialStore relation : relations) {
            List<MaterialNorms> normsList = normsMap.get(relation.getMaterialId());
            if (CollectionUtil.isEmpty(normsList)) {
                continue;
            }
            for (MaterialNorms norms : normsList) {
                if (StrUtil.isBlank(norms.getId())) {
                    continue;
                }
                ShopStock row = new ShopStock();
                row.setStoreId(storeId);
                row.setMaterialId(relation.getMaterialId());
                row.setNormsId(norms.getId());
                row.setStock(stockMap.getOrDefault(norms.getId(), CommonNumConstants.NUM_ZERO.toString()));
                rows.add(row);
            }
        }
        materialService.setDataMation(rows, ShopStock::getMaterialId);
        materialNormsService.setDataMation(rows, ShopStock::getNormsId);
        iShopStoreService.setDataMation(rows, ShopStock::getStoreId);
        outputObject.setBeans(rows);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void updateShopStock(String storeId, String materialId, String normsId, String operNumber, int type) {
        ShopStock shopStock = queryShopStock(storeId, normsId);
        // 如果该规格在指定门店中已经有存储数据，则直接做修改
        if (ObjectUtil.isNotEmpty(shopStock)) {
            String stock = shopStock.getStock();
            if (type == DepotPutOutType.PUT.getKey()) {
                // 入库
                stock = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, stock, operNumber);
            } else if (type == DepotPutOutType.OUT.getKey()) {
                // 出库
                stock = CalculationUtil.subtract(stock, operNumber, ErpConstants.NUM_AFTER_DOT);
            }
            editShopStock(storeId, normsId, stock);
        } else {
            String stockNum = CommonNumConstants.NUM_ZERO.toString();
            if (type == DepotPutOutType.PUT.getKey()) {
                // 入库
                stockNum = operNumber;
            } else if (type == DepotPutOutType.OUT.getKey()) {
                // 出库
                stockNum = CalculationUtil.subtract(stockNum, operNumber, ErpConstants.NUM_AFTER_DOT);
            }
            if (CalculationUtil.compareTo(stockNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                throw new CustomException("门店库存存量不足.");
            }
            saveShopStock(storeId, materialId, normsId, stockNum);
        }
    }

    @Override
    public ShopStock queryShopStock(String storeId, String normsId) {
        QueryWrapper<ShopStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStock::getStoreId), storeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStock::getNormsId), normsId);
        return getOne(queryWrapper);
    }

    private void editShopStock(String storeId, String normsId, String stock) {
        UpdateWrapper<ShopStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopStock::getStoreId), storeId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopStock::getNormsId), normsId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStock::getStock), stock);
        update(updateWrapper);
    }

    private void saveShopStock(String storeId, String materialId, String normsId, String stock) {
        ShopStock departmentStock = new ShopStock();
        departmentStock.setStoreId(storeId);
        departmentStock.setMaterialId(materialId);
        departmentStock.setNormsId(normsId);
        departmentStock.setStock(stock);
        save(departmentStock);
    }

    @Override
    public Map<String, String> queryNormsShopStock(String storeId, List<String> normsIds) {
        Map<String, String> stockMap = new HashMap<>();
        if (CollectionUtil.isEmpty(normsIds)) {
            return stockMap;
        }
        QueryWrapper<ShopStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStock::getStoreId), storeId);
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopStock::getNormsId), normsIds);
        List<ShopStock> departmentStockList = list(queryWrapper);

        stockMap = departmentStockList.stream()
            .collect(Collectors.toMap(ShopStock::getNormsId, ShopStock::getStock));
        for (String normsId : normsIds) {
            if (!stockMap.containsKey(normsId)) {
                stockMap.put(normsId, CommonNumConstants.NUM_ZERO.toString());
            }
        }
        return stockMap;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void executeStoreProductTransfer(InputObject inputObject, OutputObject outputObject) {
        StoreProductTransferExecute execute = inputObject.getParams(StoreProductTransferExecute.class);
        String fromStoreId = execute.getFromStoreId();
        String toStoreId = execute.getToStoreId();
        List<StoreProductTransferLink> applyLinkList = execute.getApplyLinkList();
        List<String> normsIds = applyLinkList.stream().map(StoreProductTransferLink::getNormsId).collect(Collectors.toList());
        Map<String, String> fromStoreStockMap = queryNormsShopStock(fromStoreId, normsIds);
        for (StoreProductTransferLink link : applyLinkList) {
            validateTransferStock(fromStoreId, link, fromStoreStockMap);
            updateShopStock(fromStoreId, link.getMaterialId(), link.getNormsId(), link.getOperNumber(), DepotPutOutType.OUT.getKey());
            updateShopStock(toStoreId, link.getMaterialId(), link.getNormsId(), link.getOperNumber(), DepotPutOutType.PUT.getKey());
        }
    }

    private void validateTransferStock(String fromStoreId, StoreProductTransferLink link, Map<String, String> fromStoreStockMap) {
        String normsId = link.getNormsId();
        String operNumber = link.getOperNumber();
        String fromStoreStock = fromStoreStockMap.get(normsId);
        if (StrUtil.isEmpty(fromStoreStock)) {
            fromStoreStock = CommonNumConstants.NUM_ZERO.toString();
        }
        String remainStock = CalculationUtil.subtract(fromStoreStock, operNumber, ErpConstants.NUM_AFTER_DOT);
        if (CalculationUtil.compareTo(remainStock, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
            throw new CustomException("原门店库存不足，无法调拨");
        }
        ShopStock fromShopStock = queryShopStock(fromStoreId, normsId);
        if (ObjectUtil.isEmpty(fromShopStock) || !StrUtil.equals(fromShopStock.getMaterialId(), link.getMaterialId())) {
            throw new CustomException("原门店不存在该规格库存，无法调拨");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void confirmStoreInventoryCheck(InputObject inputObject, OutputObject outputObject) {
        StoreInventoryCheckConfirm confirm = inputObject.getParams(StoreInventoryCheckConfirm.class);
        String storeId = confirm.getStoreId();
        List<StoreInventoryCheckItem> itemList = confirm.getItemList();
        if (StrUtil.isBlank(storeId) || CollectionUtil.isEmpty(itemList)) {
            throw new CustomException("盘点参数不完整");
        }
        List<String> materialIds = itemList.stream().map(StoreInventoryCheckItem::getMaterialId)
            .filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = CollectionUtil.isEmpty(materialIds)
            ? new HashMap<>()
            : materialService.selectMapByIds(materialIds);
        List<StoreInventoryCheckItem> changedList = new ArrayList<>();
        for (StoreInventoryCheckItem item : itemList) {
            if (StrUtil.isBlank(item.getMaterialId()) || StrUtil.isBlank(item.getNormsId()) || StrUtil.isBlank(item.getRealNumber())) {
                throw new CustomException("盘点明细参数不完整");
            }
            if (CalculationUtil.compareTo(item.getRealNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                throw new CustomException("实盘数量不能小于0");
            }
            ShopStock shopStock = queryShopStock(storeId, item.getNormsId());
            String current = ObjectUtil.isNotEmpty(shopStock) ? shopStock.getStock() : CommonNumConstants.NUM_ZERO.toString();
            if (StrUtil.isBlank(item.getBookStock())) {
                item.setBookStock(current);
            }
            // 增减 = 实盘 - 账面；盘盈/盘亏对齐仓库任务盘点公式：账面 + 盘盈 - 盘亏 = 实盘
            String diff = CalculationUtil.subtract(item.getRealNumber(), item.getBookStock(), ErpConstants.NUM_AFTER_DOT);
            String profitNum = CommonNumConstants.NUM_ZERO.toString();
            String lossNum = CommonNumConstants.NUM_ZERO.toString();
            if (CalculationUtil.compareTo(diff, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                profitNum = diff;
            } else if (CalculationUtil.compareTo(diff, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                lossNum = CalculationUtil.subtract(CommonNumConstants.NUM_ZERO.toString(), diff, ErpConstants.NUM_AFTER_DOT);
            }
            item.setProfitNum(profitNum);
            item.setLossNum(lossNum);

            Material material = materialMap.get(item.getMaterialId());
            if (material == null) {
                material = materialService.selectById(item.getMaterialId());
            }
            validateOneItemCodeBarcodes(item, material);
            // 一物一码：同步更新条码门店归属（对齐门店确认入库/退料）
            applyOneItemCodeBarcodes(storeId, item, material);

            int compare = CalculationUtil.compareTo(item.getRealNumber(), current, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            if (compare == 0) {
                continue;
            }
            if (compare > 0) {
                String delta = CalculationUtil.subtract(item.getRealNumber(), current, ErpConstants.NUM_AFTER_DOT);
                updateShopStock(storeId, item.getMaterialId(), item.getNormsId(), delta, DepotPutOutType.PUT.getKey());
            } else {
                String delta = CalculationUtil.subtract(current, item.getRealNumber(), ErpConstants.NUM_AFTER_DOT);
                updateShopStock(storeId, item.getMaterialId(), item.getNormsId(), delta, DepotPutOutType.OUT.getKey());
            }
            changedList.add(item);
        }
        if (CollectionUtil.isNotEmpty(changedList)) {
            shopStoreInventoryCheckService.saveInventoryCheckHistory(storeId, changedList);
        }
    }

    /**
     * 一物一码：盘盈/盘亏数量须与条码行数一致（对齐仓库任务盘点）
     */
    private void validateOneItemCodeBarcodes(StoreInventoryCheckItem item, Material material) {
        if (material == null || !MaterialItemCode.ONE_ITEM_CODE.getKey().equals(material.getItemCode())) {
            return;
        }
        String profitNum = StrUtil.blankToDefault(item.getProfitNum(), CommonNumConstants.NUM_ZERO.toString());
        String lossNum = StrUtil.blankToDefault(item.getLossNum(), CommonNumConstants.NUM_ZERO.toString());
        List<String> profitCodes = splitNormsCodes(item.getProfitNormsCode());
        List<String> lossCodes = splitNormsCodes(item.getLossNormsCode());
        String materialName = StrUtil.blankToDefault(item.getMaterialName(), material.getName());
        String normsName = StrUtil.blankToDefault(item.getNormsName(), item.getNormsId());
        if (CalculationUtil.compareTo(profitNum, CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) != 0) {
            if (Integer.parseInt(profitNum.contains(".") ? profitNum.substring(0, profitNum.indexOf('.')) : profitNum) != profitCodes.size()) {
                throw new CustomException(String.format("商品【%s】【%s】的盘盈数量与盘盈条码数量不一致，请确认",
                    materialName, normsName));
            }
        }
        if (CalculationUtil.compareTo(lossNum, CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) != 0) {
            if (Integer.parseInt(lossNum.contains(".") ? lossNum.substring(0, lossNum.indexOf('.')) : lossNum) != lossCodes.size()) {
                throw new CustomException(String.format("商品【%s】【%s】的盘亏数量与盘亏条码数量不一致，请确认",
                    materialName, normsName));
            }
        }
        item.setProfitNormsCode(String.join("\n", profitCodes));
        item.setLossNormsCode(String.join("\n", lossCodes));
    }

    /**
     * 门店一物一码盘盈/盘亏：更新条码门店归属（对齐确认入库 / 门店退料）
     */
    private void applyOneItemCodeBarcodes(String storeId, StoreInventoryCheckItem item, Material material) {
        if (material == null || !MaterialItemCode.ONE_ITEM_CODE.getKey().equals(material.getItemCode())) {
            return;
        }
        List<String> profitCodes = splitNormsCodes(item.getProfitNormsCode());
        List<String> lossCodes = splitNormsCodes(item.getLossNormsCode());
        if (CollectionUtil.isNotEmpty(profitCodes)) {
            handleStoreProfitNorms(storeId, item, profitCodes);
        }
        if (CollectionUtil.isNotEmpty(lossCodes)) {
            handleStoreLossNorms(storeId, item, lossCodes);
        }
    }

    private void handleStoreProfitNorms(String storeId, StoreInventoryCheckItem item, List<String> profitCodes) {
        // 盘盈：出库且未归属门店的条码，挂到当前门店（同门店确认入库）
        List<MaterialNormsCode> codeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, profitCodes,
            MaterialNormsCodeInDepot.OUTBOUND.getKey());
        codeList = codeList.stream()
            .filter(bean -> StrUtil.isEmpty(bean.getStoreId()))
            .filter(bean -> StrUtil.equals(item.getMaterialId(), bean.getMaterialId()))
            .filter(bean -> StrUtil.equals(item.getNormsId(), bean.getNormsId()))
            .collect(Collectors.toList());
        List<String> inSql = codeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
        List<String> diffList = profitCodes.stream().filter(num -> !inSql.contains(num)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(diffList)) {
            throw new CustomException(String.format(Locale.ROOT, "盘盈编码【%s】不存在/已在其他门店或状态不符，请确认",
                Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
        }
        codeList.forEach(code -> {
            code.setStoreId(storeId);
            code.setStoreUseState(StoreNormsCodeUseState.WAIT_USE.getKey());
        });
        materialNormsCodeService.updateEntityPick(codeList);
    }

    private void handleStoreLossNorms(String storeId, StoreInventoryCheckItem item, List<String> lossCodes) {
        // 盘亏：当前门店待使用条码，清除门店归属（同门店退料）
        List<MaterialNormsCode> codeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, lossCodes,
            MaterialNormsCodeInDepot.OUTBOUND.getKey());
        codeList = codeList.stream()
            .filter(bean -> StrUtil.equals(storeId, bean.getStoreId()))
            .filter(bean -> bean.getStoreUseState() != null
                && StoreNormsCodeUseState.WAIT_USE.getKey() == bean.getStoreUseState())
            .filter(bean -> StrUtil.equals(item.getMaterialId(), bean.getMaterialId()))
            .filter(bean -> StrUtil.equals(item.getNormsId(), bean.getNormsId()))
            .collect(Collectors.toList());
        List<String> inSql = codeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
        List<String> diffList = lossCodes.stream().filter(num -> !inSql.contains(num)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(diffList)) {
            throw new CustomException(String.format(Locale.ROOT, "盘亏编码【%s】不存在本门店或已被使用，请确认",
                Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
        }
        codeList.forEach(code -> {
            code.setStoreId(StrUtil.EMPTY);
            code.setStoreUseState(null);
        });
        materialNormsCodeService.updateEntityPick(codeList);
    }

    private List<String> splitNormsCodes(String codes) {
        if (StrUtil.isBlank(codes)) {
            return new ArrayList<>();
        }
        return Arrays.stream(codes.split("\\r?\\n"))
            .map(StrUtil::trim)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
    }

}
