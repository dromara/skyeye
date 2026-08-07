package com.skyeye.equipmentcheck.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentcheck.classenum.EquipmentCheckResult;
import com.skyeye.equipmentcheck.dao.EquipmentCheckOrderDao;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrder;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderItemService;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderService;
import com.skyeye.equipmentcheckstandard.service.EquipmentCheckStandardService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.repair.classenum.EquipmentRepairFromType;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentCheckOrderServiceImpl
 * @Description: 设备点检单服务实现层
 */
@Service
@SkyeyeService(name = "设备点检单", groupName = "设备点检", flowable = true)
public class EquipmentCheckOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentCheckOrderDao, EquipmentCheckOrder>
    implements EquipmentCheckOrderService {

    @Autowired
    private EquipmentCheckOrderItemService equipmentCheckOrderItemService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentCheckStandardService equipmentCheckStandardService;

    @Autowired
    private FarmService farmService;

    @Autowired
    @Lazy
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Override
    protected QueryWrapper<EquipmentCheckOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentCheckOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getEquipmentId), commonPageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckTime));
        return queryWrapper;
    }

    @Override
    public void createPrepose(EquipmentCheckOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business));
    }

    // 点检单编号由后端生成，编辑时强制保留原编号，前端传值无效。
    @Override
    public void updatePrepose(EquipmentCheckOrder entity) {
        super.updatePrepose(entity);
        EquipmentCheckOrder oldEntity = getDataFromDb(entity.getId());
        entity.setOddNumber(oldEntity.getOddNumber());
    }

    @Override
    public void validatorEntity(EquipmentCheckOrder entity) {
        if (CollectionUtil.isEmpty(entity.getItemList())) {
            throw new CustomException("点检项目明细至少保留一条记录.");
        }
    }

    @Override
    public EquipmentCheckOrder getDataFromDb(String id) {
        EquipmentCheckOrder entity = super.getDataFromDb(id);
        entity.setItemList(equipmentCheckOrderItemService.selectByParentId(id));
        return entity;
    }

    @Override
    public void writePostpose(EquipmentCheckOrder entity, String userId) {
        equipmentCheckOrderItemService.saveList(entity.getId(), entity.getItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        equipmentCheckOrderItemService.deleteByParentId(id);
    }

    @Override
    protected void deletePostpose(List<String> ids) {
        super.deletePostpose(ids);
        if (CollectionUtil.isNotEmpty(ids)) {
            ids.forEach(equipmentCheckOrderItemService::deleteByParentId);
        }
    }

    @Override
    public EquipmentCheckOrder selectById(String id) {
        EquipmentCheckOrder order = super.selectById(id);
        equipmentService.setDataMation(order, EquipmentCheckOrder::getEquipmentId);
        equipmentCheckStandardService.setDataMation(order, EquipmentCheckOrder::getStandardId);
        iAuthUserService.setDataMation(order, EquipmentCheckOrder::getCheckerId);
        farmService.setDataMation(order, EquipmentCheckOrder::getPosition);
        return order;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        equipmentCheckStandardService.setMationForMap(beans, "standardId", "standardMation");
        iAuthUserService.setMationForMap(beans, "checkerId", "checkerMation");
        farmService.setMationForMap(beans, "position", "positionMation");
        return beans;
    }

    // 点检审批通过：异常固定映射为带病运行；正常固定映射为正常运行。
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approvalEndIsSuccess(EquipmentCheckOrder entity) {
        EquipmentCheckOrder order = selectById(entity.getId());
        if (EquipmentCheckResult.ABNORMAL.getKey().equals(order.getCheckResult())) {
            equipmentService.editEquipmentStateById(order.getEquipmentId(), EquipmentState.DEGRADED.getKey());
        } else if (EquipmentCheckResult.NORMAL.getKey().equals(order.getCheckResult())) {
            equipmentService.editEquipmentStateById(order.getEquipmentId(), EquipmentState.NORMAL.getKey());
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertCheckOrderToRepair(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairOrder repairOrder = inputObject.getParams(EquipmentRepairOrder.class);
        EquipmentCheckOrder checkOrder = selectById(repairOrder.getId());
        if (ObjectUtil.isEmpty(checkOrder)) {
            throw new CustomException("该数据不存在.");
        }
        if (StrUtil.isNotEmpty(checkOrder.getRepairOrderId())) {
            throw new CustomException("该点检单已转维修单，无法重复转单.");
        }
        // 点检结果异常才可以转维修
        if (EquipmentCheckResult.ABNORMAL.getKey().equals(checkOrder.getCheckResult())) {
            String userId = inputObject.getLogParams().get("id").toString();
            repairOrder.setFromId(repairOrder.getId());
            repairOrder.setFromTypeId(EquipmentRepairFromType.CHECK_ORDER.getKey());
            repairOrder.setId(StrUtil.EMPTY);
            String repairOrderId = equipmentRepairOrderService.createEntity(repairOrder, userId);
            updateRepairOrderId(checkOrder.getId(), repairOrderId);
        } else {
            outputObject.setreturnMessage("点检结果非异常，无法转维修单.");
        }
    }

    private void updateRepairOrderId(String id, String repairOrderId) {
        UpdateWrapper<EquipmentCheckOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getRepairOrderId), repairOrderId);
        update(updateWrapper);
        refreshCache(id);
    }
}

