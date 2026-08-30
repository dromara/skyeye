/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ErpPageService
 * @Description: ERP统计模块服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/2 11:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpPageService {

    void queryFourTypeMoneyList(InputObject inputObject, OutputObject outputObject);

    void querySixMonthPurchaseMoneyList(InputObject inputObject, OutputObject outputObject);

    void querySixMonthSealsMoneyList(InputObject inputObject, OutputObject outputObject);

    void queryTwelveMonthProfitMoneyList(InputObject inputObject, OutputObject outputObject);

    void queryProcessFlowCount(InputObject inputObject, OutputObject outputObject);

}
