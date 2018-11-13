package com.skyeye.smprogram.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmPropertyValueService {

	public void queryRmPropertyValueList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmPropertyValueMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmPropertyValueMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
