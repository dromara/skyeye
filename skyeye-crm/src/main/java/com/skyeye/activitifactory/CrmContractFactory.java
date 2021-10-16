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
import com.skyeye.dao.CrmContractDao;
import com.skyeye.exception.CustomException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CrmContractFactory
 * @Description: 项目合同申请工作流工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 15:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CrmContractFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private CrmContractDao crmContractDao;

    public CrmContractFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public CrmContractFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.crmContractDao = SpringUtils.getBean(CrmContractDao.class);
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
        Map<String, Object> crmContractMation = crmContractDao.queryCrmContractMationToDetail(id);
        // 获取附件信息
        if(!ToolUtil.isBlank(crmContractMation.get("enclosureInfo").toString())){
            List<Map<String, Object>> beans = sysEnclosureDao.queryEnclosureInfo(crmContractMation.get("enclosureInfo").toString());
            crmContractMation.put("enclosureInfo", beans);
        }
        return crmContractMation;
    }

    @Override
    protected void judgeSubmitActiviti(Map<String, Object> bean) throws Exception {
        if(bean != null && !bean.isEmpty()){
            int state = Integer.parseInt(bean.get("state").toString());
            if(0 == state || 12 == state || 4 == state){
                // 草稿，审核不通过，撤销的可以提交审批
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
        Map<String, Object> crmContractMation = crmContractDao.queryCrmContractMationToDetail(id);
        // 将合同表状态修改为审核中
        crmContractDao.updateCrmContractStateISInAudit(id);
        // 将合同关联表中该合同的数据删除
        crmContractDao.deleteCrmContractRelationByContractId(id);
        Map<String, Object> map = new HashMap<>();
        map.put("contractId", id);//合同id
        map.put("state", 0);//审核中
        map.put("type", 1);//执行前审核
        map.put("subId", crmContractMation.get("createId"));
        map.put("processInId", processInId);
        map.put("subTime", DateUtil.getTimeAndToString());
        map.put("id", ToolUtil.getSurFaceId());
        // 新增合同关联表信息
        crmContractDao.insertCrmContractRelationMation(map);
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
        map = crmContractDao.queryCrmContractRelationByProcessInstanceId(processInstanceId);
        String contractId = map.get("contractId").toString();
        // 修改状态为撤销
        crmContractDao.editCrmContractStateToRevokeById(contractId);
        // 删除关联表中的信息
        crmContractDao.deleteCrmContractRelationByContractId(contractId);
    }
}
