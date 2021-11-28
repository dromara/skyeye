/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.IfsCommonDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        return null;
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

    }

    /**
     * 删除订单数据,操作其他数据
     *
     * @param orderMation 订单信息
     * @throws Exception
     */
    protected void deleteOrderOtherMationById(Map<String, Object> orderMation) throws Exception{

    }

}
