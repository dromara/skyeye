/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import org.springframework.stereotype.Component;

/**
 * 可直接回答时的路由出口：闲聊、解释、引导等不需要查数时选本工具。
 * 与业务工具并列，由模型自行选择（配合 tool_choice=required）。
 */
@Component
public class DirectReplyTool implements AiAgentTool {

    public static final String NAME = "direct_reply";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "当不需要查询实时业务数据或知识库统计、可以直接回答时调用本工具。"
            + "适用：闲聊、概念解释、操作步骤说明、菜单引导、澄清问题等。"
            + "若用户要问待办笔数、待出库/待入库、知识库文档数等实时数量，不要用本工具，"
            + "应改用 biz_oa_board / biz_erp_board / knowledge_doc_stats。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("reason", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "为何无需查询数据即可回答（可选）")))
            .set("required", new JSONArray());
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        return JSONUtil.createObj()
            .set("ok", true)
            .set("mode", "direct")
            .set("hint", "请直接给出完整最终回答，不要说请稍等或正在查询")
            .toString();
    }
}
