/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 代码生成器模板使用历史服务接口类
 *
 * @auther 卫志强 QQ：598748873@qq.com，微信：wzq_598748873
 * @desc 禁止商用
 */
public interface CodeModelHistoryService {

	public void queryCodeModelHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCodeModelHistoryCreate(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void downloadCodeModelHistory(InputObject inputObject, OutputObject outputObject) throws Exception;

}
