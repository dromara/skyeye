/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.eve.assets.classenum.PurchasePutFromType;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: AssetPurchasePut
 * @Description: 资产采购入库实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/3 18:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:asset:purchasePut", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "asset_purchase")
@ApiModel("资产采购申请实体类")
public class AssetPurchasePut extends SkyeyeFlowable {

    @TableField(value = "id_key", updateStrategy = FieldStrategy.NEVER)
    @Property("服务类的serviceClassName")
    private String idKey;

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总金额")
    private String allPrice;

    @TableField("need_depot")
    @ApiModelProperty(value = "是否需要出入库", enumClass = WhetherEnum.class)
    private Integer needDepot;

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型", enumClass = PurchasePutFromType.class)
    private Integer fromTypeId;

    @TableField(value = "from_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "资产信息", required = "required,json")
    private List<AssetPurchaseLink> purchaseLinks;

}
