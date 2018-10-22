package com.skyeye.smprogram.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmTypeService {

	public void queryRmTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
