/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.lightapp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: LightApp
 * @Description: 轻应用实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/12 9:18
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "light:app")
@TableName(value = "light_app")
@ApiModel("轻应用实体类")
public class LightApp extends BaseGeneralInfo {

    @TableField(value = "logo")
    @ApiModelProperty(value = "logo图片")
    private String logo;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "所属分类ID", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "所属分类信息")
    private LightAppType typeMation;

    @TableField(value = "app_url")
    @ApiModelProperty(value = "应用地址", required = "required")
    private String appUrl;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

}
