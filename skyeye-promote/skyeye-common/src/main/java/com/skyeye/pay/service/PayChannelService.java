/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pay.core.PayClient;
import com.skyeye.pay.entity.PayChannel;

/**
 * @ClassName: PayChannelService
 * @Description: 支付渠道服务接口层
 */
public interface PayChannelService extends SkyeyeBusinessService<PayChannel> {

    PayClient getPayClient(String id);

    PayChannel getPayChannelByCode(String codeNum);

    /** 按 PayApp.appKey + 渠道编码定位渠道，避免多应用同编码串台 */
    PayChannel getPayChannelByCode(String appKey, String codeNum);

    /** 收银台查询可用支付渠道：appKey 必填，clientType 选填 */
    void queryEnabledPayChannelList(InputObject inputObject, OutputObject outputObject);

}
