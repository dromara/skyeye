/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.service.PlatformShopAfterSaleAiDraftService;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.AiJsonHelper;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后摘要：根据当前售后单 + 本单历史记录给出只读建议。
 */
@Service
public class PlatformShopAfterSaleAiDraftServiceImpl implements PlatformShopAfterSaleAiDraftService {

    public static final String BIZ_TYPE = "shopAfterSaleSummary";

    private static final int MAX_CONTEXT_CHARS = 30000;

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String question = str(params, "question");
        if (StrUtil.isBlank(question)) {
            question = "请根据售后单给出摘要、风险点与处理建议。";
        }
        String content = buildPrompt(
            question,
            str(params, "afterSaleContext"),
            str(params, "historyContext"));
        Map<String, Object> bean = platformAiChatHelper.startStreamingChat(content, BIZ_TYPE);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void parseAnswer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String answer = str(params, "answer");
        if (StrUtil.isBlank(answer)) {
            throw new CustomException("AI 回答为空");
        }
        outputObject.setBean(parseAssistAnswer(answer));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String buildPrompt(String question, String afterSaleContext, String historyContext) {
        String biz = trimContext(afterSaleContext);
        String his = trimContext(historyContext);
        StringBuilder sb = new StringBuilder();
        sb.append("你是电商售后处理助手，只做只读分析与文案建议。\n");
        sb.append("硬性约束：\n");
        sb.append("1. 不得声称已同意/已拒绝/已退款/已确认收货；不得要求系统代为操作。\n");
        sb.append("2. 金额以分或元字段为准，只提示风险，最终由商家确认。\n");
        sb.append("3. suggestAction 只能是 agree / reject / confirm / 空字符串。\n");
        sb.append("4. suggestRejectReason 仅在建议拒绝时给出可粘贴文案（30～120字）。\n");
        sb.append("5. 必须阅读下方当前售后与历史记录，不得声称「没有明细/没有原因」。\n");
        sb.append("用户问题：").append(question).append("\n");
        sb.append("当前售后单(JSON)：\n").append(StrUtil.blankToDefault(biz, "{}")).append("\n");
        sb.append("本单售后历史(JSON)：\n").append(StrUtil.blankToDefault(his, "[]")).append("\n");
        AiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"summary\": \"售后一句话摘要\",\n"
                + "  \"risks\": [\"风险点1\"],\n"
                + "  \"suggestAction\": \"agree\",\n"
                + "  \"suggestRejectReason\": \"\",\n"
                + "  \"reply\": \"给商家的说明（仅供参考）\"\n"
                + "}");
        return sb.toString();
    }

    private Map<String, Object> parseAssistAnswer(String answer) {
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("summary", "");
            bean.put("risks", new ArrayList<>());
            bean.put("suggestAction", "");
            bean.put("suggestRejectReason", "");
            bean.put("reply", answer.trim());
            return bean;
        }
        bean.put("summary", StrUtil.blankToDefault(json.getStr("summary"), ""));
        bean.put("risks", toStringList(json.get("risks")));
        String action = StrUtil.blankToDefault(json.getStr("suggestAction"), "").trim().toLowerCase();
        if (!"agree".equals(action) && !"reject".equals(action) && !"confirm".equals(action)) {
            action = "";
        }
        bean.put("suggestAction", action);
        bean.put("suggestRejectReason", StrUtil.blankToDefault(json.getStr("suggestRejectReason"), ""));
        bean.put("reply", StrUtil.blankToDefault(json.getStr("reply"),
            StrUtil.blankToDefault(json.getStr("summary"), "")));
        return bean;
    }

    private List<String> toStringList(Object raw) {
        List<String> list = new ArrayList<>();
        if (raw instanceof JSONArray) {
            JSONArray arr = (JSONArray) raw;
            for (int i = 0; i < arr.size(); i++) {
                String s = arr.getStr(i);
                if (StrUtil.isNotBlank(s)) {
                    list.add(s.trim());
                }
            }
            return list;
        }
        if (raw instanceof List) {
            for (Object o : (List<?>) raw) {
                if (o != null && StrUtil.isNotBlank(o.toString())) {
                    list.add(o.toString().trim());
                }
            }
        }
        return list;
    }

    private String trimContext(String text) {
        if (StrUtil.isBlank(text)) {
            return "";
        }
        String t = text.trim();
        if (t.length() <= MAX_CONTEXT_CHARS) {
            return t;
        }
        return t.substring(0, MAX_CONTEXT_CHARS) + "\n...(truncated)";
    }

    private String str(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return "";
        }
        Object v = params.get(key);
        if (v instanceof Map || v instanceof List || v instanceof JSONObject || v instanceof JSONArray) {
            return JSONUtil.toJsonStr(v);
        }
        return String.valueOf(v);
    }
}
