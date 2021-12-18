/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ActUserProcessInstanceIdDao;
import com.skyeye.eve.dao.AssetApplyPurchaseDao;
import com.skyeye.eve.dao.AssetDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetPurchaseNodeListener
 * @Description: 资产采购监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:33
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetPurchaseNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 资产采购关联的工作流的key
	 */
	private static final String ACTIVITI_ASSET_PURCHAES_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSET_PURCHAES_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		AssetDao assetDao = SpringUtils.getBean(AssetDao.class);
		ActUserProcessInstanceIdDao actUserProcessInstanceIdDao = SpringUtils.getBean(ActUserProcessInstanceIdDao.class);
		AssetApplyPurchaseDao assetApplyPurchaseDao = SpringUtils.getBean(AssetApplyPurchaseDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取资产采购主表信息
		Map<String, Object> map = assetApplyPurchaseDao.queryAssetPurchaseByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取资产数据");
		}
		String purchaseId = map.get("id").toString();
		//服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			//获取采购单采购的资产列表
			List<Map<String, Object>> bs = assetApplyPurchaseDao.queryAssetPurchaseGoodsById(map);
			//判断资产表中是否已经存在对应的资产
			for(Map<String, Object> bean : bs){
				Map<String, Object> m = assetDao.queryAnythingFromAsset(bean);
				if(m != null){//资产表中已经存在这个资产
					assetApplyPurchaseDao.editAssetPurchaseGoodsState(bean);//修改资产采购状态，审批不通过
				}
			}
			//修改资产采购状态，审批通过
			map.put("applyState", 2);
			assetApplyPurchaseDao.editAssetPurchaseGoodsById(map);
			//修改资产采购单状态
			map.put("state", 2);
			assetApplyPurchaseDao.editAssetPurchaseById(map);
			List<Map<String, Object>> beans = assetApplyPurchaseDao.queryAssetGoodsListByPurchaseId(map);
			if(!beans.isEmpty()){
				Map<String, Object> m = actUserProcessInstanceIdDao.queryActUserProcessInstanceId(processInstanceId);
				String createTime = DateUtil.getTimeAndToString();
				for (Map<String, Object> bean : beans) {
					bean.put("id", ToolUtil.getSurFaceId());
					bean.put("purchaseTime", createTime);
					bean.put("state", "1");
					bean.put("createId", m.get("createId").toString());
					bean.put("createTime", createTime);
					bean.put("companyId", m.get("companyId").toString());
				}
				assetDao.insertAssetMationList(beans);
			}
		}else{
			//修改资产采购状态
			map.put("applyState", 3);
			assetApplyPurchaseDao.editAssetPurchaseGoodsById(map);
			//修改资产采购单状态
			map.put("state", 3);
			assetApplyPurchaseDao.editAssetPurchaseById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_ASSET_PURCHAES_PAGE_KEY).editApplyMationInActiviti(purchaseId);
	}

}
