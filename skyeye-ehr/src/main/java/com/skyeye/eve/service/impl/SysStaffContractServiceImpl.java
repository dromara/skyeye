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
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysStaffContractDao;
import com.skyeye.eve.service.SysStaffContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffContractServiceImpl
 * @Description: 员工合同管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffContractServiceImpl implements SysStaffContractService{
	
	@Autowired
	private SysStaffContractDao sysStaffContractDao;
	
	@Autowired
	private SysEnclosureDao sysEnclosureDao;
	
	public static enum state{
        START_SIGNED(1, "待签约"),
        START_EXECUTION(2, "执行中"),
        START_OVERDUE(3, "过期");
        private int state;
        private String name;
        state(int state, String name){
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
	 * Title: queryAllSysStaffContractList
	 * Description: 查询所有合同列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#queryAllSysStaffContractList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryAllSysStaffContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffContractDao.queryAllSysStaffContractList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: insertSysStaffContractMation
	 * Description: 员工合同信息录入
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#insertSysStaffContractMation(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysStaffContractMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createTime", DateUtil.getTimeAndToString());
		sysStaffContractDao.insertSysStaffContractMation(map);
	}

	/**
	 * 
	 * Title: querySysStaffContractMationToEdit
	 * Description: 编辑员工合同信息时回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#querySysStaffContractMationToEdit(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void querySysStaffContractMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffContractDao.querySysStaffContractMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			// 附件
			if(certificate.containsKey("enclosure") && !ToolUtil.isBlank(certificate.get("enclosure").toString())){
				List<Map<String,Object>> beans = sysEnclosureDao.queryEnclosureInfo(certificate.get("enclosure").toString());
				certificate.put("enclosureInfo", beans);
			}
			outputObject.setBean(certificate);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该合同信息不存在.");
		}
	}

	/**
	 * 
	 * Title: editSysStaffContractMationById
	 * Description: 编辑员工合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#editSysStaffContractMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysStaffContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffContractDao.querySysStaffContractMationToEdit(id);
		if(certificate != null && !certificate.isEmpty()){
			int nowState = Integer.parseInt(certificate.get("contractState").toString());
			if(nowState == state.START_SIGNED.getState()){
				// 待签约状态的可以编辑
				sysStaffContractDao.editSysStaffContractMationById(map);
			}else{
				outputObject.setreturnMessage("该合同信息已签约，无法编辑.");
			}
		}else{
			outputObject.setreturnMessage("该合同信息不存在.");
		}
	}

	/**
	 * 
	 * Title: deleteSysStaffContractMationById
	 * Description: 删除员工合同信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#deleteSysStaffContractMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysStaffContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffContractDao.querySysStaffContractMationToEdit(id);
		int nowState = Integer.parseInt(certificate.get("contractState").toString());
		if(nowState == state.START_SIGNED.getState()){
			// 待签约状态的可以删除
			sysStaffContractDao.deleteSysStaffContractMationById(id);
		}else{
			outputObject.setreturnMessage("该合同信息已签约，无法删除.");
		}
	}

	/**
	 * 
	 * Title: queryPointStaffSysStaffContractList
	 * Description: 查询指定员工的合同列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#queryPointStaffSysStaffContractList(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryPointStaffSysStaffContractList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffContractDao.queryPointStaffSysStaffContractList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: signSysStaffContractById
	 * Description: 员工合同签约
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.eve.service.SysStaffContractService#signSysStaffContractById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	@Transactional(value="transactionManager")
	public void signSysStaffContractById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String id = map.get("id").toString();
		Map<String, Object> certificate = sysStaffContractDao.querySysStaffContractMationToEdit(id);
		int nowState = Integer.parseInt(certificate.get("contractState").toString());
		if(nowState == state.START_SIGNED.getState()){
			// 待签约状态的可以签约
			sysStaffContractDao.editSysStaffContractStateById(id, state.START_EXECUTION.getState());
		}else{
			outputObject.setreturnMessage("该合同信息已签约，无法删除.");
		}
	}
	
}
