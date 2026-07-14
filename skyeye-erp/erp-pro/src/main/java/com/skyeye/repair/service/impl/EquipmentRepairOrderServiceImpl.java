/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.depot.classenum.DepotPutOutType;
import cn.hutool.json.JSONUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.repair.classenum.EquipmentFaultCategory;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairFaultReason;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.classenum.EquipmentRepairTeam;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartUsageDetailService;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentRepairOrderServiceImpl
 * @Description: 设备维修单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备维修单", groupName = "设备维修")
public class EquipmentRepairOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentRepairOrderDao, EquipmentRepairOrder>
    implements EquipmentRepairOrderService {

    @Autowired
    private EquipmentSparePartUsageDetailService equipmentSparePartUsageDetailService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentMaintainOrderService equipmentMaintainOrderService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IServiceUserStockService iServiceUserStockService;

    @Override
    public QueryWrapper<EquipmentRepairOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String state = commonPageInfo.getState();

        if (StrUtil.isNotEmpty(state)) {
            // 报修人
            if (StrUtil.equals(state, "myCreate")) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUserId), userId);
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.PENDING_ORDERS.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_COMPLETED.getKey()))) {
                // 待接单、待完工 - 仅维修负责人可见
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), Integer.valueOf(state));
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.AUDIT.getKey()))) {
                // 待确认 - 仅报修人可见
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.AUDIT.getKey());
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_DISPATCHED.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_EVALUATED.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.COMPLATE.getKey()))) {
                // 待派工、待评价、已完工 - 查询所有该状态的工单
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), Integer.valueOf(state));
            }
        }

        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            // 嵌套在保养任务详情：objectId = 保养任务id
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getMaintainOrderId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEquipmentId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public EquipmentRepairOrder getDataFromDb(String id) {
        EquipmentRepairOrder order = super.getDataFromDb(id);
        order.setSparePartUsageList(equipmentSparePartUsageDetailService.selectByParentId(id));
        return order;
    }

    @Override
    public EquipmentRepairOrder selectById(String id) {
        EquipmentRepairOrder order = super.selectById(id);
        if (order == null) {
            return null;
        }
        equipmentService.setDataMation(order, EquipmentRepairOrder::getEquipmentId);
        equipmentMaintainOrderService.setDataMation(order, EquipmentRepairOrder::getMaintainOrderId);
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getUserId);
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getServiceUserId);
        supplierService.setDataMation(order, EquipmentRepairOrder::getSupplierId);
        iSysDictDataService.setDataMation(order, EquipmentRepairOrder::getUrgencyId);
        iSysDictDataService.setDataMation(order, EquipmentRepairOrder::getEvaluateTypeId);
        order.setStateMation(EquipmentRepairOrderState.getMation(order.getState()));
        order.setFaultTypeMation(EquipmentFaultCategory.getMation(order.getFaultType()));
        order.setRepairTeamMation(EquipmentRepairTeam.getMation(order.getRepairTeam()));
        order.setAuditOpinionMation(EquipmentRepairAuditOpinion.getMation(order.getAuditOpinion()));
        order.setFaultReasonMation(EquipmentRepairFaultReason.getMation(order.getFaultReason()));
        if (CollectionUtil.isEmpty(order.getSparePartUsageList())) {
            return order;
        }
        materialService.setDataMation(order.getSparePartUsageList(), EquipmentSparePartUsageDetail::getMaterialId);
        materialNormsService.setDataMation(order.getSparePartUsageList(), EquipmentSparePartUsageDetail::getNormsId);
        List<String> normsIds = order.getSparePartUsageList().stream()
            .map(EquipmentSparePartUsageDetail::getNormsId)
            .collect(Collectors.toList());
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        Map<String, Map<String, Object>> serviceUserStockMap = iServiceUserStockService.queryUserStock(currentUserId, normsIds);
        order.getSparePartUsageList().forEach(detail ->
            detail.setServiceUserStock(serviceUserStockMap.get(detail.getNormsId())));
        return order;
    }

    @Override
    public void createPrepose(EquipmentRepairOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        entity.setUserId(InputObject.getLogParamsStatic().get("id").toString());
        entity.setReportTime(DateUtil.getTimeAndToString());
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(EquipmentRepairOrderState.BE_DISPATCHED.getKey());
            entity.setServiceTime(null);
        } else {
            entity.setState(EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
    }

    @Override
    protected void updatePrepose(EquipmentRepairOrder entity) {
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(EquipmentRepairOrderState.BE_DISPATCHED.getKey());
            entity.setServiceTime(null);
        } else {
            entity.setState(EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            if (StrUtil.isEmpty(entity.getServiceTime())) {
                entity.setServiceTime(DateUtil.getTimeAndToString());
            }
        }
    }

    @Override
    protected void writePostpose(EquipmentRepairOrder entity, String userId) {
        super.writePostpose(entity, userId);
        if (!EquipmentRepairOrderState.BE_COMPLETED.getKey().equals(entity.getState())) {
            sendDispatchWork(entity.getId(), userId);
        }
    }

    private void sendDispatchWork(String id, String userId) {
        Map<String, Object> notice = new HashMap<>();
        notice.put("serviceId", id);
        notice.put("type", MqConstants.JobMateMationJobType.EQUIPMENT_REPAIR_DISPATCH.getJobType());
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(notice));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    @Override
    protected void validatorEntity(EquipmentRepairOrder entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            EquipmentRepairOrder dbOrder = selectById(entity.getId());
            if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())
                || ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
                // 待派工、待接单可以进行编辑
            } else {
                throw new CustomException("该数据状态已改变，请刷新页面！");
            }
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertEquipmentRepairResult(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        EquipmentRepairOrder dbOrder = selectById(id);
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_COMPLETED.getKey())) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsRepaired),
                Integer.valueOf(map.get("isRepaired").toString()));
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsReplaceSpare),
                Integer.valueOf(map.get("isReplaceSpare").toString()));
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultReason),
                Integer.valueOf(map.get("faultReason").toString()));
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getCancelReason),
                map.get("cancelReason").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairDesc),
                map.get("repairDesc").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishPhoto),
                map.get("repairFinishPhoto").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getSupplierId),
                map.get("supplierId").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishTime),
                map.get("repairFinishTime").toString());
            update(updateWrapper);
            if (ObjectUtil.isNotEmpty(map.get("sparePartUsageList"))) {
                List<EquipmentSparePartUsageDetail> sparePartUsageList = JSONUtil.toList(
                    map.get("sparePartUsageList").toString(), EquipmentSparePartUsageDetail.class);
                revertSparePartStock(dbOrder.getId());
                saveSparePartUsage(dbOrder.getId(), sparePartUsageList, userId);
            }
            refreshCache(id);
            outputObject.setBean(selectById(id));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void completeEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        EquipmentRepairOrder dbOrder = selectById(map.get("id").toString());
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_COMPLETED.getKey())) {
            updateStateById(dbOrder.getId(), EquipmentRepairOrderState.BE_EVALUATED.getKey());
            outputObject.setBean(selectById(dbOrder.getId()));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        EquipmentRepairOrder dbOrder = selectById(id);
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_EVALUATED.getKey())) {
            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEvaluateTypeId), map.get("evaluateTypeId").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEvaluateContent), map.get("evaluateContent").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.AUDIT.getKey());
            update(updateWrapper);
            refreshCache(id);
            outputObject.setBean(selectById(id));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Integer isFixed = Integer.valueOf(map.get("isFixed").toString());
        EquipmentRepairOrder dbOrder = selectById(id);
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.AUDIT.getKey())) {
            String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
            if (!StrUtil.equals(currentUserId, dbOrder.getUserId())) {
                throw new CustomException("只有报修人可以进行结果确认");
            }

            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsFixed), isFixed);
            if (WhetherEnum.DISABLE_USING.getKey().equals(isFixed)) {
                revertAndDeleteSparePart(dbOrder.getId());
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            } else {
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.COMPLATE.getKey());
                equipmentService.editEquipmentStateById(dbOrder.getEquipmentId(),
                    Integer.valueOf(map.get("equipmentStatus").toString()));
            }
            update(updateWrapper);
            refreshCache(id);
            outputObject.setBean(selectById(id));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void deletePreExecution(EquipmentRepairOrder entity) {
        if (ObjectUtil.equal(entity.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())
            || ObjectUtil.equal(entity.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
            // 待派工/待接单可以进行删除
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        equipmentMaintainOrderService.setMationForMap(beans, "maintainOrderId", "maintainOrderMation");
        iAuthUserService.setMationForMap(beans, "userId", "userMation");
        iAuthUserService.setMationForMap(beans, "serviceUserId", "serviceUserMation");
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertEquipmentRepairWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String serviceUserId = map.get("serviceUserId").toString();
        EquipmentRepairOrder repairOrder = selectById(id);
        if (ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())) {
            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, repairOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), serviceUserId);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            sendDispatchWork(id, repairOrder.getUserId());
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void receivingEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        EquipmentRepairOrder repairOrder = selectById(map.get("id").toString());
        if (ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
            // 接单进入待完工
            updateStateById(repairOrder.getId(), EquipmentRepairOrderState.BE_COMPLETED.getKey());
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void updateStateById(String id, Integer state) {
        UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        List<EquipmentRepairOrder> list = list();
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    private void saveSparePartUsage(String repairOrderId, List<EquipmentSparePartUsageDetail> sparePartUsageList, String userId) {
        equipmentSparePartUsageDetailService.calcDetailPrice(sparePartUsageList);
        equipmentSparePartUsageDetailService.saveLinkList(repairOrderId, sparePartUsageList);
        equipmentSparePartUsageDetailService.changeUserStock(userId, sparePartUsageList, DepotPutOutType.OUT.getKey());
    }

    private void revertSparePartStock(String repairOrderId) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        List<EquipmentSparePartUsageDetail> oldList = equipmentSparePartUsageDetailService.selectByParentId(repairOrderId);
        equipmentSparePartUsageDetailService.changeUserStock(userId, oldList, DepotPutOutType.PUT.getKey());
    }

    private void revertAndDeleteSparePart(String repairOrderId) {
        List<EquipmentSparePartUsageDetail> oldList = equipmentSparePartUsageDetailService.selectByParentId(repairOrderId);
        if (CollectionUtil.isEmpty(oldList)) {
            return;
        }
        equipmentSparePartUsageDetailService.revertUserStockByDetailOwner(oldList);
        equipmentSparePartUsageDetailService.deleteByParentId(repairOrderId);
        refreshCache(repairOrderId);
    }
}
