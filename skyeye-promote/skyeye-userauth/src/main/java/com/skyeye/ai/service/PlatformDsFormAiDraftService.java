/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 表单布局 AI 辅助服务接口层。
 */
public interface PlatformDsFormAiDraftService {

    void generate(InputObject inputObject, OutputObject outputObject);

    void parseAnswer(InputObject inputObject, OutputObject outputObject);
}
