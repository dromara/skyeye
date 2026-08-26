/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.knowledge;

import lombok.Builder;
import lombok.Data;

/**
 * 平台知识库调用参数（由业务 Knowledge 组装）。
 */
@Data
@Builder
public class AiKnowledgeConfig {

    /** 平台知识库 ID */
    private String knowledgeId;

    private String apiKey;

    private String secretKey;

    /** 讯飞 AppId / 通义可复用 */
    private String appId;

    /** 通义业务空间 ID */
    private String workspaceId;

    /** 通义类目 ID，可空 */
    private String categoryId;

}
