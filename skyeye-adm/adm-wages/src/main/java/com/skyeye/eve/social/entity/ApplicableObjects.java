/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.ApplicableObjectsType;

/**
 * @ClassName: ApplicableObjects
 * @Description: 社保公积金适用对象实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("社保公积金适用对象实体类")
@TableName(value = "wages_social_security_fund_applicable_objects")
public class ApplicableObjects extends CommonInfo {

    @TableField("security_fund_id")
    @Property(value = "社保公积金id")
    private String securityFundId;

    @TableField("object_id")
    @ApiModelProperty(value = "适用对象id", required = "required")
    private String objectId;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField("object_type")
    @ApiModelProperty(value = "适用对象类型", required = "required,num", enumClass = ApplicableObjectsType.class)
    private Integer objectType;

}
