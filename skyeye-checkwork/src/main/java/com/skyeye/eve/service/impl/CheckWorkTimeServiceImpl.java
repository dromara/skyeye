/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CheckWorkTimeDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.CheckWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckWorkTimeServiceImpl implements CheckWorkTimeService {
	
	@Autowired
	private CheckWorkTimeDao checkWorkTimeDao;
	
	@Autowired
	private SysEveUserStaffDao sysEveUserStaffDao;
	
	public static enum state{
        START_UP(1, "启动"),
        START_DOWN(2, "禁用"),
        START_DELETE(3, "删除");
        private int state;
        private String name;
        state(int state, String name){
            this.state = state;
            this.name = name;
        }
        public int getState() {
            return state;
        }
        
        public String getName() {
            return name;
        }
    }

	/**
	 * 
	 * Title: queryAllCheckWorkTimeList
	 * Description: 查询所有考勤班次列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#queryAllCheckWorkTimeList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryAllCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = checkWorkTimeDao.queryAllCheckWorkTimeList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: insertCheckWorkTimeMation
	 * Description: 员工考勤班次信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#insertCheckWorkTimeMation(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCheckWorkTimeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String timeId = ToolUtil.getSurFaceId();
		map.put("id", timeId);
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("createId", inputObject.getLogParams().get("id"));
		checkWorkTimeDao.insertCheckWorkTimeMation(map);
		creatWeekDay(map.get("weekDay").toString(), timeId);
	}

	private void creatWeekDay(String weekDay, String timeId) throws Exception {
		checkWorkTimeDao.deleteWeekDayByTimeId(timeId);
		// 处理数据
		List<Map<String, Object>> jArray = JSONUtil.toList(weekDay, null);
		List<Map<String, Object>> days = new ArrayList<>();
		for(int i = 0; i < jArray.size(); i++){
			Map<String, Object> bean = jArray.get(i);
			bean.put("timeId", timeId);
			days.add(bean);
		}
		checkWorkTimeDao.creatWeekDay(days);
	}

	/**
	 * 
	 * Title: queryCheckWorkTimeMationToEdit
	 * Description: 编辑员工考勤班次信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#queryCheckWorkTimeMationToEdit(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryCheckWorkTimeMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = checkWorkTimeDao.queryCheckWorkTimeMationToEdit(id);
		if(bean != null && !bean.isEmpty()){
			List<Map<String, Object>> days = checkWorkTimeDao.queryWeekDayByTimeId(id);
			bean.put("days", days);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("The data does not exist.");
		}
	}

	/**
	 * 
	 * Title: editCheckWorkTimeMationById
	 * Description: 编辑员工考勤班次信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#editCheckWorkTimeMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editCheckWorkTimeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = checkWorkTimeDao.queryCheckWorkTimeMationToEdit(id);
		if(bean != null && !bean.isEmpty()){
			int nowState = Integer.parseInt(bean.get("state").toString());
			// 删除的不能编辑
			if(state.START_DELETE.getState() == nowState){
				outputObject.setreturnMessage("The data state is changed.");
			}else{
				map.put("lastUpdateId", inputObject.getLogParams().get("id"));
				map.put("lastUpdateTime", DateUtil.getTimeAndToString());
				checkWorkTimeDao.editCheckWorkTimeMationById(map);
				creatWeekDay(map.get("weekDay").toString(), id);
			}
		}else{
			outputObject.setreturnMessage("The data does not exist.");
		}
	}

	/**
	 * 
	 * Title: deleteCheckWorkTimeMationById
	 * Description: 删除员工考勤班次信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#deleteCheckWorkTimeMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteCheckWorkTimeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		checkWorkTimeDao.editCheckWorkTimeStateMationById(id, state.START_DELETE.getState());
		checkWorkTimeDao.deleteWeekDayByTimeId(id);
	}

	/**
	 * 
	 * Title: queryEnableCheckWorkTimeList
	 * Description: 查询启用的考勤班次列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#queryEnableCheckWorkTimeList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = checkWorkTimeDao.queryEnableCheckWorkTimeList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}

	/**
	 * 
	 * Title: queryCheckWorkTimeListByLoginUser
	 * Description: 获取当前登陆人的考勤班次
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.CheckWorkTimeService#queryCheckWorkTimeListByLoginUser(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject) throws Exception {
		String staffId = inputObject.getLogParams().get("staffId").toString();
		List<Map<String, Object>> workTime = sysEveUserStaffDao.queryStaffCheckWorkTimeRelationNameByStaffId(staffId);
		for(Map<String, Object> bean: workTime){
			List<Map<String, Object>> days = checkWorkTimeDao.queryWeekDayByTimeId(bean.get("timeId").toString());
			bean.put("days", days);
		}
		outputObject.setBeans(workTime);
		outputObject.settotal(workTime.size());
	}

	/**
	 * 根据指定年月获取所有的考勤班次的信息以及工作日信息等
	 *
	 * @param pointMonthDate 指定年月，格式为yyyy-MM
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> getAllCheckWorkTime(String pointMonthDate) throws Exception {
		List<Map<String, Object>> workTime = checkWorkTimeDao.queryAllStaffCheckWorkTime();
		// 获取上个月的所有日期
		List<String> lastMonthDays = DateUtil.getMonthFullDay(Integer.parseInt(pointMonthDate.split("-")[0]),
				Integer.parseInt(pointMonthDate.split("-")[1]));
		for(Map<String, Object> bean: workTime){
			List<Map<String, Object>> days = checkWorkTimeDao.queryWeekDayByTimeId(bean.get("timeId").toString());
			List<String> workDays = new ArrayList<>();
			for (String day : lastMonthDays) {// 周几
				int weekDay = DateUtil.getWeek(day);
				int weekType = DateUtil.getWeekType(day);
				List<Map<String, Object>> simpleDay = days.stream().filter(item -> Integer.parseInt(item.get("day").toString()) == weekDay)
						.collect(Collectors.toList());
				if (simpleDay != null && !simpleDay.isEmpty()) {
					// 如果今天是需要考勤的日期
					int dayType = Integer.parseInt(simpleDay.get(0).get("type").toString());
					if (weekType == 1 && dayType == 2) {
						// 如果获取到的日期是双周，但考勤班次里面是单周，则不做任何操作
					} else {
						// 单周或者非每周的当天都上班
						workDays.add(day);
					}
				}
			}
			bean.put("workDays", workDays);
			bean.put("days", days);
			bean.put("startTime", String.format(Locale.ROOT, "%s%s", bean.get("startTime").toString(), ":00"));
			bean.put("endTime", String.format(Locale.ROOT, "%s%s", bean.get("endTime").toString(), ":00"));
		}
		return workTime;
	}

}
