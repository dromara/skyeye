package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinThemeColorService {

	public void querySysEveWinThemeColorList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinThemeColorMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveWinThemeColorMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
