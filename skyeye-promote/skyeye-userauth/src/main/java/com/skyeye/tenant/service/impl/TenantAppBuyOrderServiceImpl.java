/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

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
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayOrderStatusResp;
import com.skyeye.pay.service.PayChannelService;
import com.skyeye.pay.service.PayService;
import com.skyeye.tenant.classenum.TenantAppBuyOrderPayState;
import com.skyeye.tenant.classenum.TenantAppBuyOrderSource;
import com.skyeye.tenant.classenum.TenantTokenBillState;
import com.skyeye.tenant.dao.TenantAppBuyOrderDao;
import com.skyeye.tenant.entity.*;
import com.skyeye.tenant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantAppBuyOrderServiceImpl
 * @Description: 订单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/30 16:25
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "订单管理", groupName = "租户管理", flowable = true, tenant = TenantEnum.PLATE)
public class TenantAppBuyOrderServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppBuyOrderDao, TenantAppBuyOrder> implements TenantAppBuyOrderService {

    /**
     * 租户购买 PayApp.appKey，与前端 queryEnabledPayChannelList 一致
     */
    private static final String TENANT_BUY_PAY_APP_KEY = "tenant-buy";

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantAppService tenantAppService;

    @Autowired
    private TenantAppBuyOrderNumService tenantAppBuyOrderNumService;

    @Autowired
    private TenantAppBuyOrderYearService tenantAppBuyOrderYearService;

    @Autowired
    private TenantAppBuyOrderTokenService tenantAppBuyOrderTokenService;

    @Autowired
    private TenantTokenAccountService tenantTokenAccountService;

    @Autowired
    private TenantTokenBillService tenantTokenBillService;

    @Autowired
    private TenantAppLinkService tenantAppLinkService;

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    @Autowired
    private PayService payService;

    @Autowired
    private PayChannelService payChannelService;

    @Override
    protected void createPrepose(TenantAppBuyOrder entity) {
        if (entity.getOrderSource() == null) {
            entity.setOrderSource(TenantAppBuyOrderSource.PLATFORM.getKey());
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        tenantService.setMationForMap(beans, "buyTenantId", "buyTenantMation");
        return beans;
    }

    @Override
    public void validatorEntity(TenantAppBuyOrder entity) {
        if (isTokenBillOrder(entity)) {
            if (StrUtil.isBlank(entity.getAllPrice())
                || new BigDecimal(entity.getAllPrice()).compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException("账单金额无效");
            }
            entity.setPayState(TenantAppBuyOrderPayState.UNPAID.getKey());
            return;
        }
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderNumList())
            && CollectionUtil.isEmpty(entity.getTenantAppBuyOrderYearList())
            && CollectionUtil.isEmpty(entity.getTenantAppBuyOrderTokenList())) {
            throw new CustomException("订单信息不能为空.");
        }
        validateBuyOrderSeatNum(entity);
        validateBuyOrderAppYear(entity);
        validateBuyOrderToken(entity);
        String totalPrice = "0";
        if (CollectionUtil.isNotEmpty(entity.getTenantAppBuyOrderNumList())) {
            for (TenantAppBuyOrderNum tenantAppBuyOrderNum : entity.getTenantAppBuyOrderNumList()) {
                String allPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(tenantAppBuyOrderNum.getAccountNum()), tenantAppBuyOrderNum.getUnitPrice());
                tenantAppBuyOrderNum.setAllPrice(allPrice);
                totalPrice = CalculationUtil.add(totalPrice, allPrice);
            }
        }
        if (CollectionUtil.isNotEmpty(entity.getTenantAppBuyOrderYearList())) {
            for (TenantAppBuyOrderYear tenantAppBuyOrderYear : entity.getTenantAppBuyOrderYearList()) {
                String allPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(tenantAppBuyOrderYear.getAccountYear()), tenantAppBuyOrderYear.getUnitPrice());
                tenantAppBuyOrderYear.setAllPrice(allPrice);
                totalPrice = CalculationUtil.add(totalPrice, allPrice);
            }
        }
        if (CollectionUtil.isNotEmpty(entity.getTenantAppBuyOrderTokenList())) {
            for (TenantAppBuyOrderToken tokenItem : entity.getTenantAppBuyOrderTokenList()) {
                totalPrice = CalculationUtil.add(totalPrice, tokenItem.getAllPrice());
            }
        }
        entity.setAllPrice(totalPrice);
        // 默认待支付
        entity.setPayState(TenantAppBuyOrderPayState.UNPAID.getKey());
    }

    /**
     * 校验购买席位数是否满足组织类型对应的最低购买数量
     */
    private void validateBuyOrderSeatNum(TenantAppBuyOrder entity) {
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderNumList())) {
            return;
        }
        Tenant buyTenant = tenantService.selectById(entity.getBuyTenantId());
        if (ObjectUtil.isEmpty(buyTenant) || buyTenant.getOrgType() == null) {
            throw new CustomException("购买租户信息不完整，无法校验席位购买数量");
        }
        Integer minBuyAccountNum = platformBaseSettingService.getMinBuyAccountNum(buyTenant.getOrgType());
        String platformUnitPrice = platformBaseSettingService.getAccountUnitPrice();
        for (TenantAppBuyOrderNum tenantAppBuyOrderNum : entity.getTenantAppBuyOrderNumList()) {
            if (tenantAppBuyOrderNum.getAccountNum() == null || tenantAppBuyOrderNum.getAccountNum() < minBuyAccountNum) {
                throw new CustomException("购买席位数不能低于" + minBuyAccountNum + "个");
            }
            if (StrUtil.isBlank(tenantAppBuyOrderNum.getUnitPrice())) {
                tenantAppBuyOrderNum.setUnitPrice(platformUnitPrice);
            }
        }
    }

    /**
     * 校验应用购买行并补全单价
     */
    private void validateBuyOrderAppYear(TenantAppBuyOrder entity) {
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderYearList())) {
            return;
        }
        List<String> appIds = entity.getTenantAppBuyOrderYearList().stream()
            .map(TenantAppBuyOrderYear::getAppId).collect(Collectors.toList());
        Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
        for (TenantAppBuyOrderYear tenantAppBuyOrderYear : entity.getTenantAppBuyOrderYearList()) {
            if (tenantAppBuyOrderYear.getAccountYear() < CommonNumConstants.NUM_ONE) {
                throw new CustomException("购买应用年限不能小于1年");
            }
            TenantApp tenantApp = tenantAppMap.get(tenantAppBuyOrderYear.getAppId());
            if (ObjectUtil.isEmpty(tenantApp)) {
                throw new CustomException("购买的应用不存在");
            }
        }
    }

    private void validateBuyOrderToken(TenantAppBuyOrder entity) {
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderTokenList())) {
            return;
        }
        String tokensPerYuan = platformBaseSettingService.getTokensPerYuan();
        String minBuyAmount = platformBaseSettingService.getMinBuyTokenAmount();
        BigDecimal minAmount = new BigDecimal(minBuyAmount);
        for (TenantAppBuyOrderToken tokenItem : entity.getTenantAppBuyOrderTokenList()) {
            if (StrUtil.isBlank(tokenItem.getBuyAmount())) {
                throw new CustomException("Token 购买金额不能为空");
            }
            BigDecimal buyAmount = new BigDecimal(tokenItem.getBuyAmount());
            if (buyAmount.compareTo(minAmount) < 0) {
                throw new CustomException("Token 购买金额不能低于" + minBuyAmount + "元");
            }
            tokenItem.setTokensPerYuan(tokensPerYuan);
            long tokenQty = buyAmount.multiply(new BigDecimal(tokensPerYuan)).setScale(0, java.math.RoundingMode.DOWN).longValue();
            if (tokenQty <= 0) {
                throw new CustomException("兑换 Token 数量必须大于0，请检查平台计费标准");
            }
            tokenItem.setTokenQty(tokenQty);
            tokenItem.setAllPrice(tokenItem.getBuyAmount());
        }
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void submitTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        TenantAppBuyOrder entity = inputObject.getParams(TenantAppBuyOrder.class);
        entity.setBuyTenantId(TenantContext.getTenantId());
        entity.setOrderSource(TenantAppBuyOrderSource.TENANT.getKey());
        entity.setFormSubType(FormSubType.DRAFT.getKey());
        String userId = inputObject.getLogParams().get("id").toString();
        TenantContext.setTenantId(TenantTypeEnum.PLATFORM.getCode());
        String orderId = createEntity(entity, userId);
        autoApprovalPass(orderId);
        outputObject.setBean(selectById(orderId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void updatePayState(String id, Integer payState, String payRemark) {
        UpdateWrapper<TenantAppBuyOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayState), payState);
        if (TenantAppBuyOrderPayState.PAID.getKey().equals(payState)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayTime), DateUtil.getTimeAndToString());
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayRemark), payRemark);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void writePostpose(TenantAppBuyOrder entity, String userId) {
        tenantAppBuyOrderNumService.saveList(entity.getId(), entity.getTenantAppBuyOrderNumList());
        tenantAppBuyOrderYearService.saveList(entity.getId(), entity.getTenantAppBuyOrderYearList());
        tenantAppBuyOrderTokenService.saveList(entity.getId(), entity.getTenantAppBuyOrderTokenList());
        super.writePostpose(entity, userId);
    }

    @Override
    protected void deletePostpose(String id) {
        tenantAppBuyOrderNumService.deleteByParentId(id);
        tenantAppBuyOrderYearService.deleteByParentId(id);
        tenantAppBuyOrderTokenService.deleteByParentId(id);
        super.deletePostpose(id);
    }

    @Override
    protected void deletePostpose(TenantAppBuyOrder entity) {
        if (entity != null && StrUtil.isNotBlank(entity.getId())) {
            tenantAppBuyOrderNumService.deleteByParentId(entity.getId());
            tenantAppBuyOrderYearService.deleteByParentId(entity.getId());
            tenantAppBuyOrderTokenService.deleteByParentId(entity.getId());
        }
        super.deletePostpose(entity);
    }

    @Override
    public TenantAppBuyOrder getDataFromDb(String id) {
        TenantAppBuyOrder tenantAppBuyOrder = super.getDataFromDb(id);
        tenantAppBuyOrder.setTenantAppBuyOrderNumList(tenantAppBuyOrderNumService.selectByParentId(id));
        tenantAppBuyOrder.setTenantAppBuyOrderYearList(tenantAppBuyOrderYearService.selectByParentId(id));
        tenantAppBuyOrder.setTenantAppBuyOrderTokenList(tenantAppBuyOrderTokenService.selectByParentId(id));
        return tenantAppBuyOrder;
    }

    @Override
    public TenantAppBuyOrder selectById(String id) {
        TenantAppBuyOrder tenantAppBuyOrder = super.selectById(id);
        tenantService.setDataMation(tenantAppBuyOrder, TenantAppBuyOrder::getBuyTenantId);
        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderYearList())) {
            List<String> appIds = tenantAppBuyOrder.getTenantAppBuyOrderYearList().stream().map(TenantAppBuyOrderYear::getAppId).collect(Collectors.toList());
            Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
            tenantAppBuyOrder.getTenantAppBuyOrderYearList().forEach(tenantAppBuyOrderYear -> {
                tenantAppBuyOrderYear.setAppMation(tenantAppMap.get(tenantAppBuyOrderYear.getAppId()));
            });
        }
        return tenantAppBuyOrder;
    }

    @Override
    public void approvalEndIsSuccess(TenantAppBuyOrder entity) {
        // 审批通过后订单进入待支付；标记租户「已购买」及席位/应用交付在支付完成后由 deliverOrderBenefits 处理
    }

    /**
     * 租户自购发起支付：对接统一 PayService。
     * <p>
     * mock 等同步渠道会立即 {@link #completeOrderPaySuccess}；二维码类渠道返回 WAITING，由前端轮询 + 渠道回调完成。
     */
    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void payTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String channelCode = params.get("channelCode").toString();
        String returnUrl = params.get("returnUrl").toString();
        String channelExtras = params.get("channelExtras").toString();

        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertTenantOrderPayable(tenantAppBuyOrder);

        Map<String, Object> payData = buildTenantOrderPayData(tenantAppBuyOrder);
        Map<String, Object> payResult = payService.executePayment(payData, channelCode, returnUrl, channelExtras,
            StrUtil.EMPTY, TENANT_BUY_PAY_APP_KEY);
        Map<String, Object> payOrderRespDTO = JSONUtil.toBean(payResult.get("payOrderRespDTO").toString(), null);
        Map<String, Object> payChannel = JSONUtil.toBean(payResult.get("payChannel").toString(), null);
        Integer payStatus = Integer.parseInt(payOrderRespDTO.get("status").toString());

        if (PayOrderStatusResp.isSuccess(payStatus)) {
            // 同步支付成功：当场交付席位/应用权益
            completeOrderPaySuccess(tenantAppBuyOrder, payChannel, channelCode,
                payOrderRespDTO.containsKey("successTime") ? payOrderRespDTO.get("successTime").toString() : null,
                "租户在线支付");
            outputObject.setBean(selectById(id));
        } else {
            // 异步待支付：记录渠道，返回二维码/跳转信息给前端，等待 notifyTenantAppBuyOrderPaySuccess
            UpdateWrapper<TenantAppBuyOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayType), channelCode);
            update(updateWrapper);
            refreshCache(id);
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("payState", TenantAppBuyOrderPayState.UNPAID.getKey());
            result.put("payOrderRespDTO", payOrderRespDTO);
            result.put("payChannel", payChannel);
            outputObject.setBean(result);
        }
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户自购取消支付（租户资料页订单管理使用，与后台 cancelPayTenantAppBuyOrder 隔离）
     */
    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String payRemark = params.containsKey("payRemark") && StrUtil.isNotBlank(params.get("payRemark").toString())
            ? params.get("payRemark").toString()
            : "租户取消支付";
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertTenantOrderPayable(tenantAppBuyOrder);
        updatePayState(id, TenantAppBuyOrderPayState.PAY_CANCELLED.getKey(), payRemark);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户购买支付成功业务回调（配置在 PayApp.orderNotifyUrl，由 PayNotify 转发，allUse=0 无需登录）。
     * 与 {@link #payTenantSelfPurchaseOrder} 的同步成功分支最终都走 {@link #completeOrderPaySuccess}。
     */
    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void notifyTenantAppBuyOrderPaySuccess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String outTradeNo = params.get("outTradeNo").toString();
        String channelCode = params.containsKey("channelCode") ? params.get("channelCode").toString() : null;
        String successTime = params.containsKey("successTime") ? params.get("successTime").toString() : null;

        TenantAppBuyOrder tenantAppBuyOrder = queryByOddNumber(outTradeNo);
        if (TenantAppBuyOrderPayState.PAID.getKey().equals(tenantAppBuyOrder.getPayState())) {
            // 渠道可能重复回调，已支付则幂等返回
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        assertApprovedAndUnpaid(tenantAppBuyOrder);
        Map<String, Object> payChannelMap = new HashMap<>();
        if (StrUtil.isNotBlank(channelCode)) {
            PayChannel payChannel = payChannelService.getPayChannelByCode(TENANT_BUY_PAY_APP_KEY, channelCode);
            payChannelMap.put("feeRate", payChannel.getFeeRate());
        }
        completeOrderPaySuccess(tenantAppBuyOrder, payChannelMap,
            StrUtil.isNotBlank(channelCode) ? channelCode : tenantAppBuyOrder.getPayType(),
            successTime, "支付渠道回调");
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 前端轮询支付结果（二维码支付场景）
     */
    @Override
    @IgnoreTenant
    public void queryTenantAppBuyOrderPayState(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        if (ObjectUtil.isEmpty(tenantAppBuyOrder)) {
            throw new CustomException("订单不存在");
        }
        assertTenantOrderOwnership(tenantAppBuyOrder);
        Map<String, Object> data = new HashMap<>();
        data.put("id", tenantAppBuyOrder.getId());
        data.put("payState", tenantAppBuyOrder.getPayState());
        data.put("payTime", tenantAppBuyOrder.getPayTime());
        data.put("oddNumber", tenantAppBuyOrder.getOddNumber());
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户端订单归属校验
     */
    private void assertTenantOrderOwnership(TenantAppBuyOrder tenantAppBuyOrder) {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("未获取到当前租户信息");
        }
        if (ObjectUtil.isEmpty(tenantAppBuyOrder) || StrUtil.isEmpty(tenantAppBuyOrder.getId())) {
            throw new CustomException("订单不存在");
        }
        if (!StrUtil.equals(tenantId, tenantAppBuyOrder.getBuyTenantId())) {
            throw new CustomException("无权操作该订单");
        }
    }

    /**
     * 租户端订单支付/取消支付前置校验：当前租户归属、审批通过且待支付
     */
    private void assertTenantOrderPayable(TenantAppBuyOrder tenantAppBuyOrder) {
        assertTenantOrderOwnership(tenantAppBuyOrder);
        assertApprovedAndUnpaid(tenantAppBuyOrder);
    }

    /**
     * 组装 PayService 所需参数；allPrice 为元，payPrice 需转为分
     */
    private Map<String, Object> buildTenantOrderPayData(TenantAppBuyOrder tenantAppBuyOrder) {
        Map<String, Object> payData = new HashMap<>();
        payData.put("oddNumber", tenantAppBuyOrder.getOddNumber());
        payData.put("payPrice", yuanToFen(tenantAppBuyOrder.getAllPrice()));
        if (isTokenBillOrder(tenantAppBuyOrder)) {
            payData.put("subject", "Token月结账单");
            payData.put("body", "Token月结账单-" + tenantAppBuyOrder.getOddNumber());
        } else {
            payData.put("subject", "租户扩容购买");
            payData.put("body", "租户席位/应用购买-" + tenantAppBuyOrder.getOddNumber());
        }
        return payData;
    }

    private String yuanToFen(String yuanPrice) {
        return CalculationUtil.multiply(yuanPrice, CommonNumConstants.ONE_HUNDRED.toString());
    }

    private TenantAppBuyOrder queryByOddNumber(String oddNumber) {
        QueryWrapper<TenantAppBuyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOddNumber), oddNumber);
        TenantAppBuyOrder tenantAppBuyOrder = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenantAppBuyOrder)) {
            throw new CustomException("订单不存在");
        }
        return selectById(tenantAppBuyOrder.getId());
    }

    /**
     * 支付成功后的统一落单：交付权益 + 更新支付状态。
     * 幂等：已支付订单直接返回，防止重复回调重复加席位。
     */
    private void completeOrderPaySuccess(TenantAppBuyOrder tenantAppBuyOrder, Map<String, Object> payChannel,
                                         String channelCode, String successTime, String payRemark) {
        if (TenantAppBuyOrderPayState.PAID.getKey().equals(tenantAppBuyOrder.getPayState())) {
            return;
        }
        deliverOrderBenefits(tenantAppBuyOrder);
        UpdateWrapper<TenantAppBuyOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, tenantAppBuyOrder.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayState), TenantAppBuyOrderPayState.PAID.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayType), channelCode);
        if (payChannel != null && payChannel.get("feeRate") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getChannelFeeRate), payChannel.get("feeRate").toString());
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayTime),
            StrUtil.isNotBlank(successTime) ? successTime : DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayRemark), payRemark);
        update(updateWrapper);
        refreshCache(tenantAppBuyOrder.getId());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void payTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String payRemark = params.get("payRemark").toString();
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertApprovedAndUnpaid(tenantAppBuyOrder);
        deliverOrderBenefits(tenantAppBuyOrder);
        updatePayState(id, TenantAppBuyOrderPayState.PAID.getKey(), payRemark);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelPayTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String payRemark = params.get("payRemark").toString();
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertApprovedAndUnpaid(tenantAppBuyOrder);
        updatePayState(id, TenantAppBuyOrderPayState.PAY_CANCELLED.getKey(), payRemark);
    }

    private void assertApprovedAndUnpaid(TenantAppBuyOrder tenantAppBuyOrder) {
        if (ObjectUtil.isEmpty(tenantAppBuyOrder) || StrUtil.isEmpty(tenantAppBuyOrder.getId())) {
            throw new CustomException("订单不存在");
        }
        if (!isApprovedFlowableEntity(tenantAppBuyOrder)) {
            throw new CustomException("当前订单未审批通过，无法操作");
        }
        if (tenantAppBuyOrder.getPayState() == null) {
            throw new CustomException("该订单支付状态异常，请联系管理员处理");
        }
        if (!TenantAppBuyOrderPayState.UNPAID.getKey().equals(tenantAppBuyOrder.getPayState())) {
            throw new CustomException("当前订单不是待支付状态");
        }
    }

    private void deliverOrderBenefits(TenantAppBuyOrder tenantAppBuyOrder) {
        if (isTokenBillOrder(tenantAppBuyOrder)) {
            tenantTokenBillService.markPaidByPayOrderId(tenantAppBuyOrder.getId());
            return;
        }
        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderNumList())) {
            tenantAppBuyOrder.getTenantAppBuyOrderNumList().forEach(tenantAppBuyOrderNum -> {
                tenantService.editTenantAccountNumber(tenantAppBuyOrder.getBuyTenantId(), tenantAppBuyOrderNum.getAccountNum());
            });
        }
        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderYearList())) {
            tenantAppBuyOrder.getTenantAppBuyOrderYearList().forEach(tenantAppBuyOrderYear -> {
                tenantAppLinkService.saveTenantAppLink(tenantAppBuyOrder.getBuyTenantId(), tenantAppBuyOrderYear.getAppId(), tenantAppBuyOrderYear.getAccountYear());
            });
        }
        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderTokenList())) {
            tenantAppBuyOrder.getTenantAppBuyOrderTokenList().forEach(tokenItem -> {
                long qty = tokenItem.getTokenQty() == null ? 0L : tokenItem.getTokenQty();
                tenantTokenAccountService.creditTokens(tenantAppBuyOrder.getBuyTenantId(), qty);
            });
        }
        // 支付成功后才标记租户为「已购买」
        if (StrUtil.isNotEmpty(tenantAppBuyOrder.getBuyTenantId())) {
            tenantService.markHasPassedAppBuyOrder(tenantAppBuyOrder.getBuyTenantId());
        }
    }

    @Override
    @IgnoreTenant
    public long countActiveBuyOrdersByBuyTenantId(String buyTenantId) {
        QueryWrapper<TenantAppBuyOrder> orderQw = new QueryWrapper<>();
        orderQw.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getBuyTenantId), buyTenantId);
        return list(orderQw).stream().filter(order -> !isInactiveFlowableEntity(order)).count();
    }

    @Override
    @IgnoreTenant
    public void queryTenantOrderStatistics(InputObject inputObject, OutputObject outputObject) {
        String tenantId = inputObject.getParams().get("tenantId").toString();
        // 1. 查询租户的订单信息--包括所有状态
        QueryWrapper<TenantAppBuyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getBuyTenantId), tenantId);
        List<TenantAppBuyOrder> tenantAppBuyOrderList = list(queryWrapper);
        // 2. 审批通过订单金额与数量（含待支付，非付费实收口径）
        BigDecimal totalPrice = tenantAppBuyOrderList.stream()
            .filter(this::isApprovedFlowableEntity)
            .map(bean -> new BigDecimal(bean.getAllPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 3. 审批通过订单数量
        long totalCount = tenantAppBuyOrderList.stream()
            .filter(this::isApprovedFlowableEntity)
            .count();
        // 4. 已支付订单金额与数量（付费统计口径）
        BigDecimal paidTotalPrice = tenantAppBuyOrderList.stream()
            .filter(this::isPaidBuyOrder)
            .map(bean -> new BigDecimal(bean.getAllPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidCount = tenantAppBuyOrderList.stream()
            .filter(this::isPaidBuyOrder)
            .count();
        // 5. 查询租户购买的应用数量
        List<TenantAppLink> tenantAppLinks = tenantAppLinkService.selectByTenantId(tenantId);
        int appCount = tenantAppLinks.size();
        // 6. 封装数据
        Map<String, Object> data = new HashMap<>();
        data.put("totalPrice", totalPrice);
        data.put("totalCount", totalCount);
        data.put("paidTotalPrice", paidTotalPrice);
        data.put("paidCount", paidCount);
        data.put("appCount", appCount);
        outputObject.setBean(data);
        outputObject.setBeans(tenantAppBuyOrderList);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 是否已支付
     */
    private boolean isPaidBuyOrder(TenantAppBuyOrder order) {
        return order != null
            && TenantAppBuyOrderPayState.PAID.getKey().equals(order.getPayState());
    }

    /**
     * 当前租户的全部购买订单（后台添加、租户自购等所有来源）。
     */
    @Override
    @IgnoreTenant
    public void queryCurrentTenantAppBuyOrderList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("未获取到当前租户信息");
        }

        QueryWrapper<TenantAppBuyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getBuyTenantId), tenantId);
        String payState = commonPageInfo.getCustomParamsMapStr("payState");
        if (StrUtil.isNotBlank(payState)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayState),
                Integer.parseInt(payState.trim()));
        }
        if (StrUtil.isNotBlank(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOddNumber), commonPageInfo.getKeyword().trim());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getCreateTime));

        Page<?> page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<TenantAppBuyOrder> orderList = list(queryWrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(orderList), null);
        if (CollectionUtil.isNotEmpty(beans)) {
            setDynamicDataForBeans(beans);
            for (Map<String, Object> bean : beans) {
                setDataFlowabledMation(bean);
            }
            iAuthUserService.setNameForMap(beans, "createId", "createName");
            iAuthUserService.setNameForMap(beans, "lastUpdateId", "lastUpdateName");
        }
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 当前租户订单详情：校验归属后返回完整单据（含席位/应用明细、生命周期状态）。
     */
    @Override
    @IgnoreTenant
    public void queryCurrentTenantAppBuyOrderById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("未获取到当前租户信息");
        }
        TenantAppBuyOrder order = selectById(id);
        assertTenantOrderOwnership(order);
        Map<String, Object> bean = JSONUtil.toBean(JSONUtil.toJsonStr(order), null);
        setDynamicDataForBeans(Collections.singletonList(bean));
        setDataFlowabledMation(bean);
        iAuthUserService.setNameForMap(Collections.singletonList(bean), "createId", "createName");
        iAuthUserService.setNameForMap(Collections.singletonList(bean), "lastUpdateId", "lastUpdateName");
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 当前租户删除订单：仅「取消支付」状态允许删除（与后台 deleteTenantAppBuyOrderById 隔离）。
     */
    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteCurrentTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("未获取到当前租户信息");
        }
        TenantAppBuyOrder order = selectById(id);
        assertTenantOrderOwnership(order);
        if (!TenantAppBuyOrderPayState.PAY_CANCELLED.getKey().equals(order.getPayState())) {
            throw new CustomException("仅取消支付状态的订单可删除");
        }
        deleteById(id);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createTenantTokenBillPayOrder(InputObject inputObject, OutputObject outputObject) {
        String billId = inputObject.getParams().get("id").toString();
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("未获取到当前租户信息");
        }
        TenantTokenBill bill = tenantTokenBillService.selectById(billId);
        if (bill == null || StrUtil.isBlank(bill.getId())) {
            throw new CustomException("账单不存在");
        }
        if (!StrUtil.equals(tenantId, bill.getTenantId())) {
            throw new CustomException("无权结清该账单");
        }
        if (TenantTokenBillState.PAID.getKey().equals(bill.getState())) {
            throw new CustomException("该账单已结清");
        }
        if (!TenantTokenBillState.SETTLED.getKey().equals(bill.getState())
            || !tenantTokenBillService.isPayableAmount(bill)) {
            throw new CustomException("当前账单无需支付");
        }
        if (StrUtil.isNotBlank(bill.getPayOrderId())) {
            TenantAppBuyOrder existOrder = selectById(bill.getPayOrderId());
            if (existOrder != null && StrUtil.isNotBlank(existOrder.getId())
                && TenantAppBuyOrderPayState.UNPAID.getKey().equals(existOrder.getPayState())
                && isApprovedFlowableEntity(existOrder)) {
                outputObject.setBean(existOrder);
                outputObject.settotal(CommonNumConstants.NUM_ONE);
                return;
            }
        }
        TenantAppBuyOrder entity = new TenantAppBuyOrder();
        entity.setBuyTenantId(tenantId);
        entity.setOrderSource(TenantAppBuyOrderSource.TOKEN_BILL.getKey());
        entity.setFormSubType(FormSubType.DRAFT.getKey());
        entity.setAllPrice(bill.getAmount());
        entity.setRemark("Token月结账单-" + bill.getBillPeriod());
        entity.setOperTime(DateUtil.getTimeAndToString());
        String userId = inputObject.getLogParams().get("id").toString();
        TenantContext.setTenantId(TenantTypeEnum.PLATFORM.getCode());
        String orderId = createEntity(entity, userId);
        autoApprovalPass(orderId);
        tenantTokenBillService.bindPayOrderId(bill.getId(), orderId);
        outputObject.setBean(selectById(orderId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void markTenantTokenBillPaid(InputObject inputObject, OutputObject outputObject) {
        if (!StrUtil.equals(TenantContext.getTenantId(), TenantTypeEnum.PLATFORM.getCode())) {
            throw new CustomException("非平台租户不能线下结清");
        }
        String billId = inputObject.getParams().get("id").toString();
        TenantTokenBill bill = tenantTokenBillService.selectById(billId);
        if (bill == null || StrUtil.isBlank(bill.getId())) {
            throw new CustomException("账单不存在");
        }
        if (TenantTokenBillState.PAID.getKey().equals(bill.getState())) {
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        if (!TenantTokenBillState.SETTLED.getKey().equals(bill.getState())) {
            throw new CustomException("当前账单状态不可结清");
        }
        if (StrUtil.isNotBlank(bill.getPayOrderId())) {
            TenantAppBuyOrder existOrder = selectById(bill.getPayOrderId());
            if (existOrder != null && StrUtil.isNotBlank(existOrder.getId())
                && TenantAppBuyOrderPayState.UNPAID.getKey().equals(existOrder.getPayState())) {
                updatePayState(existOrder.getId(), TenantAppBuyOrderPayState.PAY_CANCELLED.getKey(), "账单已线下结清");
            }
        }
        tenantTokenBillService.markPaid(bill.getId(), bill.getPayOrderId());
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private boolean isTokenBillOrder(TenantAppBuyOrder order) {
        return order != null && TenantAppBuyOrderSource.TOKEN_BILL.getKey().equals(order.getOrderSource());
    }

}
