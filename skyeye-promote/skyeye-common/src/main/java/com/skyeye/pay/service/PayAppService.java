/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.pay.entity.PayApp;

/**
 * 支付应用服务：解析渠道回调地址与业务回调地址。
 */
public interface PayAppService extends SkyeyeBusinessService<PayApp> {

    /** 获取当前唯一启用的 PayApp（切换业务场景时需切换启用项） */
    PayApp getEnabledPayApp();

    /** 按 appKey 获取已启用的 PayApp（租户购买 tenant-buy、商城 mall-order 等） */
    PayApp getEnabledPayAppByAppKey(String appKey);

    /** 构建第三方渠道回调 pay 模块的完整 URL：{channelNotifyUrl}/{channelId} */
    String buildChannelOrderNotifyUrl(PayApp payApp, String channelId);

    /** 构建第三方渠道退款回调 URL：将 channelNotifyUrl 中 /order 替换为 /refund 后拼接 channelId */
    String buildChannelRefundNotifyUrl(PayApp payApp, String channelId);

    /** 获取 pay 模块转发支付成功通知时使用的业务 URL */
    String getBusinessOrderNotifyUrl(PayApp payApp);

    /** 获取 pay 模块转发退款成功通知时使用的业务 URL */
    String getBusinessRefundNotifyUrl(PayApp payApp);

}
