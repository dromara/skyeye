/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MaterialDao;
import com.skyeye.dao.SealSeServiceDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.service.JobMateMationService;
import com.skyeye.service.SealSeServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceServiceImpl
 * @Description: 售后服务工单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:23
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealSeServiceServiceImpl implements SealSeServiceService {

    @Autowired
    private SealSeServiceDao sealSeServiceDao;
    
    @Autowired
	private MaterialDao materialDao;
    
    @Autowired
	private JobMateMationService jobMateMationService;
    
    @Autowired
	private SysEnclosureDao sysEnclosureDao;
    
    @Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

	/**
	 * 其他出库单类型
	 */
	private static final String ORDER_OUT_OTHERS_TYPE = ErpConstants.DepoTheadSubType.OUT_IS_OTHERS.getType();
	
	/**
	 *
	 * @Title: queryCrmServiceList
	 * @Description: 获取全部工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToWorkList
	 * @Description: 获取全部待派工的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceWaitToWorkList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceWaitToWorkList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToReceiveList
	 * @Description: 获取当前登录人待接单的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceWaitToReceiveList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("receiver", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceWaitToReceiveList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToSignonList
	 * @Description: 获取当前登录人待签到的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceWaitToSignonList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("receiver", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceWaitToSignonList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToSignonList
	 * @Description: 获取当前登录人待完工的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceWaitToFinishList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("receiver", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceWaitToFinishList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToAssessmentList
	 * @Description: 获取当前登录人待评价的列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceWaitToAssessmentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("receiver", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceWaitToAssessmentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceWaitToAssessmentList
	 * @Description: 获取全部待评价的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllSealSeServiceWaitToAssessmentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.queryAllSealSeServiceWaitToAssessmentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceWaitToCheckList
	 * @Description: 获取全部待审核的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllSealSeServiceWaitToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.queryAllSealSeServiceWaitToCheckList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceFinishedList
	 * @Description: 获取已完成的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryAllSealSeServiceFinishedList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.queryAllSealSeServiceFinishedList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: querySealSeServiceTodetails
	 * @Description: 查询工单详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceTodetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceToDetails(map);
		// 集合中放入附件信息
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 集合中放入完工附件信息
		bean.put("comEnclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("comEnclosureInfo").toString()));
        // 集合中放入工单协助人信息
        bean.put("cooperationUserId", sysEveUserStaffDao.queryUserNameList(bean.get("cooperationUserId").toString()));
        // 集合中放入配件使用信息
        bean.put("materialMation", sealSeServiceDao.queryMaterialMationById(map));
        // 集合中放入反馈信息
        List<Map<String, Object>> feedbackMation = sealSeServiceDao.queryFeedbackMationById(map);
        for(Map<String, Object> item: feedbackMation){
        	item.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(item.get("enclosureInfo").toString()));
        }
        bean.put("feedbackMation", feedbackMation);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: insertSealSeServiceMation
	 * @Description: 新增售后服务信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSealSeServiceMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		if(ToolUtil.isBlank(map.get("productWarranty").toString())){
			map.put("productWarranty", null);
		}
		String orderId = ToolUtil.getSurFaceId();
		map.put("id", orderId);
		map.put("orderNum", "SHFW" + ToolUtil.getUniqueKey());
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("state", "1");
		if(!ToolUtil.isBlank(map.get("serviceUserId").toString())){//接收人不为空，则进入待接单状态
			map.put("state", '2');
			map.put("serviceTime", DateUtil.getTimeAndToString());
		}
		map.put("type", "2");
		map.put("parentId", "0");
		map.put("createId", userId);
		map.put("declarationId", userId);
		map.put("pointSubscribeTime", ToolUtil.isBlank(map.get("pointSubscribeTime").toString()) ? null : map.get("pointSubscribeTime").toString());
		int size = sealSeServiceDao.insertSealSeServiceMation(map);
		if(size == 0){
			outputObject.setreturnMessage("新增工单失败。");
		}else{
			if(!ToolUtil.isBlank(map.get("serviceUserId").toString())){//接收人不为空，则进入待接单状态
				//派工成功mq消息任务
				Map<String, Object> notice = new HashMap<>();
				notice.put("serviceId", orderId);//工单id
				notice.put("type", MqConstants.JobMateMationJobType.WATI_WORKER_SEND.getJobType());//消息队列任务类型
				jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(notice), userId);
			}
		}
	}
	
	/**
	 *
	 * @Title: querySealSeServiceTodetails
	 * @Description: 根据id获取售后服务信息用于编辑回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void querySealSeServiceMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceMationToEdit(map);
		if(bean != null && !bean.isEmpty()){
			//集合中放入附件信息
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
			//集合中放入工单接收人信息
	        bean.put("serviceUserId", sysEveUserStaffDao.queryUserNameList(bean.get("serviceUserId").toString()));
	        //集合中放入工单协助人信息
	        bean.put("cooperationUserId", sysEveUserStaffDao.queryUserNameList(bean.get("cooperationUserId").toString()));
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}
	
	/**
	 *
	 * @Title: editSealSeServiceMationById
	 * @Description: 编辑售后服务信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if(bean != null && !bean.isEmpty()){
			if("1".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//1.待派工  2.待接单可以进行编辑
				if(ToolUtil.isBlank(map.get("productWarranty").toString())){
					map.put("productWarranty", null);
				}
				if(!ToolUtil.isBlank(map.get("serviceUserId").toString())){//接收人不为空，则进入待接单状态
					map.put("state", '2');
					if("1".equals(bean.get("state").toString())){
						map.put("serviceTime", DateUtil.getTimeAndToString());
					}
				}else{
					map.put("state", '1');
				}
				map.put("pointSubscribeTime", ToolUtil.isBlank(map.get("pointSubscribeTime").toString()) ? null : map.get("pointSubscribeTime").toString());
				int size = sealSeServiceDao.editSealSeServiceMationById(map);
				if(size == 0){
					outputObject.setreturnMessage("编辑工单失败。");
				}else{
					if(!ToolUtil.isBlank(map.get("serviceUserId").toString())){//接收人不为空，则进入待接单状态
						//派工成功mq消息任务
						Map<String, Object> notice = new HashMap<>();
						notice.put("serviceId", map.get("id"));//工单id
						notice.put("type", MqConstants.JobMateMationJobType.WATI_WORKER_SEND.getJobType());//消息队列任务类型
						jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(notice), inputObject.getLogParams().get("id").toString());
					}
				}
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceWaitToWorkMation
	 * @Description: 派工时获取派工信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceWaitToWorkMation(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

	/**
	 *
	 * @Title: editSealSeServiceWaitToWorkMation
	 * @Description: 派工
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if(bean != null && !bean.isEmpty()){
			if("1".equals(bean.get("state").toString())){//1.待派工可以进行派工
				map.put("serviceTime", DateUtil.getTimeAndToString());
				int size = sealSeServiceDao.editSealSeServiceWaitToWorkMation(map);
				if(size == 0){
					outputObject.setreturnMessage("派工失败。");
				}else{
					//派工成功mq消息任务
					Map<String, Object> notice = new HashMap<>();
					notice.put("serviceId", map.get("id"));//工单id
					notice.put("type", MqConstants.JobMateMationJobType.WATI_WORKER_SEND.getJobType());//消息队列任务类型
					jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(notice), inputObject.getLogParams().get("id").toString());
				}
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToReceiveMation
	 * @Description: 获取接单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceWaitToReceiveMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceWaitToReceiveMation(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

	/**
	 *
	 * @Title: insertSealSeServiceWaitToReceiveMation
	 * @Description: 接单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSealSeServiceWaitToReceiveMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if(bean != null && !bean.isEmpty()){
			if("2".equals(bean.get("state").toString())){//2.待接单可以进行接单
				String userId = inputObject.getLogParams().get("id").toString();
				map.put("serviceId", map.get("id"));
				map.put("id", ToolUtil.getSurFaceId());
				map.put("createTime", DateUtil.getTimeAndToString());
				map.put("receiverId", userId);
				sealSeServiceDao.insertSealSeServiceWaitToReceiveMation(map);
				sealSeServiceDao.editSealSeServiceWaitToReceiveMation(map);
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}
	
	/**
	 *
	 * @Title: querySealSeServiceWaitToSignonMation
	 * @Description: 获取签到信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceWaitToSignonMation(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

	/**
	 *
	 * @Title: insertSealSeServiceWaitToSignonMation
	 * @Description: 签到
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if(bean != null && !bean.isEmpty()){
			if("3".equals(bean.get("state").toString())){//3.待签到可以进行签到
				String userId = inputObject.getLogParams().get("id").toString();
				map.put("serviceId", map.get("id"));
				map.put("id", ToolUtil.getSurFaceId());
				map.put("createTime", DateUtil.getTimeAndToString());
				map.put("registerId", userId);
				sealSeServiceDao.insertSealSeServiceWaitToSignonMation(map);
				sealSeServiceDao.editSealSeServiceWaitToSignonMation(map);
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

	/**
	 *
	 * @Title: deleteSealSeServiceMationById
	 * @Description: 删除售后服务信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if(bean != null && !bean.isEmpty()){
			if("1".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//1.待派工  2.待接单可以进行删除
				sealSeServiceDao.deleteSealSeServiceMationById(map);
			}else{
				outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
			}
		}else{
			outputObject.setreturnMessage("不存在的工单信息。");
		}
	}

	/**
	 *
	 * @Title: insertSealSeServiceApplyMation
	 * @Description: 新增配件申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void insertSealSeServiceApplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String applyMaterialStr = map.get("applyMaterialStr").toString();//申领配件
		if(ToolUtil.isJson(applyMaterialStr)){
			String userId = inputObject.getLogParams().get("id").toString();
			String applyId = ToolUtil.getSurFaceId();//申领主表id
			//处理数据
			List<Map<String, Object>> jArray = JSONUtil.toList(applyMaterialStr, null);
			//产品中间转换对象，单据子表存储对象
			Map<String, Object> bean, entity, currentTock;
			List<Map<String, Object>> entitys = new ArrayList<>();//单据子表实体集合信息
			BigDecimal allPrice = new BigDecimal("0");//主单总价
			BigDecimal itemAllPrice = null;//子单对象
			for(int i = 0; i < jArray.size(); i++){
				bean = jArray.get(i);
				entity = sealSeServiceDao.queryMaterialsById(bean);//查询配件详细信息
				currentTock = sealSeServiceDao.queryMaterialsCurrentTockById(bean);//查询配件当前库存
				if(Integer.parseInt(currentTock.get("currentTock").toString()) < Integer.parseInt(bean.get("rkNum").toString())){
					outputObject.setreturnMessage(entity.get("materialName").toString() + "库存不足，请重新选择配件数据！");
					return;
				}
				if(entity != null && !entity.isEmpty()){
					//获取单价
					itemAllPrice = new BigDecimal(entity.get("unitPrice").toString());
					entity.put("id", ToolUtil.getSurFaceId());
					entity.put("serviceId", map.get("serviceId"));//工单Id
					entity.put("applyId", applyId);//单据主表id
					entity.put("operNumber", bean.get("rkNum"));//数量
					//计算子单总价：单价*数量
					itemAllPrice = itemAllPrice.multiply(new BigDecimal(bean.get("rkNum").toString()));
					entity.put("allPrice", itemAllPrice);//单据子表总价
					entity.put("remark", bean.get("remark"));//备注
					entity.put("depotId", bean.get("depotId"));//仓库
					entity.put("createId", userId);//创建人
					entity.put("createTime", DateUtil.getTimeAndToString());//创建时间
					entitys.add(entity);
					//计算主单总价
					allPrice = allPrice.add(itemAllPrice);
				}
			}
			if(entitys.size() == 0){
				outputObject.setreturnMessage("请选择产品");
				return;
			}
			//单据主表对象
			Map<String, Object> depothead = new HashMap<>();
			depothead.put("id", applyId);
			depothead.put("serviceId", map.get("serviceId"));//工单Id
			depothead.put("applyNum", "SLDH" + ToolUtil.getUniqueKey());//申领单号
			depothead.put("applyTime", map.get("applyTime"));//申领时间
			depothead.put("createTime", DateUtil.getTimeAndToString());//创建时间
			depothead.put("createId", userId);//创建人
			depothead.put("state", "0");//未审核
			depothead.put("remark", map.get("remark"));//备注
			depothead.put("customerId", map.get("customerId"));//客户Id
			depothead.put("enclosureInfo", map.get("enclosureInfo"));//附件
			depothead.put("allPrice", allPrice);//合计金额
			sealSeServiceDao.insertSealSeServiceApplyMation(depothead);
			sealSeServiceDao.insertSealSeServiceApplyMaterial(entitys);
		}else{
			outputObject.setreturnMessage("数据格式错误");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceSignon
	 * @Description: 查询待完工状态的工单用于新增配件下拉选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("receiver", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceSignon(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 *
	 * @Title: querySealSeServiceApplyList
	 * @Description: 查询当前登录人的申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceApplyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("receiver", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceApplyList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceApplyList
	 * @Description: 查询所有审批通过的申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryAllSealSeServiceApplyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.queryAllSealSeServiceApplyList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: deleteSealSeServiceApplyById
	 * @Description: 删除配件申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSealSeServiceApplyById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceApplyState(map);
		if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//0.未审核   2.审核不通过可以进行删除
			sealSeServiceDao.deleteSealSeServiceApplyById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceApplyToEdit
	 * @Description: 获取配件申领单信息用于编辑
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceApplyToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取申领单主表信息
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceApplyToEdit(map);
		//获取配件详细信息放入实体中
		List<Map<String, Object>> materiel = sealSeServiceDao.queryApplyMaterialMationById(map);
		materiel.forEach(item -> {
			//物料规格信息
			List<Map<String, Object>> maUnitList;
			try {
				maUnitList = materialDao.queryMaterialUnitByIdToSelect(item.get("productId").toString());
				if ("1".equals(item.get("unit").toString())) {// 不是多单位
					maUnitList.get(0).put("name", item.get("unitName").toString());
				}
				item.put("unitList", maUnitList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		bean.put("materials", materiel);
		//获取附件信息放入实体中
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editSealSeServiceApplyMation
	 * @Description: 编辑配件申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editSealSeServiceApplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceApplyState(map);
		if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//0.未审核   2.审核不通过可以进行编辑
			String applyMaterialStr = map.get("applyMaterialStr").toString();//申领配件
			if(ToolUtil.isJson(applyMaterialStr)){
				String userId = inputObject.getLogParams().get("id").toString();
				String applyId = map.get("id").toString();//申领主表id
				//处理数据
				List<Map<String, Object>> jArray = JSONUtil.toList(applyMaterialStr, null);
				//产品中间转换对象，单据子表存储对象
				Map<String, Object> entity, currentTock;
				List<Map<String, Object>> entitys = new ArrayList<>();//单据子表实体集合信息
				BigDecimal allPrice = new BigDecimal("0");//主单总价
				BigDecimal itemAllPrice = null;//子单对象
				for(int i = 0; i < jArray.size(); i++){
					bean = jArray.get(i);
					entity = sealSeServiceDao.queryMaterialsById(bean);//查询配件详细信息
					currentTock = sealSeServiceDao.queryMaterialsCurrentTockById(bean);//查询配件当前库存
					if(Integer.parseInt(currentTock.get("currentTock").toString()) < Integer.parseInt(bean.get("rkNum").toString())){
						outputObject.setreturnMessage(entity.get("materialName").toString() + "库存不足，请重新选择配件数据！");
						return;
					}
					if(entity != null && !entity.isEmpty()){
						//获取单价
						itemAllPrice = new BigDecimal(entity.get("unitPrice").toString());
						entity.put("id", ToolUtil.getSurFaceId());
						entity.put("serviceId", map.get("serviceId"));//工单Id
						entity.put("applyId", applyId);//单据主表id
						entity.put("operNumber", bean.get("rkNum"));//数量
						//计算子单总价：单价*数量
						itemAllPrice = itemAllPrice.multiply(new BigDecimal(bean.get("rkNum").toString()));
						entity.put("allPrice", itemAllPrice);//单据子表总价
						entity.put("remark", bean.get("remark"));//备注
						entity.put("depotId", bean.get("depotId"));//仓库
						entity.put("createId", userId);//创建人
						entity.put("createTime", DateUtil.getTimeAndToString());//创建时间
						entitys.add(entity);
						//计算主单总价
						allPrice = allPrice.add(itemAllPrice);
					}
				}
				if(entitys.size() == 0){
					outputObject.setreturnMessage("请选择产品");
					return;
				}
				//单据主表对象
				Map<String, Object> depothead = new HashMap<>();
				depothead.put("id", applyId);
 				depothead.put("applyTime", map.get("applyTime"));//申领时间
				depothead.put("state", "0");//未审核
				depothead.put("remark", map.get("remark"));//备注
				depothead.put("customerId", map.get("customerId"));//客户Id
				depothead.put("enclosureInfo", map.get("enclosureInfo"));//附件
				depothead.put("allPrice", allPrice);//合计金额
				sealSeServiceDao.editSealSeServiceApplyMation(depothead);
				sealSeServiceDao.deleteSealSeServiceApplyMaterial(depothead);
				sealSeServiceDao.insertSealSeServiceApplyMaterial(entitys);
			}else{
				outputObject.setreturnMessage("数据格式错误");
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceApplyToDetail
	 * @Description: 获取配件申领单详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceApplyToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		//获取申领单主表信息
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceApplyToDetail(map);
		if(bean != null && !bean.isEmpty()){
			//获取配件详细信息放入实体中
			bean.put("materials", sealSeServiceDao.queryApplyMaterialMationToDetail(map));
			//获取附件信息放入实体中
			bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该信息已不存在！");
		}
	}
	
	/**
	 *
	 * @Title: queryAllSealSeServiceApplyWaitToCheckList
	 * @Description: 查询所有待审核的申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryAllSealSeServiceApplyWaitToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.queryAllSealSeServiceApplyWaitToCheckList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 *
	 * @Title: editSealSeServiceApplyToCheckList
	 * @Description: 审核申领单
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSealSeServiceApplyToCheckList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceApplyState(map);
		if("0".equals(bean.get("state").toString())){//0.未审核可以进行审核
			if("1".equals(map.get("isAgree"))){//1为审核通过
				//申领单id
				String applyId = map.get("id").toString();
				//获取配件申领数量、库存等信息
				List<Map<String, Object>> beans = sealSeServiceDao.queryApplyMaterialOtherMationById(map);
				List<Map<String, Object>> entitys = new ArrayList<>();//单据子表实体集合信息
				BigDecimal allPrice = new BigDecimal("0");//主单总价
				BigDecimal itemAllPrice = null;//子单对象
				for(int i = 0; i < beans.size(); i++){
					bean = beans.get(i);//产品中间转换对象，单据子表存储对象
					if(Integer.parseInt(bean.get("currentTock").toString()) < Integer.parseInt(bean.get("operNum").toString())){
						bean.put("operNum",bean.get("currentTock"));
					}
					//获取单价
					itemAllPrice = new BigDecimal(bean.get("unitPrice").toString());
					//计算子单总价：单价*数量
					itemAllPrice = itemAllPrice.multiply(new BigDecimal(bean.get("operNum").toString()));
					bean.put("allPrice", itemAllPrice);//单据子表总价
					entitys.add(bean);
					//计算主单总价
					allPrice = allPrice.add(itemAllPrice);
				}
				//单据主表对象
				Map<String, Object> depothead = new HashMap<>();
				depothead.put("id", applyId);
				depothead.put("state", "1");//审核通过
				depothead.put("allPrice", allPrice);//合计金额
				depothead.put("opinion", map.get("opinion"));//审核意见
				depothead.put("approvalId", inputObject.getLogParams().get("id"));//审批人
				sealSeServiceDao.editSealSeServiceApplyCheckMation(depothead);
				sealSeServiceDao.editSealSeServiceCheckMation(entitys);
				
				//复制配件申请信息到erp单据主表中
				//订单id
				String headId = ToolUtil.getSurFaceId();
				String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_OUT_OTHERS_TYPE);
				sealSeServiceDao.insertErpDepotHeadByApplyId(applyId, headId, orderNum);
				//复制配件申请信息到erp单据子表中
				entitys = sealSeServiceDao.queryErpDepotItemMaterialMationByApplyId(applyId);
				for(Map<String, Object> entity: entitys){
					entity.put("id", ToolUtil.getSurFaceId());
					entity.put("headId", headId);
				}
				sealSeServiceDao.insertErpDepotItems(entitys);
			}else{//2为审核不通过
				//单据主表对象
				Map<String, Object> depothead = new HashMap<>();
				depothead.put("id", map.get("id"));
				depothead.put("state", "2");//审核不通过
				depothead.put("opinion", map.get("opinion"));//审核意见
				depothead.put("approvalId", inputObject.getLogParams().get("id"));//审批人
				sealSeServiceDao.editSealSeServiceApplyCheckMation(depothead);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceWaitToFinishedMation
	 * @Description: 工单完成操作时信息回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceWaitToFinishedMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceWaitToFinishedMation(map);
		// 集合中放入附件信息
		bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
		// 查询已经存在的配件使用明细
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> materiel = sealSeServiceDao.querySealSeServiceUseMaterialByServiceId(map);
		materiel.forEach(item -> {
			//物料规格信息
			List<Map<String, Object>> maUnitList;
			try {
				maUnitList = materialDao.queryMaterialUnitByIdToSelect(item.get("productId").toString());
				if ("1".equals(item.get("unit").toString())) {// 不是多单位
					maUnitList.get(0).put("name", item.get("unitName").toString());
				}
				item.put("unitList", maUnitList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		bean.put("partsList", materiel);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: queryMyPartsNumByMUnitId
	 * @Description: 根据配件规格id获取库存
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void queryMyPartsNumByMUnitId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Map<String, Object> bean = sealSeServiceDao.queryMyPartsNumByMUnitId(map);
        outputObject.setBean(bean);
	}

	/**
	 *
	 * @Title: editServiceToComplateByServiceId
	 * @Description: 工单完工操作
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editServiceToComplateByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if("4".equals(bean.get("state").toString())){//待完工状态下可以完工
			String useStr = map.get("useStr").toString();//申领配件
			//工单id
			String serviceId = map.get("id").toString();
			//用户id
			String userId = inputObject.getLogParams().get("id").toString();
			BigDecimal allPrice = new BigDecimal("0");//主单总价
			List<Map<String, Object>> entitys = new ArrayList<>();//单据子表实体集合信息
			if(!ToolUtil.isBlank(useStr) && ToolUtil.isJson(useStr)){
				//处理数据
				List<Map<String, Object>> jArray = JSONUtil.toList(useStr, null);
				//产品中间转换对象，单据子表存储对象
				Map<String, Object> entity, currentTock;
				BigDecimal itemAllPrice = null;//子单对象
				for(int i = 0; i < jArray.size(); i++){
					bean = jArray.get(i);
					//查询配件详细信息
					entity = sealSeServiceDao.queryMaterialsById(bean);
					if(entity != null && !entity.isEmpty()){
						//查询配件当前库存
						bean.put("userId", userId);
						bean.put("serviceId", serviceId);
						currentTock = sealSeServiceDao.queryMyPartsNumByMUnitId(bean);
						if(Integer.parseInt(currentTock.get("currentTock").toString()) < Integer.parseInt(bean.get("rkNum").toString())){
							outputObject.setreturnMessage(entity.get("materialName").toString() + "库存不足，请重新选择配件！");
							return;
						}
						//获取单价
						itemAllPrice = new BigDecimal(entity.get("unitPrice").toString());
						entity.put("id", ToolUtil.getSurFaceId());
						entity.put("serviceId", serviceId);//工单id
						entity.put("operNumber", bean.get("rkNum"));//数量
						//计算子单总价：单价*数量
						itemAllPrice = itemAllPrice.multiply(new BigDecimal(bean.get("rkNum").toString()));
						entity.put("allPrice", itemAllPrice);//单据子表总价
						entity.put("remark", bean.get("remark"));//备注
						entity.put("createId", userId);//创建人
						entity.put("createTime", DateUtil.getTimeAndToString());//创建时间
						entitys.add(entity);
						//计算材料费总价
						allPrice = allPrice.add(itemAllPrice);
					}
				}
			}else{
				outputObject.setreturnMessage("数据格式错误");
			}
			
			//判断该工单在数据库中是否存在完工信息
			Map<String, Object> faultMation = sealSeServiceDao.queryFaultMationByOrderId(serviceId);
			String faultId = "";
			if(faultMation != null && faultMation.containsKey("id") && !ToolUtil.isBlank(faultMation.get("id").toString())){
				faultId = faultMation.get("id").toString();
			}else{
				faultId = ToolUtil.getSurFaceId();
			}
			
			//故障表对象
			Map<String, Object> fault = new HashMap<>();
			fault.put("id", faultId);
			fault.put("serviceId", serviceId);//工单Id
			fault.put("typeId", map.get("faultTypeId"));//故障类型
			fault.put("comExecution", map.get("comExecution"));//完成情况
			fault.put("comPic", map.get("comPic"));//完工拍照
			fault.put("comEnclosureInfo", map.get("enclosureInfo"));//完工附件
			fault.put("comRemark", map.get("comRemark"));//完工备注
			fault.put("comTime", map.get("comTime"));//实际完工时间
			fault.put("comWorkTime", map.get("comWorkTime"));//工时
			fault.put("materialCost", allPrice);//材料费
			fault.put("coverCost", map.get("coverCost"));//服务费
			fault.put("otherCost", map.get("otherCost"));//其他费
			
			fault.put("comStarTime", map.get("comStarTime"));//实际开工时间
			fault.put("faultKeyPartsId", map.get("faultKeyPartsId"));//故障关键组件
			fault.put("actualFailure", map.get("actualFailure"));//真实故障
			fault.put("solution", map.get("solution"));//解决方案
			//加上服务费用和其他费用
			allPrice = allPrice.add(new BigDecimal(map.get("coverCost").toString()));
			allPrice = allPrice.add(new BigDecimal(map.get("otherCost").toString()));
			fault.put("allPrice", allPrice);//合计金额
			fault.put("createId", userId);//创建人
			fault.put("createTime", DateUtil.getTimeAndToString());//创建时间
			//如果故障id为空，则新增，否则修改
			if(faultMation != null && faultMation.containsKey("id") && !ToolUtil.isBlank(faultMation.get("id").toString())){
				sealSeServiceDao.editSealSeServiceFaultMationById(fault);
			}else{
				sealSeServiceDao.insertSealSeServiceFaultMation(fault);
			}
			//删除之前的配件使用明细
			sealSeServiceDao.deleteSealSeServiceUseMaterialByServiceId(serviceId);
			//插入配件使用明细
			if(!entitys.isEmpty()) {
				sealSeServiceDao.insertSealSeServiceUseMaterial(entitys);
			}
			//完工操作，修改工单状态
			if("2".equals(map.get("subType").toString())){
				sealSeServiceDao.editSealSeServiceToComplateMation(serviceId);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceWaitToEvaluateMation
	 * @Description: 评价时获取展示信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceWaitToEvaluateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceWaitToEvaluateMation(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: editSealSeServiceToEvaluateMationByServiceId
	 * @Description: 人工评价（后台用户评价）
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSealSeServiceToEvaluateMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if("5".equals(bean.get("state").toString())){//待评价可以进行评价
			//修改工单状态
			sealSeServiceDao.editSealSeServiceToEvaluateMationByServiceId(map);
			//插入评价信息
			Map<String, Object> entity = new HashMap<>();
			entity.put("id", ToolUtil.getSurFaceId());
			entity.put("serviceId", map.get("id"));
			entity.put("typeId", map.get("typeId"));
			entity.put("content", map.get("content"));
			entity.put("type", 2);
			entity.put("createId", inputObject.getLogParams().get("id"));
			entity.put("createTime", DateUtil.getTimeAndToString());
			sealSeServiceDao.insertEvaluateMation(entity);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: editSealSeServiceToFinishedMationByServiceId
	 * @Description: 完工审核操作
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void editSealSeServiceToFinishedMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceState(map);
		if("6".equals(bean.get("state").toString())){//待审核状态可以进行审核完工
			//修改工单状态
			sealSeServiceDao.editSealSeServiceToFinishedMationByServiceId(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 *
	 * @Title: querySealSeServiceList
	 * @Description: 情况反馈信息展示
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceFeedBackMationByServiceId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sealSeServiceDao.querySealSeServiceFeedBackMationByServiceId(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 *
	 * @Title: querySealSeServiceMyWriteList
	 * @Description: 获取当前登录人填报的工单列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@Override
	public void querySealSeServiceMyWriteList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("createId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sealSeServiceDao.querySealSeServiceMyWriteList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
