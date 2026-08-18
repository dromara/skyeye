/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.web;

import com.skyeye.skill.exception.CustomException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName: SkillExceptionHandler
 * @Description: Skills 异常处理
 */
@RestControllerAdvice
public class SkillExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public SkillResult handleCustom(CustomException e) {
        return SkillResult.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public SkillResult handleOther(Exception e) {
        return SkillResult.fail("Skills异常: " + e.getMessage());
    }
}
