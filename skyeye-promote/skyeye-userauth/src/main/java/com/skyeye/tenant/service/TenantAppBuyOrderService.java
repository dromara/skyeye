/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantAppBuyOrder;

/**
 * @ClassName: TenantAppBuyOrderService
 * @Description: 订单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/30 16:25
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantAppBuyOrderService extends SkyeyeBusinessService<TenantAppBuyOrder> {

    void queryTenantOrderStatistics(InputObject inputObject, OutputObject outputObject);

    /**
     * 统计指定租户下「非草稿、非作废」的应用购买订单数量，用于删除租户等业务校验。
     *
     * @param buyTenantId 购买方租户 id
     * @return 订单条数
     */
    long countActiveBuyOrdersByBuyTenantId(String buyTenantId);

    /**
     * 审批通过的订单执行支付（支付成功后交付席位/应用权益）
     */
    void payTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 审批通过且待支付的订单取消支付
     */
    void cancelPayTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 租户自购：提交购物车并生成已审核通过的待支付订单
     */
    void submitTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 租户自购在线支付：调用统一 PayService，同步成功当场交付，异步等待渠道回调
     */
    void payTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 租户自购取消支付：仅当前租户、租户自购来源、审批通过且待支付
     */
    void cancelTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 租户购买支付成功业务回调（PayApp.orderNotifyUrl 指向本接口，由 PayNotify 转发）
     */
    void notifyTenantAppBuyOrderPaySuccess(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询订单支付状态，供前端二维码支付轮询
     */
    void queryTenantAppBuyOrderPayState(InputObject inputObject, OutputObject outputObject);

    /**
     * 当前租户的全部购买订单（不限订单来源），独立分页查询
     */
    void queryCurrentTenantAppBuyOrderList(InputObject inputObject, OutputObject outputObject);

}
