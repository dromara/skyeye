package com.skyeye.finance.journal.entity;

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
@TableName(value = "ifs_journal_entry")
@ApiModel("会计凭证分录实体类")
public class JournalEntry extends SkyeyeLinkData {

    @TableField("line_no")
    @ApiModelProperty(value = "行号", required = "required,num")
    private Integer lineNo;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField(exist = false)
    @Property("科目信息")
    private Map<String, Object> subjectMation;

    @TableField("direction")
    @ApiModelProperty(value = "借贷方向", required = "required,num", enumClass = AmountDirection.class)
    private Integer direction;

    @TableField("amount")
    @ApiModelProperty(value = "金额", required = "required")
    private String amount;

    @TableField("qty")
    @ApiModelProperty(value = "数量")
    private String qty;

    @TableField("summary")
    @ApiModelProperty(value = "摘要")
    private String summary;

    @TableField("aux_customer_id")
    @ApiModelProperty(value = "辅助-客户")
    private String auxCustomerId;

    @TableField("aux_supplier_id")
    @ApiModelProperty(value = "辅助-供应商")
    private String auxSupplierId;

    @TableField("aux_material_id")
    @ApiModelProperty(value = "辅助-物料")
    private String auxMaterialId;

    @TableField("aux_department_id")
    @ApiModelProperty(value = "辅助-部门")
    private String auxDepartmentId;

    @TableField("aux_project_id")
    @ApiModelProperty(value = "辅助-项目")
    private String auxProjectId;

    @TableField("aux_depot_id")
    @ApiModelProperty(value = "辅助-仓库")
    private String auxDepotId;
}
