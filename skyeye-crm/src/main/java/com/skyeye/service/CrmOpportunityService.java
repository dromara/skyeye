/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmOpportunityService {

    public void queryCrmOpportunityList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCrmOpportunityMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOpportunityMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteOpportunityMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDiscussNumsList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertOpportunityDiscussMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOpportunityListUseToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyCrmOpportunityList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDiscussReplyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOpportunityToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOpportunityMationByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOpportunityToConmunicate(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editOpportunityToQuotedPrice(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editOpportunityToTender(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editOpportunityToNegotiate(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editOpportunityToTurnover(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editOpportunityToLosingTable(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editOpportunityToLayAside(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOpportunityProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
