/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ProProjectService {

    public void queryAllProProjectList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryMyProProjectList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void insertProProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryAllProProjectToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryProProjectMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryProProjectMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editProProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editProProjectMationToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteProProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProjectProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProjectProcessToNullify(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProjectProcessToExecute(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProjectProcessToProAppointShowById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProjectProcessToProAppointById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProjectProcessToPerFectShowById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProjectProcessToPerFectById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editProjectMationInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;
}
