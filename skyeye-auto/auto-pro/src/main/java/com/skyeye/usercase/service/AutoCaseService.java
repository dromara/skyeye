/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.usercase.entity.AutoCase;

import java.util.List;

/**
 * @ClassName: AutoCaseService
 * @Description: 用例管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface AutoCaseService extends SkyeyeBusinessService<AutoCase> {

    void executeCase(InputObject inputObject, OutputObject outputObject);

    /**
     * 按前端提交的步骤配置试跑单步（可未保存），不写入历史。
     */
    void executeStep(InputObject inputObject, OutputObject outputObject);

    void executeCase(String id, Boolean recordData);

    Object executeCase(AutoCase autoCase, Boolean recordData);

    Object updateHistoryCase(AutoCase autoCase, Boolean recordData, AutoHistoryCase autoHistoryCase);

    /**
     * 按项目（可选模块）查询用例 id
     */
    List<String> queryCaseIdsByObjectId(String objectId, List<String> moduleIds);

}
