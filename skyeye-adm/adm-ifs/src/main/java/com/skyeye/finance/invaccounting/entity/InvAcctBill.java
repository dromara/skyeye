package com.skyeye.finance.invaccounting.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.invaccounting.classenum.InvAcctBillState;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "ifs_inv_acct_bill", autoResultMap = true)
@ApiModel("存货核算单实体类")
public class InvAcctBill extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "单号", fuzzyLike = true)
    private String oddNumber;

    @TableField("bill_type")
    @ApiModelProperty(value = "核算类型/业务事项", required = "required", enumClass = BizAcctEventType.class)
    private String billType;

    @TableField("cost_domain_id")
    @ApiModelProperty(value = "成本域id", required = "required")
    private String costDomainId;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField("biz_date")
    @ApiModelProperty(value = "业务日期", required = "required")
    private String bizDate;

    @TableField("source_type")
    @ApiModelProperty(value = "来源类型")
    private String sourceType;

    @TableField("source_id")
    @ApiModelProperty(value = "来源id")
    private String sourceId;

    @TableField("source_no")
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @TableField("state")
    @Property(value = "状态", enumClass = InvAcctBillState.class)
    private Integer state;

    @TableField("journal_voucher_id")
    @ApiModelProperty(value = "凭证id")
    private String journalVoucherId;

    @TableField("total_amount")
    @ApiModelProperty(value = "金额合计")
    private String totalAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细", required = "json")
    private List<InvAcctItem> items;
}
