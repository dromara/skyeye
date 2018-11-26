package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysTAreaService {

	public void querySysTAreaList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysTAreaProvinceList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysTAreaCityList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysTAreaChildAreaList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysTAreaTownShipList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
