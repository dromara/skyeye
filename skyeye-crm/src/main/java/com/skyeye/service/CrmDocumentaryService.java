/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmDocumentaryService {

    public void queryMyDocumentaryList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryAllDocumentaryList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void insertDocumentary(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void editDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void deleteDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryDocumentaryMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

}
