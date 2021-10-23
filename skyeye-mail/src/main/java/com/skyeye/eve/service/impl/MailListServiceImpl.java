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
import com.skyeye.eve.dao.MailListDao;
import com.skyeye.eve.service.MailListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MailListServiceImpl
 * @Description: 通讯录管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MailListServiceImpl implements MailListService {
	
	@Autowired
	private MailListDao mailListDao;
	
	/**
	 * 
	     * @Title: queryMailMationList
	     * @Description: 获取通讯录列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMailMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		//通讯录类型
		String checkMail = map.get("checkMail").toString();
		List<Map<String, Object>> beans = new ArrayList<>();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		if("1".equals(checkMail)){//单位通讯录
			//获取当前登陆人所属公司的通讯录
			beans = mailListDao.queryComMailMationList(map);
		}else if("2".equals(checkMail)){//公共通讯录
			//获取公共通讯录
			beans = mailListDao.queryCommonMailMationList(map);
		}else if("3".equals(checkMail)){//个人通讯录
			//获取当前登陆人的个人通讯录
			beans = mailListDao.queryPersonalMailMationList(map);
		}else{
			outputObject.setreturnMessage("参数错误。");
			return;
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertMailMation
	     * @Description: 新增通讯录(个人或者公共通讯录)
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertMailMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		//通讯录类型
		String category = map.get("category").toString();
		if("1".equals(category)){//个人通讯录
			if(ToolUtil.isBlank(map.get("typeId").toString())){//通讯录所属类别为空
				outputObject.setreturnMessage("请选择类别");
				return;
			}
			// 将他人权限制空
			map.remove("readonly");
		}else if("2".equals(category)){//公共通讯录
			if(ToolUtil.isBlank(map.get("readonly").toString())){//他人只读为空
				outputObject.setreturnMessage("请选择他人只读权限");
				return;
			}
			// 将所属类别制空
			map.remove("typeId");
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		mailListDao.insertMailMation(map);
	}

	/**
	 * 
	     * @Title: deleteMailMationById
	     * @Description: 删除通讯录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteMailMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		mailListDao.deleteMailMationById(map);
	}

	/**
	 * 
	     * @Title: queryMailMationToEditById
	     * @Description: 编辑通讯录进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMailMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = mailListDao.queryMailMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editMailMationById
	     * @Description: 编辑通讯录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMailMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//通讯录类型
		String category = map.get("category").toString();
		if("1".equals(category)){//个人通讯录
			if(ToolUtil.isBlank(map.get("typeId").toString())){//通讯录所属类别为空
				outputObject.setreturnMessage("请选择类别");
				return;
			}
			//将他人权限制空
			map.put("readonly", null);
		}else if("2".equals(category)){//公共通讯录
			if(ToolUtil.isBlank(map.get("readonly").toString())){//他人只读为空
				outputObject.setreturnMessage("请选择他人只读权限");
				return;
			}
			//将所属类别制空
			map.put("typeId", null);
		}
		mailListDao.editMailMationById(map);
	}

	/**
	 * 
	     * @Title: queryMailMationDetailsById
	     * @Description: 个人/公共通讯录详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMailMationDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = mailListDao.queryMailMationDetailsById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: querySysMailMationDetailsById
	     * @Description: 单位通讯录详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysMailMationDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = mailListDao.querySysMailMationDetailsById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
}
