package com.skyeye.receivable.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

import com.skyeye.receivable.classenum.CrmPayStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;

/**
 * @ClassName: Receivable
 * @Description: 客户应收事项实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:28
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "crm_receivable")
@ApiModel("应收事项实体类")
public class Receivable extends SkyeyeFlowable {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @Property(value = "单号，仅用于展示使用")
    private String name;

    @TableField(value = "contract_id")
    @ApiModelProperty(value = "合同ID")
    private String contractId;

    @TableField(exist = false)
    @Property(value = "合同")
    private Map<String, Object> contractMation;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(客户id)", required = "required")
    private String objectId;

    @TableField(exist = false)
    @Property(value = "客户")
    private Map<String, Object> objectMation;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "付款方式，参考数据字典")
    private String typeId;

    @TableField(value = "amount_price")
    @ApiModelProperty(value = "应收款金额", required = "double", defaultValue = "0")
    private String amountPrice;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = FlowableStateEnum.class)
    private String state;

    @TableField(value = "pay_state")
    @ApiModelProperty(value = "付款状态", defaultValue = "0", enumClass = CrmPayStateEnum.class)
    private Integer payState;

    @TableField(value = "paid_price")
    @Property(value = "已回收金额")
    private String paidPrice;

    @TableField(value = "invoice_date")
    @ApiModelProperty(value = "单据日期")
    private String invoiceDate;

    @TableField(value = "contact_id")
    @ApiModelProperty(value = "联系人id(如果不选合同，联系人id必填，选了合同可以不填联系人id)")
    private String contactId;

    @TableField(exist = false)
    @Property(value = "联系人信息")
    private Map<String, Object> contactMation;
}
