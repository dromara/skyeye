package com.skyeye.knowledge.web;

import com.skyeye.knowledge.exception.CustomException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class KnowledgeExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public KnowledgeResult handleCustom(CustomException e) {
        return KnowledgeResult.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public KnowledgeResult handleOther(Exception e) {
        Throwable cause = unwrap(e);
        return KnowledgeResult.fail("知识库异常: " + (cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage()));
    }

    private Throwable unwrap(Throwable e) {
        Throwable current = e;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
