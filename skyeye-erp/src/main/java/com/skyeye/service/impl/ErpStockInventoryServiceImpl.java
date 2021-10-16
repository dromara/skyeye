/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.ErpStockInventoryDao;
import com.skyeye.service.ErpStockInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpStockInventoryServiceImpl
 * @Description: 库存盘点服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:43
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpStockInventoryServiceImpl implements ErpStockInventoryService{
	
	@Autowired
	private ErpStockInventoryDao erpStockInventoryDao;
	
	@Autowired
	private ErpCommonDao erpCommonDao;

	/**
     * 新增库存盘点信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void insertDepotNormsInventory(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		String normsId = params.get("normsId").toString();
		String depotId = params.get("depotId").toString();
		String materialId = params.get("materialId").toString();
		int number = Integer.parseInt(params.get("number").toString());//盘点后的数量
		int stockNum = 0;
		Map<String, Object> stock = erpCommonDao.queryDepotStockByDepotIdAndNormsId(depotId, normsId);
		//如果该规格在指定仓库中已经有存储数据，则判断数量做修改
		if(stock != null && !stock.isEmpty()){
			//当前库存
			stockNum = Integer.parseInt(stock.get("stockNum").toString());
			if(stockNum == number){
				outputObject.setreturnMessage("盘点前后数量不能一样.");
				return;
			}
			erpCommonDao.editDepotStockByDepotIdAndNormsId(depotId, normsId, number);
		}else{
			erpCommonDao.insertDepotStockByDepotIdAndNormsId(materialId, depotId, normsId, number);
		}
		//插入盘点历史表
		params.put("stockNum", stockNum);
		params.put("id", ToolUtil.getSurFaceId());
		params.put("createId", inputObject.getLogParams().get("id"));
		params.put("createTime", DateUtil.getTimeAndToString());
		erpStockInventoryDao.insertDepotNormsInventory(params);
	}
	
	/**
	 * 
	 * Title: queryDepotNormsNumberListByDepotId
	 * Description: 获取指定仓库的所有商品规格数量列表用于盘点
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.service.ErpStockInventoryService#queryDepotNormsNumberListByDepotId(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryDepotNormsNumberListByDepotId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpStockInventoryDao.queryDepotNormsNumberListByDepotId(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	 * Title: queryDepotNormsHistoryInventory
	 * Description: 获取库存盘点历史信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.service.ErpStockInventoryService#queryDepotNormsHistoryInventory(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryDepotNormsHistoryInventory(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpStockInventoryDao.queryDepotNormsHistoryInventory(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
