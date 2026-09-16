/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pickconfirm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutOtherState;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.entity.ErpOrderItemCode;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pickconfirm.classenum.ConfirmFromType;
import com.skyeye.pickconfirm.dao.ConfirmReturnDao;
import com.skyeye.pickconfirm.entity.ConfirmReturn;
import com.skyeye.pickconfirm.service.ConfirmPutService;
import com.skyeye.pickconfirm.service.ConfirmReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: ConfirmReturnServiceImpl
 * @Description: 物料退货单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/27 10:18
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "物料退货单", groupName = "物料确认", flowable = true, orderApproval = true)
public class ConfirmReturnServiceImpl extends SkyeyeErpOrderServiceImpl<ConfirmReturnDao, ConfirmReturn> implements ConfirmReturnService {

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private ConfirmPutService confirmPutService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置仓库出库单
        depotOutService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 车间
        farmService.setMationForMap(beans, "farmId", "farmMation");
        // 部门
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");
        return beans;
    }

    @Override
    public void validatorEntity(ConfirmReturn entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            ConfirmReturn confirmReturn = selectById(entity.getId());
            entity.setFromId(confirmReturn.getFromId());
            entity.setFromTypeId(confirmReturn.getFromTypeId());
            entity.setFarmId(confirmReturn.getFarmId());
            entity.setDepartmentId(confirmReturn.getDepartmentId());
            entity.setSalesman(confirmReturn.getSalesman());
        }
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
        checkMaterialNorms(entity, false);
        checkNormsCodeAndSave(entity);
    }

    @Override
    public void createPrepose(ConfirmReturn entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void writePostpose(ConfirmReturn entity, String userId) {
        // 保存单据子表关联的条形码编号信息
        super.saveErpOrderItemCode(entity);
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        // 删除关联的编码信息
        super.deleteErpOrderItemCodeById(id);
    }

    @Override
    public ConfirmReturn getDataFromDb(String id) {
        ConfirmReturn confirmReturn = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(confirmReturn);
        return confirmReturn;
    }

    @Override
    public ConfirmReturn selectById(String id) {
        ConfirmReturn confirmReturn = super.selectById(id);
        if (confirmReturn.getFromTypeId() == ConfirmFromType.DEPOT_OUT.getKey()) {
            // 仓库出库单
            depotOutService.setDataMation(confirmReturn, ConfirmReturn::getFromId);
        }
        // 车间
        farmService.setDataMation(confirmReturn, ConfirmReturn::getFarmId);
        // 部门
        iDepmentService.setDataMation(confirmReturn, ConfirmReturn::getDepartmentId);
        return confirmReturn;
    }

    private void checkMaterialNorms(ConfirmReturn entity, boolean setData) {
        // 当前物料退货单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达物料退货单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == ConfirmFromType.DEPOT_OUT.getKey()) {
            // 仓库出库单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(ConfirmReturn entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        DepotOut depotOut = depotOutService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(depotOut.getErpOrderItemList())) {
            throw new CustomException("该仓库出库单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(depotOut.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达物料接收单的商品信息
        Map<String, String> putExecuteNum = confirmPutService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 仓库出库单数量 - 当前单据数量 - 已经下达物料退货单的数量 - 已经下达物料接收单的数量
        super.setOrCheckOperNumber(depotOut.getErpOrderItemList(), setData, orderNormsNum, executeNum, putExecuteNum);

        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = depotOut.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                depotOutService.editOtherState(depotOut.getId(), DepotOutOtherState.COMPLATE_CONFIRM.getKey());
            } else {
                depotOutService.editOtherState(depotOut.getId(), DepotOutOtherState.PARTIAL_CONFIRM.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(ConfirmReturn entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
        // 校验并修改条形码信息
        checkNormsCodeAndSave(entity);
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     *
     * @param entity
     */
    protected List<String> checkNormsCodeAndSave(ConfirmReturn entity) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行入库的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 1. 和来源单据的条形码作对比
            // 获取来源单据中的条形码的信息
            DepotOut depotOut = depotOutService.selectById(entity.getFromId());
            List<String> inFromOrderNormsCodeList = depotOut.getErpOrderItemList().stream()
                .filter(bean -> CollectionUtil.isNotEmpty(bean.getNormsCodeList()))
                .flatMap(norms -> norms.getNormsCodeList().stream()).distinct().collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在来源单据中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inFromOrderNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在来源出库单中，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 2. 和数据库中条形码的状态做对比
            //  2.1 从数据库查询出库状态的条形码信息，
            //  2.2 只有部门信息为空的说明没有领料，才可以进行退货。部门信息不为空，说明该条形码已经在部门/车间的库存里
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.OUTBOUND.getKey());
            materialNormsCodeList = materialNormsCodeList.stream().filter(bean -> StrUtil.isEmpty(bean.getDepartmentId()))
                .collect(Collectors.toList());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在/已经存在其他仓库中，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
        }
        return allNormsCodeList;
    }

    @Override
    public void queryConfirmReturnTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ConfirmReturn confirmReturn = selectById(id);
        // 该物料退货单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(confirmReturn.getId());
        // 设置未下达商品数量-----物料退货单数量 - 已入库数量
        super.setOrCheckOperNumber(confirmReturn.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        confirmReturn.setErpOrderItemList(confirmReturn.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(confirmReturn);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertConfirmReturnToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取物料退货单状态
        ConfirmReturn confirmReturn = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(confirmReturn)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(confirmReturn.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.CONFIRM_RETURN.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }

    @Override
    public Map<String, List<String>> calcMaterialNormsCodeByFromId(String fromId) {
        QueryWrapper<ConfirmReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ConfirmReturn::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ConfirmReturn::getIdKey), getServiceClassName());
        // 只查询审批通过，部分出入库，已完成的单据
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
            ErpOrderStateEnum.COMPLETED.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(ConfirmReturn::getState), stateList);
        List<ConfirmReturn> entityList = list(queryWrapper);
        List<String> ids = entityList.stream().map(ConfirmReturn::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<ErpOrderItem> erpOrderItemList = skyeyeErpOrderItemService.queryErpOrderItemByPIds(ids);
        // 查询单据子表关联的条形码编号信息
        List<ErpOrderItemCode> erpOrderItemCodeList = erpOrderItemCodeService.selectByParentId(ids.toArray(new String[]{}));
        Map<String, List<ErpOrderItemCode>> listMap = erpOrderItemCodeList.stream().collect(Collectors.groupingBy(ErpOrderItemCode::getNormsId));
        erpOrderItemList.forEach(erpOrderItem -> {
            List<ErpOrderItemCode> erpOrderItemCodes = listMap.get(erpOrderItem.getNormsId());
            if (CollectionUtil.isNotEmpty(erpOrderItemCodes)) {
                List<String> normsCodeList = erpOrderItemCodes.stream().map(ErpOrderItemCode::getNormsCode).collect(Collectors.toList());
                erpOrderItem.setNormsCodeList(normsCodeList);
                erpOrderItem.setNormsCode(Joiner.on("\n").join(normsCodeList));
            }
        });
        Map<String, List<String>> collect = erpOrderItemList.stream()
            .collect(Collectors.groupingBy(ErpOrderItem::getNormsId, Collectors.mapping(ErpOrderItem::getNormsCodeList,
                Collectors.reducing(
                    new ArrayList<>(),
                    Function.identity(),
                    (l1, l2) -> {
                        if (CollectionUtil.isNotEmpty(l2)) {
                            l1.addAll(l2);
                        }
                        return l1;
                    }
                ))));
        return collect;
    }
}
