/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: SysQuartzRunHistoryService
 * @Description: 系统定时任务启动历史服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/29 11:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysQuartzRunHistoryService {

    void querySysQuartzRunHistoryByQuartzId(InputObject inputObject, OutputObject outputObject) throws Exception;

    String startSysQuartzRun(String quartzId) throws Exception;

    void endSysQuartzRun(String id, Integer state) throws Exception;

}
