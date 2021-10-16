/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpStockInventoryService;

/**
 *
 * @ClassName: ErpStockInventoryController
 * @Description: 库存盘点控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:21
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ErpStockInventoryController {
	
	@Autowired
	private ErpStockInventoryService erpStockInventoryService;
	
	/**
     * 新增库存盘点信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpStockInventoryController/insertDepotNormsInventory")
    @ResponseBody
    public void insertDepotNormsInventory(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpStockInventoryService.insertDepotNormsInventory(inputObject, outputObject);
    }
    
    /**
     * 获取指定仓库的所有商品规格数量列表用于盘点
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpStockInventoryController/queryDepotNormsNumberListByDepotId")
    @ResponseBody
    public void queryDepotNormsNumberListByDepotId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpStockInventoryService.queryDepotNormsNumberListByDepotId(inputObject, outputObject);
    }
    
    /**
     * 获取库存盘点历史信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/ErpStockInventoryController/queryDepotNormsHistoryInventory")
    @ResponseBody
    public void queryDepotNormsHistoryInventory(InputObject inputObject, OutputObject outputObject) throws Exception{
    	erpStockInventoryService.queryDepotNormsHistoryInventory(inputObject, outputObject);
    }
	
}
