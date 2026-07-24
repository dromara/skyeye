/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.incomeandexpense.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.subject.entity.AccountSubject;
import lombok.Data;

import com.skyeye.incomeandexpense.classenum.IncomeAndExpenseType;

/**
 * @ClassName: IncomeAndExpense
 * @Description: 收支信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 12:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "ifs:incomeAndExpense")
@TableName(value = "ifs_income_and_expense", autoResultMap = true)
@ApiModel("收支信息实体类")
public class IncomeAndExpense extends BaseGeneralInfo {

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", required = "required,num", enumClass = IncomeAndExpenseType.class)
    private Integer type;

    @TableField(value = "subject_id")
    @ApiModelProperty(value = "会计科目id")
    private String subjectId;

    @TableField(exist = false)
    @Property(value = "会计科目信息")
    private AccountSubject subjectMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
