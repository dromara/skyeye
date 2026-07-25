/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.framework.file.core.client.FileClientConfig;
import lombok.Data;

import com.skyeye.upload.enums.FileStorageEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;

/**
 * @ClassName: FileConfig
 * @Description: 文件配置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 9:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "skyeye:fileConfig")
@TableName(value = "skyeye_file_config", autoResultMap = true)
@ApiModel("文件配置实体类")
public class FileConfig extends BaseGeneralInfo {

    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否默认", required = "required,num", enumClass = IsDefaultEnum.class)
    private Integer isDefault;

    @TableField(value = "storage")
    @ApiModelProperty(value = "存储器", required = "required,num", enumClass = FileStorageEnum.class)
    private Integer storage;

    @TableField(value = "config")
    @ApiModelProperty(value = "配置信息", required = "required")
    private String config;

    @TableField(exist = false)
    @Property(value = "配置信息")
    private FileClientConfig configMation;

}
