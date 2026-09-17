/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.platform.service;

/**
 * @ClassName: IPlatformBaseSettingService
 * @Description: 平台基础信息调用封装
 */
public interface IPlatformBaseSettingService {

    /**
     * 单个会员最多可开个人门店数；0 表示不限制；获取失败时返回默认值
     */
    Integer getMaxPersonalStorePerMember();

}
