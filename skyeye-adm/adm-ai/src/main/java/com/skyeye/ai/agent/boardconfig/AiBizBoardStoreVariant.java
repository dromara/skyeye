/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.boardconfig;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 看板「门店变体」：主接口（买家）之外，工具传了 {@code storeId} 时再打一次门店接口，合并进 counts。
 * <p>
 * 挂在 {@link AiBizBoardDef#getStoreVariant()} 上，仅 {@code mode=beanFields} 时由 Executor 使用。
 * <p>
 * 例：商城买家 {@code queryMyOrderStat} 之后，再调 {@code queryStoreOrderStat}，
 * 把 waitPay 映射成 store_waitPay，避免与买家字段重名。
 */
@Data
public class AiBizBoardStoreVariant {

    /**
     * 门店侧接口短 id。例：queryStoreOrderStat
     */
    private String apiId;

    /**
     * 工具入参 → 请求体字段。目前识别 key={@code storeId}，value 为接口参数名（如 objectId）。
     */
    private Map<String, String> argBindings = new LinkedHashMap<>();

    /**
     * 门店接口 bean 字段 → counts 键（建议带 store_ 前缀）。
     * key=bean 字段，value=counts 的 code。
     */
    private Map<String, String> fieldMap = new LinkedHashMap<>();
}
