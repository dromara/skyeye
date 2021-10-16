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
import com.skyeye.dao.ProProjectDao;
import com.skyeye.exception.CustomException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProProjectFactory
 * @Description: 项目立项工作流工程类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/5 20:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProProjectFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private ProProjectDao proProjectDao;

    public ProProjectFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public ProProjectFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        proProjectDao = SpringUtils.getBean(ProProjectDao.class);
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
        Map<String, Object> projectMation = proProjectDao.queryProProjectMationToDetail(id);
        // 获取附件信息
        projectMation.put("businessEnclosureInfo", sysEnclosureDao.queryEnclosureInfo(projectMation.get("businessEnclosureInfo").toString()));
        return projectMation;
    }

    @Override
    protected void judgeSubmitActiviti(Map<String, Object> bean) throws Exception {
        if(bean != null && !bean.isEmpty()){
            int state = Integer.parseInt(bean.get("state").toString());
            if(0 == state || 12 == state || 5 == state){
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
        Map<String, Object> projectMation = proProjectDao.queryProProjectMationToDetail(id);
        // 将项目表状态修改为审核中
        proProjectDao.updateProProjectStateISInAudit(id);
        // 将项目关联表中该项目的数据删除
        proProjectDao.deleteProProjectRelationByProId(id);
        Map<String, Object> map = new HashMap<>();
        map.put("proId", id);// 项目id
        map.put("state", 0);// 审核中
        map.put("type", 1);// 执行前审核
        map.put("processInId", processInId);
        map.put("subId", projectMation.get("createId"));
        map.put("subTime", DateUtil.getTimeAndToString());
        map.put("id", ToolUtil.getSurFaceId());
        // 新增项目与工作流流程的关联表信息
        proProjectDao.insertProProjectRelationMation(map);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        map = proProjectDao.queryProjectId(map);
        // 撤销项目审批申请为撤销状态
        proProjectDao.editProjectProcessToRevoke(map);
        // 删除项目工作流关联表
        proProjectDao.deleteProjectProcessToRevoke(map);
    }
}
