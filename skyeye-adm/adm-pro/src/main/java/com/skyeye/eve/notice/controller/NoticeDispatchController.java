/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.notice.service.NoticeDispatchService;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: NoticeDispatchController
 * @Description: 多渠道通知分发控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "多渠道通知分发", tags = "多渠道通知分发", modelName = "消息通知")
public class NoticeDispatchController {

    @Autowired
    private NoticeDispatchService noticeDispatchService;

    @ApiOperation(id = "dispatchNotice", value = "按类型编码分发多渠道通知---供其他微服务调用", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = NoticeDispatchBody.class)
    @RequestMapping("/post/NoticeDispatchController/dispatchNotice")
    public void dispatchNotice(InputObject inputObject, OutputObject outputObject) {
        noticeDispatchService.dispatchNotice(inputObject, outputObject);
    }

}
