/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inspection.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.inspection.classenum.QualityInspectionExchangesState;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.inspection.classenum.QualityInspectionFromType;
import com.skyeye.inspection.classenum.QualityInspectionPutState;
import com.skyeye.inspection.classenum.QualityInspectionReturnState;

/**
 * @ClassName: QualityInspection
 * @Description: 质检单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 8:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "erp:order:qualityInspection", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_quality_inspection")
@ApiModel("质检单实体类")
public class QualityInspection extends SkyeyeFlowable {

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField(value = "from_type_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据类型", enumClass = QualityInspectionFromType.class)
    private Integer fromTypeId;

    @TableField(value = "from_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> fromMation;

    @TableField("department_id")
    @ApiModelProperty(value = "检验部门")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField("put_state")
    @Property(value = "入库状态", enumClass = QualityInspectionPutState.class)
    private Integer putState;

    @TableField("return_state")
    @Property(value = "退货状态", enumClass = QualityInspectionReturnState.class)
    private Integer returnState;

    @TableField("exchanges_state")
    @Property(value = "换货状态", enumClass = QualityInspectionExchangesState.class)
    private Integer exchangesState;

    @TableField(exist = false)
    @ApiModelProperty(value = "质检单明细信息", required = "required,json")
    private List<QualityInspectionItem> qualityInspectionItemList;

    @TableField("holder_id")
    @ApiModelProperty(value = "关联的客户/供应商/会员id", required = "required")
    private String holderId;

    @TableField(exist = false)
    @Property(value = "关联的客户/供应商/会员信息")
    private Map<String, Object> holderMation;

    @TableField("holder_key")
    @ApiModelProperty(value = "关联的客户/供应商/会员的className")
    private String holderKey;

}
