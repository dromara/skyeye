/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.subject.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.subject.entity.AccountSubject;

/**
 * @ClassName: IfsAccountSubjectService
 * @Description: 会计科目管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 21:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsAccountSubjectService extends SkyeyeBusinessService<AccountSubject> {

    void queryEnabledSubjectList(InputObject inputObject, OutputObject outputObject);

    void initDefaultSubjects(InputObject inputObject, OutputObject outputObject);

}
