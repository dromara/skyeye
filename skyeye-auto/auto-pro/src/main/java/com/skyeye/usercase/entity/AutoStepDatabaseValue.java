/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: AutoStepDatabaseValue
 * @Description: 用例步骤关联的数据库取值实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_step_database_value")
@ApiModel(value = "用例步骤关联的数据库取值实体类")
public class AutoStepDatabaseValue extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("step_database_id")
    @ApiModelProperty(value = "用例步骤-关联的数据库id")
    private String stepDatabaseId;

    /**
     * 写入步骤结果集的字段别名。
     * 执行后 result[步骤resultKey] 为行列表，每行形如 { 别名: 列值 }。
     * 后续断言/入参可用：步骤resultKey[0].别名。
     */
    @TableField("`key`")
    @ApiModelProperty(value = "结果别名，后续用 步骤resultKey[0].别名 引用", required = "required")
    private String key;

    /**
     * SQL 查询结果中的列名（非 JsonPath）。引擎用 metaDataRow.getCell(value) 取值后按 key 写入结果。
     */
    @TableField("value")
    @ApiModelProperty(value = "SQL结果列名（与SELECT返回列名一致）", required = "required")
    private String value;

    @TableField("case_id")
    @Property(value = "所属用例")
    private String caseId;
}
