/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.factory.ActivitiRunFactory;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CheckWorkConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CheckWorkBusinessTripDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.service.CheckWorkBusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkBusinessTripServiceImpl
 * @Description: 出差申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class CheckWorkBusinessTripServiceImpl implements CheckWorkBusinessTripService {

    @Autowired
    private CheckWorkBusinessTripDao checkWorkBusinessTripDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 出差申请关联的工作流的key
     */
    private static final String CHECK_WORK_BUSINESS_TRIP_KEY = ActivitiConstants.ActivitiObjectType.CHECK_WORK_BUSINESS_TRIP.getKey();

    /**
     * 获取我的出差申请列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMyCheckWorkBusinessTripList(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_BUSINESS_TRIP_KEY).queryWithActivitiList();
    }

    /**
     * 新增出差申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertCheckWorkBusinessTripMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 出差申请id
        String businessTripId = ToolUtil.getSurFaceId();
        // 处理出差天数
        List<Map<String, Object>> beans = getBusinessTripDays(map.get("businessTripDayStr").toString(), businessTripId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写出差日期.");
            return;
        }
        map.put("id", businessTripId);
        map.put("oddNumber", Constants.getCheckWorkBusinessTripOddNumber());
        map.put("state", 0);//状态  默认草稿
        Map<String, Object> user = inputObject.getLogParams();//用户信息
        map.put("createId", user.get("id").toString());
        map.put("createTime", DateUtil.getTimeAndToString());
        checkWorkBusinessTripDao.insertCheckWorkBusinessTripMation(map);
        checkWorkBusinessTripDao.insertCheckWorkBusinessTripDaysMation(beans);
        // 判断是否提交审批
        if("2".equals(map.get("subType").toString())){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_BUSINESS_TRIP_KEY).submitToActivi(businessTripId);
        }
    }

    private List<Map<String, Object>> getBusinessTripDays(String businessTripDayStr, String businessTripId) {
        List<Map<String, Object>> jArray = JSONUtil.toList(businessTripDayStr, null);
        List<Map<String, Object>> beans = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
            Map<String, Object> bean = jArray.get(i);
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("businessTripId", businessTripId);
            beans.add(bean);
        }
        return beans;
    }

    /**
     * 出差申请编辑时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkBusinessTripToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkBusinessTripDao.queryCheckWorkBusinessTripToEditById(id);
        // 获取出差日期信息
        List<Map<String, Object>> businessTripDay = checkWorkBusinessTripDao.queryCheckWorkBusinessTripDaysMationToEditByBusinessTripId(id);
        bean.put("businessTripDay", businessTripDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 编辑出差申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateCheckWorkBusinessTripById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 出差申请id
        String businessTripId = map.get("id").toString();
        // 处理出差天数
        List<Map<String, Object>> beans = getBusinessTripDays(map.get("businessTripDayStr").toString(), businessTripId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写出差日期.");
            return;
        }
        checkWorkBusinessTripDao.updateCheckWorkBusinessTripMation(map);
        checkWorkBusinessTripDao.deleteCheckWorkBusinessTripDaysMationByBusinessTripId(businessTripId);
        checkWorkBusinessTripDao.insertCheckWorkBusinessTripDaysMation(beans);
        // 判断是否提交审批
        if("2".equals(map.get("subType").toString())){
            // 提交审批
            ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_BUSINESS_TRIP_KEY).submitToActivi(businessTripId);
        }
    }

    /**
     * 出差申请详情
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCheckWorkBusinessTripDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = checkWorkBusinessTripDao.queryCheckWorkBusinessTripDetailById(id);
        // 获取出差日期信息
        List<Map<String, Object>> businessTripDay = checkWorkBusinessTripDao.queryCheckWorkBusinessTripDaysDetailByBusinessTripId(id);
        bean.put("businessTripDay", businessTripDay);
        // 获取附件信息
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        outputObject.setBean(bean);
    }

    /**
     * 在工作流中编辑出差申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateCheckWorkBusinessTripByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 出差申请id
        String businessTripId = map.get("id").toString();
        // 处理出差天数
        List<Map<String, Object>> beans = getBusinessTripDays(map.get("businessTripDayStr").toString(), businessTripId);
        if(beans.size() == 0){
            outputObject.setreturnMessage("请填写出差日期.");
            return;
        }
        checkWorkBusinessTripDao.updateCheckWorkBusinessTripMation(map);
        checkWorkBusinessTripDao.deleteCheckWorkBusinessTripDaysMationByBusinessTripId(businessTripId);
        checkWorkBusinessTripDao.insertCheckWorkBusinessTripDaysMation(beans);
        // 编辑工作流中的数据
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_BUSINESS_TRIP_KEY).editApplyMationInActiviti(businessTripId);
    }

    /**
     * 提交审批出差申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editCheckWorkBusinessTripToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String businessTripId = map.get("id").toString();
        Map<String, Object> bean = checkWorkBusinessTripDao.queryCheckWorkBusinessTripDetailById(businessTripId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以提交审批
            ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_BUSINESS_TRIP_KEY).submitToActivi(businessTripId);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 作废出差申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateCheckWorkBusinessTripToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String businessTripId = map.get("id").toString();
        Map<String, Object> bean = checkWorkBusinessTripDao.queryCheckWorkBusinessTripDetailById(businessTripId);
        int state = Integer.parseInt(bean.get("state").toString());
        if(ActivitiConstants.ActivitiState.DRAFT.getState() == state
                || ActivitiConstants.ActivitiState.NO_PASS.getState() == state
                || ActivitiConstants.ActivitiState.REVOKE.getState() == state){
            // 草稿、审核不通过或者撤销状态下可以作废
            checkWorkBusinessTripDao.updateCheckWorkBusinessTripStateById(businessTripId, 4);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 撤销出差申请
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editCheckWorkBusinessTripToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
        ActivitiRunFactory.run(inputObject, outputObject, CHECK_WORK_BUSINESS_TRIP_KEY).revokeActivi();
    }

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> queryStateIsSuccessBusinessTripDayByUserIdAndMonths(String userId, String timeId, List<String> months) throws Exception {
        List<Map<String, Object>> beans = checkWorkBusinessTripDao.queryStateIsSuccessBusinessTripDayByUserIdAndMonths(userId, timeId, months);
        beans.forEach(bean -> {
            bean.put("type", CheckWorkConstants.CheckDayType.DAY_IS_BUSINESS_TRAVEL.getType());
            bean.put("className", CheckWorkConstants.CheckDayType.DAY_IS_BUSINESS_TRAVEL.getClassName());
            bean.put("allDay", "1");
            bean.put("showBg", "2");
            bean.put("editable", false);
        });
        return beans;
    }
}
