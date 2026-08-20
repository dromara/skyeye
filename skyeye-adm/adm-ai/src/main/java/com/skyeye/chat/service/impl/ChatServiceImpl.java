package com.skyeye.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.common.History;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.ai.core.qianfan.QianfanChatClient;
import com.skyeye.aiStreamModle.SparkListener;
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
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import io.reactivex.Flowable;
import okhttp3.WebSocket;
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
                streamYiYan(content, systemPrompt, userId, id, aiApiKey, bizType);
                break;
            case XUN_FEI:
                streamXunFei(content, systemPrompt, userId, id, aiApiKey, bizType);
                break;
            case TONG_YI:
                streamTongYi(content, systemPrompt, userId, id, aiApiKey, bizType);
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

    private void streamTongYi(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType) {
        runStreamTask(userId, chatId, bizType, () -> {
            consumeTongYiAppStream(aiApiKey, systemPrompt, null, content, userId, chatId, bizType);
        });
    }

    /**
     * 需求草稿等业务场景的文心流式调用：只发当前这一轮用户消息，不带历史。
     * HTTP 接口已返回 chatId，正文通过 WebSocket 按块推给前端。
     */
    private void streamYiYan(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType) {
        runStreamTask(userId, chatId, bizType, () -> {
            consumeYiYanStream(aiApiKey, buildYiYanMessages(systemPrompt, null, content), userId, chatId, bizType);
        });
    }

    private void streamXunFei(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType) {
        runStreamTask(userId, chatId, bizType, () -> {
            List<SparkMessage> messageList = new ArrayList<>();
            if (StrUtil.isNotBlank(systemPrompt)) {
                messageList.add(SparkMessage.systemContent(systemPrompt));
            }
            messageList.add(SparkMessage.userContent(content));
            SparkClient sparkClient = (SparkClient) aiFactory.getDefaultChatModel(AiPlatformEnum.XUN_FEI, aiApiKey);
            SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messageList)
                .maxTokens(2048)
                .temperature(0.3)
                .apiVersion(SparkApiVersion.V3_5)
                .build();
            final String tenantId = TenantContext.getTenantId();
            final TenantEnum isolationType = TenantContext.getIsolationType();
            sparkClient.chatStream(sparkRequest, new SparkListener() {
                @Override
                public void onMessage(String chunk, SparkResponseUsage usage, Integer status, SparkRequest request,
                                      SparkResponse response, WebSocket webSocket) {
                    try {
                        bindTenantContext(tenantId, isolationType);
                        String key = String.format(Locale.ROOT, "chat:%s", chatId);
                        List<String> chunkMessage = new ArrayList<>();
                        if (jedisClientService.exists(key)) {
                            chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                        }
                        chunkMessage.add(chunk);
                        boolean end = status != null && status == 2;
                        if (end) {
                            jedisClientService.del(key);
                            Chat chat = chatService.selectById(chatId);
                            chat.setContent(Joiner.on("").join(chunkMessage));
                            chatService.updateEntity(chat, userId);
                        } else {
                            jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                        }
                        sendStreamChunk(userId, chatId, bizType, chunk, end, chunkMessage.size() - 1);
                    } finally {
                        TenantContext.clear();
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response httpResponse) {
                    sendStreamChunk(userId, chatId, bizType, t == null ? "讯飞星火调用失败" : t.getMessage(), true, -1);
                }
            });
        });
    }

    /**
     * 普通聊天页的文心流式调用：会把近期 user/assistant 历史拼进 messages，再追加本轮用户输入。
     * bizType 传 null，走旧聊天页的 WebSocket 协议（不含 chatId、bizType 字段）。
     */
    private void QianFanResponse(String message, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey) {
        runStreamTask(userId, chatId, null, () -> {
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            consumeYiYanStream(aiApiKey, buildYiYanMessages(systemPrompt, chatList, message), userId, chatId, null);
        });
    }

    /**
     * 组装千帆 V2 Chat Completions 的 messages。
     * 官方文档：https://cloud.baidu.com/doc/qianfan-docs/s/nm9l6oc8e
     * V2 已支持 role=system；旧 V1 只能走独立的 system 字段。
     */
    private List<Map<String, String>> buildYiYanMessages(String systemPrompt, List<Chat> history, String currentUserMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(systemPrompt)) {
            messages.add(yiYanMessage("system", systemPrompt));
        }
        if (history != null) {
            history.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    messages.add(yiYanMessage("user", chat.getMessage()));
                    messages.add(yiYanMessage("assistant", chat.getContent()));
                }
            });
        }
        messages.add(yiYanMessage("user", currentUserMessage));
        return messages;
    }

    private Map<String, String> yiYanMessage(String role, String content) {
        Map<String, String> item = new HashMap<>();
        item.put("role", role);
        item.put("content", content);
        return item;
    }

    /**
     * 消费千帆流式响应：边收边通过 WebSocket 推给前端。
     * 鉴权只使用 API Key（IAM 密钥），不再使用应用 Secret Key。
     *
     * @param bizType 业务类型；null 表示普通聊天页，非空表示需求草稿等业务流式
     */
    private void consumeYiYanStream(AiApiKey aiApiKey, List<Map<String, String>> messages,
                                    String userId, String chatId, String bizType) {
        QianfanChatClient client = (QianfanChatClient) aiFactory.getDefaultChatModel(AiPlatformEnum.YI_YAN, aiApiKey);
        final String tenantId = TenantContext.getTenantId();
        final TenantEnum isolationType = TenantContext.getIsolationType();
        final boolean[] finished = {false};
        try {
            client.streamChat(messages, aiApiKey.getApiAppId(), new QianfanChatClient.StreamListener() {
                @Override
                public void onDelta(String piece, boolean end) {
                    if (StrUtil.isBlank(piece) && !end) {
                        return;
                    }
                    // SSE 回调在 OkHttp 线程，必须重新绑定租户再落库
                    try {
                        bindTenantContext(tenantId, isolationType);
                        finished[0] = appendYiYanChunk(userId, chatId, bizType, piece, end) || finished[0];
                    } finally {
                        TenantContext.clear();
                    }
                }

                @Override
                public void onError(String message) {
                    try {
                        bindTenantContext(tenantId, isolationType);
                        persistYiYanPartial(userId, chatId);
                        sendYiYanError(userId, chatId, bizType, message);
                        finished[0] = true;
                    } finally {
                        TenantContext.clear();
                    }
                }
            });
            if (!finished[0]) {
                appendYiYanChunk(userId, chatId, bizType, StrUtil.EMPTY, true);
            }
        } catch (Throwable e) {
            persistYiYanPartial(userId, chatId);
            sendYiYanError(userId, chatId, bizType, StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()));
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
            List<SparkMessage> messageList = new ArrayList<>();
            if (StrUtil.isNotBlank(systemPrompt)) {
                messageList.add(SparkMessage.systemContent(systemPrompt));
            }
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            chatList.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    messageList.add(SparkMessage.userContent(chat.getMessage()));
                    messageList.add(SparkMessage.assistantContent(chat.getContent()));
                }
            });
            messageList.add(SparkMessage.userContent(message));

            SparkClient sparkClient = (SparkClient) aiFactory.getDefaultChatModel(AiPlatformEnum.XUN_FEI, aiApiKey);
            // 构造请求
            SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messageList)
                .maxTokens(2048)
                .temperature(0.5)
                .apiVersion(SparkApiVersion.V3_5)
                .build();
            // 封装聊天信息
            final String tenantId = TenantContext.getTenantId();
            final TenantEnum isolationType = TenantContext.getIsolationType();
            sparkClient.chatStream(sparkRequest, new SparkListener() {
                @Override
                public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {

                    try {
                        bindTenantContext(tenantId, isolationType);
                        JSONObject jsonObject = new JSONObject(this.objectMapper.writeValueAsString(sparkResponse));
                        // 获取payload对象
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        // 获取choices对象
                        JSONObject choices = payload.getJSONObject("choices");
                        String key = String.format(Locale.ROOT, "chat:%s", chatId);
                        List<String> chunkMessage = new ArrayList<>();
                        if (jedisClientService.exists(key)) {
                            chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                        }
                        chunkMessage.add(content);
                        if (status == 2) {
                            jedisClientService.del(key);
                            String totalMessage = Joiner.on("").join(chunkMessage);
                            // 修改回复内容
                            Chat chat = chatService.selectById(chatId);
                            chat.setContent(totalMessage);
                            chatService.updateEntity(chat, userId);
                        } else {
                            jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                        }
                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("message", content);
                        messageMap.put("end", status == 2);
                        messageMap.put("orderBy", choices.get("seq"));
                        aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
                    } catch (JsonProcessingException var9) {
                        JsonProcessingException e = var9;
                        throw new RuntimeException(e);
                    } finally {
                        TenantContext.clear();
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response httpResponse) {
                    sendYiYanError(userId, chatId, null, t == null ? "讯飞星火调用失败" : t.getMessage());
                }
            });
        });
    }

    /**
     * 通义走百炼「应用」接口（Application），不是模型 Generation。
     * apiAppId 必须填应用管理里的应用 ID；应用自己的模型和提示词在控制台配置。
     * dashscope 2.14.4 没有 messages / incrementalOutput 方法，用 prompt + history，增量参数走 extra parameters。
     */
    private ApplicationParam buildTongYiAppParam(AiApiKey aiApiKey, String systemPrompt,
                                                 List<Chat> history, String currentUserMessage) {
        if (StrUtil.isBlank(aiApiKey.getApiAppId())) {
            throw new CustomException("通义配置缺少应用 ID，请填写百炼「应用管理」中的应用 ID");
        }
        if (StrUtil.isBlank(aiApiKey.getApiKey())) {
            throw new CustomException("通义配置缺少 API Key");
        }
        String prompt = currentUserMessage;
        if (StrUtil.isNotBlank(systemPrompt)) {
            prompt = systemPrompt + "\n\n" + currentUserMessage;
        }
        ApplicationParam.ApplicationParamBuilder<?, ?> builder = ApplicationParam.builder()
            .apiKey(aiApiKey.getApiKey())
            .appId(aiApiKey.getApiAppId())
            .prompt(prompt)
            .parameter("incremental_output", true);
        if (history != null && !history.isEmpty()) {
            List<History> items = new ArrayList<>();
            for (int i = history.size() - 1; i >= 0; i--) {
                Chat chat = history.get(i);
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    items.add(History.builder().user(chat.getMessage()).bot(chat.getContent()).build());
                }
            }
            if (!items.isEmpty()) {
                builder.history(items);
            }
        }
        return builder.build();
    }

    /**
     * 消费百炼应用流式响应。流结束后再发 end 包，避免前端一直转圈。
     * 2.14.4 默认累积输出，这里按增量切片后再推给前端。
     */
    private void consumeTongYiAppStream(AiApiKey aiApiKey, String systemPrompt, List<Chat> history,
                                        String currentUserMessage, String userId, String chatId, String bizType) {
        try {
            Application application = (Application) aiFactory.getDefaultChatModel(AiPlatformEnum.TONG_YI, aiApiKey);
            ApplicationParam param = buildTongYiAppParam(aiApiKey, systemPrompt, history, currentUserMessage);
            Flowable<ApplicationResult> result = application.streamCall(param);
            final String[] last = {StrUtil.EMPTY};
            result.blockingForEach(chunk -> {
                String text = StrUtil.EMPTY;
                if (chunk.getOutput() != null && chunk.getOutput().getText() != null) {
                    text = chunk.getOutput().getText();
                }
                if (StrUtil.isEmpty(text)) {
                    return;
                }
                String delta = text.startsWith(last[0]) ? text.substring(last[0].length()) : text;
                last[0] = text;
                if (StrUtil.isEmpty(delta)) {
                    return;
                }
                appendYiYanChunk(userId, chatId, bizType, delta, false);
            });
            appendYiYanChunk(userId, chatId, bizType, StrUtil.EMPTY, true);
        } catch (Throwable t) {
            persistYiYanPartial(userId, chatId);
            sendYiYanError(userId, chatId, bizType, StrUtil.blankToDefault(t.getMessage(), t.getClass().getSimpleName()));
        }
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
            consumeTongYiAppStream(aiApiKey, systemPrompt, chatList, message, userId, chatId, null);
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
