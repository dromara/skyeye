/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.EmailSendModelDao;
import com.skyeye.eve.service.EmailSendModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmailSendModelServiceImpl implements EmailSendModelService {

    @Autowired
    private EmailSendModelDao emailSendModelDao;

    @Override
    public void queryEmailSendModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(inputParams.get("page").toString()), Integer.parseInt(inputParams.get("limit").toString()));
        List<Map<String, Object>> emailSendModelList = emailSendModelDao.queryEmailSendModelList(inputParams);
        outputObject.setBeans(emailSendModelList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void insertEmailSendModel(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        inputParams.put("id", ToolUtil.getSurFaceId());
        inputParams.put("createTime", DateUtil.getTimeAndToString());
        inputParams.put("userId", inputObject.getLogParams().get("id"));
        emailSendModelDao.insertEmailSendModel(inputParams);
    }

    @Override
    public void queryEmailSendModelInfoById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        Map<String, Object> emailSendModelInfo = emailSendModelDao.queryEmailSendModelInfoById(inputParams.get("id").toString());
        outputObject.setBean(emailSendModelInfo);
        outputObject.settotal(1);
    }

    @Override
    public void delEmailSendModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        emailSendModelDao.delEmailSendModelById(inputParams.get("id").toString());
    }

    @Override
    public void updateEmailSendModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> inputParams = inputObject.getParams();
        inputParams.put("lastUpdateTime", DateUtil.getTimeAndToString());
        inputParams.put("userId", inputObject.getLogParams().get("id"));
        emailSendModelDao.updateEmailSendModelById(inputParams);
    }
}
