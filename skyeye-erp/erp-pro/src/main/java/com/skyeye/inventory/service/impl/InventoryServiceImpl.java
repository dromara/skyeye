/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.inventory.dao.InventoryDao;
import com.skyeye.inventory.entity.Inventory;
import com.skyeye.inventory.entity.InventoryChild;
import com.skyeye.inventory.service.InventoryChildCodeService;
import com.skyeye.inventory.service.InventoryChildService;
import com.skyeye.inventory.service.InventoryService;
import com.skyeye.material.classenum.MaterialNormsCodeType;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: InventoryServiceImpl
 * @Description: 盘点任务单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 15:42
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "盘点任务单", groupName = "盘点任务单", flowable = true, orderApproval = true)
public class InventoryServiceImpl extends SkyeyeBusinessServiceImpl<InventoryDao, Inventory> implements InventoryService {

    @Autowired
    private InventoryChildService inventoryChildService;

    @Autowired
    private InventoryChildCodeService inventoryChildCodeService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Override
    protected void validatorEntity(Inventory entity) {
        chectErpOrderItem(entity.getInventoryChildList());
        String allPlanInventoryNum = inventoryChildService.calcAllPlanInventoryNum(entity.getInventoryChildList());
        entity.setAllNum(allPlanInventoryNum);
        entity.setInventoryNum(CommonNumConstants.NUM_ZERO.toString());
    }

    private void chectErpOrderItem(List<InventoryChild> inventoryChildList) {
        if (CollectionUtil.isEmpty(inventoryChildList)) {
            throw new CustomException("请最少选择一条产品信息");
        }
        List<String> normsIds = inventoryChildList.stream()
            .map(bean -> String.format("%s_%s", bean.getDepotId(), bean.getNormsId()))
            .distinct().collect(Collectors.toList());
        if (inventoryChildList.size() != normsIds.size()) {
            throw new CustomException("单据中不允许存在重复仓库的产品规格信息");
        }
    }

    @Override
    public void writePostpose(Inventory entity, String userId) {
        // 删除关联的条形码数据
        inventoryChildCodeService.deleteByOrderId(entity.getId());
        // 保存子表数据
        inventoryChildService.saveLinkList(entity.getId(), entity.getInventoryChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public Inventory getDataFromDb(String id) {
        Inventory inventory = super.getDataFromDb(id);
        List<InventoryChild> inventoryChildList = inventoryChildService.selectByPId(inventory.getId());
        inventory.setInventoryChildList(inventoryChildList);
        return inventory;
    }

    @Override
    public Inventory selectById(String id) {
        Inventory inventory = super.selectById(id);
        // 设置产品信息
        materialService.setDataMation(inventory.getInventoryChildList(), InventoryChild::getMaterialId);
        inventory.getInventoryChildList().forEach(inventoryChild -> {
            MaterialNorms norms = inventoryChild.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(inventoryChild.getNormsId(), bean.getId())).findFirst().orElse(null);
            inventoryChild.setNormsMation(norms);
            inventoryChild.setTypeMation(MaterialNormsCodeType.getMation(inventoryChild.getType()));
        });
        // 盘点人信息
        iAuthUserService.setDataMation(inventory.getInventoryChildList(), InventoryChild::getOperatorId);
        // 仓库信息
        erpDepotService.setDataMation(inventory.getInventoryChildList(), InventoryChild::getDepotId);
        return inventory;
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        inventoryChildService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public void revokePostpose(Inventory entity) {
        super.revokePostpose(entity);
        inventoryChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsFailed(Inventory entity) {
        inventoryChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(Inventory entity) {
        inventoryChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子表数据
        inventoryChildService.deleteByPId(id);
        // 删除关联的条形码数据
        inventoryChildCodeService.deleteByOrderId(id);
    }

    @Override
    public void setInventoriedNum(String id, String addNum) {
        Inventory inventory = selectById(id);
        UpdateWrapper<Inventory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Inventory::getInventoryNum), CalculationUtil.add(inventory.getInventoryNum(), addNum, ErpConstants.NUM_AFTER_DOT));
        update(updateWrapper);
        refreshCache(id);
    }
}
