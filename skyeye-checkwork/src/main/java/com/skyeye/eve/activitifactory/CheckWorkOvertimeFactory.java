/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.activitifactory;

import com.skyeye.activiti.factory.ActivitiFactory;
import com.skyeye.activiti.factory.ActivitiFactoryResult;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.dao.CheckWorkOvertimeDao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkOvertimeFactory
 * @Description: 加班申请工作流工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 13:41
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CheckWorkOvertimeFactory extends ActivitiFactory implements ActivitiFactoryResult {

    private CheckWorkOvertimeDao checkWorkOvertimeDao;

    public CheckWorkOvertimeFactory(InputObject inputObject, OutputObject outputObject, String key) {
        super(inputObject, outputObject, key);
    }

    public CheckWorkOvertimeFactory(String key) {
        super(key);
    }

    @Override
    protected void initChildObject() {
        this.checkWorkOvertimeDao = SpringUtils.getBean(CheckWorkOvertimeDao.class);
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
        return checkWorkOvertimeDao.queryMyCheckWorkOvertimeList(inputParams);
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
        Map<String, Object> checkWorkOvertimeMation = checkWorkOvertimeDao.queryCheckWorkOvertimeDetailById(id);
        // 1.获取加班日期信息
        List<Map<String, Object>> overtimeDay = checkWorkOvertimeDao.queryCheckWorkOvertimeDaysDetailByOvertimeId(id);
        checkWorkOvertimeMation.put("overtimeDay", overtimeDay);
        // 2.获取附件信息
        checkWorkOvertimeMation.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(checkWorkOvertimeMation.get("enclosureInfo").toString()));
        return checkWorkOvertimeMation;
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
        checkWorkOvertimeDao.updateCheckWorkOvertimeStateAndProcessInId(id, processInId, 1);
        checkWorkOvertimeDao.updateCheckWorkOvertimeDaysStateByOvertimeId(id, 1);
    }

    /**
     * 撤销工作流成功后会执行的回调
     *
     * @param map 入参
     * @throws Exception
     */
    @Override
    protected void revokeActiviSuccess(Map<String, Object> map) throws Exception {
        map = checkWorkOvertimeDao.queryCheckWorkOvertimeByProcessInstanceId(map.get("processInstanceId").toString());
        String overtimeId = map.get("id").toString();
        checkWorkOvertimeDao.updateCheckWorkOvertimeStateById(overtimeId, 5);
        checkWorkOvertimeDao.updateCheckWorkOvertimeDaysStateByOvertimeId(overtimeId, 0);
    }
}
