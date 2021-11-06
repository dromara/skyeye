/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: DsFormPageTypeService
 * @Description: 动态表单页面分类业务层
 * @author: skyeye云系列--郑杰
 * @date: 2021/11/6 23:33
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormPageTypeService {

	void queryDsFormPageTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertDsFormPageType(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryDsFormPageTypeByParentId(InputObject inputObject, OutputObject outputObject) throws Exception;

	void updateDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void delDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
