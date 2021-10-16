/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.listenner;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.service.StoreHouseApprovalService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * @ClassName: PurchasePutNodeListenner
 * @Description: 其他入库单监听类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/20 21:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class OtherWareHousNodeListenner implements JavaDelegate {

    /**
     * 值为pass，则通过，为nopass，则不通过
     */
    private Expression state;

    /**
     * 其他入库单类型
     */
    private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.PUT_IS_OTHERS.getType();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        ErpCommonDao erpCommonDao = SpringUtils.getBean(ErpCommonDao.class);
        // 流程实例id
        String processInstanceId = execution.getProcessInstanceId();
        Map<String, Object> orderMation = erpCommonDao.queryOrderMationByProcessInstanceId(processInstanceId);
        String orderId = orderMation.get("id").toString();
        // 服务任务状态值
        String approvalResult = (String) state.getValue(execution);
        // 修改库存信息以及单据信息
        StoreHouseApprovalService storeHouseApprovalService = SpringUtils.getBean(StoreHouseApprovalService.class);
        storeHouseApprovalService.approvalOrder(orderId, approvalResult);
        String actiriviKey = ErpConstants.DepoTheadSubType.getActivityKey(ORDER_TYPE);
        // 编辑流程表参数
        ActivitiRunFactory.run(actiriviKey).editApplyMationInActiviti(orderId);
    }
}
