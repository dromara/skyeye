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
import com.skyeye.eve.dao.LicenceDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.LicenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @ClassName: LicenceServiceImpl
 * @Description: 证照管理、证照归还服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:09
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class LicenceServiceImpl implements LicenceService {

	@Autowired
	private LicenceDao licenceDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: selectAllLicenceMation
	     * @Description: 遍历所有的证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectAllLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = licenceDao.selectAllLicenceMation(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: insertLicenceMation
	     * @Description: 新增证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Set<Entry<String, Object>> entrys = map.entrySet();
		for(Map.Entry<String, Object> entry : entrys){
			if(ToolUtil.isBlank(entry.getValue().toString())){
				map.put(entry.getKey(), null);
			}
		}
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("createId", inputObject.getLogParams().get("id"));
		Map<String, Object> cmap = new HashMap<>();
        cmap.put("id", inputObject.getLogParams().get("id"));
        Map<String, Object> b = sysEveUserStaffDao.queryCompanyIdByUserId(cmap);//获取当前登录人所属公司
        map.put("companyId", b.get("companyId"));
		licenceDao.insertLicenceMation(map);
	}

	/**
	 * 
	     * @Title: deleteLicenceById
	     * @Description: 删除证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteLicenceById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		licenceDao.deleteLicenceById(map);
	}

	/**
	 * 
	     * @Title: editLicenceMationById
	     * @Description: 查询证照信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> bean = licenceDao.queryLicenceMationById(id);
		// 查询附件
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 查询证照管理人
		bean.put("licenceAdmin", sysEveUserStaffDao.queryUserNameList(bean.get("licenceAdmin").toString()));
		// 查询证照借用人
		bean.put("borrowId", sysEveUserStaffDao.queryUserNameList(bean.get("borrowId").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editLicenceMationById
	     * @Description: 编辑证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Set<Entry<String, Object>> entrys = map.entrySet();
		for(Map.Entry<String, Object> entry : entrys){
			if(ToolUtil.isBlank(entry.getValue().toString())){
				map.put(entry.getKey(), null);
			}
		}
		licenceDao.editLicenceMationById(map);
	}
	
	/**
	 * 
	     * @Title: selectLicenceDetailsById
	     * @Description: 证照详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectLicenceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = licenceDao.selectLicenceDetailsById(map);
		if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		}
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: selectLicenceListToChoose
	     * @Description: 获取证照列表用于借用选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectLicenceListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = licenceDao.selectLicenceListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: selectRevertListToChoose
	     * @Description: 获取证照列表用于归还选择
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
		List<Map<String, Object>> beans = licenceDao.selectRevertListToChoose(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
}
