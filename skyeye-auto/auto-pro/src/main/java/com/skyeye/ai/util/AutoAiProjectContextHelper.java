/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.project.entity.AutoProject;
import com.skyeye.project.service.AutoProjectService;
import com.skyeye.version.entity.AutoVersion;
import com.skyeye.version.service.AutoVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AI 提示词中的项目/模块/版本上下文。
 */
@Component
public class AutoAiProjectContextHelper {

    @Autowired
    private AutoProjectService autoProjectService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoVersionService autoVersionService;

    public String loadProjectName(String objectId) {
        AutoProject project = autoProjectService.selectById(objectId);
        return project == null || project.getName() == null ? "" : project.getName().toString();
    }

    public String loadModuleName(String moduleId) {
        if (StrUtil.isBlank(moduleId)) {
            return "";
        }
        AutoModule module = autoModuleService.selectById(moduleId);
        return module == null || module.getName() == null ? "" : module.getName().toString();
    }

    public String loadVersionName(String versionId) {
        if (StrUtil.isBlank(versionId)) {
            return "";
        }
        AutoVersion version = autoVersionService.selectById(versionId);
        return version == null || version.getName() == null ? "" : version.getName().toString();
    }
}
