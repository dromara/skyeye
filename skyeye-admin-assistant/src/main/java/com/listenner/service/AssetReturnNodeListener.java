/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetApplyReturnDao;
import com.skyeye.eve.dao.AssetDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 *
 * @ClassName: AssetReturnNodeListener
 * @Description: 资产归还监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:34
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetReturnNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 资产归还关联的工作流的key
	 */
	private static final String ACTIVITI_ASSET_RETURN_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSET_RETURN_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		AssetDao assetDao = SpringUtils.getBean(AssetDao.class);
		AssetApplyReturnDao assetApplyReturnDao = SpringUtils.getBean(AssetApplyReturnDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取资产归还主表信息
		Map<String, Object> map = assetApplyReturnDao.queryAssetReturnByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取资产数据");
		}
		String returnId = map.get("id").toString();
		// 服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			// 修改资产归还状态
			map.put("applyState", "2");//审批通过
			assetApplyReturnDao.editAssetReturnGoodsById(map);
			// 修改资产归还单状态
			map.put("state", 2);//审核通过
			assetApplyReturnDao.editAssetReturnById(map);
			// 获取资产归还单的创建人，放入集合中
			map.put("employeeId", map.get("createId").toString());
			// 修改资产表，将领用人置为空
			assetDao.editAssetManagementMation(map);
		}else{
			//修改资产归还状态
			map.put("applyState", "3");//审批不通过
			assetApplyReturnDao.editAssetReturnGoodsById(map);
			//修改资产归还单状态
			map.put("state", 3);//审核不通过
			assetApplyReturnDao.editAssetReturnById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_ASSET_RETURN_PAGE_KEY).editApplyMationInActiviti(returnId);
	}

}
