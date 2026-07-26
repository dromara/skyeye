/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionEvaluateType;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrderEvaluate
 * @Description: 设备巡检单评价
 */
@Data
@RedisCacheField(name = "erp:equipmentInspectionOrderEvaluate", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_equipment_inspection_order_evaluate")
@ApiModel("设备巡检单评价实体类")
public class EquipmentInspectionOrderEvaluate extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "评价类型，参考数据字典", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "评价类型信息")
    private Map<String, Object> typeIdMation;

    @TableField(value = "object_id")
    @ApiModelProperty(value = "巡检单id", required = "required")
    private String objectId;

    @TableField(value = "object_key")
    @ApiModelProperty(value = "业务对象key")
    private String objectKey;

    @TableField(value = "content")
    @ApiModelProperty(value = "评价内容", required = "required")
    private String content;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", enumClass = EquipmentInspectionEvaluateType.class, required = "num", defaultValue = "2")
    private Integer type;

    @TableField(exist = false)
    @Property(value = "类型信息")
    private Map<String, Object> typeMation;

}
