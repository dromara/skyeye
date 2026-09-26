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
import com.skyeye.common.entity.features.AreaInfo;
import com.skyeye.order.enums.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Order
 * @Description: 商品订单管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@RedisCacheField(name = "shop:order")
@TableName("shop_order")
@ApiModel("商品订单管理实体类")
public class Order extends AreaInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("odd_number")
    @Property(value = "订单编号", fuzzyLike = true)
    private String oddNumber;

    @TableField("type")
    @ApiModelProperty(value = "订单类型", required = "required", enumClass = ShopOrderType.class)
    private Integer type;

    @TableField("terminal")
    @ApiModelProperty(value = "订单来源", required = "required", enumClass = ShopOrderTerminal.class)
    private Integer terminal;

    @TableField("user_ip")
    @Property(value = "用户ip")
    private String userIp;

    @TableField("state")
    @Property(value = "状态", enumClass = ShopOrderState.class)
    private Integer state;

    @TableField("sign_state")
    @Property(value = "订单签收状态", enumClass = ShopOrderSignState.class)
    private Integer signState;

    @TableField("count")
    @Property(value = "商品的总数量")
    private String count;

    @TableField("finish_time")
    @Property(value = "订单完成时间")
    private String finishTime;

    @TableField("cancel_time")
    @Property(value = "订单取消时间")
    private String cancelTime;

    @TableField("cancel_type")
    @Property(value = "取消类型", enumClass = ShopOrderCancelType.class)
    private Integer cancelType;

    @TableField("comment_state")
    @Property(value = "是否评价", enumClass = ShopOrderCommentState.class)
    private Integer commentState;

    @TableField("brokerage_user_id")
    @ApiModelProperty(value = "分销用户id")
    private String brokerageUserId;

    @TableField("pay_time")
    @Property(value = "付款时间")
    private String payTime;

    @TableField("pay_type")
    @Property(value = "付款类型")
    private String payType;

    @TableField("total_price")
    @Property(value = "商品总价，单位：分")
    private String totalPrice;

    @TableField("discount_price")
    @Property(value = "优惠金额，单位：分")
    private String discountPrice;

    @TableField("delivery_price")
    @Property(value = "运费金额，单位：分")
    private String deliveryPrice;

    @TableField("adjust_price")
    @ApiModelProperty(value = "订单调价，单位：元")
    private String adjustPrice;

    @TableField("pay_price")
    @Property(value = "应付金额（总），单位：分")
    private String payPrice;

    @TableField("delivery_type")
    @ApiModelProperty(value = "配送方式")
    private Integer deliveryType;

    @TableField("tms_order_id")
    @ApiModelProperty(value = "物流单id")
    private String tmsOrderId;

    @TableField("receive_time")
    @Property(value = "收货时间")
    private String receiveTime;

    @TableField("address_id")
    @ApiModelProperty(value = "收货地址id", required = "required")
    private String addressId;

    @TableField(exist = false)
    @Property(value = "收货地址信息")
    private Map<String, Object> addressMation;

    @TableField("address_from_type")
    @ApiModelProperty(value = "收货地址信息来源类型，0：address表；1address_history表,默认0")
    private Integer addressFromType;

    @TableField("receiver_name")
    @Property(value = "收件人姓名")
    private String receiverName;

    @TableField("receiver_mobile")
    @Property(value = "收件人手机")
    private String receiverMobile;

    @TableField("pick_up_store_id")
    @ApiModelProperty(value = "delivery_type=自提时，自提门店id")
    private String pickUpStoreId;

    @TableField("pick_up_verify_code")
    @ApiModelProperty(value = "自提核销码")
    private String pickUpVerifyCode;

    @TableField("coupon_use_id")
    @ApiModelProperty(value = "用户领取的优惠券id")
    private String couponUseId;

    @TableField(exist = false)
    @Property(value = "用户领取的优惠券信息")
    private Map<String, Object> couponUseMation;

    @TableField("coupon_price")
    @Property(value = "优惠劵减免金额，单位：分")
    private String couponPrice;

    @TableField("use_point")
    @Property(value = "使用的积分")
    private Integer usePoint;

    @TableField("point_price")
    @Property(value = "积分抵扣的金额，单位：分")
    private String pointPrice;

    @TableField("give_point")
    @ApiModelProperty(value = "赠送的积分")
    private Integer givePoint;

    @TableField("vip_price")
    @Property(value = "VIP 减免金额，单位：分")
    private String vipPrice;

    @TableField("seckill_activity_id")
    @ApiModelProperty(value = "秒杀活动id")
    private String seckillActivityId;

    @TableField("bargain_activity_id")
    @ApiModelProperty(value = "砍价活动id")
    private String bargainActivityId;

    @TableField("bargain_record_id")
    @ApiModelProperty(value = "砍价记录id")
    private String bargainRecordId;

    @TableField("combination_activity_id")
    @ApiModelProperty(value = "拼团活动id")
    private String combinationActivityId;

    @TableField("combination_record_id")
    @ApiModelProperty(value = "拼团记录id")
    private String combinationRecordId;

    @TableField("user_remark")
    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @TableField("remark")
    @ApiModelProperty(value = "商家备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "子单列表", required = "required,json")
    private List<OrderItem> orderItemList;

    @TableField("channel_fee_rate")
    @Property(value = "渠道手续费比例，比如：0.001")
    private String channelFeeRate;

    @TableField("channel_fee_price")
    @Property(value = "渠道手续金额，单位：分")
    private Integer channelFeePrice;

    @TableField("extension_id")
    @Property(value = "支付成功的订单拓展单编号")
    private String extensionId;

    @TableField("extension_no")
    @Property(value = "支付成功的外部订单号")
    private String extensionNo;

}