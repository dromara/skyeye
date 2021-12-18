/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SealApplyBorrowDao;
import com.skyeye.eve.dao.SealDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealBorrowNodeListener
 * @Description: 印章借用监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 16:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SealBorrowNodeListener implements JavaDelegate {
	
	// 值为pass，则通过，为nopass，则不通过
	private Expression state;

	/**
	 * 印章借用关联的工作流的key
	 */
	private static final String ACTIVITI_SEAL_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_SEAL_USE_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		SealDao sealDao = SpringUtils.getBean(SealDao.class);
		SealApplyBorrowDao sealApplyBorrowDao = SpringUtils.getBean(SealApplyBorrowDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取印章使用主表信息
		Map<String, Object> map = sealApplyBorrowDao.querySealBorrowByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取印章数据");
		}
		String borrowId = map.get("id").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			// 获取借用单借用的印章列表
			List<Map<String, Object>> beans = sealApplyBorrowDao.queryBorrowSealById(borrowId);
			for(Map<String, Object> bean: beans){
				String id = bean.get("id").toString();
				String sealId = bean.get("sealManageId").toString();
				Map<String, Object> sealMation = sealDao.querySealMationById(sealId);
				// 当前印章的借用人
				String sealBorrowId = sealMation.get("borrowId").toString();
				// 印章对应的借用状态    1.等待发放  2.库存不足  3.库存充足
				int applyState;
				if(ToolUtil.isBlank(sealBorrowId)){
					// 当前库存充足
					applyState = 3;
					// 给印章表中对应印章填上借用人
					sealDao.editSealBorrowIdById(sealId, map.get("createId").toString());
				}else{
					// 当前库存不足
					applyState = 2;
				}
				// 修改印章借用状态
				sealApplyBorrowDao.editSealBorrowGoodsById(id, applyState);
			}
			// 修改印章借用单状态
			sealApplyBorrowDao.editSealBorrowById(borrowId, 2);
		}else{
			// 修改印章借用单状态
			sealApplyBorrowDao.editSealBorrowById(borrowId, 3);
			sealApplyBorrowDao.editSealBorrowGoodsByBorrowId(borrowId, 4);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_SEAL_USE_PAGE_KEY).editApplyMationInActiviti(borrowId);
	}

}
