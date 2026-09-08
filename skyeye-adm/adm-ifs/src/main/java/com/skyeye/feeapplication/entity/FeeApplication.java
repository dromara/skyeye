package com.skyeye.feeapplication.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: FeeApplication
 * @Description: 费用申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 13:59
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ifs_fee_application", autoResultMap = true)
@ApiModel("费用申请实体类")
public class FeeApplication extends SkyeyeFlowable {

    @TableField("invoice_date")
    @ApiModelProperty("单据日期、申请日期")
    private String invoiceDate;

    @TableField("price")
    @ApiModelProperty("发票金额")
    private String price;

    @TableField("type_id")
    @ApiModelProperty("费用类型id")
    private String typeId;

    @TableField("department_id")
    @ApiModelProperty("部门id")
    private String departmentId;

    @TableField(exist = false)
    @ApiModelProperty("部门信息")
    private Map<String, Object> departmentMation;

    @TableField("applicant_id")
    @ApiModelProperty("申请人id")
    private String applicantId;

    @TableField(exist = false)
    @ApiModelProperty("申请人信息")
    private Map<String, Object> applicantMation;

    @TableField("purposes")
    @ApiModelProperty("申请事由")
    private String purposes;

    @TableField("remark")
    @ApiModelProperty("备注")
    private String remark;

    @TableField("project_id")
    @ApiModelProperty(value = "关联项目id")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "关联项目信息")
    private Map<String, Object> projectMation;
}
