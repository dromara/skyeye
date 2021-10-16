/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.SealApplyRevertDao;
import com.skyeye.eve.dao.SealDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealRevertNodeListener
 * @Description: 印章归还监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 18:58
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SealRevertNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 印章归还关联的工作流的key
	 */
	private static final String ACTIVITI_SEAL_REVERT_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_SEAL_REVERT_PAGE.getKey();
	
	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		SealDao sealDao = SpringUtils.getBean(SealDao.class);
		SealApplyRevertDao sealApplyRevertDao = SpringUtils.getBean(SealApplyRevertDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取印章归还主表信息
		Map<String, Object> map = sealApplyRevertDao.querySealRevertByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取印章数据");
		}
		String revertId = map.get("id").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			List<Map<String, Object>> goods = sealApplyRevertDao.querySealMationListByRevertId(revertId);
			for(Map<String, Object> good: goods){
				sealDao.editSealBorrowIdById(good.get("sealId").toString(), StringUtils.EMPTY);
			}
			// 修改印章归还单状态
			sealApplyRevertDao.editSealRevertById(revertId, 2);
			// 印章对应的归还状态  2.归还成功
			sealApplyRevertDao.updateSealRevertGoodsStateByRevertId(revertId, 2);
		}else{
			// 修改印章归还单状态
			sealApplyRevertDao.editSealRevertById(revertId, 3);
			// 修改印章归还状态，3.归还失败
			sealApplyRevertDao.updateSealRevertGoodsStateByRevertId(revertId, 3);
		}

		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_SEAL_REVERT_PAGE_KEY).editApplyMationInActiviti(revertId);
	}

}
