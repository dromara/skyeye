/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tms.driver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: TmsDriver
 * @Description: 司机管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/9 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField("userId")
@TableName(value = "tms_driver")
@ApiModel(value = "司机管理表")
public class TmsDriver extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @Property(value = "名称")
    private String name;

    @TableField("user_id")
    @ApiModelProperty(value = "用户id", required = "required")
    private String userId;

    @TableField(exist = false)
    @Property(value = "用户信息")
    private Map<String, Object> userMation;

    @TableField("driver_no")
    @ApiModelProperty(value = "驾驶证号", required = "required")
    private String driverNo;

    @TableField("effect_start_time")
    @ApiModelProperty(value = "驾驶证有效期,开始", required = "required")
    private String effectStartTime;

    @TableField("effect_end_time")
    @ApiModelProperty(value = "驾驶证有效期,结束", required = "required")
    private String effectEndTime;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;
}
