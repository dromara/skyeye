package com.skyeye.smprogram.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmGroupMemberService {

	public void queryRmGroupMemberList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmGroupMemberMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmGroupMemberById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmGroupMemberMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberAndPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
