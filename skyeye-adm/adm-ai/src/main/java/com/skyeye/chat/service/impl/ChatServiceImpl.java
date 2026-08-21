package com.skyeye.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.ai.core.qianfan.QianfanChatClient;
import com.skyeye.ai.core.tongyi.TongYiChatClient;
import com.skyeye.ai.core.xunfei.XunFeiChatClient;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chat.dao.ChatDao;
import com.skyeye.chat.entity.Chat;
import com.skyeye.chat.service.ChatService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import com.skyeye.role.service.RoleService;
import com.skyeye.websocket.AiMessageWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * @ClassName: ChatServiceImpl
 * @Description: 聊天记录接口实现层
 * @author: skyeye云系列--lqy
 * @date: 2024/10/5 17:24
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ChatServiceImpl extends SkyeyeBusinessServiceImpl<ChatDao, Chat> implements ChatService {
    @Autowired
    private AiFactory aiFactory;

    @Autowired
    private AiApiKeyService aiApiKeyService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private Executor messageStreamExecutor;

    @Autowired
    private AiMessageWebSocket aiMessageWebSocket;

    @Override
    @Transactional
    public void sendChatMessage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String content = params.get("content").toString();
        String apiKeyId = params.get("apiKeyId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Chat chat = new Chat();
        AiApiKey aiApiKey = aiApiKeyService.selectById(apiKeyId);
        String platform = aiApiKey.getPlatform();
        // 获取到具有的ai模型
        AiPlatformEnum aiModel = AiPlatformEnum.getName(platform);
        // 获取role实例
        com.skyeye.role.entity.Role role = roleService.selectById(aiApiKey.getRoleId());
        String systemPrompt = role == null ? null : role.getPrompt();
        // 创建AI实例
        chat.setMessage(content);
        chat.setPlatform(platform);
        chat.setApiKeyId(apiKeyId);
        String id = createEntity(chat, userId);
        switch (aiModel) {
            case YI_YAN:
                QianFanResponse(content, systemPrompt, userId, id, aiApiKey);
                break;
            case XUN_FEI:
                XunFeiResponse(content, systemPrompt, userId, id, aiApiKey);
                break;
            case TONG_YI:
                TongYiResponse(content, systemPrompt, userId, id, aiApiKey);
                break;
        }
        aiApiKey.setRoleMation(role);
        chat.setApiKeyMation(aiApiKey);

        outputObject.setBean(chat);
        outputObject.setreturnCode(CommonNumConstants.NUM_ZERO);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void syncChatCompletion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String content = params.get("content").toString();
        String roleId = params.get("roleId") == null ? "" : params.get("roleId").toString();
        String apiKeyId = params.get("apiKeyId") == null ? "" : params.get("apiKeyId").toString();
        String bizType = params.get("bizType") == null ? "demandDraft" : params.get("bizType").toString();
        List<String> images = readImages(params.get("images").toString());
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        AiApiKey aiApiKey = StrUtil.isNotBlank(roleId)
            ? aiApiKeyService.selectEnabledKeyByRoleId(roleId)
            : aiApiKeyService.selectEnabledKey(apiKeyId);
        com.skyeye.role.entity.Role role = aiApiKey.getRoleMation();
        if (role == null && StrUtil.isNotBlank(aiApiKey.getRoleId())) {
            role = roleService.selectById(aiApiKey.getRoleId());
        }
        String systemPrompt = role == null ? null : role.getPrompt();
        AiPlatformEnum aiModel = AiPlatformEnum.getName(aiApiKey.getPlatform());
        Chat chat = new Chat();
        chat.setMessage(content);
        chat.setPlatform(aiApiKey.getPlatform());
        chat.setApiKeyId(aiApiKey.getId());
        String id = createEntity(chat, userId);
        chat.setId(id);
        switch (aiModel) {
            case YI_YAN:
                streamYiYan(content, systemPrompt, userId, id, aiApiKey, bizType, images);
                break;
            case XUN_FEI:
                streamXunFei(content, systemPrompt, userId, id, aiApiKey, bizType, images);
                break;
            case TONG_YI:
                streamTongYi(content, systemPrompt, userId, id, aiApiKey, bizType, images);
                break;
            default:
                throw new CustomException("不支持的AI平台");
        }
        outputObject.setBean(chat);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void sendStreamChunk(String userId, String chatId, String bizType, String chunk, boolean end, Object orderBy) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", chunk);
        messageMap.put("end", end);
        messageMap.put("orderBy", orderBy);
        messageMap.put("chatId", chatId);
        messageMap.put("bizType", bizType);
        aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
    }

    /**
     * 请求线程的租户在 ThreadLocal 里，丢进线程池后会丢。
     * 提交前先拷贝，子线程和 OkHttp/WebSocket 回调里再绑回去，否则 MyBatis 租户拦截器会报「租户ID不能为空」。
     * NoClassDefFoundError 等 Error 不是 Exception，必须 catch Throwable，否则前端收不到结束包会一直转圈。
     */
    private void runStreamTask(String userId, String chatId, String bizType, Runnable task) {
        String tenantId = TenantContext.getTenantId();
        TenantEnum isolationType = TenantContext.getIsolationType();
        messageStreamExecutor.execute(() -> {
            try {
                bindTenantContext(tenantId, isolationType);
                task.run();
            } catch (Throwable t) {
                persistYiYanPartial(userId, chatId);
                sendYiYanError(userId, chatId, bizType, StrUtil.blankToDefault(t.getMessage(), t.getClass().getSimpleName()));
            } finally {
                TenantContext.clear();
            }
        });
    }

    private void bindTenantContext(String tenantId, TenantEnum isolationType) {
        if (StrUtil.isNotBlank(tenantId)) {
            TenantContext.setTenantId(tenantId);
        }
        if (isolationType != null) {
            TenantContext.setIsolationType(isolationType);
        }
    }

    private void streamTongYi(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType, List<String> images) {
        runStreamTask(userId, chatId, bizType, () -> {
            consumeTongYiStream(aiApiKey, systemPrompt, null, content, images, userId, chatId, bizType);
        });
    }

    private void streamYiYan(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType, List<String> images) {
        runStreamTask(userId, chatId, bizType, () -> {
            consumeYiYanStream(aiApiKey, buildChatMessages(systemPrompt, null, content), images, userId, chatId, bizType);
        });
    }

    private void streamXunFei(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType, List<String> images) {
        runStreamTask(userId, chatId, bizType, () -> {
            consumeXunFeiStream(aiApiKey, buildChatMessages(systemPrompt, null, content), 0.3, images, userId, chatId, bizType);
        });
    }

    /**
     * 普通聊天页的文心流式调用：会把近期 user/assistant 历史拼进 messages，再追加本轮用户输入。
     * bizType 传 null，走旧聊天页的 WebSocket 协议（不含 chatId、bizType 字段）。
     */
    private void QianFanResponse(String message, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey) {
        runStreamTask(userId, chatId, null, () -> {
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            consumeYiYanStream(aiApiKey, buildChatMessages(systemPrompt, chatList, message), null, userId, chatId, null);
        });
    }

    private List<String> readImages(String raw) {
        if (StrUtil.isEmpty(raw)) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(raw, null);
    }

    private List<Map<String, String>> buildChatMessages(String systemPrompt, List<Chat> history, String currentUserMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(systemPrompt)) {
            messages.add(chatMessage("system", systemPrompt));
        }
        if (history != null) {
            history.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    messages.add(chatMessage("user", chat.getMessage()));
                    messages.add(chatMessage("assistant", chat.getContent()));
                }
            });
        }
        messages.add(chatMessage("user", currentUserMessage));
        return messages;
    }

    private Map<String, String> chatMessage(String role, String content) {
        Map<String, String> item = new HashMap<>();
        item.put("role", role);
        item.put("content", content);
        return item;
    }

    /**
     * 通义百炼应用用的多轮历史：user / assistant，时间从旧到新。
     */
    private List<Map<String, String>> buildTongYiHistory(List<Chat> history) {
        List<Map<String, String>> items = new ArrayList<>();
        if (history == null || history.isEmpty()) {
            return items;
        }
        for (int i = history.size() - 1; i >= 0; i--) {
            Chat chat = history.get(i);
            if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                Map<String, String> item = new HashMap<>();
                item.put("user", chat.getMessage());
                item.put("assistant", chat.getContent());
                items.add(item);
            }
        }
        return items;
    }

    /**
     * 消费千帆流式响应：边收边通过 WebSocket 推给前端。
     * 鉴权只使用 API Key（IAM 密钥），不再使用应用 Secret Key。
     *
     * @param bizType 业务类型；null 表示普通聊天页，非空表示需求草稿等业务流式
     */
    private void consumeYiYanStream(AiApiKey aiApiKey, List<Map<String, String>> messages, List<String> images,
                                    String userId, String chatId, String bizType) {
        QianfanChatClient client = (QianfanChatClient) aiFactory.getDefaultChatModel(AiPlatformEnum.YI_YAN, aiApiKey);
        consumeClientStream(userId, chatId, bizType, (tenantId, isolationType, finished) ->
            client.streamChat(messages, aiApiKey.getApiAppId(), images, new QianfanChatClient.StreamListener() {
                @Override
                public void onDelta(String piece, boolean end) {
                    handleStreamDelta(tenantId, isolationType, userId, chatId, bizType, piece, end, finished);
                }

                @Override
                public void onError(String message) {
                    handleStreamError(tenantId, isolationType, userId, chatId, bizType, message, finished);
                }
            }));
    }

    private void consumeXunFeiStream(AiApiKey aiApiKey, List<Map<String, String>> messages, double temperature,
                                     String userId, String chatId, String bizType) {
        consumeXunFeiStream(aiApiKey, messages, temperature, null, userId, chatId, bizType);
    }

    private void consumeXunFeiStream(AiApiKey aiApiKey, List<Map<String, String>> messages, double temperature,
                                     List<String> images, String userId, String chatId, String bizType) {
        XunFeiChatClient client = (XunFeiChatClient) aiFactory.getDefaultChatModel(AiPlatformEnum.XUN_FEI, aiApiKey);
        consumeClientStream(userId, chatId, bizType, (tenantId, isolationType, finished) ->
            client.streamChat(messages, temperature, images, new XunFeiChatClient.StreamListener() {
                @Override
                public void onDelta(String piece, boolean end) {
                    handleStreamDelta(tenantId, isolationType, userId, chatId, bizType, piece, end, finished);
                }

                @Override
                public void onError(String message) {
                    handleStreamError(tenantId, isolationType, userId, chatId, bizType, message, finished);
                }
            }));
    }

    private void consumeTongYiStream(AiApiKey aiApiKey, String systemPrompt, List<Chat> history,
                                     String currentUserMessage, List<String> images,
                                     String userId, String chatId, String bizType) {
        TongYiChatClient client = (TongYiChatClient) aiFactory.getDefaultChatModel(AiPlatformEnum.TONG_YI, aiApiKey);
        consumeClientStream(userId, chatId, bizType, (tenantId, isolationType, finished) ->
            client.streamChat(systemPrompt, buildTongYiHistory(history), currentUserMessage, images, new TongYiChatClient.StreamListener() {
                @Override
                public void onDelta(String piece, boolean end) {
                    handleStreamDelta(tenantId, isolationType, userId, chatId, bizType, piece, end, finished);
                }

                @Override
                public void onError(String message) {
                    handleStreamError(tenantId, isolationType, userId, chatId, bizType, message, finished);
                }
            }));
    }

    @FunctionalInterface
    private interface StreamInvoker {
        void invoke(String tenantId, TenantEnum isolationType, boolean[] finished);
    }

    /**
     * 三个平台客户端都是阻塞式 streamChat，回调可能在 SDK 线程，落库前要重新绑租户。
     */
    private void consumeClientStream(String userId, String chatId, String bizType, StreamInvoker invoker) {
        final String tenantId = TenantContext.getTenantId();
        final TenantEnum isolationType = TenantContext.getIsolationType();
        final boolean[] finished = {false};
        try {
            invoker.invoke(tenantId, isolationType, finished);
            if (!finished[0]) {
                appendYiYanChunk(userId, chatId, bizType, StrUtil.EMPTY, true);
            }
        } catch (Throwable e) {
            persistYiYanPartial(userId, chatId);
            sendYiYanError(userId, chatId, bizType, StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()));
        }
    }

    private void handleStreamDelta(String tenantId, TenantEnum isolationType, String userId, String chatId,
                                   String bizType, String piece, boolean end, boolean[] finished) {
        if (StrUtil.isBlank(piece) && !end) {
            return;
        }
        try {
            bindTenantContext(tenantId, isolationType);
            finished[0] = appendYiYanChunk(userId, chatId, bizType, piece, end) || finished[0];
        } finally {
            TenantContext.clear();
        }
    }

    private void handleStreamError(String tenantId, TenantEnum isolationType, String userId, String chatId,
                                   String bizType, String message, boolean[] finished) {
        try {
            bindTenantContext(tenantId, isolationType);
            persistYiYanPartial(userId, chatId);
            sendYiYanError(userId, chatId, bizType, message);
            finished[0] = true;
        } finally {
            TenantContext.clear();
        }
    }

    private void sendYiYanError(String userId, String chatId, String bizType, String message) {
        if (StrUtil.isBlank(userId)) {
            return;
        }
        if (bizType == null) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("end", true);
            messageMap.put("orderBy", -1);
            aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
        } else {
            sendStreamChunk(userId, chatId, bizType, message, true, -1);
        }
    }

    /**
     * 把当前增量写入 Redis 缓存，结束时拼成完整回复落库，并推 WebSocket。
     * Redis key 为 chat:{chatId}，存已收到的文本片段列表。
     *
     * @return 本包是否已标记结束
     */
    private boolean appendYiYanChunk(String userId, String chatId, String bizType, String piece, boolean end) {
        String key = String.format(Locale.ROOT, "chat:%s", chatId);
        List<String> chunkMessage = new ArrayList<>();
        if (jedisClientService.exists(key)) {
            chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
        }
        if (StrUtil.isNotEmpty(piece)) {
            chunkMessage.add(piece);
        }
        if (end) {
            jedisClientService.del(key);
            Chat chat = chatService.selectById(chatId);
            if (chat != null) {
                chat.setContent(Joiner.on("").join(chunkMessage));
                chatService.updateEntity(chat, userId);
            }
        } else {
            jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
        }
        int orderBy = chunkMessage.isEmpty() ? 0 : chunkMessage.size() - 1;
        if (bizType == null) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", piece);
            messageMap.put("end", end);
            messageMap.put("orderBy", orderBy);
            aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
        } else {
            sendStreamChunk(userId, chatId, bizType, piece, end, orderBy);
        }
        return end;
    }

    /**
     * 流式中途异常时，把 Redis 里已经收到的半段回复落库，避免 chat 记录一直空着。
     */
    private void persistYiYanPartial(String userId, String chatId) {
        if (StrUtil.isBlank(chatId)) {
            return;
        }
        String key = String.format(Locale.ROOT, "chat:%s", chatId);
        if (!jedisClientService.exists(key)) {
            return;
        }
        List<String> chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
        jedisClientService.del(key);
        Chat chat = chatService.selectById(chatId);
        if (chat == null) {
            return;
        }
        chat.setContent(Joiner.on("").join(chunkMessage));
        chatService.updateEntity(chat, userId);
    }

    private void XunFeiResponse(String message, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey) {
        runStreamTask(userId, chatId, null, () -> {
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            consumeXunFeiStream(aiApiKey, buildChatMessages(systemPrompt, chatList, message), 0.5, userId, chatId, null);
        });
    }

    private List<Chat> getRecentlyChats(String userId, String apiKeyId) {
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Chat::getCreateTime));
        PageHelper.startPage(1, 10);
        return chatService.list(queryWrapper);
    }

    private void TongYiResponse(String message, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey) {
        runStreamTask(userId, chatId, null, () -> {
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            consumeTongYiStream(aiApiKey, systemPrompt, chatList, message, null, userId, chatId, null);
        });
    }


    @Override
    public void queryPageMessageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String apiKeyId = commonPageInfo.getHolderId();
        if (StrUtil.isEmpty(apiKeyId)) {
            throw new CustomException("apiKeyId不能为空");
        }

        AiApiKey aiApiKey = aiApiKeyService.selectById(apiKeyId);
        com.skyeye.role.entity.Role role = roleService.selectById(aiApiKey.getRoleId());
        aiApiKey.setRoleMation(role);

        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Chat::getCreateTime));
        List<Chat> chatList = list(queryWrapper);

        for (Chat chat : chatList) {
            chat.setApiKeyMation(aiApiKey);
        }
        outputObject.setBeans(chatList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void deleteAllByApiKeyId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String apiKeyId = params.get("apiKeyId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        remove(queryWrapper);
    }

}
