package com.skyeye.ai.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 表单 AI 澄清选项（模型 JSON → 对象）。
 */
@Data
public class DsFormAiClarifyOption {

    private String id;

    private String label;

    /** 兼容模型用 name / value 代替 label / id，不回传前端 */
    @JsonIgnore
    private String name;

    @JsonIgnore
    private String value;

    public void normalize() {
        if (StrUtil.isBlank(id) && StrUtil.isNotBlank(value)) {
            id = value.trim();
        }
        if (StrUtil.isBlank(label) && StrUtil.isNotBlank(name)) {
            label = name.trim();
        }
        if (StrUtil.isBlank(id) && StrUtil.isNotBlank(label)) {
            id = label.trim();
        }
        if (StrUtil.isBlank(label) && StrUtil.isNotBlank(id)) {
            label = id.trim();
        }
        if (StrUtil.isNotBlank(id)) {
            id = id.trim();
        }
        if (StrUtil.isNotBlank(label)) {
            label = label.trim();
        }
    }

    public boolean isValid() {
        return StrUtil.isNotBlank(id) || StrUtil.isNotBlank(label);
    }
}
