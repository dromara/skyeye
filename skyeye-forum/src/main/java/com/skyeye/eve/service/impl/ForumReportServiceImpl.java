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
import com.skyeye.eve.dao.ForumReportDao;
import com.skyeye.eve.service.ForumReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ForumReportServiceImpl
 * @Description: 论坛内容举报管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:51
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ForumReportServiceImpl implements ForumReportService {

	@Autowired
	private ForumReportDao forumReportDao;
	
	/**
	 * 
	     * @Title: insertForumReportMation
	     * @Description: 新增举报信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertForumReportMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("reportId", user.get("id"));
		map.put("reportTime", DateUtil.getTimeAndToString());
		map.put("id", ToolUtil.getSurFaceId());
		map.put("examineState", "1");
		forumReportDao.insertForumReportMation(map);
	}
	
	/**
	 * 
	     * @Title: queryReportNoCheckList
	     * @Description: 获取论坛举报未审核列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReportNoCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = forumReportDao.queryReportNoCheckList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: editReportCheckMationById
	     * @Description: 举报信息审核
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editReportCheckMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> beans = forumReportDao.queryForumReportStateById(map);
		if("1".equals(beans.get("examineState").toString())){//未审核的状态可以审核
			Map<String, Object> user = inputObject.getLogParams();
			map.put("examineId", user.get("id"));
			map.put("examineTime", DateUtil.getTimeAndToString());
			int edit = forumReportDao.editReportCheckMationById(map);
            if(edit == 1){
                if(map.get("examineState").toString().equals("2")){//审核通过，通知举报人和发帖人
                    Map<String, Object> m = forumReportDao.queryForumReportMationById(map);
                    Map<String, Object> bean = new HashMap<>();
                    bean.put("id", ToolUtil.getSurFaceId());
                    bean.put("noticeContent", map.get("examineState"));
                    bean.put("noticeTitle", "违规");
                    bean.put("forumId", m.get("forumId"));
                    bean.put("receiveId", m.get("createId"));
                    bean.put("type", 1);
                    bean.put("state", 1);
                    bean.put("createTime", DateUtil.getTimeAndToString());
                    forumReportDao.insertForumNoticeMation(bean);//通知发帖人
                    bean.put("id", ToolUtil.getSurFaceId());
                    bean.put("receiveId", m.get("reportId"));
                    bean.put("noticeTitle", "举报");
                    forumReportDao.insertForumNoticeMation(bean);//通知举报人
                }else if(map.get("examineState").toString().equals("3")){//审核不通过，通知举报人
                    Map<String, Object> m = forumReportDao.queryForumReportMationById(map);
                    Map<String, Object> bean = new HashMap<>();
                    bean.put("id", ToolUtil.getSurFaceId());
                    bean.put("noticeContent", map.get("examineState"));
                    bean.put("noticeTitle", "举报");
                    bean.put("forumId", m.get("forumId"));
                    bean.put("receiveId", m.get("reportId"));
                    bean.put("type", 1);
                    bean.put("state", 1);
                    bean.put("createTime", DateUtil.getTimeAndToString());
                    forumReportDao.insertForumNoticeMation(bean);
                }
            }
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: queryReportCheckedList
	     * @Description: 获取论坛举报已审核列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryReportCheckedList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = forumReportDao.queryReportCheckedList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: queryForumReportMationToDetails
	     * @Description: 举报详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryForumReportMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = forumReportDao.queryForumReportMationToDetails(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
}
