/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.erp.entity.TransmitObject;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpCommonService
 * @Description: ERP公共服务
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 21:58
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpCommonService {

	public void queryDepotHeadDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 根据单据id获取单据信息
	 *
	 * @param orderId 单据id
	 * @throws Exception
	 */
	Map<String, Object> queryDepotHeadMationById(String orderId) throws Exception;

	public void deleteErpOrderById(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 编辑单据时，需要回显商品数据以及规格列表，该方法则为整理这些数据
	 *
	 * @param norm 规格信息
	 * @param unitList 单位列表信息
	 */
	public void resetProductAndUnitToEditShow(Map<String, Object> norm, List<Map<String, Object>> unitList) throws Exception;

	/**
	 * 单据计算子单据信息
	 *
	 * @param jsonStr 子单据json字符串
	 * @param headerId 父单据id
	 * @param object 主单据信息
	 * @param entitys 要返回的单据子表实体集合信息
	 * @return 空
	 * @throws Exception
	 */
	public void resetChildBillTypeOneMation(String jsonStr, String headerId, TransmitObject object, List<Map<String, Object>> entitys) throws Exception;

	/**
	 * 创建订单时，订单数据整理
	 *
	 * @param header 整理后的数据
	 * @param from 入参-源数据
	 * @param user 用户信息
	 */
	public void addOrderCreateHaderMation(Map<String, Object> header, Map<String, Object> from, Map<String, Object> user) throws Exception;

	/**
	 * 编辑订单时，订单数据整理
	 *
	 * @param header 整理后的数据
	 * @param from 入参-源数据
	 * @param user 用户信息
	 */
	public void editOrderCreateHaderMation(Map<String, Object> header, Map<String, Object> from, Map<String, Object> user) throws Exception;

	/**
	 * 修改商品规格库存
	 *
	 * @param depotId 仓库id
	 * @param materialId 商品id
	 * @param normsId 规格id
	 * @param operNumber 变化数量
	 * @param type 1.入库  2.出库
	 * @throws Exception    参数
	 * @return void    返回类型
	 */
	public void editMaterialNormsDepotStock(String depotId, String materialId, String normsId, String operNumber, int type) throws Exception;

	/**
	 * （erp+生产）根据单据id以及类型获取所有子单据的信息
	 *
	 * @param orderId 订单id
	 * @param type 单据类型  1.销售订单；2.生产计划单；3.加工单；4.采购订单
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryChildOrderMation(String orderId, int type) throws Exception;

	public void queryDepotFlowLineDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    void editDepotHeadToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryDepotHeadSubmitTypeByOrderType(InputObject inputObject, OutputObject outputObject) throws Exception;

	void orderSubmitToApproval(InputObject inputObject, OutputObject outputObject) throws Exception;
}
