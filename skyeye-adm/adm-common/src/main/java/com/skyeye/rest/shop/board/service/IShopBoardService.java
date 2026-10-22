/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.board.service;

import java.util.Map;

/**
 * 商城看板快照：买家订单待办；可选门店经营角标。
 */
public interface IShopBoardService {

    /**
     * @param storeId 可选。传入则额外合并门店经营统计（objectId）
     * @return key → 笔数/金额字段
     */
    Map<String, Object> queryMallBoardCounts(String storeId);
}
