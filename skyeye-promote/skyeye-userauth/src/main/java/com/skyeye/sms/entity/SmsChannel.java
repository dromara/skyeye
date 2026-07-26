/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

import com.skyeye.sms.classenum.SmsChannelEnum;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: SmsChannel
 * @Description: 短信渠道
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 22:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "sms:channel", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "skyeye_sms_channel")
@ApiModel("短信模板")
@Accessors(chain = true)
public class SmsChannel extends BaseGeneralInfo {

    @TableField("code_num")
    @ApiModelProperty(value = "渠道编码", required = "required", fuzzyLike = true, enumClass = SmsChannelEnum.class)
    private String codeNum;

    @TableField(exist = false)
    @Property("渠道编码对应的信息")
    private Map<String, Object> codeNumMation;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField("api_key")
    @ApiModelProperty(value = "短信 API 的账号", required = "required")
    private String apiKey;

    @TableField("api_secret")
    @ApiModelProperty(value = "短信 API 的密钥")
    private String apiSecret;

    @TableField("callback_url")
    @ApiModelProperty(value = "短信发送回调 URL")
    private String callbackUrl;

}
