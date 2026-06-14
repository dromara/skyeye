/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ClockSource
 * @Description: 打卡来源枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ClockSource implements SkyeyeEnumClass {

    WEB_SOURCE("web", "网站端", true, true),
    ONLINE_SOURCE("online", "线上", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getShowName(String key) {
        if (StrUtil.isBlank(key)) {
            return StrUtil.EMPTY;
        }
        for (ClockSource item : ClockSource.values()) {
            if (StrUtil.equals(item.getKey(), key)) {
                return item.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

    public static ClockSource getByKey(String key) {
        if (StrUtil.isBlank(key)) {
            return WEB_SOURCE;
        }
        for (ClockSource item : ClockSource.values()) {
            if (StrUtil.equals(item.getKey(), key)) {
                return item;
            }
        }
        return WEB_SOURCE;
    }

}
