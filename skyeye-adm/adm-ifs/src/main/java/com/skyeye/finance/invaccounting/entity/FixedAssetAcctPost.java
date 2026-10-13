package com.skyeye.finance.invaccounting.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * 固定资产折旧记账入参（仅接口绑定，不落库）
 */
@Data
@ApiModel("固定资产折旧记账入参")
public class FixedAssetAcctPost {

    @ApiModelProperty(value = "来源单据id，空则用来源单", required = "required")
    private String sourceId;

    @ApiModelProperty(value = "来源单号", required = "required")
    private String sourceNo;

    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @ApiModelProperty(value = "折旧金额", required = "required")
    private String amount;

    @ApiModelProperty(value = "摘要")
    private String summary;
}
