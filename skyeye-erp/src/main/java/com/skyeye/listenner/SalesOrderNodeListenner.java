/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.listenner;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.dao.SalesOrderDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PurchaseOrderNodeListenner
 * @Description: 销售订单监听类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/15 21:34
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SalesOrderNodeListenner implements JavaDelegate {

    /**
     * 值为pass，则通过，为nopass，则不通过
     */
    private Expression state;

    /**
     * 销售订单类型
     */
    private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.OUTCHASE_ORDER.getType();

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        ErpCommonDao erpCommonDao = SpringUtils.getBean(ErpCommonDao.class);
        SalesOrderDao salesOrderDao = SpringUtils.getBean(SalesOrderDao.class);
        // 流程实例id
        String processInstanceId = execution.getProcessInstanceId();
        Map<String, Object> orderMation = erpCommonDao.queryOrderMationByProcessInstanceId(processInstanceId);
        String orderId = orderMation.get("id").toString();
        // 服务任务状态值
        String value1 = (String) state.getValue(execution);
        Map<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        map.put("orderType", ORDER_TYPE);
        if("pass".equals(value1)) {
            // 通过
            map.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_PASS);
            map.put("realComplateTime", DateUtil.getTimeAndToString());
        }else{
            map.put("status", ErpConstants.ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS);
        }
        salesOrderDao.editSalesOrderStateToExamineById(map);

        String actiriviKey = ErpConstants.DepoTheadSubType.getActivityKey(ORDER_TYPE);
        // 编辑流程表参数
        ActivitiRunFactory.run(actiriviKey).editApplyMationInActiviti(orderId);
    }

}
