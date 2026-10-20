package com.skyeye.school.building.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.RoomOccupant;

import java.util.List;
import java.util.Map;

/**
 * 房间人员
 */
public interface RoomOccupantService extends SkyeyeBusinessService<RoomOccupant> {

    void queryRoomOccupantByFloorId(InputObject inputObject, OutputObject outputObject);

    void deleteByFloorInfoIds(List<String> floorInfoIds);

    Map<String, List<RoomOccupant>> queryGroupByFloorInfoIds(List<String> floorInfoIds);

    void fillPersonNames(List<RoomOccupant> occupants);
}
