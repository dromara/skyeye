/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysNoticeDao;
import com.skyeye.eve.service.SysNoticeService;
import com.skyeye.quartz.config.QuartzService;
import com.skyeye.service.JobMateMationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysNoticeServiceImpl
 * @Description: 公告模块服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 21:36
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysNoticeServiceImpl implements SysNoticeService {

	@Autowired
	private SysNoticeDao sysNoticeDao;
	
	@Autowired
	private QuartzService quartzService;

	@Autowired
	private JobMateMationService jobMateMationService;

	/**
	 * 
	     * @Title: querySysNoticeList
	     * @Description: 查出所有公告列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysNoticeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysNoticeDao.querySysNoticeList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertSysNoticeMation
	     * @Description: 新增公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysNoticeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeMationByName(map);
		// 该公告名称已经存在
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该公告名称已存在，请更换");
			return;
		}
		String id = ToolUtil.getSurFaceId();
		// 1.判断接收人类型是全体人员还是指定人员   如果是指定人员，判断人员是否为空并且做人员集合操作
		if("2".equals(map.get("sendType").toString())){
			// 接收人为指定人员
			if(!ToolUtil.isBlank(map.get("userInfo").toString())){
				setNotice2PointUser(id, map.get("userInfo").toString());
			}else{
				outputObject.setreturnMessage("请选择公告接收人！");
				return;
			}
		}else if("1".equals(map.get("sendType").toString())){
			// 接收人为所有人
			setNotice2AllUser(id);
		}
		String timeSend = map.get("timeSend").toString();
		// 2.判断发送类型是定时发送还是手动发送   如果是定时发送，做定时任务处理
		if("2".equals(timeSend)){
			if(DateUtil.compare(map.get("delayedTime").toString(), DateUtil.getTimeAndToString())){	//定时通知时间早于当前时间
				outputObject.setreturnMessage("公告定时发送的时间不能早于当前时间，请重新设定发送时间！");
				return;
			}else{
				// 定时任务，要求定时通知时间晚于当前时间
				quartzService.startUpTaskQuartz(id, map.get("title").toString(), map.get("delayedTime").toString(), user.get("id").toString(),
						QuartzConstants.QuartzMateMationJobType.QUARTZ_NOTICE_GROUP_STR.getTaskType());
			}
		}else if("1".equals(timeSend)){
			// 发送类型为手动发送
			map.put("delayedTime", StringUtils.EMPTY);
		}
		Map<String, Object> itemCount = sysNoticeDao.querySysNoticeBySimpleLevel(map);
		int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
		map.put("orderBy", thisOrderBy);
		map.put("id", id);
		map.put("state", "1");//默认新建
		map.put("createId", user.get("id"));
		map.put("createName", user.get("userName"));
		map.put("createTime", DateUtil.getTimeAndToString());
		sysNoticeDao.insertSysNoticeMation(map);
	}

	/**
	 * 设置指定人员为公告接收人
	 *
	 * @param id 公告id
	 * @param userInfo 接收人信息，json串
	 * @throws Exception
	 */
	private void setNotice2PointUser(String id, String userInfo) throws Exception {
		List<Map<String, Object>> userInfoList = JSONUtil.toList(userInfo, null);
		List<Map<String,Object>> beans = new ArrayList<>();
		for(int i = 0; i < userInfoList.size(); i++){
			Map<String, Object> j = userInfoList.get(i);
			Map<String, Object> item = new HashMap<>();
			item.put("id", ToolUtil.getSurFaceId());
			item.put("noticeId", id);
			item.put("userId", j.get("id"));
			item.put("userName", j.get("name"));
			item.put("userEmail", j.get("email"));
			beans.add(item);
		}
		if(!beans.isEmpty())
			sysNoticeDao.insertSysNoticeUser(beans);
	}

	/**
	 * 设置所有人为该公告的接收人
	 *
	 * @param id 公告id
	 * @throws Exception
	 */
	private void setNotice2AllUser(String id) throws Exception {
		List<Map<String, Object>> userInfoList = sysNoticeDao.queryAllUserList();
		List<Map<String,Object>> beans = new ArrayList<>();
		for(Map<String, Object> it : userInfoList){
			Map<String, Object> item = new HashMap<>();
			item.put("id", ToolUtil.getSurFaceId());
			item.put("noticeId", id);
			item.put("userId", it.get("userId"));
			item.put("userName", it.get("userName"));
			item.put("userEmail", it.get("email"));
			beans.add(item);
		}
		if(!beans.isEmpty())
			sysNoticeDao.insertSysNoticeUser(beans);
	}

	/**
	 * 
	     * @Title: deleteSysNoticeById
	     * @Description: 删除公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以删除
			sysNoticeDao.deleteSysNoticeById(map);
			// 删除定时任务
			quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.QUARTZ_NOTICE_GROUP_STR.getTaskType());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateUpSysNoticeById
	     * @Description: 手动上线公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeMationById(map);	//查询state、timeSend、whetherEmail、title的值
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以上线
			if("2".equals(bean.get("timeSend").toString())){
				// 如果设置了定时上线任务则需要删除定时任务
				quartzService.stopAndDeleteTaskQuartz(map.get("id").toString(), QuartzConstants.QuartzMateMationJobType.QUARTZ_NOTICE_GROUP_STR.getTaskType());
				map.put("timeSend", "3");
			}
			map.put("realLinesType", "1");
			map.put("realLinesTime", DateUtil.getTimeAndToString());
			if("2".equals(bean.get("whetherEmail").toString())){	//如果设置邮件发送
				List<Map<String,Object>> userEmail = sysNoticeDao.selectAllSendUser(map);
				// 启动mq消息任务
				Map<String, Object> notice = new HashMap<>();
				notice.put("title", "公告提醒");
				notice.put("content", "内部公告 -【" + bean.get("title").toString() + "】");
				notice.put("email", userEmail);
				notice.put("type", MqConstants.JobMateMationJobType.NOTICE_SEND.getJobType());//消息队列任务类型
				jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(notice), inputObject.getLogParams().get("id").toString());
			}
			sysNoticeDao.updateUpSysNoticeById(map);	//上线公告
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownSysNoticeById
	     * @Description: 下线公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			sysNoticeDao.updateDownSysNoticeById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectSysNoticeById
	     * @Description: 通过id查找对应的公告信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = sysNoticeDao.selectSysNoticeById(map);
		List<Map<String, Object>> bs = sysNoticeDao.queryReceivedSysNoticeUserInfoById(map);
		bean.put("userInfo", bs);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysNoticeMationById
	     * @Description: 编辑公告信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以编辑
			Map<String, Object> b = sysNoticeDao.querySysNoticeMationByNameAndId(map);
			if(b != null && !b.isEmpty()){
				outputObject.setreturnMessage("该公告名称已存在，请更换");
			}else{
				// 1.判断接收人类型是全体人员还是指定人员   如果是指定人员，判断人员是否为空并且做人员集合操作
				if("2".equals(map.get("sendType").toString())){
					//接收人为指定人员
					if(!ToolUtil.isBlank(map.get("userInfo").toString())){
						// 删除这条公告原来绑定的用户
						sysNoticeDao.deleteSysNoticeUserById(map);
						setNotice2PointUser(map.get("id").toString(), map.get("userInfo").toString());
					}else{
						outputObject.setreturnMessage("请选择公告接收人！");
						return;
					}
				}else if("1".equals(map.get("sendType").toString())){
					// 接收人为所有人
					// 删除这条公告原来绑定的用户
					sysNoticeDao.deleteSysNoticeUserById(map);
					setNotice2AllUser(map.get("id").toString());
				}
				// 2.判断发送类型是定时发送还是手动发送   如果是定时发送，做定时任务处理
				if("2".equals(map.get("timeSend").toString())){
					if(DateUtil.compare(map.get("delayedTime").toString(), DateUtil.getTimeAndToString())){	//定时通知时间早于当前时间
						outputObject.setreturnMessage("公告定时发送的时间不能早于当前时间，请重新设定发送时间！");
						return;
					}
					// 定时任务，需要定时通知时间晚于当前时间
					quartzService.startUpTaskQuartz(map.get("id").toString(), map.get("title").toString(), map.get("delayedTime").toString(), inputObject.getLogParams().get("id").toString(),
							QuartzConstants.QuartzMateMationJobType.QUARTZ_NOTICE_GROUP_STR.getTaskType());
				}else if("1".equals(map.get("timeSend").toString())){	//发送类型为手动发送
					map.put("delayedTime", StringUtils.EMPTY);
				}
				sysNoticeDao.editSysNoticeMationById(map);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editSysNoticeMationOrderNumUpById
	     * @Description: 公告上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysNoticeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前公告已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysNoticeDao.editSysNoticeMationOrderNumUpById(map);
			sysNoticeDao.editSysNoticeMationOrderNumUpById(bean);
		}
	}
	
	/**
	 * 
	     * @Title: editSysNoticeMationOrderNumDownById
	     * @Description: 公告下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysNoticeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前公告已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysNoticeDao.editSysNoticeMationOrderNumUpById(map);
			sysNoticeDao.editSysNoticeMationOrderNumUpById(bean);
		}
	}

	/**
	 * 
	     * @Title: editSysNoticeTimeUpById
	     * @Description: 定时上线时间
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysNoticeTimeUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeDao.querySysNoticeStateAndTimeSend(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){
			// 新建或者下线可以编辑定时上线
			if("1".equals(bean.get("timeSend").toString()) || "3".equals(bean.get("timeSend").toString()) || "4".equals(bean.get("timeSend").toString())){
				// 1不设置 3已失效 4已执行
				if(DateUtil.compare(map.get("delayedTime").toString(), DateUtil.getTimeAndToString())){
					// 定时通知时间早于当前时间
					outputObject.setreturnMessage("公告定时发送的时间不能早于当前时间，请重新设定发送时间！");
					return;
				}
				sysNoticeDao.editSysNoticeTimeUpMation(map);
				// 定时任务
				quartzService.startUpTaskQuartz(map.get("id").toString(), map.get("title").toString(), map.get("delayedTime").toString(), inputObject.getLogParams().get("id").toString(),
						QuartzConstants.QuartzMateMationJobType.QUARTZ_NOTICE_GROUP_STR.getTaskType());
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: querySysNoticeDetailsById
	     * @Description: 公告详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysNoticeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = sysNoticeDao.querySysNoticeDetailsById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: queryUserReceivedSysNotice
	     * @Description: 用户收到的公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryUserReceivedSysNotice(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysNoticeDao.queryUserReceivedSysNotice(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: queryReceivedSysNoticeDetailsById
	     * @Description: 用户收到的公告详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReceivedSysNoticeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Map<String, Object>	bean = sysNoticeDao.queryReceivedSysNoticeDetailsById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
}
