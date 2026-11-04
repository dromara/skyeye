/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NoticeChannelEnum
 * @Description: 消息通知渠道枚举
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NoticeChannelEnum implements SkyeyeEnumClass {

    SITE(1, "站内信", true, true),
    EMAIL(2, "邮件", true, false),
    SMS(3, "短信", true, false),
    MP(4, "公众号", true, false),
    MINI_PROGRAM(5, "小程序", true, false),
    CHAT(6, "聊天", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static NoticeChannelEnum getByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (NoticeChannelEnum bean : values()) {
            if (bean.getKey().equals(key)) {
                return bean;
            }
        }
        return null;
    }

}
