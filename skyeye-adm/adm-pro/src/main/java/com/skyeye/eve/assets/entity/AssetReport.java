/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.assets.classenum.AssetReportState;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: AssetReport
 * @Description: 资产明细实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 10:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:assetReport", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "asset_management_detail")
@ApiModel("资产明细实体类")
public class AssetReport extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "asset_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "资产id", required = "required")
    private String assetId;

    @TableField(value = "asset_num", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "资产编号", required = "required", fuzzyLike = true)
    private String assetNum;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "资产单价", required = "required,double")
    private String unitPrice;

    @TableField(value = "useful_life_months")
    @ApiModelProperty(value = "折旧年限(月)，直线法；空则月结时按60")
    private Integer usefulLifeMonths;

    @TableField(value = "residual_rate")
    @ApiModelProperty(value = "残值率(%)，空则月结时按5")
    private String residualRate;

    @TableField(value = "deprec_start_month")
    @ApiModelProperty(value = "开始折旧年月YYYY-MM；空则按入库次月")
    private String deprecStartMonth;

    @TableField(value = "accum_deprec_amount")
    @ApiModelProperty(value = "累计折旧")
    private String accumDeprecAmount;

    @TableField(value = "last_deprec_month")
    @ApiModelProperty(value = "上次折旧年月YYYY-MM")
    private String lastDeprecMonth;

    @TableField(value = "from_id")
    @ApiModelProperty(value = "资产来源id", required = "required")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "资产来源信息")
    private Map<String, Object> fromMation;

    @TableField(value = "purchase_id")
    @ApiModelProperty(value = "采购单id")
    private String purchaseId;

    @TableField(value = "purchase_time")
    @ApiModelProperty(value = "采购日期")
    private String purchaseTime;

    @TableField(value = "warehousing_id")
    @ApiModelProperty(value = "入库单id")
    private String warehousingId;

    @TableField(value = "warehousing_time")
    @ApiModelProperty(value = "入库日期")
    private String warehousingTime;

    @TableField(value = "return_id")
    @ApiModelProperty(value = "退货单id")
    private String returnId;

    @TableField(value = "return_time")
    @ApiModelProperty(value = "退货日期")
    private String returnTime;

    @TableField(value = "storage_area")
    @ApiModelProperty(value = "存放区域")
    private String storageArea;

    @TableField(value = "use_id")
    @ApiModelProperty(value = "领用单id")
    private String useId;

    @TableField(value = "use_user_id")
    @ApiModelProperty(value = "领用人")
    private String useUserId;

    @TableField(exist = false)
    @Property(value = "领用人信息")
    private Map<String, Object> useUserMation;

    @TableField(value = "revert_id")
    @ApiModelProperty(value = "归还单id")
    private String revertId;

    @TableField(value = "revert_user_id")
    @ApiModelProperty(value = "归还人")
    private String revertUserId;

    @TableField(exist = false)
    @Property(value = "归还人信息")
    private Map<String, Object> revertUserMation;

    @TableField(value = "asset_admin")
    @ApiModelProperty(value = "管理人")
    private String assetAdmin;

    @TableField(exist = false)
    @Property(value = "管理人信息")
    private Map<String, Object> assetAdminMation;

    @TableField(value = "`describe`")
    @ApiModelProperty(value = "附加描述")
    private String describe;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = AssetReportState.class)
    private Integer state;

}
