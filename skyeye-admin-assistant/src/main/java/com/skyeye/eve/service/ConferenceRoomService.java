/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: ConferenceRoomService
 * @Description: 会议室管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 15:21
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ConferenceRoomService {

	public void selectAllConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteConferenceRoomById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateConferenceRoomNormalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateConferenceRoomRepairById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateConferenceRoomScrapById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectConferenceRoomDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectConferenceRoomListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;

}
