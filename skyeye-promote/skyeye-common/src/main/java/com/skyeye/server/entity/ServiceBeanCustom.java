/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.coderule.entity.CodeRule;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: ServiceBeanCustom
 * @Description: 自定义服务信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/1/6 22:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"appId", "className"})
@TableName(value = "skyeye_class_service_bean_custom", autoResultMap = true)
@ApiModel("自定义服务信息实体类")
public class ServiceBeanCustom extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("class_name")
    @ApiModelProperty(value = "服务类的className", required = "required")
    private String className;

    @TableField(value = "app_id")
    @ApiModelProperty(value = "应用的appId", required = "required")
    private String appId;

    @TableField("code_rule_id")
    @ApiModelProperty(value = "编码规则id", required = "required")
    private String codeRuleId;

    @TableField(value = "ai_form_assist", updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "是否开启表单AI辅助", enumClass = WhetherEnum.class, defaultValue = "1")
    private Integer aiFormAssist;

    @TableField(exist = false)
    @Property(value = "编码规则信息")
    private CodeRule codeRule;

    @TableField(exist = false)
    @Property(value = "服务的原始信息")
    private ServiceBean serviceBean;

}
