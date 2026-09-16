/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.classenum.ProductReturnFromType;
import com.skyeye.product.dao.ProductReturnDao;
import com.skyeye.product.entity.ProductReturn;
import com.skyeye.product.entity.ProductReturnChild;
import com.skyeye.product.entity.ProductReturnInStock;
import com.skyeye.product.service.ProductReturnChildService;
import com.skyeye.product.service.ProductReturnInStockService;
import com.skyeye.product.service.ProductReturnService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "归还申请", groupName = "归还申请", flowable = true, orderApproval = true)
public class ProductReturnServiceImpl extends SkyeyeBusinessServiceImpl<ProductReturnDao, ProductReturn> implements ProductReturnService {

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private ProductReturnChildService productReturnChildService;

    @Autowired
    private ProductReturnInStockService productReturnInStockService;

    @Autowired
    private ICustomerService iCustomerService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
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
    public void createPrepose(ProductReturn entity) {
        super.createPrepose(entity);
        getTotalPrice(entity);
        checkForLentOutItems(entity);
    }

    @Override
    protected void updatePrepose(ProductReturn entity) {
        super.updatePrepose(entity);
        getTotalPrice(entity);
        checkForLentOutItems(entity);
    }

    @Override
    protected void updatePostpose(ProductReturn entity, String userId) {
        productReturnChildService.deleteByParentId(entity.getId());
        List<ProductReturnChild> erpOrderItemList = entity.getErpOrderItemList();
        erpOrderItemList.forEach(child -> child.setParentId(entity.getId()));
        productReturnChildService.createEntity(erpOrderItemList, userId);
    }

    private void checkForLentOutItems(ProductReturn entity) {
        List<ProductReturnChild> erpOrderItemList = entity.getErpOrderItemList();
        erpOrderItemList.forEach(
            item -> {
                if (Double.parseDouble(item.getOperNumber()) <= CommonNumConstants.NUM_ZERO) {
                    throw new CustomException("归还数量不能小于等于0");
                }
            }
        );
    }

    private void getTotalPrice(ProductReturn entity) {
        String totalPrice = productReturnChildService.calcOrderAllTotalPrice(entity.getErpOrderItemList());
        entity.setTotalPrice(totalPrice);
    }

    @Override
    public void writePostpose(ProductReturn entity, String userId) {
        productReturnChildService.saveList(entity.getId(), entity.getErpOrderItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        productReturnChildService.deleteByParentId(id);
    }

    @Override
    public ProductReturn selectById(String id) {
        ProductReturn productReturn = super.selectById(id);
        List<ProductReturnChild> productLeadChildren = productReturnChildService.selectProductLeadChildById(id);
        productReturn.setErpOrderItemList(productLeadChildren);
        materialNormsService.setDataMation(productReturn.getErpOrderItemList(), ProductReturnChild::getNormsId);
        materialService.setDataMation(productReturn.getErpOrderItemList(), ProductReturnChild::getMaterialId);
        erpDepotService.setDataMation(productReturn.getErpOrderItemList(), ProductReturnChild::getDepotId);
        if (productReturn.getHolderKey().equals(CorrespondentEnterEnum.CUSTOM.getKey())) {
            iCustomerService.setDataMation(productReturn, ProductReturn::getHolderId);
        } else {
            supplierService.setDataMation(productReturn, ProductReturn::getHolderId);
        }
        return productReturn;
    }

    @Override
    public void productLeadToContractOutStock(InputObject inputObject, OutputObject outputObject) {
        ProductReturnInStock productReturnInStock = inputObject.getParams(ProductReturnInStock.class);
        productReturnInStock.setFromId(productReturnInStock.getId());
        productReturnInStock.setFromTypeId(ProductReturnFromType.RETURNAPPLICATIONFORM.getKey());
        productReturnInStock.setId(null);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        productReturnInStockService.createEntity(productReturnInStock, userId);
    }

    @Override
    public void updateOtherState(String fromId) {
        UpdateWrapper<ProductReturn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, fromId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ProductReturn::getOtherState), IsDefaultEnum.IS_DEFAULT.getKey());
        update(updateWrapper);
    }

}
