/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pickconfirm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutOtherState;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.entity.ErpOrderItemCode;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.classenum.PickNormsCodeUseState;
import com.skyeye.pick.service.DepartmentStockService;
import com.skyeye.pick.service.RequisitionMaterialService;
import com.skyeye.pick.service.RequisitionOutLetService;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.RequisitionOutLet;
import com.skyeye.pickconfirm.classenum.ConfirmFromType;
import com.skyeye.pickconfirm.dao.ConfirmPutDao;
import com.skyeye.pickconfirm.entity.ConfirmPut;
import com.skyeye.pickconfirm.service.ConfirmPutService;
import com.skyeye.pickconfirm.service.ConfirmReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: ConfirmPutServiceImpl
 * @Description: 物料接收单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/27 10:04
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "物料接收单", groupName = "物料确认", flowable = true, orderApproval = true)
public class ConfirmPutServiceImpl extends SkyeyeErpOrderServiceImpl<ConfirmPutDao, ConfirmPut> implements ConfirmPutService {

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private ConfirmReturnService confirmReturnService;

    @Autowired
    private DepartmentStockService departmentStockService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private RequisitionOutLetService requisitionOutLetService;

    @Autowired
    private RequisitionMaterialService requisitionMaterialService;

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
    public void validatorEntity(ConfirmPut entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            ConfirmPut confirmPut = selectById(entity.getId());
            entity.setFromId(confirmPut.getFromId());
            entity.setFromTypeId(confirmPut.getFromTypeId());
            entity.setFarmId(confirmPut.getFarmId());
            entity.setDepartmentId(confirmPut.getDepartmentId());
            entity.setSalesman(confirmPut.getSalesman());
        }
        checkMaterialNorms(entity, false);
        checkNormsCodeAndSave(entity, true);
    }

    @Override
    public void createPrepose(ConfirmPut entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void writePostpose(ConfirmPut entity, String userId) {
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
    public ConfirmPut getDataFromDb(String id) {
        ConfirmPut confirmPut = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(confirmPut);
        return confirmPut;
    }

    @Override
    public ConfirmPut selectById(String id) {
        ConfirmPut confirmPut = super.selectById(id);
        if (confirmPut.getFromTypeId() == ConfirmFromType.DEPOT_OUT.getKey()) {
            // 仓库出库单
            depotOutService.setDataMation(confirmPut, ConfirmPut::getFromId);
        }
        // 车间
        farmService.setDataMation(confirmPut, ConfirmPut::getFarmId);
        // 部门
        iDepmentService.setDataMation(confirmPut, ConfirmPut::getDepartmentId);
        return confirmPut;
    }

    private void checkMaterialNorms(ConfirmPut entity, boolean setData) {
        // 当前物料接收单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达物料接收单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == ConfirmFromType.DEPOT_OUT.getKey()) {
            // 仓库出库单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(ConfirmPut entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        DepotOut depotOut = depotOutService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(depotOut.getErpOrderItemList())) {
            throw new CustomException("该仓库出库单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(depotOut.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达物料退货单的商品信息
        Map<String, String> returnExecuteNum = confirmReturnService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 仓库出库单数量 - 当前单据数量 - 已经下达物料接收单的数量 - 已经下达物料退货单的数量
        super.setOrCheckOperNumber(depotOut.getErpOrderItemList(), setData, orderNormsNum, executeNum, returnExecuteNum);

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
    public void approvalEndIsSuccess(ConfirmPut entity) {
        ConfirmPut oldEntity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(oldEntity, true);
        // 校验并修改条形码信息
        checkNormsCodeAndSave(oldEntity, false);
        // 减少在途库存，需要指定objectId以匹配对应的在途库存
        String machinId = getMachinIdFromConfirmPut(oldEntity);
        oldEntity.getErpOrderItemList().forEach(pickChild -> {
            departmentStockService.updateDepartmentStock(oldEntity.getDepartmentId(), oldEntity.getFarmId(),
                pickChild.getMaterialId(), pickChild.getNormsId(), pickChild.getOperNumber(), DepotPutOutType.OUT.getKey(), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey(), machinId);
        });
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 接收需要入库到对应的车间/部门的仓库下
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected List<String> checkNormsCodeAndSave(ConfirmPut entity, Boolean onlyCheck) {
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
            //  2.2 只有部门信息为空的说明没有领料，才可以进行物料接收。部门信息不为空，说明该条形码已经在部门/车间的库存里
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
            if (!onlyCheck) {
                // 批量修改条形码信息
                materialNormsCodeList.forEach(materialNormsCode -> {
                    materialNormsCode.setDepartmentId(entity.getDepartmentId());
                    materialNormsCode.setFarmId(entity.getFarmId());
                    materialNormsCode.setPickUseState(PickNormsCodeUseState.WAIT_USE.getKey());
                    materialNormsCode.setPickState(null);
                });
                materialNormsCodeService.updateEntityPick(materialNormsCodeList);
            }
        }
        if (!onlyCheck) {
            // 修改部门/车间的库存，记录关联的加工单ID
            String machinId = getMachinIdFromConfirmPut(entity);
            entity.getErpOrderItemList().forEach(erpOrderItem -> {
                departmentStockService.updateDepartmentStock(entity.getDepartmentId(), entity.getFarmId(), erpOrderItem.getMaterialId(),
                    erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.PUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey(), machinId);
            });
        }
        return allNormsCodeList;
    }

    @Override
    public Map<String, List<String>> calcMaterialNormsCodeByFromId(String fromId) {
        QueryWrapper<ConfirmPut> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ConfirmPut::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ConfirmPut::getIdKey), getServiceClassName());
        // 只查询审批通过，部分出入库，已完成的单据
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
            ErpOrderStateEnum.COMPLETED.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(ConfirmPut::getState), stateList);
        List<ConfirmPut> entityList = list(queryWrapper);
        List<String> ids = entityList.stream().map(ConfirmPut::getId).collect(Collectors.toList());
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

    /**
     * 从加工入库单获取关联的加工单ID
     * 关联关系：ConfirmPut.fromId -> DepotOut.fromId -> RequisitionOutLet.fromId -> RequisitionMaterial.fromId -> Machin.id
     *
     * @param confirmPut 加工入库单
     * @return 加工单ID，如果找不到则返回null
     */
    private String getMachinIdFromConfirmPut(ConfirmPut confirmPut) {
        if (StrUtil.isEmpty(confirmPut.getFromId())) {
            return null;
        }

        try {
            // 1. 获取仓库出库单
            DepotOut depotOut = depotOutService.selectById(confirmPut.getFromId());
            if (depotOut == null || StrUtil.isEmpty(depotOut.getFromId())) {
                return null;
            }

            // 2. 获取领料出库单
            RequisitionOutLet requisitionOutLet = requisitionOutLetService.selectById(depotOut.getFromId());
            if (requisitionOutLet == null || StrUtil.isEmpty(requisitionOutLet.getFromId())) {
                return null;
            }

            // 3. 获取领料单
            RequisitionMaterial requisitionMaterial = requisitionMaterialService.selectById(requisitionOutLet.getFromId());
            if (requisitionMaterial == null || StrUtil.isEmpty(requisitionMaterial.getFromId())) {
                return null;
            }

            // 4. 返回加工单ID
            return requisitionMaterial.getFromId();
        } catch (Exception e) {
            // 如果查询失败，返回null，不影响主流程
            return null;
        }
    }
}
