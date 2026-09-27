/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.order.enums.OrderAfterSaleStatus;
import com.skyeye.order.enums.OrderAfterSaleType;
import lombok.Data;

/**
 * 商城商品售后单
 */
@Data
@TableName("shop_order_after_sale")
@ApiModel("商城商品售后单")
public class OrderAfterSale extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("order_item_id")
    @ApiModelProperty(value = "订单子单id", required = "required")
    private String orderItemId;

    @TableField("parent_id")
    @ApiModelProperty(value = "订单主单id")
    private String parentId;

    @TableField("store_id")
    @ApiModelProperty(value = "售出门店id")
    private String storeId;

    @TableField("source_store_id")
    @ApiModelProperty(value = "供货方门店id（代发）")
    private String sourceStoreId;

    @TableField("type")
    @ApiModelProperty(value = "售后类型", enumClass = OrderAfterSaleType.class, required = "required")
    private Integer type;

    @TableField("reason_text")
    @ApiModelProperty(value = "申请原因", required = "required")
    private String reasonText;

    @TableField("apply_amount")
    @ApiModelProperty(value = "申请退款金额（分）")
    private String applyAmount;

    @TableField("apply_count")
    @ApiModelProperty(value = "申请数量")
    private String applyCount;

    @TableField("evidence")
    @ApiModelProperty(value = "凭证图片JSON")
    private String evidence;

    @TableField("status")
    @Property(value = "售后状态", enumClass = OrderAfterSaleStatus.class)
    private Integer status;

    @TableField("reject_reason")
    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    @TableField("refund_amount")
    @ApiModelProperty(value = "实际退款金额（分）")
    private String refundAmount;

    @TableField("prior_item_state")
    @ApiModelProperty(value = "申请前子单状态，拒绝/撤销时恢复")
    private Integer priorItemState;

    @TableField("restore_stock")
    @ApiModelProperty(value = "商家决定：完成后是否回补库存，0否1是")
    private Integer restoreStock;

    @TableField("return_deliver_number")
    @ApiModelProperty(value = "买家退货快递单号")
    private String returnDeliverNumber;

    @TableField("out_refund_no")
    @ApiModelProperty(value = "商户退款单号")
    private String outRefundNo;

    @TableField("refund_channel_status")
    @ApiModelProperty(value = "通道退款状态")
    private Integer refundChannelStatus;

    @TableField("channel_refund_no")
    @ApiModelProperty(value = "通道退款单号")
    private String channelRefundNo;

    @TableField(exist = false)
    @Property(value = "订单子单信息")
    private OrderItem orderItemMation;
}
