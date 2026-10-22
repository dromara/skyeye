/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.oa.board.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.adm.schedule.rest.IScheduleAgencyRest;
import com.skyeye.rest.flowable.task.rest.IFlowableTaskRest;
import com.skyeye.rest.oa.board.service.IOaBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 对齐仪表盘 PendingProcessWidget：三次分页查询只取 total。
 */
@Service
public class IOaBoardServiceImpl implements IOaBoardService {

    private static final int PAGE = 1;
    private static final int LIMIT = 1;

    @Autowired
    private IFlowableTaskRest iFlowableTaskRest;

    @Autowired
    private IScheduleAgencyRest iScheduleAgencyRest;

    @Override
    public Map<String, Object> queryTodoBoardCounts() {
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("page", PAGE);
        pageParams.put("limit", LIMIT);
        pageParams.put("keyword", "");

        Map<String, Object> counts = new LinkedHashMap<>();
        // 与仪表盘一致：单项失败不拖垮整板
        counts.put("pending", safeTotal(() -> iFlowableTaskRest.queryUserAgencyTasksListByUserId(pageParams)));
        counts.put("initiated", safeTotal(() -> iFlowableTaskRest.queryStartProcessNotSubByUserId(pageParams)));
        counts.put("schedule", safeTotal(() -> iScheduleAgencyRest.queryMyAgencyList(pageParams)));
        return counts;
    }

    private int safeTotal(Supplier<String> loader) {
        try {
            ResultEntity entity = ExecuteFeignClient.get(loader);
            if (entity == null || entity.getTotal() == null) {
                return 0;
            }
            return entity.getTotal();
        } catch (Exception e) {
            // 单项失败记 0，由上层 hint；登录失效等仍向上抛便于 Agent 提示重登
            if (e instanceof CustomException) {
                String msg = e.getMessage();
                if (StrUtil.containsIgnoreCase(msg, "登录") || StrUtil.containsIgnoreCase(msg, "TIMEOUT")
                    || StrUtil.containsIgnoreCase(msg, "凭证")) {
                    throw (CustomException) e;
                }
            }
            return 0;
        }
    }
}
