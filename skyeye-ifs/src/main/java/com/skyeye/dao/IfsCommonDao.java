/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsCommonDao
 * @Description: 财务模块公共部分
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/28 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IfsCommonDao {

    /**
     * 根据条件查询单据列表
     *
     * @param params 条件参数
     * @return 单据列表
     * @throws Exception
     */
    List<Map<String, Object>> queryIfsOrderList(Map<String, Object> params) throws Exception;

}
