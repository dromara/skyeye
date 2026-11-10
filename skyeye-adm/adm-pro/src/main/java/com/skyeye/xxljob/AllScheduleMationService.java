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
import com.skyeye.eve.schedule.dao.ScheduleDayDao;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import com.skyeye.eve.schedule.service.ScheduleDayService;
import com.skyeye.eve.service.IQuartzService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AllScheduleMationService
 * @Description: 通知所有人日程提醒
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 15:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class AllScheduleMationService {

    @Autowired
    private ScheduleDayDao scheduleDayDao;

    @Autowired
    private ScheduleDayService scheduleDayService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private NoticeDispatchService noticeDispatchService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @XxlJob("allScheduleMationService")
    public void call() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String scheduleId = paramMap.get("objectId");
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        ScheduleDay scheduleDay = scheduleDayService.selectById(scheduleId);
        List<Map<String, Object>> users = scheduleDayDao.queryAllUserAndEmailISNotNull(tenantId);
        List<String> receiveUserIds = users.stream()
            .map(user -> user.get("userId").toString())
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (!receiveUserIds.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", scheduleDay.getId());
            params.put("title", scheduleDay.getName());
            params.put("startTime", scheduleDay.getStartTime());
            params.put("endTime", scheduleDay.getEndTime());
            params.put("remark", StrUtil.blankToDefault(scheduleDay.getRemark(), StrUtil.EMPTY));

            NoticeDispatchBody body = new NoticeDispatchBody();
            body.setTypeCode("SCHEDULE_MESSAGE");
            body.setReceiveUserIds(receiveUserIds);
            body.setParams(params);
            body.setCreateUserId(CommonConstants.ADMIN_USER_ID);
            body.setObjectData(JSONUtil.toJsonStr(scheduleDay));
            // 按人补充 userName（模板可使用）
            Map<String, Map<String, Object>> extras = new HashMap<>();
            for (Map<String, Object> user : users) {
                String userId = user.get("userId").toString();
                Map<String, Object> extra = new HashMap<>();
                if (user.get("email") != null) {
                    extra.put("email", user.get("email"));
                }
                extras.put(userId, extra);
            }
            body.setReceiverExtras(extras);
            // 批量分发时每人参数中的 userName 在 dispatch 内从用户信息补全
            noticeDispatchService.dispatch(body);
        }
        scheduleDayService.editScheduleStateById(scheduleId, ScheduleState.REMINDED_SCHEDULE.getKey());
        iQuartzService.stopAndDeleteTaskQuartz(paramMap.get("objectId"));
    }

}
