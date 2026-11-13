/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;

/**
 * @ClassName: NoticeDispatchService
 * @Description: 多渠道通知分发服务
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface NoticeDispatchService {

    void dispatch(NoticeDispatchBody body);

    void dispatchNotice(InputObject inputObject, OutputObject outputObject);

    /** 小程序消息订阅页：已启用且配置了平台模板号的模板列表（按模板 ID 去重） */
    void queryMiniSubscribeTemplateList(InputObject inputObject, OutputObject outputObject);

    /** 当前用户上报小程序订阅授权结果 */
    void saveMiniSubscribeStatus(InputObject inputObject, OutputObject outputObject);

    /** 发送成功或 43101 后消耗订阅额度---供其他微服务调用 */
    void consumeMiniSubscribeStatus(InputObject inputObject, OutputObject outputObject);

}
