/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.entity.BomProcedureConsumables;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.classenum.AutoAssignFarmSelectStrategyEnum;
import com.skyeye.machin.classenum.AutoAssignQuantityTypeEnum;
import com.skyeye.machin.classenum.MachinFromType;
import com.skyeye.machin.classenum.MachinPickStateEnum;
import com.skyeye.machin.dao.MachinDao;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.service.MachinChildService;
import com.skyeye.machin.service.MachinService;
import com.skyeye.machinprocedure.classenum.MachinProcedureFarmState;
import com.skyeye.machinprocedure.classenum.MachinProcedureState;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.material.classenum.MaterialFromType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.classenum.PickFromType;
import com.skyeye.pick.entity.PatchMaterial;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.ReturnMaterial;
import com.skyeye.pick.service.PatchMaterialService;
import com.skyeye.pick.service.RequisitionMaterialService;
import com.skyeye.pick.service.ReturnMaterialService;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.entity.WayProcedureChild;
import com.skyeye.procedure.service.WayProcedureService;
import com.skyeye.procedure.service.WorkProcedureService;
import com.skyeye.production.classenum.ProductionChildType;
import com.skyeye.production.classenum.ProductionMachinOrderState;
import com.skyeye.production.entity.Production;
import com.skyeye.production.entity.ProductionChild;
import com.skyeye.production.service.ProductionService;
import com.skyeye.service.ErpCommonService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinServiceImpl
 * @Description: 加工单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "加工单管理", groupName = "加工单管理", flowable = true, orderApproval = true)
public class MachinServiceImpl extends SkyeyeBusinessServiceImpl<MachinDao, Machin> implements MachinService {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private MachinChildService machinChildService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private BomService bomService;

    @Autowired
    private WayProcedureService wayProcedureService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private WorkProcedureService workProcedureService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private RequisitionMaterialService requisitionMaterialService;

    @Autowired
    private PatchMaterialService patchMaterialService;

    @Autowired
    private ReturnMaterialService returnMaterialService;

    @Autowired
    protected ErpCommonService erpCommonService;

    /**
     * 部门范围与 {@link CommonPageInfo#getType()} 配合使用（不传 type 或传 AllDept：不按部门过滤，查全部）：
     * <ul>
     *     <li>AllDept — 所有部门（默认，不追加 department_id 条件）</li>
     *     <li>SpecifyDept — 指定部门，需传 {@link CommonPageInfo#getDepartmentId()}</li>
     *     <li>MyDept — 当前登录用户所属部门（登录缓存 departmentId）</li>
     * </ul>
     */
    @Override
    protected QueryWrapper<Machin> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Machin> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String scopeType = commonPageInfo.getType();
        if (StrUtil.isEmpty(scopeType) || StrUtil.equals(scopeType, "AllDept")) {
            return queryWrapper;
        }
        if (StrUtil.equals(scopeType, "SpecifyDept")) {
            if (StrUtil.isEmpty(commonPageInfo.getDepartmentId())) {
                throw new CustomException("指定部门查询时，部门id不能为空");
            }
            queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getDepartmentId), commonPageInfo.getDepartmentId());
            return queryWrapper;
        }
        if (StrUtil.equals(scopeType, "MyDept")) {
            Map<String, Object> logParams = InputObject.getLogParamsStatic();
            String departmentId = logParams.getOrDefault("departmentId", StrUtil.EMPTY).toString();
            if (StrUtil.isEmpty(departmentId)) {
                throw new CustomException("未获取到当前登录用户的部门信息");
            }
            queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getDepartmentId), departmentId);
            return queryWrapper;
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        // 生产计划单
        productionService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void queryMachinListForApsSchedule(InputObject inputObject, OutputObject outputObject) {
        // 1) 查询存在待接收/待执行车间任务的加工单ID
        List<String> states = Arrays.asList(MachinProcedureFarmState.WAIT_RECEIVE.getKey(), MachinProcedureFarmState.WAIT_EXECUTED.getKey());
        QueryWrapper<MachinProcedureFarm> farmQw = new QueryWrapper<>();
        farmQw.select(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinId));
        farmQw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getState), states);
        List<MachinProcedureFarm> farmList = machinProcedureFarmService.list(farmQw);
        List<String> machinIds = farmList.stream().map(MachinProcedureFarm::getMachinId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(machinIds)) {
            return;
        }
        // 2) 查询加工单（仅审核通过的，不分页，供下拉框展示）
        QueryWrapper<Machin> machinQw = new QueryWrapper<>();
        machinQw.in(CommonConstants.ID, machinIds);
        machinQw.eq(MybatisPlusUtil.toColumns(Machin::getState), FlowableStateEnum.PASS.getKey());
        List<Machin> machinList = list(machinQw);
        outputObject.setBeans(machinList);
        outputObject.settotal(machinList.size());
    }

    /**
     * 加工单自动安排车间任务（入口方法，解析请求参数后调用核心逻辑）
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void autoAssignFarmTasks(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String machinId = params.get("id").toString();
        String quantityType = params.get("quantityType").toString();
        List<String> priorityFarmIds = JSONUtil.toList(params.get("priorityFarmIds").toString(), null);
        String farmSelectStrategy = params.get("farmSelectStrategy").toString();
        int maxFarmsPerProcedure = Integer.parseInt(params.get("maxFarmsPerProcedure").toString());
        doAutoAssignFarmTasks(machinId, quantityType, priorityFarmIds, farmSelectStrategy, maxFarmsPerProcedure);
    }

    /**
     * 加工单自动安排车间任务（核心逻辑）
     * <p>
     * 按工序顺序遍历，为每个工序分配车间任务。支持：
     * - 数量类型：all-全部数量 / remaining-剩余未分配数量
     * - 优先车间：指定后仅在优先车间内分配，并按顺序/任务量/随机选取
     * - 数量分配：capacity-按产能比例 / equal-平均分配
     * </p>
     *
     * @param machinId             加工单ID
     * @param quantityType         数量类型：all / remaining
     * @param priorityFarmIds      优先车间ID列表（可为空）
     * @param farmSelectStrategy   车间选取策略：order-按顺序 / minLoad-任务量最少 / random-随机
     * @param maxFarmsPerProcedure 每个工序最多分配给多少个车间，默认1
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void doAutoAssignFarmTasks(String machinId, String quantityType, List<String> priorityFarmIds, String farmSelectStrategy, int maxFarmsPerProcedure) {
        // 1. 参数校验及枚举解析
        Machin machin = getDataFromDb(machinId);
        if (machin == null) {
            throw new CustomException("加工单不存在");
        }
        AutoAssignQuantityTypeEnum quantityTypeEnum = AutoAssignQuantityTypeEnum.parse(quantityType);
        AutoAssignFarmSelectStrategyEnum farmSelectStrategyEnum = AutoAssignFarmSelectStrategyEnum.parse(farmSelectStrategy);
        int maxFarms = Math.max(1, maxFarmsPerProcedure);
        boolean isRemaining = quantityTypeEnum == AutoAssignQuantityTypeEnum.REMAINING;

        // 2. 批量查询工序及已有车间任务（避免循环内 service 调用）
        Map<String, MachinProcedure> procedureMap = machinProcedureService.queryMachinProcedureMapByMachinId(machinId);
        if (procedureMap.isEmpty()) {
            throw new CustomException("该加工单暂无工序信息，无法自动安排车间任务");
        }
        Map<String, List<MachinProcedureFarm>> procedureFarmMap = machinProcedureFarmService.queryMachinProcedureFarmMapByMachinId(machinId);

        // 3. 构建子单据ID -> 加工数量的映射（用于车间任务的目标数量）
        Map<String, String> childIdToOperNum = machin.getMachinChildList().stream()
            .filter(c -> StrUtil.isNotEmpty(c.getId()) && StrUtil.isNotEmpty(c.getOperNumber()))
            .collect(Collectors.toMap(MachinChild::getId, MachinChild::getOperNumber, (a, b) -> a));

        // 4. 按工序类型（procedureId）批量查询可执行车间，构建 procedureId -> 车间列表 映射
        List<String> distinctProcedureIds = procedureMap.values().stream()
            .map(MachinProcedure::getProcedureId)
            .filter(StrUtil::isNotEmpty).distinct()
            .collect(Collectors.toList());
        Map<String, List<Farm>> procedureIdToFarms = workProcedureService.queryExecuteFarmByWorkProcedureIds(distinctProcedureIds);
        List<Farm> enabledFarmList = farmService.queryEnabledFarmList();

        // 4.1 若为任务量最少或加工数量最少策略，预查询各车间数据
        Map<String, Long> taskCountByFarmId = Collections.emptyMap();
        Map<String, String> quantitySumByFarmId = Collections.emptyMap();
        if (farmSelectStrategyEnum == AutoAssignFarmSelectStrategyEnum.MIN_LOAD || farmSelectStrategyEnum == AutoAssignFarmSelectStrategyEnum.MIN_QUANTITY) {
            List<String> allFarmIds = procedureIdToFarms.values().stream()
                .flatMap(List::stream)
                .map(Farm::getId)
                .filter(StrUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
            enabledFarmList.stream()
                .map(Farm::getId)
                .filter(StrUtil::isNotEmpty)
                .filter(id -> !allFarmIds.contains(id))
                .forEach(allFarmIds::add);
            if (farmSelectStrategyEnum == AutoAssignFarmSelectStrategyEnum.MIN_LOAD) {
                taskCountByFarmId = machinProcedureFarmService.countPendingTaskByFarmIds(allFarmIds);
            } else {
                quantitySumByFarmId = machinProcedureFarmService.sumPendingTargetNumByFarmIds(allFarmIds);
            }
        }

        // 5. 按工序顺序排序，遍历工序生成待创建的车间任务
        List<MachinProcedure> procedureList = procedureMap.values().stream()
            .sorted(Comparator.comparing(MachinProcedure::getOrderBy, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        List<MachinProcedureFarm> toCreate = new ArrayList<>();
        for (MachinProcedure procedure : procedureList) {
            List<MachinProcedureFarm> existingFarms = procedureFarmMap.getOrDefault(procedure.getId(), Collections.emptyList());
            // remaining：补充安排剩余数量；all：仅安排尚未有车间任务的工序
            if (isRemaining) {
                // 剩余数量 = 子单据 operNumber - 已有车间任务的 targetNum 之和
                String operNumStr = childIdToOperNum.getOrDefault(procedure.getChildId(), CommonNumConstants.NUM_ONE.toString());
                if (StrUtil.isEmpty(operNumStr)) {
                    operNumStr = CommonNumConstants.NUM_ONE.toString();
                }
                String assignedSumStr = existingFarms.stream()
                    .map(MachinProcedureFarm::getTargetNum)
                    .filter(StrUtil::isNotEmpty)
                    .reduce(CommonNumConstants.NUM_ZERO.toString(), (sum, targetNum) ->
                        CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, sum, targetNum));
                String remaining = CalculationUtil.subtract(operNumStr, assignedSumStr, ErpConstants.NUM_AFTER_DOT, RoundingMode.DOWN);
                if (CalculationUtil.compareTo(remaining, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) <= 0) {
                    continue;
                }
                // 优先使用工序关联设备所在车间，若无则使用启用车间列表
                List<Farm> farmList = procedureIdToFarms.getOrDefault(procedure.getProcedureId(), enabledFarmList);
                if (CollectionUtil.isEmpty(farmList)) {
                    continue;
                }
                List<Farm> targetFarms = resolveTargetFarms(farmList, priorityFarmIds, farmSelectStrategyEnum, taskCountByFarmId, quantitySumByFarmId, maxFarms);
                List<String> quantities = splitQuantity(remaining, targetFarms.size());
                for (int i = 0; i < targetFarms.size(); i++) {
                    MachinProcedureFarm farm = new MachinProcedureFarm();
                    farm.setMachinId(machinId);
                    farm.setMachinProcedureId(procedure.getId());
                    farm.setFarmId(targetFarms.get(i).getId());
                    farm.setTargetNum(quantities.get(i));
                    farm.setState(MachinProcedureFarmState.WAIT_RECEIVE.getKey());
                    toCreate.add(farm);
                }
            } else {
                // all：已有车间任务的工序跳过
                if (CollectionUtil.isNotEmpty(existingFarms)) {
                    continue;
                }
                List<Farm> farmList = procedureIdToFarms.getOrDefault(procedure.getProcedureId(), enabledFarmList);
                if (CollectionUtil.isEmpty(farmList)) {
                    continue;
                }
                String targetNum = childIdToOperNum.getOrDefault(procedure.getChildId(), CommonNumConstants.NUM_ONE.toString());
                if (StrUtil.isEmpty(targetNum)) {
                    targetNum = CommonNumConstants.NUM_ONE.toString();
                }
                List<Farm> targetFarms = resolveTargetFarms(farmList, priorityFarmIds, farmSelectStrategyEnum, taskCountByFarmId, quantitySumByFarmId, maxFarms);
                List<String> quantities = splitQuantity(targetNum, targetFarms.size());
                for (int i = 0; i < targetFarms.size(); i++) {
                    MachinProcedureFarm farm = new MachinProcedureFarm();
                    farm.setMachinId(machinId);
                    farm.setMachinProcedureId(procedure.getId());
                    farm.setFarmId(targetFarms.get(i).getId());
                    farm.setTargetNum(quantities.get(i));
                    farm.setState(MachinProcedureFarmState.WAIT_RECEIVE.getKey());
                    toCreate.add(farm);
                }
            }
        }

        // 6. 批量创建车间任务
        if (CollectionUtil.isNotEmpty(toCreate)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            machinProcedureFarmService.createEntity(toCreate, userId);
        }
    }

    /**
     * 解析目标车间列表
     * <p>
     * - maxFarms=1：取一个车间，按 farmSelectStrategy 选取
     * - maxFarms>1：取最多 maxFarms 个车间，平均分配；若有优先车间则仅在优先车间内筛选
     * </p>
     *
     * @param farmList            可选车间列表（已按优先车间排序）
     * @param priorityFarmIds     优先车间ID，非空时仅在此范围内分配
     * @param farmSelectStrategy  单车间时的选取策略
     * @param taskCountByFarmId   各车间待办任务数（minLoad 策略用）
     * @param quantitySumByFarmId 各车间待办加工数量之和（minQuantity 策略用）
     * @param maxFarms            每个工序最多分配车间数
     * @return 参与分配的目标车间列表，与 quantities 一一对应
     */
    private List<Farm> resolveTargetFarms(List<Farm> farmList, List<String> priorityFarmIds, AutoAssignFarmSelectStrategyEnum farmSelectStrategy,
                                          Map<String, Long> taskCountByFarmId, Map<String, String> quantitySumByFarmId, int maxFarms) {
        if (CollectionUtil.isEmpty(farmList)) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isNotEmpty(priorityFarmIds)) {
            farmList = reorderFarmListWithPriority(farmList, priorityFarmIds);
        }
        if (maxFarms <= 1) {
            Farm single = selectFarmFromList(farmList, farmSelectStrategy, taskCountByFarmId, quantitySumByFarmId);
            return single != null ? Collections.singletonList(single) : Collections.emptyList();
        }
        List<Farm> candidateFarms = CollectionUtil.isNotEmpty(priorityFarmIds)
            ? farmList.stream().filter(f -> priorityFarmIds.contains(f.getId())).collect(Collectors.toList())
            : new ArrayList<>(farmList);
        if (CollectionUtil.isEmpty(candidateFarms)) {
            Farm single = selectFarmFromList(farmList, farmSelectStrategy, taskCountByFarmId, quantitySumByFarmId);
            return single != null ? Collections.singletonList(single) : Collections.emptyList();
        }
        if (farmSelectStrategy == AutoAssignFarmSelectStrategyEnum.MIN_LOAD && !taskCountByFarmId.isEmpty()) {
            candidateFarms = candidateFarms.stream()
                .sorted(Comparator.comparingLong(f -> taskCountByFarmId.getOrDefault(f.getId(), 0L)))
                .collect(Collectors.toList());
        } else if (farmSelectStrategy == AutoAssignFarmSelectStrategyEnum.MIN_QUANTITY && !quantitySumByFarmId.isEmpty()) {
            candidateFarms = candidateFarms.stream()
                .sorted(Comparator.comparing(f -> quantitySumByFarmId.getOrDefault(f.getId(), CommonNumConstants.NUM_ZERO.toString()),
                    (a, b) -> CalculationUtil.compareTo(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP)))
                .collect(Collectors.toList());
        }
        int take = Math.min(maxFarms, candidateFarms.size());
        return candidateFarms.subList(0, take);
    }

    /**
     * 从车间列表中选取一个车间
     * <p>
     * - order：取第一个（已按优先车间排序）
     * - minLoad：取待办任务数最少的车间
     * - minQuantity：取待办加工数量最少的车间
     * - random：随机选取
     * </p>
     *
     * @param farmList            可选车间列表
     * @param farmSelectStrategy  order / minLoad / minQuantity / random
     * @param taskCountByFarmId   各车间待办任务数（minLoad 时用）
     * @param quantitySumByFarmId 各车间待办加工数量之和（minQuantity 时用）
     * @return 选中的车间，列表为空时返回 null
     */
    private Farm selectFarmFromList(List<Farm> farmList, AutoAssignFarmSelectStrategyEnum farmSelectStrategy,
                                    Map<String, Long> taskCountByFarmId, Map<String, String> quantitySumByFarmId) {
        if (CollectionUtil.isEmpty(farmList)) {
            return null;
        }
        if (farmSelectStrategy == AutoAssignFarmSelectStrategyEnum.RANDOM) {
            return farmList.get(new Random().nextInt(farmList.size()));
        }
        if (farmSelectStrategy == AutoAssignFarmSelectStrategyEnum.MIN_LOAD && !taskCountByFarmId.isEmpty()) {
            return farmList.stream()
                .min(Comparator.comparingLong(f -> taskCountByFarmId.getOrDefault(f.getId(), 0L)))
                .orElse(farmList.get(0));
        }
        if (farmSelectStrategy == AutoAssignFarmSelectStrategyEnum.MIN_QUANTITY && !quantitySumByFarmId.isEmpty()) {
            return farmList.stream()
                .min(Comparator.comparing(f -> quantitySumByFarmId.getOrDefault(f.getId(), CommonNumConstants.NUM_ZERO.toString()),
                    (a, b) -> CalculationUtil.compareTo(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP)))
                .orElse(farmList.get(0));
        }
        return farmList.get(0);
    }

    /**
     * 将数量平均分配到 n 个车间，余数分配给前几个车间
     * <p>
     * 例：100 分 3 份 → [34, 33, 33]
     * </p>
     *
     * @param totalStr 待分配总数量（字符串）
     * @param n        车间数量
     * @return 各车间分配数量列表
     */
    private List<String> splitQuantity(String totalStr, int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        if (n == 1) {
            return Collections.singletonList(totalStr);
        }
        java.math.BigDecimal total = new java.math.BigDecimal(totalStr);
        java.math.BigDecimal[] divRem = total.divideAndRemainder(java.math.BigDecimal.valueOf(n));
        java.math.BigDecimal base = divRem[0];      // 每份基数
        int remainder = divRem[1].intValue();       // 余数，前 remainder 个车间多分 1
        List<String> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            java.math.BigDecimal part = base.add(i < remainder ? java.math.BigDecimal.ONE : java.math.BigDecimal.ZERO);
            result.add(part.setScale(0, RoundingMode.DOWN).toPlainString());
        }
        return result;
    }

    /**
     * 将优先车间按顺序排在列表前面
     * <p>
     * 优先车间中存在于 farmList 的按 priorityFarmIds 顺序排在前面，其余车间接在后面。
     * </p>
     *
     * @param farmList        原始车间列表
     * @param priorityFarmIds 优先车间ID列表（顺序即优先级）
     * @return 重排后的车间列表
     */
    private List<Farm> reorderFarmListWithPriority(List<Farm> farmList, List<String> priorityFarmIds) {
        if (CollectionUtil.isEmpty(priorityFarmIds)) {
            return farmList;
        }
        List<Farm> result = new ArrayList<>(farmList.size());
        for (String farmId : priorityFarmIds) {
            farmList.stream().filter(f -> farmId.equals(f.getId())).findFirst().ifPresent(result::add);
        }
        farmList.stream().filter(f -> !priorityFarmIds.contains(f.getId())).forEach(result::add);
        return result;
    }

    @Override
    public void validatorEntity(Machin entity) {
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(Machin entity) {
        super.createPrepose(entity);
        entity.setPickState(MachinPickStateEnum.NOT_PICKED.getKey());
    }

    @Override
    public void writePostpose(Machin entity, String userId) {
        // 保存子单据信息
        machinChildService.saveList(entity.getId(), entity.getMachinChildList());
        super.writePostpose(entity, userId);
        refreshCache(entity.getId());
    }

    @Override
    public void updatePrepose(Machin entity) {
        super.updatePrepose(entity);
        Machin machin = selectById(entity.getId());
        entity.setFromId(machin.getFromId());
    }

    @Override
    public Machin getDataFromDb(String id) {
        Machin machin = super.getDataFromDb(id);
        // 查询子单据信息
        machin.setMachinChildList(machinChildService.selectByParentId(machin.getId()));
        return machin;
    }

    @Override
    public List<Machin> getDataFromDb(List<String> idList) {
        List<Machin> machinList = super.getDataFromDb(idList);
        List<String> machinIdList = machinList.stream().map(Machin::getId).collect(Collectors.toList());
        Map<String, List<MachinChild>> listMap = machinChildService.selectMapByParentId(machinIdList);
        machinList.forEach(machin -> {
            machin.setMachinChildList(listMap.get(machin.getId()));
        });
        return machinList;
    }

    @Override
    public Machin selectById(String id) {
        Machin machin = super.selectById(id);
        // 部门信息
        iDepmentService.setDataMation(machin, Machin::getDepartmentId);
        // 配件商品规格信息
        materialService.setDataMation(machin.getMachinChildList(), MachinChild::getMaterialId);
        materialNormsService.setDataMation(machin.getMachinChildList(), MachinChild::getNormsId);
        if (machin.getFromTypeId() == MachinFromType.PRODUCTION.getKey()) {
            // 生产计划单
            productionService.setDataMation(machin, Machin::getFromId);
        }
        // 设置工序/工序信息,车间任务
        Map<String, MachinProcedure> machinProcedureMap = machinProcedureService.queryMachinProcedureMapByMachinId(id);
        // 根据加工单ID查询车间任务 Map<加工单子单据工序id, List<车间任务>>
        Map<String, List<MachinProcedureFarm>> procedureFarmMap = machinProcedureFarmService.queryMachinProcedureFarmMapByMachinId(id);
        // 查询bom方案
        List<String> bomIds = machin.getMachinChildList().stream().filter(bean -> StrUtil.isNotEmpty(bean.getBomId()))
            .map(MachinChild::getBomId).distinct().collect(Collectors.toList());
        Map<String, Bom> bomMap = bomService.selectMapByIds(bomIds);

        // 获取规格对应的所有bom信息
        List<String> normsId = machin.getMachinChildList().stream()
            .map(MachinChild::getNormsId).distinct().collect(Collectors.toList());
        Map<String, List<Bom>> listMap = bomService.getBomListByNormsId(normsId.toArray(new String[]{}));
        machin.getMachinChildList().forEach(machinChild -> {
            machinChild.setBomList(listMap.get(machinChild.getNormsId()));
            // 判断产品的所有工序是否已完成
            Boolean[] checkComplateFlag = new Boolean[]{true};
            // 最后一个工序所完成的数量
            String[] lastProcedureNum = new String[]{CommonNumConstants.NUM_ZERO.toString()};

            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = bomMap.get(machinChild.getBomId());
                // 1. 设置加工单子单据bom清单的工序信息
                WayProcedure bomProcedure = resetMachinProcedure(bom.getWayProcedureId(), machinChild.getMaterialId(), machinChild.getNormsId(),
                    machinChild.getId(), StrUtil.EMPTY, machinProcedureMap, procedureFarmMap, checkComplateFlag, lastProcedureNum);
                bom.setWayProcedureMation(bomProcedure);
                // 2. 设置加工单子单据bom子件清单关联的工序信息
                bom.getBomChildList().forEach(bomChild -> {
                    WayProcedure bomWayProcedure = resetMachinProcedure(bomChild.getWayProcedureId(), bomChild.getMaterialId(), bomChild.getNormsId(),
                        machinChild.getId(), bomChild.getId(), machinProcedureMap, procedureFarmMap, checkComplateFlag, lastProcedureNum);
                    bomChild.setWayProcedureMation(bomWayProcedure);
                });

                // bom名称展示的时候同时显示版本
                bom.setName(String.format(Locale.ROOT, "%s(V%s)", bom.getName(), bom.getLargeVersion()));
                machinChild.setBomMation(bom);
            }
            machinChild.setCheckComplateFlag(checkComplateFlag[0]);
            machinChild.setLastProcedureNum(lastProcedureNum[0]);
        });

        return machin;
    }

    @Override
    public List<Machin> selectByIds(String... ids) {
        // 获取基础数据
        List<Machin> machinList = super.selectByIds(ids);
        if (CollectionUtil.isEmpty(machinList)) {
            return machinList;
        }
        // 部门信息
        iDepmentService.setDataMation(machinList, Machin::getDepartmentId);

        List<String> machinIdList = machinList.stream().map(Machin::getId).collect(Collectors.toList());

        // 批量查询子单据信息
        Map<String, List<MachinChild>> childListMap = machinChildService.selectMapByParentId(machinIdList);

        // 收集所有需要查询的ID
        List<String> materialIds = new ArrayList<>();
        List<String> normsIds = new ArrayList<>();
        List<String> bomIds = new ArrayList<>();
        List<String> fromIds = new ArrayList<>();

        for (Machin machin : machinList) {
            // 设置子单据列表
            machin.setMachinChildList(childListMap.get(machin.getId()));
            // 收集来源单据ID
            if (StrUtil.isNotEmpty(machin.getFromId())) {
                fromIds.add(machin.getFromId());
            }
            // 收集子单据相关的ID
            if (CollectionUtil.isNotEmpty(machin.getMachinChildList())) {
                for (MachinChild child : machin.getMachinChildList()) {
                    materialIds.add(child.getMaterialId());
                    normsIds.add(child.getNormsId());
                    bomIds.add(child.getBomId());
                }
            }
        }
        materialIds = materialIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        normsIds = normsIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        bomIds = bomIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        fromIds = fromIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());

        // 批量查询关联数据
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIds);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIds);
        Map<String, Bom> bomMap = bomService.selectMapByIds(bomIds);
        Map<String, Map<String, Object>> productionMap = productionService.selectValIsMapByIds(fromIds);

        // 获取规格对应的所有bom信息
        Map<String, List<Bom>> normsBomMap = bomService.getBomListByNormsId(normsIds.stream().distinct()
            .collect(Collectors.toList()).toArray(new String[]{}));

        Map<String, Map<String, MachinProcedure>> machinProcedureListMap = machinProcedureService.queryMachinProcedureMapByMachinIds(machinIdList);
        // 根据加工单ID查询车间任务 Map<加工单id, Map<加工单子单据工序id, List<车间任务>>>
        Map<String, Map<String, List<MachinProcedureFarm>>> machinProcedureFarmListMap = machinProcedureFarmService.queryMachinProcedureFarmMapByMachinIds(machinIdList);

        // 为每个加工单设置完整信息
        machinList.forEach(machin -> {
            // 设置来源单据信息
            if (StrUtil.isNotEmpty(machin.getFromId()) && machin.getFromTypeId() == MachinFromType.PRODUCTION.getKey()) {
                machin.setFromMation(productionMap.get(machin.getFromId()));
            }

            // 设置子单据信息
            if (CollectionUtil.isNotEmpty(machin.getMachinChildList())) {
                machin.getMachinChildList().forEach(machinChild -> {
                    // 设置配件商品信息
                    if (StrUtil.isNotEmpty(machinChild.getMaterialId())) {
                        machinChild.setMaterialMation(materialMap.get(machinChild.getMaterialId()));
                    }
                    // 设置规格信息
                    if (StrUtil.isNotEmpty(machinChild.getNormsId())) {
                        machinChild.setNormsMation(normsMap.get(machinChild.getNormsId()));
                    }
                    // 设置BOM信息
                    if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                        machinChild.setBomMation(bomMap.get(machinChild.getBomId()));
                    }
                    // 设置规格对应的BOM列表
                    if (StrUtil.isNotEmpty(machinChild.getNormsId())) {
                        machinChild.setBomList(normsBomMap.get(machinChild.getNormsId()));
                    }
                });

                // 为每个加工单单独查询工序信息（因为现有方法只支持单条查询）
                Map<String, MachinProcedure> machinProcedureMap = machinProcedureListMap.get(machin.getId());
                // 获取车间任务信息
                Map<String, List<MachinProcedureFarm>> procedureFarmMap = machinProcedureFarmListMap.get(machin.getId());

                if (machinProcedureMap != null && procedureFarmMap != null) {
                    machin.getMachinChildList().forEach(machinChild -> {
                        // 判断产品的所有工序是否已完成
                        Boolean[] checkComplateFlag = new Boolean[]{true};
                        // 最后一个工序所完成的数量
                        String[] lastProcedureNum = new String[]{CommonNumConstants.NUM_ZERO.toString()};

                        if (StrUtil.isNotEmpty(machinChild.getBomId()) && machinChild.getBomMation() != null) {
                            Bom bom = machinChild.getBomMation();
                            // 1. 设置加工单子单据bom清单的工序信息
                            WayProcedure bomProcedure = resetMachinProcedure(bom.getWayProcedureId(), machinChild.getMaterialId(), machinChild.getNormsId(),
                                machinChild.getId(), StrUtil.EMPTY, machinProcedureMap, procedureFarmMap, checkComplateFlag, lastProcedureNum);
                            bom.setWayProcedureMation(bomProcedure);
                            // 2. 设置加工单子单据bom子件清单关联的工序信息
                            bom.getBomChildList().forEach(bomChild -> {
                                WayProcedure bomWayProcedure = resetMachinProcedure(bomChild.getWayProcedureId(), bomChild.getMaterialId(), bomChild.getNormsId(),
                                    machinChild.getId(), bomChild.getId(), machinProcedureMap, procedureFarmMap, checkComplateFlag, lastProcedureNum);
                                bomChild.setWayProcedureMation(bomWayProcedure);
                            });
                        }

                        machinChild.setCheckComplateFlag(checkComplateFlag[0]);
                        machinChild.setLastProcedureNum(lastProcedureNum[0]);
                    });
                }
            }
        });

        return machinList;
    }

    /**
     * @param wayProcedureId     工艺id
     * @param materialId         商品id
     * @param normsId            规格id
     * @param childId            加工单子单据id
     * @param bomChildId         bom子件清单id
     * @param machinProcedureMap 加工单子单据产品规格其中的工序信息Map<加工单子单据产品规格其中的工序id, 加工单子单据产品规格其中的工序信息>
     * @param procedureFarmMap   车间任务Map<加工单子单据工序id, List<车间任务>>
     * @param checkComplateFlag  工序是否完成加工的状态，true:完成，false:未完成
     * @param lastProcedureNum   最后加工完成的数量
     * @return
     */
    private WayProcedure resetMachinProcedure(String wayProcedureId, String materialId, String normsId, String childId,
                                              String bomChildId, Map<String, MachinProcedure> machinProcedureMap,
                                              Map<String, List<MachinProcedureFarm>> procedureFarmMap, Boolean[] checkComplateFlag,
                                              String[] lastProcedureNum) {
        if (StrUtil.isNotEmpty(wayProcedureId)) {
            // 获取工艺信息
            WayProcedure wayProcedure = wayProcedureService.selectById(wayProcedureId);
            wayProcedure.getWorkProcedureList().forEach(wayProcedureChild -> {
                String key = String.format(Locale.ROOT, "%s-%s-%s-%s-%s-%s",
                    childId, bomChildId, materialId, normsId, wayProcedureId, wayProcedureChild.getProcedureId());
                // 加工单子单据工序信息
                MachinProcedure machinProcedure = machinProcedureMap.get(key);
                if (machinProcedure.getState() == MachinProcedureState.WAIT_STARTED.getKey()
                    || machinProcedure.getState() == MachinProcedureState.PARTIAL_COMPLETION.getKey()) {
                    // 工序未开始/部分完成
                    checkComplateFlag[0] = false;
                }
                // 设置工序关联的车间任务
                List<MachinProcedureFarm> machinProcedureFarmList = procedureFarmMap.get(machinProcedure.getId());
                // 计算已经完成的数量
                lastProcedureNum[0] = calcMachinProcedureFarmPassNum(machinProcedureFarmList);
                machinProcedure.setMachinProcedureFarmList(machinProcedureFarmList);
                wayProcedureChild.setMachinProcedureMation(machinProcedure);
            });
            return wayProcedure;
        }
        return null;
    }

    private String calcMachinProcedureFarmPassNum(List<MachinProcedureFarm> machinProcedureFarmList) {
        if (CollectionUtil.isEmpty(machinProcedureFarmList)) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        // 获取已经审批通过的工序验收单
        List<MachinProcedureAccept> machinProcedureAcceptList = machinProcedureFarmList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getMachinProcedureAcceptList()))
            .flatMap(bean -> bean.getMachinProcedureAcceptList().stream())
            .filter(bean -> StrUtil.equals(bean.getState(), FlowableStateEnum.PASS.getKey())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(machinProcedureAcceptList)) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        return machinProcedureAcceptList.stream()
            .map(MachinProcedureAccept::getQualifiedNum)
            .reduce(CommonNumConstants.NUM_ZERO.toString(), (sum, qualifiedNum) -> CalculationUtil.add(
                ErpConstants.NUM_AFTER_DOT,
                StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum,
                StrUtil.isEmpty(qualifiedNum) ? CommonNumConstants.NUM_ZERO.toString() : qualifiedNum
            ));
    }

    @Override
    public void deletePreExecution(String id) {
        Machin machin = selectById(id);
        if (!FlowableStateEnum.DRAFT.getKey().equals(machin.getState())
            && !FlowableStateEnum.REJECT.getKey().equals(machin.getState())
            && !FlowableStateEnum.REVOKE.getKey().equals(machin.getState())) {
            throw new CustomException("只有草稿、驳回、撤销状态的可删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子单据信息
        machinChildService.deleteByParentId(id);
    }

    @Override
    public void approvalEndIsSuccess(Machin entity) {
        entity = selectById(entity.getId());
        // 修改来源单据的状态信息
        checkMaterialNorms(entity, true);
        // 增加在制库存
        entity.getMachinChildList().forEach(erpOrderItem -> {
            erpCommonService.editMaterialNormsDepotStock(MaterialNormsStockType.IN_TRANSIT_STOCK.getDefaultDepotId(), erpOrderItem.getMaterialId(),
                erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.PUT.getKey(), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey());
        });
    }

    @Override
    public void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<Machin> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<Machin> machinList = list(queryWrapper);
        Map<String, Machin> machinMap = machinList.stream()
            .collect(Collectors.toMap(Machin::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                Machin entity = machinMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    private void checkMaterialNorms(Machin entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前加工单的商品数量
        Map<String, String> orderNormsNum = entity.getMachinChildList().stream()
            .collect(Collectors.toMap(MachinChild::getNormsId, MachinChild::getOperNumber));
        // 获取同一个来源单据下已经审批通过的加工单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == MachinFromType.PRODUCTION.getKey()) {
            // 生产计划单
            Production production = productionService.selectById(entity.getFromId());
            // 获取需要【加工】的商品
            List<ProductionChild> productionChildList = production.getProductionChildList().stream()
                .filter(bean -> bean.getProductionType() == ProductionChildType.SELF_CONTROL.getKey()).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(productionChildList)) {
                throw new CustomException("该生产计划单下未包含需要加工的商品.");
            }
            List<String> fromNormsIds = productionChildList.stream()
                .map(ProductionChild::getNormsId).collect(Collectors.toList());
            // 求差集(生产计划单不包含的商品)
            List<String> diffList = inSqlNormsId.stream()
                .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
                List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
                throw new CustomException(String.format(Locale.ROOT, "该生产计划单下未包含如下商品规格：【%s】.",
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
            }
            productionChildList.forEach(productionChild -> {
                // 生产计划单数量 - 当前加工单数量 - 已经审批通过的加工单数量
                String surplusNum = ErpOrderUtil.checkOperNumber(productionChild.getOperNumber(), productionChild.getNormsId(),
                    orderNormsNum, executeNum);
                if (setData) {
                    productionChild.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                productionChildList = productionChildList.stream()
                    .filter(productionChild -> CalculationUtil.compareTo(productionChild.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                    .collect(Collectors.toList());
                // 该生产计划单的商品已经全部下达了加工单
                if (CollectionUtil.isEmpty(productionChildList)) {
                    productionService.editMachinOrderState(production.getId(), ProductionMachinOrderState.COMPLATE_ISSUE.getKey());
                } else {
                    productionService.editMachinOrderState(production.getId(), ProductionMachinOrderState.PARTIAL_ISSUE.getKey());
                }
            }
        }
    }

    @Override
    public void editPickStateById(String id, String pickState) {
        UpdateWrapper<Machin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Machin::getPickState), pickState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editMachinStateById(String id, String machinState) {
        UpdateWrapper<Machin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Machin::getState), machinState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<Machin> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getState), FlowableStateEnum.PASS.getKey());
        List<Machin> machinList = list(queryWrapper);
        List<String> ids = machinList.stream().map(Machin::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<MachinChild> machinChildList = machinChildService.selectByParentId(ids);
        Map<String, String> collect = machinChildList.stream()
            .collect(Collectors.groupingBy(
                MachinChild::getNormsId,
                Collectors.reducing(
                    CommonNumConstants.NUM_ZERO.toString(),
                    MachinChild::getOperNumber,
                    (sum, operNumber) -> CalculationUtil.add(
                        ErpConstants.NUM_AFTER_DOT,
                        StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum,
                        StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber
                    )
                )
            ));
        return collect;
    }

    @Override
    public void setMachinMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<Machin> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<Machin> machinList = list(queryWrapper);
        Map<String, Machin> machinMap = machinList.stream()
            .collect(Collectors.toMap(Machin::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                Machin entity = machinMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void queryMachinForGanttById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Machin machin = selectById(id);
        // 生成唯一的主键id
        machin.getMachinChildList().forEach(machinChild -> {
            String machinChildId = ToolUtil.getSurFaceId();
            machinChild.setNewId(machinChildId);
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = machinChild.getBomMation();
                if (ObjectUtil.isNotEmpty(bom.getWayProcedureMation())) {
                    bom.getWayProcedureMation().getWorkProcedureList().forEach(wayProcedureChild -> {
                        wayProcedureChild.setNewId(ToolUtil.getSurFaceId());
                    });
                }
                Map<String, String> newMaterialIdMap = new HashMap<>();
                bom.getBomChildList().forEach(bomChild -> {
                    newMaterialIdMap.put(bomChild.getMaterialId(), ToolUtil.getSurFaceId());
                });
                bom.getBomChildList().forEach(bomChild -> {
                    String bomChildId = ToolUtil.getSurFaceId();
                    bomChild.setNewId(bomChildId);
                    if (StrUtil.equals(bomChild.getParentId(), CommonNumConstants.NUM_ZERO.toString())) {
                        bomChild.setNewParentId(machinChildId);
                    } else {
                        bomChild.setNewParentId(newMaterialIdMap.get(bomChild.getMaterialId()));
                    }
                    if (ObjectUtil.isNotEmpty(bomChild.getWayProcedureMation())) {
                        bomChild.getWayProcedureMation().getWorkProcedureList().forEach(wayProcedureChild -> {
                            wayProcedureChild.setNewId(ToolUtil.getSurFaceId());
                        });
                    }
                });
            }
        });

        Map<String, Object> mathinTime = new HashMap<>();
        List<String> dateArray = new ArrayList<>();
        machin.getMachinChildList().forEach(machinChild -> {
            dateArray.add(machinChild.getPlanStartTime());
            dateArray.add(machinChild.getPlanEndTime());
        });
        showResult(dateArray.toArray(new String[]{}), mathinTime);
        // 构造数据
        List<Map<String, Object>> node = new ArrayList<>();
        List<Map<String, Object>> link = new ArrayList<>();
        machin.getMachinChildList().forEach(machinChild -> {
            // 产品信息
            Map<String, Object> materialNode = getNode(machinChild.getNewId(), machinChild.getMaterialMation().getName(), CommonNumConstants.NUM_ZERO.toString(),
                machinChild.getPlanStartTime(), machinChild.getPlanEndTime(), true, machinChild);
            node.add(materialNode);
            // bom清单
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = machinChild.getBomMation();
                // 加工单子单据的产品信息不重新计算开始和结束时间，直接用计划时间
                resetMachinProcedure(bom.getWayProcedureMation(), node, link, machinChild.getNewId(), mathinTime, null);
                // 计算BOM子件的实际需要数量（考虑树结构和加工数量）
                List<BomChild> calculatedBomChildList = calculateBomChildNeedNum(bom, machinChild.getOperNumber());
                calculatedBomChildList.forEach(bomChild -> {
                    Map<String, Object> childMaterialNode = getNode(bomChild.getNewId(), bomChild.getMaterialMation().getName(), bomChild.getNewParentId(),
                        machinChild.getPlanStartTime(), machinChild.getPlanEndTime(), true, bomChild);
                    node.add(childMaterialNode);
                    link.add(getLink(bomChild.getNewParentId(), bomChild.getNewId()));
                    link.get(link.size() - 1).put("color", "#009688");
                    resetMachinProcedure(bomChild.getWayProcedureMation(), node, link, bomChild.getNewId(), mathinTime, childMaterialNode);
                });
            }
        });

        node.forEach(item -> {
            if (!MapUtil.checkKeyIsNull(item, "start_date")) {
                dateArray.add(item.get("start_date").toString());
            }
            if (!MapUtil.checkKeyIsNull(item, "end_date")) {
                dateArray.add(item.get("end_date").toString());
            }
        });
        showResult(dateArray.toArray(new String[]{}), mathinTime);

        Map<String, Object> retult = new HashMap<>();
        retult.put("node", node);
        retult.put("link", link);
        retult.put("mathinTime", mathinTime);
        outputObject.setBean(retult);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private static void showResult(String[] dateArray, Map<String, Object> mathinTime) {
        Map<String, Integer> dateMap = new TreeMap<>();
        int i, arrayLen;
        arrayLen = dateArray.length;
        for (i = 0; i < arrayLen; i++) {
            String dateKey = dateArray[i];
            if (dateMap.containsKey(dateKey)) {
                int value = dateMap.get(dateKey) + 1;
                dateMap.put(dateKey, value);
            } else {
                dateMap.put(dateKey, 1);
            }
        }
        Set<String> keySet = dateMap.keySet();
        String[] sorttedArray = new String[keySet.size()];
        Iterator<String> iter = keySet.iterator();
        int index = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            sorttedArray[index++] = key;
        }
        int sorttedArrayLen = sorttedArray.length;
        mathinTime.put("start_date", sorttedArray[0]);
        mathinTime.put("end_date", sorttedArray[sorttedArrayLen - 1]);
    }

    private void resetMachinProcedure(WayProcedure wayProcedureMation, List<Map<String, Object>> node,
                                      List<Map<String, Object>> link, String parentId, Map<String, Object> mathinTime,
                                      Map<String, Object> materialNode) {
        if (ObjectUtil.isEmpty(wayProcedureMation)) {
            return;
        }
        String prveId = parentId;
        // 获取工艺信息
        for (WayProcedureChild wayProcedureChild : wayProcedureMation.getWorkProcedureList()) {
            // 加工单子单据工序信息
            MachinProcedure machinProcedure = wayProcedureChild.getMachinProcedureMation();
            if (ObjectUtil.isEmpty(machinProcedure)) {
                node.add(getNode(wayProcedureChild.getNewId(), wayProcedureChild.getProcedureMation().getName(), parentId,
                    mathinTime.get("start_date").toString(), mathinTime.get("end_date").toString(), false, machinProcedure));
                node.get(node.size() - 1).put("notAddWorkProcedure", true);
            } else {
                node.add(getNode(wayProcedureChild.getNewId(), wayProcedureChild.getProcedureMation().getName(), parentId,
                    machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime(), false, machinProcedure));
                List<String> dateArray = new ArrayList<>();
                if (StrUtil.isNotEmpty(machinProcedure.getPlanStartTime())) {
                    dateArray.add(machinProcedure.getPlanStartTime());
                }
                if (StrUtil.isNotEmpty(machinProcedure.getPlanEndTime())) {
                    dateArray.add(machinProcedure.getPlanEndTime());
                }
                if (CollectionUtil.isNotEmpty(materialNode)) {
                    dateArray.add(materialNode.getOrDefault("start_date", StrUtil.EMPTY).toString());
                    dateArray.add(materialNode.getOrDefault("end_date", StrUtil.EMPTY).toString());
                    showResult(dateArray.toArray(new String[]{}), materialNode);
                }
            }
            link.add(getLink(prveId, wayProcedureChild.getNewId()));
            link.get(link.size() - 1).put("color", "#FFB800");
            prveId = wayProcedureChild.getNewId();
        }
    }

    private Map<String, Object> getNode(String id, String name, String parentId, String startTime, String endTime, Boolean type, Object object) {
        Map<String, Object> retult = new HashMap<>();
        retult.put("id", id);
        retult.put("text", name);
        retult.put("parent", parentId);
        if (type) {
            retult.put("types", "project");
        }
        retult.put("start_date", startTime);
        retult.put("end_date", endTime);
        retult.put("open", true);
        retult.put("data", object);
        return retult;
    }

    private Map<String, Object> getLink(String id, String parentId) {
        Map<String, Object> retult = new HashMap<>();
        retult.put("id", ToolUtil.getSurFaceId());
        retult.put("source", id);
        retult.put("target", parentId);
        retult.put("type", CommonNumConstants.NUM_ZERO);
        return retult;
    }

    @Override
    public void queryMachinTransRequestById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Machin machin = selectById(id);
        // 获取需要的原材料
        List<BomChild> needRawMaterial = getNeedRawMaterial(machin);
        // 获取已经领料的数量
        Map<String, String> requestNum = requisitionMaterialService.calcMaterialNormsNumByFromId(id);
        // 获取已经补料的数量
        Map<String, String> patchNum = patchMaterialService.calcMaterialNormsNumByFromId(id);
        // 设置未申领的原材料信息
        needRawMaterial.forEach(machinChild -> {
            // 设置未下达领料单/补料单的商品数量-----原材料需求数量 - 已领料的数量 - 已补料的数量
            String requestNumValue = requestNum.containsKey(machinChild.getNormsId())
                ? requestNum.get(machinChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String patchNumValue = patchNum.containsKey(machinChild.getNormsId())
                ? patchNum.get(machinChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String tempNum = CalculationUtil.subtract(machinChild.getNeedNum(), requestNumValue, ErpConstants.NUM_AFTER_DOT);
            String surplusNum = CalculationUtil.subtract(tempNum, patchNumValue, ErpConstants.NUM_AFTER_DOT);
            if (CalculationUtil.compareTo(surplusNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                // 超出需求数量，设置为0，方便补料时进行补料
                surplusNum = CommonNumConstants.NUM_ZERO.toString();
            }
            // 设置未下达领料单/补料单的数量
            machinChild.setNeedNum(surplusNum);
        });
        // 不需要过滤掉数量为0的商品信息，方便补料时进行补料
        machin.setNeedRawMaterialList(needRawMaterial);

        outputObject.setBean(machin);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 计算BOM子件的实际需要数量（考虑树结构和加工数量）
     *
     * @param bom        BOM方案
     * @param operNumber 预加工数量
     * @return 计算后的BOM子件列表
     */
    public static List<BomChild> calculateBomChildNeedNum(Bom bom, String operNumber) {
        if (bom == null || CollectionUtil.isEmpty(bom.getBomChildList()) || StrUtil.isEmpty(operNumber)
            || CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) <= 0) {
            return bom != null ? bom.getBomChildList() : new ArrayList<>();
        }

        // 深拷贝BOM子件列表，避免修改原始数据
        List<BomChild> bomChildList = bom.getBomChildList().stream()
            .map(bomChild -> {
                BomChild newBomChild = new BomChild();
                BeanUtil.copyProperties(bomChild, newBomChild);
                return newBomChild;
            })
            .collect(Collectors.toList());

        // 构建父子关系Map，key为materialId，value为BomChild
        Map<String, BomChild> bomChildMap = bomChildList.stream()
            .collect(Collectors.toMap(BomChild::getMaterialId, child -> child, (k1, k2) -> k1));

        // 计算单元所需数量 = 加工数量 / BOM制造数量
        String unitRatio = CalculationUtil.divide(String.valueOf(operNumber), String.valueOf(bom.getMakeNum()), CommonNumConstants.NUM_TWO);

        // 找到所有根节点（parentId为"0"）
        List<BomChild> rootNodes = bomChildList.stream()
            .filter(child -> StrUtil.equals(child.getParentId(), CommonNumConstants.NUM_ZERO.toString()))
            .collect(Collectors.toList());

        // 递归计算每个节点的实际需要数量
        // 所有层级的子节点都使用相同的unitRatio（预加工数量 / BOM制造数量）
        for (BomChild rootNode : rootNodes) {
            calculateChildNeedNum(rootNode, bomChildMap, unitRatio);
        }

        return bomChildList;
    }

    /**
     * 递归计算子节点的实际需要数量
     * <p>
     * 计算公式：实际需要数量 = (预加工数量 / BOM制造数量) * BOM子件的needNum
     * 不管多少级的子节点，都使用相同的单元比例（预加工数量 / BOM制造数量）
     *
     * @param bomChild    当前BOM子件
     * @param bomChildMap BOM子件Map（key为materialId）
     * @param unitRatio   单元比例（预加工数量 / BOM制造数量），所有层级共用
     */
    private static void calculateChildNeedNum(BomChild bomChild, Map<String, BomChild> bomChildMap,
                                              String unitRatio) {
        if (bomChild == null || bomChild.getNeedNum() == null) {
            return;
        }

        // 计算当前节点的实际需要数量 = 单元比例 * 当前节点的needNum
        // 例如：预加工20个，BOM制造数量10个，needNum=100
        // 实际需要数量 = (20/10) * 100 = 2 * 100 = 200
        String actualNeedNumStr = CalculationUtil.multiply(unitRatio, bomChild.getNeedNum(), ErpConstants.NUM_AFTER_DOT);
        bomChild.setNeedNum(actualNeedNumStr);

        // 查找当前节点的所有子节点（parentId等于当前节点的materialId）
        List<BomChild> childNodes = bomChildMap.values().stream()
            .filter(child -> StrUtil.equals(child.getParentId(), bomChild.getMaterialId()))
            .collect(Collectors.toList());

        // 递归处理子节点，使用相同的unitRatio
        if (CollectionUtil.isNotEmpty(childNodes)) {
            for (BomChild childNode : childNodes) {
                calculateChildNeedNum(childNode, bomChildMap, unitRatio);
            }
        }
    }

    /**
     * 获取需要的原材料（耗材）
     * 新的逻辑：
     * 1. 如果BOM的子件清单没有绑定耗材，那么子件本身就是耗材
     * 2. 如果子件绑定了耗材，那么需要去计算耗材
     * 3. 同时考虑BOM级别的耗材
     * 4. 自产商品：如果没有绑定耗材，则跳过；如果绑定了耗材，计算耗材
     * 5. 外购商品：子件本身就是耗材（不需要判断是否绑定耗材）
     *
     * @param machin 加工单
     * @return
     */
    private List<BomChild> getNeedRawMaterial(Machin machin) {
        List<BomChild> needRawMaterial = new ArrayList<>();

        // 在最外层循环前，收集所有需要查询的商品ID
        List<String> allMaterialIds = new ArrayList<>();
        for (MachinChild machinChild : machin.getMachinChildList()) {
            Bom bom = machinChild.getBomMation();
            if (bom == null) {
                continue;
            }
            // 收集BOM子件的商品ID
            if (CollectionUtil.isEmpty(bom.getBomChildList())) {
                continue;
            }
            bom.getBomChildList().forEach(bomChild -> {
                if (StrUtil.isNotEmpty(bomChild.getMaterialId())) {
                    allMaterialIds.add(bomChild.getMaterialId());
                }
            });
        }

        // 批量查询所有商品信息
        Map<String, Material> materialMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(allMaterialIds)) {
            List<String> distinctMaterialIds = allMaterialIds.stream().distinct().collect(Collectors.toList());
            materialMap = materialService.selectMapByIds(distinctMaterialIds);
        }

        for (MachinChild machinChild : machin.getMachinChildList()) {
            Bom bom = machinChild.getBomMation();
            if (bom == null) {
                continue;
            }

            // 计算单元比例：订单数量 / BOM制造数量
            String unitRatio = CalculationUtil.divide(machinChild.getOperNumber(), String.valueOf(bom.getMakeNum()), CommonNumConstants.NUM_TWO);

            // 处理BOM子件清单
            if (CollectionUtil.isNotEmpty(bom.getBomChildList())) {
                for (BomChild bomChild : bom.getBomChildList()) {
                    // 获取子件的商品信息
                    Material material = materialMap.get(bomChild.getMaterialId());
                    if (ObjectUtil.isEmpty(material) || material.getFromType() == null) {
                        continue;
                    }

                    // 计算子件的实际需要数量
                    String bomChildNeedNum = CalculationUtil.multiply(unitRatio, bomChild.getNeedNum(), ErpConstants.NUM_AFTER_DOT);

                    // 判断商品类型
                    boolean isSelfProduced = material.getFromType().equals(MaterialFromType.SELF_PRODUCED.getKey());
                    boolean isOutsourcing = material.getFromType().equals(MaterialFromType.OUTSOURCING.getKey());

                    if (isOutsourcing) {
                        // 外购商品：子件本身就是耗材
                        BomChild consumableChild = new BomChild();
                        BeanUtil.copyProperties(bomChild, consumableChild);
                        consumableChild.setNeedNum(bomChildNeedNum);
                        needRawMaterial.add(consumableChild);
                    } else if (isSelfProduced) {
                        // 自产商品：判断是否绑定了耗材，如果没有耗材，则结束
                        List<BomProcedureConsumables> childConsumablesList = bomChild.getProcedureConsumablesList();
                        if (CollectionUtil.isEmpty(childConsumablesList)) {
                            continue;
                        }
                        // 子件绑定了耗材，需要计算耗材数量
                        for (BomProcedureConsumables consumable : childConsumablesList) {
                            // 耗材的实际需要数量 = 子件的实际需要数量 * 耗材的needNum
                            String consumableNeedNum = CalculationUtil.multiply(bomChildNeedNum, consumable.getNeedNum(), ErpConstants.NUM_AFTER_DOT);

                            // 将耗材转换为BomChild格式
                            BomChild consumableChild = new BomChild();
                            consumableChild.setMaterialId(consumable.getMaterialId());
                            consumableChild.setMaterialMation(consumable.getMaterialMation());
                            consumableChild.setNormsId(consumable.getNormsId());
                            consumableChild.setNormsMation(consumable.getNormsMation());
                            consumableChild.setNeedNum(consumableNeedNum);
                            consumableChild.setRemark(consumable.getRemark());
                            needRawMaterial.add(consumableChild);
                        }
                    }
                }
            }

            // 处理BOM级别的耗材（Bom.procedureConsumablesList）
            if (CollectionUtil.isNotEmpty(bom.getProcedureConsumablesList())) {
                for (BomProcedureConsumables bomConsumable : bom.getProcedureConsumablesList()) {
                    // BOM级别耗材的实际需要数量 = 单元比例 * 耗材的needNum
                    String bomConsumableNeedNum = CalculationUtil.multiply(unitRatio, bomConsumable.getNeedNum(), ErpConstants.NUM_AFTER_DOT);

                    // 将耗材转换为BomChild格式
                    BomChild consumableChild = new BomChild();
                    consumableChild.setMaterialId(bomConsumable.getMaterialId());
                    consumableChild.setMaterialMation(bomConsumable.getMaterialMation());
                    consumableChild.setNormsId(bomConsumable.getNormsId());
                    consumableChild.setNormsMation(bomConsumable.getNormsMation());
                    consumableChild.setNeedNum(bomConsumableNeedNum);
                    consumableChild.setRemark(bomConsumable.getRemark());
                    needRawMaterial.add(consumableChild);
                }
            }
        }

        // 根据规格id去重并合并所需数量
        Map<String, BomChild> needRawMaterialMap = new HashMap<>();
        needRawMaterial.forEach(bomChild -> {
            String normsId = bomChild.getNormsId();
            if (StrUtil.isEmpty(normsId)) {
                return; // 跳过没有规格ID的项
            }

            if (needRawMaterialMap.containsKey(normsId)) {
                BomChild existBomChild = needRawMaterialMap.get(normsId);
                String sum = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, existBomChild.getNeedNum(), bomChild.getNeedNum());
                existBomChild.setNeedNum(sum);
            } else {
                // 设置临时ID，实际上没什么用处，主要是前端需要
                bomChild.setId(ToolUtil.getSurFaceId());
                needRawMaterialMap.put(normsId, bomChild);
            }
        });

        return new ArrayList<>(needRawMaterialMap.values());
    }

    @Override
    public void insertMachinToPickRequest(InputObject inputObject, OutputObject outputObject) {
        RequisitionMaterial requisitionMaterial = inputObject.getParams(RequisitionMaterial.class);
        // 获取加工单状态
        Machin order = selectById(requisitionMaterial.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行下达领料单/补料单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            requisitionMaterial.setFromId(requisitionMaterial.getId());
            requisitionMaterial.setFromTypeId(PickFromType.MACHIN.getKey());
            requisitionMaterial.setId(StrUtil.EMPTY);
            requisitionMaterialService.createEntity(requisitionMaterial, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达领料单.");
        }
    }

    @Override
    public void insertMachinToPickPatch(InputObject inputObject, OutputObject outputObject) {
        PatchMaterial patchMaterial = inputObject.getParams(PatchMaterial.class);
        // 获取加工单状态
        Machin order = selectById(patchMaterial.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行下达领料单/补料单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            patchMaterial.setFromId(patchMaterial.getId());
            patchMaterial.setFromTypeId(PickFromType.MACHIN.getKey());
            patchMaterial.setId(StrUtil.EMPTY);
            patchMaterialService.createEntity(patchMaterial, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达补料单.");
        }
    }

    @Override
    public void queryMachinTransReturnById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Machin machin = selectById(id);
        // 获取已经领料的数量
        Map<String, String> requestNum = requisitionMaterialService.calcMaterialNormsNumByFromId(id);
        // 获取已经补料的数量
        Map<String, String> patchNum = patchMaterialService.calcMaterialNormsNumByFromId(id);
        // 获取已经退料的数量
        Map<String, String> returnNum = returnMaterialService.calcMaterialNormsNumByFromId(id);
        machin.getMachinChildList().forEach(machinChild -> {
            // 设置未下达退料单的商品数量-----已领料的数量 + 已补料的数量 - 订单数量 - 已退料的数量
            String requestNumValue = requestNum.containsKey(machinChild.getNormsId())
                ? requestNum.get(machinChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String patchNumValue = patchNum.containsKey(machinChild.getNormsId())
                ? patchNum.get(machinChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String returnNumValue = returnNum.containsKey(machinChild.getNormsId())
                ? returnNum.get(machinChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String lastProcedureNumValue = StrUtil.isEmpty(machinChild.getLastProcedureNum())
                ? CommonNumConstants.NUM_ZERO.toString()
                : machinChild.getLastProcedureNum();
            String tempNum = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, requestNumValue, patchNumValue);
            String tempNum2 = CalculationUtil.subtract(tempNum, lastProcedureNumValue, ErpConstants.NUM_AFTER_DOT);
            String surplusNum = CalculationUtil.subtract(tempNum2, returnNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达退料单的数量
            machinChild.setOperNumber(surplusNum);
        });
        outputObject.setBean(machin);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertMachinToPickReturn(InputObject inputObject, OutputObject outputObject) {
        ReturnMaterial returnMaterial = inputObject.getParams(ReturnMaterial.class);
        // 获取加工单状态
        Machin order = selectById(returnMaterial.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行下达退料单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            returnMaterial.setFromId(returnMaterial.getId());
            returnMaterial.setFromTypeId(PickFromType.MACHIN.getKey());
            returnMaterial.setId(StrUtil.EMPTY);
            returnMaterialService.createEntity(returnMaterial, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达退料单.");
        }
    }

    @Override
    public boolean checkIsLastProcedure(Machin machin, String childId, String bomChildId, String wayProcedureId, String materialId, String normsId, String procedureId) {
        if (ObjectUtil.isEmpty(machin) || StrUtil.isEmpty(childId)
            || StrUtil.isEmpty(materialId) || StrUtil.isEmpty(normsId) || StrUtil.isEmpty(procedureId)) {
            return false;
        }
        // 获取子单据信息
        MachinChild machinChild = machin.getMachinChildList().stream().filter(m -> m.getId().equals(childId)).findFirst().orElse(null);
        if (ObjectUtil.isEmpty(machinChild)) {
            return false;
        }

        // 判断是否绑定bom清单
        if (StrUtil.isNotEmpty(machinChild.getBomId())) {
            Bom bom = machinChild.getBomMation();

            // 整体是先走的bom的工艺路线，所以先判断工艺路线，再判断bom子件清单
            // 判断是否绑定工艺
            if (ObjectUtil.isNotEmpty(bom.getWayProcedureMation())) {
                // 判断是否为最后一道工序
                if (StrUtil.isNotEmpty(bomChildId)) {
                    // 如果bom子件清单的id不为空 && 加工单子单据有绑定工艺，那么就一定不是最后一道工序
                    return false;
                }
                int lastIndex = bom.getWayProcedureMation().getWorkProcedureList().size() - 1;
                WayProcedureChild wayProcedureChild = bom.getWayProcedureMation().getWorkProcedureList().get(lastIndex);
                if (StrUtil.equals(machinChild.getMaterialId(), materialId) && StrUtil.equals(machinChild.getNormsId(), normsId)
                    && StrUtil.equals(bom.getWayProcedureId(), wayProcedureId) && StrUtil.equals(wayProcedureChild.getProcedureId(), procedureId)) {
                    return true;
                } else {
                    return false;
                }
            }

            // 获取bom子件清单中绑定了工艺的id
            List<BomChild> bomChildList = bom.getBomChildList().stream().filter(bc -> StrUtil.isNotEmpty(bc.getWayProcedureId())).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bomChildList)) {
                // 如果没有绑定工艺，那么就一定是最后一道工序
                return true;
            }
            int lastIndex = bomChildList.size() - 1;
            BomChild bomChild = bomChildList.get(lastIndex);
            if (StrUtil.equals(machinChild.getMaterialId(), materialId) && StrUtil.equals(machinChild.getNormsId(), normsId)
                && StrUtil.equals(bom.getWayProcedureId(), wayProcedureId) && StrUtil.equals(bomChild.getId(), bomChildId)) {
                // 判断是否为最后一道工序
                int lastProcedureIndex = bom.getWayProcedureMation().getWorkProcedureList().size() - 1;
                WayProcedureChild wayProcedureChild = bom.getWayProcedureMation().getWorkProcedureList().get(lastProcedureIndex);
                if (StrUtil.equals(wayProcedureChild.getProcedureId(), procedureId)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return false;
    }
}
