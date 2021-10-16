/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.SealApplyRevertDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealRevertFactory
 * @Description: 印章归还申请工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SealRevertFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private SealApplyRevertDao sealApplyRevertDao;

    public SealRevertFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public SealRevertFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.sealApplyRevertDao = SpringUtils.getBean(SealApplyRevertDao.class);
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
        return sealApplyRevertDao.selectMySealRevertList(inputParams);
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
        Map<String, Object> sealRevert = sealApplyRevertDao.querySealRevertMationById(id);
        // 获取印章信息
        List<Map<String, Object>> goods = sealApplyRevertDao.querySealMationListByRevertId(id);
        sealRevert.put("goods", goods);
        // 获取附件信息
        sealRevert.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(sealRevert.get("enclosureInfo").toString()));
        return sealRevert;
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
        // 修改为审核中
        sealApplyRevertDao.updateSealRevertStateISInAudit(id, processInId);
        // 归还单状态修改为等待发放
        sealApplyRevertDao.updateSealRevertGoodsStateByRevertId(id, 1);
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
        map = sealApplyRevertDao.querySealRevertByProcessInstanceId(processInstanceId);
        String revertId = map.get("id").toString();
        // 修改印章归还单状态为撤销
        sealApplyRevertDao.editSealRevertToRevokeById(revertId);
        // 修改印章归还单关联的印章信息为草稿
        sealApplyRevertDao.updateSealRevertGoodsStateByRevertId(revertId, 0);
    }
}
