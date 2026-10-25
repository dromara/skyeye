/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 商城商品文案 AI 草稿（不落库，人工确认后写入表单）。
 */
public interface PlatformShopGoodsCopyAiDraftService {

    void generate(InputObject inputObject, OutputObject outputObject);

    void parseAnswer(InputObject inputObject, OutputObject outputObject);
}
