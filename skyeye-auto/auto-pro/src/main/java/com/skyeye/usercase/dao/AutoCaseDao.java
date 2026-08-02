/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.usercase.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.entity.AutoUserCaseQueryDo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoCaseDao
 * @Description: 用例数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface AutoCaseDao extends SkyeyeBaseMapper<AutoCase> {
    List<Map<String, Object>> queryAutoCaseList(AutoUserCaseQueryDo pageInfo);
}
