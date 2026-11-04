/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.util;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

/**
 * @ClassName: NoticeTemplateRender
 * @Description: 通知模板占位符渲染，占位符格式 {name}
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public final class NoticeTemplateRender {

    private NoticeTemplateRender() {
    }

    public static String render(String template, Map<String, Object> params) {
        if (StrUtil.isEmpty(template)) {
            return StrUtil.EMPTY;
        }
        if (params == null || params.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() == null ? StrUtil.EMPTY : entry.getValue().toString();
            result = result.replace(placeholder, value);
        }
        return result;
    }

}
