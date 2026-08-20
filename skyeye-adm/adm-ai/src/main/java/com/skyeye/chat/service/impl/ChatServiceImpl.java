package com.skyeye.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.ChatCompletionAssistantMessageParam;
import com.openai.models.ChatCompletionChunk;
import com.openai.models.ChatCompletionCreateParams;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.aiStreamModle.SparkListener;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chat.dao.ChatDao;
import com.skyeye.chat.entity.Chat;
import com.skyeye.chat.service.ChatService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
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

    /**
     * 千帆 V2 OpenAI 兼容接口的模型名。
     * 对应旧 SDK 的 ERNIE-Speed-8K；V2 要求小写加连字符。
     * 若报模型不存在，请到千帆模型列表换成当前可用的 model，例如 ernie-4.5-turbo-32k。
     */
    private static final String YI_YAN_MODEL = "ernie-speed-8k";

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

    private void streamTongYi(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType) {
        messageStreamExecutor.execute(() -> {
            List<Message> messages = new ArrayList<>();
            if (StrUtil.isNotBlank(systemPrompt)) {
                messages.add(Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build());
            }
            messages.add(Message.builder().role(Role.USER.getValue()).content(content).build());
            Generation generation = (Generation) aiFactory.getDefaultChatModel(AiPlatformEnum.TONG_YI, aiApiKey);
            GenerationParam param = GenerationParam.builder()
                .model("qwen-turbo")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.3)
                .apiKey(aiApiKey.getApiKey())
                .incrementalOutput(true)
                .build();
            try {
                Flowable<GenerationResult> result = generation.streamCall(param);
                result.blockingForEach(chunk -> {
                    String key = String.format(Locale.ROOT, "chat:%s", chatId);
                    List<String> chunkMessage = new ArrayList<>();
                    if (jedisClientService.exists(key)) {
                        chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                    }
                    boolean end = false;
                    String piece = StrUtil.EMPTY;
                    List<GenerationOutput.Choice> choiceList = chunk.getOutput().getChoices();
                    GenerationOutput.Choice choice = choiceList.stream().findFirst().orElse(null);
                    if (ObjectUtil.isEmpty(choice)) {
                        end = true;
                    } else {
                        if ("stop".equals(choice.getFinishReason())) {
                            end = true;
                        }
                        if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
                            piece = choice.getMessage().getContent();
                        }
                    }
                    chunkMessage.add(piece);
                    if (end) {
                        jedisClientService.del(key);
                        Chat chat = chatService.selectById(chatId);
                        chat.setContent(Joiner.on("").join(chunkMessage));
                        chatService.updateEntity(chat, userId);
                    } else {
                        jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                    }
                    sendStreamChunk(userId, chatId, bizType, piece, end, chunkMessage.size() - 1);
                });
            } catch (Exception e) {
                sendStreamChunk(userId, chatId, bizType, e.getMessage(), true, -1);
            }
        });
    }

    /**
     * 需求草稿等业务场景的文心流式调用：只发当前这一轮用户消息，不带历史。
     * HTTP 接口已返回 chatId，正文通过 WebSocket 按块推给前端。
     */
    private void streamYiYan(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType) {
        messageStreamExecutor.execute(() -> {
            ChatCompletionCreateParams.Builder builder = buildYiYanParams(aiApiKey, systemPrompt);
            builder.addUserMessage(content);
            consumeYiYanStream(aiApiKey, builder.build(), userId, chatId, bizType);
        });
    }

    private void streamXunFei(String content, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey, String bizType) {
        messageStreamExecutor.execute(() -> {
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
            sparkClient.chatStream(sparkRequest, new SparkListener() {
                @Override
                public void onMessage(String chunk, SparkResponseUsage usage, Integer status, SparkRequest request,
                                      SparkResponse response, WebSocket webSocket) {
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
        messageStreamExecutor.execute(() -> {
            ChatCompletionCreateParams.Builder builder = buildYiYanParams(aiApiKey, systemPrompt);
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            chatList.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    // 0.22.0 只有 addUserMessage(String)，没有 addAssistantMessage(String)
                    // 历史回复要用 AssistantMessageParam 再走 addMessage(...)
                    builder.addUserMessage(chat.getMessage())
                        .addMessage(ChatCompletionAssistantMessageParam.builder()
                            .content(chat.getContent())
                            .build());
                }
            });
            builder.addUserMessage(message);
            consumeYiYanStream(aiApiKey, builder.build(), userId, chatId, null);
        });
    }

    /**
     * 组装千帆 V2 Chat Completions 请求。
     * 官方文档：https://cloud.baidu.com/doc/qianfan-docs/s/nm9l6oc8e
     * V2 已支持 messages 里的 system；旧 V1 只能走独立的 system 字段，不能把 system 放进 messages。
     */
    private ChatCompletionCreateParams.Builder buildYiYanParams(AiApiKey aiApiKey, String systemPrompt) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
            .model(YI_YAN_MODEL);
        // 多应用场景把应用 ID 放到自定义 Header，对应官方 putAdditionalHeader("appid", ...)
        if (StrUtil.isNotBlank(aiApiKey.getApiAppId())) {
            builder.putAdditionalHeader("appid", aiApiKey.getApiAppId());
        }
        if (StrUtil.isNotBlank(systemPrompt)) {
            builder.addSystemMessage(systemPrompt);
        }
        return builder;
    }

    /**
     * 消费千帆流式响应：边收边通过 WebSocket 推给前端。
     * 鉴权只使用 API Key（IAM 密钥），不再使用应用 Secret Key。
     *
     * @param bizType 业务类型；null 表示普通聊天页，非空表示需求草稿等业务流式
     */
    private void consumeYiYanStream(AiApiKey aiApiKey, ChatCompletionCreateParams params,
                                    String userId, String chatId, String bizType) {
        OpenAIClient client = (OpenAIClient) aiFactory.getDefaultChatModel(AiPlatformEnum.YI_YAN, aiApiKey);
        // 流可能在最后一个有内容的 chunk 上不带 finish_reason，结束后要补一条 end=true
        final boolean[] finished = {false};
        try (StreamResponse<ChatCompletionChunk> stream = client.chat().completions().createStreaming(params)) {
            stream.stream().forEach(chunk -> {
                String piece = readYiYanDelta(chunk);
                boolean end = isYiYanStreamEnd(chunk);
                // 心跳/空 delta 且未结束：直接丢掉，避免前端拼进空白
                if (StrUtil.isBlank(piece) && !end) {
                    return;
                }
                finished[0] = appendYiYanChunk(userId, chatId, bizType, piece, end) || finished[0];
            });
            if (!finished[0]) {
                appendYiYanChunk(userId, chatId, bizType, StrUtil.EMPTY, true);
            }
        } catch (Exception e) {
            persistYiYanPartial(userId, chatId);
            // orderBy = -1 给前端当错误标记，用于弹 Toast，不再当作正常增量拼接
            if (bizType == null) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", e.getMessage());
                messageMap.put("end", true);
                messageMap.put("orderBy", -1);
                aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
            } else {
                sendStreamChunk(userId, chatId, bizType, e.getMessage(), true, -1);
            }
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
            chat.setContent(Joiner.on("").join(chunkMessage));
            chatService.updateEntity(chat, userId);
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
        String key = String.format(Locale.ROOT, "chat:%s", chatId);
        if (!jedisClientService.exists(key)) {
            return;
        }
        List<String> chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
        jedisClientService.del(key);
        Chat chat = chatService.selectById(chatId);
        chat.setContent(Joiner.on("").join(chunkMessage));
        chatService.updateEntity(chat, userId);
    }

    /**
     * 读取 OpenAI 兼容流式 chunk 的增量文本。
     * openai-java 0.22 里 delta().content() 返回 Optional&lt;String&gt;，空包常见于只有 finish_reason 的最后一帧。
     */
    private String readYiYanDelta(ChatCompletionChunk chunk) {
        try {
            if (chunk == null || chunk.choices() == null || chunk.choices().isEmpty()) {
                return StrUtil.EMPTY;
            }
            Optional<String> content = chunk.choices().get(0).delta().content();
            return content == null ? StrUtil.EMPTY : content.orElse(StrUtil.EMPTY);
        } catch (Exception e) {
            // 个别 chunk 没有 delta 字段时 SDK 会抛异常，按空增量处理即可
            return StrUtil.EMPTY;
        }
    }

    /**
     * 是否流结束：choices[0].finishReason 有值（一般是 stop）。
     * 空 choices 不当作结束，避免心跳包提前关掉前端流。
     */
    private boolean isYiYanStreamEnd(ChatCompletionChunk chunk) {
        try {
            if (chunk == null || chunk.choices() == null || chunk.choices().isEmpty()) {
                return false;
            }
            return chunk.choices().get(0).finishReason().isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    private void XunFeiResponse(String message, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey) {
        messageStreamExecutor.execute(() -> {
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
            sparkClient.chatStream(sparkRequest, new SparkListener() {
                @Override
                public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {

                    try {
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
                    }
                }
            });
        });
    }

    private List<Chat> getRecentlyChats(String userId, String apiKeyId) {
        // 获取聊天记录
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Chat::getCreateTime));
        PageHelper.startPage(1, 10);
        List<Chat> chatList = chatService.list(queryWrapper);
        return chatList;
    }

    private void TongYiResponse(String message, String systemPrompt, String userId, String chatId, AiApiKey aiApiKey) {
        // 开启异步请求：
        messageStreamExecutor.execute(() -> {
            List<Message> messages = new ArrayList<>();
            if (StrUtil.isNotBlank(systemPrompt)) {
                messages.add(Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build());
            }
            Message question = Message.builder().role(Role.USER.getValue()).content(message).build();
            Generation generation = (Generation) aiFactory.getDefaultChatModel(AiPlatformEnum.TONG_YI, aiApiKey);
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            chatList.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    Message userMsg = Message.builder().role(Role.USER.getValue()).content(chat.getMessage()).build();
                    Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(chat.getContent()).build();
                    messages.add(userMsg);
                    messages.add(assistantMsg);
                }
            });
            messages.add(question);
            GenerationParam param = GenerationParam.builder()
                //指定用于对话的通义千问模型名
                .model("qwen-turbo")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                //生成过程中核采样方法概率阈值，例如，取值为0.8时，仅保留概率加起来大于等于0.8的最可能token的最小集合作为候选集。
                // 取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的确定性越高。
                .topP(0.8)
                //阿里云控制台DASHSCOPE获取的api-key
                .apiKey(aiApiKey.getApiKey())
                //启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑“自行判断”是否使用互联网搜索结果。
                .enableSearch(true)
                .incrementalOutput(true)
                .build();
            Flowable<GenerationResult> result;
            try {
                result = generation.streamCall(param);
            } catch (NoApiKeyException e) {
                throw new RuntimeException(e);
            } catch (InputRequiredException e) {
                throw new RuntimeException(e);
            }
            result.blockingForEach(chunk -> {
                String key = String.format(Locale.ROOT, "chat:%s", chatId);
                List<String> chunkMessage = new ArrayList<>();
                if (jedisClientService.exists(key)) {
                    chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                }
                Boolean end = false;
                List<GenerationOutput.Choice> choiceList = chunk.getOutput().getChoices();
                GenerationOutput.Choice choice = choiceList.stream().findFirst().orElse(null);
                if (ObjectUtil.isEmpty(choice)) {
                    end = true;
                    chunkMessage.add(StrUtil.EMPTY);
                } else {
                    if (choice.getFinishReason().equals("stop")) {
                        end = true;
                    }
                    chunkMessage.add(choice.getMessage().getContent());
                }
                if (end) {
                    jedisClientService.del(key);
                    String content = Joiner.on("").join(chunkMessage);
                    // 修改回复内容
                    Chat chat = chatService.selectById(chatId);
                    chat.setContent(content);
                    chatService.updateEntity(chat, userId);
                } else {
                    jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                }
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", chunkMessage.get(chunkMessage.size() - 1));
                messageMap.put("end", end);
                messageMap.put("orderBy", chunkMessage.size() - 1);
                aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
            });
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
