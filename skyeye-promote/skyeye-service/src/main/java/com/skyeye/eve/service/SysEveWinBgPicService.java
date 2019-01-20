package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinBgPicService {

	public void querySysEveWinBgPicList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinBgPicMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinBgPicMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinBgPicMationByCustom(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveWinBgPicCustomList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinBgPicMationCustomById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
