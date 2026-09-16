/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.contract.classenum.SupplierContractChildStateEnum;
import com.skyeye.contract.classenum.SupplierContractFromType;
import com.skyeye.contract.classenum.SupplierContractStateEnum;
import com.skyeye.contract.dao.SupplierContractDao;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.contract.entity.SupplierContractChild;
import com.skyeye.contract.service.SupplierContractChildService;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.purchase.classenum.PurchaseOrderFromType;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.service.PurchaseOrderService;
import com.skyeye.request.classenum.PurchaseRequestStateEnum;
import com.skyeye.request.entity.PurchaseRequest;
import com.skyeye.request.entity.PurchaseRequestFixedChild;
import com.skyeye.request.service.PurchaseRequestService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SupplierContractServiceImpl
 * @Description: 供应商合同管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:05
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "供应商合同管理", groupName = "供应商合同管理", flowable = true, orderApproval = true)
public class SupplierContractServiceImpl extends SkyeyeBusinessServiceImpl<SupplierContractDao, SupplierContract> implements SupplierContractService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private SupplierContractChildService supplierContractChildService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private PurchaseRequestService purchaseRequestService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public QueryWrapper<SupplierContract> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SupplierContract> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getObjectId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getFromId), commonPageInfo.getFromId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("projectId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getProjectId), commonPageInfo.getCustomParamsMapStr("projectId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        purchaseRequestService.setRequestMationByFromId(beans, "fromId", "fromMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public void validatorEntity(SupplierContract entity) {
        super.validatorEntity(entity);
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(SupplierContract entity) {
        getTotalPrice(entity);
        if (CollectionUtil.isNotEmpty(entity.getSupplierContractChildList())) {
            entity.setChildState(SupplierContractChildStateEnum.PENDING_ORDER.getKey());
        }
        // 设置商品为使用中
        entity.getSupplierContractChildList().forEach(supplierContractChild -> {
            materialService.setUsed(supplierContractChild.getMaterialId());
        });
        super.createPrepose(entity);
    }

    @Override
    public void updatePrepose(SupplierContract entity) {
        getTotalPrice(entity);
        if (CollectionUtil.isNotEmpty(entity.getSupplierContractChildList())) {
            entity.setChildState(SupplierContractChildStateEnum.PENDING_ORDER.getKey());
        }
    }

    private void checkMaterialNorms(SupplierContract entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        if (CollectionUtil.isEmpty(entity.getSupplierContractChildList())) {
            throw new CustomException("合同下无商品信息，请确认.");
        }
        // 当前订单的商品数量
        Map<String, String> orderNormsNum = entity.getSupplierContractChildList().stream()
            .collect(Collectors.toMap(SupplierContractChild::getNormsId, 
                child -> StrUtil.isEmpty(child.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : child.getOperNumber()));
        // 获取已经签订合同的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == SupplierContractFromType.PURCHASE_REQUEST.getKey()) {
            PurchaseRequest purchaseRequest = purchaseRequestService.selectById(entity.getFromId());
            List<String> fromNormsIds = purchaseRequest.getPurchaseRequestFixedChildList().stream()
                .map(PurchaseRequestFixedChild::getNormsId).collect(Collectors.toList());
            // 求差集(采购申请单中不包含的商品)
            List<String> diffList = inSqlNormsId.stream()
                .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
                List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
                throw new CustomException(String.format(Locale.ROOT, "该采购申请单下未包含如下商品规格：【%s】.",
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
            }
            purchaseRequest.getPurchaseRequestFixedChildList().forEach(purchaseRequestFixedChild -> {
                String purchaseRequestOperNumber = StrUtil.isEmpty(purchaseRequestFixedChild.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : purchaseRequestFixedChild.getOperNumber();
                String orderNum = orderNormsNum.getOrDefault(purchaseRequestFixedChild.getNormsId(), CommonNumConstants.NUM_ZERO.toString());
                if (StrUtil.isEmpty(orderNum)) {
                    orderNum = CommonNumConstants.NUM_ZERO.toString();
                }
                String executeNumValue = executeNum.getOrDefault(purchaseRequestFixedChild.getNormsId(), CommonNumConstants.NUM_ZERO.toString());
                if (StrUtil.isEmpty(executeNumValue)) {
                    executeNumValue = CommonNumConstants.NUM_ZERO.toString();
                }
                String surplusNum = CalculationUtil.subtract(
                    CalculationUtil.subtract(purchaseRequestOperNumber, orderNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP),
                    executeNumValue, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
                if (CalculationUtil.compareTo(surplusNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                    throw new CustomException("超出采购申请单的商品数量.");
                }
                if (setData) {
                    purchaseRequestFixedChild.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = purchaseRequest.getPurchaseRequestFixedChildList().stream()
                    .filter(purchaseRequestFixedChild -> {
                        String operNumber = StrUtil.isEmpty(purchaseRequestFixedChild.getOperNumber()) 
                            ? CommonNumConstants.NUM_ZERO.toString() 
                            : purchaseRequestFixedChild.getOperNumber();
                        return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                    }).collect(Collectors.toList());
                // 如果该采购申请单的商品已经全部签订合同，那说明已经完成了申请单的内容
                if (CollectionUtil.isEmpty(purchaseRequestFixedChildList)) {
                    purchaseRequestService.editStateById(purchaseRequest.getId(), PurchaseRequestStateEnum.PROCUREMENT_COMPLETED.getKey());
                } else {
                    purchaseRequestService.editStateById(purchaseRequest.getId(), PurchaseRequestStateEnum.PARTIAL_PROCUREMENT.getKey());
                }
            }
        }
    }

    private void getTotalPrice(SupplierContract entity) {
        // 计算关联的产品总价
        String totalPrice = supplierContractChildService.calcOrderAllTotalPrice(entity.getSupplierContractChildList());
        entity.setMaterialTotalPrice(totalPrice);
    }

    @Override
    public void writePostpose(SupplierContract entity, String userId) {
        supplierContractChildService.saveList(entity.getId(), entity.getSupplierContractChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        supplierContractChildService.deleteByParentId(id);
    }

    @Override
    public SupplierContract getDataFromDb(String id) {
        SupplierContract supplierContract = super.getDataFromDb(id);
        // 设置合同商品信息
        List<SupplierContractChild> supplierContractChildList = supplierContractChildService.selectByParentId(supplierContract.getId());
        supplierContract.setSupplierContractChildList(supplierContractChildList);
        return supplierContract;
    }

    @Override
    public SupplierContract selectById(String id) {
        SupplierContract supplierContract = super.selectById(id);
        Map<String, Object> department = iDepmentService.queryDataMationById(supplierContract.getDepartmentId());
        supplierContract.setDepartmentMation(department);
        // 联系人信息
        iContactsService.setDataMation(supplierContract, SupplierContract::getContacts);
        // 设置关联人员
        supplierContract.setRelationUserMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(supplierContract.getRelationUserId())));
        // 设置合同商品详情信息
        materialService.setDataMation(supplierContract.getSupplierContractChildList(), SupplierContractChild::getMaterialId);
        supplierContract.getSupplierContractChildList().forEach(supplierContractChild -> {
            MaterialNorms norms = supplierContractChild.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(supplierContractChild.getNormsId(), bean.getId())).findFirst().orElse(null);
            supplierContractChild.setNormsMation(norms);
        });
        if (supplierContract.getFromTypeId() == SupplierContractFromType.PURCHASE_REQUEST.getKey()) {
            // 采购申请单
            purchaseRequestService.setDataMation(supplierContract, SupplierContract::getFromId);
        }

        iProProjectService.setDataMation(supplierContract, SupplierContract::getProjectId);
        return supplierContract;
    }

    @Override
    public List<SupplierContract> getDataFromDb(List<String> idList) {
        List<SupplierContract> supplierContractList = super.getDataFromDb(idList);
        // 设置合同商品信息
        Map<String, List<SupplierContractChild>> childMap = supplierContractChildService.selectByParentId(idList);
        supplierContractList.forEach(supplierContract -> {
            supplierContract.setSupplierContractChildList(childMap.get(supplierContract.getId()));
        });
        return supplierContractList;
    }

    @Override
    public List<SupplierContract> selectByIds(String... ids) {
        List<SupplierContract> supplierContractList = super.selectByIds(ids);
        iDepmentService.setDataMation(supplierContractList, SupplierContract::getDepartmentId);
        // 联系人信息
        iContactsService.setDataMation(supplierContractList, SupplierContract::getContacts);
        // 设置关联人员
        List<String> relationUserIds = supplierContractList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getRelationUserId()))
            .flatMap(norms -> norms.getRelationUserId().stream()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(relationUserIds)) {
            Map<String, Map<String, Object>> userMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(relationUserIds));
            supplierContractList.forEach(supplierContract -> {
                if (CollectionUtil.isEmpty(supplierContract.getRelationUserId())) {
                    return;
                }
                List<Map<String, Object>> userMation = new ArrayList<>();
                supplierContract.getRelationUserId().forEach(operatorId -> {
                    if (!userMap.containsKey(operatorId)) {
                        return;
                    }
                    userMation.add(userMap.get(operatorId));
                });
                supplierContract.setRelationUserMation(userMation);
            });
        }

        // 设置合同商品详情信息
        List<String> materialIds = supplierContractList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getSupplierContractChildList()))
            .flatMap(farm -> farm.getSupplierContractChildList().stream().map(SupplierContractChild::getMaterialId)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(materialIds)) {
            List<Material> materialList = materialService.selectByIds(materialIds.toArray(new String[]{}));
            Map<String, Material> materialMap = materialList.stream().collect(Collectors.toMap(bean -> bean.getId(), item -> item));
            supplierContractList.forEach(supplierContract -> {
                if (CollectionUtil.isNotEmpty(supplierContract.getSupplierContractChildList())) {
                    // 合同商品明细不为空
                    supplierContract.getSupplierContractChildList().forEach(supplierContractChild -> {
                        Material material = materialMap.get(supplierContractChild.getMaterialId());
                        supplierContractChild.setMaterialMation(material);
                        MaterialNorms norms = supplierContractChild.getMaterialMation().getMaterialNorms()
                            .stream().filter(bean -> StrUtil.equals(supplierContractChild.getNormsId(), bean.getId())).findFirst().orElse(null);
                        supplierContractChild.setNormsMation(norms);
                    });
                }
            });
        }

        return supplierContractList;
    }

    /**
     * 根据供应商id获取合同列表用于下拉框选择
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySupplierContractListByObjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectId = map.get("objectId").toString();
        if (StrUtil.isEmpty(objectId)) {
            return;
        }
        QueryWrapper<SupplierContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getState), SupplierContractStateEnum.EXECUTING.getKey());
        List<SupplierContract> supplierContractList = list(queryWrapper);
        supplierContractList.forEach(supplierContract -> {
            supplierContract.setName(supplierContract.getTitle());
        });
        outputObject.setBeans(supplierContractList);
        outputObject.settotal(supplierContractList.size());
    }

    /**
     * 合同执行
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void performSupplierContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        SupplierContract supplierContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(supplierContract, userId, CommonNumConstants.NUM_SEVEN);
        if (supplierContract.getState().equals(FlowableStateEnum.PASS.getKey())) {
            // 审核通过可以执行
            UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getState), SupplierContractStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 合同关闭
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void closeSupplierContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        SupplierContract supplierContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(supplierContract, userId, CommonNumConstants.NUM_EIGHT);
        if (supplierContract.getState().equals(SupplierContractStateEnum.EXECUTING.getKey())) {
            // 执行中可以关闭
            UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getState), SupplierContractStateEnum.CLOSE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 合同搁置
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void shelveSupplierContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        SupplierContract supplierContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(supplierContract, userId, CommonNumConstants.NUM_NINE);
        if (supplierContract.getState().equals(SupplierContractStateEnum.EXECUTING.getKey())) {
            // 执行中可以搁置
            UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getState), SupplierContractStateEnum.LAY_ASIDE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 合同恢复
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void recoverySupplierContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        SupplierContract supplierContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(supplierContract, userId, CommonNumConstants.NUM_TEN);
        if (supplierContract.getState().equals(SupplierContractStateEnum.LAY_ASIDE.getKey())) {
            // 搁置中可以恢复
            UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getState), SupplierContractStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据所属第三方业务数据id查询合同信息
     *
     * @param objectId 所属第三方业务数据id
     * @return
     */
    @Override
    public List<SupplierContract> querySupplierContractListByObjectId(String objectId) {
        QueryWrapper<SupplierContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getObjectId), objectId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<SupplierContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierContract::getFromId), fromId);
        // 只查询审批通过，执行中，关闭，搁置状态的
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), SupplierContractStateEnum.EXECUTING.getKey(),
            SupplierContractStateEnum.CLOSE.getKey(), SupplierContractStateEnum.LAY_ASIDE.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(SupplierContract::getState), stateList);
        List<SupplierContract> supplierContractList = list(queryWrapper);
        List<String> ids = supplierContractList.stream().map(SupplierContract::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        // 获取所有的商品信息
        List<SupplierContractChild> supplierContractChildList = supplierContractChildService.getSupplierContractChildList(ids);
        if (CollectionUtil.isNotEmpty(supplierContractChildList)) {
            // 分组计算已经签订合同的数量
            return supplierContractChildList.stream()
                .collect(Collectors.groupingBy(SupplierContractChild::getNormsId,
                    Collectors.reducing(CommonNumConstants.NUM_ZERO.toString(),
                        child -> StrUtil.isEmpty(child.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : child.getOperNumber(),
                        (a, b) -> CalculationUtil.add(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP))));
        }
        return cn.hutool.core.map.MapUtil.newHashMap();
    }

    @Override
    public void approvalEndIsSuccess(SupplierContract entity) {
        entity = selectById(entity.getId());
        checkMaterialNorms(entity, true);
    }

    @Override
    public void querySupplierContractTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SupplierContract supplierContract = selectById(id);
        if (CollectionUtil.isEmpty(supplierContract.getSupplierContractChildList())) {
            throw new CustomException("合同下无商品信息，无需转采购订单.");
        }
        // 获取已经下达采购订单的商品信息
        Map<String, String> executeNum = purchaseOrderService.calcMaterialNormsNumByFromId(supplierContract.getId());
        supplierContract.getSupplierContractChildList().forEach(supplierContractChild -> {
            String supplierContractOperNumber = StrUtil.isEmpty(supplierContractChild.getOperNumber()) 
                ? CommonNumConstants.NUM_ZERO.toString() 
                : supplierContractChild.getOperNumber();
            String executeNumValue = executeNum.getOrDefault(supplierContractChild.getNormsId(), CommonNumConstants.NUM_ZERO.toString());
            if (StrUtil.isEmpty(executeNumValue)) {
                executeNumValue = CommonNumConstants.NUM_ZERO.toString();
            }
            String surplusNum = CalculationUtil.subtract(supplierContractOperNumber, executeNumValue, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            // 设置未下达采购订单的商品数量
            supplierContractChild.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成采购订单
        supplierContract.setSupplierContractChildList(supplierContract.getSupplierContractChildList().stream()
            .filter(purchaseRequestChild -> {
                String operNumber = StrUtil.isEmpty(purchaseRequestChild.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : purchaseRequestChild.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));

        supplierService.setDataMation(supplierContract, SupplierContract::getObjectId);

        outputObject.setBean(supplierContract);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void supplierContractToOrder(InputObject inputObject, OutputObject outputObject) {
        PurchaseOrder purchaseOrder = inputObject.getParams(PurchaseOrder.class);
        purchaseOrder.setFromId(purchaseOrder.getId());
        purchaseOrder.setFromTypeId(PurchaseOrderFromType.SUPPLIER_CONTRACT.getKey());
        purchaseOrder.setId(null);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 保存采购订单
        purchaseOrderService.createEntity(purchaseOrder, userId);
    }

    @Override
    public void setContractMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<SupplierContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<SupplierContract> supplierContractList = list(queryWrapper);
        Map<String, SupplierContract> supplierContractMap = supplierContractList.stream()
            .collect(Collectors.toMap(SupplierContract::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                SupplierContract entity = supplierContractMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void editChildState(String id, String childState) {
        UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getChildState), childState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void updatePaymentPrice(String contractId, String price) {
        SupplierContract erpContract = selectById(contractId);
        if (StrUtil.equals(erpContract.getState(), SupplierContractStateEnum.EXECUTING.getKey())) {
            // 只有执行中的合同才可以进行付款
            String paidPrice = StrUtil.isEmpty(erpContract.getPaidPrice()) ? CommonNumConstants.NUM_ZERO.toString() : erpContract.getPaidPrice();
            if (StrUtil.isEmpty(price)) {
                price = CommonNumConstants.NUM_ZERO.toString();
            }
            price = CalculationUtil.add(paidPrice, price, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, contractId);
            updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getPaidPrice), price);
            update(updateWrapper);
            refreshCache(contractId);
        } else {
            throw new CustomException("只有执行中的合同才可以进行付款操作。");
        }
    }

    @Override
    public void updateInvoicePrice(String contractId, String invoicePrice) {
        SupplierContract erpContract = selectById(contractId);
        String currentInvoicePrice = StrUtil.isEmpty(erpContract.getInvoicePrice()) ? CommonNumConstants.NUM_ZERO.toString() : erpContract.getInvoicePrice();
        if (StrUtil.isEmpty(invoicePrice)) {
            invoicePrice = CommonNumConstants.NUM_ZERO.toString();
        }
        invoicePrice = CalculationUtil.add(currentInvoicePrice, invoicePrice, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
        UpdateWrapper<SupplierContract> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, contractId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SupplierContract::getInvoicePrice), invoicePrice);
        update(updateWrapper);
        refreshCache(contractId);
    }

}
