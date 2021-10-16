/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.app.wechat.util;

public class WxchatUtil {
	
	//微信用户在redis中的存储key
	private static final String WECHAT_USER_OPENID = "wechat_user_openid_";
	public static String getWechatUserOpenIdMation(String openId){
		return WECHAT_USER_OPENID + openId;
	}
	
	
}
