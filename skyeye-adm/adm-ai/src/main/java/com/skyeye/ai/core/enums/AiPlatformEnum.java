/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.enums;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AiPlatformEnum
 * @Description: AI 模型平台
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/5 11:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AiPlatformEnum implements SkyeyeEnumClass {

    YI_YAN("YiYan", "文心一言", "百度", true, true),
    XUN_FEI("XunFei", "讯飞星火", "讯飞", true, false),
    TONG_YI("TongYi", "通义千问", "阿里", true, false),
    DOU_BAO("DouBao", "豆包", "字节", true, false);

    private String key;

    private String value;

    private String remark;

    private Boolean show;

    private Boolean isDefault;

    public static AiPlatformEnum getName(String key) {
        for (AiPlatformEnum bean : AiPlatformEnum.values()) {
            if (StrUtil.equals(key, bean.getKey())) {
                return bean;
            }
        }
        throw new CustomException("非法的AI平台状态");
    }

    public static AiPlatformEnum getValue(String value) {
        for (AiPlatformEnum bean : AiPlatformEnum.values()) {
            if (StrUtil.equals(value, bean.getValue())) {
                return bean;
            }
        }
        throw new CustomException("非法的AI平台状态");
    }

}
