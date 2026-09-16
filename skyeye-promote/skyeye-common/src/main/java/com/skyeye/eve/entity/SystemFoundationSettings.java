/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SystemFoundationSettings
 * @Description: 系统基础设置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/1 22:18
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = CacheConstants.SYSTEM_FOUNDATION_SETTINGS_CACHE_KEY)
@TableName(value = "system_foundation_settings", autoResultMap = true)
@ApiModel("系统基础设置实体类")
public class SystemFoundationSettings extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("email_type")
    @ApiModelProperty(value = "邮箱类型", required = "required")
    private String emailType;

    @TableField("email_receipt_server")
    @ApiModelProperty(value = "收件服务器", required = "required")
    private String emailReceiptServer;

    @TableField("email_receipt_server_port")
    @ApiModelProperty(value = "收件服务器ssl端口", required = "required")
    private String emailReceiptServerPort;

    @TableField("email_send_server")
    @ApiModelProperty(value = "发件服务器", required = "required")
    private String emailSendServer;

    @TableField("email_send_server_port")
    @ApiModelProperty(value = "发件服务器ssl端口", required = "required")
    private String emailSendServerPort;

    @TableField("no_documentary_day_num")
    @ApiModelProperty(value = "未跟单天数，N天未跟单自动进入公海", required = "num")
    private Integer noDocumentaryDayNum;

    @TableField("no_charge_id")
    @ApiModelProperty(value = "未指定负责人，未指定责任人自动进入公海", required = "num")
    private Integer noChargeId;

    @TableField(value = "holidays_type_json", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "企业假期类型以及扣薪信息", required = "json")
    private List<Map<String, Object>> holidaysTypeJson;

    @TableField(value = "year_holidays_mation", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "年假信息", required = "json")
    private List<Map<String, Object>> yearHolidaysMation;

    @TableField(value = "abnormal_mation", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "异常考勤制度管理信息", required = "required,json")
    private List<Map<String, Object>> abnormalMation;

    @TableField(value = "sys_order_basic_design", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "单据审批配置（key/title/serviceClassName/examineSwitch）", required = "json")
    private List<Map<String, Object>> sysOrderBasicDesign;

    @TableField("diary_day_revoke_minute")
    @ApiModelProperty(value = "日报可撤销时间（分钟）", required = "num")
    private Integer diaryDayRevokeMinute;

    @TableField("diary_week_revoke_minute")
    @ApiModelProperty(value = "周报可撤销时间（分钟）", required = "num")
    private Integer diaryWeekRevokeMinute;

    @TableField("diary_month_revoke_minute")
    @ApiModelProperty(value = "月报可撤销时间（分钟）", required = "num")
    private Integer diaryMonthRevokeMinute;

}
