/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory;

import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.lang.reflect.Constructor;

/**
 * @ClassName: ErpRunFactory
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/9 20:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ErpRunFactory {

    public static ErpOrderFactoryResult run(InputObject inputObject, OutputObject outputObject, String orderType) {
        try {
            String classPath = ErpConstants.DepoTheadSubType.getFactoryClassPath(orderType);
            Class<?> clazz = Class.forName(classPath);
            Constructor<?> constructor = clazz.getConstructor(InputObject.class, OutputObject.class, String.class);
            return (ErpOrderFactoryResult) constructor.newInstance(inputObject, outputObject, orderType);
        } catch (Exception ex) {
            throw new RuntimeException("ErpRunFactory error", ex);
        }
    }

}
