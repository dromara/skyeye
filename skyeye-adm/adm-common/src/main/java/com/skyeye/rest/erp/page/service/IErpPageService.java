/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.erp.page.service;

import java.util.Map;

/**
 * ERP 流程板统计（跨服务）。
 */
public interface IErpPageService {

    /**
     * @return 节点 code → 笔数，如 E01、E03
     */
    Map<String, Object> queryProcessFlowCount();
}
