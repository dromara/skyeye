/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.subject.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.subject.classenum.AccountSubjectType;
import com.skyeye.subject.classenum.AmountDirection;
import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: AccountSubject
 * @Description: 会计科目实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 12:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "ifs:accountSubject")
@TableName(value = "ifs_account_subject", autoResultMap = true)
@ApiModel("会计科目实体类")
public class AccountSubject extends BaseGeneralInfo {

    @TableField(value = "num")
    @ApiModelProperty(value = "编号", required = "required")
    private String num;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", required = "required,num", enumClass = AccountSubjectType.class)
    private Integer type;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField("amount_direction")
    @ApiModelProperty(value = "余额方向", required = "required,num", enumClass = AmountDirection.class)
    private Integer amountDirection;

}
