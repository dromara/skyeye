/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.voucher.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.voucher.classenum.VoucherState;
import com.skyeye.voucher.classenum.VoucherType;

/**
 * @ClassName: Voucher
 * @Description: 凭证实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 12:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "ifs:voucher")
@TableName(value = "ifs_voucher", autoResultMap = true)
@ApiModel("凭证实体类")
public class Voucher extends BaseGeneralInfo {

    @TableField(value = "path")
    @ApiModelProperty(value = "凭证文件路径")
    private String path;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = VoucherState.class)
    private Integer state;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", required = "required,num", enumClass = VoucherType.class)
    private Integer type;

}
