/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.factory.IfsOrderFactory;
import com.skyeye.factory.IfsOrderFactoryResult;

/**
 * @ClassName: ExpenditureFactory
 * @Description: 支出单服务工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/28 23:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ExpenditureFactory extends IfsOrderFactory implements IfsOrderFactoryResult {

    public ExpenditureFactory(InputObject inputObject, OutputObject outputObject, String orderType) {
        super(inputObject, outputObject, orderType);
    }

}
