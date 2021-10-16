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
import com.skyeye.dao.CrmOpportunityDao;
import com.skyeye.exception.CustomException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CrmOpportunityFactory
 * @Description: 客户商机申请工作流工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 16:50
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CrmOpportunityFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private CrmOpportunityDao crmOpportunityDao;

    public CrmOpportunityFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public CrmOpportunityFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.crmOpportunityDao = SpringUtils.getBean(CrmOpportunityDao.class);
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
        Map<String, Object> crmOpportunityMation = crmOpportunityDao.queryOpportunityMationById(id);
        // 获取附件信息
        crmOpportunityMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(crmOpportunityMation.get("enclosureInfo").toString()));
        return crmOpportunityMation;
    }

    @Override
    protected void judgeSubmitActiviti(Map<String, Object> bean) throws Exception {
        if(bean != null && !bean.isEmpty()){
            int state = Integer.parseInt(bean.get("state").toString());
            if(0 == state || 12 == state){
                // 草稿，审核不通过的可以提交审批
            }else{
                throw new CustomException("该数据状态已改变，请刷新页面");
            }
        }else{
            throw new CustomException("该数据不存在，请刷新页面");
        }
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
        Map<String, Object> crmOpportunityMation = crmOpportunityDao.queryOpportunityMationById(id);
        // 商机状态修改为审核中
        crmOpportunityDao.updateCrmOpportunityStateISInAudit(id);
        // 若商机工作流关联表中已经存在这条数据则删除
        crmOpportunityDao.deleteCrmOpportunityProcessById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("opportunityId", id);
        map.put("id", ToolUtil.getSurFaceId());
        map.put("processInId", processInId);
        map.put("state", "0");
        map.put("type", "1");
        map.put("subId", crmOpportunityMation.get("createId"));
        map.put("subTime", DateUtil.getTimeAndToString());
        // 新增商机工作流关联表
        crmOpportunityDao.insertOpportunityProcess(map);
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
        map = crmOpportunityDao.queryOpportunityIdByProcessInstanceId(processInstanceId);
        String opportunityId = map.get("opportunityId").toString();
        // 撤销商机审批申请为草稿状态
        crmOpportunityDao.editOpportunityProcessToRevoke(opportunityId);
        // 删除商机工作流关联表
        crmOpportunityDao.deleteCrmOpportunityProcessById(opportunityId);
    }
}
