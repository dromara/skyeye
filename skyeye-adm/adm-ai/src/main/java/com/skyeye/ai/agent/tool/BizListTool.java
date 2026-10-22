/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.oa.list.service.IOaListService;
import com.skyeye.rest.shop.list.service.IShopListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * 业务列表查询（只读、强制分页）。OA 待办 + 商城买家订单。
 */
@Component
public class BizListTool implements AiAgentTool {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;

    @Autowired
    private IOaListService iOaListService;

    @Autowired
    private IShopListService iShopListService;

    @Override
    public String name() {
        return "biz_list";
    }

    @Override
    public String description() {
        return "分页查询业务列表（只读）。"
            + "OA：domain=oa，type=pending（审批待办）/ initiated（我发起）/ schedule（日程待办）；笔数用 biz_oa_board。"
            + "商城买家订单：domain=mall，type=waitPay/waitDeliver/waitReceive/waitReview/afterSale/all；"
            + "笔数用 biz_mall_board。"
            + "用户说「列出/看看/有哪些」明细时用本工具。"
            + "必须传 page（从1）与 limit（默认5，最大10）。"
            + "回答时在最终 JSON 中带 list 字段（title + items），便于前端卡片展示；items 用工具返回的精简字段。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("domain", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "业务域：oa / mall"))
                .set("type", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "oa：pending / initiated / schedule；"
                        + "mall：waitPay / waitDeliver / waitReceive / waitReview / afterSale / all"))
                .set("page", JSONUtil.createObj()
                    .set("type", "integer")
                    .set("description", "页码，从 1 开始，默认 1"))
                .set("limit", JSONUtil.createObj()
                    .set("type", "integer")
                    .set("description", "每页条数，默认 5，最大 10")))
            .set("required", new JSONArray().put("domain").put("type"));
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        String domain = args == null ? null : args.getStr("domain");
        String type = args == null ? null : args.getStr("type");
        int page = args == null ? 1 : args.getInt("page", 1);
        int limit = args == null ? DEFAULT_LIMIT : args.getInt("limit", DEFAULT_LIMIT);
        if (page < 1) {
            page = 1;
        }
        if (limit < 1) {
            limit = DEFAULT_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }
        if (StrUtil.isBlank(domain) || StrUtil.isBlank(type)) {
            return JSONUtil.createObj().set("ok", false).set("error", "domain 与 type 必填").toString();
        }
        String domainKey = domain.trim().toLowerCase(Locale.ROOT);
        try {
            if ("oa".equals(domainKey)) {
                return okResult("oa", type, iOaListService.queryTodoList(type, page, limit), listTitleOa);
            }
            if ("mall".equals(domainKey) || "shop".equals(domainKey)) {
                return okResult("mall", type, iShopListService.queryMyOrderList(type, page, limit), listTitleMall);
            }
            return JSONUtil.createObj()
                .set("ok", false)
                .set("domain", domainKey)
                .set("error", "该 domain 列表尚未接入，目前支持 oa / mall")
                .set("supported", JSONUtil.createObj()
                    .set("oa", JSONUtil.createArray().put("pending").put("initiated").put("schedule"))
                    .set("mall", JSONUtil.createArray()
                        .put("waitPay").put("waitDeliver").put("waitReceive")
                        .put("waitReview").put("afterSale").put("all")))
                .toString();
        } catch (CustomException e) {
            return JSONUtil.createObj().set("ok", false).set("error", e.getMessage()).toString();
        } catch (Exception e) {
            return JSONUtil.createObj()
                .set("ok", false)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }

    private String okResult(String domain, String type, Map<String, Object> data, TitleFn titleFn) {
        String listType = String.valueOf(data.getOrDefault("type", type));
        return JSONUtil.createObj()
            .set("ok", true)
            .set("domain", domain)
            .set("type", listType)
            .set("total", data.get("total"))
            .set("page", data.get("page"))
            .set("limit", data.get("limit"))
            .set("listTitle", titleFn.apply(listType))
            .set("items", data.get("items"))
            .set("hint", "请在最终回复的 JSON 中设置 list:{title,items}，items 原样或精简引用本结果；reply 中用一两句话概括")
            .toString();
    }

    @FunctionalInterface
    private interface TitleFn {
        String apply(String type);
    }

    private static final TitleFn listTitleOa = type -> {
        if ("initiated".equals(type)) {
            return "我发起的流程";
        }
        if ("schedule".equals(type)) {
            return "日程待办";
        }
        return "审批待办";
    };

    private static final TitleFn listTitleMall = type -> {
        if ("waitPay".equals(type)) {
            return "待支付订单";
        }
        if ("waitDeliver".equals(type)) {
            return "待发货订单";
        }
        if ("waitReceive".equals(type)) {
            return "待收货订单";
        }
        if ("waitReview".equals(type)) {
            return "待评价订单";
        }
        if ("afterSale".equals(type)) {
            return "售后中订单";
        }
        return "我的订单";
    };
}
