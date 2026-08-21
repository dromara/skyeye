/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.tongyi;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.common.History;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skyeye.common.util.ImagesUtil;
import com.skyeye.exception.CustomException;
import io.reactivex.Flowable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通义千问（百炼应用）流式对话客户端。
 * 走 Application 接口，不是模型 Generation。
 * 配置里的 appId 填百炼「应用管理」中的应用 ID。
 * <p>
 * dashscope-sdk-java 2.14.4 没有 messages / incrementalOutput 方法，
 * 用 prompt + history，增量参数走 extra parameters。
 */
public class TongYiChatClient {

    private final String appId;
    private final String apiKey;
    private final Application application;

    public TongYiChatClient(String appId, String apiKey) {
        if (StrUtil.isBlank(appId)) {
            throw new CustomException("通义配置缺少应用 ID，请填写百炼「应用管理」中的应用 ID");
        }
        if (StrUtil.isBlank(apiKey)) {
            throw new CustomException("通义配置缺少 API Key");
        }
        this.appId = appId;
        this.apiKey = apiKey;
        this.application = new Application();
    }

    /**
     * 阻塞消费流式对话，直到结束或失败。
     *
     * @param systemPrompt       AI 角色提示词，可空；应用自身提示词在百炼控制台
     * @param history            多轮历史，每项含 user / assistant，时间从旧到新
     * @param currentUserMessage 当前用户输入
     * @param listener           增量回调
     */
    public void streamChat(String systemPrompt, List<Map<String, String>> history,
                           String currentUserMessage, StreamListener listener) {
        streamChat(systemPrompt, history, currentUserMessage, null, listener);
    }

    public void streamChat(String systemPrompt, List<Map<String, String>> history,
                           String currentUserMessage, List<String> images, StreamListener listener) {
        ApplicationParam param = buildParam(systemPrompt, history, currentUserMessage, images);
        AtomicBoolean ended = new AtomicBoolean(false);
        try {
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
                // 2.14.4 默认累积输出，按增量切片后再回调
                String delta = text.startsWith(last[0]) ? text.substring(last[0].length()) : text;
                last[0] = text;
                if (StrUtil.isEmpty(delta)) {
                    return;
                }
                listener.onDelta(delta, false);
            });
            if (ended.compareAndSet(false, true)) {
                listener.onDelta(StrUtil.EMPTY, true);
            }
        } catch (Throwable t) {
            ended.set(true);
            listener.onError(StrUtil.blankToDefault(t.getMessage(), t.getClass().getSimpleName()));
        }
    }

    private ApplicationParam buildParam(String systemPrompt, List<Map<String, String>> history,
                                        String currentUserMessage, List<String> images) {
        String prompt = currentUserMessage;
        if (StrUtil.isNotBlank(systemPrompt)) {
            prompt = systemPrompt + "\n\n" + currentUserMessage;
        }
        AppParamWithImages.AppParamWithImagesBuilder<?, ?> builder = AppParamWithImages.builder()
            .apiKey(apiKey)
            .appId(appId)
            .prompt(prompt)
            .parameter("incremental_output", true)
            .imageList(filterImages(images));
        List<History> items = toHistory(history);
        if (!items.isEmpty()) {
            builder.history(items);
        }
        return builder.build();
    }

    private List<String> filterImages(List<String> images) {
        List<String> result = new ArrayList<>();
        if (images == null) {
            return result;
        }
        for (String image : images) {
            if (StrUtil.isBlank(image) || result.size() >= 3) {
                continue;
            }
            if (image.startsWith("data:")) {
                result.add(image);
                continue;
            }
            String encoded = ImagesUtil.urlToBase64(image.trim());
            if (StrUtil.isBlank(encoded)) {
                continue;
            }
            result.add(encoded.startsWith("data:") ? encoded : "data:image/png;base64," + encoded);
        }
        return result;
    }

    private List<History> toHistory(List<Map<String, String>> history) {
        List<History> items = new ArrayList<>();
        if (history == null) {
            return items;
        }
        for (Map<String, String> item : history) {
            if (item == null) {
                continue;
            }
            String user = item.get("user");
            String bot = item.get("assistant");
            if (StrUtil.isNotEmpty(user) && StrUtil.isNotEmpty(bot)) {
                items.add(History.builder().user(user).bot(bot).build());
            }
        }
        return items;
    }

    public interface StreamListener {
        void onDelta(String content, boolean end);

        void onError(String message);
    }

    /**
     * 2.14.4 没有 images()，按百炼应用 HTTP 协议把截图放进 input.image_list。
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    private static class AppParamWithImages extends ApplicationParam {
        private List<String> imageList;

        @Override
        public JsonObject getInput() {
            JsonObject input = super.getInput();
            if (imageList != null && !imageList.isEmpty()) {
                JsonArray arr = new JsonArray();
                for (String image : imageList) {
                    arr.add(image);
                }
                input.add("image_list", arr);
            }
            return input;
        }

        @Override
        public void validate() throws InputRequiredException {
            super.validate();
        }
    }
}
