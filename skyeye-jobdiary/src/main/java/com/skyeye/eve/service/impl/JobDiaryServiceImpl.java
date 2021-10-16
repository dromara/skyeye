/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.JobDiaryDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.JobDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: JobDiaryServiceImpl
 * @Description: 工作日报管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:53
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class JobDiaryServiceImpl implements JobDiaryService {
	
	@Autowired
	private JobDiaryDao jobDiaryDao;

	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: queryJobDiaryDayReceived
	     * @Description: 遍历我收到的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryJobDiaryDayReceived(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("receivedId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = jobDiaryDao.queryJobDiaryDayReceived(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertDayJobDiary
	     * @Description: 发表日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("state", "1");
		jobDiaryDao.insertDayJobDiary(map);
		List<Map<String,Object>> beans = new ArrayList<>();
		String[] userId = map.get("userId").toString().split(",");//把字符串以","分截成字符数组
		if(userId.length > 0){  //如果数组长度大于0
			Map<String, Object> item;
			for(String str : userId){  //遍历数组
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("diaryDayId", map.get("id"));
				if(!ToolUtil.isBlank(str)){
					item.put("receivedId", str);
				}
				item.put("state", "1");
				beans.add(item); //把一个个item对象放入集合beans
			}
		}
		jobDiaryDao.insertDayJobDiaryReceived(beans);  //在数据库中插入集合beans
	}

	/**
	 * 
	     * @Title: querySysEveUserStaff
	     * @Description: 查出所有有账户的员工
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
		List<Map<String, Object>> beans = jobDiaryDao.querySysEveUserStaff(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryJobDiaryDetails
	     * @Description: 阅读收到的日志内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void queryJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> state = jobDiaryDao.queryJobDiaryState(map);
		if("1".equals(state.get("state").toString())){
			map.put("readTime", DateUtil.getTimeAndToString());
			jobDiaryDao.editJobDiaryState(map);
		}
		Map<String, Object>	bean = jobDiaryDao.queryJobDiaryDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: queryJobDiaryDayMysend
	     * @Description: 遍历我发出的所有日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = jobDiaryDao.queryJobDiaryDayMysend(map);
		for(Map<String, Object> str : beans){  //遍历数组
			// 计算当前时间和创建时间的时间差，返回分钟
			long twoHour = DateUtil.getDistanceMinute(DateUtil.getTimeAndToString(), str.get("realCreateTime").toString());
			if(twoHour < 120){  //两个小时之内可以撤销
				str.put("isrepeal", "1");
			}else{
				str.put("isrepeal", "2");
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: deleteJobDiaryDayMysend
	     * @Description: 撤销我发出的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> createtime = jobDiaryDao.queryCreateTime(map);
		// 计算当前时间和创建时间的时间差，返回分钟
		long twoHour = DateUtil.getDistanceMinute(DateUtil.getTimeAndToString(), createtime.get("realCreateTime").toString());
		if(twoHour < 120){  //两个小时之内可以撤销
			if("1".equals(createtime.get("diaryType").toString())){
				jobDiaryDao.editJobDiaryDayMysendState(map);  //修改日报表状态
			}else if("2".equals(createtime.get("diaryType").toString())){
				jobDiaryDao.editJobDiaryWeekMysendState(map);  //修改周报表状态
			}else if("3".equals(createtime.get("diaryType").toString())){
				jobDiaryDao.editJobDiaryMonthMysendState(map);  //修改月报表状态
			}
		}else{
			outputObject.setreturnMessage("已超出撤销时间，撤销失败！");
		}
	}

	/**
	 * 
	     * @Title: selectMysendDetails
	     * @Description: 阅读我发出的日报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectMysendDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = jobDiaryDao.selectMysendDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editMyReceivedJobDiary
	     * @Description: 删除我收到的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMyReceivedJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> diaryType = jobDiaryDao.queryDiaryType(map);
		if("1".equals(diaryType.get("diaryType").toString())){
			jobDiaryDao.editMyReceivedJobDiary(map);
		}else if("2".equals(diaryType.get("diaryType").toString())){
			jobDiaryDao.editMyReceivedWeekJobDiary(map);
		}else if("3".equals(diaryType.get("diaryType").toString())){
			jobDiaryDao.editMyReceivedMonthJobDiary(map);
		}
	}

	/**
	 * 
	     * @Title: insertWeekJobDiary
	     * @Description: 发表周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertWeekJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("state", "1");
		jobDiaryDao.insertWeekJobDiary(map);
		List<Map<String, Object>> beans = new ArrayList<>();
		String[] str = map.get("userId").toString().split(",");
		if(str.length > 0){
			Map<String, Object> item;
			for(String receivedId : str){
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("diaryWeekId", map.get("id"));
				item.put("state", "1");
				if(!ToolUtil.isBlank(receivedId)){
					item.put("receivedId", receivedId);
				}
				beans.add(item);
			}
			jobDiaryDao.insertWeekJobDiaryReceived(beans);
		}
	}
	
	/**
	 * 
	     * @Title: selectMysendWeekDetails
	     * @Description: 阅读我发出的周报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectMysendWeekDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = jobDiaryDao.selectMysendWeekDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: queryWeekJobDiaryDetails
	     * @Description: 阅读我收到的周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void queryWeekJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> state = jobDiaryDao.queryWeekJobDiaryState(map);
		if("1".equals(state.get("state").toString())){
			map.put("readTime", DateUtil.getTimeAndToString());
			jobDiaryDao.editWeekJobDiaryState(map);
		}
		Map<String, Object>	bean = jobDiaryDao.queryWeekJobDiaryDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: insertMonthJobDiary
	     * @Description: 发表月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertMonthJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("state", "1");
		jobDiaryDao.insertMonthJobDiary(map);
		List<Map<String, Object>> beans = new ArrayList<>();
		String[] str = map.get("userId").toString().split(",");
		if(str.length > 0){
			Map<String, Object> item;
			for(String receivedId : str){
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("diaryMonthId", map.get("id"));
				item.put("state", "1");
				if(!ToolUtil.isBlank(receivedId)){
					item.put("receivedId", receivedId);
				}
				beans.add(item);
			}
			jobDiaryDao.insertMonthJobDiaryReceived(beans);
		}
	}
	
	/**
	 * 
	     * @Title: selectMysendMonthDetails
	     * @Description: 阅读我发出的月报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectMysendMonthDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = jobDiaryDao.selectMysendMonthDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: queryMonthJobDiaryDetails
	     * @Description: 阅读我收到的月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void queryMonthJobDiaryDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> state = jobDiaryDao.queryMonthJobDiaryState(map);
		if("1".equals(state.get("state").toString())){
			map.put("readTime", DateUtil.getTimeAndToString());
			jobDiaryDao.editMonthJobDiaryState(map);
		}
		Map<String, Object>	bean = jobDiaryDao.queryMonthJobDiaryDetails(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editJobDiaryDayMysend
	     * @Description: 删除我发出的日志
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editJobDiaryDayMysend(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> createtime = jobDiaryDao.queryJobDiaryType(map);
		if("1".equals(createtime.get("diaryType").toString())){
			jobDiaryDao.editJobDiaryDayMysendDelete(map);  //修改日报表状态变删除
		}else if("2".equals(createtime.get("diaryType").toString())){
			jobDiaryDao.editJobDiaryWeekMysendDelete(map);  //修改周报表状态变删除
		}else if("3".equals(createtime.get("diaryType").toString())){
			jobDiaryDao.editJobDiaryMonthMysendDelete(map);  //修改月报表状态变删除
		}
	}

	/**
	 * 
	     * @Title: queryJobDiaryDayMysendToEdit
	     * @Description: 回显我撤回的日报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = jobDiaryDao.queryJobDiaryDayMysendToEdit(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
        bean.put("userInfo", jobDiaryDao.queryJobDiaryDayReceivedUserInfoById(map));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editDayJobDiary
	     * @Description: 提交撤回的日报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("state", "1");
		jobDiaryDao.editDayJobDiary(map);
		jobDiaryDao.deleteJobDiaryReceived(map);
		List<Map<String,Object>> beans = new ArrayList<>();
		String[] userId = map.get("userId").toString().split(",");//把字符串以","分截成字符数组
		if(userId.length > 0){  //如果数组长度大于0
			Map<String, Object> item;
			for(String str : userId){  //遍历数组
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("diaryDayId", map.get("id"));
				if(!ToolUtil.isBlank(str)){
					item.put("receivedId", str);
				}
				item.put("state", "1");
				beans.add(item); //把一个个item对象放入集合beans
			}
		}
		jobDiaryDao.insertDayJobDiaryReceived(beans);  //在数据库中插入集合beans
	}

	/**
	 * 
	     * @Title: queryWeekJobDiaryDayMysendToEdit
	     * @Description: 回显我撤回的周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryWeekJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = jobDiaryDao.queryWeekJobDiaryDayMysendToEdit(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
        bean.put("userInfo", jobDiaryDao.queryWeekJobDiaryDayReceivedUserInfoById(map));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editWeekDayJobDiary
	     * @Description: 提交撤回的周报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editWeekDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("state", "1");
		jobDiaryDao.editWeekDayJobDiary(map);
		jobDiaryDao.deleteWeekJobDiaryReceived(map);
		List<Map<String,Object>> beans = new ArrayList<>();
		String[] userId = map.get("userId").toString().split(",");//把字符串以","分截成字符数组
		if(userId.length > 0){  //如果数组长度大于0
			Map<String, Object> item;
			for(String str : userId){  //遍历数组
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("diaryWeekId", map.get("id"));
				if(!ToolUtil.isBlank(str)){
					item.put("receivedId", str);
				}
				item.put("state", "1");
				beans.add(item); //把一个个item对象放入集合beans
			}
		}
		jobDiaryDao.insertWeekJobDiaryReceived(beans);  //在数据库中插入集合beans
	}

	/**
	 * 
	     * @Title: queryMonthJobDiaryDayMysendToEdit
	     * @Description: 回显我撤回的月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMonthJobDiaryDayMysendToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = jobDiaryDao.queryMonthJobDiaryDayMysendToEdit(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
        bean.put("userInfo", jobDiaryDao.queryMonthJobDiaryDayReceivedUserInfoById(map));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editMonthDayJobDiary
	     * @Description: 提交撤回的月报
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMonthDayJobDiary(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("state", "1");
		jobDiaryDao.editMonthDayJobDiary(map);
		jobDiaryDao.deleteMonthJobDiaryReceived(map);
		List<Map<String,Object>> beans = new ArrayList<>();
		String[] userId = map.get("userId").toString().split(",");//把字符串以","分截成字符数组
		if(userId.length > 0){  //如果数组长度大于0
			Map<String, Object> item;
			for(String str : userId){  //遍历数组
				item = new HashMap<>();
				item.put("id", ToolUtil.getSurFaceId());
				item.put("diaryMonthId", map.get("id"));
				if(!ToolUtil.isBlank(str)){
					item.put("receivedId", str);
				}
				item.put("state", "1");
				beans.add(item); //把一个个item对象放入集合beans
			}
		}
		jobDiaryDao.insertMonthJobDiaryReceived(beans);  //在数据库中插入集合beans
	}

	/**
	 * 
	     * @Title: queryJobDiaryDayNumber
	     * @Description: 查询日志类型各个类型的条数
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryJobDiaryDayNumber(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = jobDiaryDao.queryJobDiaryDayNumber(map);
		outputObject.setBeans(beans);
		outputObject.settotal(1);
	}
	
	/**
     * 
         * @Title: queryJobDiaryListToTimeTree
         * @Description: 获取日志列表展示位时间树
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryJobDiaryListToTimeTree(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("receivedId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = jobDiaryDao.queryJobDiaryListToTimeTree(map);
        List<Map<String, Object>> rows = null;//当天的日志列表
        for(Map<String, Object> bean : beans){
            bean.put("receivedId", user.get("id"));
            bean.put("createName", map.get("createName"));
            rows = jobDiaryDao.queryJobDiaryDayChildListToTimeTree(bean);
            bean.put("dayChild", rows);
        }
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
    }
    
    /**
     * 
         * @Title: editReceivedJobDiaryToAlreadyRead
         * @Description: 我收到的日志全部设置为已读
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editReceivedJobDiaryToAlreadyRead(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("readTime", DateUtil.getTimeAndToString());
        map.put("userId", inputObject.getLogParams().get("id"));
        jobDiaryDao.editReceivedJobDiaryToAlreadyRead(map);
        jobDiaryDao.editReceivedWeekJobDiaryToAlreadyRead(map);
        jobDiaryDao.editReceivedMonthJobDiaryToAlreadyRead(map);
    }
	
}
