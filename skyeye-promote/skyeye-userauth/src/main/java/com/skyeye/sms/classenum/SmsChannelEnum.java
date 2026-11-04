/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.classenum;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SmsChannelEnum
 * @Description: 短信渠道枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 23:05
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SmsChannelEnum implements SkyeyeEnumClass {

    DEBUG_DING_TALK("debug_ding_talk", "调试(钉钉)", true, false),
    ALIYUN("aliyun", "阿里云", true, true),
    TENCENT("tencent", "腾讯云", true, false),
    HUAWEI("huawei", "华为云", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static SmsChannelEnum getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getKey().equals(code), values());
    }

    public static Map<String, Object> getMation(String code) {
        SmsChannelEnum type = getByCode(code);
        Map<String, Object> result = new HashMap<>();
        if (ObjectUtil.isNull(type)) {
            return result;
        }
        result.put("id", type.getKey());
        result.put("name", type.getValue());
        return result;
    }

}
