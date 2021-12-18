/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.annotation.transaction.ActivitiAndBaseTransaction;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CheckWorkConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CheckWorkOvertimeDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.CheckWorkOvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkOvertimeServiceImpl
 * @Description: 加班申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/8 22:16
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class CheckWorkOvertimeServiceImpl implements CheckWorkOvertimeService {

    @Autowired
    private CheckWorkOvertimeDao checkWorkOvertimeDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    @Autowired
    private ActivitiUserService activitiUserService;

    /**
     * 加班申请关联的工作流的key
     */
    private static final String CHECK_WORK_OVERTIME_PAGE_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_OVERTIME_PAGE.getKey();

    /**
     * 获取我的加班申请列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMyCheckWorkOvertimeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_OVERTIME_PAGE_KEY).queryWithActivitiList();
    }

    /**
     * 新增加班申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void insertCheckWorkOvertimeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 加班申请id
        String overtimeId = ToolUtil.getSurFaceId();
        // 处理加班天数
        List<Map<String, Object>> beans = getOverTimeDays(map.get("overtimeDayStr").toString(), overtimeId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写加班日期.");
            return;
        }
        map.put("id", overtimeId);
        map.put("oddNumber", Constants.getCheckWorkOvertimeOddNumber());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        checkWorkOvertimeDao.insertCheckWorkOvertimeMation(map);
        checkWorkOvertimeDao.insertCheckWorkOvertimeDaysMation(beans);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            CHECK_WORK_OVERTIME_PAGE_KEY, overtimeId, map.get("approvalId").toString());
    }

    private List<Map<String, Object>> getOverTimeDays(String overtimeDayStr, String overtimeId) {
        List<Map<String, Object>> jArray = JSONUtil.toList(overtimeDayStr, null);
        List<Map<String, Object>> beans = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("overtimeId", overtimeId);
            beans.add(bean);
        }
        return beans;
    }

    /**
     * 加班申请编辑时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkOvertimeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkOvertimeDao.queryCheckWorkOvertimeToEditById(id);
        // 获取加班日期信息
        List<Map<String, Object>> overtimeDay = checkWorkOvertimeDao.queryCheckWorkOvertimeDaysMationToEditByOvertimeId(id);
        bean.put("overtimeDay", overtimeDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 编辑加班申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void updateCheckWorkOvertimeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 加班申请id
        String overtimeId = map.get("id").toString();
        // 处理加班天数
        List<Map<String, Object>> beans = getOverTimeDays(map.get("overtimeDayStr").toString(), overtimeId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写加班日期.");
            return;
        }
        checkWorkOvertimeDao.updateCheckWorkOvertimeMation(map);
        checkWorkOvertimeDao.deleteCheckWorkOvertimeDaysMationByOvertimeId(overtimeId);
        checkWorkOvertimeDao.insertCheckWorkOvertimeDaysMation(beans);
        // 操作工作流数据
        activitiUserService.addOrEditToSubmit(inputObject, outputObject, Integer.parseInt(map.get("subType").toString()),
            CHECK_WORK_OVERTIME_PAGE_KEY, overtimeId, map.get("approvalId").toString());
    }

    /**
     * 加班申请详情
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkOvertimeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkOvertimeDao.queryCheckWorkOvertimeDetailById(id);
        // 获取加班日期信息
        List<Map<String, Object>> overtimeDay = checkWorkOvertimeDao.queryCheckWorkOvertimeDaysDetailByOvertimeId(id);
        bean.put("overtimeDay", overtimeDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 提交审批加班申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editCheckWorkOvertimeToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String overtimeId = map.get("id").toString();
        Map<String, Object> bean = checkWorkOvertimeDao.queryCheckWorkOvertimeDetailById(overtimeId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            // 操作工作流数据
            activitiUserService.addOrEditToSubmit(inputObject, outputObject, 2,
                CHECK_WORK_OVERTIME_PAGE_KEY, overtimeId, map.get("approvalId").toString());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 作废加班申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateCheckWorkOvertimeToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String overtimeId = map.get("id").toString();
        Map<String, Object> bean = checkWorkOvertimeDao.queryCheckWorkOvertimeDetailById(overtimeId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            checkWorkOvertimeDao.updateCheckWorkOvertimeStateById(overtimeId, 4);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 撤销加班申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @ActivitiAndBaseTransaction(value = {"transactionManager"})
    public void editCheckWorkOvertimeToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_OVERTIME_PAGE_KEY).revokeActivi();
    }

    /**
     * 获取指定员工在指定月份的所有审核通过的加班申请数据
     *
     * @param userId 用户id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(String userId, List<String> months) throws Exception {
        List<Map<String, Object>> beans = checkWorkOvertimeDao.queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(userId, months);
        beans.forEach(bean -> {
            bean.put("type", CheckWorkConstants.CheckDayType.DAY_IS_WORK_OVERTIME.getType());
            bean.put("className", CheckWorkConstants.CheckDayType.DAY_IS_WORK_OVERTIME.getClassName());
            bean.put("allDay", "1");
            bean.put("showBg", "2");
            bean.put("editable", false);
        });
        return beans;
    }
}
