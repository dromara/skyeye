/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.pay.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.rest.pay.rest.IPayRest;
import com.skyeye.rest.pay.service.IPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: IPayServiceImpl
 * @Description: 支付接口实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/21 9:36
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IPayServiceImpl extends IServiceImpl implements IPayService {

    @Autowired
    private IPayRest iPayRest;

    @Override
    public ResultEntity payment(Map<String, Object> data, String channelCode, String returnUrl, String channelExtras,
                                String notifyUrl, String appKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("data", JSONUtil.toJsonStr(data));
        params.put("channelCode", channelCode);
        params.put("appKey", appKey);
        params.put("returnUrl", returnUrl);
        params.put("channelExtras", channelExtras);
        params.put("notifyUrl", notifyUrl);
        return ExecuteFeignClient.get(() -> iPayRest.payment(params));
    }
}
