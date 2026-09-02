/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PlatformBaseSettingGroup
 * @Description: 平台基础信息设置分组枚举
 * <p>
 * key 对应 settingData 的一级 key，value 为前端 Tab 展示名称。
 * 新增分组时在此扩展，并同步前端 Tab 与校验逻辑。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PlatformBaseSettingGroup {

    /**
     * 租户计费相关：席位单价等
     */
    TENANT("tenant", "租户计费"),

    /**
     * AI 角色绑定：研发需求/Bug 与办公OA 助手各自绑定的平台级 AI 角色
     */
    AI("ai", "AI角色"),

    /**
     * Token 计费标准：兑换比例与最低购买金额
     */
    TOKEN("token", "Token计费");

    /**
     * 分组标识，存入 settingData 的 key
     */
    private String key;

    /**
     * 分组展示名称
     */
    private String value;

}
