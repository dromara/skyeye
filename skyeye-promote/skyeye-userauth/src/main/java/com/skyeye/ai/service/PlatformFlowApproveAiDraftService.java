/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 审批详情 AI 辅助（只读建议，不代为同意/驳回）。
 */
public interface PlatformFlowApproveAiDraftService {

    /**
     * 启动流式生成：摘要 / 风险点 / 建议意见。
     */
    void generate(InputObject inputObject, OutputObject outputObject);

    /**
     * 解析完整回答为结构化 bean。
     */
    void parseAnswer(InputObject inputObject, OutputObject outputObject);
}
