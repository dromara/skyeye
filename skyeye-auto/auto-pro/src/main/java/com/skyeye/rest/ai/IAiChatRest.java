/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.ai;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @ClassName: IAiChatRest
 * @Description: 调用 adm-ai 同步对话
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-adm}", configuration = {ClientConfiguration.class, AiChatFeignConfiguration.class})
public interface IAiChatRest {

    /**
     * 流式调用大模型，立即返回 chatId
     * <p>
     * content: 用户消息（必填）
     * roleId: 平台绑定的 AI 角色 id，优先按角色取唯一启用配置
     * apiKeyId: AI配置id，roleId 为空时使用
     * bizType: 业务类型
     * saveChat: 是否记录聊天，1是 0否，默认是。需求/Bug草稿传 0
     * images: 截图地址列表，看图生成时传入
     *
     * @param params 请求参数
     * @return 平台统一 JSON 字符串
     */
    @PostMapping("/syncChatCompletion")
    String syncChatCompletion(Map<String, Object> params);
}
