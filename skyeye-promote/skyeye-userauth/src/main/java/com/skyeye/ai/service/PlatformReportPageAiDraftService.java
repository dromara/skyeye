/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 报表大屏 AI 辅助服务接口层。
 */
public interface PlatformReportPageAiDraftService {

    /**
     * 启动流式生成，返回 chatId。
     */
    void generate(InputObject inputObject, OutputObject outputObject);

    /**
     * 解析模型完整回答，得到 reply / canvas / ops。
     */
    void parseAnswer(InputObject inputObject, OutputObject outputObject);
}
