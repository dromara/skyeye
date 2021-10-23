/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: MailGroupService
 * @Description: 通讯录分组管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/23 12:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MailGroupService {

    public void queryMailMationTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertMailMationType(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteMailMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMailMationTypeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editMailMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMailMationTypeListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

}
