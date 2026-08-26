/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.framework.file.core.client.FileClient;
import com.skyeye.upload.entity.FileConfig;

/**
 * @ClassName: FileConfigService
 * @Description: 文件配置服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 17:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FileConfigService extends SkyeyeBusinessService<FileConfig> {

    /**
     * 获得默认的文件客户端
     *
     * @return 文件客户端
     */
    FileClient getMasterFileClient();

    FileClient getFileClient(String configId);

    /**
     * 按存储器类型获取文件客户端（{@link com.skyeye.upload.enums.FileStorageEnum}）
     *
     * @param storage 存储器类型，为空则返回默认
     * @return 文件客户端；找不到时返回 null
     */
    FileClient getFileClientByStorage(Integer storage);

    /**
     * 按存储器类型获取文件配置
     *
     * @param storage 存储器类型，为空则返回默认配置
     * @return 文件配置；找不到时返回 null
     */
    FileConfig getFileConfigByStorage(Integer storage);
}
