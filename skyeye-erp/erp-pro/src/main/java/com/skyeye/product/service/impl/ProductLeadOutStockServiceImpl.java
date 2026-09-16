package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.entity.ErpOrderCommon;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.dao.ProductLeadOutStockDao;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "借出出库", groupName = "借出出库", flowable = true, orderApproval = true)
public class ProductLeadOutStockServiceImpl extends SkyeyeBusinessServiceImpl<ProductLeadOutStockDao, ProductLeadOutStock> implements ProductLeadOutStockService {

    @Autowired
    private SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ProductLeadService productLeadService;

    @Autowired
    private FarmService farmService;

    @Autowired
    protected ICustomerService iCustomerService;

    @Autowired
    protected SupplierService supplierService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Override
    public QueryWrapper<ProductLeadOutStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductLeadOutStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getIdKey), getServiceClassName());
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public void createPrepose(ProductLeadOutStock entity) {
        chectErpOrderItem(entity.getErpOrderItemList());
        entity.setIdKey(getServiceClassName());
        // 设置商品为使用中
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            materialService.setUsed(erpOrderItem.getMaterialId());
        });
        super.createPrepose(entity);
    }

    @Override
    protected void createPostpose(ProductLeadOutStock entity, String userId) {
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
        erpOrderItemList.forEach(
            erpOrderItem -> {
                erpOrderItem.setParentId(entity.getId());
            }
        );
        if (CollectionUtil.isNotEmpty(erpOrderItemList)) {
            skyeyeErpOrderItemService.createEntity(erpOrderItemList, userId);
        }
    }

    @Override
    protected void updatePostpose(ProductLeadOutStock entity, String userId) {
        String parentId = entity.getId();
        // 删除所有数据
        skyeyeErpOrderItemService.deleteByPId(parentId);
        // 拿到前端的数据
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
        erpOrderItemList.forEach(
            erpOrderItem -> erpOrderItem.setParentId(parentId)
        );
        if (CollectionUtil.isNotEmpty(erpOrderItemList)) {
            skyeyeErpOrderItemService.createEntity(erpOrderItemList, userId);
        }
    }

    private void chectErpOrderItem(List<ErpOrderItem> erpOrderItemList) {
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("请最少选择一条产品信息");
        }
        List<String> normsIds = erpOrderItemList.stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        if (erpOrderItemList.size() != normsIds.size()) {
            throw new CustomException("单据中不允许存在重复的产品规格信息");
        }
    }

    @Override
    public void updatePrepose(ProductLeadOutStock entity) {
        chectErpOrderItem(entity.getErpOrderItemList());
        super.updatePrepose(entity);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        productLeadService.setMationForMap(beans, "fromId", "fromMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        beans.forEach(
            bean -> {
                String holderKey = bean.get("holderKey").toString();
                if (StrUtil.equals(holderKey, CorrespondentEnterEnum.CUSTOM.getKey())) {
                    iCustomerService.setMationForMap(bean, "holderId", "holderMation");
                } else {
                    supplierService.setMationForMap(bean, "holderId", "holderMation");
                }
            }
        );
        return beans;
    }

    @Override
    public void validatorEntity(ProductLeadOutStock entity) {
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("该借出出库订单没有商品信息");
        }
        erpOrderItemList.forEach(
            e -> {
                String depotId = e.getDepotId();
                if (StrUtil.isEmpty(depotId)) {
                    throw new CustomException("商品[" + e.getMaterialMation().getName() + "]没有选择仓库");
                }
            }
        );
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
    }

    @Override
    public ProductLeadOutStock selectById(String id) {
        ProductLeadOutStock productLeadOutStock = super.selectById(id);
        List<ErpOrderItem> erpOrderItemList = skyeyeErpOrderItemService.selectByPId(id);
        productLeadOutStock.setErpOrderItemList(erpOrderItemList);
        productLeadService.setDataMation(productLeadOutStock, ProductLeadOutStock::getFromId);
        farmService.setDataMation(productLeadOutStock, ProductLeadOutStock::getFarmId);
        iProProjectService.setDataMation(productLeadOutStock, ProductLeadOutStock::getProjectId);
        erpDepotService.setDataMation(productLeadOutStock.getErpOrderItemList(), ErpOrderItem::getDepotId);
        materialNormsService.setDataMation(productLeadOutStock.getErpOrderItemList(), ErpOrderItem::getNormsId);
        materialService.setDataMation(productLeadOutStock.getErpOrderItemList(), ErpOrderItem::getMaterialId);
        if (productLeadOutStock.getHolderKey().equals(CorrespondentEnterEnum.CUSTOM.getKey())) {
            iCustomerService.setDataMation(productLeadOutStock, ProductLeadOutStock::getHolderId);
        } else {
            supplierService.setDataMation(productLeadOutStock, ProductLeadOutStock::getHolderId);
        }
        return productLeadOutStock;
    }


    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取借出出库单状态
        ProductLeadOutStock productLeadOutStock = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(productLeadOutStock)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(productLeadOutStock.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.LOANOUT.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }

    @Override
    public List<ProductLeadOutStock> queryLeadByHolderId(String holderId) {
        QueryWrapper<ProductLeadOutStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getHolderId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ProductLeadOutStock::getCreateTime));
        List<ProductLeadOutStock> list = list(queryWrapper);
        List<String> productLeadOutStockIds = list.stream().map(ProductLeadOutStock::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(productLeadOutStockIds)) {
            return list;
        }
        List<ErpOrderItem> erpOrderItems = skyeyeErpOrderItemService.queryErpOrderItemByPIds(productLeadOutStockIds);
        Map<String, List<ErpOrderItem>> stringListMap = erpOrderItems.stream().collect(Collectors.groupingBy(ErpOrderItem::getParentId));
        list.forEach(productLeadOutStock ->
            productLeadOutStock.setErpOrderItemList(stringListMap.get(productLeadOutStock.getId())));
        return list(queryWrapper);
    }

    @Override
    public List<ProductLeadOutStock> queryByIds(List<String> framIds) {
        if (CollectionUtil.isEmpty(framIds)) {
            return null;
        }
        QueryWrapper<ProductLeadOutStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, framIds);
        return list(queryWrapper);
    }

    @Override
    public void editOtherState(String fromId, Integer key) {
        UpdateWrapper<ProductLeadOutStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, fromId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), key);
        update(updateWrapper);
        refreshCache(fromId);
    }

    @Override
    protected void approvalEndIsSuccess(ProductLeadOutStock entity) {
        super.approvalEndIsSuccess(entity);
        productLeadService.updateLeadType(entity.getFromId());
    }

    @Override
    public void deletePostpose(String id) {
        skyeyeErpOrderItemService.deleteByPId(id);
    }
}
