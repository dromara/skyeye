/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.BossIntervieweeFromDao;
import com.skyeye.service.BossIntervieweeFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BossIntervieweeFromServiceImpl
 * @Description: 面试者来源管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/7 13:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class BossIntervieweeFromServiceImpl implements BossIntervieweeFromService {

    @Autowired
    private BossIntervieweeFromDao bossIntervieweeFromDao;

    @Override
    public void queryBossIntervieweeFromList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        List<Map<String, Object>> beans = bossIntervieweeFromDao.queryBossIntervieweeFromList(inputParams);
        if(!beans.isEmpty()){
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }

    @Override
    public void insertBossIntervieweeFrom(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        inputParams.put("id", ToolUtil.getSurFaceId());
        inputParams.put("createTime", DateUtil.getTimeAndToString());
        inputParams.put("userId", inputObject.getLogParams().get("id"));
        bossIntervieweeFromDao.insertBossIntervieweeFrom(inputParams);
    }

    @Override
    public void queryBossIntervieweeFromById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        Map<String, Object> bean = bossIntervieweeFromDao.queryBossIntervieweeFromById(inputParams.get("id").toString());
        if (bean != null) {
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    @Override
    public void updateBossIntervieweeFromById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        inputParams.put("userId", inputObject.getLogParams().get("id"));
        inputParams.put("lastUpdateTime", DateUtil.getTimeAndToString());
        bossIntervieweeFromDao.updateBossIntervieweeFromById(inputParams);
    }

    @Override
    public void delBossIntervieweeFromById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        bossIntervieweeFromDao.delBossIntervieweeFromById(inputParams.get("id").toString());
    }
}
