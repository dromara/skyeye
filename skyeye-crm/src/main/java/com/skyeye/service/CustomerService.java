/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CustomerService {

    public void queryCustomerList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void insertCustomerMation(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void deleteCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCustomerMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCustomerListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryCustomerNumsDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCustomerListTableToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyConscientiousList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyCreateList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInternationalCustomerList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
