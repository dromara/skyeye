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
import com.skyeye.eve.dao.SealDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.SealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealServiceImpl
 * @Description: 印章管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:02
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class SealServiceImpl implements SealService {

	@Autowired
	private SealDao sealDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllSealMation
	     * @Description: 遍历所有的印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllSealMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealDao.selectAllSealMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertSealMation
	     * @Description: 新增印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSealMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("createId", inputObject.getLogParams().get("id"));
		Map<String, Object> cmap = new HashMap<>();
		cmap.put("id", inputObject.getLogParams().get("id"));//获取当前用户id
		Map<String, Object> b = sysEveUserStaffDao.queryCompanyIdByUserId(cmap);//根据用户id获取当前用户所在的公司
		map.put("companyId", b.get("companyId"));//将用户所在的公司作为印章的所属公司
		sealDao.insertSealMation(map);
	}

	/**
	 * 
	     * @Title: deleteSealById
	     * @Description: 删除印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSealById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sealDao.deleteSealById(map);
	}

	/**
	 * 
	     * @Title: editSealMationById
	     * @Description: 查询印章信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySealMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = sealDao.querySealMationById(id);
		// 查询附件
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 查询印章管理人
		bean.put("sealAdmin", sysEveUserStaffDao.queryUserNameList(bean.get("sealAdmin").toString()));
		// 查询印章租借人
		bean.put("borrowId", sysEveUserStaffDao.queryUserNameList(bean.get("borrowId").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSealMationById
	     * @Description: 编辑印章
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSealMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sealDao.editSealMationById(map);
	}
	
	/**
	 * 
	     * @Title: selectSealDetailsById
	     * @Description: 印章详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectSealDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealDao.selectSealDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: selectSealListToChoose
	     * @Description: 获取印章列表用于借用选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectSealListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = sealDao.selectSealListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: selectRevertListToChoose
	     * @Description: 获取印章列表用于归还选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectRevertListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = sealDao.selectRevertListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
}
