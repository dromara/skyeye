/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffDataDictionaryService {

	public void querySysStaffDataDictionaryList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertSysStaffDataDictionaryMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySysStaffDataDictionaryMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void downSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void upSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySysStaffDataDictionaryUpMation(InputObject inputObject, OutputObject outputObject) throws Exception;
}
