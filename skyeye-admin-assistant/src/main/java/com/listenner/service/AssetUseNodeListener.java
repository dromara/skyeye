/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.AssetApplyUseDao;
import com.skyeye.eve.dao.AssetDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetUseNodeListener
 * @Description: 资产领用监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:35
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetUseNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 资产领用关联的工作流的key
	 */
	private static final String ACTIVITI_ASSET_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSET_USE_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		AssetDao assetDao = SpringUtils.getBean(AssetDao.class);
		AssetApplyUseDao assetApplyUseDao = SpringUtils.getBean(AssetApplyUseDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取资产领用主表信息
		Map<String, Object> map = assetApplyUseDao.queryAssetUseByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取资产数据");
		}
		String useId = map.get("id").toString();
		//服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equalsIgnoreCase(value1)){//通过
			//获取领用单领用的资产列表
			List<Map<String, Object>> beans = assetApplyUseDao.queryAssetUseGoodsById(map);
			for(Map<String, Object> bean : beans){
				//当前资产商品的领用人
				String employeeName = bean.get("employeeName").toString();
				if(ToolUtil.isBlank(employeeName)){
					//获取这份领用单的领用人
					Map<String, Object> m = assetDao.queryEmployeeNameFromAssetUse(bean);
					bean.put("employeeId", m.get("createName").toString());
					//给资产表中对应资产填上领用人
					assetDao.editAssetManagementById(bean);
				}else{
					//当前库存不足
					bean.put("applyState", '2');
					assetApplyUseDao.editAssetUseGoodsState(bean);//修改资产领用状态，审批不通过
				}
			}
			//当库存充足时，修改资产领用状态，审批通过
			map.put("applyState", '3');
			assetApplyUseDao.editAssetUseGoodsById(map);
			//修改资产领用单状态
			map.put("state", 2);
			assetApplyUseDao.editAssetUseById(map);
		}else{
			//修改资产领用状态
			map.put("applyState", 4);
			assetApplyUseDao.editAssetUseGoodsById(map);
			//修改资产领用单状态
			map.put("state", 3);
			assetApplyUseDao.editAssetUseById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_ASSET_USE_PAGE_KEY).editApplyMationInActiviti(useId);
	}

}
