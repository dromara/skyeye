package com.skyeye.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.classenum.ProductLeadFromType;
import com.skyeye.product.dao.ProductLeadDao;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.entity.ProductLeadChild;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadChildService;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "借出申请", groupName = "借出申请", flowable = true, orderApproval = true)
public class ProductLeadServiceImpl extends SkyeyeBusinessServiceImpl<ProductLeadDao, ProductLead> implements ProductLeadService {

    @Autowired
    private ProductLeadChildService productLeadChildService;

    @Autowired
    private ProductLeadOutStockService productLeadOutStockService;

    @Autowired
    private ICustomerService iCustomerService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SupplierService supplierService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
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
    public void createPrepose(ProductLead entity) {
        super.createPrepose(entity);
        getTotalPrice(entity);
    }

    @Override
    protected void updatePrepose(ProductLead entity) {
        super.updatePrepose(entity);
        getTotalPrice(entity);
    }

    @Override
    protected void updatePostpose(ProductLead entity, String userId) {
        productLeadChildService.deleteByParentId(entity.getId());
        List<ProductLeadChild> erpOrderItemList = entity.getErpOrderItemList();
        erpOrderItemList.forEach(child -> child.setParentId(entity.getId()));
        productLeadChildService.createEntity(erpOrderItemList, userId);
    }


    private void getTotalPrice(ProductLead entity) {
        String totalPrice = productLeadChildService.calcOrderAllTotalPrice(entity.getErpOrderItemList());
        entity.setTotalPrice(totalPrice);
    }

    @Override
    public void writePostpose(ProductLead entity, String userId) {
        productLeadChildService.saveList(entity.getId(), entity.getErpOrderItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        productLeadChildService.deleteByParentId(id);
    }

    @Autowired
    private ErpDepotService erpDepotService;

    @Override
    public ProductLead selectById(String id) {
        ProductLead productLead = super.selectById(id);
        List<ProductLeadChild> productLeadChildren = productLeadChildService.selectProductLeadChildById(id);
        productLead.setErpOrderItemList(productLeadChildren);
        materialNormsService.setDataMation(productLead.getErpOrderItemList(), ProductLeadChild::getNormsId);
        materialService.setDataMation(productLead.getErpOrderItemList(), ProductLeadChild::getMaterialId);
        erpDepotService.setDataMation(productLead.getErpOrderItemList(), ProductLeadChild::getDepotId);
        if (productLead.getHolderKey().equals(CorrespondentEnterEnum.CUSTOM.getKey())) {
            iCustomerService.setDataMation(productLead, ProductLead::getHolderId);
        } else {
            supplierService.setDataMation(productLead, ProductLead::getHolderId);
        }
        return productLead;
    }

    @Override
    public void productLeadToContractOutStock(InputObject inputObject, OutputObject outputObject) {
        ProductLeadOutStock productLeadOutStock = inputObject.getParams(ProductLeadOutStock.class);
        productLeadOutStock.setFromId(productLeadOutStock.getId());
        productLeadOutStock.setFromTypeId(ProductLeadFromType.LOANAPPLICATIONFORM.getKey());
        productLeadOutStock.setId(StrUtil.EMPTY);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        productLeadOutStockService.createEntity(productLeadOutStock, userId);
    }

    @Override
    public void updateLeadType(String farmId) {
        UpdateWrapper<ProductLead> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, farmId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ProductLead::getOtherState), IsDefaultEnum.IS_DEFAULT.getKey());
        update(updateWrapper);
    }

}
