/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.MaterialDao;
import com.skyeye.erp.entity.TransmitObject;
import com.skyeye.service.ErpCommonService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName: ErpOrderFactory
 * @Description: erp单据工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/9 20:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public abstract class ErpOrderFactory {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ErpCommonDao erpCommonDao;

    protected ErpCommonService erpCommonService;

    protected MaterialDao materialDao;

    protected InputObject inputObject;

    protected OutputObject outputObject;

    /**
     * 单据类型
     */
    protected String orderType;

    protected ErpOrderFactory(String orderType) {
        this.orderType = orderType;
    }

    protected ErpOrderFactory(InputObject inputObject, OutputObject outputObject, String orderType) {
        this.orderType = orderType;
        this.inputObject = inputObject;
        this.outputObject = outputObject;
        this.initObject();
    }

    /**
     * 初始化数据
     */
    protected void initObject(){
        this.erpCommonDao = SpringUtils.getBean(ErpCommonDao.class);
        this.erpCommonService = SpringUtils.getBean(ErpCommonService.class);
        this.materialDao = SpringUtils.getBean(MaterialDao.class);
    }

    /**
     * 获取订单列表
     *
     * @throws Exception
     */
    public List<Map<String, Object>> queryOrderList() throws Exception {
        String userId = inputObject.getLogParams().get("id").toString();
        String actiriviKey = ErpConstants.DepoTheadSubType.getActivityKey(orderType);
        // 工作流任务类型
        String taskType = this.getActTaskType(actiriviKey);
        Map<String, Object> params = inputObject.getParams();
        params.put("orderType", orderType);
        // 获取分页信息
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> result = this.queryOrderListSqlRun(params);

        // 获取单据类型是否需要审核  1需要审核；2不需要审核
        Integer needExamine = this.getSubmitTypeByOrderType();
        for(Map<String, Object> bean: result) {
            // 获取单据提交类型  1.走工作流提交  2.直接提交
            Integer submitType = getSubmitType(bean);
            bean.put("needExamine", needExamine);
            if(submitType == 1){
                ActivitiRunFactory.run(inputObject, outputObject, actiriviKey).setDataOtherMation(userId, taskType, bean);
            }
        }
        outputObject.setBeans(result);
        outputObject.settotal(pages.getTotal());
        return result;
    }

    private String getActTaskType(String actiriviKey) throws Exception {
        if(ToolUtil.isBlank(actiriviKey)){
            return StringUtils.EMPTY;
        }
        return ActivitiRunFactory.run(inputObject, outputObject, actiriviKey).getActModelTitle();
    }

    /**
     * 获取单据提交类型
     *
     * @param bean 实体bean
     * @return
     */
    private Integer getSubmitType(Map<String, Object> bean){
        // 单据提交类型  1.走工作流提交  2.直接提交
        if(bean.containsKey("submitType")
                && !ToolUtil.isBlank(bean.get("submitType").toString())){
            return Integer.parseInt(bean.get("submitType").toString());
        }
        // 默认直接提交
        return 1;
    }

    /**
     * 获取订单列表的执行sql
     *
     * @param params 入参
     * @return 订单列表
     */
    protected abstract List<Map<String, Object>> queryOrderListSqlRun(Map<String, Object> params) throws Exception;

    /**
     * 新增订单数据
     *
     * @throws Exception
     */
    public void insertOrderMation() throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 单据主表id
        String orderId = ToolUtil.getSurFaceId();
        // 值传递对象，获取对应的值即可
        TransmitObject object = new TransmitObject();
        // 单据子表实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        erpCommonService.resetChildBillTypeOneMation(map.get("depotheadStr").toString(), orderId, object, entitys);
        if(entitys == null || entitys.isEmpty()){
            outputObject.setreturnMessage("请选择商品");
            return;
        }
        // 单据主表对象
        Map<String, Object> depothead = new HashMap<>();
        erpCommonService.addOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
        depothead.put("id", orderId);
        // 出入库类型
        depothead.put("type", ErpConstants.DepoTheadSubType.getOutInWarehouse(orderType));
        depothead.put("orderType", orderType);
        String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(orderType);
        depothead.put("defaultNumber", orderNum);
        depothead.put("number", orderNum);

        // 如果包含优惠金额，则去计算优惠后的金额
        if(map.containsKey("discountMoney")){
            // 优惠金额
            BigDecimal discountMoney = new BigDecimal(map.get("discountMoney").toString());
            // 优惠后金额 = 主单据价税合计 - 优惠金额
            depothead.put("discountLastMoney", object.getTaxLastMoneyPrice().subtract(discountMoney));
        }
        // 合计金额
        depothead.put("totalPrice", object.getAllPrice());
        logger.info("create purchase order mation, json is: {}", JSONUtil.toJsonStr(depothead));
        erpCommonDao.insertOrderParentMation(depothead);
        erpCommonDao.insertOrderChildMation(entitys);
        this.insertOrderOtherMation(map, orderId);
        this.submitData2Activiti(orderId,
                Integer.parseInt(depothead.get("subType").toString()),
                Integer.parseInt(depothead.get("submitType").toString()));
    }

    /**
     * 提交数据到工作流
     *
     * @param orderId 订单id
     * @param subType 表单提交类型
     * @param submitType 单据提交类型  1.走工作流提交  2.直接提交
     * @throws Exception
     */
    private void submitData2Activiti(String orderId, Integer subType, Integer submitType) throws Exception {
        if(subType == 1){
            // 草稿，什么都不用做
        } else if(subType == 2){
            // 提交
            if(submitType == 1){
                // 工作流提交
                String actiriviKey = ErpConstants.DepoTheadSubType.getActivityKey(orderType);
                ActivitiRunFactory.run(inputObject, outputObject, actiriviKey).submitToActivi(orderId);
            }else if(submitType == 2){
                // 直接提交
                erpCommonDao.editOrderStateById(orderId, orderType, ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS);
                this.subOrderMationSuccessAfter(orderId, ActivitiConstants.APPROVAL_PASS);
            }
        } else if(subType == 3) {
            // 编辑工作流中的参数
            String actiriviKey = ErpConstants.DepoTheadSubType.getActivityKey(orderType);
            ActivitiRunFactory.run(inputObject, outputObject, actiriviKey).editApplyMationInActiviti(orderId);
        }
    }

    /**
     * 单据状态修改为2之后的调用
     *
     * @param orderId 单据id
     * @param approvalResult 审批结果：pass:同意；其他:不同意
     * @throws Exception
     */
    protected void subOrderMationSuccessAfter(String orderId, String approvalResult) throws Exception{

    }

    /**
     * 新增订单时操作其他数据
     *
     * @param inputParams 前台传递的参数
     * @param orderId 订单id
     * @throws Exception
     */
    protected void insertOrderOtherMation(Map<String, Object> inputParams, String orderId) throws Exception{

    }

    /**
     * 编辑时获取订单数据进行回显
     *
     * @return 订单数据
     * @throws Exception
     */
    public Map<String, Object> queryOrderMationToEditById() throws Exception{
        Map<String, Object> map = inputObject.getParams();
        String orderId = map.get("id").toString();
        // 获取单据信息
        Map<String, Object> bean = erpCommonDao.queryOrderMationToEditById(orderId, orderType);
        if(bean != null && !bean.isEmpty()){
            // 根据订单id获取该订单下的所有商品规格信息
            List<Map<String, Object>> norms = erpCommonDao.queryOrderNormsToEditByOrderId(orderId);
            for(Map<String, Object> norm : norms){
                String materialId = norm.get("materialId").toString();
                erpCommonService.resetProductAndUnitToEditShow(norm, materialDao.queryMaterialUnitByIdToSelect(materialId));
            }
            bean.put("norms", norms);
            // 获取其他项目相关费用列表(支出项目，收入项目)
            if(bean.containsKey("otherMoneyList") && !ToolUtil.isBlank(bean.get("otherMoneyList").toString()) && ToolUtil.isJson(bean.get("otherMoneyList").toString())){
                bean.put("otherMoneyList", JSONUtil.toList(bean.get("otherMoneyList").toString(), null));
            }else {
                bean.put("otherMoneyList", new JSONArray());
            }
            this.quertOrderOtherMationToEditById(bean, orderId);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }else{
            String message = String.format(Locale.ROOT, "The order[%s] status has changed or the data does not exist.", orderId);
            logger.warn(message);
            outputObject.setreturnMessage(message);
        }
        return bean;
    }

    /**
     * 编辑时获取订单数据进行回显时，获取其他数据
     *
     * @param bean 单据信息
     * @param orderId 订单id
     * @throws Exception
     */
    protected void quertOrderOtherMationToEditById(Map<String, Object> bean, String orderId) throws Exception{

    }

    /**
     * 编辑订单数据
     *
     * @throws Exception
     */
    public void editOrderMationById() throws Exception{
        Map<String, Object> map = inputObject.getParams();
        // 单据主表id
        String orderId = map.get("id").toString();
        // 值传递对象，获取对应的值即可
        TransmitObject object = new TransmitObject();
        // 单据子表实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        erpCommonService.resetChildBillTypeOneMation(map.get("depotheadStr").toString(), orderId, object, entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择商品");
            return;
        }
        // 单据主表对象
        Map<String, Object> depothead = new HashMap<>();
        erpCommonService.editOrderCreateHaderMation(depothead, map, inputObject.getLogParams());
        depothead.put("id", orderId);
        // 如果包含优惠金额，则去计算优惠后的金额
        if(map.containsKey("discountMoney")){
            // 优惠金额
            BigDecimal discountMoney = new BigDecimal(map.get("discountMoney").toString());
            // 优惠后金额 = 主单据价税合计 - 优惠金额
            depothead.put("discountLastMoney", object.getTaxLastMoneyPrice().subtract(discountMoney));
        }
        // 合计金额
        depothead.put("totalPrice", object.getAllPrice());
        // 单据类型，编辑时需要传递
        depothead.put("orderType", orderType);
        erpCommonDao.editOrderParentMationById(depothead);
        // 删除子单据信息
        erpCommonDao.deleteOrderChildMationById(orderId);
        // 插入新的子单据信息
        erpCommonDao.insertOrderChildMation(entitys);
        this.editOrderOtherMationById(map, orderId);
        this.submitData2Activiti(orderId,
                Integer.parseInt(depothead.get("subType").toString()),
                Integer.parseInt(depothead.get("submitType").toString()));
    }

    /**
     * 编辑订单数据时，进行其他操作
     *
     * @param inputParams 前台传递的入参
     * @param orderId 订单id
     * @throws Exception
     */
    protected void editOrderOtherMationById(Map<String, Object> inputParams, String orderId) throws Exception{

    }

    /**
     * 删除订单数据
     *
     * @throws Exception
     */
    public void deleteOrderMationById() throws Exception{
        Map<String, Object> map = inputObject.getParams();
        String orderId = map.get("id").toString();
        // 获取订单状态
        Map<String, Object> orderMation = erpCommonDao.queryOrderParentStateById(orderId, orderType);
        if(orderMation != null && !orderMation.isEmpty()){
            String status = orderMation.get("status").toString();
            if(ErpConstants.ERP_HEADER_STATUS_IS_NOT_APPROVED.equals(status)
                    || ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS.equals(status)){
                // 未提交审核或者审核拒绝的可以删除
                erpCommonDao.deleteOrderParentMationById(orderId, orderType);
                // 删除子单据信息
                erpCommonDao.deleteOrderChildMationById(orderId);
                this.deleteOrderOtherMationById(orderMation);
            }
        }else{
            String message = String.format(Locale.ROOT, "The order[%s] status has changed or the data does not exist.", orderId);
            logger.warn(message);
            outputObject.setreturnMessage(message);
        }
    }

    /**
     * 删除订单数据,操作其他数据
     *
     * @param orderMation 订单信息
     * @throws Exception
     */
    protected void deleteOrderOtherMationById(Map<String, Object> orderMation) throws Exception{

    }

    /**
     * 单据提交审核
     *
     * @throws Exception
     */
    public void subOrderToExamineById() throws Exception{
        Map<String, Object> map = inputObject.getParams();
        String orderId = map.get("id").toString();
        // 获取单据信息
        Map<String, Object> bean = erpCommonDao.queryDepotHeadDetailsMationById(orderId);
        if(bean != null && !bean.isEmpty()){
            this.submitData2Activiti(orderId, 2,
                    Integer.parseInt(bean.get("submitType").toString()));
        }else{
            String message = String.format(Locale.ROOT, "The order[%s] status has changed or the data does not exist.", orderId);
            logger.warn(message);
            outputObject.setreturnMessage(message);
        }
    }

    /**
     * 单据撤销
     *
     * @throws Exception
     */
    public void revokeOrder() throws Exception {
        String actiriviKey = ErpConstants.DepoTheadSubType.getActivityKey(orderType);
        ActivitiRunFactory.run(inputObject, outputObject, actiriviKey).revokeActivi();
    }

    /**
     * 根据单据类型获取单据提交类型
     *
     * @return 1需要审核；2不需要审核
     * @throws Exception
     */
    public Integer getSubmitTypeByOrderType() throws Exception{
        // 是否开启审核标识：true需要审核；false不需要审核
        Boolean whetherNeedExamine = ErpConstants.DepoTheadSubType.whetherNeedExamineByType(orderType);
        return whetherNeedExamine ? 1 : 2;
    }

}
