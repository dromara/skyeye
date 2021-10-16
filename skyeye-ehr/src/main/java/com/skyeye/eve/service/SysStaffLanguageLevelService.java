/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffLanguageLevelService {

	public void querySysStaffLanguageLevelList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertSysStaffLanguageLevelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySysStaffLanguageLevelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void downSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void upSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySysStaffLanguageLevelUpMation(InputObject inputObject, OutputObject outputObject) throws Exception;
}
