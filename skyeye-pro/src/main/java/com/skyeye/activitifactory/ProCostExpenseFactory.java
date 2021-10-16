/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProCostExpenseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProCostExpenseFactory
 * @Description: 项目费用报销管理工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/11 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProCostExpenseFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private ProCostExpenseDao proCostExpenseDao;

    public ProCostExpenseFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public ProCostExpenseFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.proCostExpenseDao = SpringUtils.getBean(ProCostExpenseDao.class);
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
        Map<String, Object> proCostExpenseMation = proCostExpenseDao.queryProCostExpenseMationById(id);
        // 获取费用用途详细
        proCostExpenseMation.put("purposes", proCostExpenseDao.queryProCostExpensePurpose(id));
        // 获取附件信息
        proCostExpenseMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(proCostExpenseMation.get("enclosureInfo").toString()));
        return proCostExpenseMation;
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
        Map<String, Object> proCostExpenseMation = proCostExpenseDao.queryProCostExpenseMationById(id);
        // 费用报销状态修改为审核中
        proCostExpenseDao.updateProCostExpenseStateISInAudit(id);
        // 若费用报销工作流关联表中已经存在这条数据则删除
        proCostExpenseDao.deleteProCostExpenseProcessById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("expenseId", id);
        map.put("id", ToolUtil.getSurFaceId());
        map.put("processInId", processInId);
        map.put("state", "0");
        map.put("type", "1");
        map.put("subId", proCostExpenseMation.get("createId"));
        map.put("subTime", DateUtil.getTimeAndToString());
        // 新增费用报销工作流关联表
        proCostExpenseDao.insertProCostExpenseProcess(map);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        map = proCostExpenseDao.queryProCostExpenseId(map);
        // 撤销费用报销审批申请为撤销状态
        proCostExpenseDao.editProCostExpenseProcessToRevoke(map);
        // 删除费用报销工作流关联表
        proCostExpenseDao.deleteProCostExpenseProcessToRevoke(map);
    }
}
