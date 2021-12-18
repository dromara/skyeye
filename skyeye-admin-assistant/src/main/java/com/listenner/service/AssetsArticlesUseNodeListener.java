/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.AssetArticlesApplyUseDao;
import com.skyeye.eve.dao.AssetArticlesDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetsArticlesUseNodeListener
 * @Description: 用品领用监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:35
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AssetsArticlesUseNodeListener implements JavaDelegate {

	/**
	 * 值为pass，则通过，为nopass，则不通过
	 */
	private Expression state;

	/**
	 * 用品领用关联的工作流的key
	 */
	private static final String ACTIVITI_ASSETARTICLES_USE_PAGE_KEY = ActivitiConstants.ActivitiObjectType.ACTIVITI_ASSETARTICLES_USE_PAGE.getKey();

	@SneakyThrows
	@Override
	public void execute(DelegateExecution execution) {
		AssetArticlesDao assetArticlesDao = SpringUtils.getBean(AssetArticlesDao.class);
		AssetArticlesApplyUseDao assetArticlesApplyUseDao = SpringUtils.getBean(AssetArticlesApplyUseDao.class);
		String processInstanceId = execution.getProcessInstanceId();//流程实例id
		// 获取用品使用主表信息
		Map<String, Object> map = assetArticlesApplyUseDao.queryAssetsArticlesUseByProcessInstanceId(processInstanceId);
		if(map == null || map.isEmpty()){
			throw new Exception("无法获取用品数据");
		}
		String useId = map.get("id").toString();
		//服务任务状态值
		String value1 = (String) state.getValue(execution);
		if("pass".equals(value1.toLowerCase())){//通过
			//获取领用单领用的用品列表
			List<Map<String, Object>> beans = assetArticlesApplyUseDao.queryAssetsArticlesUseGoodsById(map);
			for(Map<String, Object> bean : beans){
				//当前用品剩余的数量
				int residualNum = Integer.parseInt(bean.get("residualNum").toString());
				//用户申请领用的数量
				int applyUseNum = Integer.parseInt(bean.get("applyUseNum").toString());
				//允许用户领用的数量
				int actualUseNum = 0;
				//用品对应的领用状态    1.等待发放  2.库存不足  3.库存充足
				int applyState;
				if(residualNum >= applyUseNum){
					//当前库存充足
					applyState = 3;
					actualUseNum = applyUseNum;
				}else{
					//当前库存不足
					applyState = 2;
					actualUseNum = residualNum;
				}
				//重置库存剩余数量
				residualNum = residualNum - actualUseNum;
				//修改库存
				bean.put("residualNum", residualNum);
				assetArticlesDao.editAssetsArticlesById(bean);
				//修改用品领用状态
				bean.put("applyState", applyState);
				bean.put("actualUseNum", actualUseNum);
				assetArticlesApplyUseDao.editAssetsArticlesUseGoodsById(bean);
			}
			//修改用品领用单状态
			map.put("state", 2);
			assetArticlesApplyUseDao.editAssetsArticlesUseById(map);
		}else{
			// 修改用品领用单状态
			map.put("state", 3);
			assetArticlesApplyUseDao.editAssetsArticlesUseById(map);
			// 修改领用单关联的用品状态，state='4'为拒绝发放
			assetArticlesApplyUseDao.editAssetsArticlesUseGoodsByUseId(map);
		}
		// 编辑流程表参数
		ActivitiRunFactory.run(ACTIVITI_ASSETARTICLES_USE_PAGE_KEY).editApplyMationInActiviti(useId);
	}

}
