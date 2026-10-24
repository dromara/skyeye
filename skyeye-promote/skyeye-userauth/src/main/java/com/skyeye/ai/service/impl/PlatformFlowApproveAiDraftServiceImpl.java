/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.service.PlatformFlowApproveAiDraftService;
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
 * 审批 AI：根据业务单据摘要 + 审批历史，给出只读建议。
 * <p>
 * 严禁代用户调用同意/驳回接口；前端仅可「一键填入意见」且需用户再点提交。
 */
@Service
public class PlatformFlowApproveAiDraftServiceImpl implements PlatformFlowApproveAiDraftService {

    public static final String BIZ_TYPE = "flowApproveAssist";

    /** 整表 JSON 可能较大；过短会截掉明细，导致模型误判「无明细」 */
    private static final int MAX_CONTEXT_CHARS = 50000;

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String question = str(params, "question");
        if (StrUtil.isBlank(question)) {
            question = "请根据单据与审批历史，给出摘要、风险点和建议意见。";
        }
        String content = buildPrompt(
            question,
            str(params, "taskNodeName"),
            str(params, "processInstanceId"),
            str(params, "businessContext"),
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

    private String buildPrompt(String question, String taskNodeName, String processInstanceId,
                               String businessContext, String historyContext) {
        String biz = trimContext(businessContext);
        String his = trimContext(historyContext);
        StringBuilder sb = new StringBuilder();
        sb.append("你是企业审批助手，只做只读分析与文案建议。\n");
        sb.append("硬性约束：\n");
        sb.append("1. 不得声称已同意/已驳回/已提交；不得要求系统代为审批。\n");
        sb.append("2. 金额、数量、合规风险只提示，最终由审批人确认。\n");
        sb.append("3. suggestFlag 只能是 \"1\"(建议同意) 或 \"2\"(建议拒绝)，也可留空表示无法判断。\n");
        sb.append("4. suggestOpinion 为可粘贴进审批意见框的简短中文（50～200字）。\n");
        sb.append("5. 下方「业务表单完整数据」含表头与明细（如 purchaseLinks 等），摘要与风险点必须基于其中实际字段，不得声称「未展示明细」。\n");
        sb.append("当前审批节点：").append(StrUtil.blankToDefault(taskNodeName, "未知")).append("\n");
        if (StrUtil.isNotBlank(processInstanceId)) {
            sb.append("流程实例：").append(processInstanceId).append("\n");
        }
        sb.append("用户问题：").append(question).append("\n");
        sb.append("业务表单完整数据(JSON)：\n").append(StrUtil.blankToDefault(biz, "{}")).append("\n");
        sb.append("审批历史(JSON)：\n").append(StrUtil.blankToDefault(his, "[]")).append("\n");
        AiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"summary\": \"单据与当前节点一句话摘要\",\n"
                + "  \"risks\": [\"风险点1\", \"风险点2\"],\n"
                + "  \"suggestFlag\": \"1\",\n"
                + "  \"suggestOpinion\": \"建议写入意见框的文案\",\n"
                + "  \"reply\": \"给审批人看的说明（仅供参考）\"\n"
                + "}");
        return sb.toString();
    }

    private Map<String, Object> parseAssistAnswer(String answer) {
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("summary", "");
            bean.put("risks", new ArrayList<>());
            bean.put("suggestFlag", "");
            bean.put("suggestOpinion", "");
            bean.put("reply", answer.trim());
            return bean;
        }
        bean.put("summary", StrUtil.blankToDefault(json.getStr("summary"), ""));
        bean.put("risks", toStringList(json.get("risks")));
        String flag = StrUtil.blankToDefault(json.getStr("suggestFlag"), "").trim();
        if (!"1".equals(flag) && !"2".equals(flag)) {
            flag = "";
        }
        bean.put("suggestFlag", flag);
        bean.put("suggestOpinion", StrUtil.blankToDefault(json.getStr("suggestOpinion"), ""));
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
