/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyJobScoreService {

    public void queryCompanyJobScoreList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertCompanyJobScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCompanyJobScoreMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void enableCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void disableCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryEnableCompanyJobScoreList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCompanyJobScoreDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
}
