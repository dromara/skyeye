/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.chtopic.classenum.StudentChooseActionType;
import com.skyeye.chtopic.entity.ChooseStudentHistory;
import com.skyeye.chtopic.entity.ChooseTopic;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ChooseStudentHistoryService extends SkyeyeBusinessService<ChooseStudentHistory> {

    void saveStudentHistory(String activityId, String studentId, StudentChooseActionType actionType,
                            ChooseTopic chooseTopic, String teacherId, String remark, String operatorId);

    void queryStudentChooseHistoryByActivity(InputObject inputObject, OutputObject outputObject);

    void queryTeacherReviewHistoryByActivity(InputObject inputObject, OutputObject outputObject);
}
