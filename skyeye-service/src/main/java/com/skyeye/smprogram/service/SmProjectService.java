package com.skyeye.smprogram.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SmProjectService {

	public void querySmProjectList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSmProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSmProjectById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySmProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupMemberMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
