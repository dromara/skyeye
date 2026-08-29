/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台办公 AI 编排。角色提示词负责人设，本类补充当前页上下文、办事能力与输出格式。
 */
@Service
public class PlatformAiGuideService {

    private static final String JSON_BLOCK_BEGIN = "@@SKYEYE_JSON_BEGIN@@";

    private static final String JSON_BLOCK_END = "@@SKYEYE_JSON_END@@";

    private static final String BIZ_TYPE_CHAT = "chat";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = generate(inputObject.getParams());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    public Map<String, Object> generate(Map<String, Object> params) {
        String question = params.get("question").toString().trim();
        String pageTitle = params.get("pageTitle").toString();
        String pagePath = params.get("pagePath").toString();
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("saveChat", 1);
        extraParams.put("knowledgeQuery", question);
        return platformAiChatHelper.startStreamingChat(
            buildUserContent(question, pageTitle, pagePath),
            BIZ_TYPE_CHAT,
            extraParams);
    }

    private String buildUserContent(String question, String pageTitle, String pagePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户问题：").append(question).append("\n\n");
        sb.append("当前页面：").append(StrUtil.blankToDefault(pageTitle, "未知"));
        if (StrUtil.isNotBlank(pagePath)) {
            sb.append("（").append(pagePath).append("）");
        }
        sb.append("\n\n");
        sb.append("你是 SkyEye 云平台的办公 AI，覆盖 OA、ERP、CRM、项目协同，以及知识库、AI角色、AI配置等平台内全部 AI 能力。\n");
        sb.append("主任务是帮用户把事情办成：讲清楚怎么做、填什么、卡在哪、下一步是什么。不要把问题默认理解成「帮我找菜单」。\n\n");
        sb.append("按问题类型回答：\n");
        sb.append("1. 办事：请假、审批、考勤、采购、合同、库存、客户、项目等业务流程、必填项、状态流转、常见卡点\n");
        sb.append("2. 当前页：本页用途、关键字段、可做操作、易错点；用户在某页提问时优先结合本页\n");
        sb.append("3. 业务数据：按知识库解释单据、表、字段、关联关系；资料不足时说明缺什么，不要编造\n");
        sb.append("4. AI 管理：知识库与文件/库表同步、AI角色与提示词、AI配置启用与绑定，如何设置和排查\n");
        sb.append("5. 办公协助：归纳要点、对比方案、起草说明、列出检查清单\n");
        sb.append("6. 打开页面：仅当用户明确要去某功能，或把事办完必须进入某菜单时，才给跳转；否则 actions 为空数组\n\n");
        sb.append("回答要求：\n");
        sb.append("1. reply 用中文写完整、可执行的说明，步骤分点；不要只丢一个菜单名\n");
        sb.append("2. 以当前页面和知识库为准，没有依据的功能、字段、配置不要编造\n");
        sb.append("3. 跳转是辅助能力：navigate 的 menuId 必须来自知识库，一次最多 3 个；不需要打开页面时 actions 必须为 []\n");
        appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"reply\": \"给用户看的完整说明：怎么办理、填什么、注意什么；资料不足时说明缺什么\",\n"
                + "  \"actions\": []\n"
                + "}");
        return sb.toString();
    }

    private void appendMarkedJsonOutput(StringBuilder sb, String exampleJson) {
        sb.append("输出格式（必须严格遵守）：\n");
        sb.append("1. 可在 ").append(JSON_BLOCK_BEGIN).append(" 与 ")
            .append(JSON_BLOCK_END).append(" 之外写思考或说明\n");
        sb.append("2. 两个标记之间只能有一个合法 JSON 对象或数组，不要 markdown 代码块\n");
        sb.append("3. 程序只读取标记之间的内容，请务必输出完整起止标记\n");
        sb.append("示例：\n");
        sb.append(JSON_BLOCK_BEGIN).append("\n");
        sb.append(exampleJson.trim()).append("\n");
        sb.append(JSON_BLOCK_END).append("\n");
    }
}
