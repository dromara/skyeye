package com.skyeye.finance.journal.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.journal.classenum.JournalVoucherState;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RedisCacheField(name = "ifs:journalVoucher")
@TableName(value = "ifs_journal_voucher", autoResultMap = true)
@ApiModel("会计凭证头实体类")
public class JournalVoucher extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "凭证号（后端编码规则生成，前端无需传）", fuzzyLike = true)
    private String oddNumber;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField(exist = false)
    @Property("账套信息")
    private Map<String, Object> setOfBooksMation;

    @TableField("period_id")
    @ApiModelProperty(value = "期间id")
    private String periodId;

    @TableField("period_code")
    @ApiModelProperty(value = "期间编码", required = "required")
    private String periodCode;

    @TableField("voucher_date")
    @ApiModelProperty(value = "凭证日期", required = "required")
    private String voucherDate;

    @TableField("state")
    @ApiModelProperty(value = "状态", enumClass = JournalVoucherState.class)
    private Integer state;

    @TableField("source_type")
    @ApiModelProperty(value = "来源类型")
    private String sourceType;

    @TableField("source_id")
    @ApiModelProperty(value = "来源单据id")
    private String sourceId;

    @TableField("source_no")
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @TableField("event_type")
    @ApiModelProperty(value = "业务事项类型", enumClass = BizAcctEventType.class)
    private String eventType;

    @TableField("idempotency_key")
    @ApiModelProperty(value = "幂等键")
    private String idempotencyKey;

    @TableField("debit_total")
    @ApiModelProperty(value = "借方合计")
    private String debitTotal;

    @TableField("credit_total")
    @ApiModelProperty(value = "贷方合计")
    private String creditTotal;

    @TableField("attach_voucher_id")
    @ApiModelProperty(value = "原始附件凭证id")
    private String attachVoucherId;

    @TableField("reverse_of_id")
    @ApiModelProperty(value = "冲销原凭证id")
    private String reverseOfId;

    @TableField(exist = false)
    @Property("冲销原凭证号（列表展示）")
    private String reverseOfOddNumber;

    @TableField(exist = false)
    @Property("被冲销后生成的冲销凭证号（原凭证列表展示）")
    private String reversedByOddNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "分录列表", required = "required,json")
    private List<JournalEntry> entries;
}
