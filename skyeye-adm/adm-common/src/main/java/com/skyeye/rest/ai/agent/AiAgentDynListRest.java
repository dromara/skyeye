/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.ai.agent;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.Map;

/**
 * Agent 业务数据「万能」Feign：biz_list / biz_board 都走这里。
 * <p>
 * 与 {@code IDataDynamicRest} 同套路——<b>不能</b>指望改 {@code @FeignClient} 的 name 指向不同微服务：
 * <ul>
 *   <li>{@code url = "EMPTY"}：仅占位，真正基址由方法首参 {@link URI} 覆盖</li>
 *   <li>URI 由调用方通过 appId+serviceClassName → ServiceBean → Discovery 得到</li>
 *   <li>path 只用短 apiId，禁止配置里写完整 URL 或带斜杠路径</li>
 * </ul>
 */
@FeignClient(name = "AiAgentDynListRest${spring.application.name}",
    configuration = ClientConfiguration.class, url = "EMPTY")
public interface AiAgentDynListRest {

    /**
     * POST {uri}/{apiId}，body 为 params。
     *
     * @param uri    目标服务根地址（注册中心实例 URI）
     * @param apiId  接口短 id，如 activitimode008、queryDepotOutList、erppage005
     * @param params 请求体
     * @return 下游原始 JSON 字符串，由 ExecuteFeignClient 解析为 ResultEntity
     */
    @PostMapping("/{apiId}")
    String invoke(URI uri, @PathVariable("apiId") String apiId, @RequestBody Map<String, Object> params);
}
