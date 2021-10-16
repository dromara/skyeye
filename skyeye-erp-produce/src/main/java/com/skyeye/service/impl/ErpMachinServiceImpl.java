/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.BarCodeUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.ErpMachinDao;
import com.skyeye.dao.ErpProductionDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.service.ErpCommonService;
import com.skyeye.service.ErpMachinService;
import com.skyeye.service.ErpPickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpMachinServiceImpl
 * @Description: 加工单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpMachinServiceImpl implements ErpMachinService {
	
	@Autowired
	private ErpMachinDao erpMachinDao;
	
	@Autowired
	private ErpProductionDao erpProductionDao;
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private ErpCommonDao erpCommonDao;
	
	@Autowired
	private ErpPickService erpPickService;
	
	@Autowired
	private ErpCommonService erpCommonService;
	
	@Value("${IMAGES_PATH}")
	private String tPath;

	/**
	 * 加工单类型
	 */
	private static final String ORDER_MACHIN_HEADER_TYPE = ErpConstants.DepoTheadSubType.MACHIN_HEADER.getType();

	/**
	 * 加工单子单据类型（工序验收单）
	 */
	private static final String ORDER_MACHIN_CHILD_TYPE = ErpConstants.DepoTheadSubType.MACHIN_CHILD.getType();

	/**
	 * 验收入库单类型
	 */
	private static final String ORDER_ACCEPTANCE_WAREHOUSING_TYPE = ErpConstants.DepoTheadSubType.PUT_ACCEPTANCE_WAREHOUSING.getType();

	/**
     * 获取加工单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMachinOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpMachinDao.queryMachinOrderToList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 新增加工单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertMachinOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String materielStr = map.get("materielStr").toString();//物料信息
		String procedureStr = map.get("procedureJsonStr").toString();//加工工序列表
		if(ToolUtil.isJson(materielStr) && ToolUtil.isJson(procedureStr)){
			//单据主表id
			String useId = ToolUtil.getSurFaceId();
			String number = map.get("number").toString();
			//物料信息
			List<Map<String, Object>> materiel = rsetMachinMaterial(materielStr, useId);
			//工序信息
			List<Map<String, Object>> procedure = rsetMachinChild(procedureStr, useId, number);
			if(procedure.isEmpty()){
				outputObject.setreturnMessage("请选择工序.");
				return;
			}
			//主单据信息
			Map<String, Object> header = new HashMap<>();
			header.put("id", useId);
			String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_MACHIN_HEADER_TYPE);
			header.put("orderNum", orderNum);
			header.put("productionId", map.get("orderId"));
			header.put("departmentId", map.get("departmentId"));
			header.put("barCode", getImageBarCodePath(orderNum));
			header.put("materialId", map.get("materialId"));
			header.put("normsId", map.get("normsId"));
			header.put("needNum", number);
			header.put("startTime", map.get("starTime"));
			header.put("endTime", map.get("endTime"));
			header.put("state", 1);//新建
			header.put("remark", map.get("remark"));
			header.put("createId", inputObject.getLogParams().get("id"));
			header.put("createTime", DateUtil.getTimeAndToString());
			
			//插入加工单信息
			erpMachinDao.insertMachinOrderMation(header);
			//插入工序信息
			erpMachinDao.insertMachinOrderProcedureMation(procedure);
			//插入物料清单信息
			if(!materiel.isEmpty()){
				erpMachinDao.insertMachinOrderMaterialMation(materiel);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}
	
	/**
	 * 
	    * @Title: rsetMachinMaterial
	    * @Description: 构造物料信息集合
	    * @param materielStr 物料信息字符串
	    * @param useId 主单据id
	    * @return void    返回类型
	    * @throws
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> rsetMachinMaterial(String materielStr, String useId){
		//处理数据
		List<Map<String, Object>> jArray = JSONUtil.toList(materielStr, null);
		List<Map<String, Object>> beans = new ArrayList<>();
		for(int i = 0; i < jArray.size(); i++){
			Map<String, Object> materiel = jArray.get(i);
			Map<String, Object> bean = new HashMap<>();
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("headerId", useId);
			bean.put("materialId", materiel.get("materialId"));
			bean.put("normsId", materiel.get("mUnitId"));
			bean.put("needNumber", materiel.get("rkNum"));
			bean.put("unitPrice", materiel.get("unitPrice"));
			beans.add(bean);
		}
		return beans;
	}
	
	/**
	 * @throws Exception 
	    * @Title: rsetMachinChild
	    * @Description: 构造工序信息集合
	    * @param procedureStr 工序信息字符串
	    * @param useId 主单据id
	    * @param number 加工数量
	    * @param @return    参数
	    * @return List<Map<String,Object>>    返回类型
	    * @throws
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> rsetMachinChild(String procedureStr, String useId, String number) throws Exception{
		//处理数据
		List<Map<String, Object>> jArray = JSONUtil.toList(procedureStr, null);
		List<Map<String, Object>> beans = new ArrayList<>();
		for(int i = 0; i < jArray.size(); i++){
			Map<String, Object> materiel = jArray.get(i);
			Map<String, Object> bean = new HashMap<>();
			String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_MACHIN_CHILD_TYPE);
			bean.put("id", ToolUtil.getSurFaceId());
			bean.put("headerId", useId);
			bean.put("procedureId", materiel.get("procedureId"));
			bean.put("procedureNum", materiel.get("number"));
			bean.put("procedureName", materiel.get("procedureName"));
			bean.put("unitPrice", materiel.get("unitPrice"));
			bean.put("orderNumber", orderNum);
			bean.put("barCode", getImageBarCodePath(orderNum));
			bean.put("needNum", number);
			bean.put("state", 1);//待验收
			bean.put("createTime", DateUtil.getTimeAndToString());
			beans.add(bean);
		}
		return beans;
	}
	
	/**
	 * 
	    * @Title: getImageBarCodePath
	    * @Description: 获取条形码
	    * @param orderNum
	    * @param @return
	    * @throws IOException    参数
	    * @return String    返回类型
	    * @throws
	 */
	private String getImageBarCodePath(String orderNum) throws IOException{
		//获取条形码
		BufferedImage image = BarCodeUtil.insertWords(orderNum);
		String newFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
		String filePath = "/images/upload/erpMachin/";
		String basePath = tPath + "\\upload\\erpMachin";
		File pack = new File(basePath);
		if(!pack.isDirectory())//目录不存在 
			pack.mkdirs();//创建目录
        ImageIO.write(image, "jpg", new File(basePath + "\\" + newFileName));
        return filePath + newFileName;
	}

	/**
     * 编辑加工单信息时回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMachinOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String rowId = map.get("id").toString();
		Map<String, Object> machinOrderMation = erpMachinDao.queryMachinOrderMationById(rowId);
		if(machinOrderMation != null && !machinOrderMation.isEmpty()){
			//加工成品规格信息
			List<Map<String, Object>> unitList = materialDao.queryMaterialUnitByIdToSelect(machinOrderMation.get("materialId").toString());
			if ("1".equals(machinOrderMation.get("unit").toString())) {// 不是多单位
				unitList.get(0).put("name", machinOrderMation.get("unitName").toString());
			}
			machinOrderMation.put("unitList", unitList);
			
			//工序信息
			List<Map<String, Object>> procedure = erpMachinDao.queryMachinOrderProcedureMationByOrdeId(rowId);
			machinOrderMation.put("procedure", procedure);
			//物料清单信息
			List<Map<String, Object>> materiel = erpMachinDao.queryMachinOrderMaterialMationByOrdeId(rowId);
			materiel.forEach(bean -> {
				//物料规格信息
				List<Map<String, Object>> maUnitList;
				try {
					maUnitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
					if ("1".equals(bean.get("unit").toString())) {// 不是多单位
						maUnitList.get(0).put("name", bean.get("unitName").toString());
					}
					bean.put("unitList", maUnitList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			machinOrderMation.put("materiel", materiel);
			//判断是否绑定计划单id
			if(machinOrderMation.containsKey("productionId") && !ToolUtil.isBlank(machinOrderMation.get("productionId").toString())){
				//获取绑定的计划单信息，包含外购和自产
				machinOrderMation.put("referToMation", erpProductionDao.queryErpProductionOutsideProListByOrderId(machinOrderMation.get("productionId").toString(), "1,2"));
			}else{
				machinOrderMation.put("referToMation", new ArrayList<>());
			}
			
			outputObject.setBean(machinOrderMation);
		}else{
			outputObject.setreturnMessage("该单据不存在.");
		}
	}

	/**
     * 编辑加工单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editMachinOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//单据主表id
		String useId = map.get("id").toString();
		Map<String, Object> machinOrderMation = erpMachinDao.queryMachinOrderMationById(useId);
		if(machinOrderMation == null || machinOrderMation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if(!"1".equals(machinOrderMation.get("state").toString())
				&& !"4".equals(machinOrderMation.get("state").toString())){
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
		String materielStr = map.get("materielStr").toString();//物料信息
		String procedureStr = map.get("procedureJsonStr").toString();//加工工序列表
		if(ToolUtil.isJson(materielStr) && ToolUtil.isJson(procedureStr)){
			String number = map.get("number").toString();
			//物料信息
			List<Map<String, Object>> materiel = rsetMachinMaterial(materielStr, useId);
			//工序信息
			List<Map<String, Object>> procedure = rsetMachinChild(procedureStr, useId, number);
			if(procedure.isEmpty()){
				outputObject.setreturnMessage("请选择工序.");
				return;
			}
			//主单据信息
			Map<String, Object> header = new HashMap<>();
			header.put("id", useId);
			header.put("productionId", map.get("orderId"));
			header.put("departmentId", map.get("departmentId"));
			header.put("barCode", getImageBarCodePath(machinOrderMation.get("orderNum").toString()));
			header.put("materialId", map.get("materialId"));
			header.put("normsId", map.get("normsId"));
			header.put("needNum", number);
			header.put("startTime", map.get("starTime"));
			header.put("endTime", map.get("endTime"));
			header.put("remark", map.get("remark"));
			//修改加工单信息
			FileUtil.deleteFile(tPath.replace("images", "") + machinOrderMation.get("barCode").toString());
			erpMachinDao.editMachinOrderMationById(header);
			//删除工序信息
			List<Map<String, Object>> procedures = erpMachinDao.queryMachinOrderProcedureMationByOrdeId(useId);
			//删除之前的工序码
			procedures.forEach(bean -> {
				FileUtil.deleteFile(tPath.replace("images", "") + bean.get("barCode").toString());
			});
			erpMachinDao.deleteMachinOrderProcedureMationByOrderId(useId);
			//删除物料清单信息
			erpMachinDao.deleteMachinOrderMaterialMationByOrderId(useId);
			//插入工序信息
			erpMachinDao.insertMachinOrderProcedureMation(procedure);
			//插入物料清单信息
			if(!materiel.isEmpty()){
				erpMachinDao.insertMachinOrderMaterialMation(materiel);
			}
		}else{
			outputObject.setreturnMessage("数据格式错误.");
		}
	}

	/**
     * 删除加工单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void deleteMachinOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//单据主表id
		String useId = map.get("id").toString();
		Map<String, Object> machinOrderMation = erpMachinDao.queryMachinOrderMationById(useId);
		if(machinOrderMation == null || machinOrderMation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if(!"1".equals(machinOrderMation.get("state").toString())
				&& !"4".equals(machinOrderMation.get("state").toString())){
			outputObject.setreturnMessage("该单据状态已更改.");
			return;
		}
		//删除加工单信息
		erpMachinDao.deleteMachinOrderMationById(useId);
		//删除加工单条形码
		FileUtil.deleteFile(tPath.replace("images", "") + machinOrderMation.get("barCode").toString());
		//删除工序信息
		List<Map<String, Object>> procedures = erpMachinDao.queryMachinOrderProcedureMationByOrdeId(useId);
		//删除工序的条形码
		procedures.forEach(bean -> {
			FileUtil.deleteFile(tPath.replace("images", "") + bean.get("barCode").toString());
		});
		erpMachinDao.deleteMachinOrderProcedureMationByOrderId(useId);
		//删除物料清单信息
		erpMachinDao.deleteMachinOrderMaterialMationByOrderId(useId);
	}

	/**
     * 加工单详情信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMachinOrderDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String rowId = map.get("id").toString();
		Map<String, Object> machinOrderMation = erpMachinDao.queryMachinOrderMationById(rowId);
		if(machinOrderMation != null && !machinOrderMation.isEmpty()){
			//工序信息
			List<Map<String, Object>> procedure = erpMachinDao.queryMachinOrderProcedureMationByOrdeId(rowId);
			machinOrderMation.put("procedure", procedure);
			//物料清单信息
			List<Map<String, Object>> materiel = erpMachinDao.queryMachinOrderMaterialMationByOrdeId(rowId);
			machinOrderMation.put("materiel", materiel);
			outputObject.setBean(machinOrderMation);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该单据不存在.");
		}
	}

	/**
     * 加工单提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editMachinOrderMationToSubmitById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//单据主表id
		String useId = map.get("id").toString();
		Map<String, Object> machinOrderMation = erpMachinDao.queryMachinOrderMationById(useId);
		if(machinOrderMation == null || machinOrderMation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("1".equals(machinOrderMation.get("state").toString())
				|| "4".equals(machinOrderMation.get("state").toString())){
			//只有新建和审核拒绝的才能提交审核
			erpMachinDao.editMachinOrderStateMationById(useId, "2");//修改为审核中
		}else{
			outputObject.setreturnMessage("该单据状态已更改.");
		}
	}

	/**
     * 加工单审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editMachinOrderMationToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		// 单据主表id
		String useId = map.get("id").toString();
		Map<String, Object> machinOrderMation = erpMachinDao.queryMachinOrderMationById(useId);
		if(machinOrderMation == null || machinOrderMation.isEmpty()){
			outputObject.setreturnMessage("该单据不存在.");
			return;
		}
		if("2".equals(machinOrderMation.get("state").toString())){
			//只有审核中的才能审核
			String status = map.get("status").toString();
			String userId = inputObject.getLogParams().get("id").toString();
			String content = map.get("content").toString();
			String time = DateUtil.getTimeAndToString();
			if("1".equals(status)){
				//审核通过
				//判断是否绑定计划单id
				if(machinOrderMation.containsKey("productionId") && !ToolUtil.isBlank(machinOrderMation.get("productionId").toString())){
					//获取工序信息
					List<Map<String, Object>> procedure = erpMachinDao.queryMachinOrderProcedureMationByOrdeId(useId);
					for(Map<String, Object> bean : procedure){
						int num = erpMachinDao.editProductionProcedureBindMationByOrdeIdAndProceId(machinOrderMation.get("productionId").toString(), bean.get("procedureId").toString());
						if(num == 0){
							throw new Exception("选定生产计划单中存在已下达加工单的工序，无法审核通过.");
						}
					}
				}
				erpMachinDao.editMachinOrderStateMationToExamineById(useId, "3", userId, content, time);
			}else{
				//审核不通过
				erpMachinDao.editMachinOrderStateMationToExamineById(useId, "4", userId, content, time);
			}
		}else{
			outputObject.setreturnMessage("该单据状态已更改.");
		}
	}

	/**
     * 根据部门获取已经审核通过的加工单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMachinStateIsPassOrderListByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String departMentId = user.get("departmentId").toString();
		params.put("departMentId", departMentId);
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpMachinDao.queryMachinStateIsPassOrderListByDepartmentId(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据部门获取审核通过的加工单列表信息展示为表格供领料/补料单使用（不包含已完成的加工单）
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMachinStateIsPassNoComplateOrderListByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String departMentId = user.get("departmentId").toString();
		params.put("departMentId", departMentId);
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpMachinDao.queryMachinStateIsPassNoComplateOrderListByDepartmentId(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据加工单id获取该单据下的所有单据中商品以及剩余领料数量
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMachinStateIsPassOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		String rowId = params.get("id").toString();
		//物料清单信息
		List<Map<String, Object>> materiel = erpMachinDao.queryMachinStateIsPassOrderMationById(rowId);
		materiel.forEach(bean -> {
			//物料规格信息
			List<Map<String, Object>> maUnitList;
			try {
				maUnitList = materialDao.queryMaterialUnitByIdToSelect(bean.get("productId").toString());
				if ("1".equals(bean.get("unit").toString())) {// 不是多单位
					maUnitList.get(0).put("name", bean.get("unitName").toString());
				}
				bean.put("unitList", maUnitList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		outputObject.setBeans(materiel);
	}

	/**
     * 加工单工序验收
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editMachinStateMationByChildId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		String childId = params.get("childId").toString();
		Map<String, Object> machinChildMation = erpMachinDao.queryMachinChildMationByChildId(childId);
		// 判断该加工单的子单据是否存在
		if(machinChildMation != null && !machinChildMation.isEmpty()){
			String state = machinChildMation.get("state").toString();
			if("1".equals(state)){
				// 待验收数量
				int needNum = Integer.parseInt(machinChildMation.get("needNum").toString());
				// 验收合格数量
				int acceptNum = Integer.parseInt(params.get("acceptNum").toString());
				// 验收不合格数量
				int belowNum = Integer.parseInt(params.get("belowNum").toString());
				// 加工单id
				String headerId = machinChildMation.get("headerId").toString();
				if(acceptNum + belowNum == needNum){
					List<Map<String, Object>> procedure = erpMachinDao.queryMachinOrderProcedureMationByOrdeId(headerId);
					// 判断当前工序验收后面是否还有工序
					int index = -1;
					for(int i = 0; i < procedure.size(); i++){
						if(childId.equals(procedure.get(i).get("id").toString())){
							index = i;
							break;
						}
					}
					if(index + 1 == procedure.size()){
						// 物料清单信息
						List<Map<String, Object>> materiel = erpMachinDao.queryMachinOrderMaterialListByOrdeId(headerId);
						for(Map<String, Object> bean : materiel){
							int currentTock = Integer.parseInt(bean.get("currentTock").toString());
							int operNumber = Integer.parseInt(bean.get("operNumber").toString());
							if(currentTock < operNumber){
								outputObject.setreturnMessage("部门库存存量不足，验收失败.");
								return;
							}
						}
						// 该加工单的最后一个工序,生成验收入库单
						createAcceptanceWarehousing(headerId, inputObject.getLogParams(), 
								acceptNum, params.get("depotId").toString());
						// 修改加工单状态为已完成
						erpMachinDao.queryMachinOrderToComplateByOrdeId(headerId);
						// 修改部门库存存量
						updateDepartmentDepotStock(materiel);
						// 修改生产计划单信息
						if(machinChildMation.containsKey("productionId") && !ToolUtil.isBlank(machinChildMation.get("productionId").toString())){
							updateProductionMation(machinChildMation.get("productionId").toString());
						}
					}
					// 不是该加工单的最后一个工序,修改验收信息
					params.put("state", 2);// 验收完成
					params.put("acceptTime", DateUtil.getTimeAndToString());
					erpMachinDao.editMachinStateMationByChildId(params);
				}else{
					outputObject.setreturnMessage("验收数量与待验收数量不匹配，验收失败.");
				}
			}else{
				outputObject.setreturnMessage("已验收，无法进行多次验收.");
			}
		}else{
			outputObject.setreturnMessage("无法找到验收信息，验收失败.");
		}
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: updateProductionMation
	    * @Description: 修改生产计划单状态
	    * @param productionId    参数
	    * @return void    返回类型
	    * @throws
	 */
	private void updateProductionMation(String productionId) throws Exception {
		// 获取该生产计划单下面未完成的工序关联订单
		List<Map<String, Object>> beans = erpMachinDao.queryNoComProcedureByProductionId(productionId);
		if(beans == null || beans.isEmpty()){
			erpMachinDao.editProductionStateIsComByProductionId(productionId);
		}
	}

	/**
	 * @throws Exception 
	 * 
	    * @Title: createAcceptanceWarehousing
	    * @Description: 生成验收入库单
	    * @param machinId 加工单id
	    * @param user 用户信息
	    * @param acceptNum 验收数量
	    * @return void    返回类型
	    * @throws
	 */
	private void createAcceptanceWarehousing(String machinId, Map<String, Object> user, int acceptNum, String depotId) throws Exception{
		// 物料清单信息
		List<Map<String, Object>> materiel = erpMachinDao.queryMachinOrderFinishedMaterialMationByOrdeId(machinId);
		materiel.parallelStream().forEach(bean -> {
			bean.put("rkNum", acceptNum);
			bean.put("depotId", depotId);
		});
		// 单据主表id
		String useId = ToolUtil.getSurFaceId();
		// 值传递对象，获取对应的值即可
		TransmitObject object = new TransmitObject();
		// 单据子表实体集合信息
		List<Map<String, Object>> entitys = new ArrayList<>();
		erpCommonService.resetChildBillTypeOneMation(JSONUtil.toJsonStr(materiel), useId, object, entitys);
		
		Map<String, Object> map = new HashMap<>();
		map.put("operTime", DateUtil.getTimeAndToString());
		map.put("remark", "");
		// 单据主表对象
		Map<String, Object> depothead = new HashMap<>();
		erpCommonService.addOrderCreateHaderMation(depothead, map, user);
		depothead.put("id", useId);
		depothead.put("type", ErpConstants.ERP_HEADER_TYPE_IS_IN_WAREHOUSE);
		depothead.put("subType", ORDER_ACCEPTANCE_WAREHOUSING_TYPE);// 验收入库单
		String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_ACCEPTANCE_WAREHOUSING_TYPE);
		depothead.put("defaultNumber", orderNum);// 初始票据号
		depothead.put("number", orderNum);// 票据号
		depothead.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS);
		depothead.put("linkNumber", machinId);
		// 合计金额
		depothead.put("totalPrice", object.getAllPrice());
		erpCommonDao.insertOrderParentMation(depothead);
		erpCommonDao.insertOrderChildMation(entitys);
		// 修改库存
		updateDepotStock(entitys);
	}
	
	/**
	 * @throws Exception 
	 * 
	    * @Title: updateDepotStock
	    * @Description: 修改库存
	    * @param entitys    参数
	    * @return void    返回类型
	    * @throws
	 */
	private void updateDepotStock(List<Map<String, Object>> entitys) throws Exception{
		// 修改库存
		for(Map<String, Object> bean : entitys){
			String depotId = bean.get("depotId").toString();
			String materialId = bean.get("materialId").toString();
			String normsId = bean.get("mUnitId").toString();
			String operNumber = bean.get("operNumber").toString();
			// 入库
			erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, 1);
		}
	}
	
	/**
	 * 
	    * @Title: updateDepartmentDepotStock
	    * @Description: 修改部门库存存量信息
	    * @param entitys
	    * @throws Exception    参数
	    * @return void    返回类型
	    * @throws
	 */
	private void updateDepartmentDepotStock(List<Map<String, Object>> entitys) throws Exception{
		// 修改部门库存存量信息
		for(Map<String, Object> bean : entitys){
			String departmentId = bean.get("departmentId").toString();
			String materialId = bean.get("materialId").toString();
			String normsId = bean.get("normsId").toString();
			String operNumber = bean.get("operNumber").toString();
			// 出库
			erpPickService.updateDepartMentStock(departmentId, materialId, normsId, operNumber, 2);
		}
	}
	
}
