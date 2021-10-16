/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.dao.WagesFieldTypeDao;
import com.skyeye.eve.service.SysEveUserStaffService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: SysEveUserStaffServiceImpl
 * @Description: 员工管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:02
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysEveUserStaffServiceImpl implements SysEveUserStaffService {

	@Autowired
	private SysEveUserStaffDao sysEveUserStaffDao;
	
	@Autowired
	public JedisClientService jedisClient;

	@Autowired
	private WagesFieldTypeDao wagesFieldTypeDao;
	
	/**
	 * 
	     * @Title: querySysUserStaffList
	     * @Description: 查出所有员工列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserStaffList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveUserStaffDao.querySysUserStaffList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSysUserStaffMation
	     * @Description: 新增员工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysUserStaffMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserStaffDao.querySysUserStaffMationByIdCard(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该员工身份证已存在，不能重复添加！");
		}else{
			insertNewUserMation(map);
		}
	}
	
	/**
	 * 
	 * Title: insertNewUserMation
	 * Description: 新增员工信息
	 * @param map
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysEveUserStaffService#insertNewUserMation(java.util.Map)
	 */
	@Override
	public void insertNewUserMation(Map<String, Object> map) throws Exception{
		String staffId = ToolUtil.getSurFaceId();
		map.put("id", staffId);
		map.put("state", "1");
		// 1.新增员工信息
		sysEveUserStaffDao.insertSysUserStaffMation(map);
		// 2.新增员工考勤时间段
		createUserStaffCheckWorkTime(map, staffId);
		// 3.新增员工薪资字段信息
		createUserStaffWagesFieldType(staffId);
	}

	/**
	 * 新增员工薪资字段信息
	 *
	 * @param staffId
	 */
	private void createUserStaffWagesFieldType(String staffId) throws Exception {
		List<Map<String, Object>> fieldType = wagesFieldTypeDao.queryAllWagesFieldTypeList();
		if(fieldType != null && !fieldType.isEmpty()){
			fieldType.stream().forEach(bean -> {
				bean.put("id", staffId);
			});
			wagesFieldTypeDao.insertWagesFieldTypeKeyToStaff(fieldType);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 * @Title: createUserStaffCheckWorkTime
	 * @Description: 新增员工考勤时间段
	 * @param map
	 * @param staffId 员工id
	 * @return: void
	 * @throws
	 */
	private void createUserStaffCheckWorkTime(Map<String, Object> map, String staffId) throws Exception {
		// 逗号隔开的多班次考勤
		String str = map.containsKey("checkTimeStr") ? map.get("checkTimeStr").toString() : "";
		if(!ToolUtil.isBlank(str)){
			List<String> timeIds = Arrays.asList(str.split(","));
			// 校验多班次考勤是否有重复时间段
			boolean repeat = judgeRepeatShift(timeIds);
			if(repeat){
				// 存在冲突的工作时间段
				throw new CustomException("Conflicting working hours.");
			}
			List<Map<String, Object>> staffTimeMation = new ArrayList<>();
			timeIds.stream().forEach(timeId -> {
				if(!ToolUtil.isBlank(timeId)){
					Map<String, Object> bean = new HashMap<>();
					bean.put("staffId", staffId);
					bean.put("timeId", timeId);
					staffTimeMation.add(bean);
				}
			});
			if(!staffTimeMation.isEmpty()){
				sysEveUserStaffDao.insertStaffCheckWorkTimeRelation(staffTimeMation);
			}
		}
	}

	private boolean judgeRepeatShift(List<String> timeIds) throws Exception {
		// 1.获取班次的上下班打卡时间
		List<Map<String, Object>> timeMation = sysEveUserStaffDao.queryCheckTimeMationByTimeIds(timeIds);
		// 2.获取班次的工作日
		List<Map<String, Object>> timeDayMation = sysEveUserStaffDao.queryCheckTimeDaysMationByTimeIds(timeIds);
		timeMation.forEach(bean -> {
			List<Map<String, Object>> thisDayMation = timeDayMation.stream()
					.filter(item -> item.get("timeId").toString().equals(bean.get("timeId").toString()))
					.collect(Collectors.toList());
			bean.put("days", thisDayMation);
		});
		// 3.校验工作日是否冲突
		return judgeRepeatWorking(timeMation);
	}

	private boolean judgeRepeatWorking(List<Map<String, Object>> timeMation) throws Exception {
		if (timeMation.size() > 1) {
			for (int i = 0; i < timeMation.size(); i++) {
				for (int j = (i + 1); j < timeMation.size(); j++) {
					List<String> times = new ArrayList<>();
					times.add(timeMation.get(i).get("startTime").toString() + "-"
							+ timeMation.get(i).get("endTime").toString());
					times.add(timeMation.get(j).get("startTime").toString() + "-"
							+ timeMation.get(j).get("endTime").toString());
					// 1.首先判断每天的工作日的开始时间和结束时间是否有重复
					boolean flag = DateUtil.checkOverlap(times);
					if (flag) {
						// 开始时间和结束时间是否有重复
						List<Map<String, Object>> iDayMation = (List<Map<String, Object>>) timeMation.get(i)
								.get("days");
						List<Map<String, Object>> jDayMation = (List<Map<String, Object>>) timeMation.get(j)
								.get("days");
						// 求这两个班次的工作日冲突的天数，根据类型和工作日(周几)判断
						int size = iDayMation.stream()
								.map(t -> jDayMation.stream()
										.filter(s -> (Objects.equals(t.get("type").toString(), s.get("type").toString())
												|| Objects.equals(t.get("type").toString(), "1")
												|| Objects.equals(s.get("type").toString(), "1"))
												&& Objects.equals(t.get("day").toString(), s.get("day").toString()))
								.findAny().orElse(null)).filter(Objects::nonNull).collect(Collectors.toList()).size();
						if (size > 0) {
							return true;
						}
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * 
	     * @Title: querySysUserStaffById
	     * @Description: 通过id查询一条员工信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserStaffById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveUserStaffDao.querySysUserStaffById(map);
		if (bean != null && !bean.isEmpty()) {
			// 1.员工考勤时间段信息
			List<Map<String, Object>> staffTimeMation = sysEveUserStaffDao
					.queryStaffCheckWorkTimeRelationByStaffId(bean.get("id").toString());
			bean.put("checkTimeStr", staffTimeMation);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		} else {
			outputObject.setreturnMessage("The data does not exist");
		}
	}

	/**
	 * 
	     * @Title: editSysUserStaffById
	     * @Description: 编辑员工信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysUserStaffById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String staffId = map.get("id").toString();
		Map<String, Object> bean = sysEveUserStaffDao.querySysUserStaffMationByIdCardAndId(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该员工身份证已存在，不能重复添加！");
		}else{
			// 1.删除员工所在部门的缓存
			Map<String, Object> userMation = sysEveUserStaffDao.querySysUserStaffById(map);
			if(userMation != null && !userMation.isEmpty()){
				jedisClient.delKeys(Constants.getSysTalkGroupUserListMationById(userMation.get("departmentId").toString()) + "*");
			}
			// 1.1删除用户在redis中存储的好友组信息
			jedisClient.delKeys(Constants.getSysTalkGroupUserListMationById(map.get("departmentId").toString()) + "*");
			// 2.编辑员工信息
			sysEveUserStaffDao.editSysUserStaffById(map);
			// 3.删除员工考勤时间段信息再重新添加
			sysEveUserStaffDao.deleteStaffCheckWorkTimeRelationByStaffId(staffId);
			// 3.1.新增员工考勤时间段
			createUserStaffCheckWorkTime(map, staffId);
		}
	}

	/**
	 * 
	     * @Title: querySysUserStaffById
	     * @Description: 通过id查询一条员工信息展示详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysUserStaffByIdToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String staffId = map.get("id").toString();
		Map<String, Object>	bean = sysEveUserStaffDao.querySysUserStaffByIdToDetails(staffId);
		if(bean != null && !bean.isEmpty()){
			// 1.员工考勤时间段信息
			List<Map<String, Object>> staffTimeMation = sysEveUserStaffDao
					.queryStaffCheckWorkTimeRelationNameByStaffId(staffId);
			bean.put("checkTimeStr", staffTimeMation);
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("The data does not exist");
		}
	}

	/**
	 * 
	     * @Title: editSysUserStaffState
	     * @Description: 员工离职
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysUserStaffState(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysEveUserStaffDao.editSysUserStaffState(map);
		Map<String, Object>	department = sysEveUserStaffDao.querySysUserId(map);
		if(department != null){
			//删除redis中缓存的单位下的用户
			jedisClient.delKeys(Constants.getSysTalkGroupUserListMationById(department.get("departmentId").toString()) + "*");
			//锁定帐号
			sysEveUserStaffDao.editSysUserLock(department);
		}
	}

	/**
	 * 
	     * @Title: editTurnTeacher
	     * @Description: 普通员工转教职工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editTurnTeacher(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String staffId = map.get("staffId").toString();
		
		//员工类型判断
		Map<String, Object> staffType = sysEveUserStaffDao.queryStaffTypeById(staffId);
		if(staffType != null && !staffType.isEmpty()){
			//如果是普通员工，则允许转教职工
			if("1".equals(staffType.get("staffType").toString())){
				//修改类型
				sysEveUserStaffDao.editStaffTypeById(staffId);
				//添加教职工学校绑定信息
				Map<String, Object> schoolStaff = new HashMap<>();
				schoolStaff.put("id", ToolUtil.getSurFaceId());
				schoolStaff.put("staffId", staffId);
				schoolStaff.put("schoolId", map.get("schoolId"));
				sysEveUserStaffDao.insertSchoolStaffMation(schoolStaff);
			}else{
				outputObject.setreturnMessage("该员工无法转教职工。");
			}
		}else{
			outputObject.setreturnMessage("The data does not exist");
		}
	}

	/**
	 * 
	 * Title: querySysUserStaffListToTable
	 * Description: 查看所有员工列表展示为表格供其他选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysEveUserStaffService#querySysUserStaffListToTable(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysUserStaffListToTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEveUserStaffDao.querySysUserStaffListToTable(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 根据员工ids获取员工信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySysUserStaffListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = sysEveUserStaffDao.queryStaffNameListByIdList(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}

	/**
	 * 获取当前登录员工的信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySysUserStaffLogin(InputObject inputObject, OutputObject outputObject) throws Exception {
		String staffId = inputObject.getLogParams().get("staffId").toString();
		Map<String, Object> bean = sysEveUserStaffDao.querySysUserStaffByIdToDetails(staffId);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

}
