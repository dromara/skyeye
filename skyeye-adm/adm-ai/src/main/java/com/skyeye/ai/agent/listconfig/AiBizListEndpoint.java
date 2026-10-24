/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.listconfig;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * biz_list 一条列表接入配置（对应 JSON endpoints[] 的一项）。
 * <p>
 * <b>怎么命中</b>：工具入参 domain+type（+有 storeId 时 variant=store）→ Registry 索引本对象。
 * <b>怎么调用</b>：{@link #springApplicationName} 或 {@link #appId}+{@link #serviceClassName}，再加 {@link #apiId} 拉分页；
 * {@link #domain} 给模型选业务，不是微服务名。
 * <b>怎么展示</b>：{@link #fields} 决定行数据如何瘦成卡片 title/subtitle。
 */
@Data
public class AiBizListEndpoint {

    /**
     * 给模型看的业务域：oa / erp / mall …（工具入参 domain，不是 Feign 服务名）
     */
    private String domain;

    /**
     * 列表类型：pending / depotOut / waitDeliver …
     */
    private String type;

    /**
     * 别名，如 waitDepotOut → depotOut、E01 → depotOut
     */
    private List<String> aliases = new ArrayList<>();

    /**
     * 列表卡片标题
     */
    private String listTitle;

    /**
     * 返回给模型的 scope 说明
     */
    private String scope;

    /**
     * 应用 id（可空字符串）。与业务对象 ServiceBean 一致，用于解析所在微服务。
     */
    private String appId = "";

    /**
     * 可选。Nacos 服务名前缀（如 skyeye-erp），有值则跳过 ServiceBean。
     */
    private String springApplicationName;

    /**
     * 业务 Service 全限定名。无 springApplicationName 时必填。
     * 例：com.skyeye.depot.service.impl.DepotOutServiceImpl（须已注册到 ServiceBean）
     */
    private String serviceClassName;

    /**
     * 接口短 id，如 activitimode008、queryDepotOutList
     */
    private String apiId;

    /**
     * 变体：default | store（商城有 storeId 时走门店列表）
     */
    private String variant = "default";

    /**
     * 是否必须传 storeId（variant=store 时）
     */
    private boolean requireStoreId;

    /**
     * 固定入参，如 type=AllWait
     */
    private Map<String, Object> fixedParams = new LinkedHashMap<>();

    /**
     * 将工具 type 映射到接口 type 参数。例：waitPay → "1"
     */
    private Map<String, String> typeParamMap = new LinkedHashMap<>();

    /**
     * 写入请求的 type 参数名，默认 type
     */
    private String typeParamKey = "type";

    /**
     * 工具入参 → 请求字段，如 storeId → objectId
     */
    private Map<String, String> argBindings = new LinkedHashMap<>();

    /**
     * 行字段 → 卡片 title/subtitle
     */
    private AiBizListFieldMapping fields = new AiBizListFieldMapping();
}
