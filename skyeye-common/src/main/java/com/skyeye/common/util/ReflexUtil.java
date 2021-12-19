/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.util;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

/**
 * @ClassName: ReflexUtil
 * @Description: 反射工具类
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/19 13:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ReflexUtil {

    /**
     * java将字符串转换成可执行代码 工具类
     *
     * @param jexlExp
     * @param map
     * @return
     */
    public static Object convertToCode(String jexlExp, Map<String, String> map) {
        JexlEngine jexl = new JexlEngine();
        Expression expression = jexl.createExpression(jexlExp);
        JexlContext jc = new MapContext();
        for (String key : map.keySet()) {
            jc.set(key, map.get(key));
        }
        if (null == expression.evaluate(jc)) {
            return "";
        }
        return expression.evaluate(jc);
    }

}
