package com.skyeye.eve.assets.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * 固定资产月结折旧明细
 */
@Data
@TableName(value = "asset_depreciation_detail")
@ApiModel("固定资产月结折旧明细")
public class AssetDepreciationDetail extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("run_id")
    @ApiModelProperty(value = "批次id", required = "required")
    private String runId;

    @TableField("asset_report_id")
    @ApiModelProperty(value = "资产明细id", required = "required")
    private String assetReportId;

    @TableField("asset_num")
    @ApiModelProperty(value = "资产编号")
    private String assetNum;

    @TableField("original_amount")
    @ApiModelProperty(value = "原值")
    private String originalAmount;

    @TableField("residual_amount")
    @ApiModelProperty(value = "残值")
    private String residualAmount;

    @TableField("accum_before")
    @ApiModelProperty(value = "计提前累计折旧")
    private String accumBefore;

    @TableField("deprec_amount")
    @ApiModelProperty(value = "本期折旧额")
    private String deprecAmount;

    @TableField("accum_after")
    @ApiModelProperty(value = "计提后累计折旧")
    private String accumAfter;
}
