/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.pro.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName: ISysEveUserStaffTimeRest
 * @Description: 员工考勤班次服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/30 9:32
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffTimeRest {

    /**
     * 根据考勤班次id查询员工绑定的考勤班次列表
     *
     * @param timeId 考勤班次id
     */
    @PostMapping("/querySysEveUserStaffTimeListByTimeId")
    String querySysEveUserStaffTimeListByTimeId(@RequestParam("timeId") String timeId);

    /**
     * 批量统计各班次绑定员工数
     *
     * @param params 参数信息：timeIds 班次id，多个逗号隔开
     */
    @PostMapping("/countSysEveUserStaffTimeByTimeIds")
    String countSysEveUserStaffTimeByTimeIds(Map<String, Object> params);
}
