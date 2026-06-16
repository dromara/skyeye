/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.checkwork.classenum.*;
import com.skyeye.checkwork.dao.CheckWorkDao;
import com.skyeye.checkwork.entity.CheckWork;
import com.skyeye.checkwork.service.CheckWorkService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.*;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constans.CheckWorkConstants;
import com.skyeye.eve.centerrest.entity.checkwork.DayWork;
import com.skyeye.eve.centerrest.entity.checkwork.UserOtherDayMation;
import com.skyeye.eve.service.IScheduleDayService;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.overtime.dao.OvertimeDao;
import com.skyeye.overtime.service.OvertimeService;
import com.skyeye.scheduling.entity.SchedulingTime;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.scheduling.service.SchedulingTimeService;
import com.skyeye.trip.service.BusinessTripService;
import com.skyeye.worktime.classenum.CheckWorkTimeWeekType;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.entity.CheckWorkTimePoint;
import com.skyeye.worktime.entity.CheckWorkTimeWeek;
import com.skyeye.worktime.service.CheckWorkTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkServiceImpl
 * @Description: 考勤打卡管理服务接口层
 * 班次优先级：排版班次 > 节假日 > 固定班次
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "考勤打卡管理", groupName = "考勤打卡管理")
public class CheckWorkServiceImpl extends SkyeyeBusinessServiceImpl<CheckWorkDao, CheckWork> implements CheckWorkService {

    private static Logger LOGGER = LoggerFactory.getLogger(CheckWorkServiceImpl.class);

    @Autowired
    private CheckWorkDao checkWorkDao;

    @Autowired
    private BusinessTripService checkWorkBusinessTripService;

    @Autowired
    private LeaveService checkWorkLeaveService;

    @Autowired
    private OvertimeService checkWorkOvertimeService;

    @Autowired
    private OvertimeDao checkWorkOvertimeDao;

    @Autowired
    private IScheduleDayService iScheduleDayService;

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private SchedulingTimeService schedulingTimeService;

    /**
     * 上班打卡
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertCheckWorkStartWork(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String timeId = map.get("timeId").toString();
        String shiftType = map.get("shiftType").toString();
        String staffId = user.get("staffId").toString();
        String userId = user.get("id").toString();
        String todayYMD = DateUtil.getYmdTimeAndToString();
        // 1.获取当前用户的考勤班次信息
        Map<String, Object> workTime = getWorkTime(userId, todayYMD, timeId, staffId, shiftType);
        validateClockPermission(workTime, map, shiftType);
        validateOnlineClockLocation(workTime, map);
        // 2.获取今天的打卡记录
        String checkInTime = DateUtil.getHmsTimeAndToString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        CheckWork todayCheckWork = checkWorkDao.queryisAlreadyCheck(DateUtil.getYmdTimeAndToString(), userId, timeId, tenantId);
        if (ObjectUtil.isEmpty(todayCheckWork) && DateUtil.compareTimeHMS(checkInTime, workTime.get("clockOut").toString())) {
            // 今日没有打卡，且没有到下班时间，可以进行打卡
            CheckWork checkWork = new CheckWork();
            checkWork.setCheckDate(DateUtil.getYmdTimeAndToString());
            checkWork.setCreateId(userId);
            checkWork.setTimeId(timeId);
            checkWork.setState(ClockState.START.getKey());
            if (DateUtil.compareTimeHMS(checkInTime, workTime.get("clockIn").toString())) {
                // 当前打卡时间是否早于上班时间，视为正常
                checkWork.setClockInState(ClockInTime.NORMAL.getKey());
            } else {
                // 迟到
                checkWork.setClockInState(ClockInTime.LATE.getKey());
            }
            checkWork.setClockIn(checkInTime);
            checkWork.setClockInIp(ToolUtil.getIpByRequest(PutObject.getRequest()));

            String longitude = map.get("longitude").toString();
            String latitude = map.get("latitude").toString();
            String address = map.get("address").toString();
            String clockSource = map.get("clockSource").toString();

            checkWork.setClockInLongitude(longitude);
            checkWork.setClockInLatitude(latitude);
            checkWork.setClockInAddress(address);
            checkWork.setClockInSource(clockSource);

            createEntity(checkWork, userId);
        } else if (ObjectUtil.isNotEmpty(todayCheckWork) && ToolUtil.isBlank(todayCheckWork.getClockOut())) {
            // 今日已经打过晚卡，不能打早卡
            outputObject.setreturnMessage("今日已经打过晚卡，现在不能打早卡！");
        } else if (!DateUtil.compareTimeHMS(checkInTime, workTime.get("clockOut").toString())) {
            // 今日没有打卡，已是下班时间，不能进行打卡
            outputObject.setreturnMessage("今日打早卡时间已过，不能进行打卡！");
        } else {
            outputObject.setreturnMessage("今日早卡已打过卡，请不要重复打卡！");
        }
    }

    /**
     * 获取指定员工指定班次的考勤信息
     *
     * @param timeId    班次id
     * @param staffId   员工id
     * @param shiftType 班次类型 {@link com.skyeye.common.enumeration.CheckWorkShiftType}
     * @return 该班次的上下班时间，时间格式为HH:mm:ss
     */
    private Map<String, Object> getWorkTimeByUserMation(String timeId, String staffId, String shiftType) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        if (StrUtil.equals(shiftType, CheckWorkShiftType.FIXED.getKey())) {
            // 1.获取指定班次的上下班时间
            Map<String, Object> bean = checkWorkDao.queryStartWorkTime(timeId, staffId, tenantId);
            if (CollectionUtil.isEmpty(bean)) {
                // 您不具备该班次的考勤权限
                throw new CustomException("You do not have the attendance authority for this shift.");
            }
            CheckWorkTime checkWorkTime = checkWorkTimeService.selectById(timeId);
            bean.put("clockIn", checkWorkTime.getStartTime() + ":00");
            bean.put("clockOut", checkWorkTime.getEndTime() + ":00");
            bean.put("checkWorkTimeWeekList", checkWorkTime.getCheckWorkTimeWeekList());
            bean.put("onlineClockEnabled", ObjectUtil.defaultIfNull(checkWorkTime.getOnlineClockEnabled(), EnableEnum.ENABLE_USING.getKey()));
            bean.put("webClockEnabled", ObjectUtil.defaultIfNull(checkWorkTime.getWebClockEnabled(), EnableEnum.ENABLE_USING.getKey()));
            bean.put("checkWorkTimePointList", checkWorkTime.getCheckWorkTimePointList());
            return bean;
        } else {
            SchedulingTime schedulingTime = schedulingTimeService.selectById(timeId);
            if (ObjectUtil.isEmpty(schedulingTime)) {
                // 排班时间不存在
                throw new CustomException("The scheduling time does not exist.");
            }
            Map<String, Object> bean = new HashMap<>();
            bean.put("clockIn", schedulingTime.getStartTime());
            bean.put("clockOut", schedulingTime.getEndTime());
            bean.put("isNextDay", schedulingTime.getIsNextDay());
            // 是否是排班班次
            bean.put("isSchedulingWorkDay", true);
            return bean;
        }
    }

    /**
     * 校验打卡端权限（固定班次）
     *
     * @param workTime  班次信息
     * @param map       入参
     * @param shiftType 班次类型
     */
    private void validateClockPermission(Map<String, Object> workTime, Map<String, Object> map, String shiftType) {
        if (!StrUtil.equals(shiftType, CheckWorkShiftType.FIXED.getKey())) {
            return;
        }
        if (!workTime.containsKey("onlineClockEnabled") || !workTime.containsKey("webClockEnabled")) {
            return;
        }
        ClockSource clockSource = ClockSource.getByKey(map.get("clockSource").toString());
        if (ClockSource.ONLINE_SOURCE.equals(clockSource)) {
            Integer onlineClockEnabled = Integer.parseInt(workTime.get("onlineClockEnabled").toString());
            if (!EnableEnum.ENABLE_USING.getKey().equals(onlineClockEnabled)) {
                throw new CustomException("该班次未开启线上打卡。");
            }
        } else {
            Integer webClockEnabled = Integer.parseInt(workTime.get("webClockEnabled").toString());
            if (!EnableEnum.ENABLE_USING.getKey().equals(webClockEnabled)) {
                throw new CustomException("该班次未开启网站端打卡。");
            }
        }
    }

    /**
     * 校验线上打卡定位是否在任一点位范围内
     */
    private void validateOnlineClockLocation(Map<String, Object> workTime, Map<String, Object> map) {
        ClockSource clockSource = ClockSource.getByKey(map.containsKey("clockSource") && map.get("clockSource") != null
            ? map.get("clockSource").toString() : null);
        if (!ClockSource.ONLINE_SOURCE.equals(clockSource)) {
            return;
        }
        Object pointObj = workTime.get("checkWorkTimePointList");
        if (ObjectUtil.isEmpty(pointObj)) {
            return;
        }
        List<CheckWorkTimePoint> pointList = JSONUtil.toList(JSONUtil.toJsonStr(pointObj), CheckWorkTimePoint.class);
        if (CollectionUtil.isEmpty(pointList)) {
            return;
        }
        String longitude = map.containsKey("longitude") && map.get("longitude") != null ? map.get("longitude").toString() : StrUtil.EMPTY;
        String latitude = map.containsKey("latitude") && map.get("latitude") != null ? map.get("latitude").toString() : StrUtil.EMPTY;
        if (StrUtil.isBlank(longitude) || StrUtil.isBlank(latitude)) {
            throw new CustomException("请先获取定位信息后再打卡。");
        }
        double currentLat = Double.parseDouble(latitude);
        double currentLng = Double.parseDouble(longitude);
        for (CheckWorkTimePoint point : pointList) {
            if (StrUtil.isBlank(point.getLongitude()) || StrUtil.isBlank(point.getLatitude())) {
                continue;
            }
            int radius = point.getRadius() == null ? 500 : point.getRadius();
            double distance = ToolUtil.calculateDistance(currentLat, currentLng,
                Double.parseDouble(point.getLatitude()), Double.parseDouble(point.getLongitude()));
            if (distance <= radius) {
                return;
            }
        }
        throw new CustomException("你不在打卡范围内，请前往打卡范围内再进行打卡。");
    }

    /**
     * 下班打卡
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editCheckWorkEndWork(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String timeId = map.get("timeId").toString();
        String staffId = user.get("staffId").toString();
        String shiftType = map.get("shiftType").toString();
        String userId = user.get("id").toString();
        String todayYMD = DateUtil.getYmdTimeAndToString();
        // 1.获取当前用户的考勤班次信息
        Map<String, Object> workTime = getWorkTime(userId, todayYMD, timeId, staffId, shiftType);
        validateClockPermission(workTime, map, shiftType);
        validateOnlineClockLocation(workTime, map);
        // 2.获取今天的打卡记录
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        CheckWork todayCheckWork = checkWorkDao.queryisAlreadyCheck(DateUtil.getYmdTimeAndToString(), userId, timeId, tenantId);
        CheckWork checkWork = new CheckWork();
        checkWork.setCheckDate(DateUtil.getYmdTimeAndToString());
        checkWork.setCreateId(userId);
        checkWork.setTimeId(timeId);

        String longitude = map.get("longitude").toString();
        String latitude = map.get("latitude").toString();
        String address = map.get("address").toString();
        String clockSource = map.get("clockSource").toString();

        if (ObjectUtil.isEmpty(todayCheckWork)) {
            // 早卡晚卡都没有打，可以打晚卡【缺早卡】【上班打卡状态-未打卡】
            checkWork.setClockOut(DateUtil.getHmsTimeAndToString());
            checkWork.setState(ClockState.NOT_START.getKey());
            checkWork.setClockInState(ClockInTime.NOTCLOCK.getKey());
            if (DateUtil.compareTimeHMS(checkWork.getClockOut(), workTime.get("clockOut").toString())) {
                // 当前打卡时间是否早于下班时间，视为早退
                checkWork.setClockOutState(ClockOutTime.EARLY.getKey());
            } else {
                // 正常
                checkWork.setClockOutState(ClockOutTime.NORMAL.getKey());
            }
            checkWork.setWorkHours(String.valueOf(CommonNumConstants.NUM_ZERO));
            String ip = ToolUtil.getIpByRequest(PutObject.getRequest());
            checkWork.setClockInIp(ip);
            checkWork.setClockOutIp(ip);
            checkWork.setClockInLongitude(longitude);
            checkWork.setClockInLatitude(latitude);
            checkWork.setClockInAddress(address);
            checkWork.setClockOutSource(clockSource);
            createEntity(checkWork, userId);
        } else if (!ToolUtil.isBlank(todayCheckWork.getClockIn())) {
            // 打过早卡，没有打晚卡
            checkWork.setClockOut(DateUtil.getHmsTimeAndToString());
            // 系统设置的上班时长
            String a = DateUtil.getDistanceHMS(workTime.get("clockOut").toString(), workTime.get("clockIn").toString());
            // 用户的上班时长
            String b = DateUtil.getDistanceHMS(checkWork.getClockOut(), todayCheckWork.getClockIn());
            // 当前打卡时间是否早于下班时间
            if (DateUtil.compareTimeHMS(a, b)) {
                // 全勤
                checkWork.setState(ClockState.NORMAL.getKey());
            } else {
                // 工时不足
                checkWork.setState(ClockState.IN_SUFFICIENT.getKey());
            }
            if (DateUtil.compareTimeHMS(checkWork.getClockOut(), workTime.get("clockOut").toString())) {
                // 早退
                checkWork.setClockOutState(ClockOutTime.EARLY.getKey());
            } else {
                // 正常
                checkWork.setClockOutState(ClockOutTime.NORMAL.getKey());
            }
            checkWork.setWorkHours(b);
            checkWork.setClockOutIp(ToolUtil.getIpByRequest(PutObject.getRequest()));
            checkWork.setId(todayCheckWork.getId());
            checkWork.setClockOutLongitude(longitude);
            checkWork.setClockOutLatitude(latitude);
            checkWork.setClockOutAddress(address);
            checkWork.setClockOutSource(clockSource);
            updateEntity(checkWork, userId);
        } else {
            // 已经打过晚卡
            outputObject.setreturnMessage("今日已打过晚卡，请不要重复打卡！");
        }
    }

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        pageInfo.setState(FlowableStateEnum.PASS.getKey());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryCheckWorkList(pageInfo);
        checkWorkTimeService.setMationForMap(beans, "timeId", "timeMation");
        return beans;
    }

    @Override
    public CheckWork selectById(String id) {
        CheckWork checkWork = super.selectById(id);
        checkWorkTimeService.setDataMation(checkWork, CheckWork::getTimeId);
        if (ObjectUtil.isNotEmpty(checkWork.getTimeMation())) {
            checkWork.setName(checkWork.getCheckDate() + "；班次[" + checkWork.getTimeMation().getName() + "]；" + "考勤[" + ClockState.getClockState(checkWork.getState()) + "]");
        } else {
            checkWork.setName(checkWork.getCheckDate() + "；考勤[" + ClockState.getClockState(checkWork.getState()) + "]");
        }
        return checkWork;
    }

    /**
     * 当前登录用户可以进行申诉的打卡信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCheckWorkIdByAppealType(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkIdByAppealType(userId, FlowableStateEnum.PASS.getKey(),
            Arrays.asList(ClockState.START.getKey(), ClockState.NORMAL.getKey()), tenantId);
        checkWorkTimeService.setMationForMap(beans, "timeId", "timeMation");
        for (Map<String, Object> bean : beans) {
            Integer state = Integer.parseInt(bean.get("state").toString());
            Map<String, Object> timeMation = (Map<String, Object>) bean.get("timeMation");
            if (CollectionUtil.isNotEmpty(timeMation)) {
                bean.put("name", bean.get("name").toString() + "；班次[" + timeMation.get("name").toString() + "]；" + "考勤[" + ClockState.getClockState(state) + "]");
            } else {
                bean.put("name", bean.get("name").toString() + "；考勤[" + ClockState.getClockState(state) + "]");
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void queryCheckWorkTimeToShowButton(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String today = DateUtil.getYmdTimeAndToString();
        String userId = user.get("id").toString();
        String timeId = map.get("timeId").toString();
        String shiftType = map.get("shiftType").toString();
        String staffId = user.get("staffId").toString();
        String nowTimeHMS = DateUtil.getHmsTimeAndToString();
        // 1.获取当前用户的考勤班次信息
        Map<String, Object> workTime = getWorkTime(userId, today, timeId, staffId, shiftType);
        if (Integer.parseInt(workTime.get("type").toString()) == CheckTypeFrom.CHECT_BTN_FROM_OVERTIME.getKey()) {
            timeId = "-";
        }
        // 2.判断显示打上班卡或者下班卡
        Map<String, Object> result = getChectBtn(today, userId, timeId, workTime, nowTimeHMS);
        outputObject.setBean(result);
    }

    /**
     * 获取当前用户的考勤班次信息
     *
     * @param userId    用户id
     * @param today     指定日期，格式为yyyy-MM-dd(一般为今天的日期)
     * @param timeId    班次id
     * @param staffId   员工id
     * @param shiftType {@link com.skyeye.common.enumeration.CheckWorkShiftType}
     * @return
     */
    private Map<String, Object> getWorkTime(String userId, String today, String timeId, String staffId, String shiftType) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        Map<String, Object> workTime;
        // 判断今天是否是加班日
        List<Map<String, Object>> overTimeMation = checkWorkOvertimeDao.queryPassThisDayAndCreateId(userId, today,
            FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
        if (CollectionUtil.isNotEmpty(overTimeMation)) {
            // 根据加班日判断显示打上班卡或者下班卡
            workTime = overTimeMation.get(0);
            workTime.put("clockIn", workTime.get("clockIn").toString() + ":00");
            workTime.put("clockOut", workTime.get("clockOut").toString() + ":00");
            workTime.put("type", CheckTypeFrom.CHECT_BTN_FROM_OVERTIME.getKey());
        } else {
            // 根据考勤班次判断显示打上班卡或者下班卡
            workTime = getWorkTimeByUserMation(timeId, staffId, shiftType);
            workTime.put("type", CheckTypeFrom.CHECT_BTN_FROM_TIMEID.getKey());
        }
        return workTime;
    }

    /**
     * 判断显示打上班卡或者下班卡
     *
     * @param today      指定日期，格式为yyyy-MM-dd(一般为今天的日期)
     * @param userId     用户id
     * @param timeId     班次id
     * @param workTime   考勤班次信息
     * @param nowTimeHMS 指定日期，格式为HH:mm:ss(一般为当前时间)
     * @return
     */
    private Map<String, Object> getChectBtn(String today, String userId, String timeId, Map<String, Object> workTime, String nowTimeHMS) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 获取今天的打卡记录
        CheckWork todayCheckWork = checkWorkDao.queryisAlreadyCheck(today, userId, timeId, tenantId);
        Integer checkState = getCheckState(todayCheckWork, nowTimeHMS, workTime, today);
        Map<String, Object> result = new HashMap<>();
        result.put("isCheck", checkState);
        result.putAll(workTime);
        if (ObjectUtil.isNotEmpty(todayCheckWork)) {
            result.put("realClockIn", todayCheckWork.getClockIn());
            result.put("realClockOut", todayCheckWork.getClockOut());
            result.put("clockInSource", todayCheckWork.getClockInSource());
            result.put("clockOutSource", todayCheckWork.getClockOutSource());
        }
        return result;
    }

    /**
     * 获取指定日期在规定班次内的打卡状态
     *
     * @param todayCheckWork 今日打卡信息
     * @param nowTimeHMS     指定日期，格式为HH:mm:ss
     * @param workTime       班次考勤信息
     * @param today          指定日期，格式为yyyy-MM-dd(一般为今天的日期)
     * @return
     */
    private Integer getCheckState(CheckWork todayCheckWork, String nowTimeHMS, Map<String, Object> workTime, String today) {
        Integer checkState = null;
        if (Integer.parseInt(workTime.get("type").toString()) == CheckTypeFrom.CHECT_BTN_FROM_TIMEID.getKey()) {
            // isSchedulingWorkDay为true则是排班班次
            Boolean isSchedulingWorkDay = (Boolean) workTime.getOrDefault("isSchedulingWorkDay", false);
            // 排班班次，不做节假日判断
            if (!isSchedulingWorkDay) {
                // 固定班次逻辑
                // 1. 判断是否是节假日
                boolean result = iScheduleDayService.judgeISHoliday(today);
                // 2. 判断今天是否在考勤班次关联的时间段内（工作日类型）
                boolean isWorkDay = !isWorkDayInCheckWorkTimeWeek(workTime, today);
                if (result || isWorkDay) {
                    // 今天不是加班日，但是是节假日，则不显示按钮 || 不在该班次的工作日范围内，也不显示打卡按钮
                    checkState = 5;
                    return checkState;
                }
            }
        }
        if (ObjectUtil.isEmpty(todayCheckWork) && DateUtil.compareTimeHMS(nowTimeHMS, workTime.get("clockOut").toString())) {
            // 今日没有打卡，且没有到下班时间，显示早卡按钮
            checkState = 1;
        } else if (ObjectUtil.isEmpty(todayCheckWork) && !DateUtil.compareTimeHMS(nowTimeHMS, workTime.get("clockOut").toString())) {
            // 今日没有打卡，已是下班时间，不显示按钮
            checkState = 3;
        } else if (!ToolUtil.isBlank(todayCheckWork.getClockIn()) && ToolUtil.isBlank(todayCheckWork.getClockOut())) {
            // 今日打过早卡没打晚卡，显示晚卡按钮
            checkState = 2;
        } else if (!ToolUtil.isBlank(todayCheckWork.getClockIn()) && !ToolUtil.isBlank(todayCheckWork.getClockOut())) {
            // 今日打过早卡打过晚卡，不显示按钮
            checkState = 4;
        }
        return checkState;
    }

    /**
     * 判断指定日期是否在固定班次的工作日范围内
     * <p>
     * 使用班次关联的时间段配置（checkWorkTimeWeekList）+ 周类型（单周/双周）计算。
     * 若未配置时间段，则默认视为工作日（保持原有行为）。
     *
     * @param workTime 含有 checkWorkTimeWeekList 的班次信息
     * @param today    指定日期，格式 yyyy-MM-dd
     * @return true 表示今天是该班次的工作日，false 表示休息日
     */
    @SuppressWarnings("unchecked")
    private boolean isWorkDayInCheckWorkTimeWeek(Map<String, Object> workTime, String today) {
        Object listObj = workTime.get("checkWorkTimeWeekList");
        if (!(listObj instanceof List)) {
            // 未配置时间段时，保持老逻辑：认为是工作日
            return true;
        }
        List<CheckWorkTimeWeek> weekList = (List<CheckWorkTimeWeek>) listObj;
        if (CollectionUtil.isEmpty(weekList)) {
            return true;
        }
        int weekDay = DateUtil.getWeek(today);
        int weekType = DateUtil.getWeekType(today);
        CheckWorkTimeWeek simpleDay = weekList.stream()
            .filter(item -> item.getWeekNumber() == weekDay && !item.getType().equals(CheckWorkTimeWeekType.DOUBLE.getKey()))
            .findFirst().orElse(null);
        if (ObjectUtil.isEmpty(simpleDay)) {
            // 没有为该星期几配置工作日，视为休息
            return false;
        }
        // 在该班次中找到了指定日期的配置，根据周类型判断是否工作
        if (weekType == WeekTypeEnum.ODD_WEEKS.getKey() && simpleDay.getType().equals(CheckWorkTimeWeekType.SINGLE_DAY.getKey())) {
            // 单周并且班次配置为单周上班
            return true;
        } else if (weekType == WeekTypeEnum.BIWEEKLY.getKey() && simpleDay.getType().equals(CheckWorkTimeWeekType.SINGLE_DAY.getKey())) {
            // 双周且班次配置为单周上班 → 本周休
            return false;
        } else {
            // 其它情况（每周都上班等）
            return true;
        }
    }

    /**
     * 根据月份查询当月的考勤信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCheckWorkMationByMonth(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String yearMonth = map.get("monthMation").toString();
        String timeId = map.get("timeId").toString();
        String shiftType = map.get("shiftType").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        String staffId = inputObject.getLogParams().get("staffId").toString();
        List<String> months = DateUtil.getPointMonthBeforeAfterMonth(yearMonth);
        LOGGER.info("需要查询的月份信息：{}", months);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = new ArrayList<>();
        if (StrUtil.isNotBlank(timeId)) {
            // 获取当前用户的考勤打卡信息
            List<Map<String, Object>> rows = checkWorkDao.queryCheckWorkMationByMonth(userId, timeId, months, tenantId);
            rows.forEach(bean -> {
                if ("-".equals(bean.get("timeId").toString())) {
                    // 加班日的打卡信息
                    bean.put("title", String.format(Locale.ROOT, "(%s) %s", "加班", bean.get("title").toString()));
                }
            });
            beans.addAll(rows);
        }
        // 1.判断节假日信息
        queryDayWorkMation(beans, months, timeId, shiftType, staffId);
        // 2.获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
        beans.addAll(getUserOtherDayMation(userId, timeId, months));
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void getUserOtherDayMation(InputObject inputObject, OutputObject outputObject) {
        UserOtherDayMation userOtherDayMation = inputObject.getParams(UserOtherDayMation.class);
        List<Map<String, Object>> beans = this.getUserOtherDayMation(userOtherDayMation.getUserId(), userOtherDayMation.getTimeId(), userOtherDayMation.getMonths());
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
     */
    @Override
    public List<Map<String, Object>> getUserOtherDayMation(String userId, String timeId, List<String> months) {
        List<Map<String, Object>> beans = new ArrayList<>();
        if (StrUtil.isNotBlank(timeId)) {
            // 1.获取审核通过的出差信息
            List<Map<String, Object>> businessTripDay = checkWorkBusinessTripService.queryStateIsSuccessBusinessTripDayByUserIdAndMonths(userId, timeId, months);
            beans.addAll(businessTripDay);
            // 2.获取审核通过的请假信息
            List<Map<String, Object>> leaveDay = checkWorkLeaveService.queryStateIsSuccessLeaveDayByUserIdAndMonths(userId, timeId, months);
            beans.addAll(leaveDay);
        }
        // 3.获取审核通过的加班信息
        List<Map<String, Object>> workOvertimeDay = checkWorkOvertimeService.queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(userId, months);
        beans.addAll(workOvertimeDay);
        return beans;
    }

    @Override
    public void queryDayWorkMation(InputObject inputObject, OutputObject outputObject) {
        DayWork dayWorkMation = inputObject.getParams(DayWork.class);
        List<Map<String, Object>> beans = dayWorkMation.getBeans();
        this.queryDayWorkMation(beans, dayWorkMation.getMonths(), dayWorkMation.getTimeId(), dayWorkMation.getShiftType(), dayWorkMation.getStaffId());
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void queryDayWorkMation(List<Map<String, Object>> beans, List<String> months, String timeId, String shiftType, String staffId) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 获取指定月份的节假日(type=3)
        List<Map<String, Object>> holiday = checkWorkDao.queryHolidayScheduleDayMation(months, tenantId);
        beans.addAll(holiday);
        // 开始计算上班日期
        if (StrUtil.equals(shiftType, CheckWorkShiftType.FIXED.getKey()) && StrUtil.isNotBlank(timeId)) {
            // 固定班次
            calcWorkTime(beans, months, timeId);
        }
        if (StrUtil.isNotBlank(staffId) && StrUtil.equals(shiftType, CheckWorkShiftType.SCHEDULE.getKey())) {
            // 计算排版班次信息
            calcScheduleShiftType(beans, months, staffId);
        }
        // 将节假日时间段转化为每一天
        calcHolidayPartToDay(beans);
    }

    private void calcScheduleShiftType(List<Map<String, Object>> beans, List<String> months, String staffId) {
        List<String> workDay = schedulingService.querySchedulingByStaffIdAndMouths(staffId, months);
        if (CollectionUtil.isEmpty(workDay)) {
            return;
        }
        for (String day : workDay) {
            beans.add(CheckWorkConstants.structureScheduleWorkMation(day));
        }
    }

    /**
     * 将节假日时间段转化为每一天
     *
     * @param beans 返回前台的参数
     */
    private void calcHolidayPartToDay(List<Map<String, Object>> beans) {
        List<Map<String, Object>> newList = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            if (CheckDayType.DAY_IS_HOLIDAY.getKey().equals(beans.get(i).get("type").toString())) {
                // 节假日
                List<String> days = DateUtil.getDays(beans.get(i).get("start").toString(), beans.get(i).get("end").toString());
                if (days.size() > 1) {
                    for (String day : days) {
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
     * @param beans  返回前台的参数(type=3--节假日)
     * @param months 指定月
     * @param timeId 班次id
     */
    private void calcWorkTime(List<Map<String, Object>> beans, List<String> months, String timeId) {
        List<String> monthDays = DateUtil.getDaysByMonths(months);
        CheckWorkTime checkWorkTime = checkWorkTimeService.selectById(timeId);
        LOGGER.info("获取指定班次中的工作日信息，{}", checkWorkTime.getCheckWorkTimeWeekList());
        for (String day : monthDays) {
            // 判断该日期在节假日类型中是否包含
            if (!inHolidayScheduleDay(day, beans)) {
                // 如果该天不是节假日
                int weekDay = DateUtil.getWeek(day);
                int weekType = DateUtil.getWeekType(day);
                CheckWorkTimeWeek simpleDay = checkWorkTime.getCheckWorkTimeWeekList()
                    .stream().filter(item -> item.getWeekNumber() == weekDay && !item.getType().equals(CheckWorkTimeWeekType.DOUBLE.getKey()))
                    .findFirst().orElse(null);
                if (ObjectUtil.isEmpty(simpleDay)) {
                    continue;
                }
                // 如果今天是需要考勤的日期
                if (weekType == WeekTypeEnum.BIWEEKLY.getKey() && simpleDay.getType().equals(CheckWorkTimeWeekType.SINGLE_DAY.getKey())) {
                    // 如果获取到的日期是双周，但考勤班次里面是单周，则不做任何操作
                } else {
                    // 单周或者每周的当天都上班
                    beans.add(CheckWorkConstants.structureWorkMation(day));
                    continue;
                }
                beans.add(CheckWorkConstants.structureRestMation(day, StrUtil.EMPTY));
            }
        }
    }

    /**
     * 判断指定日期是否属于节假日
     *
     * @param day   指定日期，格式为yyyy-MM-dd
     * @param beans 包含节假日信息的集合
     * @return true:是节假日，false:不是节假日
     */
    private boolean inHolidayScheduleDay(String day, List<Map<String, Object>> beans) {
        List<Map<String, Object>> fillter = beans.stream().filter(bean -> {
            // 节假日类型
            if (CheckDayType.DAY_IS_HOLIDAY.getKey().equals(bean.get("type").toString())) {
                return DateUtil.compare(bean.get("start").toString(), day + " 00:00:01") && DateUtil.compare(day + " 00:00:01", bean.get("end").toString());
            }
            return false;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(fillter)) {
            return false;
        }
        return true;
    }

    /**
     * 获取考勤报表数据
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryCheckWorkReport(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 1.获取所有的考勤班次在指定日期内需要上班多少天
        Map<String, Integer> timeWorkDay = getAllCheckWorkTime(map.get("startTime").toString(), map.get("endTime").toString());
        // 2.分页获取员工考勤信息
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));

        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        List<Map<String, Object>> beans = checkWorkDao.queryCheckWorkReport(map);
        if (tenantEnable) {
            // 如果开启多租户，则需要查询员工所在的租户下的员工信息
            List<String> userIds = beans.stream().map(item -> item.get("userId").toString()).distinct().collect(Collectors.toList());
            Map<String, Map<String, Object>> tenantUserMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds));
            beans.forEach(bean -> {
                String userId = bean.get("userId").toString();
                Map<String, Object> tenantUser = tenantUserMap.get(userId);
                if (CollectionUtil.isNotEmpty(tenantUser)) {
                    bean.put("jobNumber", tenantUser.get("jobNumber"));
                    bean.put("companyId", tenantUser.get("companyId"));
                    bean.put("departmentId", tenantUser.get("departmentId"));
                    bean.put("jobId", tenantUser.get("jobId"));
                }
            });
        }
        iCompanyService.setNameForMap(beans, "companyId", "companyName");
        iDepmentService.setNameForMap(beans, "departmentId", "departmentName");
        iCompanyJobService.setNameForMap(beans, "jobId", "jobName");
        setShouldTime(beans, timeWorkDay);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    private void setShouldTime(List<Map<String, Object>> beans, Map<String, Integer> timeWorkDay) {
        for (Map<String, Object> bean : beans) {
            String[] timsIds = bean.getOrDefault("timsIds", StrUtil.EMPTY).toString().split(CommonCharConstants.COMMA_MARK);
            // 该员工在指定日期范围内应该上班的天数
            Integer shouldTime = 0;
            for (String timeId : timsIds) {
                if (!ToolUtil.isBlank(timeId)) {
                    shouldTime += timeWorkDay.get(timeId) == null ? 0 : timeWorkDay.get(timeId);
                }
            }
            bean.put("shouldTime", shouldTime);
        }
    }

    /**
     * 获取所有的考勤班次在指定日期内需要上班多少天
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return key:考勤班次id,value:指定日期内需要上班的天数
     */
    private Map<String, Integer> getAllCheckWorkTime(String startTime, String endTime) {
        List<CheckWorkTime> workTime = checkWorkTimeService.queryAllData();
        Map<String, Integer> timeWorkDay = new HashMap<>();
        for (CheckWorkTime bean : workTime) {
            timeWorkDay.put(bean.getId(), 0);
        }
        // 1.获取范围内的所有日期
        List<String> days = DateUtil.getDays(startTime, endTime);
        for (String day : days) {
            boolean result = iScheduleDayService.judgeISHoliday(day);
            if (result) {
                // 如果是法定节假日，则不参与计算
                continue;
            }
            // 判断日期是周几
            int weekDay = DateUtil.getWeek(day);
            // 判断日期是单周还是双周
            int weekType = DateUtil.getWeekType(day);
            for (String timeId : timeWorkDay.keySet()) {
                if (getTimeWhetherWork(timeId, weekDay, weekType, workTime)) {
                    timeWorkDay.put(timeId, (timeWorkDay.get(timeId) + 1));
                }
            }
        }
        return timeWorkDay;
    }

    /**
     * 判断该周天在指定班次是否是上班日
     *
     * @param timeId   班次id
     * @param weekDay  周几
     * @param weekType 是单周还是双周
     * @param workTime 班次信息
     * @return
     */
    private boolean getTimeWhetherWork(String timeId, int weekDay, int weekType, List<CheckWorkTime> workTime) {
        CheckWorkTime timeMation = workTime.stream().filter(item -> item.getId().equals(timeId)).findFirst().orElse(null);
        if (ObjectUtil.isEmpty(timeMation) || CollectionUtil.isEmpty(timeMation.getCheckWorkTimeWeekList())) {
            return false;
        }
        CheckWorkTimeWeek simpleDay = timeMation.getCheckWorkTimeWeekList().stream()
            .filter(item -> item.getWeekNumber() == weekDay && !item.getType().equals(CheckWorkTimeWeekType.DOUBLE.getKey()))
            .findFirst().orElse(null);
        if (ObjectUtil.isEmpty(simpleDay)) {
            return false;
        }
        // 在该班次中找到了指定日期的上班时间
        if (weekType == WeekTypeEnum.ODD_WEEKS.getKey() && simpleDay.getType().equals(CheckWorkTimeWeekType.SINGLE_DAY.getKey())) {
            // 该周天是单周并且该班次是单周上班
            return true;
        } else if (weekType == WeekTypeEnum.BIWEEKLY.getKey() && simpleDay.getType().equals(CheckWorkTimeWeekType.SINGLE_DAY.getKey())) {
            // 该周天是双周并且该班次是单周上班
            return false;
        } else {
            // 该周天是双周或者单周并且该班次是每周上班
            return true;
        }
    }

    /**
     * 获取考勤图标数据
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCheckWorkEcharts(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String arr = map.get("arr").toString();
        String[] dayarr = arr.split(",");
        List<Map<String, Object>> beans = new ArrayList<>();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        for (int i = 0, l = dayarr.length; i < l; i++) {
            map.put("day", dayarr[i]);
            Map<String, Object> bean = checkWorkDao.queryCheckWorkEcharts(map);
            beans.add(bean);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取表格数据详情信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryReportDetail(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        List<Map<String, Object>> beans = checkWorkDao.queryReportDetail(map);
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取所有昨天没有打卡的用户
     *
     * @param timeId        考勤班次
     * @param yesterdayTime 昨天的日期
     */
    @Override
    public List<Map<String, Object>> queryNotCheckMember(String timeId, String yesterdayTime) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = checkWorkDao.queryNotCheckMember(timeId, yesterdayTime, tenantId);
        return beans;
    }

    /**
     * 获取所有昨天没有打下班卡的用户
     *
     * @param timeId        考勤班次
     * @param yesterdayTime 昨天的日期
     */
    @Override
    public List<Map<String, Object>> queryNotCheckEndWorkId(String timeId, String yesterdayTime) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = checkWorkDao.queryNotCheckEndWorkId(timeId, yesterdayTime, tenantId);
        return beans;
    }

    /**
     * 填充下班卡信息
     *
     * @param map
     */
    @Override
    public void editCheckWorkBySystem(Map<String, Object> map) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        checkWorkDao.editCheckWorkBySystem(map);
    }

    /**
     * 获取所有待结算的加班数据
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> queryCheckWorkOvertimeWaitSettlement() {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = checkWorkOvertimeDao.queryCheckWorkOvertimeWaitSettlement(FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
        return beans;
    }

    /**
     * 新增打卡信息(用于新增旷工的考勤信息)
     *
     * @param beans
     */
    @Override
    public void insertCheckWorkBySystem(List<Map<String, Object>> beans) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        checkWorkDao.insertCheckWorkBySystem(beans, tenantId);
    }

    @Override
    public void queryInfoByStaffIdsAndDates(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 取出所有员工id
        List<String> staffIds = Arrays.asList(params.get("staffIds").toString().split(CommonCharConstants.COMMA_MARK));
        // 取出所有日期
        List<String> dates = Arrays.asList(params.get("dates").toString().split(CommonCharConstants.COMMA_MARK));
        // 查出所有考勤信息
        QueryWrapper<CheckWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CheckWork::getCreateId), staffIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(CheckWork::getCheckDate), dates);
        List<CheckWork> allCheckWork = list(queryWrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(allCheckWork), null);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
