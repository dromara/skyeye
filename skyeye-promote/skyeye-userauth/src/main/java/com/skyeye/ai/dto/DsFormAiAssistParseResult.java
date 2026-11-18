package com.skyeye.ai.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单 AI 流式回答解析结果。
 */
@Data
public class DsFormAiAssistParseResult {

    private String reply;

    private Map<String, Object> fieldValues = new HashMap<>();

    private List<DsFormAiAction> actions = new ArrayList<>();

    private List<DsFormAiClarification> clarifications = new ArrayList<>();
}
