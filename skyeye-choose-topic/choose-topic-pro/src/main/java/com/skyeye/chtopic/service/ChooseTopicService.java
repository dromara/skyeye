/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ChooseTopicService
 * @Description: 课题服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ChooseTopicService extends SkyeyeBusinessService<ChooseTopic> {
    void importChooseTopic(InputObject inputObject, OutputObject outputObject);

    void chooseTopicById(InputObject inputObject, OutputObject outputObject);

    void cnacleChooseTopicById(InputObject inputObject, OutputObject outputObject);

    void exportChooseTopic(InputObject inputObject, OutputObject outputObject);

    List<ChooseTopic> queryListByActivityId(String activityId);

    void queryChooseMeTopicList(InputObject inputObject, OutputObject outputObject);

    void changeResultForTeacher(InputObject inputObject, OutputObject outputObject);

    void cancelTeacherResult(InputObject inputObject, OutputObject outputObject);

    void chooseTeacher(InputObject inputObject, OutputObject outputObject);

    void deleteByActivityId(String activityId);

    Map<String, Integer> getChooseTopicCountByActivityId(String activityId, List<String> userIds);

    void queryTeacherTopicNum(InputObject inputObject, OutputObject outputObject);

    void chooseActivityTeacher(InputObject inputObject, OutputObject outputObject);

    void cancelActivityTeacher(InputObject inputObject, OutputObject outputObject);

    void queryStudentChooseByActivity(InputObject inputObject, OutputObject outputObject);
}
