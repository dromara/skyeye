/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.xxljob;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.schedule.entity.AutoScheduleTask;
import com.skyeye.schedule.service.AutoScheduleTaskService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 由 SysQuartz 按自动化定时任务注册的 XXL 子任务：根据 objectId(任务 id) 触发执行。
 * 任务参数为 JSON：objectId、userId、tenantId。
 * Handler 名须与 {@code QuartzConstants.AUTO_SCHEDULE_TASK_EXECUTE.serviceName} 一致。
 */
@Slf4j
@Component
public class AutoScheduleTaskExecuteService {

    @Autowired
    private AutoScheduleTaskService autoScheduleTaskService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("autoScheduleTaskExecuteService")
    public void executeScheduleTask() {
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            log.warn("自动化定时任务执行：执行参数为空");
            return;
        }
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String taskId = paramMap.get("objectId");
        if (StrUtil.isBlank(taskId)) {
            log.warn("自动化定时任务执行：objectId 为空");
            return;
        }
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            AutoScheduleTask task = autoScheduleTaskService.selectById(taskId);
            if (ObjectUtil.isEmpty(task)) {
                log.warn("自动化定时任务[{}]不存在，跳过执行", taskId);
                return;
            }
            if (!EnableEnum.ENABLE_USING.getKey().equals(task.getEnabled())) {
                log.warn("自动化定时任务[{}]未启用，跳过执行", taskId);
                return;
            }
            autoScheduleTaskService.executeScheduleTask(taskId);
        } catch (Exception e) {
            log.warn("自动化定时任务[{}]定时执行失败", taskId, e);
        } finally {
            if (tenantEnable) {
                TenantContext.clear();
            }
        }
    }
}
