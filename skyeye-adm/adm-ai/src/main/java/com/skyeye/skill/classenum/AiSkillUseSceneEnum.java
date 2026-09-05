/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI 技能使用位置：聊天助手 / 表单 AI 辅助 / 仅编码调用（可多选，逗号分隔存储）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AiSkillUseSceneEnum implements SkyeyeEnumClass {

    CHAT(1, "聊天助手", "blue", true, true),
    FORM(2, "表单AI辅助", "green", true, true),
    /**
     * 不在聊天、表单界面展示，业务侧按技能编码 oddNumber 调用
     */
    CODE(3, "仅编码调用", "orange", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

    public static final String DEFAULT_SCENES = "1,2";

    public static AiSkillUseSceneEnum getByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (AiSkillUseSceneEnum bean : values()) {
            if (key.equals(bean.getKey())) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 未配置时视为聊天+表单可用（兼容历史数据），不含「仅编码调用」
     */
    public static boolean contains(String useScene, AiSkillUseSceneEnum scene) {
        if (scene == null) {
            return false;
        }
        if (StrUtil.isBlank(useScene)) {
            return scene == CHAT || scene == FORM;
        }
        Set<String> keys = parseKeys(useScene);
        return keys.contains(String.valueOf(scene.getKey()));
    }

    public static String normalize(String useScene) {
        Set<String> keys = parseKeys(useScene);
        if (keys.isEmpty()) {
            return DEFAULT_SCENES;
        }
        return keys.stream().sorted().collect(Collectors.joining(","));
    }

    /**
     * 是否仅编码调用（不出现在聊天/表单，不与其它技能互斥）
     */
    public static boolean isCodeOnly(String useScene) {
        if (StrUtil.isBlank(useScene)) {
            return false;
        }
        Set<String> keys = parseKeys(useScene);
        return keys.size() == 1 && keys.contains(String.valueOf(CODE.getKey()));
    }

    /**
     * 聊天/表单场景是否重叠（不含仅编码）。空值按默认 1,2 处理。
     */
    public static boolean overlapsUiScene(String left, String right) {
        Set<String> a = uiSceneKeys(left);
        Set<String> b = uiSceneKeys(right);
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        for (String key : a) {
            if (b.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> uiSceneKeys(String useScene) {
        Set<String> keys;
        if (StrUtil.isBlank(useScene)) {
            keys = parseKeys(DEFAULT_SCENES);
        } else {
            keys = parseKeys(useScene);
        }
        keys.remove(String.valueOf(CODE.getKey()));
        return keys;
    }

    private static Set<String> parseKeys(String useScene) {
        Set<String> keys = new LinkedHashSet<>();
        if (StrUtil.isBlank(useScene)) {
            return keys;
        }
        Arrays.stream(useScene.split(","))
            .map(String::trim)
            .filter(StrUtil::isNotBlank)
            .forEach(item -> {
                try {
                    Integer key = Integer.valueOf(item);
                    if (getByKey(key) != null) {
                        keys.add(String.valueOf(key));
                    }
                } catch (NumberFormatException ignored) {
                    // ignore invalid
                }
            });
        return keys;
    }
}
