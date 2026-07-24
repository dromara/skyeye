/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.PayTypeEnum;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: ErpOrderHead
 * @Description: ERP相关订单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("ERP相关订单实体类")
public class ErpOrderHead extends ErpOrderCommon {

    @TableField("project_id")
    @ApiModelProperty(value = "关联项目id")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "关联项目信息")
    private Map<String, Object> projectMation;

    @TableField("discount")
    @ApiModelProperty(value = "优惠率,默认为0.00", required = "double")
    private String discount;

    @TableField("discount_money")
    @ApiModelProperty(value = "优惠金额/折损扣费,默认为0.00", required = "double")
    private String discountMoney;

    @TableField("need_over_plan")
    @ApiModelProperty(value = "是否需要统筹", enumClass = WhetherEnum.class)
    private Integer needOverPlan;

    @TableField("plan_complate_time")
    @ApiModelProperty(value = "计划完成时间")
    private String planComplateTime;

    @TableField("pay_type")
    @ApiModelProperty(value = "付款类型", required = "num", defaultValue = "0", enumClass = PayTypeEnum.class)
    private String payType;

    @TableField("holder_id")
    @ApiModelProperty(value = "关联的客户/供应商/会员id")
    private String holderId;

    @TableField(exist = false)
    @Property(value = "关联的客户/供应商/会员信息")
    private Map<String, Object> holderMation;

    @TableField("holder_key")
    @ApiModelProperty(value = "关联的客户/供应商/会员的className")
    private String holderKey;

    @TableField("total_price")
    @ApiModelProperty(value = "合计总金额(减去优惠后的金额，加上其他金额)")
    private String totalPrice;

    @TableField("account_id")
    @ApiModelProperty(value = "账户id")
    private String accountId;

    @TableField(exist = false)
    @Property(value = "账户信息")
    private Map<String, Object> accountMation;

}
