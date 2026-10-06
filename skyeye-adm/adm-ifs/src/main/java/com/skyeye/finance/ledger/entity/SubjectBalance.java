package com.skyeye.finance.ledger.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@TableName(value = "ifs_subject_balance", autoResultMap = true)
@ApiModel("科目余额实体类")
public class SubjectBalance extends OperatorUserInfo {

    @TableId("id")
    private String id;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField("period_code")
    @ApiModelProperty(value = "期间编码", required = "required")
    private String periodCode;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField("subject_num")
    @ApiModelProperty(value = "科目编码")
    private String subjectNum;

    @TableField("begin_debit")
    private String beginDebit;

    @TableField("begin_credit")
    private String beginCredit;

    @TableField("period_debit")
    private String periodDebit;

    @TableField("period_credit")
    private String periodCredit;

    @TableField("end_debit")
    private String endDebit;

    @TableField("end_credit")
    private String endCredit;

    @TableField("aux_customer_id")
    private String auxCustomerId;

    @TableField(exist = false)
    @Property("客户名称")
    private String auxCustomerName;

    @TableField("aux_supplier_id")
    private String auxSupplierId;

    @TableField(exist = false)
    @Property("供应商名称")
    private String auxSupplierName;

    @TableField("aux_material_id")
    private String auxMaterialId;

    @TableField("aux_department_id")
    private String auxDepartmentId;

    @TableField("aux_project_id")
    private String auxProjectId;

    @TableField("aux_depot_id")
    private String auxDepotId;
}
