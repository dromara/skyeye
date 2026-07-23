/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.dao.MainPageDao;
import com.skyeye.eve.service.MainPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MainPageServiceImpl implements MainPageService {

    @Autowired
    private MainPageDao mainPageDao;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 获取本月考勤天数，我的文件数，我的论坛帖数，我的知识库文档数
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFourNumListByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 1.获取本月考勤天数
        String checkOnWorkNum = mainPageDao.queryCheckOnWorkNumByUserId(userId, tenantId);
        // 2.获取我的文件数
        String diskCloudFileNum = mainPageDao.queryDiskCloudFileNumByUserId(userId, tenantId);
        // 3.获取我的论坛帖数
        String forumNum = mainPageDao.queryForumNumByUserId(userId, tenantId);
        // 4.获取我已审核通过的知识库文档数
        String knowledgeNum = mainPageDao.queryKnowledgeNumByUserId(userId, tenantId);
        Map<String, Object> map = new HashMap<>();
        map.put("checkOnWorkNum", checkOnWorkNum);
        map.put("diskCloudFileNum", diskCloudFileNum);
        map.put("forumNum", forumNum);
        map.put("knowledgeNum", knowledgeNum);
        outputObject.setBean(map);
    }

}
