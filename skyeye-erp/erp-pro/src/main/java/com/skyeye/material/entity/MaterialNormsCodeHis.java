/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import com.skyeye.material.classenum.MaterialNormsCodeType;

/**
 * @ClassName: MaterialNormsCodeHis
 * @Description: 商品规格一物一码条形码变更历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/11 17:08
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_material_norms_code_his")
@ApiModel("商品规格一物一码条形码变更历史实体类")
public class MaterialNormsCodeHis extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "norms_code_id", updateStrategy = FieldStrategy.NEVER)
    @Property("条形码id")
    private String normsCodeId;

    @TableField(value = "type")
    @Property(value = "类型", enumClass = MaterialNormsCodeType.class)
    private Integer type;

    @TableField(value = "operator_id")
    @Property(value = "操作单据id")
    private String operatorId;

    @TableField(value = "operator_key")
    @Property(value = "操作单据的key")
    private String operatorKey;

    @TableField(value = "operator_time")
    @Property(value = "操作时间")
    private String operatorTime;

}
