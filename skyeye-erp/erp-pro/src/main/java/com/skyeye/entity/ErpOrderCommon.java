/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.farm.entity.Farm;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: ErpOrderHead
 * @Description: ERP相关订单实体类，包括：采购入库单
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("ERP相关订单实体类")
public class ErpOrderCommon extends SkyeyeFlowable {

    @TableField(value = "type", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "出入库类型", enumClass = DepotPutOutType.class)
    private Integer type;

    /**
     * 订单类型，每个服务类的serviceClassName
     */
    @TableField(value = "id_key", updateStrategy = FieldStrategy.NEVER)
    private String idKey;

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("salesman")
    @ApiModelProperty(value = "业务员id")
    private String salesman;

    @TableField(exist = false)
    @Property(value = "业务员信息")
    private Map<String, Object> salesmanMation;

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型")
    private Integer fromTypeId;

    @TableField(value = "from_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField("real_complate_time")
    @Property(value = "实际完成时间")
    private String realComplateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品列表,json串", required = "required,json")
    private List<ErpOrderItem> erpOrderItemList;

    @TableField("quality_inspection")
    @Property(value = "质检类型", enumClass = OrderQualityInspectionType.class)
    private Integer qualityInspection;

    @TableField("need_depot")
    @ApiModelProperty(value = "是否需要出入库", enumClass = WhetherEnum.class)
    private Integer needDepot;

    @TableField("other_state")
    @Property("其他状态信息，根据单据类型不同，状态信息表达含义不同。")
    private Integer otherState;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间id")
    private String farmId;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;

}
