/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.VehicleApplyUseDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VehicleUseFactory
 * @Description: 用车申请工程类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 18:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class VehicleUseFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private VehicleApplyUseDao vehicleApplyUseDao;

    public VehicleUseFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public VehicleUseFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.vehicleApplyUseDao = SpringUtils.getBean(VehicleApplyUseDao.class);
    }

    /**
     * 获取和工作流相关操作的列表时获取数据
     *
     * @param inputParams 前台传递的入参
     * @return 结果
     * @throws Exception
     */
    @Override
    protected List<Map<String, Object>> queryWithActivitiListRunSql(Map<String, Object> inputParams) throws Exception {
        return vehicleApplyUseDao.queryMyUseVehicleMation(inputParams);
    }

    /**
     * 提交数据到工作流时获取数据
     *
     * @param id 需要提交到工作流的主单据id
     * @return
     * @throws Exception
     */
    @Override
    protected Map<String, Object> submitToActiviGetDate(String id) throws Exception {
        Map<String, Object> vehicleUseMation = vehicleApplyUseDao.queryVehicleUseMationShowById(id);
        // 获取附件信息
        vehicleUseMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(vehicleUseMation.get("enclosureInfo").toString()));
        return vehicleUseMation;
    }

    /**
     * 提交数据到工作流后成功的回调函数
     *
     * @param id          需要提交到工作流的主单据id
     * @param processInId 提交成功后的流程实例id
     * @throws Exception
     */
    @Override
    protected void submitToActiviSuccess(String id, String processInId) throws Exception {
        // 用车申请主表单状态修改为审核中
        vehicleApplyUseDao.updateVehicleUseStateISInAudit(id, processInId);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        // 撤销用车申请为撤销状态
        vehicleApplyUseDao.updateVehicleUseToRevoke(map);
    }
}
