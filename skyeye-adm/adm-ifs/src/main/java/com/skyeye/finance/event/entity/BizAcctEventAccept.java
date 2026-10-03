package com.skyeye.finance.event.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * 业务会计事件受理入参（接口绑定）
 */
@Data
@ApiModel("业务会计事件受理入参")
public class BizAcctEventAccept {

    @ApiModelProperty(value = "业务事项类型", required = "required")
    private String eventType;

    @ApiModelProperty(value = "来源类型", required = "required")
    private String sourceType;

    @ApiModelProperty(value = "来源单据id", required = "required")
    private String sourceId;

    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @ApiModelProperty(value = "账套id")
    private String setOfBooksId;

    @ApiModelProperty(value = "金额", required = "required")
    private String amount;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "供应商id")
    private String supplierId;
}
