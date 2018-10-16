package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveIconService {

	public void querySysIconList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysIconMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysIconMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	
	
	
}
