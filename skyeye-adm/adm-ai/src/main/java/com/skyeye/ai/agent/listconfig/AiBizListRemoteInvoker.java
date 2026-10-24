/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.listconfig;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.ai.agent.AiAgentDynListRest;
import com.skyeye.sdk.data.service.IDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

/**
 * Agent 业务数据动态远程调用（biz_list / biz_board 共用）。
 * <p>
 * <b>为什么不用多个 FeignClient</b>：Feign 的 {@code value/name} 在启动时定死，运行时改不了。
 * 做法与 {@code IDataDynamicRest} / {@code IDataServiceImpl} 相同：
 * <ol>
 *   <li>声明一个 {@link AiAgentDynListRest}，{@code url="EMPTY"} 占位</li>
 *   <li>解析目标 URI（见下方两种路由）</li>
 *   <li>调用时把 URI 作为方法第一个参数，覆盖基址，再 POST {@code /{apiId}}</li>
 * </ol>
 * <p>
 * <b>路由（二选一）</b>：
 * <ul>
 *   <li>{@code springApplicationName}：直接 Discovery（适合未进 ServiceBean 的统计类 Service，如 ErpPageServiceImpl）</li>
 *   <li>{@code appId + serviceClassName}：查 ServiceBean（须是 {@code SkyeyeBusinessService} 实现且已注册）</li>
 * </ul>
 */
@Slf4j
@Component
public class AiBizListRemoteInvoker {

    @Autowired
    private AiAgentDynListRest dynListRest;

    @Autowired
    private IDataService iDataService;

    /** 与 bootstrap 中 skyeye-xxx-${spring.profiles.active} 对齐，配置可只写 skyeye-erp */
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * @param appId                 可空；走 ServiceBean 时与注册 appId 一致
     * @param serviceClassName      走 ServiceBean 时必填；有 springApplicationName 时可空
     * @param springApplicationName 可选；有值则跳过 ServiceBean，直接按服务名 Discovery
     * @param apiId                 接口短 id（必填）
     * @param params                请求体（勿传 null）
     */
    public ResultEntity invoke(String appId, String serviceClassName, String springApplicationName,
                               String apiId, Map<String, Object> params) {
        if (StrUtil.isBlank(apiId)) {
            throw new CustomException("配置缺少 apiId");
        }
        String api = apiId.trim();
        if (api.contains("/") || api.contains("..") || api.contains("\\")) {
            throw new CustomException("非法 apiId");
        }
        String app = StrUtil.nullToEmpty(appId);
        String clazz = StrUtil.trim(serviceClassName);
        String discoveryName = StrUtil.trim(springApplicationName);
        try {
            URI uri = resolveUri(app, clazz, discoveryName);
            return ExecuteFeignClient.get(() -> dynListRest.invoke(uri, api, params));
        } catch (CustomException e) {
            log.error("biz remote invoke failed appId={}, class={}, springApp={}, apiId={}, err={}",
                app, clazz, discoveryName, api, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("biz remote invoke failed appId={}, class={}, springApp={}, apiId={}",
                app, clazz, discoveryName, api, e);
            throw new CustomException(StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()));
        }
    }

    private URI resolveUri(String appId, String serviceClassName, String springApplicationName) {
        if (StrUtil.isNotBlank(springApplicationName)) {
            // 配置可写 skyeye-erp，运行时拼成 skyeye-erp-dev
            return iDataService.getUriByServiceClassName(withActiveProfile(springApplicationName));
        }
        if (StrUtil.isBlank(serviceClassName)) {
            throw new CustomException("配置缺少 serviceClassName（或改配 springApplicationName 直接按服务名路由）");
        }
        return iDataService.getUriByServiceClassName(appId, serviceClassName);
    }

    private String withActiveProfile(String springApplicationName) {
        if (StrUtil.isBlank(activeProfile)) {
            return springApplicationName;
        }
        String suffix = "-" + activeProfile.trim();
        if (springApplicationName.endsWith(suffix)) {
            return springApplicationName;
        }
        return springApplicationName + suffix;
    }
}
