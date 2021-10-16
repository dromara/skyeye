/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface AppWorkPageService {

	public void queryAppWorkPageList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertAppWorkPageMation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAppWorkPageListById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkPageUpById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkPageDownById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkPageTitleById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteAppWorkPageById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkUpById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkDownById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editAppWorkSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
