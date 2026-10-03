package com.skyeye.finance.event.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.annotation.api.Property;
import com.skyeye.finance.event.classenum.BizAcctEventState;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "ifs_biz_acct_event", autoResultMap = true)
@ApiModel("业务会计事件实体类")
public class BizAcctEvent extends BaseGeneralInfo {

    @TableField("event_type")
    @ApiModelProperty(value = "事项类型", required = "required", fuzzyLike = true)
    private String eventType;

    @TableField("source_type")
    @ApiModelProperty(value = "来源类型", required = "required")
    private String sourceType;

    @TableField("source_id")
    @ApiModelProperty(value = "来源单据id", required = "required")
    private String sourceId;

    @TableField("source_no")
    @ApiModelProperty(value = "来源单号", fuzzyLike = true)
    private String sourceNo;

    @TableField("idempotency_key")
    @ApiModelProperty(value = "幂等键", required = "required")
    private String idempotencyKey;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id")
    private String setOfBooksId;

    @TableField(exist = false)
    @Property("账套信息")
    private Map<String, Object> setOfBooksMation;

    @TableField("payload")
    @ApiModelProperty(value = "业务载荷JSON")
    private String payload;

    @TableField("state")
    @ApiModelProperty(value = "状态", enumClass = BizAcctEventState.class)
    private Integer state;

    @TableField("journal_voucher_id")
    @ApiModelProperty(value = "生成的凭证id")
    private String journalVoucherId;

    @TableField("error_msg")
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @TableField("retry_count")
    @ApiModelProperty(value = "重试次数")
    private Integer retryCount;
}
