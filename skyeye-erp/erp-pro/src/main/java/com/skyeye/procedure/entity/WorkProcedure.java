/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.procedure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.procedure.classenum.ProcedureCapacitySubject;

/**
 * @ClassName: WorkProcedure
 * @Description: 工序信息
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/23 21:14
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_PROCEDURE_CACHE_KEY)
@TableName(value = "erp_work_procedure", autoResultMap = true)
@ApiModel("工序信息实体类")
public class WorkProcedure extends BaseGeneralInfo {

    @TableField(value = "number")
    @ApiModelProperty(value = "工序编号", required = "required")
    private String number;

    @TableField(value = "content")
    @ApiModelProperty(value = "工序内容")
    private String content;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "工序类别id，数据字典", required = "required")
    private String typeId;

    @TableField(value = "charge_id")
    @ApiModelProperty(value = "负责人id")
    private String chargeId;

    @TableField(exist = false)
    @Property(value = "负责人信息")
    private Map<String, Object> chargeMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(value = "capacity_subject")
    @ApiModelProperty(value = "产能主体", required = "required,num", enumClass = ProcedureCapacitySubject.class)
    private Integer capacitySubject;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备清单信息", required = "required,json")
    private List<WorkProcedureEquipment> workProcedureEquipmentList;

}
