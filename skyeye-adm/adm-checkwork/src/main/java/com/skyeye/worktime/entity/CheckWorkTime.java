/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.AreaInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.worktime.classenum.CheckWorkTimeType;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: CheckWorkTime
 * @Description: 考勤班次实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.CHECK_WORK_TIME_CACHE_KEY)
@TableName(value = "check_work_time", autoResultMap = true)
@ApiModel("考勤班次实体类")
public class CheckWorkTime extends AreaInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "考勤开始时间", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "考勤结束时间", required = "required")
    private String endTime;

    @TableField(value = "rest_start_time")
    @ApiModelProperty(value = "作息开始时间")
    private String restStartTime;

    @TableField(value = "rest_end_time")
    @ApiModelProperty(value = "作息结束时间")
    private String restEndTime;

    @TableField(value = "type")
    @ApiModelProperty(value = "时间段类型", enumClass = CheckWorkTimeType.class, required = "required,num")
    private Integer type;

    @TableField(exist = false)
    @Property("时间段类型名称")
    private String typeName;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "online_clock_enabled")
    @ApiModelProperty(value = "是否开启线上打卡", enumClass = EnableEnum.class, required = "required,num")
    private Integer onlineClockEnabled;

    @TableField(value = "web_clock_enabled")
    @ApiModelProperty(value = "是否开启网站端打卡", enumClass = EnableEnum.class, required = "required,num")
    private Integer webClockEnabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "考勤班次关联的时间段", required = "required,json")
    private List<CheckWorkTimeWeek> checkWorkTimeWeekList;

    @TableField(exist = false)
    @Property(value = "每个月工作的日期")
    private List<String> workDays;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度")
    private String latitude;

}
