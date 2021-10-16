/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetArticlesApplyPurchaseDao;
import com.skyeye.eve.dao.AssetArticlesDao;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetsArticlesPurchaseNodeListener
 * @Description: 用品采购监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:34
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetsArticlesPurchaseNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 用品采购关联的工作流的key
	 */
	private static final String ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSETARTICLES_PURCHASE_PAGE.getKey();
	
	/**
	 * 
	 * @param execution
	 * @throws Exception
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		AssetArticlesDao assetArticlesDao = SpringUtils.getBean(AssetArticlesDao.class);
		AssetArticlesApplyPurchaseDao assetArticlesApplyPurchaseDao = SpringUtils.getBean(AssetArticlesApplyPurchaseDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取用品采购主表信息
		Map<String, Object> map = assetArticlesApplyPurchaseDao.queryAssetsArticlesPurchaseByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取用品数据");
		}
		String purchaseId = map.get("id").toString();
		//服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			//获取采购单采购的用品列表
			List<Map<String, Object>> beans = assetArticlesApplyPurchaseDao.queryAssetsArticlesPurchaseGoodsById(map);
			for(Map<String, Object> bean : beans){
				//当前用品剩余的数量
				int residualNum = Integer.parseInt(bean.get("residualNum").toString());
				//用户申请采购的数量
				int applyPurchaseNum = Integer.parseInt(bean.get("applyPurchaseNum").toString());
				//重置库存剩余数量
				residualNum = residualNum + applyPurchaseNum;
				//修改库存
				bean.put("residualNum", residualNum);
				assetArticlesDao.editAssetsArticlesById(bean);
				//修改用品采购状态
				bean.put("applyState", '2');
				bean.put("actualPurchaseNum", applyPurchaseNum);
				assetArticlesApplyPurchaseDao.editAssetsArticlesPurchaseGoodsById(bean);
			}
			//修改用品采购单状态
			map.put("state", 2);
			assetArticlesApplyPurchaseDao.editAssetsArticlesPurchaseById(map);
		}else{
			//修改用品采购状态，state='3'为审批不通过
			assetArticlesApplyPurchaseDao.editAssetsArticlesPurchaseGoodsByPurchaseId(map);
			//修改用品采购单状态
			map.put("state", 3);
			assetArticlesApplyPurchaseDao.editAssetsArticlesPurchaseById(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_ASSETARTICLES_PURCHASE_PAGE_KEY).editApplyMationInActiviti(purchaseId);
	}

}
