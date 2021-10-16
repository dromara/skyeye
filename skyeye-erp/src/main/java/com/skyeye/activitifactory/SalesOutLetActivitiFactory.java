/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.ErpCommonDao;
import com.skyeye.service.ErpCommonService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SalesOutLetActivitiFactory
 * @Description: 销售出库单工作流工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/18 21:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SalesOutLetActivitiFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private ErpCommonService erpCommonService;

    private ErpCommonDao erpCommonDao;

    public SalesOutLetActivitiFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public SalesOutLetActivitiFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.erpCommonService = SpringUtils.getBean(ErpCommonService.class);
        this.erpCommonDao = SpringUtils.getBean(ErpCommonDao.class);
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
        return null;
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
        return erpCommonService.queryDepotHeadMationById(id);
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
        erpCommonDao.editOrderStateToSubActiviti(id, ErpConstants.ERP_HEADER_STATUS_IS_IN_APPROVED, processInId);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        String processInstanceId = map.get("processInstanceId").toString();
        Map<String, Object> orderMation = erpCommonDao.queryOrderMationByProcessInstanceId(processInstanceId);
        erpCommonDao.editOrderStateToSubActiviti(orderMation.get("id").toString(), ErpConstants.ERP_HEADER_STATUS_IS_REVOKE, StringUtils.EMPTY);
    }
}
