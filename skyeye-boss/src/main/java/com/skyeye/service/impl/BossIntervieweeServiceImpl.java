/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.constans.IntervieweeStatusEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.BossIntervieweeDao;
import com.skyeye.service.BossIntervieweeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @ClassName: BossIntervieweeServiceImpl
 * @Description: 面试者来源管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/27 13:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class BossIntervieweeServiceImpl implements BossIntervieweeService {

    @Autowired
    private BossIntervieweeDao bossIntervieweeDao;

    @Override
    public void queryBossIntervieweeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        List<Map<String, Object>> beans = bossIntervieweeDao.queryBossIntervieweeList(inputParams);
        if(!beans.isEmpty()){
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }

    @Override
    public void insertBossInterviewee(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        if (repeatVerification(outputObject, inputParams, null)){
            return;
        }
        // 状态(0.待面试  1.面试中  2.面试通过  3.全部面试不通过  4.拒绝入职)
        inputParams.put("state", IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getStatus());
        inputParams.put("id", ToolUtil.getSurFaceId());
        inputParams.put("createTime", DateUtil.getTimeAndToString());
        inputParams.put("userId", inputObject.getLogParams().get("id"));
        bossIntervieweeDao.insertBossInterviewee(inputParams);
    }

    // 重复面试者校验
    private boolean repeatVerification(OutputObject outputObject, Map<String, Object> inputParams, String id) throws ParseException {
        // 根据姓名、手机号查询面试者信息
        List<Map<String, Object>> bossIntervieweeBeans = bossIntervieweeDao.queryBossIntervieweeByCondition(inputParams);
        if (!CollectionUtil.isEmpty(bossIntervieweeBeans)) {
            // 获取相同姓名+手机号最近的一条面试者数据
            Map<String, Object> bossIntervieweeBean = bossIntervieweeBeans.get(0);
            // 适配更新操作重复校验
            if (bossIntervieweeBean.get("id").toString().equals(id)) {
                return false;
            }
            Integer state = Integer.valueOf(bossIntervieweeBean.get("state").toString());
            List<Integer> needCheckStates = Arrays.asList(IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getStatus(),
                    IntervieweeStatusEnum.INTERVIEW_STATUS.getStatus(), IntervieweeStatusEnum.INTERVIEW_FAILED_STATUS.getStatus());
            if (needCheckStates.contains(state)) {
                outputObject.setreturnMessage("同一个姓名、手机号的面试者已存在, 请重新确认面试者信息!");
                return true;
            } else if (IntervieweeStatusEnum.INTERVIEW_PASS_STATUS.getStatus().equals(state)) {
                Date lastJoinTimeDate = DateUtil.getPointTime(bossIntervieweeBean.get("lastJoinTime").toString(), DateUtil.YYYY_MM_DD);
                // 比较当前时间与最后入职的日期相差几个月
                long differMonth = cn.hutool.core.date.DateUtil.betweenMonth(lastJoinTimeDate, new Date(), true);
                if (differMonth < 6) {
                    outputObject.setreturnMessage("该面试者通过面试未没有超过半年，则不允许录入");
                    return true;
                }
            } else if (IntervieweeStatusEnum.REJECTED_STATUS.getStatus().equals(state)) {
                Date refuseTimeDate = DateUtil.getPointTime(bossIntervieweeBean.get("refuseTime").toString(), DateUtil.YYYY_MM_DD);
                // 比较当前时间与最后入职的日期相差几个月
                long differMonth = cn.hutool.core.date.DateUtil.betweenMonth(refuseTimeDate, new Date(), true);
                if (differMonth < 6) {
                    outputObject.setreturnMessage("该面试者不通过面试未没有超过半年，则不允许录入");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void queryBossIntervieweeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        Map<String, Object> bean = bossIntervieweeDao.queryBossIntervieweeById(inputParams.get("id").toString());
        if (bean != null) {
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    @Override
    public void updateBossIntervieweeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        String id = inputParams.get("id").toString();
        Map<String, Object> bossIntervieweeBean = bossIntervieweeDao.queryBossIntervieweeById(id);
        if (bossIntervieweeBean == null) {
            outputObject.setreturnMessage("该面试者数据已被删除, 请重新刷新界面!");
            return;
        }
        Integer state = Integer.valueOf(bossIntervieweeBean.get("state").toString());
        // state=0/1可以进行编辑
        if (IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getStatus().equals(state)
                || IntervieweeStatusEnum.INTERVIEW_STATUS.getStatus().equals(state)) {
            if (repeatVerification(outputObject, inputParams, id)){
                return;
            }
            // 入参state
            Integer inputState = Integer.valueOf(inputParams.get("state").toString());
            if (IntervieweeStatusEnum.INTERVIEW_PASS_STATUS.getStatus().equals(inputState)) {
                inputParams.put("lastJoinTime", DateUtil.getYmdTimeAndToString());
            } else if (IntervieweeStatusEnum.REJECTED_STATUS.getStatus().equals(inputState)) {
                inputParams.put("refuseTime", DateUtil.getYmdTimeAndToString());
            }
            inputParams.put("userId", inputObject.getLogParams().get("id"));
            inputParams.put("lastUpdateTime", DateUtil.getTimeAndToString());
            bossIntervieweeDao.updateBossIntervieweeById(inputParams);
        } else {
            outputObject.setreturnMessage("更新失败, 面试者状态为待面试、面试中的方可更新!");
        }
    }

    @Override
    public void delBossIntervieweeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        String id = inputParams.get("id").toString();
        Map<String, Object> bossIntervieweeBean = bossIntervieweeDao.queryBossIntervieweeById(id);
        if (bossIntervieweeBean != null) {
            Integer state = Integer.valueOf(bossIntervieweeBean.get("state").toString());
            if (!IntervieweeStatusEnum.PENDING_INTERVIEW_STATUS.getStatus().equals(state)) {
                outputObject.setreturnMessage("删除失败, 只有待面试状态的数据可删除!");
                return;
            }
            bossIntervieweeDao.delBossIntervieweeById(id);
        }
    }
}
