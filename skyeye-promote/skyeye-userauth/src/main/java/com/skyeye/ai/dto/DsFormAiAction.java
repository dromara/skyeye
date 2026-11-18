package com.skyeye.ai.dto;

import lombok.Data;

/**
 * 表单 AI 解析结果中的导航/动作项。
 */
@Data
public class DsFormAiAction {

    private String type;

    private String label;

    private String menuId;

    private String pageId;
}
