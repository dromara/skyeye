package com.skyeye.ai.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单 AI 澄清项（模型 JSON → 对象）。
 */
@Data
public class DsFormAiClarification {

    private String id;

    private String attrKey;

    private String type;

    private String question;

    private List<DsFormAiClarifyOption> options;

    public void normalize(int index) {
        attrKey = StrUtil.blankToDefault(attrKey, "").trim();
        type = StrUtil.isBlank(type) ? "choice" : type.trim();
        question = StrUtil.isBlank(question) ? "请选择" : question.trim();
        if (StrUtil.isBlank(id)) {
            id = StrUtil.isBlank(attrKey) ? ("clarify_" + index) : attrKey;
        } else {
            id = id.trim();
        }
        if (options == null) {
            options = new ArrayList<>();
        }
    }

    public boolean isChoice() {
        return "choice".equalsIgnoreCase(type);
    }
}
