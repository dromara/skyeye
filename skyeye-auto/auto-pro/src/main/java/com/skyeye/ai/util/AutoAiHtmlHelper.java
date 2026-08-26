/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;

/**
 * AI 生成 HTML 内容处理工具。
 */
public final class AutoAiHtmlHelper {

    private AutoAiHtmlHelper() {
    }

    public static String nvlText(String value) {
        return StrUtil.isBlank(value) ? "无" : value;
    }

    public static String plainText(String html) {
        if (StrUtil.isBlank(html)) {
            return "";
        }
        return html.replaceAll("<[^>]*>", " ").replace("&nbsp;", " ").trim();
    }

    public static String wrapAsHtml(String raw) {
        if (StrUtil.isBlank(raw)) {
            return "";
        }
        String text = raw.trim();
        if (text.startsWith("<")) {
            return text;
        }
        String escaped = escapeHtml(text);
        return "<p>" + escaped.replace("\n", "</p><p>") + "</p>";
    }

    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * 兜底：模型常输出纯文本小节，补上 strong 加粗。
     */
    public static String ensureSectionBold(String html, String... labels) {
        if (StrUtil.isBlank(html) || labels == null || labels.length == 0) {
            return html;
        }
        String result = html;
        for (String label : labels) {
            if (StrUtil.isBlank(label)) {
                continue;
            }
            if (result.contains("<strong>" + label) || result.contains("<b>" + label)
                || result.contains("<h3>" + label) || result.contains("<h2>" + label)) {
                continue;
            }
            String withCnColon = label + "：";
            String withEnColon = label + ":";
            if (result.contains(withCnColon)) {
                result = result.replace(withCnColon, "<strong>" + withCnColon + "</strong>");
            } else if (result.contains(withEnColon)) {
                result = result.replace(withEnColon, "<strong>" + withEnColon + "</strong>");
            } else if (result.contains(label)) {
                result = result.replaceFirst(java.util.regex.Pattern.quote(label),
                    "<strong>" + label + "</strong>");
            }
        }
        return result;
    }
}
