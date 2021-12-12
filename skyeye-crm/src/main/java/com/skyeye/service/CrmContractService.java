/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmContractService {

    public void queryCrmContractList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryMyCrmContractList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryDepartmentListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void insertCrmContractMation(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCrmContractMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCrmContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCrmContractListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractToPerform(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractToClose(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractToShelve(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractToRecovery(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void deleteCrmContractById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCrmContractToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;
    
}
