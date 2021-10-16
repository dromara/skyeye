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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知所有人工作计划提醒
 */
@Service("mySysWorkMationService")
public class MySysWorkMationService implements TaskMateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySysWorkMationService.class);

    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Autowired
    private JobMateMationService jobMateMationService;

    @Override
    public void call(SysQuartz sysQuartz) throws Exception {
        Map<String, Object> map = new HashMap<>();
        LOGGER.info("start task name is: {}", sysQuartz.getName());
        map.put("id", sysQuartz.getName());
        Map<String, Object> planMation = sysQuartzDao.queryWorkPlanMationToQuartzById(map);
        if(planMation != null){
            //计划类型 1.个人计划  2.部门计划  3.公司计划
            String planType = planMation.get("planType").toString();
            //是否邮件通知
            String whetherMail = planMation.get("whetherMail").toString();
            //是否内部通告通知
            String whetherNotice = planMation.get("whetherNotice").toString();
            if("1".equals(planType)){//个人计划单独处理
                //获取计划接收人的信息
                Map<String, Object> userMation = sysQuartzDao.queryUserMationByWorkPlanId(map);
                //发送的消息内容
                String content = "尊敬的" + userMation.get("userName").toString() + ",您好：<br/>您收到一条新的工作计划信息《" + planMation.get("title").toString() + "》，请登录系统查看";
                if("1".equals(whetherMail)){//邮件通知
                    if(!ToolUtil.isBlank(userMation.get("email").toString()) && userMation.containsKey("email")){
                        Map<String, Object> emailNotice = new HashMap<>();
                        emailNotice.put("title", "计划提醒");
                        emailNotice.put("content", content);
                        emailNotice.put("email", userMation.get("email").toString());
                        emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
                        jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), sysQuartz.getCreateId());
                    }
                }
                if("1".equals(whetherNotice)){//内部通告通知
                    //调用消息系统添加通知
                    Map<String, Object> notice = new HashMap<>();
                    notice.put("id", ToolUtil.getSurFaceId());
                    notice.put("title", "计划提醒");
                    notice.put("noticeDesc", "您有一条新的工作计划信息，请及时阅读。");
                    notice.put("content", content);
                    notice.put("state", "1");//未读消息
                    notice.put("userId", userMation.get("userId"));
                    notice.put("type", "2");//消息类型
                    notice.put("createId", "0dc9dd4cd4d446ae9455215fe753c44e");//默认系统管理员
                    notice.put("createTime", DateUtil.getTimeAndToString());
                    sysQuartzDao.insertNoticeMation(notice);
                }
                sysQuartzDao.updateNotifyStateByPlanId(map);
            }else if("2".equals(planType) || "3".equals(planType)){//部门计划和公司计划统一处理
                //获取计划接收人的信息
                List<Map<String, Object>> userMations = sysQuartzDao.queryUserMationsByWorkPlanId(map);
                //内部消息通知对象
                List<Map<String, Object>> userJson = new ArrayList<>();
                Map<String, Object> uJson = null;
                for(Map<String, Object> userMation : userMations){
                    //发送的消息内容
                    String content = "尊敬的" + userMation.get("userName").toString() + ",您好：<br/>您收到一条新的工作计划信息《" + planMation.get("title").toString() + "》，请登录系统查看";
                    if("1".equals(whetherMail)){//邮件通知
                        if(!ToolUtil.isBlank(userMation.get("email").toString()) && userMation.containsKey("email")){
                            Map<String, Object> emailNotice = new HashMap<>();
                            emailNotice.put("title", "计划提醒");
                            emailNotice.put("content", content);
                            emailNotice.put("email", userMation.get("email").toString());
                            emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
                            jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(emailNotice), sysQuartz.getCreateId());
                        }
                    }
                    if("1".equals(whetherNotice)){//内部通告通知
                        uJson = new HashMap<>();
                        uJson.put("content", content);
                        uJson.put("userId", userMation.get("userId"));
                        userJson.add(uJson);
                    }
                }

                //内部通告通知
                if(!userJson.isEmpty()){
                    //调用消息系统添加通知
                    List<Map<String, Object>> notices = new ArrayList<>();
                    Map<String, Object> notice = null;
                    for(int i = 0; i < userJson.size(); i++){
                        Map<String, Object> userJsonObject = userJson.get(i);
                        notice = new HashMap<>();
                        notice.put("id", ToolUtil.getSurFaceId());
                        notice.put("title", "计划提醒");
                        notice.put("noticeDesc", "您有一条新的工作计划信息，请及时阅读。");
                        notice.put("content", userJsonObject.get("content").toString());
                        notice.put("state", "1");//未读消息
                        notice.put("userId", userJsonObject.get("userId").toString());
                        notice.put("type", "12");//消息类型
                        notice.put("createId", "0dc9dd4cd4d446ae9455215fe753c44e");//默认系统管理员
                        notice.put("createTime", DateUtil.getTimeAndToString());
                        notices.add(notice);
                    }
                    if(!notices.isEmpty())
                        sysQuartzDao.insertNoticeListMation(notices);
                }
                sysQuartzDao.updateNotifyStateByPlanId(map);
            }
        }
        LOGGER.info("end task name is: {}", sysQuartz.getName());
    }

}
