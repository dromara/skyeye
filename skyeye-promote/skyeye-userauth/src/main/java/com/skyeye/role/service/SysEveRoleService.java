/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.role.entity.Role;

public interface SysEveRoleService extends SkyeyeBusinessService<Role> {

    /**
     * 查询全部角色（不分页，下拉选用）
     */
    void queryAllSysRoleList(InputObject inputObject, OutputObject outputObject);

    void querySysRoleBandMenuList(InputObject inputObject, OutputObject outputObject);

    void querySysRoleBandAppMenuList(InputObject inputObject, OutputObject outputObject);

    void editSysRoleAppMenuById(InputObject inputObject, OutputObject outputObject);

    void editSysRolePCAuth(InputObject inputObject, OutputObject outputObject);
}
