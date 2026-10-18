/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.payment.entity.PaymentCollection;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Invoice
 * @Description: 发票实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 19:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "crm:invoice", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_invoice", autoResultMap = true)
@ApiModel("发票实体类")
public class Invoice extends SkyeyeFlowable {

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "contract_id")
    @ApiModelProperty(value = "合同ID", required = "required")
    private String contractId;

    @TableField(exist = false)
    @Property(value = "合同")
    private Map<String, Object> contractMation;

    @TableField(value = "invoic_time")
    @ApiModelProperty(value = "开票日期", required = "required")
    private String invoicTime;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "开票类型，参考数据字典")
    private String typeId;

    @TableField(value = "price")
    @ApiModelProperty(value = "开票金额(价税合计)", required = "double", defaultValue = "0")
    private String price;

    @TableField(value = "tax_rate")
    @ApiModelProperty(value = "税率(%)，如 13；空则业财税额按 0")
    private String taxRate;

    @TableField(value = "tax_price")
    @ApiModelProperty(value = "税额；有值优先，否则按税率从价税合计拆分")
    private String taxPrice;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "invoice_header_id")
    @ApiModelProperty(value = "发票抬头id", required = "required")
    private String invoiceHeaderId;

    @TableField(exist = false)
    @Property(value = "发票抬头")
    private InvoiceHeader invoiceHeaderMation;

    @TableField(value = "payment_collection_id")
    @ApiModelProperty(value = "回款id", required = "required")
    private String paymentCollectionId;

    @TableField(exist = false)
    @Property(value = "回款")
    private PaymentCollection paymentCollectionMation;

    @TableField("mail_contacts_name")
    @ApiModelProperty(value = "邮寄-联系人名字")
    private String mailContactsName;

    @TableField("mail_phone")
    @ApiModelProperty(value = "邮寄-联系方式")
    private String mailPhone;

    @TableField("province_id")
    @ApiModelProperty(value = "邮寄-省ID")
    private String provinceId;

    @TableField(exist = false)
    @Property(value = "省信息")
    private Map<String, Object> provinceMation;

    @TableField("city_id")
    @ApiModelProperty(value = "邮寄-市ID")
    private String cityId;

    @TableField(exist = false)
    @Property(value = "市信息")
    private Map<String, Object> cityMation;

    @TableField("area_id")
    @ApiModelProperty(value = "邮寄-区县ID")
    private String areaId;

    @TableField(exist = false)
    @Property(value = "区/县信息")
    private Map<String, Object> areaMation;

    @TableField("township_id")
    @ApiModelProperty(value = "邮寄-乡镇ID")
    private String townshipId;

    @TableField(exist = false)
    @Property(value = "乡镇信息")
    private Map<String, Object> townshipMation;

    @TableField("absolute_address")
    @ApiModelProperty(value = "邮寄-具体地址")
    private String absoluteAddress;

}
