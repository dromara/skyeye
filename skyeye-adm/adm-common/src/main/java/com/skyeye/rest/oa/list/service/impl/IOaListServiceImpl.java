/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.oa.list.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.adm.schedule.rest.IScheduleAgencyRest;
import com.skyeye.rest.flowable.task.rest.IFlowableTaskRest;
import com.skyeye.rest.oa.list.service.IOaListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class IOaListServiceImpl implements IOaListService {

    @Autowired
    private IFlowableTaskRest iFlowableTaskRest;

    @Autowired
    private IScheduleAgencyRest iScheduleAgencyRest;

    @Override
    public Map<String, Object> queryTodoList(String type, int page, int limit) {
        String listType = StrUtil.blankToDefault(type, "pending").trim().toLowerCase(Locale.ROOT);
        Map<String, Object> params = new HashMap<>();
        params.put("page", Math.max(page, 1));
        params.put("limit", Math.max(limit, 1));
        params.put("keyword", "");

        ResultEntity entity;
        if ("initiated".equals(listType)) {
            entity = fetch(() -> iFlowableTaskRest.queryStartProcessNotSubByUserId(params));
        } else if ("schedule".equals(listType)) {
            entity = fetch(() -> iScheduleAgencyRest.queryMyAgencyList(params));
        } else {
            listType = "pending";
            entity = fetch(() -> iFlowableTaskRest.queryUserAgencyTasksListByUserId(params));
        }

        List<Map<String, Object>> rows = entity == null || entity.getRows() == null
            ? Collections.emptyList() : entity.getRows();
        int total = entity == null || entity.getTotal() == null ? rows.size() : entity.getTotal();

        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }
            items.add(slimItem(listType, row));
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
            throw new CustomException(StrUtil.blankToDefault(e.getMessage(), "查询 OA 列表失败"));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> slimItem(String type, Map<String, Object> row) {
        Map<String, Object> item = new LinkedHashMap<>();
        if ("schedule".equals(type)) {
            item.put("title", str(row, "name", "title"));
            item.put("subtitle", joinMeta(str(row, "startTime"), str(row, "endTime")));
            item.put("id", str(row, "id"));
            return item;
        }
        Map<String, Object> process = asMap(row.get("processMation"));
        String title = str(process, "title");
        if (StrUtil.isBlank(title)) {
            title = str(row, "processDefinitionName", "name", "taskName");
        }
        item.put("title", title);
        if ("pending".equals(type)) {
            item.put("subtitle", joinMeta(
                prefix("申请人", str(process, "createName")),
                prefix("节点", str(row, "taskName"))));
            item.put("processInstanceId", str(row, "processInstanceId"));
            item.put("taskId", str(row, "taskId"));
        } else {
            String applyTime = str(process, "createTime");
            if (StrUtil.isBlank(applyTime)) {
                applyTime = str(row, "createTime");
            }
            item.put("subtitle", prefix("申请时间", applyTime));
            item.put("processInstanceId", str(row, "processInstanceId", "id"));
        }
        item.put("id", str(row, "taskId", "id", "processInstanceId"));
        return item;
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
