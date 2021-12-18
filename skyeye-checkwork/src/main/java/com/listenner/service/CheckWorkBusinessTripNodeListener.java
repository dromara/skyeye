/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.listenner.service;

import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.CheckWorkBusinessTripDao;
import lombok.SneakyThrows;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkBusinessTripNodeListener
 * @Description: 出差申请监听
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/7 21:23
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class CheckWorkBusinessTripNodeListener implements JavaDelegate {

    /**
     * 值为pass，则通过，为nopass，则不通过
     */
    private Expression state;

    /**
     * 出差申请关联的工作流的key
     */
    private static final String CHECK_WORK_BUSINESS_TRIP_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_BUSINESS_TRIP.getKey();

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {
        CheckWorkBusinessTripDao checkWorkBusinessTripDao = SpringUtils.getBean(CheckWorkBusinessTripDao.class);
        String processInstanceId = execution.getProcessInstanceId();//流程实例id
        Map<String, Object> mation = checkWorkBusinessTripDao.queryCheckWorkBusinessTripByProcessInstanceId(processInstanceId);
        String businessTripId = mation.get("id").toString();
        String createId = mation.get("createId").toString();
        // 服务任务状态值
        String value1 = (String) state.getValue(execution);
        // 修改状态
        if("pass".equalsIgnoreCase(value1)){
            // 通过
            checkWorkBusinessTripDao.updateCheckWorkBusinessTripStateById(businessTripId, 2);
            calcUserStaffBusinessTripDaysMation(businessTripId, createId);
        }else{
            checkWorkBusinessTripDao.updateCheckWorkBusinessTripStateById(businessTripId, 3);
            checkWorkBusinessTripDao.updateCheckWorkBusinessTripDaysStateByBusinessTripId(businessTripId, 3);
        }
        // 编辑流程表参数
        ActivitiRunFactory.run(CHECK_WORK_BUSINESS_TRIP_KEY).editApplyMationInActiviti(businessTripId);
    }

    /**
     * 校验该单据中的天数是否符合规则
     *
     * @param businessTripId 出差申请单id
     * @param createId 创建人
     */
    private void calcUserStaffBusinessTripDaysMation(String businessTripId, String createId) throws Exception {
        CheckWorkBusinessTripDao checkWorkBusinessTripDao = SpringUtils.getBean(CheckWorkBusinessTripDao.class);
        // 获取出差天数信息
        List<Map<String, Object>> businessTripDay = checkWorkBusinessTripDao.queryCheckWorkBusinessTripDaysDetailByBusinessTripId(businessTripId);
        for(Map<String, Object> day: businessTripDay){
            String businessTripTimeId = day.get("id").toString();
            String timeId = day.get("timeId").toString();
            String businessTravelDay = day.get("businessTravelDay").toString();
            List<Map<String, Object>> inData = checkWorkBusinessTripDao.queryPassThisTimeAndDayAndCreateId(createId, timeId, businessTravelDay);
            if(inData == null || inData.isEmpty()){
                // 如果指定天的指定班次还没有审批通过的记录，则审批通过
                checkWorkBusinessTripDao.updateCheckWorkBusinessTripSoltStateByBusinessTripTimeId(businessTripTimeId, 2);
            }else{
                checkWorkBusinessTripDao.updateCheckWorkBusinessTripSoltStateByBusinessTripTimeId(businessTripTimeId, 3);
            }
        }
    }

}
