/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.listconfig;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class AiBizListFieldMapping {

    /**
     * title 候选路径，取第一个非空，支持点号嵌套
     */
    private List<String> titlePaths = new ArrayList<>();

    /**
     * subtitle 各段
     */
    private List<SubtitlePart> subtitle = new ArrayList<>();

    private List<String> idPaths = new ArrayList<>();

    private List<String> oddNumberPaths = new ArrayList<>();

    @Data
    public static class SubtitlePart {
        /**
         * 前缀标签，如「申请人」；金额分转元时可空
         */
        private String label;

        private List<String> paths = new ArrayList<>();

        /**
         * 值映射，如 "2"→"待出库"
         */
        private Map<String, String> valueLabels = new LinkedHashMap<>();

        /**
         * 按分转元并加 ¥
         */
        private boolean fenToYuan;
    }
}
