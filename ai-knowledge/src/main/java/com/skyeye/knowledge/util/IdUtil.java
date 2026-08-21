package com.skyeye.knowledge.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 本地工具
 */
public final class IdUtil {

    private IdUtil() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
