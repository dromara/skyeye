/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent;

import com.skyeye.key.entity.AiApiKey;
import com.skyeye.role.entity.Role;
import lombok.Builder;
import lombok.Data;

/**
 * 工具执行上下文：沿用当前登录用户与角色绑定的知识库/密钥。
 */
@Data
@Builder
public class AiAgentToolContext {

    private String userId;

    private String tenantId;

    private boolean tenantEnable;

    private String bizType;

    private String chatId;

    private Role role;

    private AiApiKey apiKey;

    /** 用户原问题（检索用，勿用整段 system prompt） */
    private String userQuery;
}
