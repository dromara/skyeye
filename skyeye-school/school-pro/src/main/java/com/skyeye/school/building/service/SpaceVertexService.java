package com.skyeye.school.building.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.SpaceVertex;

import java.util.List;
import java.util.Map;

/**
 * 空间轮廓顶点
 */
public interface SpaceVertexService extends SkyeyeBusinessService<SpaceVertex> {

    void writeSpaceOutline(InputObject inputObject, OutputObject outputObject);

    void querySpaceVertexByOwner(InputObject inputObject, OutputObject outputObject);

    void deleteByOwners(Integer ownerType, List<String> ownerIds);

    Map<String, List<SpaceVertex>> queryGroupByOwner(Integer ownerType, List<String> ownerIds);
}
