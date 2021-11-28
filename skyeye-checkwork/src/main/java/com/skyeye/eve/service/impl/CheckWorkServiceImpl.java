/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CheckWorkConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CheckWorkDao;
import com.skyeye.eve.dao.CheckWorkOvertimeDao;
import com.skyeye.eve.dao.CheckWorkTimeDao;
import com.skyeye.eve.service.*;
import com.skyeye.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: CheckWorkServiceImpl
 * @Description: 考勤相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:11
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class CheckWorkServiceImpl implements CheckWorkService {

	private static Logger LOGGER = LoggerFactory.getLogger(CheckWorkServiceImpl.class);

	@Autowired
	private CheckWorkDao checkWorkDao;
	
	@Autowired
	private CheckWorkTimeDao checkWorkTimeDao;

	@Autowired
	private CheckWorkBusinessTripService checkWorkBusinessTripService;

	@Autowired
	private CheckWorkLeaveService checkWorkLeaveService;

	@Autowired
	private CheckWorkOvertimeService checkWorkOvertimeService;

	@Autowired
	private CheckWorkOvertimeDao checkWorkOvertimeDao;

	@Autowired
	private SysScheduleCommonService sysScheduleCommonService;

	public static enum CheckTypeFrom{
		CHECT_BTN_FROM_TIMEID(1, "根据班次id计算考勤按钮的显示状态"),
		CHECT_BTN_FROM_OVERTIME(2, "根据加班日计算考勤按钮的显示状态");
		private int type;
		private String name;
		CheckTypeFrom(int type, String name){
			this.type = type;
			this.name = name;
		}
		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}
	}
	
	/**
	 * 
	     * @Title: insertCheckWorkStartWork
	     * @Description: 上班打卡
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void insertCheckWorkStartWork(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String timeId = map.get("timeId").toString();
		String staffId = user.get("staffId").toString();
		String userId = user.get("id").toString();
		String todayYMD = DateUtil.getYmdTimeAndToString();
		// 1.获取当前用户的考勤班次信息
		Map<String, Object> workTime = getWorkTime(userId, todayYMD, timeId, staffId);
		// 2.获取今天的打卡记录
		String checkInTime = DateUtil.getHmsTimeAndToString();
		Map<String, Object> isAlreadyCheck = checkWorkDao.queryisAlreadyCheck(DateUtil.getYmdTimeAndToString(), user.get("id").toString(), timeId);
		if (isAlreadyCheck == null && DateUtil.compareTimeHMS(checkInTime, workTime.get("clockOut").toString())) {
			// 今日没有打卡，且没有到下班时间，可以进行打卡
			map.put("state", "0");
			if (DateUtil.compareTimeHMS(checkInTime, workTime.get("clockIn").toString())) {
				// 当前打卡时间是否早于上班时间
				// 正常
				map.put("clockInState", "1");
			} else {
				// 迟到
				map.put("clockInState", "2");
			}
			map.put("checkDate", DateUtil.getYmdTimeAndToString());
			map.put("createId", user.get("id"));
			map.put("id", ToolUtil.getSurFaceId());
			map.put("clockIn", checkInTime);
			// 上班打卡IP
			map.put("clockInIp", ToolUtil.getIpByRequest(inputObject.getRequest()));
			checkWorkDao.insertCheckWorkStartWork(map);
		} else if (isAlreadyCheck != null && ToolUtil.isBlank(isAlreadyCheck.get("clockOut").toString())) {
			// 今日已经打过晚卡，不能打早卡
			outputObject.setreturnMessage("今日已经打过晚卡，现在不能打早卡！");
		} else if (!DateUtil.compareTimeHMS(map.get("clockIn").toString(), workTime.get("clockOut").toString())) {
			// 今日没有打卡，已是下班时间，不能进行打卡
			outputObject.setreturnMessage("今日打早卡时间已过，不能进行打卡！");
		} else {
			outputObject.setreturnMessage("今日早卡已打过卡，请不要重复打卡！");
		}
	}

	/**
	 * 获取指定员工指定班次的考勤信息
	 *
	 * @param timeId 班次id
	 * @param staffId 员工id
	 * @return 该班次的上下班时间，时间格式为HH:mm:ss
	 * @throws Exception
	 */
	private Map<String, Object> getWorkTimeByUserMation(String timeId, String staffId) throws Exception{
		// 1.获取指定班次的上下班时间
		Map<String, Object> bean = checkWorkDao.queryStartWorkTime(timeId, staffId);
		if(bean == null || bean.isEmpty()){
			// 您不具备该班次的考勤权限
			throw new CustomException("You do not have the attendance authority for this shift.");
		}
		bean.put("clockIn", bean.get("clockIn").toString() + ":00");
		bean.put("clockOut", bean.get("clockOut").toString() + ":00");
		return bean;
	}

	/**
	 * 
	     * @Title: editCheckWorkEndWork
	     * @Description: 下班打卡
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value = "transactionManager")
	public void editCheckWorkEndWork(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String timeId = map.get("timeId").toString();
		String staffId = user.get("staffId").toString();
		String userId = user.get("id").toString();
		String todayYMD = DateUtil.getYmdTimeAndToString();
		// 1.获取当前用户的考勤班次信息
		Map<String, Object> workTime = getWorkTime(userId, todayYMD, timeId, staffId);
		// 2.获取今天的打卡记录
		Map<String, Object> isAlreadyCheck = checkWorkDao.queryisAlreadyCheck(DateUtil.getYmdTimeAndToString(), userId, timeId);
		map.put("checkDate", DateUtil.getYmdTimeAndToString());
		map.put("createId", inputObject.getLogParams().get("id"));
		if (isAlreadyCheck == null) {
			// 早卡晚卡都没有打，可以打晚卡
			map.put("id", ToolUtil.getSurFaceId());
			map.put("clockOut", DateUtil.getHmsTimeAndToString());
			// 缺早卡
			map.put("state", "4");
			// 上班打卡状态-未打卡
			map.put("clockInState", "3");
			if (DateUtil.compareTimeHMS(map.get("clockOut").toString(), workTime.get("clockOut").toString())) {
				// 当前打卡时间是否早于下班时间
				// 早退
				map.put("clockOutState", "2");
			} else {
				// 正常
				map.put("clockOutState", "1");
			}
			map.put("workHours", "0");
			// 上班和下班打卡IP
			map.put("clockIp", ToolUtil.getIpByRequest(inputObject.getRequest()));
			checkWorkDao.insertCheckWorkEndWork(map);
		} else if (!ToolUtil.isBlank(isAlreadyCheck.get("clockIn").toString())
				&& ToolUtil.isBlank(isAlreadyCheck.get("clockOut").toString())) {
			// 打过早卡，没有打晚卡
			map.put("clockOut", DateUtil.getHmsTimeAndToString());
			// 系统设置的上班时长
			String a = DateUtil.getDistanceHMS(workTime.get("clockOut").toString(), workTime.get("clockIn").toString());
			// 用户的上班时长
			String b = DateUtil.getDistanceHMS(map.get("clockOut").toString(), isAlreadyCheck.get("clockIn").toString());
			// 当前打卡时间是否早于下班时间
			if (DateUtil.compareTimeHMS(a, b)) {
				// 全勤
				map.put("state", "1");
			} else {
				// 工时不足
				map.put("state", "3");
			}
			if (DateUtil.compareTimeHMS(map.get("clockOut").toString(), workTime.get("clockOut").toString())) {
				// 早退
				map.put("clockOutState", "2");
			} else {
				// 正常
				map.put("clockOutState", "1");
			}
			map.put("workHours", (Object) b);
			// 下班打卡IP
			map.put("clockOutIp", ToolUtil.getIpByRequest(inputObject.getRequest()));
			checkWorkDao.editCheckWorkEndWork(map);
		} else {
			// 已经打过晚卡
			outputObject.setreturnMessage("今日已打过晚卡，请不要重复打卡！");
		}
	}

	/**
	 * 
	     * @Title: queryCheckWork
	     * @Description: 我的考勤
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckWork(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = checkWorkDao.queryCheckWork(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryCheckWorkIdByAppealType
	     * @Description: 申诉内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckWorkIdByAppealType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkIdByAppealType(map);
		for(Map<String, Object> bean : beans){
			bean.put("name", bean.get("name").toString() + "考勤[" + bean.get("situation").toString() + "]");
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: querySysEveUserStaff
	     * @Description: 审批人列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveUserStaff(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = checkWorkDao.querySysEveUserStaff(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertCheckWorkAppeal
	     * @Description: 新增申诉
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertCheckWorkAppeal(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("createId", inputObject.getLogParams().get("id"));
		map.put("state", "0");
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		checkWorkDao.insertCheckWorkAppeal(map);
	}

	/**
	 * 
	     * @Title: queryCheckWorkAppeallist
	     * @Description: 我的申诉申请列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckWorkAppeallist(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkAppeallist(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryCheckWorkAppealMyGetlist
	     * @Description: 员工考勤申诉审批列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckWorkAppealMyGetlist(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("approvalId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkAppealMyGetlist(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryStaffCheckWorkDetails
	     * @Description: 回显员工的申诉申请详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryStaffCheckWorkDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkDao.queryStaffCheckWorkDetails(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editStaffCheckWork
	     * @Description: 审批员工的申诉申请
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editStaffCheckWork(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("approvalTime", DateUtil.getTimeAndToString());
		checkWorkDao.editStaffCheckWork(map);
	}

	/**
	 * 
	     * @Title: queryCheckWorkDetails
	     * @Description: 申诉申请详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCheckWorkDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = checkWorkDao.queryCheckWorkDetails(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 判断显示打上班卡或者下班卡
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryCheckWorkTimeToShowButton(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String today = DateUtil.getYmdTimeAndToString();
		String userId = user.get("id").toString();
		String timeId = map.get("timeId").toString();
		String staffId = user.get("staffId").toString();
		String nowTimeHMS = DateUtil.getHmsTimeAndToString();
		// 1.获取当前用户的考勤班次信息
		Map<String, Object> workTime = getWorkTime(userId, today, timeId, staffId);
		if(Integer.parseInt(workTime.get("type").toString()) == CheckTypeFrom.CHECT_BTN_FROM_OVERTIME.getType()){
			timeId = "-";
		}
		// 2.判断显示打上班卡或者下班卡
		Map<String, Object> result = getChectBtn(today, userId, timeId, workTime, nowTimeHMS);
		outputObject.setBean(result);
	}

	/**
	 * 获取当前用户的考勤班次信息
	 *
	 * @param userId 用户id
	 * @param today 指定日期，格式为yyyy-MM-dd(一般为今天的日期)
	 * @param timeId 班次id
	 * @param staffId 员工id
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getWorkTime(String userId, String today, String timeId, String staffId) throws Exception {
		Map<String, Object> workTime;
		// 判断今天是否是加班日
		List<Map<String, Object>> overTimeMation = checkWorkOvertimeDao.queryPassThisDayAndCreateId(userId, today);
		if(overTimeMation != null && !overTimeMation.isEmpty()){
			// 根据加班日判断显示打上班卡或者下班卡
			workTime = overTimeMation.get(0);
			workTime.put("clockIn", workTime.get("clockIn").toString() + ":00");
			workTime.put("clockOut", workTime.get("clockOut").toString() + ":00");
			workTime.put("type", CheckTypeFrom.CHECT_BTN_FROM_OVERTIME.getType());
		}else{
			// 根据考勤班次判断显示打上班卡或者下班卡
			workTime = getWorkTimeByUserMation(timeId, staffId);
			workTime.put("type", CheckTypeFrom.CHECT_BTN_FROM_TIMEID.getType());
		}
		return workTime;
	}

	/**
	 * 判断显示打上班卡或者下班卡
	 *
	 * @param today 指定日期，格式为yyyy-MM-dd(一般为今天的日期)
	 * @param userId 用户id
	 * @param timeId 班次id
	 * @param workTime 考勤班次信息
	 * @param nowTimeHMS 指定日期，格式为HH:mm:ss(一般为当前时间)
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getChectBtn(String today, String userId, String timeId, Map<String, Object> workTime, String nowTimeHMS) throws Exception {
		// 获取今天的打卡记录
		Map<String, Object> isAlreadyCheck = checkWorkDao.queryisAlreadyCheck(today, userId, timeId);
		Integer checkState = getCheckState(isAlreadyCheck, nowTimeHMS, workTime, today);
		Map<String, Object> result = new HashMap<>();
		result.put("isCheck", checkState);
		result.putAll(workTime);
		if (isAlreadyCheck != null && !isAlreadyCheck.isEmpty()) {
			result.put("realClockIn", isAlreadyCheck.get("clockIn").toString());
			result.put("realClockOut", isAlreadyCheck.get("clockOut").toString());
		}
		return result;
	}

	/**
	 * 获取指定日期在规定班次内的打卡状态
	 *
	 * @param isAlreadyCheck 今日打卡信息
	 * @param nowTimeHMS 指定日期，格式为HH:mm:ss
	 * @param workTime 班次考勤信息
	 * @param today 指定日期，格式为yyyy-MM-dd(一般为今天的日期)
	 * @return
	 * @throws Exception
	 */
	private Integer getCheckState(Map<String, Object> isAlreadyCheck, String nowTimeHMS, Map<String, Object> workTime,
								  String today) throws Exception {
		Integer checkState = null;
		if(Integer.parseInt(workTime.get("type").toString()) == CheckTypeFrom.CHECT_BTN_FROM_TIMEID.getType()){
			if(sysScheduleCommonService.judgeISHoliday(today)){
				// 今天不是加班日，但是是节假日，则不显示按钮
				checkState = 4;
				return checkState;
			}
		}
		if (isAlreadyCheck == null && DateUtil.compareTimeHMS(nowTimeHMS, workTime.get("clockOut").toString())) {
			// 今日没有打卡，且没有到下班时间，显示早卡按钮
			checkState = 1;
		} else if (isAlreadyCheck == null && !DateUtil.compareTimeHMS(nowTimeHMS, workTime.get("clockOut").toString())) {
			// 今日没有打卡，已是下班时间，不显示按钮
			checkState = 3;
		} else if (!ToolUtil.isBlank(isAlreadyCheck.get("clockIn").toString()) && ToolUtil.isBlank(isAlreadyCheck.get("clockOut").toString())) {
			// 今日打过早卡没打晚卡，显示晚卡按钮
			checkState = 2;
		} else if (!ToolUtil.isBlank(isAlreadyCheck.get("clockIn").toString()) && !ToolUtil.isBlank(isAlreadyCheck.get("clockOut").toString())) {
			// 今日打过早卡打过晚卡，不显示按钮
			checkState = 4;
		}
		return checkState;
	}

    /**
     * 
         * @Title: queryCheckWorkMationByMonth
         * @Description: 根据月份查询当月的考勤信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryCheckWorkMationByMonth(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String yearMonth = map.get("monthMation").toString();
        String timeId = map.get("timeId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
		List<String> months = DateUtil.getPointMonthAfterMonthList(yearMonth, 2);
		LOGGER.info("需要查询的月份信息：{}", months);
        List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkMationByMonth(userId, timeId, months);
		beans.forEach(bean -> {
			if("-".equals(bean.get("timeId").toString())){
				// 加班日的打卡信息
				bean.put("title", String.format(Locale.ROOT, "(%s) %s", "加班", bean.get("title").toString()));
			}
		});
        // 1.判断节假日信息
		queryDayWorkMation(beans, months, timeId);
		// 2.获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
		beans.addAll(getUserOtherDayMation(userId, timeId, months));
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

	/**
	 * 获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
	 *
	 * @param userId 用户id
	 * @param timeId 班次id
	 * @param months 指定月份，格式["2020-04", "2020-05"...]
	 * @return
	 * @throws Exception
	 */
	@Override
    public List<Map<String, Object>> getUserOtherDayMation(String userId, String timeId, List<String> months) throws Exception {
		List<Map<String, Object>> beans = new ArrayList<>();
		// 1.获取审核通过的出差信息
		List<Map<String, Object>> businessTripDay = checkWorkBusinessTripService.queryStateIsSuccessBusinessTripDayByUserIdAndMonths(userId, timeId, months);
		beans.addAll(businessTripDay);
		// 2.获取审核通过的加班信息
		List<Map<String, Object>> workOvertimeDay = checkWorkOvertimeService.queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(userId, months);
		beans.addAll(workOvertimeDay);
		// 3.获取审核通过的请假信息
		List<Map<String, Object>> leaveDay = checkWorkLeaveService.queryStateIsSuccessLeaveDayByUserIdAndMonths(userId, timeId, months);
		beans.addAll(leaveDay);
		return beans;
	}

	@Override
	public void queryDayWorkMation(List<Map<String, Object>> beans, List<String> months, String timeId) throws Exception {
		// 获取指定月份的节假日(type=3)
		List<Map<String, Object>> holiday = checkWorkDao.queryHolidayScheduleDayMation(months);
		beans.addAll(holiday);
		// 开始计算上班日期
		calcWorkTime(beans, months, timeId);
		// 将节假日时间段转化为每一天
		calcHolidayPartToDay(beans);
	}

	/**
	 * 将节假日时间段转化为每一天
	 *
	 * @param beans 返回前台的参数
	 */
	private void calcHolidayPartToDay(List<Map<String, Object>> beans) {
		List<Map<String, Object>> newList = new ArrayList<>();
		for(int i = 0; i < beans.size(); i++){
			if("3".equals(beans.get(i).get("type").toString())){
				// 节假日
				List<String> days = DateUtil.getDays(beans.get(i).get("start").toString(), beans.get(i).get("end").toString());
				if(days.size() > 1){
					for(String day: days){
						newList.add(CheckWorkConstants.structureRestMation(day, beans.get(i).get("title").toString()));
					}
					beans.remove(i);
					// 索引减1，否则会报java.util.ConcurrentModificationException
					i--;
				}
			}
		}
		beans.addAll(newList);
	}

	/**
	 * 计算上班日期
	 *
	 * @param beans 返回前台的参数(type=3--节假日)
	 * @param months 指定月
	 * @param timeId 班次id
	 */
	private void calcWorkTime(List<Map<String, Object>> beans, List<String> months, String timeId) throws Exception {
		List<String> monthDays = DateUtil.getDaysByMonths(months);
		List<Map<String, Object>> weekDays = checkWorkTimeDao.queryWeekDayByTimeId(timeId);
		LOGGER.info("获取指定班次中的工作日信息，{}", weekDays);
		for (String day: monthDays){
			// 判断该日期在节假日类型中是否包含
			if(!inHolidayScheduleDay(day, beans)){
				// 如果该天不是节假日
				int weekDay = DateUtil.getWeek(day);
				int weekType = DateUtil.getWeekType(day);
				List<Map<String, Object>> simpleDay = weekDays.stream()
						.filter(item -> Integer.parseInt(item.get("day").toString()) == weekDay).collect(Collectors.toList());
				if (simpleDay != null && !simpleDay.isEmpty()) {
					// 如果今天是需要考勤的日期
					int dayType = Integer.parseInt(simpleDay.get(0).get("type").toString());
					if (weekType == 1 && dayType == 2) {
						// 如果获取到的日期是双周，但考勤班次里面是单周，则不做任何操作
					} else {
						// 单周或者每周的当天都上班
						beans.add(CheckWorkConstants.structureWorkMation(day));
						continue;
					}
				}
				beans.add(CheckWorkConstants.structureRestMation(day, ""));
			}
		}
	}

	/**
	 * 判断指定日期是否属于节假日
	 *
	 * @param day 指定日期，格式为yyyy-MM-dd
	 * @param beans 包含节假日信息的集合
	 * @return true:是节假日，false:不是节假日
	 */
	private boolean inHolidayScheduleDay(String day, List<Map<String, Object>> beans){
		List<Map<String, Object>> fillter = beans.stream().filter(bean -> {
			try {
				// 节假日类型
				if("3".equals(bean.get("type").toString())){
					return DateUtil.compare(bean.get("start").toString(), day + " 00:00:01")
							&& DateUtil.compare(day + " 00:00:01", bean.get("end").toString());
				}
			} catch (ParseException e) {
				LOGGER.warn("time compare error", e);
			}
			return false;
		}).collect(Collectors.toList());
		if(fillter == null || fillter.isEmpty()){
			return false;
		}
		return true;
	}

    /**
     * 
         * @Title: queryCheckWorkReport
         * @Description: 获取考勤报表数据
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryCheckWorkReport(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 1.获取所有的考勤班次在指定日期内需要上班多少天
        Map<String, Integer> timeWorkDay = getAllCheckWorkTime(map.get("startTime").toString(), map.get("endTime").toString());
        // 2.分页获取员工考勤信息
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkReport(map);
		setShouldTime(beans, timeWorkDay);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
    }
    
    private void setShouldTime(List<Map<String, Object>> beans, Map<String, Integer> timeWorkDay){
    	for(Map<String, Object> bean: beans){
        	String[] timsIds = bean.get("timsIds").toString().split(",");
        	// 该员工在指定日期范围内应该上班的天数
        	Integer shouldTime = 0;
        	for(String timeId: timsIds){
        		if(!ToolUtil.isBlank(timeId)){
        			shouldTime += timeWorkDay.get(timeId);
        		}
        	}
        	bean.put("shouldTime", shouldTime);
        }
    }
    
    /**
     * 
     * @Title: getAllCheckWorkTime
     * @Description: 获取所有的考勤班次在指定日期内需要上班多少天
     * @param startTime 开始日期
     * @param endTime 结束日期
     * @return
     * @throws Exception
     * @return: Map<String,Integer> key:考勤班次id,value:指定日期内需要上班的天数
     * @throws
     */
	private Map<String, Integer> getAllCheckWorkTime(String startTime, String endTime) throws Exception{
		List<Map<String, Object>> workTime = checkWorkTimeDao.queryAllStaffCheckWorkTime();
		Map<String, Integer> timeWorkDay = new HashMap<>();
		for(Map<String, Object> bean: workTime){
			List<Map<String, Object>> days = checkWorkTimeDao.queryWeekDayByTimeId(bean.get("timeId").toString());
			bean.put("days", days);
			timeWorkDay.put(bean.get("timeId").toString(), 0);
		}
		// 1.获取范围内的所有日期
		List<String> days = DateUtil.getDays(startTime, endTime);
		for(String day: days){
			if(sysScheduleCommonService.judgeISHoliday(day)){
				// 如果是法定节假日，则不参与计算
				continue;
			}
			// 判断日期是周几
			int weekDay = DateUtil.getWeek(day);
			// 判断日期是单周还是双周
			int weekType = DateUtil.getWeekType(day);
			for(String timeId: timeWorkDay.keySet()){
				if(getTimeWhetherWork(timeId, weekDay, weekType, workTime)){
					timeWorkDay.put(timeId, (timeWorkDay.get(timeId) + 1));
				}
			}
		}
		return timeWorkDay;
	}
	
	/**
	 * 
	 * @Title: getTimeWhetherWork
	 * @Description: 判断该周天在指定班次是否是上班日
	 * @param timeId 班次id
	 * @param weekDay 周几
	 * @param weekType 是单周还是双周
	 * @param workTime 班次信息
	 * @return
	 * @return: boolean
	 * @throws
	 */
	private boolean getTimeWhetherWork(String timeId, int weekDay, int weekType, List<Map<String, Object>> workTime){
		Map<String, Object> timeMation = workTime.stream().filter(item -> item.get("timeId").toString().equals(timeId)).collect(Collectors.toList()).get(0);
		if(timeMation != null && !timeMation.isEmpty()){
			List<Map<String, Object>> days = (List<Map<String, Object>>) timeMation.get("days");
			List<Map<String, Object>> simpleDay = days.stream().filter(item -> Integer.parseInt(item.get("day").toString()) == weekDay).collect(Collectors.toList());
			if(simpleDay != null && !simpleDay.isEmpty()){
				// 在该班次中找到了指定日期的上班时间
				int dayType = Integer.parseInt(simpleDay.get(0).get("type").toString());
				if(weekType == 0 && dayType == 2){
					// 该周天是单周并且该班次是单周上班
					return true;
				}else if(weekType == 1 && dayType == 2){
					// 该周天是双周并且该班次是单周上班
					return false;
				}else{
					// 该周天是双周或者单周并且该班次是每周上班
					return true;
				}
			}
		}
		return false;
	}
	
    /**
     * 
         * @Title: queryCheckWorkEcharts
         * @Description: 获取考勤图标数据
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryCheckWorkEcharts(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> ma = new HashMap<>();
        String arr = map.get("arr").toString();
        String[] dayarr = arr.split(",");
        List<Map<String, Object>> beans = new ArrayList<>();
        for(int i = 0, l = dayarr.length; i < l; i++){
            map.put("day", dayarr[i]);
            map.put("companyName", ma.get("companyId"));
            Map<String, Object> bean = checkWorkDao.queryCheckWorkEcharts(map);
            beans.add(bean);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
    
    /**
     * 
         * @Title: downloadCheckWorkTemplate
         * @Description: 个人考勤情况导出
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @SuppressWarnings("static-access")
	@Override
    public void downloadCheckWorkTemplate(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        List<Map<String, Object>> beans = checkWorkDao.downloadCheckWorkTemplate(map);//获取当前账号的考勤信息
        String[] key = new String[]{"userName", "checkDate", "workTimeName", "week", "clockIn", "clockOut", "workHours", "state"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dstr = null;
        String time = "";
        for(Map<String, Object> bean : beans){
            dstr = bean.get("checkDate").toString();
            java.util.Date date = sdf.parse(dstr);
            bean.put("week", Constants.WeekDay.getWeekName(date)); //获取当前日期是星期几
            //获取上班打卡状态
            time = CheckWorkConstants.ClockInTime.getClockInState(bean.get("clockInState").toString());
            if("1".equals(bean.get("appealInState").toString()) && "2".equals(bean.get("appealInType").toString())){
                time += " ( 申诉成功 )";
            }
            bean.put("clockIn", bean.get("clockIn").toString() + "(" + time + ")");
            //获取下班打卡状态
            time = CheckWorkConstants.ClockOutTime.getClockOutState(bean.get("clockOutState").toString());
            if("1".equals(bean.get("appealOutState").toString()) && "3".equals(bean.get("appealOutType").toString())){
                time += " ( 申诉成功 )";
            }
            bean.put("clockOut", bean.get("clockOut").toString() + "(" + time + ")");
            //获取考勤状态
            time = CheckWorkConstants.ClockState.getClockState(bean.get("state").toString());
            if("1".equals(bean.get("appealAllState").toString()) && "1".equals(bean.get("appealAllType").toString())){
                time += " ( 申诉成功 )";
            }
            bean.put("state", time);
            // 考勤班次
            if(bean.containsKey("timeId") && !ToolUtil.isBlank(bean.get("timeId").toString())){
            	bean.put("workTimeName", String.format("%s[%s~%s]", bean.get("title").toString(), bean.get("startTime").toString(), bean.get("endTime").toString()));
            }else{
            	bean.put("workTimeName", "");
            }
        }
        String[] column = new String[]{"姓名", "考勤日期", "班次", "星期", "上班打卡时间", "下班打卡时间", "工时", "考勤状态"};
        String[] dataType = new String[]{"", "data", "data", "data", "data", "data", "data", "data"};
        ExcelUtil.createWorkBook("我的考勤", "考勤详细", beans, key, column, dataType, inputObject.getResponse()); //考勤信息导出
    }
    
    /**
     * 
         * @Title: queryReportDetail
         * @Description: 获取表格数据详情信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryReportDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = checkWorkDao.queryReportDetail(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
    }
	
    /**
     * 
         * @Title: queryEchartsDetail
         * @Description: 获取图表数据详情信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryEchartsDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = checkWorkDao.queryEchartsDetail(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
    }
    
    /**
     * 
         * @Title: queryReportDownload
         * @Description: 考勤报表导出
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @SuppressWarnings("static-access")
    @Override
    public void queryReportDownload(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String startTime = map.get("startTime").toString();
        String endTime = map.get("endTime").toString();
        Map<String, Integer> timeWorkDay = getAllCheckWorkTime(startTime, endTime);
        List<Map<String, Object>> beans = checkWorkDao.queryReportDownload(map);
        setShouldTime(beans, timeWorkDay);
        String[] key = new String[]{"companyName", "departmentName", "jobName", "userName", "shouldTime", "fullTime", "absenteeism", "lackTime", "late", "leaveEarly", "missing"};
        String[] column = new String[]{"公司", "部门", "职位", "姓名", "应出勤（次）", "全勤（次）", "缺勤（次）"," 工时不足（次）", "迟到（次）", "早退（次）", "漏签（次）"};
        String[] dataType = new String[]{"", "", "", "", "", "", "", "", "", "", ""};
        ExcelUtil.createWorkBook("考勤报表", startTime + "-" + endTime, beans, key, column, dataType, inputObject.getResponse()); //考勤信息导出
    }
    
}
