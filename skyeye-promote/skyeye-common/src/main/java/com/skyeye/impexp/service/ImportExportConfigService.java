/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.impexp.entity.ImportExportConfig;

public interface ImportExportConfigService extends SkyeyeBusinessService<ImportExportConfig> {

    void queryImportExportConfigList(InputObject inputObject, OutputObject outputObject);

    void queryImportExportFieldOptions(InputObject inputObject, OutputObject outputObject);

    /**
     * 按当前导入类型配置下载导入模板（仅表头，与 ScheduleDay 下载模板方式一致）
     */
    void downloadImportTemplate(InputObject inputObject, OutputObject outputObject);

    /**
     * 按配置导出数据（不接收rows，后端按filters查询数据）
     */
    void exportByConfig(InputObject inputObject, OutputObject outputObject);

    /**
     * 按导入配置上传 Excel 并落库（multipart，file + appId/className/id）
     */
    void importByConfig(InputObject inputObject, OutputObject outputObject);
}

