/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.constans;

/**
 * @ClassName: PlatformBaseSettingConst
 * @Description: 平台基础信息设置项 key 常量
 * <p>
 * 常量值为 settingData 中各分组下的二级 key，与 {@link com.skyeye.tenant.classenum.PlatformBaseSettingGroup} 配合使用。
 */
public class PlatformBaseSettingConst {

    private PlatformBaseSettingConst() {
    }

    /**
     * 租户计费分组 - 成员席位单价（元/席位）
     */
    public static final String KEY_ACCOUNT_UNIT_PRICE = "accountUnitPrice";

    /**
     * 按组织类型区分的席位规则，结构：{组织类型key: {initAccountNum, minBuyAccountNum}}
     * 组织类型 key 见 {@link com.skyeye.tenant.classenum.TenantOrgType}
     */
    public static final String KEY_ORG_TYPE_CONFIG = "orgTypeConfig";

    /**
     * 组织类型配置 - 初始化席位数（新建租户时赋予）
     */
    public static final String KEY_INIT_ACCOUNT_NUM = "initAccountNum";

    /**
     * 组织类型配置 - 每次购买席位的最低数量
     */
    public static final String KEY_MIN_BUY_ACCOUNT_NUM = "minBuyAccountNum";

    /**
     * 是否允许账号自助创建组织（0-否，1-是）
     */
    public static final String KEY_ALLOW_USER_CREATE_ORG = "allowUserCreateOrg";

    /**
     * 单个账号最多可自助创建的个人组织数量（>= 1）
     */
    public static final String KEY_MAX_PERSONAL_ORG_PER_USER = "maxPersonalOrgPerUser";

    /**
     * 单个账号最多可自助创建的企业组织数量（>= 1）
     */
    public static final String KEY_MAX_ENTERPRISE_ORG_PER_USER = "maxEnterpriseOrgPerUser";

    /**
     * AI 角色分组 - 研发/需求侧绑定的 AI 角色 id
     */
    public static final String KEY_AI_ROLE_ID = "roleId";

    /**
     * AI 角色分组 - 办公OA AI 助手绑定的 AI 角色 id
     */
    public static final String KEY_OA_AI_ROLE_ID = "oaRoleId";

    /**
     * Token 计费分组 - 1 元兑换的 Token 数量
     */
    public static final String KEY_TOKENS_PER_YUAN = "tokensPerYuan";

    /**
     * Token 计费分组 - 预付最低购买金额（元）
     */
    public static final String KEY_MIN_BUY_TOKEN_AMOUNT = "minBuyTokenAmount";

}
