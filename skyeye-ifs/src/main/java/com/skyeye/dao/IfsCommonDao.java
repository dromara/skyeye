/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsCommonDao
 * @Description: 财务模块公共部分
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/28 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsCommonDao {

    /**
     * 根据条件查询单据列表
     *
     * @param params 条件参数
     * @return 单据列表
     * @throws Exception
     */
    List<Map<String, Object>> queryIfsOrderList(Map<String, Object> params) throws Exception;

    /**
     * 插入财务单据信息
     *
     * @param orderMation
     * @throws Exception
     */
    void insertIfsOrderMation(Map<String, Object> orderMation) throws Exception;

    /**
     * 插入财务单据关联信息
     *
     * @param entitys
     * @throws Exception
     */
    void insertIfsOrderItemMation(List<Map<String, Object>> entitys) throws Exception;

    /**
     * 根据订单id获取订单信息用来编辑
     *
     * @param orderId 订单id
     * @return 订单信息
     * @throws Exception
     */
    Map<String, Object> queryIfsOrderMationToEditById(@Param("orderId") String orderId) throws Exception;

    /**
     * 根据订单id获取订单关联信息用来编辑
     *
     * @param orderId 订单id
     * @return 订单关联信息
     * @throws Exception
     */
    List<Map<String, Object>> queryIfsOrderItemMationToEditById(@Param("orderId") String orderId) throws Exception;

    /**
     * 修改财务单据信息
     *
     * @param orderMation
     * @throws Exception
     */
    void editIfsOrderMation(Map<String, Object> orderMation) throws Exception;

    /**
     * 插入财务订单相关单据信息
     *
     * @param orderId 订单id
     * @throws Exception
     */
    void deleteIfsOrderItemMationByOrderId(@Param("orderId") String orderId) throws Exception;

    /**
     * 删除单据信息
     *
     * @param orderId 订单id
     * @throws Exception
     */
    void deleteIfsOrderMationById(@Param("orderId") String orderId) throws Exception;

    /**
     * 根据订单id获取订单信息
     *
     * @param orderId 订单id
     * @return 订单信息
     * @throws Exception
     */
    Map<String, Object> queryIfsOrderMationDetailsById(@Param("orderId") String orderId) throws Exception;

    /**
     * 根据订单id获取订单关联信息
     *
     * @param orderId 订单id
     * @return 订单关联信息
     * @throws Exception
     */
    List<Map<String, Object>> queryIfsOrderItemMationDetailsById(@Param("orderId") String orderId) throws Exception;

}
