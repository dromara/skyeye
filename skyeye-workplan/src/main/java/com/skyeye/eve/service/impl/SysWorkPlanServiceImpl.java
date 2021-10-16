/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.constans.WorkPlanConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.constants.ScheduleDayConstants;
import com.skyeye.eve.dao.ScheduleDayDao;
import com.skyeye.eve.dao.SysWorkPlanDao;
import com.skyeye.eve.service.ScheduleDayService;
import com.skyeye.eve.service.SysWorkPlanService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.quartz.config.QuartzService;
import com.skyeye.service.JobMateMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: SysWorkPlanServiceImpl
 * @Description: 任务计划服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 11:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysWorkPlanServiceImpl implements SysWorkPlanService{
	
	@Autowired
	private SysWorkPlanDao sysWorkPlanDao;
	
	@Autowired
	private QuartzService quartzService;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Autowired
	private ScheduleDayDao scheduleDayDao;
	
	@Autowired
	private ScheduleDayService scheduleDayService;
	
	@Autowired
	private JobMateMationService jobMateMationService;
	
	private static final String MESSAGE_TITLE = "计划提醒";

	/**
	 * 计划执行状态
	 */
	public static enum PLAN_EXECUTOR_STATE{
		START_NEW(1, "待执行"),
		START_SUCCESS(2, "执行完成"),
		START_DELAY(3, "延期"),
		START_TRANSFER_TASK(4, "转其他任务");
		private int state;
		private String name;
		PLAN_EXECUTOR_STATE(int state, String name){
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
	     * @Title: querySysWorkPlanList
	     * @Description: 获取计划列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//转化计划周期类型为数字
		map.put("nowCheckType", WorkPlanConstants.SysWorkPlan.getClockInState(map.get("nowCheckType").toString()));
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysWorkPlanDao.querySysWorkPlanList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertSysWorkPlanISPeople
	     * @Description: 新增个人计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysWorkPlanISPeople(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		// 计划id
		String planId = ToolUtil.getSurFaceId();
		// 计划执行者
		Map<String, Object> executor = getTypeISPeopleExecutor(map, userId, planId);
		if(executor == null){
			outputObject.setreturnMessage("请选择计划执行人。");
			return;
		}
		
		//是否通知
		String whetherNotify = "1";
		//是否定时通知  1.是  2.否
		//如果为定时通知，则通知时间不能为空
		if("1".equals(map.get("whetherTime").toString())){
			//通知时间为空，返回提示信息
			if(ToolUtil.isBlank(map.get("notifyTime").toString())){
				outputObject.setreturnMessage("请选择通知时间。");
				return;
			}
			if(!DateUtil.compare(DateUtil.getTimeAndToString(), map.get("notifyTime").toString())){//定时时间小于当前时间
				outputObject.setreturnMessage("定时发送时间不能小于当前时间");
				return;
			}
			// 启动定时任务
			quartzService.startUpTaskQuartz(planId, map.get("title").toString(), map.get("notifyTime").toString(), userId,
					QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
		}else{
			// 非定时通知，执行邮件判断，内部通知判断
			sendMessageToStaff(map, userId, executor.get("userId").toString());
		}
		
		map.put("id", planId);
		map.put("planCycle", WorkPlanConstants.SysWorkPlan.getClockInState(map.get("nowCheckType").toString()));//转化计划周期类型为数字
		map.put("planType", "1");//计划类型  1.个人计划  2.部门计划  3.公司计划
		map.put("whetherNotify", whetherNotify);//是否已经通知  1.未通知  2.已通知
		map.put("createId", userId);//计划创建人
		map.put("createTime", DateUtil.getTimeAndToString());//创建时间
		sysWorkPlanDao.insertSysWorkPlanISPeople(map);
		sysWorkPlanDao.insertSysWorkPlanExecutorISPeople(executor);
		
		// 同步到日程
        scheduleDayService.synchronizationSchedule(map.get("title").toString(), map.get("content").toString(),
            map.get("startTime").toString(), map.get("endTime").toString(), executor.get("userId").toString(), planId,
            ScheduleDayConstants.ScheduleDayObjectType.OBJECT_TYPE_IS_PLAN);
	}

	/**
	 * 
	 * @Title: getTypeISPeopleExecutor
	 * @Description: 个人计划获取计划执行者
	 * @param map
	 * @param userId
	 * @param planId
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	private Map<String, Object> getTypeISPeopleExecutor(Map<String, Object> map, String userId, String planId){
		Map<String, Object> executor = new HashMap<>();
		//分配类型  1.自己执行  2.他人执行
		//如果为他人执行，则执行人不能为空
		if("2".equals(map.get("assignmentType").toString())){
			//计划执行人为空，返回提示信息
			if(ToolUtil.isBlank(map.get("carryPeople").toString())){
				return null;
			}
			executor.put("userId", map.get("carryPeople"));
		}else{
			executor.put("userId", userId);
		}
		executor.put("planId", planId);
		executor.put("state", PLAN_EXECUTOR_STATE.START_NEW.getState());
		executor.put("id", ToolUtil.getSurFaceId());
		return executor;
	}

	/**
	 * 团队（部门或者企业）获取计划执行者
	 *
	 * @param executorIds 执行者数组id
	 * @param planId 计划id
	 * @return
	 */
	private List<Map<String, Object>> getTypeISTeamExecutor(String[] executorIds, String planId){
		List<Map<String, Object>> executors = new ArrayList<>();
		for(String str : executorIds){
			if(!ToolUtil.isBlank(str)){
				Map<String, Object> executor = new HashMap<>();
				executor.put("userId", str);
				executor.put("planId", planId);
				executor.put("state", PLAN_EXECUTOR_STATE.START_NEW.getState());
				executor.put("id", ToolUtil.getSurFaceId());
				executors.add(executor);
			}
		}
		return executors;
	}
	
	private void sendMessageToStaff(Map<String, Object> map, String userId, String carryPeople) throws Exception{
		map.put("notifyTime", null);
		// 是否邮件通知
		String whetherMail = map.get("whetherMail").toString();
		// 是否内部通告通知
		String whetherNotice = map.get("whetherNotice").toString();
		// 获取计划接收人的信息
		List<Map<String, Object>> userMations = sysWorkPlanDao.queryUserMationByUserIds(carryPeople);
		// 内部消息通知对象
		List<Map<String, Object>> userJson = new ArrayList<>();
		//uJson内部通知对象
		Map<String, Object> uJson = null;
		for(Map<String, Object> userMation : userMations){
			//发送的消息内容
			String content = "尊敬的" + userMation.get("userName").toString() + ",您好：<br/>您收到一条新的工作计划信息《" + map.get("title").toString() + "》，请登录系统查看";
			if("1".equals(whetherMail)){//邮件通知
				if(!ToolUtil.isBlank(userMation.get("email").toString()) && userMation.containsKey("email")){
					sendEmailToStaff(userMation.get("email").toString(), content, userId);
				}
			}
			if("1".equals(whetherNotice)){//内部通告通知
				uJson = new HashMap<>();
				uJson.put("content", content);
				uJson.put("userId", userMation.get("userId"));
				userJson.add(uJson);
			}
		}
		//内部通告通知
		if(!userJson.isEmpty()){
			// 调用消息系统添加通知
			sendInsideNotice(userJson);
		}
	}
	
	/**
	 * 
	 * @Title: sendEmailToStaff
	 *
	 * @Description: 发送邮件给指定员工
	 * @param email 邮箱
	 * @param content 内容
	 * @param createId 创建人
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
	private void sendEmailToStaff(String email, String content, String createId) throws Exception{
		Map<String, Object> emailNotice = new HashMap<>();
		emailNotice.put("title", MESSAGE_TITLE);
		emailNotice.put("content", content);
		emailNotice.put("email", email);
		emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
		jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), createId);
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: sendInsideNotice
	    * @Description: 内部消息通知通用方法
	    * @param userJson 内部消息通知对象
	    * @return void    返回类型
	    * @throws
	 */
	private void sendInsideNotice(List<Map<String, Object>> userJson) throws Exception{
		//调用消息系统添加通知
		List<Map<String, Object>> notices = new ArrayList<>();
		Map<String, Object> notice = null;
		for(int i = 0; i < userJson.size(); i++){
			Map<String, Object> userJsonObject = userJson.get(i);
			notice = new HashMap<>();
			notice.put("id", ToolUtil.getSurFaceId());
			notice.put("title", MESSAGE_TITLE);
			notice.put("noticeDesc", "您有一条新的工作计划信息，请及时阅读。");
			notice.put("content", userJsonObject.get("content").toString());
			notice.put("state", "1");//未读消息
			notice.put("userId", userJsonObject.get("userId").toString());
			notice.put("type", "2");//消息类型
			notice.put("createId", "0dc9dd4cd4d446ae9455215fe753c44e");//默认系统管理员
			notice.put("createTime", DateUtil.getTimeAndToString());
			notices.add(notice);
		}
		if(!notices.isEmpty())
			sysWorkPlanDao.insertNoticeListMation(notices);
	}

	/**
	 * 
	     * @Title: insertSysWorkPlanISDepartMent
	     * @Description: 新增部门计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysWorkPlanISDepartMent(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		//计划id
		String planId = ToolUtil.getSurFaceId();
		// 分配类型  1.自己执行  2.他人执行,如果为他人执行，则执行人不能为空
		if("2".equals(map.get("assignmentType").toString())){
			//计划执行人为空，返回提示信息
			if(ToolUtil.isBlank(map.get("carryPeople").toString())){
				outputObject.setreturnMessage("请选择计划执行人。");
				return;
			}
		}

		// 是否通知
		String whetherNotify = "1";
		// 是否定时通知  1.是  2.否
		if("1".equals(map.get("whetherTime").toString())){
			// 如果为定时通知，则通知时间不能为空
			if(ToolUtil.isBlank(map.get("notifyTime").toString())){
				outputObject.setreturnMessage("请选择通知时间。");
				return;
			}
			if(!DateUtil.compare(DateUtil.getTimeAndToString(), map.get("notifyTime").toString())){//定时时间小于当前时间
				outputObject.setreturnMessage("定时发送时间不能小于当前时间");
				return;
			}
			// 启动定时任务
			quartzService.startUpTaskQuartz(planId, map.get("title").toString(), map.get("notifyTime").toString(), userId,
					QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
		}else{
			// 非定时通知，执行邮件判断，内部通知判断,通知状态设置为已通知
			whetherNotify = "2";
			sendMessageToStaff(map, userId, map.get("carryPeople").toString());
		}
		
		map.put("id", planId);
		map.put("planCycle", WorkPlanConstants.SysWorkPlan.getClockInState(map.get("nowCheckType").toString()));//转化计划周期类型为数字
		map.put("planType", "2");//计划类型  1.个人计划  2.部门计划  3.公司计划
		map.put("whetherNotify", whetherNotify);//是否已经通知  1.未通知  2.已通知
		map.put("createId", userId);//计划创建人
		map.put("createTime", DateUtil.getTimeAndToString());//创建时间
		sysWorkPlanDao.insertSysWorkPlanISPeople(map);
		// 修改计划执行者
		editWorkPlanExecutors(map, planId);
	}

	/**
	 * 修改计划执行者
	 *
	 * @param map 日程的通知信息
	 * @param planId 计划id
	 * @throws Exception
	 */
	private void editWorkPlanExecutors(Map<String, Object> map, String planId) throws Exception {
		// 获取计划执行者
		List<Map<String, Object>> executors = getTypeISTeamExecutor(map.get("carryPeople").toString().split(","), planId);
		if(!executors.isEmpty()) {
			sysWorkPlanDao.insertSysWorkPlanExecutors(executors);
			executors.forEach(bean -> {
				// 同步到日程
				try {
					scheduleDayService.synchronizationSchedule(map.get("title").toString(), map.get("content").toString(),
							map.get("startTime").toString(), map.get("endTime").toString(), bean.get("userId").toString(), planId,
							ScheduleDayConstants.ScheduleDayObjectType.OBJECT_TYPE_IS_PLAN);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * 
	     * @Title: insertSysWorkPlanISCompany
	     * @Description: 新增公司计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysWorkPlanISCompany(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		//计划id
		String planId = ToolUtil.getSurFaceId();
		//分配类型  1.自己执行  2.他人执行
		//如果为他人执行，则执行人不能为空
		if("2".equals(map.get("assignmentType").toString())){
			//计划执行人为空，返回提示信息
			if(ToolUtil.isBlank(map.get("carryPeople").toString())){
				outputObject.setreturnMessage("请选择计划执行人。");
				return;
			}
		}
		// 是否通知
		String whetherNotify = "1";
		// 是否定时通知  1.是  2.否
		if("1".equals(map.get("whetherTime").toString())){
			// 如果为定时通知，则通知时间不能为空
			if(ToolUtil.isBlank(map.get("notifyTime").toString())){
				outputObject.setreturnMessage("请选择通知时间。");
				return;
			}
			if(!DateUtil.compare(DateUtil.getTimeAndToString(), map.get("notifyTime").toString())){//定时时间小于当前时间
				outputObject.setreturnMessage("定时发送时间不能小于当前时间");
				return;
			}
			// 启动定时任务
			quartzService.startUpTaskQuartz(planId, map.get("title").toString(), map.get("notifyTime").toString(), userId,
					QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
		}else{
			// 非定时通知，执行邮件判断，内部通知判断,通知状态设置为已通知
			whetherNotify = "2";
			sendMessageToStaff(map, userId, map.get("carryPeople").toString());
		}
		
		map.put("id", planId);
		map.put("planCycle", WorkPlanConstants.SysWorkPlan.getClockInState(map.get("nowCheckType").toString()));//转化计划周期类型为数字
		map.put("planType", "3");//计划类型  1.个人计划  2.部门计划  3.公司计划
		map.put("whetherNotify", whetherNotify);//是否已经通知  1.未通知  2.已通知
		map.put("createId", userId);//计划创建人
		map.put("createTime", DateUtil.getTimeAndToString());//创建时间
		sysWorkPlanDao.insertSysWorkPlanISPeople(map);
		// 修改计划执行者
		editWorkPlanExecutors(map, planId);
	}

	/**
	 * 
	     * @Title: deleteSysWorkPlanTimingById
	     * @Description: 取消定时发送
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysWorkPlanTimingById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> planMation = sysWorkPlanDao.queryPlanMationByUserIdAndPlanId(map);
		if(planMation != null){
			if("1".equals(planMation.get("whetherTime").toString())){
				// 当前计划有定时任务
				if(DateUtil.compare(DateUtil.getTimeAndToString(), planMation.get("notifyTime").toString())){//定时时间大于当前时间
					// 删除定时任务
					quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
					// 修改计划数据状态
					map.put("whetherTime", 2);
					map.put("notifyTime", null);
					sysWorkPlanDao.updateWhetherTimeById(map);
				}else{
					outputObject.setreturnMessage("定时发送任务已执行，取消失败~");
				}
			}else{
				outputObject.setreturnMessage("数据状态已改变，请刷新数据~");
			}
		}else{
			outputObject.setreturnMessage("您不具备该权限或者该数据已被删除，请刷新数据~");
		}
	}

	/**
	 * 
	     * @Title: deleteSysWorkPlanById
	     * @Description: 删除计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysWorkPlanById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String planId = map.get("id").toString();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> planMation = sysWorkPlanDao.queryPlanMationByUserIdAndPlanId(map);
		if(planMation != null){
			// 删除定时任务
			quartzService.stopAndDeleteTaskQuartz(planId, QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
			// 删除计划
			sysWorkPlanDao.deleteSysWorkPlanById(map);
			// 删除日程
			scheduleDayDao.deleteScheduleDayMationByPlanId(planId);
			// 删除计划相关用户绑定信息
			sysWorkPlanDao.deleteSysWorkPlanUserById(planId);
		}else{
			outputObject.setreturnMessage("您不具备该权限或者该数据已被删除，请刷新数据~");
		}
	}

	/**
	 * 
	     * @Title: querySysWorkPlanToEditById
	     * @Description: 编辑计划时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysWorkPlanToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取计划信息
		Map<String, Object> bean = sysWorkPlanDao.querySysWorkPlanToEditById(map);
		bean.put("planCycle", WorkPlanConstants.SysWorkPlan.getClockInName(bean.get("planCycle").toString()));
		//获取计划执行人信息
		List<Map<String, Object>> executors = sysWorkPlanDao.querySysWorkPlanExecutorsToEditById(map);
		//获取附件信息
		List<Map<String, Object>> enclosures = sysWorkPlanDao.querySysWorkPlanEnclosuresToEditById(map);
		bean.put("executors", executors);
		bean.put("enclosures", enclosures);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysWorkPlanISPeople
	     * @Description: 编辑个人计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysWorkPlanISPeople(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		// 计划id
		String planId = map.get("id").toString();
		// 获取计划详情
		Map<String, Object> mation = sysWorkPlanDao.querySysWorkPlanDetailsById(planId, "");
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该计划不存在.");
			return;
		}
		// 计划执行者
		Map<String, Object> executor = getTypeISPeopleExecutor(map, userId, planId);
		if(executor == null){
			outputObject.setreturnMessage("请选择计划执行人。");
			return;
		}
		
		//是否定时通知  1.是  2.否
		//如果为定时通知，则通知时间不能为空
		if("1".equals(map.get("whetherTime").toString())){
			//通知时间为空，返回提示信息
			if(ToolUtil.isBlank(map.get("notifyTime").toString())){
				outputObject.setreturnMessage("请选择通知时间。");
				return;
			}
			if(!DateUtil.compare(DateUtil.getTimeAndToString(), map.get("notifyTime").toString())){//定时时间小于当前时间
				outputObject.setreturnMessage("定时发送时间不能小于当前时间");
				return;
			}
			//启动定时任务
			quartzService.startUpTaskQuartz(planId, map.get("title").toString(), map.get("notifyTime").toString(), userId,
					QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
		}else{
			// 非定时通知，执行邮件判断，内部通知判断
			sendMessageToStaff(map, userId, executor.get("userId").toString());
		}
		// 删除日程
		scheduleDayDao.deleteScheduleDayMationByPlanId(planId);
		// 删除计划相关用户绑定信息
		sysWorkPlanDao.deleteSysWorkPlanUserById(planId);
		// 修改数据
		sysWorkPlanDao.editSysWorkPlanISPeople(map);
		// 重新插入计划接收人
		sysWorkPlanDao.insertSysWorkPlanExecutorISPeople(executor);
		// 同步到日程
		scheduleDayService.synchronizationSchedule(map.get("title").toString(), map.get("content").toString(),
				mation.get("startTime").toString(), mation.get("endTime").toString(), executor.get("userId").toString(), planId,
				ScheduleDayConstants.ScheduleDayObjectType.OBJECT_TYPE_IS_PLAN);
	}

	/**
	 * 
	     * @Title: editSysWorkPlanISDepartMentOrCompany
	     * @Description: 编辑部门或者公司计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysWorkPlanISDepartMentOrCompany(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String userId = user.get("id").toString();
		// 计划id
		String planId = map.get("id").toString();
		// 获取计划详情
		Map<String, Object> mation = sysWorkPlanDao.querySysWorkPlanDetailsById(planId, "");
		if(mation == null || mation.isEmpty()){
			outputObject.setreturnMessage("该计划不存在.");
			return;
		}
		// 分配类型  1.自己执行  2.他人执行,如果为他人执行，则执行人不能为空
		if("2".equals(map.get("assignmentType").toString())){
			//计划执行人为空，返回提示信息
			if(ToolUtil.isBlank(map.get("carryPeople").toString())){
				outputObject.setreturnMessage("请选择计划执行人。");
				return;
			}
		}
		// 是否定时通知  1.是  2.否
		if("1".equals(map.get("whetherTime").toString())){
			// 如果为定时通知，则通知时间不能为空
			if(ToolUtil.isBlank(map.get("notifyTime").toString())){
				outputObject.setreturnMessage("请选择通知时间。");
				return;
			}
			if(!DateUtil.compare(DateUtil.getTimeAndToString(), map.get("notifyTime").toString())){//定时时间小于当前时间
				outputObject.setreturnMessage("定时发送时间不能小于当前时间");
				return;
			}
			// 启动定时任务
			quartzService.startUpTaskQuartz(planId, map.get("title").toString(), map.get("notifyTime").toString(), userId,
					QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
		}else{
			// 非定时通知，执行邮件判断，内部通知判断,通知状态设置为已通知
			sendMessageToStaff(map, userId, map.get("carryPeople").toString());
		}
		// 删除日程
		scheduleDayDao.deleteScheduleDayMationByPlanId(planId);
		// 删除计划相关用户绑定信息
		sysWorkPlanDao.deleteSysWorkPlanUserById(planId);
		// 修改数据
		sysWorkPlanDao.editSysWorkPlanISPeople(map);
		// 修改计划执行者(重新插入计划接收人)
		mation.put("title", map.get("title"));
		mation.put("content", map.get("content"));
		editWorkPlanExecutors(mation, planId);
	}

	/**
	 * 
	     * @Title: editSysWorkPlanTimingSend
	     * @Description: 定时发送
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysWorkPlanTimingSend(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Map<String, Object> planMation = sysWorkPlanDao.queryPlanMationByUserIdAndPlanId(map);
		if(planMation != null){
			// 计划id
			String planId = map.get("id").toString();
			// 启动定时任务
			quartzService.startUpTaskQuartz(planId, planMation.get("title").toString(), map.get("notifyTime").toString(), user.get("id").toString(),
					QuartzConstants.QuartzMateMationJobType.PLAN_QUARTZ_GROUPS.getTaskType());
			sysWorkPlanDao.editSysWorkPlanTimingSend(map);
		}else{
			outputObject.setreturnMessage("您不具备该权限或者该数据已被删除，请刷新数据~");
		}
	}

	/**
	 * 
	     * @Title: querySysWorkPlanDetailsById
	     * @Description: 计划详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysWorkPlanDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取计划信息
		Map<String, Object> bean = sysWorkPlanDao.querySysWorkPlanDetailsById(map.get("id").toString(), map.get("executorId").toString());
		if(bean != null && !bean.isEmpty()){
			bean.put("planCycle", WorkPlanConstants.SysWorkPlan.getClockInName(bean.get("planCycle").toString()));
			//获取计划执行人信息
			List<Map<String, Object>> executors = sysWorkPlanDao.querySysWorkPlanExecutorsToEditById(map);
			//获取附件信息
			List<Map<String, Object>> enclosures = sysWorkPlanDao.querySysWorkPlanEnclosuresToEditById(map);
			bean.put("executors", executors);
			bean.put("enclosures", enclosures);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该计划不存在.");
		}
	}

	/**
	 * 获取我的任务计划列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMySysWorkPlanListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysWorkPlanDao.queryMySysWorkPlanListByUserId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 计划状态变更
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void subEditWorkPlanStateById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		Map<String, Object> executorMation = sysWorkPlanDao.queryMySysWorkPlanMationByUserId(map.get("id").toString(), userId);
		int nowState = Integer.parseInt(executorMation.get("state").toString());
		if(PLAN_EXECUTOR_STATE.START_NEW.getState() == nowState){
			// 待执行
			map.put("userId", userId);
			sysWorkPlanDao.subEditWorkPlanStateById(map);
		}else{
			outputObject.setreturnMessage("this data is changed.");
		}
	}

	/**
	 * 获取我创建的任务计划列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMyCreateSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysWorkPlanDao.queryMyCreateSysWorkPlanList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 获取所有任务计划列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysWorkPlanDao.queryAllSysWorkPlanList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 获取我的下属的任务计划列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMyBranchSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		// 获取我的下属职位员工
		List<Map<String, Object>> jobChildUser = sysWorkPlanDao.queryMyChildJobUserListByUserId(userId);
		if(jobChildUser != null && !jobChildUser.isEmpty()){
			List<String> jodUserId = jobChildUser.stream().map(p -> p.get("user_id").toString()).collect(Collectors.toList());
			map.put("list", jodUserId);
			Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
			List<Map<String, Object>> beans = sysWorkPlanDao.queryMyBranchSysWorkPlanList(map);
			outputObject.setBeans(beans);
			outputObject.settotal(pages.getTotal());
		}
	}

}
