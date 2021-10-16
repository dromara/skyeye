/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: InoutitemService
 * @Description: 收支项目管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/21 17:14
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface InoutitemService {

    public void queryInoutitemByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertInoutitem(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryInoutitemByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInoutitemListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;
}
