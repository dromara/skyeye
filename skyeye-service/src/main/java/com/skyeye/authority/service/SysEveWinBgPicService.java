package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinBgPicService {

	public void querySysEveWinBgPicList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinBgPicMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinBgPicMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
