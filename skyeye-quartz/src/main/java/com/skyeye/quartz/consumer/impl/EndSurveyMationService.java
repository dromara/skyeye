package com.skyeye.quartz.consumer.impl;

import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.dao.SysQuartzDao;
import com.skyeye.quartz.consumer.TaskMateService;
import com.skyeye.eve.entity.quartz.SysQuartz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 问卷调查
 */
@Service("endSurveyMationService")
public class EndSurveyMationService implements TaskMateService {

    @Autowired
    private SysQuartzDao sysQuartzDao;

    @Override
    public void call(SysQuartz sysQuartz) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", sysQuartz.getName());
        // 获取问卷信息
        Map<String, Object> surveyMation = sysQuartzDao.querySurveyMationById(map);
        if("1".equals(surveyMation.get("surveyState").toString())){
            // 执行中
            map.put("realEndTime", DateUtil.getTimeAndToString());
            sysQuartzDao.editSurveyStateToEndNumZdById(map);
        }
    }

}
