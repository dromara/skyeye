/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.contract.classenum.SupplierContractFromType;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialService;
import com.skyeye.request.classenum.*;
import com.skyeye.request.dao.PurchaseRequestDao;
import com.skyeye.request.entity.PurchaseRequest;
import com.skyeye.request.entity.PurchaseRequestChild;
import com.skyeye.request.entity.PurchaseRequestFixedChild;
import com.skyeye.request.entity.PurchaseRequestInquiryChild;
import com.skyeye.request.service.PurchaseRequestChildService;
import com.skyeye.request.service.PurchaseRequestFixedChildService;
import com.skyeye.request.service.PurchaseRequestInquiryChildService;
import com.skyeye.request.service.PurchaseRequestService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.supplier.entity.Supplier;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PurchaseRequestServiceImpl
 * @Description: 采购申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购申请", groupName = "采购申请", flowable = true, orderApproval = true)
public class PurchaseRequestServiceImpl extends SkyeyeBusinessServiceImpl<PurchaseRequestDao, PurchaseRequest> implements PurchaseRequestService {

    @Autowired
    private PurchaseRequestChildService purchaseRequestChildService;

    @Autowired
    private PurchaseRequestInquiryChildService purchaseRequestInquiryChildService;

    @Autowired
    private PurchaseRequestFixedChildService purchaseRequestFixedChildService;

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private ITenantService iTenantService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public void createPrepose(PurchaseRequest entity) {
        super.createPrepose(entity);
        // 设置询价类型
        Integer inquiryState = PurchaseRequestInquiryState.NOT_INQUIRY.getKey();
        for (PurchaseRequestChild purchaseRequestChild : entity.getPurchaseRequestChildList()) {
            if (purchaseRequestChild.getNeedInquiry() == PurchaseRequestChildInquiry.INQUIRY.getKey()) {
                inquiryState = PurchaseRequestInquiryState.WAIT_INQUIRY.getKey();
            }
        }
        entity.setInquiryState(inquiryState);
        // 设置商品为使用中
        entity.getPurchaseRequestChildList().forEach(purchaseRequestChild -> {
            materialService.setUsed(purchaseRequestChild.getMaterialId());
        });
        getTotalPrice(entity);
    }

    @Override
    public void updatePrepose(PurchaseRequest entity) {
        super.updatePrepose(entity);
        // 设置询价类型
        Integer inquiryState = PurchaseRequestInquiryState.NOT_INQUIRY.getKey();
        for (PurchaseRequestChild purchaseRequestChild : entity.getPurchaseRequestChildList()) {
            if (purchaseRequestChild.getNeedInquiry() == PurchaseRequestChildInquiry.INQUIRY.getKey()) {
                inquiryState = PurchaseRequestInquiryState.WAIT_INQUIRY.getKey();
            }
        }
        entity.setInquiryState(inquiryState);
        getTotalPrice(entity);
    }

    private void getTotalPrice(PurchaseRequest entity) {
        // 计算关联的产品总价
        String totalPrice = purchaseRequestChildService.calcOrderAllTotalPrice(entity.getPurchaseRequestChildList());
        entity.setTotalPrice(totalPrice);
    }

    @Override
    public void writePostpose(PurchaseRequest entity, String userId) {
        purchaseRequestChildService.saveList(entity.getId(), entity.getPurchaseRequestChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        purchaseRequestChildService.deleteByParentId(id);
        purchaseRequestInquiryChildService.deleteByParentId(id, StrUtil.EMPTY);
    }

    @Override
    public PurchaseRequest getDataFromDb(String id) {
        PurchaseRequest purchaseRequest = super.getDataFromDb(id);
        // 商品明细信息
        List<PurchaseRequestChild> purchaseRequestChildList = purchaseRequestChildService.selectByParentId(purchaseRequest.getId());
        purchaseRequest.setPurchaseRequestChildList(purchaseRequestChildList);

        // 询价信息：按 quoteSource 拆分为后端添加与供应商报价
        List<PurchaseRequestInquiryChild> purchaseRequestInquiryChildList = purchaseRequestInquiryChildService.selectByParentId(purchaseRequest.getId());
        if (CollectionUtil.isNotEmpty(purchaseRequestInquiryChildList)) {
            List<PurchaseRequestInquiryChild> backendList = purchaseRequestInquiryChildList.stream()
                .filter(bean -> InquiryQuoteSourceEnum.BACKEND.getKey().equals(bean.getQuoteSource()))
                .collect(Collectors.toList());
            List<PurchaseRequestInquiryChild> supplierList = purchaseRequestInquiryChildList.stream()
                .filter(bean -> InquiryQuoteSourceEnum.SUPPLIER.getKey().equals(bean.getQuoteSource()))
                .collect(Collectors.toList());
            purchaseRequest.setPurchaseRequestInquiryChildList(backendList);
            purchaseRequest.setPurchaseRequestSupplierInquiryChildList(supplierList);
        }

        // 定价信息
        List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = purchaseRequestFixedChildService.selectByParentId(purchaseRequest.getId());
        purchaseRequest.setPurchaseRequestFixedChildList(purchaseRequestFixedChildList);
        return purchaseRequest;
    }

    @Override
    public PurchaseRequest selectById(String id) {
        PurchaseRequest purchaseRequest = super.selectById(id);
        // 设置子单据的产品信息
        materialService.setDataMation(purchaseRequest.getPurchaseRequestChildList(), PurchaseRequestChild::getMaterialId);
        purchaseRequest.getPurchaseRequestChildList().forEach(purchaseRequestChild -> {
            MaterialNorms norms = purchaseRequestChild.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(purchaseRequestChild.getNormsId(), bean.getId())).findFirst().orElse(null);
            purchaseRequestChild.setNormsMation(norms);
            // 设置子单据的询价状态信息
            Map<String, Object> needInquiryMation = new HashMap<>();
            needInquiryMation.put("name", PurchaseRequestChildInquiry.getName(purchaseRequestChild.getNeedInquiry()));
            purchaseRequestChild.setNeedInquiryMation(needInquiryMation);
        });

        if (purchaseRequest.getInquiryState() != PurchaseRequestInquiryState.NOT_INQUIRY.getKey()) {
            // 需要询价的申请单
            if (purchaseRequest.getInquiryState() == PurchaseRequestInquiryState.INQUIRYING.getKey()) {
                // 询价中
                // 采购申请明细定价信息
                List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = JSONUtil.toList(
                    JSONUtil.toJsonStr(purchaseRequest.getPurchaseRequestChildList()), PurchaseRequestFixedChild.class
                );
                purchaseRequest.setPurchaseRequestFixedChildList(purchaseRequestFixedChildList);
            }
        }
        if (CollectionUtil.isNotEmpty(purchaseRequest.getPurchaseRequestFixedChildList())) {
            // 设置定价明细信息
            materialService.setDataMation(purchaseRequest.getPurchaseRequestFixedChildList(), PurchaseRequestFixedChild::getMaterialId);
            purchaseRequest.getPurchaseRequestFixedChildList().forEach(purchaseRequestFixedChild -> {
                MaterialNorms norms = purchaseRequestFixedChild.getMaterialMation().getMaterialNorms()
                    .stream().filter(bean -> StrUtil.equals(purchaseRequestFixedChild.getNormsId(), bean.getId())).findFirst().orElse(null);
                purchaseRequestFixedChild.setNormsMation(norms);
            });
            // 定价供应商
            supplierService.setDataMation(purchaseRequest.getPurchaseRequestFixedChildList(), PurchaseRequestFixedChild::getLastSupplierId);
        }

        if (CollectionUtil.isNotEmpty(purchaseRequest.getPurchaseRequestInquiryChildList())) {
            // 设置 主动 询价明细的产品信息
            materialService.setDataMation(purchaseRequest.getPurchaseRequestInquiryChildList(), PurchaseRequestInquiryChild::getMaterialId);
            purchaseRequest.getPurchaseRequestInquiryChildList().forEach(purchaseRequestInquiryChild -> {
                MaterialNorms norms = purchaseRequestInquiryChild.getMaterialMation().getMaterialNorms()
                    .stream().filter(bean -> StrUtil.equals(purchaseRequestInquiryChild.getNormsId(), bean.getId())).findFirst().orElse(null);
                purchaseRequestInquiryChild.setNormsMation(norms);
            });
            supplierService.setDataMation(purchaseRequest.getPurchaseRequestInquiryChildList(), PurchaseRequestInquiryChild::getSupplierId);
            // 设置子单据开票类型
            iSysDictDataService.setDataMation(purchaseRequest.getPurchaseRequestInquiryChildList(), PurchaseRequestInquiryChild::getTypeId);
        }

        if (CollectionUtil.isNotEmpty(purchaseRequest.getPurchaseRequestSupplierInquiryChildList())) {
            // 设置 供应商 报价明细的产品信息
            materialService.setDataMation(purchaseRequest.getPurchaseRequestSupplierInquiryChildList(), PurchaseRequestInquiryChild::getMaterialId);
            purchaseRequest.getPurchaseRequestSupplierInquiryChildList().forEach(purchaseRequestSupplierInquiryChild -> {
                MaterialNorms norms = purchaseRequestSupplierInquiryChild.getMaterialMation().getMaterialNorms()
                    .stream().filter(bean -> StrUtil.equals(purchaseRequestSupplierInquiryChild.getNormsId(), bean.getId())).findFirst().orElse(null);
                purchaseRequestSupplierInquiryChild.setNormsMation(norms);
            });
            supplierService.setDataMation(purchaseRequest.getPurchaseRequestSupplierInquiryChildList(), PurchaseRequestInquiryChild::getSupplierId);
        }

        // 设置项目信息
        iProProjectService.setDataMation(purchaseRequest, PurchaseRequest::getProjectId);
        // 设置定价人员信息
        iAuthUserService.setDataMation(purchaseRequest, PurchaseRequest::getFixedPriceUserId);

        // 设置允许报价的供应商信息
        if (PurchaseRequestSupplierQuoteType.SPECIFIED_SUPPLIER.getKey().equals(purchaseRequest.getSupplierQuoteType())
            && CollectionUtil.isNotEmpty(purchaseRequest.getSupplierId())) {
            List<Supplier> supplierMationList = supplierService.selectByIds(purchaseRequest.getSupplierId().toArray(new String[]{}));
            purchaseRequest.setSupplierMation(supplierMationList);
        }
        return purchaseRequest;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void inquiryPurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        PurchaseRequest purchaseRequest = selectById(id);
        if (!purchaseRequest.getState().equals(FlowableStateEnum.PASS.getKey())) {
            throw new CustomException("只有审批通过的申请单可以进行询价.");
        }
        if (PurchaseRequestInquiryState.NOT_INQUIRY.getKey() == purchaseRequest.getInquiryState()) {
            // 无需询价
            throw new CustomException("该申请单无需进行询价.");
        }
        List<String> oldNormsId = purchaseRequest.getPurchaseRequestChildList().stream()
            .map(PurchaseRequestChild::getNormsId).collect(Collectors.toList());

        List<PurchaseRequestInquiryChild> purchaseRequestInquiryChildList = JSONUtil.toList(
            params.getOrDefault("purchaseRequestInquiryChildList", "[]").toString(), PurchaseRequestInquiryChild.class
        );
        purchaseRequestInquiryChildList.forEach(purchaseRequestInquiryChild -> {
            if (!oldNormsId.contains(purchaseRequestInquiryChild.getNormsId())) {
                throw new CustomException("存在申请单中不包含的商品规格信息，请确认.");
            }
        });
        purchaseRequestInquiryChildService.saveList(id, purchaseRequestInquiryChildList);
        // 设置为询价中
        editInquiryStateById(id, PurchaseRequestInquiryState.INQUIRYING.getKey(), null);
    }

    @Override
    @IgnoreTenant
    public void editInquiryStateById(String id, Integer inquiryState, String fixedPriceUserId) {
        UpdateWrapper<PurchaseRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseRequest::getInquiryState), inquiryState);
        if (StrUtil.isNotEmpty(fixedPriceUserId)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseRequest::getFixedPriceUserId), fixedPriceUserId);
        }
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void approvalEndIsSuccess(PurchaseRequest entity) {
        // 审批通过的且无需询价的需要将采购申请明细转到定价信息表中
        if (PurchaseRequestInquiryState.NOT_INQUIRY.getKey() == entity.getInquiryState()) {
            PurchaseRequest purchaseRequest = selectById(entity.getId());
            List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = JSONUtil.toList(
                JSONUtil.toJsonStr(purchaseRequest.getPurchaseRequestChildList()), PurchaseRequestFixedChild.class
            );
            // 保存定价信息
            purchaseRequestFixedChildService.saveList(entity.getId(), purchaseRequestFixedChildList);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void fixedPricePurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        PurchaseRequest purchaseRequest = selectById(id);
        if (!purchaseRequest.getState().equals(FlowableStateEnum.PASS.getKey())) {
            throw new CustomException("只有审批通过的申请单可以进行定价.");
        }
        if (PurchaseRequestInquiryState.NOT_INQUIRY.getKey() == purchaseRequest.getInquiryState()) {
            // 无需定价
            throw new CustomException("该申请单无需进行定价.");
        }
        // 采购申请明细定价信息
        List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = JSONUtil.toList(
            params.get("purchaseRequestFixedChildList").toString(), PurchaseRequestFixedChild.class
        );
        // 保存定价信息
        purchaseRequestFixedChildService.saveList(id, purchaseRequestFixedChildList);
        // 设置为询价完毕
        String fixedPriceUserId = params.get("fixedPriceUserId").toString();
        editInquiryStateById(id, PurchaseRequestInquiryState.COMPLATE_INQUIRY.getKey(), fixedPriceUserId);
    }

    @Override
    public void queryPurchaseRequestTransferContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        PurchaseRequest purchaseRequest = getPurchaseRequest(id);
        // 根据供应商进行分组生成合同
        Map<String, List<PurchaseRequestFixedChild>> requestChildMap = purchaseRequest.getPurchaseRequestFixedChildList().stream()
            .collect(Collectors.groupingBy(bean -> {
                return StrUtil.isEmpty(bean.getLastSupplierId()) ? "-" : bean.getLastSupplierId();
            }));

        outputObject.setBean(requestChildMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private PurchaseRequest getPurchaseRequest(String id) {
        PurchaseRequest purchaseRequest = selectById(id);
        if (purchaseRequest.getState().equals(PurchaseRequestStateEnum.PROCUREMENT_COMPLETED.getKey())) {
            throw new CustomException("该申请单已完成采购合同申请，无法再次进行合同");
        }
        return getPurchaseRequest(purchaseRequest);
    }

    private PurchaseRequest getPurchaseRequest(PurchaseRequest purchaseRequest) {
        // 获取已经签订合同的商品信息
        Map<String, String> executeNum = supplierContractService.calcMaterialNormsNumByFromId(purchaseRequest.getId());
        if (CollectionUtil.isEmpty(purchaseRequest.getPurchaseRequestFixedChildList())) {
            purchaseRequest.setPurchaseRequestFixedChildList(CollectionUtil.newArrayList());
            return purchaseRequest;
        }
        purchaseRequest.getPurchaseRequestFixedChildList().forEach(purchaseRequestFixedChild -> {
            String operNumber = StrUtil.isEmpty(purchaseRequestFixedChild.getOperNumber())
                ? CommonNumConstants.NUM_ZERO.toString()
                : purchaseRequestFixedChild.getOperNumber();
            String contractNum = executeNum.getOrDefault(purchaseRequestFixedChild.getNormsId(), CommonNumConstants.NUM_ZERO.toString());
            if (StrUtil.isEmpty(contractNum)) {
                contractNum = CommonNumConstants.NUM_ZERO.toString();
            }
            // 设置未签合同的商品数量
            String surplusNum = CalculationUtil.subtract(operNumber, contractNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            purchaseRequestFixedChild.setOperNumber(surplusNum);
        });
        // 过滤掉申请数量为0的进行生成合同
        purchaseRequest.setPurchaseRequestFixedChildList(purchaseRequest.getPurchaseRequestFixedChildList().stream()
            .filter(purchaseRequestChild -> {
                String operNumber = StrUtil.isEmpty(purchaseRequestChild.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : purchaseRequestChild.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        return purchaseRequest;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void purchaseRequestToContract(InputObject inputObject, OutputObject outputObject) {
        SupplierContract supplierContract = inputObject.getParams(SupplierContract.class);
        supplierContract.setFromId(supplierContract.getId());
        supplierContract.setFromTypeId(SupplierContractFromType.PURCHASE_REQUEST.getKey());
        supplierContract.setId(null);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 保存供应商合同
        supplierContractService.createEntity(supplierContract, userId, false);
    }

    @Override
    @IgnoreTenant
    public void setRequestMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<PurchaseRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<PurchaseRequest> purchaseRequestList = list(queryWrapper);
        Map<String, PurchaseRequest> purchaseRequestMap = purchaseRequestList.stream()
            .collect(Collectors.toMap(PurchaseRequest::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                PurchaseRequest entity = purchaseRequestMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setQuoteInfo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String supplierQuoteType = map.get("supplierQuoteType").toString();
        String supplierId = map.get("supplierId").toString();
        String quoteStartTime = map.get("quoteStartTime").toString();
        String quoteEndTime = map.get("quoteEndTime").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        PurchaseRequest purchaseRequest = selectById(id);
        if (purchaseRequest == null) {
            outputObject.setreturnMessage("采购申请不存在");
            return;
        }

        // 验证供应商报价类型参数
        if (!PurchaseRequestSupplierQuoteType.ALL_SUPPLIER.getKey().equals(supplierQuoteType) &&
            !PurchaseRequestSupplierQuoteType.SPECIFIED_SUPPLIER.getKey().equals(supplierQuoteType)) {
            outputObject.setreturnMessage("供应商报价类型参数错误");
            return;
        }

        // 验证指定供应商时的必填性
        if (PurchaseRequestSupplierQuoteType.SPECIFIED_SUPPLIER.getKey().equals(supplierQuoteType) && StrUtil.isEmpty(supplierId)) {
            outputObject.setreturnMessage("选择指定供应商报价时，必须选择至少一个供应商");
            return;
        }

        // 验证时间段合理性
        if (StrUtil.isNotEmpty(quoteStartTime) && StrUtil.isNotEmpty(quoteEndTime)) {
            if (quoteStartTime.compareTo(quoteEndTime) > 0) {
                outputObject.setreturnMessage("报价开始时间不能晚于结束时间");
                return;
            }
        }

        // 更新报价信息
        UpdateWrapper<PurchaseRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(PurchaseRequest::getSupplierQuoteType), supplierQuoteType)
            .set(MybatisPlusUtil.toColumns(PurchaseRequest::getSupplierId), supplierId)
            .set(MybatisPlusUtil.toColumns(PurchaseRequest::getQuoteStartTime), quoteStartTime)
            .set(MybatisPlusUtil.toColumns(PurchaseRequest::getQuoteEndTime), quoteEndTime)
            .set(MybatisPlusUtil.toColumns(PurchaseRequest::getLastUpdateId), userId)
            .set(MybatisPlusUtil.toColumns(PurchaseRequest::getLastUpdateTime), DateUtil.getTimeAndToString());

        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @IgnoreTenant
    public void queryEnterpriseQuoteRequestList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);

        // 从登录用户信息中获取营业执照注册号
        String socialCreditCode = InputObject.getLogParamsStatic().getOrDefault("socialCreditCode", StrUtil.EMPTY).toString();
        if (StrUtil.isEmpty(socialCreditCode)) {
            throw new CustomException("未获取到企业营业执照注册号，请先登录企业账户");
        }

        // 使用 QueryWrapper + EXISTS 子查询，避免先查 1000 个 supplierId 再拼 OR 条件
        // EXISTS 在找到第一个匹配即停止，支持多租户下同一企业有多个 supplierId 的场景
        QueryWrapper<PurchaseRequest> wrapper = new QueryWrapper<>();

        wrapper.isNotNull(MybatisPlusUtil.toColumns(PurchaseRequest::getSupplierQuoteType));

        // 报价类型：全部供应商 或 指定供应商（EXISTS：存在该企业任一租户下的供应商ID在 supplier_id JSON 中）
        Integer deleteFlag = DeleteFlagEnum.NOT_DELETE.getKey();
        wrapper.and(w -> w
            .eq(MybatisPlusUtil.toColumns(PurchaseRequest::getSupplierQuoteType),
                PurchaseRequestSupplierQuoteType.ALL_SUPPLIER.getKey())
            .or(subW -> subW
                .eq(MybatisPlusUtil.toColumns(PurchaseRequest::getSupplierQuoteType),
                    PurchaseRequestSupplierQuoteType.SPECIFIED_SUPPLIER.getKey())
                .apply("EXISTS (SELECT 1 FROM erp_supplier s WHERE s.social_credit_code = {0} AND s.delete_flag = {1} " +
                    "AND JSON_CONTAINS(erp_purchase_request.supplier_id, JSON_QUOTE(s.id), '$'))", socialCreditCode, deleteFlag))
        );

        String currentTime = DateUtil.getYmdTimeAndToString();
        wrapper.and(w -> w
            .isNull(MybatisPlusUtil.toColumns(PurchaseRequest::getQuoteStartTime))
            .or(subW -> subW.le(MybatisPlusUtil.toColumns(PurchaseRequest::getQuoteStartTime), currentTime))
        );
        wrapper.and(w -> w
            .isNull(MybatisPlusUtil.toColumns(PurchaseRequest::getQuoteEndTime))
            .or(subW -> subW.ge(MybatisPlusUtil.toColumns(PurchaseRequest::getQuoteEndTime), currentTime))
        );

        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            wrapper.like(MybatisPlusUtil.toColumns(PurchaseRequest::getOddNumber), commonPageInfo.getKeyword());
        }

        wrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequest::getState), FlowableStateEnum.PASS.getKey());
        wrapper.in(MybatisPlusUtil.toColumns(PurchaseRequest::getInquiryState), PurchaseRequestInquiryState.WAIT_INQUIRY.getKey(),
            PurchaseRequestInquiryState.INQUIRYING.getKey());

        wrapper.orderByDesc(MybatisPlusUtil.toColumns(PurchaseRequest::getCreateTime));

        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<PurchaseRequest> list = list(wrapper);
        List<Map<String, Object>> resultList = CollectionUtil.isEmpty(list)
            ? new ArrayList<>()
            : JSONUtil.toList(JSONUtil.toJsonStr(list), null);

        iTenantService.setMationForMap(resultList, "tenantId", "tenantMation");

        outputObject.setBeans(resultList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public PurchaseRequest queryByIdAndNoIsolation(String id) {
        QueryWrapper<PurchaseRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        return getOne(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public void queryEnterprisePurchaseRequestDetail(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        // 采购申请：自己写 wrapper，忽略租户
        QueryWrapper<PurchaseRequest> prWrapper = new QueryWrapper<>();
        prWrapper.eq(CommonConstants.ID, id);
        PurchaseRequest purchaseRequest = getOne(prWrapper);
        if (purchaseRequest == null) {
            throw new CustomException("采购申请不存在");
        }
        // 商品明细：调用子单据 service
        List<PurchaseRequestChild> purchaseRequestChildList = purchaseRequestChildService.selectByParentId(id);
        // 判断当前企业账户是否已对各商品规格报过价
        setEnterpriseHasQuotedForChildList(id, purchaseRequest.getTenantId(), purchaseRequestChildList);
        purchaseRequest.setPurchaseRequestChildList(purchaseRequestChildList);
        materialService.setDataMation(purchaseRequest.getPurchaseRequestChildList(), PurchaseRequestChild::getMaterialId);
        purchaseRequest.getPurchaseRequestChildList().forEach(purchaseRequestChild -> {
            MaterialNorms norms = purchaseRequestChild.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(purchaseRequestChild.getNormsId(), bean.getId())).findFirst().orElse(null);
            purchaseRequestChild.setNormsMation(norms);
        });
        // 组装结果并填充租户信息
        Map<String, Object> result = BeanUtil.beanToMap(purchaseRequest);
        iTenantService.setMationForMap(result, "tenantId", "tenantMation");
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 为商品明细设置当前企业账户是否已报过价
     * 若未登录企业账户或未关联供应商，则均设为 false
     */
    private void setEnterpriseHasQuotedForChildList(String parentId, String tenantId, List<PurchaseRequestChild> purchaseRequestChildList) {
        if (CollectionUtil.isEmpty(purchaseRequestChildList)) {
            return;
        }
        purchaseRequestChildList.forEach(c -> c.setEnterpriseHasQuoted(false));
        String socialCreditCode = InputObject.getLogParamsStatic().getOrDefault("socialCreditCode", StrUtil.EMPTY).toString();
        if (StrUtil.isEmpty(socialCreditCode)) {
            return;
        }
        Supplier supplier = supplierService.queryBySocialCreditCodeAndPointTenant(socialCreditCode, tenantId);
        if (supplier == null) {
            return;
        }
        // 采购申请询价明细
        List<PurchaseRequestInquiryChild> quotedList = purchaseRequestInquiryChildService.selectByParentId(parentId);
        // key: materialId_normsId, value: 报价明细id（同一规格多条时保留第一条）
        Map<String, String> quotedIdMap = quotedList.stream()
            .filter(c -> supplier.getId().equals(c.getSupplierId()) && InquiryQuoteSourceEnum.SUPPLIER.getKey().equals(c.getQuoteSource()))
            .collect(Collectors.toMap(
                c -> c.getMaterialId() + "_" + c.getNormsId(),
                PurchaseRequestInquiryChild::getId,
                (existing, replacement) -> existing
            ));
        for (PurchaseRequestChild child : purchaseRequestChildList) {
            String key = child.getMaterialId() + "_" + child.getNormsId();
            String inquiryChildId = quotedIdMap.get(key);
            child.setEnterpriseHasQuoted(StrUtil.isNotEmpty(inquiryChildId));

            child.setEnterpriseQuotedInquiryChildId(inquiryChildId);
        }
    }

}
