/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @Author: 奈何繁华如云烟
 * @Description: TODO
 * @Date: 2019/9/16 21:23
 */
public interface MemberService {
    public void queryMemberByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertMember(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMemberById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteMemberById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editMemberById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editMemberByEnabled(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editMemberByNotEnabled(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMemberByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMemberListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;
}
