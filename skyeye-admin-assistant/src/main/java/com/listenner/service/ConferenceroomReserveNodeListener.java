/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.ConferenceRoomReserveDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ConferenceroomReserveNodeListener
 * @Description: 会议室预定申请处理类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ConferenceroomReserveNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 会议室预定关联的工作流的key
	 */
	private static final String ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_CONFERENCEROOM_USE_PAGE.getKey();
	
	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ConferenceRoomReserveDao conferenceRoomReserveDao = SpringUtils.getBean(ConferenceRoomReserveDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取会议室预定表信息
		Map<String, Object> map = conferenceRoomReserveDao.queryConferenceRoomReserveByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取会议室数据");
		}
		String id = map.get("id").toString();
		//服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			int approvalState = 2;//审核状态-审核通过
			int reserveState;//预定状态-1、预定成功 2、预定失败
			//获取该会议室预定时间段内已被其他人预定的数据
			List<Map<String, Object>> beans = conferenceRoomReserveDao.queryConferenceRoomReserveListByTime(map);
			if(beans == null || beans.isEmpty()){
				reserveState = 1;
			}else{
				reserveState = 2;
			}
			map.put("approvalState", approvalState);
			map.put("reserveState", reserveState);
			conferenceRoomReserveDao.updateConferenceRoomReserveStateById(map);
		}else{
			map.put("approvalState", 3);//审核不通过
			map.put("reserveState", 2);//预定失败
			conferenceRoomReserveDao.updateConferenceRoomReserveStateById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_CONFERENCEROOM_USE_PAGE_KEY).editApplyMationInActiviti(id);
	}

}
