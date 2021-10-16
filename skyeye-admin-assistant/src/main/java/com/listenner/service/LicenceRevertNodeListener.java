/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.LicenceApplyRevertDao;
import com.skyeye.eve.dao.LicenceDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: LicenceRevertNodeListener
 * @Description: 证照归还监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 12:20
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class LicenceRevertNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 证照归还关联的工作流的key
	 */
	private static final String ACTIVITI_LICENCE_REVERT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_LICENCE_REVERT_PAGE.getKey();
	
	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		LicenceDao licenceDao = SpringUtils.getBean(LicenceDao.class);
		LicenceApplyRevertDao licenceApplyRevertDao = SpringUtils.getBean(LicenceApplyRevertDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取证照归还主表信息
		Map<String, Object> map = licenceApplyRevertDao.queryLicenceRevertByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取证照数据");
		}
		String id = map.get("id").toString();
		String createId = map.get("createId").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equalsIgnoreCase(value1)){//通过
			// 修改证照归还单状态
			licenceApplyRevertDao.editLicenceRevertStateById(id, 2);
			// 修改归还单关联的证照信息
			setLicenceMation(licenceDao, licenceApplyRevertDao, id, createId);
		}else{
			// 修改证照归还单状态
			licenceApplyRevertDao.editLicenceRevertStateById(id, 3);
			// 修改证照归还状态，3.归还失败
			licenceApplyRevertDao.editLicenceRevertGoodsStateByRevertId(id, 3);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_LICENCE_REVERT_PAGE_KEY).editApplyMationInActiviti(id);
	}

	private void setLicenceMation(LicenceDao licenceDao, LicenceApplyRevertDao licenceApplyRevertDao, String id, String createId) throws Exception {
		List<Map<String, Object>> beans = licenceApplyRevertDao.queryLicenceRevertChildListById(id);
		for(Map<String, Object> bean : beans){
			String childId = bean.get("id").toString();
			String licenceId = bean.get("licenceId").toString();
			// 获取证照信息
			Map<String, Object> licenceMation = licenceDao.queryLicenceMationById(licenceId);
			// 获取当前证照借用人
			String borrowId = licenceMation.get("borrowId").toString();
			if(createId.equals(borrowId)){
				// 如果当前借用人为归还单申请人
				// 证照对应的归还状态  2.归还成功
				licenceApplyRevertDao.editLicenceRevertGoodsStateById(childId, 2);
				// 将证照的借用人置为空
				licenceDao.editLicenceBorrowIdById(licenceId, StringUtils.EMPTY);
			}else{
				licenceApplyRevertDao.editLicenceRevertGoodsStateById(childId, 3);
			}
		}
	}

}
