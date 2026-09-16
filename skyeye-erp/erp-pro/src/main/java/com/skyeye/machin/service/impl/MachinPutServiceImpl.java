/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.classenum.MachinPutFromType;
import com.skyeye.machin.classenum.MachinStateEnum;
import com.skyeye.machin.dao.MachinPutDao;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.entity.MachinPut;
import com.skyeye.machin.service.MachinChildService;
import com.skyeye.machin.service.MachinPutService;
import com.skyeye.machin.service.MachinService;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinPutServiceImpl
 * @Description: 加工入库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 22:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加工入库单", groupName = "加工单管理", flowable = true, orderApproval = true)
public class MachinPutServiceImpl extends SkyeyeErpOrderServiceImpl<MachinPutDao, MachinPut> implements MachinPutService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private MachinService machinService;

    @Autowired
    private MachinChildService machinChildService;

    @Override
    public QueryWrapper<MachinPut> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MachinPut> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFarmId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 部门
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");

        machinProcedureFarmService.setOrderMationByFromId(beans, "fromId", "fromMation");

        farmService.setMationForMap(beans, "farmId", "farmMation");
        return beans;
    }

    @Override
    public void validatorEntity(MachinPut entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
    }

    @Override
    public void createPrepose(MachinPut entity) {
        super.createPrepose(entity);
        if (StrUtil.isEmpty(entity.getFarmId())) {
            throw new CustomException("请选择加工车间");
        }
        entity.setFromTypeId(MachinPutFromType.FARM_TASK.getKey());
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void updatePrepose(MachinPut entity) {
        super.updatePrepose(entity);
        // 保证下面的参数不会因为编辑而改变
        MachinPut oldMachinPut = selectById(entity.getId());
        entity.setFarmId(oldMachinPut.getFarmId());
    }

    @Override
    public MachinPut selectById(String id) {
        MachinPut machinPut = super.selectById(id);
        iDepmentService.setDataMation(machinPut, MachinPut::getDepartmentId);
        return machinPut;
    }

    @Override
    public void queryMachinPutTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        MachinPut machinPut = selectById(id);
        // 该加工入库单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(machinPut.getId());
        // 设置未下达商品数量-----加工入库单数量 - 已入库数量
        super.setOrCheckOperNumber(machinPut.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        machinPut.setErpOrderItemList(machinPut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(machinPut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertMachinPutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取加工入库单状态
        MachinPut machinPut = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(machinPut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(machinPut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.MACHIN_PUT.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }

    @Override
    public List<MachinPut> queryMachinPutByMachinProcedureFarmId(String machinProcedureFarmId) {
        QueryWrapper<MachinPut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFromId), machinProcedureFarmId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFromTypeId), MachinPutFromType.FARM_TASK.getKey());
        return this.list(queryWrapper);
    }

    @Override
    public void approvalEndIsSuccess(MachinPut entity) {
        MachinPut oldEntity = selectById(entity.getId());
        // 减少在制库存（加工单审批通过时增加了在制库存，现在加工入库单审批通过需要减去）
        if (CollectionUtil.isNotEmpty(oldEntity.getErpOrderItemList())) {
            oldEntity.getErpOrderItemList().forEach(erpOrderItem -> {
                erpCommonService.editMaterialNormsDepotStock(MaterialNormsStockType.IN_TRANSIT_STOCK.getDefaultDepotId(), erpOrderItem.getMaterialId(),
                    erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.OUT.getKey(), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey());
            });
        }

        // 获取车间任务
        MachinProcedureFarm machinProcedureFarm = machinProcedureFarmService.selectById(oldEntity.getFromId());
        if (machinProcedureFarm == null || StrUtil.isEmpty(machinProcedureFarm.getMachinId())) {
            return;
        }

        // 检查并更新加工单状态
        updateMachinStateIfNeeded(machinProcedureFarm.getMachinId());
    }

    /**
     * 检查并更新加工单状态
     * 根据该加工单的所有子件的入库情况更新加工单状态
     *
     * @param machinId 加工单ID
     */
    private void updateMachinStateIfNeeded(String machinId) {
        // 1. 获取加工单下的所有子件信息
        List<MachinChild> machinChildList = machinChildService.selectByParentId(machinId);
        if (CollectionUtil.isEmpty(machinChildList)) {
            return;
        }

        // 2. 查询该加工单下所有车间任务
        QueryWrapper<MachinProcedureFarm> farmWrapper = new QueryWrapper<>();
        farmWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinId), machinId);
        List<MachinProcedureFarm> farmList = machinProcedureFarmService.list(farmWrapper);
        if (CollectionUtil.isEmpty(farmList)) {
            return;
        }

        // 3. 获取所有车间任务的ID
        List<String> farmIdList = farmList.stream()
            .map(MachinProcedureFarm::getId)
            .collect(Collectors.toList());

        // 4. 查询这些车间任务对应的所有已审批通过的加工入库单
        QueryWrapper<MachinPut> putWrapper = new QueryWrapper<>();
        putWrapper.in(MybatisPlusUtil.toColumns(MachinPut::getFromId), farmIdList);
        putWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFromTypeId), MachinPutFromType.FARM_TASK.getKey());
        putWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getState), FlowableStateEnum.PASS.getKey());
        List<MachinPut> machinPutList = this.list(putWrapper);

        // 5. 计算每个子件（normsId）的入库数量
        Map<String, String> putNumMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(machinPutList)) {
            List<String> putIdList = machinPutList.stream()
                .map(MachinPut::getId)
                .collect(Collectors.toList());
            // 查询所有入库单明细，按normsId汇总数量
            putNumMap = skyeyeErpOrderItemService.queryErpOrderItemByPIds(putIdList).stream()
                .collect(Collectors.groupingBy(
                    item -> item.getNormsId(),
                    Collectors.reducing(
                        CommonNumConstants.NUM_ZERO.toString(),
                        item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber(),
                        (sum, operNumber) -> CalculationUtil.add(
                            ErpConstants.NUM_AFTER_DOT,
                            StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum,
                            StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber
                        )
                    )
                ));
        }

        // 6. 如果没有已审批通过的加工入库单，不需要更新状态
        if (CollectionUtil.isEmpty(machinPutList)) {
            return;
        }

        // 7. 统计已完成和未完成的子件数量
        int totalChildCount = 0;
        int completedChildCount = 0;

        for (MachinChild machinChild : machinChildList) {
            totalChildCount++;
            String planNum = machinChild.getOperNumber();
            String putNum = putNumMap.getOrDefault(machinChild.getNormsId(), CommonNumConstants.NUM_ZERO.toString());

            // 比较计划数量和入库数量，如果入库数量 >= 计划数量，则认为已完成
            if (CalculationUtil.compareTo(putNum, planNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) >= 0) {
                completedChildCount++;
            }
        }

        // 8. 更新加工单状态
        if (totalChildCount > 0) {
            if (completedChildCount == totalChildCount) {
                // 所有子件都已入库完成，设置为已完成
                machinService.editMachinStateById(machinId, MachinStateEnum.COMPLETED.getKey());
            } else {
                // 只要有加工入库单（已审批通过的），不管子件是否全部完成，都设置为部分完成
                machinService.editMachinStateById(machinId, MachinStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }
}
