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
import cn.hutool.json.JSONUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.repair.classenum.EquipmentFaultCategory;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairFaultReason;
import com.skyeye.repair.classenum.EquipmentRepairFromType;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.classenum.EquipmentRepairTeam;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairFailRecord;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;
import com.skyeye.repair.service.EquipmentRepairFailRecordService;
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
    private EquipmentRepairFailRecordService equipmentRepairFailRecordService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentMaintainOrderService equipmentMaintainOrderService;

    @Autowired
    private EquipmentCheckOrderService equipmentCheckOrderService;

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

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

        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFromId), commonPageInfo.getFromId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEquipmentId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public EquipmentRepairOrder getDataFromDb(String id) {
        EquipmentRepairOrder order = super.getDataFromDb(id);
        order.setSparePartUsageList(equipmentSparePartUsageDetailService.selectByParentId(id));
        order.setFailRecordList(equipmentRepairFailRecordService.selectByParentId(id));
        return order;
    }

    @Override
    public EquipmentRepairOrder selectById(String id) {
        EquipmentRepairOrder order = super.selectById(id);
        if (order == null) {
            return null;
        }
        equipmentService.setDataMation(order, EquipmentRepairOrder::getEquipmentId);
        // 设置来源单据信息
        if (ObjectUtil.isNotEmpty(order.getFromTypeId())) {
            if (order.getFromTypeId().equals(EquipmentRepairFromType.MAINTAIN_ORDER.getKey())) {
                equipmentMaintainOrderService.setDataMation(order, EquipmentRepairOrder::getFromId);
            } else if (order.getFromTypeId().equals(EquipmentRepairFromType.INSPECTION_TASK.getKey())) {
                equipmentInspectionOrderService.setDataMation(order, EquipmentRepairOrder::getFromId);
            } else if (order.getFromTypeId().equals(EquipmentRepairFromType.CHECK_ORDER.getKey())) {
                equipmentCheckOrderService.setDataMation(order, EquipmentRepairOrder::getFromId);
            }
        }
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getUserId);
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getServiceUserId);
        supplierService.setDataMation(order, EquipmentRepairOrder::getSupplierId);
        iSysDictDataService.setDataMation(order, EquipmentRepairOrder::getUrgencyId);
        iSysDictDataService.setDataMation(order, EquipmentRepairOrder::getEvaluateTypeId);
        order.setStateMation(EquipmentRepairOrderState.getMation(order.getState()));
        order.setFromTypeMation(EquipmentRepairFromType.getMation(order.getFromTypeId()));
        order.setFaultTypeMation(EquipmentFaultCategory.getMation(order.getFaultType()));
        order.setRepairTeamMation(EquipmentRepairTeam.getMation(order.getRepairTeam()));
        order.setAuditOpinionMation(EquipmentRepairAuditOpinion.getMation(order.getAuditOpinion()));
        order.setFaultReasonMation(EquipmentRepairFaultReason.getMation(order.getFaultReason()));
        if (CollectionUtil.isNotEmpty(order.getFailRecordList())) {
            iAuthUserService.setDataMation(order.getFailRecordList(), EquipmentRepairFailRecord::getServiceUserId);
        }
        if (CollectionUtil.isEmpty(order.getSparePartUsageList())) {
            return order;
        }
        materialService.setDataMation(order.getSparePartUsageList(), EquipmentSparePartUsageDetail::getMaterialId);
        materialNormsService.setDataMation(order.getSparePartUsageList(), EquipmentSparePartUsageDetail::getNormsId);
        List<String> normsIds = order.getSparePartUsageList().stream()
            .map(EquipmentSparePartUsageDetail::getNormsId)
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> serviceUserStockMap = iServiceUserStockService.queryUserStock(normsIds);
        order.getSparePartUsageList().forEach(detail ->
            detail.setServiceUserStock(serviceUserStockMap.get(detail.getNormsId())));
        return order;
    }

    @Override
    public void createPrepose(EquipmentRepairOrder entity) {
        Equipment equipment = equipmentService.selectById(entity.getEquipmentId());
        if (ObjectUtil.isNotEmpty(equipment) && EquipmentState.SCRAPPED.getKey().equals(equipment.getEquipmentState())) {
            throw new CustomException("设备已报废，无法新增维修单");
        }
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
            Integer isReplaceSpare = Integer.valueOf(map.get("isReplaceSpare").toString());
            List<EquipmentSparePartUsageDetail> sparePartUsageList = CollectionUtil.newArrayList();
            if (ObjectUtil.isNotEmpty(map.get("sparePartUsageList"))) {
                sparePartUsageList = JSONUtil.toList(
                    map.get("sparePartUsageList").toString(), EquipmentSparePartUsageDetail.class);
            }
            if (WhetherEnum.ENABLE_USING.getKey().equals(isReplaceSpare) && CollectionUtil.isEmpty(sparePartUsageList)) {
                throw new CustomException("是否更换配件为是时，备件使用明细不能为空");
            }

            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsRepaired),
                Integer.valueOf(map.get("isRepaired").toString()));
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsReplaceSpare), isReplaceSpare);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultReason),
                Integer.valueOf(map.get("faultReason").toString()));
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getCancelReason),
                map.get("cancelReason").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairDesc),
                map.get("repairDesc").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishPhoto),
                map.get("repairFinishPhoto").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishTime),
                DateUtil.getTimeAndToString());
            update(updateWrapper);
            if (CollectionUtil.isNotEmpty(sparePartUsageList)) {
                // 编辑维修结果只落明细，不增减库存；待确认→确认时再扣
                equipmentSparePartUsageDetailService.saveLinkList(dbOrder.getId(), sparePartUsageList);
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
                equipmentRepairFailRecordService.saveFailRecord(dbOrder);
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.BE_DISPATCHED.getKey());
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), StrUtil.EMPTY);
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsFixed), null);
            } else {
                // 按备件明细 createId（保存时的登录人）扣库存
                equipmentSparePartUsageDetailService.deductStockByParentId(dbOrder.getId());
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.COMPLATE.getKey());
                Integer equipmentState = Integer.valueOf(map.get("equipmentState").toString());
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEquipmentState), equipmentState);
                // 同步更新设备主表状态
                equipmentService.editEquipmentStateById(dbOrder.getEquipmentId(), equipmentState);
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
    public void deletePostpose(String id) {
        equipmentRepairFailRecordService.deleteByParentId(id);
        equipmentSparePartUsageDetailService.deleteByParentId(id);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        // 按来源类型分别填充 fromMation，避免互相覆盖
        List<Map<String, Object>> maintainList = filterByFromType(beans, EquipmentRepairFromType.MAINTAIN_ORDER.getKey());
        equipmentMaintainOrderService.setMationForMap(maintainList, "fromId", "fromMation");
        List<Map<String, Object>> inspectionList = filterByFromType(beans, EquipmentRepairFromType.INSPECTION_TASK.getKey());
        equipmentInspectionOrderService.setMationForMap(inspectionList, "fromId", "fromMation");
        List<Map<String, Object>> checkList = filterByFromType(beans, EquipmentRepairFromType.CHECK_ORDER.getKey());
        equipmentCheckOrderService.setMationForMap(checkList, "fromId", "fromMation");
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
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getSupplierId), map.get("supplierId").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getAuditOpinion),
                Integer.valueOf(map.get("auditOpinion").toString()));
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

    private List<Map<String, Object>> filterByFromType(List<Map<String, Object>> beans, Integer fromTypeId) {
        return beans.stream()
            .filter(bean -> bean.get("fromTypeId") != null
                && String.valueOf(fromTypeId).equals(String.valueOf(bean.get("fromTypeId"))))
            .collect(Collectors.toList());
    }

}
