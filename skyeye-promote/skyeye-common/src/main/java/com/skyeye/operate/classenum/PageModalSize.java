/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.operate.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PageModalSize
 * @Description: 操作按钮新开页面时的弹窗尺寸（仅弹窗打开生效，新窗口打开忽略）
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/15 13:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PageModalSize implements SkyeyeEnumClass {

    NORMAL(1, "默认宽度", "70%", "default-modal", true, true),
    LARGE(2, "较大宽度", "90%", "default-modal", true, false),
    FULL(3, "全屏", "100%", "full-modal", true, false);

    private Integer key;

    private String value;

    /** 弹窗宽度，如 70% / 100% */
    private String width;

    /** 对应 SkModal 的 modalClass */
    private String modalClass;

    private Boolean show;

    private Boolean isDefault;

    public static PageModalSize getByKey(Integer key) {
        if (key == null) {
            return NORMAL;
        }
        for (PageModalSize item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return NORMAL;
    }

}
