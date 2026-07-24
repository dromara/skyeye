/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.util.Map;

import com.skyeye.subject.classenum.AmountDirection;

/**
 * @ClassName: IncomeOrderItem
 * @Description: 明细账订单子内容实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/23 12:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ifs_order_item")
@ApiModel("明细账订单子内容实体类")
public class IncomeOrderItem extends SkyeyeLinkData {

    @TableField("voucher_id")
    @ApiModelProperty(value = "凭证ID", required = "required")
    private String voucherId;

    @TableField(exist = false)
    @Property(value = "凭证信息")
    private Map<String, Object> voucherMation;

    @TableField("subject_id")
    @ApiModelProperty(value = "会计科目ID", required = "required")
    private String subjectId;

    @TableField(exist = false)
    @Property(value = "会计科目信息")
    private Map<String, Object> subjectMation;

    @TableField("each_amount")
    @ApiModelProperty(value = "金额", required = "required")
    private String eachAmount;

    @TableField("direction_type")
    @ApiModelProperty(value = "金额类型", required = "required,num", enumClass = AmountDirection.class)
    private Integer directionType;

}
