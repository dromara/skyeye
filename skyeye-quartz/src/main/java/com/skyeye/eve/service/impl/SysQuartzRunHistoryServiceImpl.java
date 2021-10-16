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
import com.skyeye.eve.dao.SysQuartzRunHistoryDao;
import com.skyeye.eve.entity.quartz.SysQuartzRunHistory;
import com.skyeye.eve.service.SysQuartzRunHistoryService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: SysQuartzRunHistoryServiceImpl
 * @Description: 系统定时任务启动历史服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/29 11:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysQuartzRunHistoryServiceImpl implements SysQuartzRunHistoryService {

    @Autowired
    private SysQuartzRunHistoryDao sysQuartzRunHistoryDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     * 获取系统定时任务启动历史列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysQuartzRunHistoryByQuartzId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sysQuartzRunHistoryDao.querySysQuartzRunHistoryByQuartzId(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 记录启动定时任务的历史
     *
     * @param quartzId 定时任务的id
     * @return
     * @throws Exception
     */
    @Override
    public String startSysQuartzRun(String quartzId) throws Exception {
        SysQuartzRunHistory sysQuartzRunHistory = new SysQuartzRunHistory();
        String historyId = ToolUtil.getSurFaceId();
        sysQuartzRunHistory.setId(historyId);
        sysQuartzRunHistory.setQuartzId(quartzId);
        sysQuartzRunHistory.setStartTime(DateUtil.getTimeAndToString());
        sysQuartzRunHistory.setEndTime(DateUtil.getTimeAndToString());
        String quartzCreateIdKey = String.format(Locale.ROOT, "%s-userId", quartzId);
        String userId = jedisClient.get(quartzCreateIdKey);
        sysQuartzRunHistory.setStartUserId(userId);
        if(ToolUtil.isBlank(userId)){
            sysQuartzRunHistory.setStartType(SysQuartzRunHistory.StartType.AUTO_START.getType());
        }else{
            sysQuartzRunHistory.setStartType(SysQuartzRunHistory.StartType.MANUAL_START.getType());
        }
        sysQuartzRunHistory.setState(SysQuartzRunHistory.State.START_RUNING.getState());
        sysQuartzRunHistoryDao.insertSysQuartzRunHistoryMation(sysQuartzRunHistory);
        return historyId;
    }

    /**
     * 结束定时任务
     *
     * @param id 定时任务执行历史id
     * @param state 完成状态
     * @throws Exception
     */
    @Override
    public void endSysQuartzRun(String id, Integer state) throws Exception {
        sysQuartzRunHistoryDao.updateSysQuartzRunHistoryComplateState(id, state, DateUtil.getTimeAndToString());
        SysQuartzRunHistory sysQuartzRunHistory = sysQuartzRunHistoryDao.querySysQuartzRunHistoryById(id);
        String quartzCreateIdKey = String.format(Locale.ROOT, "%s-userId", sysQuartzRunHistory.getQuartzId());
        jedisClient.del(quartzCreateIdKey);
    }
}
