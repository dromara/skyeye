/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
@TableName(value = "tenant_token_daily_usage")
@ApiModel("租户 Token 日用量")
public class TenantTokenDailyUsage extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("tenant_id")
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @TableField(exist = false)
    @Property("租户信息")
    private Tenant tenantMation;

    @TableField("usage_date")
    @ApiModelProperty(value = "用量日期 yyyy-MM-dd")
    private String usageDate;

    @TableField("prompt_tokens")
    @Property("提问 Token")
    private Long promptTokens;

    @TableField("completion_tokens")
    @Property("回答 Token")
    private Long completionTokens;

    @TableField("total_tokens")
    @Property("合计 Token")
    private Long totalTokens;

    @TableField("call_count")
    @Property("调用次数")
    private Integer callCount;

}
