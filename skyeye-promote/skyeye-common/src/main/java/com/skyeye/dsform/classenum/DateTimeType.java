/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dsform.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: DateTimeType
 * @Description: 日期类型（含 Excel 导出 DataFormat 模式串）
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/5 18:02
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DateTimeType implements SkyeyeEnumClass {

    /**
     * 仅年。Excel 中请勿使用 yyyy 日期格式（输入 2025 会被当成「第 2025 天」显示成约 1905）。
     * 导出模板按「整数」格式写入，用户直接输入年份数字即可。
     */
    YEAR("year", "年", "0", false, true, false),
    /**
     * 年-月。Excel 日期语义下输入纯数字易与序列号混淆，模板按文本格式写入（用户填 2025-08）。
     */
    MONTH("month", "年-月", "@", false, true, false),
    DATE("date", "年-月-日", "yyyy-mm-dd", true, true, false),
    TIME("time", "时:分:秒", "hh:mm:ss", true, true, false),
    DATETIME("datetime", "年-月-日 时:分:秒", "yyyy-mm-dd hh:mm:ss", true, true, true),
    TIMEMINUTE("timeminute", "时:分", "hh:mm", true, true, false);

    private String key;

    private String value;

    /**
     * Excel/POI 单元格 DataFormat 模式串
     */
    private String excelPattern;

    /**
     * 是否按 Excel「日期序列值」写入（true=真实日期；false=不要用日期列，避免 2025→1905 等）
     */
    private Boolean excelDateSerial;

    private Boolean show;

    private Boolean isDefault;

    /**
     * 是否应按 Excel 日期序列处理（false 时模板/导出按文本或数值，不写 Date 值）
     */
    public boolean isExcelDateSerial() {
        return Boolean.TRUE.equals(excelDateSerial);
    }

    /**
     * 按枚举 key（与前端 id 一致）查找
     */
    public static DateTimeType getByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        String k = key.trim();
        for (DateTimeType type : values()) {
            if (type.getKey().equalsIgnoreCase(k)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 按 key 取 Excel 格式串；无法识别时返回 null
     */
    public static String getExcelPatternByKey(String key) {
        DateTimeType type = getByKey(key);
        return type == null ? null : type.getExcelPattern();
    }

    /**
     * 配置 key 是否应按 Excel 日期序列列处理；无法识别时默认 true（兼容历史 pattern）
     */
    public static boolean isExcelDateSerialByKey(String key) {
        DateTimeType type = getByKey(key);
        if (type == null) {
            return true;
        }
        return type.isExcelDateSerial();
    }

}
