/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.boardconfig;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一块「业务看板」的配置定义（对应 JSON 里 {@code boards[]} 的一条）。
 * <p>
 * <b>和谁对应</b>：Agent 工具 {@code biz_board} 的入参 {@code domain} 命中本对象后，
 * 由 {@link AiBizBoardExecutor} 按 {@link #mode} 去远程取数，组装 {@code counts}/{@code labels}。
 * <p>
 * <b>三种取数模式（mode）</b>：
 * <ul>
 *   <li>{@code metrics}：多项指标，每项单独调一个接口，通常取分页结果的 {@code total}
 *       （例：OA 审批待办 / 我发起 / 日程，三个 apiId）</li>
 *   <li>{@code beanMap}：只调一次接口，整个 {@code ResultEntity.bean} 就是 code→数量 的 Map
 *       （例：ERP {@code erppage005}，bean 里直接有 E01、E03…）</li>
 *   <li>{@code beanFields}：只调一次（或再加门店一次），按 {@link #fieldMap} 从 bean 里挑字段改名写入 counts
 *       （例：商城 {@code queryMyOrderStat} 的 waitPay→counts.waitPay）</li>
 * </ul>
 * <p>
 * <b>路由</b>：优先 {@link #springApplicationName}（直接 Discovery）；否则 {@link #appId}+{@link #serviceClassName}
 * （查 ServiceBean，须为 SkyeyeBusinessService）。{@link #domain} 不是服务名。
 */
@Data
public class AiBizBoardDef {

    /**
     * 业务域，给大模型选：oa / erp / mall（shop 会归一成 mall）。
     * 对应工具入参 domain，也是 Registry 的索引键。
     */
    private String domain;

    /**
     * 返回给模型的「数据范围」说明文案，如「当前登录用户」「当前租户全量」。
     */
    private String scope;

    /**
     * 返回给模型的取值提示，如「E01=待出库，E03=待入库」。
     */
    private String hint;

    /**
     * 取数模式：metrics | beanMap | beanFields（大小写不敏感）。
     * @see AiBizBoardExecutor
     */
    private String mode;

    /**
     * 应用 id，可空字符串。与业务 ServiceBean 的 appId 一致；
     * beanMap / beanFields 模式用本字段；metrics 模式优先用各 {@link AiBizBoardMetric#getAppId()}。
     */
    private String appId = "";

    /**
     * 可选。Nacos 服务名前缀，如 skyeye-erp（运行时自动拼 -${spring.profiles.active}）。
     * 有值则跳过 ServiceBean，适合未实现 SkyeyeBusinessService、进不了注册表的统计类。
     */
    private String springApplicationName;

    /**
     * 业务 Service 全限定名。无 {@link #springApplicationName} 时必填，用于查 ServiceBean。
     * 例：com.skyeye.depot.service.impl.DepotOutServiceImpl
     */
    private String serviceClassName;

    /**
     * 接口短 id（beanMap / beanFields 必填）。
     * 例：erppage005、queryMyOrderStat（禁止含 /）
     */
    private String apiId;

    /**
     * metrics 模式：每次远程调用都带上的固定参数。
     * 例：OA 只取 total 时用 page=1, limit=1, keyword=""。
     */
    private Map<String, Object> fixedParams = new LinkedHashMap<>();

    /**
     * metrics 模式：指标列表，一项 = 一次远程调用。
     */
    private List<AiBizBoardMetric> metrics = new ArrayList<>();

    /**
     * 指标 code → 中文名。beanMap / beanFields 用；metrics 也可在 metric.label 上写。
     */
    private Map<String, String> labels = new LinkedHashMap<>();

    /**
     * beanFields 模式：接口 bean 字段名 → 返回给模型的 counts 键。
     * 例：接口返回 waitPay，映射成 counts 的 "waitPay"（也可映射成别的名字）。
     * key=bean 里的字段，value=counts 里的 code。
     */
    private Map<String, String> fieldMap = new LinkedHashMap<>();

    /**
     * 可选。商城等场景：工具传了 storeId 时，再调一次门店接口，把结果合并进 counts。
     */
    private AiBizBoardStoreVariant storeVariant;
}
