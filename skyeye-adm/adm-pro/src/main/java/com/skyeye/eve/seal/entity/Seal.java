/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.seal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.eve.seal.classenum.SealBgColorType;

/**
 * @ClassName: Seal
 * @Description: 印章实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/6 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "assistant:seal", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "seal")
@ApiModel("印章实体类")
public class Seal extends BaseGeneralInfo {

    @TableField(value = "company_name")
    @ApiModelProperty(value = "公司名称", required = "required")
    private String companyName;

    @TableField(value = "enable_time")
    @ApiModelProperty(value = "启用日期", required = "required")
    private String enableTime;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "num", defaultValue = "1", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(value = "bg_color_type")
    @ApiModelProperty(value = "背景类型", required = "required,num", enumClass = SealBgColorType.class)
    private Integer bgColorType;

    @TableField(value = "logo")
    @ApiModelProperty(value = "logo图片")
    private String logo;

    @TableField(value = "seal_admin")
    @ApiModelProperty(value = "管理人id")
    private String sealAdmin;

    @TableField(exist = false)
    @Property(value = "管理人")
    private Map<String, Object> sealAdminMation;

    @TableField(value = "borrow_id")
    @Property(value = "借用人id")
    private String borrowId;

    @TableField(exist = false)
    @Property(value = "借用人")
    private Map<String, Object> borrowMation;

}
