/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.key.service;

import com.skyeye.key.entity.AiApiKey;
import com.skyeye.base.business.service.SkyeyeBusinessService;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai配置服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AiApiKeyService extends SkyeyeBusinessService<AiApiKey> {

    /**
     * 获取一条已启用的 AI 配置；指定 id 时校验必须启用
     */
    AiApiKey selectEnabledKey(String apiKeyId);

    /**
     * 按角色获取唯一一条已启用的 AI 配置
     */
    AiApiKey selectEnabledKeyByRoleId(String roleId);

    /**
     * 按知识库关联角色，获取一条已启用的 AI 配置（多表 join，需手动带表别名过滤租户）
     */
    AiApiKey selectEnabledKeyByKnowledgeId(String knowledgeId);
}
