package com.skyeye.smprogram.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SmProjectPageService {

	public void queryProPageMationByProIdList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertProPageMationByProId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySmProjectPageMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSmProjectPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
