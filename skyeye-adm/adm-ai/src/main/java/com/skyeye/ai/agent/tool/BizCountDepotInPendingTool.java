/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 待入库笔数（对齐 ERP 仪表盘 E03 / 入库管理 AllWait）。
 */
@Component
public class BizCountDepotInPendingTool implements AiAgentTool {

    @Autowired
    private BizCountGenericTool bizCountGenericTool;

    @Override
    public String name() {
        return "biz_count_depot_in_pending";
    }

    @Override
    public String description() {
        return "查询当前租户待入库单据笔数（含待入库、部分入库；口径与 ERP 仪表盘「入库管理」角标一致）。"
            + "用户问待入库有多少单/多少笔时调用本工具。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj())
            .set("required", new cn.hutool.json.JSONArray());
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        return bizCountGenericTool.execute(
            JSONUtil.createObj().set("domain", "erp").set("metric", "depot_in_pending"),
            context);
    }
}
