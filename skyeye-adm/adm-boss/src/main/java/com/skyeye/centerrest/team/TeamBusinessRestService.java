/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.centerrest.team;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @ClassName: TeamBusinessRestService
 * @Description: 业务团队远程调用
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface TeamBusinessRestService {

    /**
     * 转让全部团队经理身份
     *
     * @param params fromUserId: 原团队经理用户id; toUserId: 新团队经理用户id
     * @return
     */
    @PostMapping("/transferAllChargeUser")
    String transferAllChargeUser(Map<String, Object> params);

}
