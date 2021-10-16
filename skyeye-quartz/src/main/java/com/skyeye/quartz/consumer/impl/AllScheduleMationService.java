/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.quartz.consumer.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.quartz.consumer.TaskMateService;
import com.skyeye.eve.entity.quartz.SysQuartz;
import com.skyeye.service.JobMateMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知所有人日程提醒
 */
@Service("allScheduleMationService")
public class AllScheduleMationService implements TaskMateService {

    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Autowired
    private JobMateMationService jobMateMationService;

    @Override
    public void call(SysQuartz sysQuartz) throws Exception {
        Map<String, Object> bean = new HashMap<>();
        bean.put("id", sysQuartz.getName());
        bean = sysQuartzDao.queryScheduleMationByScheduleId(bean);
        List<Map<String, Object>> users = sysQuartzDao.queryAllUserAndEmailISNotNull(bean);
        List<Map<String, Object>> userJson = new ArrayList<>();
        Map<String, Object> uJson = null;
        for(Map<String, Object> user : users){
            //发送消息
            String content = "尊敬的" + user.get("userName").toString() + ",您好：<br/>《" + bean.get("title").toString() + "》放假时间为：" + bean.get("startTime").toString() + " ~ "
                    + bean.get("endTime").toString() + "<br/>请注意安排好您的工作时间，祝您出行愉快。";
            if(!ToolUtil.isBlank(bean.get("remarks").toString())){
                content += "<br>备注信息：" + bean.get("remarks").toString();
            }
            uJson = new HashMap<>();
            uJson.put("content", content);
            uJson.put("userId", user.get("userId"));
            userJson.add(uJson);
            //发送邮件
            if(!ToolUtil.isBlank(user.get("email").toString()) && user.containsKey("email")){
                Map<String, Object> emailNotice = new HashMap<>();
                emailNotice.put("title", "日程提醒");
                emailNotice.put("content", content);
                emailNotice.put("email", user.get("email").toString());
                emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
                jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), sysQuartz.getCreateId());
            }
        }
        if(!userJson.isEmpty()){
            //调用消息系统添加通知
            List<Map<String, Object>> notices = new ArrayList<>();
            Map<String, Object> notice = null;
            for(int i = 0; i < userJson.size(); i++){
                Map<String, Object> userJsonObject = userJson.get(i);
                notice = new HashMap<>();
                notice.put("id", ToolUtil.getSurFaceId());
                notice.put("title", "日程提醒");
                notice.put("noticeDesc", "您有一条新的日程信息，请及时阅读。");
                notice.put("content", userJsonObject.get("content").toString());
                notice.put("state", "1");//未读消息
                notice.put("userId", userJsonObject.get("userId").toString());
                notice.put("type", "1");//消息类型
                notice.put("createId", "0dc9dd4cd4d446ae9455215fe753c44e");//默认系统管理员
                notice.put("createTime", DateUtil.getTimeAndToString());
                notices.add(notice);
            }
            if(!notices.isEmpty()){
                sysQuartzDao.insertNoticeListMation(notices);
            }
        }
        //修改日程状态
        sysQuartzDao.editMationByScheduleId(bean);
    }

}
