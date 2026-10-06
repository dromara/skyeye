package com.skyeye.finance.template.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.subject.classenum.AmountDirection;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "ifs_voucher_template_line")
@ApiModel("凭证模板分录实体类")
public class VoucherTemplateLine extends SkyeyeLinkData {

    @TableField("line_no")
    @ApiModelProperty(value = "行号", required = "required,num")
    private Integer lineNo;

    @TableField("direction")
    @ApiModelProperty(value = "借贷方向", required = "required,num", enumClass = AmountDirection.class)
    private Integer direction;

    @TableField("subject_id")
    @ApiModelProperty(value = "会计科目ID", required = "required")
    private String subjectId;

    @TableField(exist = false)
    @Property(value = "会计科目信息")
    private Map<String, Object> subjectMation;

    @TableField("amount_expr")
    @ApiModelProperty(value = "金额表达式", required = "required")
    private String amountExpr;

    @TableField("summary_tpl")
    @ApiModelProperty(value = "摘要模板")
    private String summaryTpl;
}
