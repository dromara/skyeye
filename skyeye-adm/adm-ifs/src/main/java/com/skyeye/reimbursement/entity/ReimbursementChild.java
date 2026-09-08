/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ReimbursementChild
 * @Description: 报销订单子内容实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ifs_reimbursement_child")
@ApiModel("报销订单子内容实体类")
public class ReimbursementChild extends SkyeyeLinkData {

    @TableField("occur_time")
    @ApiModelProperty(value = "发生日期", required = "required")
    private String occurTime;

    @TableField("reimburse_pro_id")
    @ApiModelProperty(value = "报销项目id，参考数据字典", required = "required")
    private String reimburseProId;

    @TableField(exist = false)
    @Property(value = "报销项目信息")
    private Map<String, Object> reimburseProMation;

    @TableField(value = "price")
    @ApiModelProperty(value = "报销金额", required = "required,double")
    private String price;

    @TableField("project_id")
    @ApiModelProperty(value = "关联项目id")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "关联项目信息")
    private Map<String, Object> projectMation;

    @TableField(exist = false)
    @ApiModelProperty("部门id")
    private String departmentId;

}
