package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ApiService {

    public void queryAllSysEveReqMapping(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySysEveReqMappingMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryLimitRestrictions(InputObject inputObject, OutputObject outputObject) throws Exception;
}
