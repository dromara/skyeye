/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: SysQuartzService
 * @Description: 定时任务service接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/25 22:01
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface SysQuartzService {

    public void querySystemQuartzList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyTaskQuartzList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void runSystemQuartz(InputObject inputObject, OutputObject outputObject) throws Exception;
}
