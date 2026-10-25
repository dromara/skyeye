/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.skyeye.ai.service.PlatformShopGoodsCopyAiDraftService;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.AiJsonHelper;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品文案草稿：生成名称 / 简介 / 详情 HTML，不代为保存上架。
 */
@Service
public class PlatformShopGoodsCopyAiDraftServiceImpl implements PlatformShopGoodsCopyAiDraftService {

    public static final String BIZ_TYPE = "shopGoodsCopyDraft";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String question = str(params, "question");
        if (StrUtil.isBlank(question)) {
            throw new CustomException("请输入商品卖点或生成要求");
        }
        String content = buildPrompt(
            question,
            str(params, "name"),
            str(params, "model"),
            str(params, "remark"),
            str(params, "content"),
            str(params, "categoryName"));
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
        outputObject.setBean(parseDraft(answer));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String buildPrompt(String question, String name, String model, String remark,
                               String content, String categoryName) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是电商商品文案助手，只生成可编辑草稿，不声称已上架或已保存。\n");
        sb.append("要求：\n");
        sb.append("1. name：简洁商品标题（中文，尽量≤40字）。\n");
        sb.append("2. remark：商品简介/卖点（纯文本，≤200字）。\n");
        sb.append("3. content：商品详情，使用简单 HTML（可用 p/ul/li/strong），不要 markdown 代码块，不要外链脚本。\n");
        sb.append("4. model：型号可留空或沿用已有；不要编造不存在的认证/参数。\n");
        sb.append("5. 金额、库存、规格价不要写入文案，除非用户明确要求。\n");
        sb.append("用户要求：").append(question).append("\n");
        if (StrUtil.isNotBlank(categoryName)) {
            sb.append("商城分类：").append(categoryName).append("\n");
        }
        sb.append("当前名称：").append(trim(name, 200)).append("\n");
        sb.append("当前型号：").append(trim(model, 100)).append("\n");
        sb.append("当前简介：").append(trim(remark, 500)).append("\n");
        sb.append("当前详情(可参考改写)：\n").append(trim(stripHtmlBrief(content), 3000)).append("\n");
        AiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"name\": \"商品标题\",\n"
                + "  \"model\": \"型号可空\",\n"
                + "  \"remark\": \"简介卖点\",\n"
                + "  \"content\": \"<p>详情段落</p>\",\n"
                + "  \"reply\": \"给编辑者的一句说明\"\n"
                + "}");
        return sb.toString();
    }

    private Map<String, Object> parseDraft(String answer) {
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("name", "");
            bean.put("model", "");
            bean.put("remark", "");
            bean.put("content", "");
            bean.put("reply", answer.trim());
            return bean;
        }
        bean.put("name", StrUtil.blankToDefault(json.getStr("name"), "").trim());
        bean.put("model", StrUtil.blankToDefault(json.getStr("model"), "").trim());
        bean.put("remark", StrUtil.blankToDefault(json.getStr("remark"), "").trim());
        bean.put("content", StrUtil.blankToDefault(json.getStr("content"), "").trim());
        bean.put("reply", StrUtil.blankToDefault(json.getStr("reply"), ""));
        return bean;
    }

    private String stripHtmlBrief(String html) {
        if (StrUtil.isBlank(html)) {
            return "";
        }
        return html.replaceAll("(?is)<script[^>]*>.*?</script>", "")
            .replaceAll("(?is)<style[^>]*>.*?</style>", "")
            .replaceAll("<[^>]+>", " ")
            .replaceAll("&nbsp;", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private String trim(String text, int max) {
        if (StrUtil.isBlank(text)) {
            return "";
        }
        String t = text.trim();
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max) + "…";
    }

    private String str(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return "";
        }
        return String.valueOf(params.get(key));
    }
}
