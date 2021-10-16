/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.StoreHouseApprovalDao;
import com.skyeye.exception.CustomException;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.StoreHouseApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @ClassName: StoreHouseApprovalServiceImpl
 * @Description: 出入库单据审核管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:46
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class StoreHouseApprovalServiceImpl implements StoreHouseApprovalService {
	
	@Autowired
	private StoreHouseApprovalDao storeHouseApprovalDao;
	
	@Autowired
	private ErpCommonDao erpCommonDao;
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	/**
	 * 采购、零售、销售出入库单据审核修改库存以及订单信息
	 *
	 * @param orderId 出入库单据id
	 * @param approvalResult 审批结果：pass:同意；其他:不同意
	 * @throws Exception
	 */
	@Override
	public void approvalOrder(String orderId, String approvalResult) throws Exception {
		//获取单据信息
		Map<String, Object> orderMation = storeHouseApprovalDao.queryOrderMationByOrderId(orderId);
		if(orderMation != null && !orderMation.isEmpty()){
			if("pass".equals(approvalResult)) {
				synchronized(orderId){
					//单据类型，是出库还是入库
					String orderType = orderMation.get("type").toString();
					if(ErpConstants.ERP_HEADER_TYPE_IS_EX_WAREHOUSE.equals(orderType)){
						//如果是出库，需要做库存数量判断
						//获取库存数量不足的商品
						List<Map<String, Object>> noStockList = storeHouseApprovalDao.queryNoStockListMationByOrderId(orderId);
						if(!noStockList.isEmpty()){
							throw new CustomException("单据中库存不足，审核失败.");
						}
					}
					//审核通过
					orderMation.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS);
					//获取当前单据所有规格的数量
					List<Map<String, Object>> thisOrderNum = erpCommonDao.queryOrderNormsNumMationByOrderId(orderId);
					//获取父单据单号
					String linkNumber = orderMation.containsKey("linkNumber") ? orderMation.get("linkNumber").toString() : "";
					if(!ToolUtil.isBlank(linkNumber)){
						//如果父单据单号不为空，则需要做商品规格数量校验
						List<Map<String, Object>> needNumOrder = erpCommonDao.queryOrderNormsOverNumMationByOrderNumber(linkNumber);
						int result = checkStockWhetherOutstrip(needNumOrder, thisOrderNum);
						if(result == 1){
							//什么都不做
						}else if(result == 2){
							throw new CustomException("超过父单据商品规格数量，审核失败.");
						}else if(result == 3){
							throw new CustomException("存在父单据不包含的规格，审核失败.");
						}else if(result == 4){
							// 修改父单据状态
							erpCommonDao.editOrderStatusToComByOrderNumber(linkNumber);
						}
					}
					// 修改库存
					editWarehouseOrderMaterialNormsStock(thisOrderNum, orderType);
				}
			} else {
				// 审核不通过
				orderMation.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS);
			}
			// 修改单据状态
			editWarehouseOrderMation(orderMation);
		}else{
			throw new CustomException("该单据状态已经改变或数据不存在.");
		}
	}

	/**
	 * 出入库单据修改单据信息
	 *
	 * @param orderMation 订单信息
	 * @throws Exception
	 */
	private void editWarehouseOrderMation(Map<String, Object> orderMation) throws Exception {
		String status = orderMation.get("status").toString();
		if(ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS.equals(status)){
			// 审核通过
			orderMation.put("realComplateTime", DateUtil.getTimeAndToString());
		}
		storeHouseApprovalDao.editWarehouseOrderStateToExamineById(orderMation);
	}

	/**
	 * 出入库单据审核修改库存
	 *
	 * @param thisOrderNum 单据所有规格的数量
	 * @param orderType 订单类型
	 * @throws Exception
	 */
	private void editWarehouseOrderMaterialNormsStock(List<Map<String, Object>> thisOrderNum, String orderType) throws Exception {
		for(Map<String, Object> bean : thisOrderNum){
			String depotId = bean.get("depotId").toString();
			String materialId = bean.get("materialId").toString();
			String normsId = bean.get("normsId").toString();
			String operNumber = bean.get("operNumber").toString();
			if(ErpConstants.ERP_HEADER_TYPE_IS_IN_WAREHOUSE.equals(orderType)){
				// 入库
				erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 1);
			}else if(ErpConstants.ERP_HEADER_TYPE_IS_EX_WAREHOUSE.equals(orderType)){
				// 出库
				erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 2);
			}
		}
	}
	
	/**
	 * 
	    * @Title: checkStockWhetherOutstrip
	    * @Description: 判断该单据审核通过后，该单据所属父单据的数量是否正常
	    * @param needNumOrder 父单据目前剩余的数量列表
	    * @param thisOrderNum 当前单据的数量列表
	    * @param @return    参数
	    * @return int    返回类型 
	    * 			1.当前单据的商品规格数量未超过父单据的数量，不需要修改父单据状态 
	    * 			2.当前单据的商品规格数量超过父单据的数量，不允许审核通过
	    * 			3.当前单据的商品规格存在父单据中不存在的规格，不允许审核通过
	    * 			4.当前单据的商品规格数量=父单据的商品规格剩余的数量，修改父单据状态
	    * @throws
	 */
	private int checkStockWhetherOutstrip(List<Map<String, Object>> needNumOrder, List<Map<String, Object>> thisOrderNum){
		if(thisOrderNum.size() > needNumOrder.size()){
			return 3;
		}else{
			//记录当前单据在父单据中存在的规格数量
			int number = 0;
			//记录父单据中为0的规格数量
			int parentIsZeroNum = 0;
			//遍历父单据的规格
			for(Map<String, Object> bean : needNumOrder) {
				String normsId = bean.get("normsId").toString();
				//剩余出入库数量
				int needNum = Integer.parseInt(bean.get("nowNumber").toString());
				Optional<Map<String, Object>> thisOrderNorms = thisOrderNum.stream()
						.filter(item -> normsId.equals(item.get("normsId").toString())).findFirst();
				//当前单据商品规格中包含父单据的指定规格
				if(thisOrderNorms != null && thisOrderNorms.isPresent()){
					number++;
					needNum = needNum - Integer.parseInt(thisOrderNorms.get().get("operNumber").toString());
				}
				if(needNum < 0){
					return 2;
				}
				if(needNum == 0){
					parentIsZeroNum++;
				}
			}
			if(number < thisOrderNum.size()){
				return 3;
			}
			if(parentIsZeroNum == needNumOrder.size()){
				return 4;
			}
			return 1;
		}
	}

	/**
     * 获取待审核其他单列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryNotApprovedOtherOrderList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = storeHouseApprovalDao.queryNotApprovedOtherOrderList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 其他单据单信息审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void editOtherOrderStateToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String orderId = map.get("id").toString();
		//获取单据信息
		Map<String, Object> orderMation = storeHouseApprovalDao.queryOrderMationByOrderId(orderId);
		if(orderMation != null && !orderMation.isEmpty()){
			String status = orderMation.get("status").toString();
			if(ErpConstants.ERP_HEADER_STATUS_IS_IN_APPROVED.equals(status)){
				//当前数据状态为审核中的可以进行审核
				String upStatus = map.get("status").toString();
				if("1".equals(upStatus)){
					synchronized(orderId){
						//单据出入库分类，组装单，拆分单，调拨
						String subType = orderMation.get("subType").toString();
						List<Map<String, Object>> noStockList = null;
						//获取库存数量不足的商品
						if(subType.equals(ErpConstants.DepoTheadSubType.SPLIT_LIST_ORDER.getType())){
							//拆分单
							noStockList = storeHouseApprovalDao.queryNoStockSplitListMationByOrderId(orderId);
						}else if(subType.equals(ErpConstants.DepoTheadSubType.ASSEMBLY_SHEET_ORDER.getType())){
							//组装单
							noStockList = storeHouseApprovalDao.queryNoStockAssemblyListMationByOrderId(orderId);
						}else if(subType.equals(ErpConstants.DepoTheadSubType.ALLOCATION_FORM_ORDER.getType())){
							//调拨单
							noStockList = storeHouseApprovalDao.queryNoStockListMationByOrderId(orderId);
						}else{
							outputObject.setreturnMessage("单据类型错误，审核失败.");
							return;
						}
						if(noStockList != null && !noStockList.isEmpty()){
							outputObject.setreturnMessage("单据中指定仓库的库存不足，审核失败.");
							return;
						}
						//审核通过
						orderMation.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS);
						//获取当前单据所有规格的数量
						List<Map<String, Object>> thisOrderNum = erpCommonDao.queryOrderNormsNumMationByOrderId(orderId);
						//修改库存
						updateOtherOrderStock(thisOrderNum, subType);
					}
				}else if("0".equals(upStatus)){
					//审核不通过
					orderMation.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS);
				}else{
					outputObject.setreturnMessage("参数错误");
					return;
				}
				// 修改单据状态
				editWarehouseOrderMation(orderMation);
			}
		}else{
			outputObject.setreturnMessage("该单据状态已经改变或数据不存在.");
		}
	}
	
	/**
	 	* @throws Exception 
	    * @Title: updateOtherOrderStock
	    * @Description: 其他单据修改库存
	    * @param thisOrderNum 当前单据所有规格的数量
	    * @param subType 单据出入库分类，组装单，拆分单，调拨
	    * @return void    返回类型
	    * @throws
	 */
	private void updateOtherOrderStock(List<Map<String, Object>> thisOrderNum, String subType) throws Exception{
		for(Map<String, Object> bean : thisOrderNum){
			String depotId = bean.get("depotId").toString();
			String materialId = bean.get("materialId").toString();
			String normsId = bean.get("normsId").toString();
			String operNumber = bean.get("operNumber").toString();
			String mType = bean.get("mType").toString();
			if(subType.equals(ErpConstants.DepoTheadSubType.SPLIT_LIST_ORDER.getType())){
				//拆分单
				if("1".equals(mType)){
					//组合件
					erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 2);
				}else if("2".equals(mType)){
					//普通子件
					erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 1);
				}
			}else if(subType.equals(ErpConstants.DepoTheadSubType.ASSEMBLY_SHEET_ORDER.getType())){
				//组装单
				if("1".equals(mType)){
					//组合件
					erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 1);
				}else if("2".equals(mType)){
					//普通子件
					erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 2);
				}
			}else if(subType.equals(ErpConstants.DepoTheadSubType.ALLOCATION_FORM_ORDER.getType())){
				//调拨单
				String anotherDepotId = bean.get("anotherDepotId").toString();//调入仓库
				//当前仓库出库
				erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 2);
				//调入仓库入库
				erpCommonService.editMaterialNormsDepotStock(anotherDepotId, materialId, normsId, operNumber, 1);
			}
		}
	}

	/**
     * 获取已审核其他单列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryPassApprovedOtherOrderList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = storeHouseApprovalDao.queryPassApprovedOtherOrderList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
