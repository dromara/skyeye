package com.skyeye.eve.assets.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * 固定资产月结折旧批次
 */
@Data
@TableName(value = "asset_depreciation_run")
@ApiModel("固定资产月结折旧批次")
public class AssetDepreciationRun extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("period")
    @ApiModelProperty(value = "折旧期间YYYY-MM", required = "required")
    private String period;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField("total_amount")
    @ApiModelProperty(value = "本期折旧合计")
    private String totalAmount;

    @TableField("asset_count")
    @ApiModelProperty(value = "计提资产数")
    private Integer assetCount;

    @TableField("state")
    @ApiModelProperty(value = "状态 1成功")
    private Integer state;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property("折旧明细")
    private List<AssetDepreciationDetail> detailList;
}
