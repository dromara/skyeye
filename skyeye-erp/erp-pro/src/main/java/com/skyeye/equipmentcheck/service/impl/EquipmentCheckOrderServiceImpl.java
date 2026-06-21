package com.skyeye.equipmentcheck.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentcheck.classenum.EquipmentCheckItemResult;
import com.skyeye.equipmentcheck.classenum.EquipmentCheckResult;
import com.skyeye.equipmentcheck.dao.EquipmentCheckOrderDao;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrder;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrderItem;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderItemService;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderService;
import com.skyeye.exception.CustomException;
import com.skyeye.repair.classenum.EquipmentRepairUrgency;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        iAuthUserService.setDataMation(order, EquipmentCheckOrder::getCheckerId);
        return order;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        iAuthUserService.setMationForMap(beans, "checkerId", "checkerMation");
        return beans;
    }

    // 点检审批通过：异常固定映射为带病运行并生成维修草案；正常固定映射为正常运行。
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approvalEndIsSuccess(EquipmentCheckOrder entity) {
        EquipmentCheckOrder order = selectById(entity.getId());
        if (EquipmentCheckResult.ABNORMAL.getKey().equals(order.getCheckResult())) {
            equipmentService.editEquipmentStateById(order.getEquipmentId(), EquipmentState.DEGRADED.getKey());
            createRepairDraftFromCheck(order);
        } else if (EquipmentCheckResult.NORMAL.getKey().equals(order.getCheckResult())) {
            equipmentService.editEquipmentStateById(order.getEquipmentId(), EquipmentState.NORMAL.getKey());
        }
    }

    private void createRepairDraftFromCheck(EquipmentCheckOrder checkOrder) {
        EquipmentRepairOrder repairOrder = new EquipmentRepairOrder();
        repairOrder.setEquipmentId(checkOrder.getEquipmentId());
        repairOrder.setUserId(checkOrder.getCheckerId());
        repairOrder.setReportTime(checkOrder.getCheckTime());
        repairOrder.setFaultBrief(buildFaultDesc(checkOrder));
        repairOrder.setFaultPhoto(checkOrder.getImages());
        repairOrder.setUrgencyLevel(EquipmentRepairUrgency.NORMAL.getKey());
        String operatorId = StrUtil.isNotEmpty(checkOrder.getCheckerId()) ? checkOrder.getCheckerId() : checkOrder.getCreateId();
        equipmentRepairOrderService.createEntity(repairOrder, operatorId);
    }

    private String buildFaultDesc(EquipmentCheckOrder order) {
        StringBuilder sb = new StringBuilder();
        sb.append("点检单[").append(order.getOddNumber()).append("]发现异常");
        if (CollectionUtil.isNotEmpty(order.getItemList())) {
            sb.append("：");
            order.getItemList().stream()
                .filter(this::isAbnormalItem)
                .forEach(item -> appendAbnormalItemDesc(sb, item));
        }
        return sb.toString();
    }

    private boolean isAbnormalItem(EquipmentCheckOrderItem item) {
        return EquipmentCheckItemResult.ABNORMAL.getKey().equals(item.getItemResult());
    }

    private void appendAbnormalItemDesc(StringBuilder sb, EquipmentCheckOrderItem item) {
        sb.append("\n- ").append(item.getCheckItem());
        if (StrUtil.isNotEmpty(item.getCheckValue())) {
            sb.append("，检查值：").append(item.getCheckValue());
        }
        if (StrUtil.isNotEmpty(item.getMinValue()) || StrUtil.isNotEmpty(item.getMaxValue())) {
            sb.append("，标准：").append(item.getMinValue()).append("~").append(item.getMaxValue());
        }
    }

    //统计今日点检设备分布、今日点检设备次数、今日未点检设备分布
    @Override
    public void queryStatistics(InputObject inputObject, OutputObject outputObject) {
        String today = LocalDate.now().toString();
        QueryWrapper<EquipmentCheckOrder> todayWrapper = new QueryWrapper<>();
        todayWrapper.likeRight(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckTime), today);
        List<EquipmentCheckOrder> todayList = list(todayWrapper);

        List<Map<String, Object>> checkedDistribution = todayList.stream()
            .collect(Collectors.groupingBy(EquipmentCheckOrder::getEquipmentName, Collectors.counting()))
            .entrySet().stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("equipmentName", e.getKey());
                map.put("count", e.getValue());
                return map;
            }).collect(Collectors.toList());

        List<Map<String, Object>> checkTimes = todayList.stream()
            .collect(Collectors.groupingBy(EquipmentCheckOrder::getEquipmentCode, Collectors.counting()))
            .entrySet().stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("equipmentCode", e.getKey());
                map.put("count", e.getValue());
                return map;
            }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("todayCheckedDistribution", checkedDistribution);
        result.put("todayEquipmentCheckTimes", checkTimes);
        result.put("todayUncheckedDistribution", buildTodayUncheckedDistribution(todayList));
        result.put("detailRows", todayList);
        outputObject.setBean(result);
        outputObject.settotal(todayList.size());
    }

     //今日未点检：基于历史点检单中出现过的设备，排除今日已点检。
    private List<Map<String, Object>> buildTodayUncheckedDistribution(List<EquipmentCheckOrder> todayList) {
        Set<String> checkedTodayIds = todayList.stream()
            .map(EquipmentCheckOrder::getEquipmentId)
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toSet());

        Map<String, EquipmentCheckOrder> latestByEquipment = new LinkedHashMap<>();
        QueryWrapper<EquipmentCheckOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckTime));
        for (EquipmentCheckOrder order : list(wrapper)) {
            if (StrUtil.isEmpty(order.getEquipmentId())) {
                continue;
            }
            latestByEquipment.putIfAbsent(order.getEquipmentId(), order);
        }

        return latestByEquipment.entrySet().stream()
            .filter(entry -> !checkedTodayIds.contains(entry.getKey()))
            .map(entry -> {
                EquipmentCheckOrder order = entry.getValue();
                Map<String, Object> map = new HashMap<>();
                map.put("equipmentId", order.getEquipmentId());
                map.put("equipmentName", order.getEquipmentName());
                map.put("equipmentCode", order.getEquipmentCode());
                map.put("count", CommonNumConstants.NUM_ONE);
                return map;
            })
            .collect(Collectors.toList());
    }
}

