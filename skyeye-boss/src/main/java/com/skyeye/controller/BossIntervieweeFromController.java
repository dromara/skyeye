/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.service.BossIntervieweeFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @ClassName: BossIntervieweeFromController
 * @Description: 面试者来源管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/7 13:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class BossIntervieweeFromController {

    @Autowired
    private BossIntervieweeFromService bossIntervieweeFromService;

}
