package com.skyeye.finance.invaccounting.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

@Data
@TableName(value = "ifs_inv_acct_item", autoResultMap = true)
@ApiModel("存货核算明细实体类")
public class InvAcctItem extends SkyeyeLinkData {

    @TableField("material_id")
    @ApiModelProperty(value = "物料id", required = "required")
    private String materialId;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id")
    private String normsId;

    @TableField("depot_id")
    @ApiModelProperty(value = "仓库id")
    private String depotId;

    @TableField("direction")
    @ApiModelProperty(value = "1入库 2出库", required = "required,num")
    private Integer direction;

    @TableField("qty")
    @ApiModelProperty(value = "数量", required = "required")
    private String qty;

    @TableField("price")
    @ApiModelProperty(value = "单价")
    private String price;

    @TableField("amount")
    @ApiModelProperty(value = "金额")
    private String amount;
}
