package com.skyeye.chat.service;


import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.chat.entity.Chat;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ChatService
 * @Description: 聊天记录接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/10/5 17:24
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ChatService extends SkyeyeBusinessService<Chat> {

    void sendChatMessage(InputObject inputObject, OutputObject outputObject);

        /**
     * 流式调用大模型，立即返回 chatId，正文通过 WebSocket 推送
     */
    void syncChatCompletion(InputObject inputObject, OutputObject outputObject);

    void queryPageMessageList(InputObject inputObject, OutputObject outputObject);

    void deleteAllByApiKeyId(InputObject inputObject, OutputObject outputObject);
}
