/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderHead;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shop.classenum.StoreNormsCodeUseState;
import com.skyeye.shop.dao.ShopReturnsDao;
import com.skyeye.shop.entity.ShopReturns;
import com.skyeye.shop.service.ShopReturnsService;
import com.skyeye.shop.service.ShopStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopReturnsServiceImpl
 * @Description: 门店退货单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "门店退货单", groupName = "门店", flowable = true, orderApproval = true)
public class ShopReturnsServiceImpl extends SkyeyeErpOrderServiceImpl<ShopReturnsDao, ShopReturns> implements ShopReturnsService {

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Autowired
    private ShopStockService shopStockService;

    @Override
    public QueryWrapper<ShopReturns> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopReturns> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            commonPageInfo.setObjectId("-");
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderHead::getStoreId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        iShopStoreService.setMationForMap(beans, "storeId", "storeMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");
        return beans;
    }

    @Override
    public void validatorEntity(ShopReturns entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
        checkMaterialNorms(entity);
        checkNormsCodeAndSave(entity, true);
    }

    @Override
    public void createPrepose(ShopReturns entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void writePostpose(ShopReturns entity, String userId) {
        // 保存单据子表关联的条形码编号信息
        super.saveErpOrderItemCode(entity);
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        // 删除关联的编码信息
        super.deleteErpOrderItemCodeById(id);
    }

    @Override
    public ShopReturns getDataFromDb(String id) {
        ShopReturns shopReturns = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(shopReturns);
        return shopReturns;
    }

    @Override
    public ShopReturns selectById(String id) {
        ShopReturns returnPut = super.selectById(id);
        // 部门
        iDepmentService.setDataMation(returnPut, ShopReturns::getDepartmentId);
        // 门店
        iShopStoreService.setDataMation(returnPut, ShopReturns::getStoreId);
        // 业务员
        iAuthUserService.setDataMation(returnPut, ShopReturns::getSalesman);
        return returnPut;
    }

    private void checkMaterialNorms(ShopReturns entity) {
        // 和当前门店的库存做对比
        checkStoreStockWhetherOutstrip(entity.getStoreId(), entity.getErpOrderItemList());
    }

    @Override
    public void approvalEndIsSuccess(ShopReturns entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity);
        // 校验并修改条形码信息
        checkNormsCodeAndSave(entity, false);
    }

    private void checkStoreStockWhetherOutstrip(String storeId, List<ErpOrderItem> erpOrderItemList) {
        List<String> normsIds = erpOrderItemList.stream().map(ErpOrderItem::getNormsId).collect(Collectors.toList());
        Map<String, String> normsStoreStock = shopStockService.queryNormsShopStock(storeId, normsIds);
        for (ErpOrderItem bean : erpOrderItemList) {
            // 门店库存存量
            String departMentTock = normsStoreStock.get(bean.getNormsId());
            if (StrUtil.isEmpty(departMentTock)) {
                departMentTock = CommonNumConstants.NUM_ZERO.toString();
            }
            // 单据数量 小于 仓储数量
            String subtractResult = CalculationUtil.subtract(departMentTock, bean.getOperNumber(), ErpConstants.NUM_AFTER_DOT);
            if (CalculationUtil.compareTo(subtractResult, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                throw new CustomException("单据储量小于仓储数量，请确认");
            }
        }
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 从门店的库存入库到仓库的库存
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected List<String> checkNormsCodeAndSave(ShopReturns entity, Boolean onlyCheck) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行入库的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 1. 和数据库中条形码的状态做对比
            //  1.1 从数据库查询出库状态的条形码信息，
            //  1.2 只有门店信息不为空的说明没有退料，才可以进行退料。门店信息为空，说明该条形码还未进行领料
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.OUTBOUND.getKey());
            //  1.3 过滤出当前门店的库存
            materialNormsCodeList = materialNormsCodeList.stream()
                .filter(bean -> StrUtil.equals(entity.getStoreId(), bean.getStoreId())).collect(Collectors.toList());
            //  1.4 只有未使用的可以退料
            materialNormsCodeList = materialNormsCodeList.stream()
                .filter(bean -> StoreNormsCodeUseState.WAIT_USE.getKey() == bean.getStoreUseState())
                .collect(Collectors.toList());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在或不存在【门店】仓库中或已被使用，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                materialNormsCodeList.forEach(materialNormsCode -> {
                    materialNormsCode.setStoreId(StrUtil.EMPTY);
                    materialNormsCode.setStoreUseState(null);
                });
                materialNormsCodeService.updateEntityPick(materialNormsCodeList);
            }
        }
        if (!onlyCheck) {
            // 修改门店的库存
            entity.getErpOrderItemList().forEach(erpOrderItem -> {
                shopStockService.updateShopStock(entity.getStoreId(), erpOrderItem.getMaterialId(),
                    erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.OUT.getKey());
            });
        }
        return allNormsCodeList;
    }

    @Override
    public void queryShopReturnsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ShopReturns shopReturns = selectById(id);
        // 该门店退货单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(shopReturns.getId());
        // 设置未下达商品数量-----门店退货单数量 - 已入库数量
        super.setOrCheckOperNumber(shopReturns.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        shopReturns.setErpOrderItemList(shopReturns.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(shopReturns);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertShopReturnsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取门店退货单状态
        ShopReturns shopReturns = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(shopReturns)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(shopReturns.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.SHOP_RETURNS.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
