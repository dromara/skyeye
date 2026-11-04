/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.notice.service.NoticeDispatchService;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import com.skyeye.eve.schedule.classenum.ScheduleState;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import com.skyeye.eve.schedule.service.ScheduleDayService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.IQuartzService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MyScheduleDayMationService
 * @Description: 我的日程提醒
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 15:23
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class MyScheduleDayMationService {

    @Autowired
    private ScheduleDayService scheduleDayService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private NoticeDispatchService noticeDispatchService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @XxlJob("myScheduleDayMationService")
    public void call() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String userId = paramMap.get("userId");
        String scheduleId = paramMap.get("objectId");
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            Map<String, Object> userMation = iAuthUserService.queryDataMationById(userId);
            ScheduleDay scheduleDay = scheduleDayService.selectById(scheduleId);

            Map<String, Object> params = new HashMap<>();
            params.put("userName", userMation.getOrDefault("userName", StrUtil.EMPTY));
            params.put("title", scheduleDay.getName());
            params.put("startTime", scheduleDay.getStartTime());
            params.put("endTime", scheduleDay.getEndTime());
            params.put("remark", StrUtil.blankToDefault(scheduleDay.getRemark(), StrUtil.EMPTY));

            NoticeDispatchBody body = new NoticeDispatchBody();
            body.setTypeCode("SCHEDULE_MESSAGE");
            body.setReceiveUserIds(Arrays.asList(userId));
            body.setParams(params);
            body.setCreateUserId(CommonConstants.ADMIN_USER_ID);
            noticeDispatchService.dispatch(body);
        } catch (Exception e) {
            log.error("日程提醒失败", e);
        } finally {
            scheduleDayService.editScheduleStateById(scheduleId, ScheduleState.REMINDED_SCHEDULE.getKey());
            iQuartzService.stopAndDeleteTaskQuartz(scheduleId);
        }
    }

}
