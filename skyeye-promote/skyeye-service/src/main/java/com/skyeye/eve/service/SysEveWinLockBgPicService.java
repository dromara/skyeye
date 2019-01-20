package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinLockBgPicService {

	public void querySysEveWinLockBgPicList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinLockBgPicMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinLockBgPicMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinBgPicMationByCustom(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveWinBgPicCustomList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinBgPicMationCustomById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
