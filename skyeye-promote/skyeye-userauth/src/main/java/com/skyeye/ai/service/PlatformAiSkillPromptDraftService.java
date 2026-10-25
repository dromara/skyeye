/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * AI 技能「技能提示词」辅助生成（不落库，填入表单后人工确认再保存）。
 */
public interface PlatformAiSkillPromptDraftService {

    void generate(InputObject inputObject, OutputObject outputObject);
}
