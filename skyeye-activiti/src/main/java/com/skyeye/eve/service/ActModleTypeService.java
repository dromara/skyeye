/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ActModleTypeService {

	public void insertActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectAllActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertActModleByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActModleTypeNameById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActModleById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpActModleById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownActModleById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectActModleMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActModleMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActModleMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActModleMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActModleTypeOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActModleTypeOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryActModleUpStateByUpStateType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllDsForm(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormMationBySequenceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryActModleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryHotActModleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;
}
