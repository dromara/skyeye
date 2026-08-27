/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.util.DateUtil;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 步骤入参/断言随机数拼接（执行时动态生成，避免保存预览值导致重复拼接）。
 */
public final class AutoStepRandomHelper {

    private AutoStepRandomHelper() {
    }

    public static String buildCustomValue(String literal, String randomCategory, String randomPosition) {
        String raw = literal == null ? "" : literal;
        if (StrUtil.isBlank(randomCategory) || StrUtil.isBlank(randomPosition)) {
            return raw;
        }
        String random = generateRandomByCategory(randomCategory);
        if (StrUtil.isBlank(random)) {
            return raw;
        }
        return "front".equalsIgnoreCase(randomPosition) ? random + raw : raw + random;
    }

    public static String generateRandomByCategory(String category) {
        if (StrUtil.isBlank(category)) {
            return "";
        }
        Date now = new Date();
        switch (category.toLowerCase(Locale.ROOT)) {
            case "date":
                return DateUtil.formatDate2Str(now, DateUtil.YYYY_MM_DD);
            case "datetime":
                return DateUtil.formatDate2Str(now, DateUtil.YYYY_MM_DD_HH_MM_SS);
            case "code6":
                return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
            case "code8":
                return randomAlphaNum(8);
            default:
                return "";
        }
    }

    private static String randomAlphaNum(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
