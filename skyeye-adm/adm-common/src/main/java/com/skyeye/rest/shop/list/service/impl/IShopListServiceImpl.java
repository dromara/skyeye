/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.list.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shop.list.service.IShopListService;
import com.skyeye.rest.shop.order.rest.IShopOrderRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 买家订单列表，口径对齐商城「我的订单」queryOrderPageList（type 1~6）。
 */
@Service
public class IShopListServiceImpl implements IShopListService {

    /** 与 ShopPC orderStatusMap / ShopOrderItemOtherState 对齐 */
    private static final Map<String, String> STATE_LABELS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("0", "待支付");
        m.put("1", "支付失败");
        m.put("2", "已取消");
        m.put("3", "待发货");
        m.put("4", "全部发货");
        m.put("5", "运输中");
        m.put("6", "已签收");
        m.put("7", "已完成");
        m.put("8", "待评价");
        m.put("9", "已评价");
        m.put("10", "退款中");
        m.put("11", "已退款");
        m.put("12", "退货中");
        m.put("13", "已退货");
        m.put("14", "换货中");
        m.put("15", "已换货");
        m.put("16", "部分完成");
        m.put("17", "部分评价");
        m.put("18", "部分发货");
        m.put("19", "部分签收");
        STATE_LABELS = Collections.unmodifiableMap(m);
    }

    @Autowired
    private IShopOrderRest iShopOrderRest;

    @Override
    public Map<String, Object> queryMyOrderList(String type, int page, int limit) {
        String listType = normalizeType(type);
        Map<String, Object> params = new HashMap<>();
        params.put("page", Math.max(page, 1));
        params.put("limit", Math.max(limit, 1));
        params.put("type", toApiType(listType));
        params.put("keyword", "");

        ResultEntity entity = fetch(() -> iShopOrderRest.queryOrderPageList(params));
        List<Map<String, Object>> rows = entity == null || entity.getRows() == null
            ? Collections.emptyList() : entity.getRows();
        int total = entity == null || entity.getTotal() == null ? rows.size() : entity.getTotal();

        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            if (row != null) {
                items.add(slimItem(row));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", listType);
        result.put("total", total);
        result.put("page", params.get("page"));
        result.put("limit", params.get("limit"));
        result.put("items", items);
        return result;
    }

    private ResultEntity fetch(Supplier<String> loader) {
        try {
            return ExecuteFeignClient.get(loader);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(StrUtil.blankToDefault(e.getMessage(), "查询商城订单列表失败"));
        }
    }

    private String normalizeType(String type) {
        String raw = StrUtil.blankToDefault(type, "all").trim().toLowerCase(Locale.ROOT);
        if ("waitpay".equals(raw) || "1".equals(raw)) {
            return "waitPay";
        }
        if ("waitdeliver".equals(raw) || "2".equals(raw)) {
            return "waitDeliver";
        }
        if ("waitreceive".equals(raw) || "3".equals(raw)) {
            return "waitReceive";
        }
        if ("waitreview".equals(raw) || "4".equals(raw)) {
            return "waitReview";
        }
        if ("aftersale".equals(raw) || "6".equals(raw)) {
            return "afterSale";
        }
        return "all";
    }

    /** 商城 queryOrderPageList 的 type：空=全部，1待支付…6售后 */
    private String toApiType(String listType) {
        if ("waitPay".equals(listType)) {
            return "1";
        }
        if ("waitDeliver".equals(listType)) {
            return "2";
        }
        if ("waitReceive".equals(listType)) {
            return "3";
        }
        if ("waitReview".equals(listType)) {
            return "4";
        }
        if ("afterSale".equals(listType)) {
            return "6";
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> slimItem(Map<String, Object> row) {
        Map<String, Object> item = new LinkedHashMap<>();
        String title = goodsName(row);
        if (StrUtil.isBlank(title)) {
            title = str(row, "oddNumber", "id");
        }
        item.put("title", StrUtil.blankToDefault(title, "未命名订单"));
        item.put("subtitle", joinMeta(
            stateLabel(str(row, "state")),
            prefix("金额", fenToYuan(row.get("payPrice"))),
            prefix("时间", str(row, "createTime")),
            storeName(row)));
        item.put("id", str(row, "id"));
        item.put("oddNumber", str(row, "oddNumber"));
        return item;
    }

    @SuppressWarnings("unchecked")
    private String goodsName(Map<String, Object> row) {
        Map<String, Object> shopMaterial = asMap(row.get("shopMaterial"));
        Map<String, Object> material = asMap(shopMaterial.get("materialMation"));
        String name = str(material, "name");
        if (StrUtil.isNotBlank(name)) {
            return name;
        }
        Map<String, Object> norms = asMap(row.get("normsMation"));
        return str(norms, "name");
    }

    @SuppressWarnings("unchecked")
    private String storeName(Map<String, Object> row) {
        Map<String, Object> store = asMap(row.get("storeMation"));
        return str(store, "name");
    }

    private String stateLabel(String state) {
        if (StrUtil.isBlank(state)) {
            return "";
        }
        return STATE_LABELS.getOrDefault(state, "状态" + state);
    }

    private String fenToYuan(Object fen) {
        if (fen == null || StrUtil.isBlank(String.valueOf(fen))) {
            return "";
        }
        try {
            BigDecimal yuan = new BigDecimal(String.valueOf(fen))
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            return "¥" + yuan.toPlainString();
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object raw) {
        if (raw instanceof Map) {
            return (Map<String, Object>) raw;
        }
        if (raw == null) {
            return Collections.emptyMap();
        }
        try {
            return JSONUtil.parseObj(JSONUtil.toJsonStr(raw));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String str(Map<String, Object> map, String... keys) {
        if (map == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            Object v = map.get(key);
            if (v != null && StrUtil.isNotBlank(String.valueOf(v))) {
                return String.valueOf(v).trim();
            }
        }
        return "";
    }

    private String prefix(String label, String value) {
        if (StrUtil.isBlank(value)) {
            return "";
        }
        if (value.startsWith("¥")) {
            return value;
        }
        return label + "：" + value;
    }

    private String joinMeta(String... parts) {
        if (parts == null) {
            return "";
        }
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            if (StrUtil.isNotBlank(p)) {
                list.add(p);
            }
        }
        return CollectionUtil.isEmpty(list) ? "" : String.join(" · ", list);
    }
}
