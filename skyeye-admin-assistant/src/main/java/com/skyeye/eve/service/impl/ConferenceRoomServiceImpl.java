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
import com.skyeye.eve.dao.ConferenceRoomDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.ConferenceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ConferenceRoomServiceImpl
 * @Description: 会议室管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:08
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class ConferenceRoomServiceImpl implements ConferenceRoomService {

	@Autowired
	private ConferenceRoomDao conferenceRoomDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllConferenceRoomMation
	     * @Description: 遍历所有的会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = conferenceRoomDao.selectAllConferenceRoomMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertConferenceRoomMation
	     * @Description: 新增会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertConferenceRoomMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		String str = ToolUtil.getUniqueKey();
        map.put("roomNum", "HYS" + str);
		map.put("state", "1");
		Map<String, Object> m = conferenceRoomDao.queryUserCompanyById(user);
        map.put("companyId", m.get("companyId"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("createId", user.get("id"));
		conferenceRoomDao.insertConferenceRoomMation(map);
	}

	/**
	 * 
	     * @Title: deleteConferenceRoomById
	     * @Description: 删除会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteConferenceRoomById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		conferenceRoomDao.deleteConferenceRoomById(map);
	}

	/**
	 * 
	     * @Title: updateConferenceRoomNormalById
	     * @Description: 会议室恢复正常
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateConferenceRoomNormalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = conferenceRoomDao.queryConferenceRoomState(map);
		if("2".equals(m.get("state").toString()) || "3".equals(m.get("state").toString())){//维修或者报废可以恢复正常
			conferenceRoomDao.updateConferenceRoomNormalById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateConferenceRoomRepairById
	     * @Description: 会议室维修
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateConferenceRoomRepairById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = conferenceRoomDao.queryConferenceRoomState(map);
		if("1".equals(m.get("state").toString()) || "3".equals(m.get("state").toString())){//正常或者报废可以维修
			conferenceRoomDao.updateConferenceRoomRepairById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateConferenceRoomScrapById
	     * @Description: 会议室报废
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateConferenceRoomScrapById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> m = conferenceRoomDao.queryConferenceRoomState(map);
		if("1".equals(m.get("state").toString()) || "2".equals(m.get("state").toString())){//正常或者维修可以报废
			conferenceRoomDao.updateConferenceRoomScrapById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectConferenceRoomDetailsById
	     * @Description: 会议室详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectConferenceRoomDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = conferenceRoomDao.selectConferenceRoomDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editConferenceRoomMationById
	     * @Description: 查询会议室信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = conferenceRoomDao.queryConferenceRoomMationById(map);
		// 查询附件
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 查询管理人
		bean.put("roomAdmin", sysEveUserStaffDao.queryUserNameList(bean.get("roomAdmin").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editConferenceRoomMationById
	     * @Description: 编辑会议室
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editConferenceRoomMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		conferenceRoomDao.editConferenceRoomMationById(map);
	}
	
	/**
	 * 
	     * @Title: selectConferenceRoomListToChoose
	     * @Description: 获取会议室列表用于预定选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectConferenceRoomListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = conferenceRoomDao.selectConferenceRoomListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
}
