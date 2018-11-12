package com.skyeye.smprogram.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SmProjectPageModeService {

	public void queryProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
