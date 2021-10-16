/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.quartz.consumer;

import com.skyeye.eve.entity.quartz.SysQuartz;

public interface TaskMateService {

    public void call(SysQuartz sysQuartz) throws Exception;

}
