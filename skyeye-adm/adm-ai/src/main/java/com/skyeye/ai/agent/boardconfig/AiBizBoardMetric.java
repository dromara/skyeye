/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.boardconfig;

import lombok.Data;

/**
 * {@code mode=metrics} 时的单项指标配置。
 * <p>
 * 执行时：用路由字段 + {@link #apiId} 远程调用一次，
 * 再按 {@link #valueFrom} 从 {@code ResultEntity} 取出数字，写入 counts[{@link #code}]。
 * <p>
 * 典型场景：OA 看板三类待办各自打一个列表接口，只取 {@code total}，limit=1 即可。
 */
@Data
public class AiBizBoardMetric {

    /**
     * 指标编码，写入返回的 counts / labels 的 key。
     * 例：pending、initiated、schedule
     */
    private String code;

    /**
     * 中文名，写入 labels[code]。例：审批待办
     */
    private String label;

    /**
     * 应用 id，可空。与 ServiceBean.appId 一致。
     */
    private String appId = "";

    /**
     * 可选。直接按 Nacos 服务名路由（如 skyeye-flowable），跳过 ServiceBean。
     */
    private String springApplicationName;

    /**
     * 业务 Service 全限定名。无 {@link #springApplicationName} 时必填。
     * <p>
     * 须为已进 ServiceBean 的 {@code SkyeyeBusinessService}。
     * 若接口类未注册：可配 springApplicationName，或改用<strong>同微服务</strong>已注册类仅解析地址（apiId 仍指向真实接口）。
     */
    private String serviceClassName;

    /**
     * 接口短 id（必填）。例：activitimode008、myagency001
     */
    private String apiId;

    /**
     * 从 ResultEntity 取哪个值：
     * <ul>
     *   <li>{@code total}（默认）：分页总数</li>
     *   <li>{@code bean.xxx}：取 bean 里字段 xxx</li>
     * </ul>
     */
    private String valueFrom = "total";
}
