/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.otherwise.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;

/**
 * @ClassName: ErpOtherWiseOrderItem
 * @Description: 其他微服务创建ERP单据的子单据实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/19 18:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("其他微服务创建ERP单据的子单据实体类")
public class ErpOtherWiseOrderItem extends SkyeyeLinkData {

    @ApiModelProperty(value = "产品id", required = "required")
    private String materialId;

    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @ApiModelProperty(value = "数量", required = "required,num")
    private String operNumber;

    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @ApiModelProperty(value = "不含税的总金额", defaultValue = "0")
    private String allPrice;

    @ApiModelProperty(value = "仓库id")
    private String depotId;

    @ApiModelProperty(value = "调拨时，对方仓库Id")
    private String anotherDepotId;

    @ApiModelProperty(value = "税率", defaultValue = "0")
    private String taxRate;

    @ApiModelProperty(value = "税额", required = "double", defaultValue = "0")
    private String taxMoney;

    @ApiModelProperty(value = "含税单价", required = "double", defaultValue = "0")
    private String taxUnitPrice;

    @ApiModelProperty(value = "价税合计", defaultValue = "0")
    private String taxLastMoney;

    @ApiModelProperty(value = "商品在单据中的类型", required = "num", enumClass = MaterialInOrderType.class)
    private Integer mType;

    @ApiModelProperty(value = "质检类型", required = "num", enumClass = OrderItemQualityInspectionType.class)
    private Integer qualityInspection;

    @ApiModelProperty(value = "质检比例(%)，质检类型为抽检时才生效")
    private String qualityInspectionRatio;

    @ApiModelProperty(value = "交货日期")
    private String deliveryTime;

}
