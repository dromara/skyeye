/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.oa.list.service;

import java.util.Map;

/**
 * OA 待办列表（审批待办 / 我发起 / 日程），分页只读。
 */
public interface IOaListService {

    /**
     * @param type pending | initiated | schedule
     * @param page 从 1 开始
     * @param limit 上限由调用方裁剪
     * @return total + items（已精简字段）
     */
    Map<String, Object> queryTodoList(String type, int page, int limit);
}
