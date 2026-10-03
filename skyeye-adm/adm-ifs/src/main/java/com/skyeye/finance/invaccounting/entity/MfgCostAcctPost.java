package com.skyeye.finance.invaccounting.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 生产成本记账入参（仅接口绑定，不落库）
 */
@Data
@ApiModel("生产成本记账入参")
public class MfgCostAcctPost {

    @ApiModelProperty(value = "来源单据id，空则用来源单号")
    private String sourceId;

    @ApiModelProperty(value = "来源单号", required = "required")
    private String sourceNo;

    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @ApiModelProperty(value = "成本域id（传明细时必填）")
    private String costDomainId;

    @ApiModelProperty(value = "金额（无明细时必填）")
    private String amount;

    @ApiModelProperty(value = "存货核算明细")
    private List<InvAcctItem> items;
}
