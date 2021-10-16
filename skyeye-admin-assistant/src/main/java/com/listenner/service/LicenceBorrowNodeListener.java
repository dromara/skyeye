/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.LicenceApplyBorrowDao;
import com.skyeye.eve.dao.LicenceDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: LicenceBorrowNodeListener
 * @Description: 证照借用监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/26 22:21
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class LicenceBorrowNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 证照借用关联的工作流的key
	 */
	private static final String ACTIVITI_LICENCE_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_LICENCE_USE_PAGE.getKey();

	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		LicenceDao licenceDao = SpringUtils.getBean(LicenceDao.class);
		LicenceApplyBorrowDao licenceApplyBorrowDao = SpringUtils.getBean(LicenceApplyBorrowDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		Map<String, Object> map = new HashMap<>();
		map.put("processInstanceId", processInstanceId);
		// 获取证照使用主表信息
		map = licenceApplyBorrowDao.queryLicenceBorrowByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取证照数据");
		}
		String id = map.get("id").toString();
		String createId = map.get("createId").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equalsIgnoreCase(value1)){//通过
			// 获取借用单关联的证照列表
			List<Map<String, Object>> beans = licenceApplyBorrowDao.queryLicenceBorrowGoodsByUseId(id);
			for(Map<String, Object> bean : beans){
				String childId = bean.get("id").toString();
				String licenceId = bean.get("licenceId").toString();
				// 获取证照信息
				Map<String, Object> licence = licenceDao.queryLicenceMationById(licenceId);
				String borrowId = licence.get("borrowId").toString();
				// 证照对应的借用状态    1.等待发放  2.库存不足  3.库存充足
				if(ToolUtil.isBlank(borrowId)){
					// 当前库存充足
					licenceApplyBorrowDao.editLicenceBorrowGoodsById(childId, 3);
					// 给证照表中对应证照填上借用人
					licenceDao.editLicenceBorrowIdById(licenceId, createId);
				}else{
					// 当前库存不足
					licenceApplyBorrowDao.editLicenceBorrowGoodsById(childId, 2);
				}
			}
			// 修改证照借用单状态
			map.put("state", 2);
			licenceApplyBorrowDao.editLicenceBorrowById(map);
		}else{
			// 修改证照借用单状态
			map.put("state", 3);
			licenceApplyBorrowDao.editLicenceBorrowById(map);
			// 修改证照借用单关联的证照状态
			licenceApplyBorrowDao.editLicenceBorrowGoodsByUseId(id, 4);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_LICENCE_USE_PAGE_KEY).editApplyMationInActiviti(id);
	}

}
