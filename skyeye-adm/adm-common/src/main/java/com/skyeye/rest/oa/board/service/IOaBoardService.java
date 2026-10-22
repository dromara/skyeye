/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.oa.board.service;

import java.util.Map;

/**
 * OA 待办看板快照（审批待办 / 我发起 / 日程待办）。
 */
public interface IOaBoardService {

    /**
     * @return key → 笔数：pending / initiated / schedule
     */
    Map<String, Object> queryTodoBoardCounts();
}
