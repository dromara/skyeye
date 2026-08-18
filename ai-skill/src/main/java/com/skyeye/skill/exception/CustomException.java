/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.exception;

/**
 * @ClassName: CustomException
 * @Description: 业务异常（对齐 Skyeye CustomException）
 */
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
