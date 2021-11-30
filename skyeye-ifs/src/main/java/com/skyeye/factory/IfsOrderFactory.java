/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.IfsCommonDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsOrderFactory
 * @Description: IFS单据工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/28 22:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public abstract class IfsOrderFactory {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected IfsCommonDao ifsCommonDao;

    protected SysEveUserStaffDao sysEveUserStaffDao;

    protected InputObject inputObject;

    protected OutputObject outputObject;

    /**
     * 单据类型
     */
    protected String orderType;

    protected IfsOrderFactory(String orderType) {
        this.orderType = orderType;
    }

    protected IfsOrderFactory(InputObject inputObject, OutputObject outputObject, String orderType) {
        this.orderType = orderType;
        this.inputObject = inputObject;
        this.outputObject = outputObject;
        this.initObject();
    }

    /**
     * 初始化数据
     */
    protected void initObject(){
        ifsCommonDao = SpringUtils.getBean(IfsCommonDao.class);
        sysEveUserStaffDao = SpringUtils.getBean(SysEveUserStaffDao.class);
    }

    /**
     * 获取订单列表
     *
     * @throws Exception
     */
    public List<Map<String, Object>> queryOrderList() throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("orderType", orderType);
        // 获取分页信息
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> result = this.queryOrderListSqlRun(params);
        outputObject.setBeans(result);
        outputObject.settotal(pages.getTotal());
        return result;
    }

    /**
     * 获取订单列表的执行sql
     *
     * @param params 入参
     * @return 订单列表
     */
    protected List<Map<String, Object>> queryOrderListSqlRun(Map<String, Object> params) throws Exception {
        return ifsCommonDao.queryIfsOrderList(params);
    }

    /**
     * 新增订单数据
     *
     * @throws Exception
     */
    public void insertOrderMation() throws Exception {
        Map<String, Object> params = inputObject.getParams();
        // 财务主表ID
        String orderId = ToolUtil.getSurFaceId();
        // 财务子表实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        BigDecimal allPrice = getAllPriceAndChildList(orderId, params.get("initemStr").toString(), entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择支出项目");
            return;
        }
        Map<String, Object> accountHead = new HashMap<>();
        String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(orderType);
        accountHead.put("id", orderId);
        accountHead.put("type", orderType);
        accountHead.put("billNo", orderNum);
        accountHead.put("totalPrice", allPrice);
        accountHead.put("organId", params.get("organId"));
        accountHead.put("operTime", params.get("operTime"));
        accountHead.put("accountId", params.get("accountId"));
        accountHead.put("handsPersonId", params.get("handsPersonId"));
        accountHead.put("remark", params.get("remark"));
        accountHead.put("changeAmount", params.get("changeAmount"));
        accountHead.put("deleteFlag", 0);
        ifsCommonDao.insertIfsOrderMation(accountHead);
        ifsCommonDao.insertIfsOrderItemMation(entitys);
        this.insertOrderOtherMation(params, orderId);
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

    private BigDecimal getAllPriceAndChildList(String orderId, String itemStr, List<Map<String, Object>> entitys) {
        List<Map<String, Object>> jArray = JSONUtil.toList(itemStr, null);
        // 主单总价
        BigDecimal allPrice = new BigDecimal("0");
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = new HashMap<>();
            // 获取子项金额
            BigDecimal itemAllPrice = new BigDecimal(bean.get("initemMoney").toString());
            entity.put("id", ToolUtil.getSurFaceId());
            entity.put("headerId", orderId);
            entity.put("inOutItemId", bean.get("initemId"));
            entity.put("eachAmount", bean.get("initemMoney"));
            entity.put("remark", bean.get("remark"));
            entity.put("deleteFlag", 0);
            entitys.add(entity);
            // 计算总金额
            allPrice = allPrice.add(itemAllPrice);
        }
        return allPrice;
    }

    /**
     * 编辑时获取订单数据进行回显
     *
     * @return 订单数据
     * @throws Exception
     */
    public Map<String, Object> queryOrderMationToEditById() throws Exception{
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("id").toString();
        Map<String, Object> bean = ifsCommonDao.queryIfsOrderMationToEditById(orderId);
        if(bean != null && !bean.isEmpty()){
            List<Map<String, Object>> beans = ifsCommonDao.queryIfsOrderItemMationToEditById(orderId);
            bean.put("items", beans);
            // 获取经手人员
            bean.put("userInfo", sysEveUserStaffDao.queryUserNameList(bean.get("handsPersonId").toString()));
            this.quertOrderOtherMationToEditById(bean, orderId);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }else{
            outputObject.setreturnMessage("未查询到信息！");
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
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("id").toString();
        // 财务子表实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        BigDecimal allPrice = getAllPriceAndChildList(orderId, params.get("initemStr").toString(), entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择支出项目");
            return;
        }
        Map<String, Object> accountHead = new HashMap<>();
        accountHead.put("id", orderId);
        accountHead.put("totalPrice", allPrice);
        accountHead.put("organId", params.get("organId"));
        accountHead.put("operTime", params.get("operTime"));
        accountHead.put("accountId", params.get("accountId"));
        accountHead.put("handsPersonId", params.get("handsPersonId"));
        accountHead.put("remark", params.get("remark"));
        accountHead.put("changeAmount", params.get("changeAmount"));
        ifsCommonDao.editIfsOrderMation(accountHead);
        // 删除之前的绑定信息
        ifsCommonDao.deleteIfsOrderItemMationByOrderId(orderId);
        ifsCommonDao.insertIfsOrderItemMation(entitys);
        this.editOrderOtherMationById(params, orderId);
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
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("id").toString();
        // 删除订单信息
        ifsCommonDao.deleteIfsOrderMationById(orderId);
        // 删除绑定信息
        ifsCommonDao.deleteIfsOrderItemMationByOrderId(orderId);
        this.deleteOrderOtherMationById(params);
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
     * 获取订单详情
     *
     * @throws Exception
     */
    public Map<String, Object> queryOrderMationDetailsById() throws Exception{
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("id").toString();
        // 获取财务主表信息
        Map<String, Object> bean = ifsCommonDao.queryIfsOrderMationDetailsById(orderId);
        if(bean != null && !bean.isEmpty()){
            // 获取子表信息
            List<Map<String, Object>> beans = ifsCommonDao.queryIfsOrderItemMationDetailsById(orderId);
            bean.put("items", beans);
            this.queryOrderOtherDetailsMationById(bean);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }else{
            outputObject.setreturnMessage("该数据已不存在.");
        }
        return bean;
    }

    /**
     * 获取订单详情时,操作其他数据
     *
     * @param orderMation 订单信息
     * @throws Exception
     */
    protected void queryOrderOtherDetailsMationById(Map<String, Object> orderMation) throws Exception{
    }

    /**
     * 单据提交审核
     *
     * @throws Exception
     */
    public void subOrderToExamineById() throws Exception{

    }

    /**
     * 单据撤销
     *
     * @throws Exception
     */
    public void revokeOrder() throws Exception{

    }

}
