/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CheckWorkConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ScheduleDayDao;
import com.skyeye.eve.service.CheckWorkService;
import com.skyeye.eve.service.ScheduleDayService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.quartz.config.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.*;

/**
 *
 * @ClassName: ScheduleDayServiceImpl
 * @Description: 日程相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:43
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ScheduleDayServiceImpl implements ScheduleDayService{

	private static Logger LOGGER = LoggerFactory.getLogger(ScheduleDayServiceImpl.class);
	
	@Autowired
	private ScheduleDayDao scheduleDayDao;
	
	@Autowired
	private QuartzService quartzService;

	@Autowired
	public JedisClientService jedisClient;

	@Autowired
	private CheckWorkService checkWorkService;
	
	/**
	 * 
	     * @Title: insertScheduleDayMation
	     * @Description: 添加日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertScheduleDayMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		//获取提醒时间类型	1.日程开始时 2.5分钟前 3.15分钟前 4.30分钟前 5.1小时前 6.2小时前 7.1天前 8.2天前 9.一周前
		int remindTimeType = Integer.parseInt(map.get("remindType").toString());
		String remindTime = null;
		if(remindTimeType == 1){//日程开始时
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 0, remindTimeType);
		}else if(remindTimeType == 2){//5分钟前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 5, remindTimeType);
		}else if(remindTimeType == 3){//15分钟前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 15, remindTimeType);
		}else if(remindTimeType == 4){//30分钟前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 30, remindTimeType);
		}else if(remindTimeType == 5){//1小时前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 1, remindTimeType);
		}else if(remindTimeType == 6){//2小时前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 2, remindTimeType);
		}else if(remindTimeType == 7){//1天前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 1, remindTimeType);
		}else if(remindTimeType == 8){//2天前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 2, remindTimeType);
		}else if(remindTimeType == 9){//1周前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 7, remindTimeType);
		}else{
			outputObject.setreturnMessage("传值错误。");
			return;
		}
		if(!ToolUtil.isBlank(remindTime)){
			if(DateUtil.compare(remindTime, DateUtil.getTimeAndToString())){//日程提醒时间早于当前时间
				outputObject.setreturnMessage("日程提醒时间不能早于当前时间");
			}else{
				if(DateUtil.compare(map.get("scheduleEndTime").toString(), map.get("scheduleStartTime").toString())){//结束时间早于开始时间
					outputObject.setreturnMessage("日程结束时间不能早于开始时间");
				}else{
					String id = ToolUtil.getSurFaceId();
					map.put("remindTime", remindTime);
					map.put("id", id);
					map.put("createId", user.get("id"));
					map.put("createTime", DateUtil.getTimeAndToString());
					scheduleDayDao.insertScheduleDayMation(map);
					// 定时任务
					quartzService.startUpTaskQuartz(id, map.get("scheduleTitle").toString(), remindTime, user.get("id").toString(),
							QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
					outputObject.setBean(map);
				}
			}
		}else{
			outputObject.setreturnMessage("参数错误");
		}
	}

	/**
	 * 
	     * @Title: queryScheduleDayMationByUserId
	     * @Description: 根据用户日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryScheduleDayMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		String yearMonth = map.get("yearMonth").toString();
		String timeId = map.get("checkWorkId").toString();
		List<String> months = DateUtil.getPointMonthAfterMonthList(yearMonth, 2);
		// 1.获取当前登录人指定月份的日程信息
		List<Map<String, Object>> beans = scheduleDayDao.queryScheduleDayMationByUserId(userId, months);
		checkWorkService.queryDayWorkMation(beans, months, timeId);
		// 2.获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
		beans.addAll(checkWorkService.getUserOtherDayMation(userId, timeId, months));
		outputObject.setBeans(beans);
	}

	/**
	 * 
	     * @Title: queryScheduleDayMationTodayByUserId
	     * @Description: 根据用户获取今日日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryScheduleDayMationTodayByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> beans = scheduleDayDao.queryScheduleDayMationTodayByUserId(map);
		outputObject.setBeans(beans);
		if(beans != null && !beans.isEmpty())
			outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: editScheduleDayMationById
	     * @Description: 修改日程日期信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = scheduleDayDao.queryScheduleDayMationById(map);
		//获取提醒时间类型	1.日程开始时 2.5分钟前 3.15分钟前 4.30分钟前 5.1小时前 6.2小时前 7.1天前 8.2天前 9.一周前
		int remindTimeType = Integer.parseInt(bean.get("remindType").toString());
		String remindTime = null;
		if(remindTimeType == 1){//日程开始时
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 0, remindTimeType);
		}else if(remindTimeType == 2){//5分钟前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 5, remindTimeType);
		}else if(remindTimeType == 3){//15分钟前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 15, remindTimeType);
		}else if(remindTimeType == 4){//30分钟前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 30, remindTimeType);
		}else if(remindTimeType == 5){//1小时前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 1, remindTimeType);
		}else if(remindTimeType == 6){//2小时前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 2, remindTimeType);
		}else if(remindTimeType == 7){//1天前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 1, remindTimeType);
		}else if(remindTimeType == 8){//2天前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 2, remindTimeType);
		}else if(remindTimeType == 9){//1周前
			remindTime = DateUtil.getSpecifiedDayMation(map.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 7, remindTimeType);
		}else{
			outputObject.setreturnMessage("传值错误。");
			return;
		}
		if(!ToolUtil.isBlank(remindTime)){
			if(DateUtil.compare(remindTime, DateUtil.getTimeAndToString())){//日程提醒时间早于当前时间,提醒时间则变为当前时间+2分钟
				long minute = DateUtil.getDistanceMinute(remindTime, map.get("scheduleStartTime").toString());//获取当前时间和开始时间相差几分钟
				if(minute >= 2){
					remindTime = DateUtil.getSpecifiedDayMation(DateUtil.getTimeAndToString(), "yyyy-MM-dd HH:mm:ss", 1, 2, remindTimeType);//两分钟后
				}else if(minute < 2 && minute > 0){
					remindTime = DateUtil.getSpecifiedDayMation(DateUtil.getTimeAndToString(), "yyyy-MM-dd HH:mm:ss", 1, (int)minute, remindTimeType);//minute分钟后
				}else{
					remindTime = map.get("scheduleStartTime").toString();//日程开始时
				}
			}else if(DateUtil.compare(map.get("scheduleEndTime").toString(), map.get("scheduleStartTime").toString())){//结束时间早于开始时间
				outputObject.setreturnMessage("日程结束时间不能早于开始时间");
				return;
			}
			map.put("remindTime", remindTime);
			scheduleDayDao.editScheduleDayMationById(map);
			// 删除缓存中的日程信息
			Map<String, Object> user = inputObject.getLogParams();
			// 修改定时任务
			quartzService.startUpTaskQuartz(map.get("id").toString(), map.get("scheduleTitle").toString(), remindTime, user.get("id").toString(),
					QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
		}else{
			outputObject.setreturnMessage("参数错误");
		}
	}
	
	/**
	 * 
	     * @Title: queryScheduleDayMationById
	     * @Description: 获取日程详细信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = scheduleDayDao.queryScheduleDayDetailsMationById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: deleteScheduleDayMationById
	     * @Description: 删除日程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteScheduleDayMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 删除缓存中的日程信息
		Map<String, Object> user = inputObject.getLogParams();
		// 删除日程信息
		scheduleDayDao.deleteScheduleDayMationById(map);
		// 删除定时任务
		quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
	}
	
	/**
	 * 
	     * @Title: queryHolidayScheduleList
	     * @Description: 获取系统发布的请假日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryHolidayScheduleList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = scheduleDayDao.queryHolidayScheduleList(map);
		for(Map<String, Object> str : beans){  //遍历数组
			boolean before = DateUtil.compare(DateUtil.getTimeAndToString(), str.get("startTime").toString());
			if(before){  //当前时间 在 节假日开始时间 之前
				str.put("before", "1");
			}else{
				str.put("before", "2");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: downloadScheduleTemplate
	     * @Description: 下载节假日导入模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void downloadScheduleTemplate(InputObject inputObject, OutputObject outputObject) throws Exception {
		String[] key = new String[9];
		String[] column = new String[]{"标题", "开始日期", "结束日期"};
		String[] dataType = new String[]{"", "data", "data"};
		ExcelUtil.createWorkBook("节假日模板", "节假日", null, key, column, dataType, inputObject.getResponse());
	}

	/**
	 * 
	     * @Title: exploreScheduleTemplate
	     * @Description: 导入节假日日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void exploreScheduleTemplate(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(inputObject.getRequest().getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(inputObject.getRequest())) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) inputObject.getRequest();
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();
			//循环内使用参数
			MultipartFile file;//获取到文件
			List<List<String>> list;//excel读取到的数据
			List<Map<String, Object>> beans;//数据库存储的数据
			String nowTime = DateUtil.getTimeAndToString(), startTime, endTime;//当前时间，日程开始时间，日程结束时间
			int uploadNum;//上传成功数
			List<String> row;//获取每一行的数据
			Map<String, Object> bean;//存储实体bean
			while (iter.hasNext()) {
				// 一次遍历所有文件
				file = multiRequest.getFile(iter.next().toString());
				list = ExcelUtil.readExcelContent(file.getInputStream());
				beans = new ArrayList<>();
				uploadNum = 0;
				for (int i = 0, j = list.size(); i < j; i++){
					row = list.get(i);
					if(!ToolUtil.isBlank(row.get(1).toString()) && !ToolUtil.isBlank(row.get(1).toString())){
						uploadNum++;
						startTime = row.get(1).toString() + " 00:00:00";//日程开始时间
						endTime = row.get(2).toString() + " 23:59:59";//日程结束时间
						bean = new HashMap<>();
						bean.put("id", ToolUtil.getSurFaceId());
						bean.put("createId", user.get("id"));
						bean.put("createTime", nowTime);
						bean.put("allDay", 1);//全天
						bean.put("remindType", 0);//默认无需提醒
						bean.put("type", 3);//默认节假日
						bean.put("typeName", "节假日");//类型名称
						if(DateUtil.compare(startTime, nowTime)){//日程开始时间早于当前时间
							bean.put("state", 1);//已结束
						}else{
							bean.put("state", 0);//新日程
						}
						bean.put("import", 1);//普通
						bean.put("isRemind", 0);//默认不需要提醒
						if(ToolUtil.isBlank(row.get(0).toString())){
							bean.put("title", "休息日");
						}else{
							bean.put("title", row.get(0));
						}
						bean.put("startTime", startTime);//日程开始时间
						bean.put("endTime", endTime);//日程结束时间
						beans.add(bean);
					}
				}
				if(!beans.isEmpty() && beans != null){
					scheduleDayDao.exploreScheduleTemplate(beans);
				}
				map.put("uploadNum", uploadNum);
				outputObject.setBean(map);
			}
		}
	}
	
	/**
	 * 
	     * @Title: deleteHolidayScheduleById
	     * @Description: 删除节假日日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteHolidayScheduleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 删除节假日信息
		scheduleDayDao.deleteHolidayScheduleById(map);
		// 删除定时任务
		quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
	}

	/**
	 * 
	     * @Title: deleteHolidayScheduleByThisYear
	     * @Description: 删除本年度节假日日程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteHolidayScheduleByThisYear(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = scheduleDayDao.queryHolidayScheduleByThisYear(map);//获取本年度节假日
		for(Map<String, Object> bean: beans){
			// 删除定时任务
			quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
		}
		scheduleDayDao.deleteHolidayScheduleByThisYear(map);
	}

	/**
	 * 
	     * @Title: addHolidayScheduleRemind
	     * @Description: 添加节假日日程提醒
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = scheduleDayDao.queryHolidayScheduleDayMationById(map);
		//获取提醒时间类型	1.日程开始时 2.5分钟前 3.15分钟前 4.30分钟前 5.1小时前 6.2小时前 7.1天前 8.2天前 9.一周前
		int remindTimeType = Integer.parseInt(map.get("remindType").toString());
		String remindTime = null;
		if(remindTimeType == 1){//日程开始时
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 0, remindTimeType);
		}else if(remindTimeType == 2){//5分钟前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 5, remindTimeType);
		}else if(remindTimeType == 3){//15分钟前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 15, remindTimeType);
		}else if(remindTimeType == 4){//30分钟前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 30, remindTimeType);
		}else if(remindTimeType == 5){//1小时前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 1, remindTimeType);
		}else if(remindTimeType == 6){//2小时前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 2, remindTimeType);
		}else if(remindTimeType == 7){//1天前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 1, remindTimeType);
		}else if(remindTimeType == 8){//2天前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 2, remindTimeType);
		}else if(remindTimeType == 9){//1周前
			remindTime = DateUtil.getSpecifiedDayMation(bean.get("scheduleStartTime").toString(), "yyyy-MM-dd HH:mm:ss", 0, 7, remindTimeType);
		}else{
			outputObject.setreturnMessage("传值错误。");
			return;
		}
		if(!ToolUtil.isBlank(remindTime)){
			if(DateUtil.compare(remindTime, DateUtil.getTimeAndToString())){//日程提醒时间早于当前时间
				outputObject.setreturnMessage("日程提醒时间不能早于当前时间");
			}else{
				if(DateUtil.compare(bean.get("scheduleEndTime").toString(), bean.get("scheduleStartTime").toString())){//结束时间早于开始时间
					outputObject.setreturnMessage("日程结束时间不能早于开始时间");
				}else{
					map.put("remindTime", remindTime);
					scheduleDayDao.addHolidayScheduleRemind(map);
					// 定时任务
					quartzService.startUpTaskQuartz(map.get("id").toString(), bean.get("scheduleTitle").toString(), remindTime, inputObject.getLogParams().get("id").toString(),
							QuartzConstants.QuartzMateMationJobType.ALL_SCHEDULE_MATION.getTaskType());
				}
			}
		}else{
			outputObject.setreturnMessage("参数错误");
		}
	}

	/**
	 * 
	     * @Title: deleteHolidayScheduleRemind
	     * @Description: 取消节假日日程提醒
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 修改节假日信息
		scheduleDayDao.deleteHolidayScheduleRemind(map);
		// 删除定时任务
		quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.ALL_SCHEDULE_MATION.getTaskType());
	}

	/**
	 * 
	     * @Title: queryScheduleByIdToEdit
	     * @Description: 回显节假日信息以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryScheduleByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = scheduleDayDao.queryScheduleByIdToEdit(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editScheduleById
	     * @Description: 编辑节假日
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editScheduleById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		scheduleDayDao.editScheduleById(map);
	}

	/**
	 * 
	     * @Title: addSchedule
	     * @Description: 新增节假日
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void addSchedule(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = scheduleDayDao.queryIsnullThistime(map);
		if(m != null && !m.isEmpty()){
			outputObject.setreturnMessage("该节假日时间段与已有的节假日时间段有冲突，请重新设置节假日");
		}else{
			map.put("id", ToolUtil.getSurFaceId());
			map.put("allDay", "1");
			map.put("remindType", "0");
			map.put("type", CheckWorkConstants.CheckDayType.DAY_IS_HOLIDAY.getType());
			map.put("typeName", "节假日");
			map.put("state", "0");
			map.put("import", "1");
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			map.put("isRemind", "0");
			scheduleDayDao.addSchedule(map);
		}
		
	}
	
	/**
	 * 
	     * @Title: queryHolidayScheduleListBySys
	     * @Description: 获取所有节假日
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryHolidayScheduleListBySys(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = scheduleDayDao.queryHolidayScheduleListBySys(map);
		outputObject.setBeans(beans);
	}

	/**
	 * 获取我的日程
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryMyScheduleList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = scheduleDayDao.queryMyScheduleList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 将其他模块同步到日程
	 * 不启动日程定时任务，如需启动，在该接口外面自行启动
	 *
	 * @param title 标题
	 * @param content 日程内容
	 * @param startTime 开始时间,格式为：yyyy-MM-dd HH:mm:ss
	 * @param endTime 结束时间,格式为：yyyy-MM-dd HH:mm:ss
	 * @param userId 执行人
	 * @param objectId 关联id
	 * @param objectType object类型：1.任务计划id，2.项目任务id
	 * @throws Exception
	 */
	public String synchronizationSchedule(String title, String content, String startTime, String endTime, String userId, String objectId, int objectType) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String scheduleId = ToolUtil.getSurFaceId();
		map.put("id", scheduleId);
		map.put("scheduleTitle", title);
		int length = content.length();
		map.put("remarks", length > 1000 ? content.substring(0, 1000) : content);
		map.put("allDay", 1); // 是否全天 0否 1是
		map.put("scheduleStartTime", startTime);
		map.put("scheduleEndTime", endTime);
		map.put("remindType", 0); // 提醒时间所属类型  0.无需提醒
		map.put("type", 2); // 日程类型2工作
		map.put("typeName", "工作");
		map.put("import", "2"); // 日程重要性  1普通  2重要
		map.put("objectId", objectId);
		// object类型：1.任务计划id，2.项目任务id
		map.put("objectType", objectType);
		map.put("createId", userId);
		map.put("createTime", DateUtil.getTimeAndToString());
		scheduleDayDao.insertScheduleDayMation(map);
		return scheduleId;
	}

}
