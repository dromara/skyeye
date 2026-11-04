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

}
