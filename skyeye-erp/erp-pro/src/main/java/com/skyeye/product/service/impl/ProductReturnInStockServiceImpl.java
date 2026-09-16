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
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotOutPutRecordService;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.dao.ProductReturnInStockDao;
import com.skyeye.product.entity.ProductReturnInStock;
import com.skyeye.product.service.ProductReturnInStockService;
import com.skyeye.product.service.ProductReturnService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "归还入库", groupName = "归还入库", flowable = true, orderApproval = true)
public class ProductReturnInStockServiceImpl extends SkyeyeBusinessServiceImpl<ProductReturnInStockDao, ProductReturnInStock> implements ProductReturnInStockService {

    @Autowired
    private SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private ProductReturnService productReturnService;

    @Autowired
    protected ICustomerService iCustomerService;

    @Autowired
    protected SupplierService supplierService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private DepotOutPutRecordService depotOutPutRecordService;

    @Override
    public QueryWrapper<ProductReturnInStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductReturnInStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnInStock::getIdKey), getServiceClassName());
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnInStock::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnInStock::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public void createPrepose(ProductReturnInStock entity) {
        chectErpOrderItem(entity.getErpOrderItemList());
        entity.setIdKey(getServiceClassName());
        // 设置商品为使用中
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            materialService.setUsed(erpOrderItem.getMaterialId());
        });
        super.createPrepose(entity);
    }

    @Override
    protected void createPostpose(ProductReturnInStock entity, String userId) {
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
    protected void updatePostpose(ProductReturnInStock entity, String userId) {
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
    public void updatePrepose(ProductReturnInStock entity) {
        chectErpOrderItem(entity.getErpOrderItemList());
        super.updatePrepose(entity);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        productReturnService.setMationForMap(beans, "fromId", "fromMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
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
    public void validatorEntity(ProductReturnInStock entity) {
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("该归还入库订单没有商品信息");
        }
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
    }

    @Override
    public ProductReturnInStock selectById(String id) {
        ProductReturnInStock productReturnInStock = super.selectById(id);
        List<ErpOrderItem> erpOrderItemList = skyeyeErpOrderItemService.selectByPId(id);
        productReturnInStock.setErpOrderItemList(erpOrderItemList);
        productReturnService.setDataMation(productReturnInStock, ProductReturnInStock::getFromId);
        farmService.setDataMation(productReturnInStock, ProductReturnInStock::getFarmId);
        iProProjectService.setDataMation(productReturnInStock, ProductReturnInStock::getProjectId);
        erpDepotService.setDataMation(productReturnInStock.getErpOrderItemList(), ErpOrderItem::getDepotId);
        materialNormsService.setDataMation(productReturnInStock.getErpOrderItemList(), ErpOrderItem::getNormsId);
        materialService.setDataMation(productReturnInStock.getErpOrderItemList(), ErpOrderItem::getMaterialId);
        if (productReturnInStock.getHolderKey().equals(CorrespondentEnterEnum.CUSTOM.getKey())) {
            iCustomerService.setDataMation(productReturnInStock, ProductReturnInStock::getHolderId);
        } else {
            supplierService.setDataMation(productReturnInStock, ProductReturnInStock::getHolderId);
        }
        return productReturnInStock;
    }

    @Override
    protected void approvalEndIsSuccess(ProductReturnInStock entity) {
        super.approvalEndIsSuccess(entity);
        productReturnService.updateOtherState(entity.getFromId());
    }

    @Override
    public void deletePostpose(String id) {
        skyeyeErpOrderItemService.deleteByPId(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertProductReturnInStockToInDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        ProductReturnInStock productReturnInStock = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(productReturnInStock)) {
            throw new CustomException("该数据不存在.");
        }
        depotOutPutRecordService.checkOutPutRecord(depotPut.getErpOrderItemList(), depotPut.getHolderId());
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(productReturnInStock.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.LOANIN.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }

    @Override
    public void updateOtherState(String fromId) {
        UpdateWrapper<ProductReturnInStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, fromId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ProductReturnInStock::getOtherState), IsDefaultEnum.IS_DEFAULT.getKey());
        update(updateWrapper);
    }

    @Override
    public void editOtherState(String fromId, Integer key) {
        UpdateWrapper<ProductReturnInStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, fromId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ProductReturnInStock::getOtherState), key);
        update(updateWrapper);
    }
}
