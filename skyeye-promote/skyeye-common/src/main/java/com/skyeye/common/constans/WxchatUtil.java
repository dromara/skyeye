/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

public class WxchatUtil {

    // 微信用户在redis中的存储key（按 appId + openId 区分公众号/小程序）
    private static final String WECHAT_USER_OPENID = "wechat_user_openid_";

    public static String getWechatUserOpenIdMation(String openId) {
        return WECHAT_USER_OPENID + openId;
    }

    public static String getWechatUserOpenIdMation(String appId, String openId) {
        if (appId == null || appId.isEmpty()) {
            return getWechatUserOpenIdMation(openId);
        }
        return WECHAT_USER_OPENID + appId + "_" + openId;
    }

}
