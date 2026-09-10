/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.impexp.entity.ImportExportApplicableObjects;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ImportExportApplicableObjectsService
 * @Description: 导入导出模板适用对象服务
 * @author: skyeye云系列--卫志强
 * @date: 2026/9/10
 */
public interface ImportExportApplicableObjectsService extends SkyeyeBusinessService<ImportExportApplicableObjects> {

    void deleteApplicableObjectsByConfigId(String configId);

    void saveApplicableObjects(String configId, List<ImportExportApplicableObjects> applicableObjectsList);

    List<ImportExportApplicableObjects> queryApplicableObjectsByConfigId(String configId);

    Map<String, List<ImportExportApplicableObjects>> queryApplicableObjectsByConfigIds(List<String> configIds);
}
