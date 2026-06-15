/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentarchive.entity.EquipmentArchive;
import com.skyeye.equipmentarchive.service.EquipmentArchiveService;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanItem;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionAbnormalFlag;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionRunStatus;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrderServiceImpl
 * @Description: 设备巡检单服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检单", groupName = "设备巡检", flowable = true, allowDynamicAttrKey = false)
public class EquipmentInspectionOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionOrderDao, EquipmentInspectionOrder>
    implements EquipmentInspectionOrderService {

    @Autowired
    private EquipmentInspectionOrderItemService equipmentInspectionOrderItemService;

    @Lazy
    @Autowired
    private EquipmentArchiveService equipmentArchiveService;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Override
    protected QueryWrapper<EquipmentInspectionOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getStartTime())) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), commonPageInfo.getStartTime());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getEndTime())) {
            queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), commonPageInfo.getEndTime());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentArchiveService.setMationForMap(beans, "equipmentId", "equipmentMation");
        equipmentArchiveService.appendPatrolRecordForMationMap(beans, "equipmentId", "equipmentMation");
        iAuthUserService.setMationForMap(beans, "inspectorUserId", "inspectorUserMation");
        beans.forEach(this::appendEnumMationForMap);
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isNotBlank(pageInfo.getStartTime()) || StrUtil.isNotBlank(pageInfo.getEndTime())) {
            appendStatRecordDisplayFields(beans);
        }
        return beans;
    }

    @Override
    public EquipmentInspectionOrder getDataFromDb(String id) {
        EquipmentInspectionOrder entity = super.getDataFromDb(id);
        entity.setEquipmentInspectionOrderItemList(equipmentInspectionOrderItemService.selectByPId(entity.getId()));
        return entity;
    }

    @Override
    public EquipmentInspectionOrder selectById(String id) {
        EquipmentInspectionOrder order = super.selectById(id);
        EquipmentArchive archive = equipmentArchiveService.selectById(order.getEquipmentId());
        order.setEquipmentMation(BeanUtil.beanToMap(archive, false, true));
        appendPlanItemMation(order, archive);
        appendOrderItemResultMation(order);
        appendEnumMation(order);
        iAuthUserService.setDataMation(order, EquipmentInspectionOrder::getInspectorUserId);
        return order;
    }

    @Override
    public void validatorEntity(EquipmentInspectionOrder entity) {
        normalizeCreateIdentity(entity);
        if (StrUtil.isBlank(entity.getId())) {
            assignOddNumber(entity);
            if (entity.getSeqInDay() == null) {
                String today = LocalDate.now().toString();
                QueryWrapper<EquipmentInspectionOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), entity.getEquipmentId());
                queryWrapper.apply("DATE_FORMAT(inspection_time, '%Y-%m-%d') = {0}", today);
                entity.setSeqInDay((int) count(queryWrapper) + 1);
            }
        }
        super.validatorEntity(entity);
        EquipmentArchive archive = equipmentArchiveService.selectById(entity.getEquipmentId());
        alignOrderItemsWithPlan(entity.getEquipmentInspectionOrderItemList(), archive.getPatrolRecord());
        if (entity.getOverallResult() == null) {
            boolean abnormal = entity.getEquipmentInspectionOrderItemList().stream()
                .anyMatch(row -> EquipmentInspectionResultType.ABNORMAL.getKey().equals(row.getItemResult())
                    || EquipmentInspectionAbnormalFlag.YES.getKey().equals(row.getAbnormalFlag()));
            entity.setOverallResult(abnormal
                ? EquipmentInspectionResultType.ABNORMAL.getKey()
                : EquipmentInspectionResultType.NORMAL.getKey());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writePostpose(EquipmentInspectionOrder entity, String userId) {
        equipmentInspectionOrderItemService.saveLinkList(entity.getId(), entity.getEquipmentInspectionOrderItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        equipmentInspectionOrderItemService.deleteByPId(id);
    }

    private void appendStatRecordDisplayFields(List<Map<String, Object>> beans) {
        for (Map<String, Object> bean : beans) {
            String photoUrls = MapUtil.getStr(bean, "headerPhotoUrls");
            bean.put("photo", photoUrls);
            bean.put("photoUrls", photoUrls);
            String location = StrUtil.blankToDefault(MapUtil.getStr(bean, "locationText"),
                MapUtil.getStr(bean, "headerLocationText"));
            bean.put("location", location);
            if (StrUtil.isBlank(MapUtil.getStr(bean, "farmName"))) {
                Map<String, Object> equipmentMation = BeanUtil.toBean(bean.get("equipmentMation"), Map.class);
                if (equipmentMation != null) {
                    bean.put("farmName", equipmentMation.get("useFarm"));
                }
            }
        }
    }

    private void appendPlanItemMation(EquipmentInspectionOrder order, EquipmentArchive archive) {
        if (CollectionUtil.isEmpty(order.getEquipmentInspectionOrderItemList())) {
            return;
        }
        EquipmentInspectionPlan plan = loadPlanFromPatrolRecord(archive.getPatrolRecord());
        if (StrUtil.isBlank(plan.getId()) || CollectionUtil.isEmpty(plan.getEquipmentInspectionPlanItemList())) {
            return;
        }
        List<EquipmentInspectionPlanItem> planItems = plan.getEquipmentInspectionPlanItemList();
        for (EquipmentInspectionOrderItem orderItem : order.getEquipmentInspectionOrderItemList()) {
            Integer lineNo = orderItem.getLineNo();
            if (lineNo != null && lineNo > 0 && lineNo <= planItems.size()) {
                orderItem.setPlanItemMation(planItems.get(lineNo - 1));
            }
        }
    }

    private void appendOrderItemResultMation(EquipmentInspectionOrder order) {
        if (CollectionUtil.isEmpty(order.getEquipmentInspectionOrderItemList())) {
            return;
        }
        for (EquipmentInspectionOrderItem orderItem : order.getEquipmentInspectionOrderItemList()) {
            orderItem.setItemResultMation(EquipmentInspectionResultType.getMation(orderItem.getItemResult()));
            orderItem.setAbnormalFlagMation(EquipmentInspectionAbnormalFlag.getMation(orderItem.getAbnormalFlag()));
        }
    }

    private void appendEnumMation(EquipmentInspectionOrder order) {
        order.setOverallResultMation(EquipmentInspectionResultType.getMation(order.getOverallResult()));
        order.setEquipmentRunStatusMation(EquipmentInspectionRunStatus.getMation(order.getEquipmentRunStatus()));
    }

    private void appendEnumMationForMap(Map<String, Object> bean) {
        bean.put("overallResultMation", EquipmentInspectionResultType.getMation(MapUtil.getInt(bean, "overallResult")));
        bean.put("equipmentRunStatusMation", EquipmentInspectionRunStatus.getMation(MapUtil.getInt(bean, "equipmentRunStatus")));
    }

    private void alignOrderItemsWithPlan(List<EquipmentInspectionOrderItem> orderItems, Map<String, Object> patrolRecord) {
        EquipmentInspectionPlan plan = loadPlanFromPatrolRecord(patrolRecord);
        if (StrUtil.isBlank(plan.getId()) || CollectionUtil.isEmpty(plan.getEquipmentInspectionPlanItemList())) {
            throw new CustomException("设备关联的巡检方案不存在或未配置检查项.");
        }
        List<EquipmentInspectionPlanItem> planItems = plan.getEquipmentInspectionPlanItemList();
        if (orderItems.size() != planItems.size()) {
            throw new CustomException("巡检明细条数与方案检查项不一致.");
        }
        for (int i = 0; i < orderItems.size(); i++) {
            orderItems.get(i).setLineNo(i + 1);
        }
    }

    private EquipmentInspectionPlan loadPlanFromPatrolRecord(Map<String, Object> patrolRecord) {
        String planId = MapUtil.isEmpty(patrolRecord) ? StrUtil.EMPTY : MapUtil.getStr(patrolRecord, "planId", StrUtil.EMPTY);
        return equipmentInspectionPlanService.getDataFromDb(StrUtil.blankToDefault(planId, StrUtil.EMPTY));
    }

    private void normalizeCreateIdentity(EquipmentInspectionOrder entity) {
        if (StrUtil.isBlank(entity.getId())) {
            return;
        }
        EquipmentInspectionOrder existing = super.getDataFromDb(entity.getId());
        if (StrUtil.isBlank(existing.getId())) {
            entity.setId(null);
        }
    }

    private void assignOddNumber(EquipmentInspectionOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        if (StrUtil.isBlank(oddNumber)) {
            oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        }
        entity.setOddNumber(oddNumber);
    }

}
