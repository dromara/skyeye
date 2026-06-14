/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.checkwork.classenum.ClockInTime;
import com.skyeye.checkwork.classenum.ClockOutTime;
import com.skyeye.checkwork.classenum.ClockSource;
import com.skyeye.checkwork.classenum.ClockState;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.worktime.entity.CheckWorkTime;
import lombok.Data;

/**
 * @ClassName: CheckWork
 * @Description: 考勤打卡实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "checkwork:check", cacheTime = RedisConstants.ONE_WEEK_SECONDS)
@TableName(value = "check_work", autoResultMap = true)
@ApiModel("考勤打卡实体类")
public class CheckWork extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @Property(value = "名称")
    private String name;

    @TableField(value = "time_id")
    @ApiModelProperty(value = "班次id", required = "required")
    private String timeId;

    @TableField(exist = false)
    @Property(value = "班次信息")
    private CheckWorkTime timeMation;

    @TableField(value = "check_date")
    @ApiModelProperty(value = "考勤日期 格式 ：yyyy-mm-dd", required = "required")
    private String checkDate;

    @TableField(value = "state")
    @Property(value = "考勤状态", enumClass = ClockState.class)
    private Integer state;

    @TableField(value = "clock_in")
    @ApiModelProperty(value = "上班打卡时间  格式：hh:mm:ss")
    private String clockIn;

    @TableField(value = "clock_in_state")
    @ApiModelProperty(value = "上班打卡状态", enumClass = ClockInTime.class, required = "required,num")
    private Integer clockInState;

    @TableField(value = "clock_out")
    @ApiModelProperty(value = "下班打卡时间  格式：hh:mm:ss")
    private String clockOut;

    @TableField(value = "clock_out_state")
    @ApiModelProperty(value = "下班打卡状态", enumClass = ClockOutTime.class, required = "required,num")
    private Integer clockOutState;

    @TableField(value = "work_hours")
    @Property(value = "工时  格式：4:15:00")
    private String workHours;

    @TableField(value = "clock_in_ip")
    @ApiModelProperty(value = "上班打卡IP")
    private String clockInIp;

    @TableField(value = "clock_out_ip")
    @ApiModelProperty(value = "下班打卡IP")
    private String clockOutIp;

    @TableField(value = "create_id")
    @Property(value = "打卡人id")
    private String createId;

    @TableField(exist = false)
    @Property("创建人姓名")
    private String createName;

    @TableField(value = "clock_in_longitude")
    @ApiModelProperty(value = "上班打卡的经度")
    private String clockInLongitude;

    @TableField(value = "clock_in_latitude")
    @ApiModelProperty(value = "上班打卡的纬度")
    private String clockInLatitude;

    @TableField(value = "clock_in_address")
    @ApiModelProperty(value = "上班打卡的地址")
    private String clockInAddress;

    @TableField(value = "clock_in_source")
    @ApiModelProperty(value = "上班打卡来源", enumClass = ClockSource.class)
    private String clockInSource;

    @TableField(value = "clock_out_longitude")
    @ApiModelProperty(value = "下班打卡的经度")
    private String clockOutLongitude;

    @TableField(value = "clock_out_latitude")
    @ApiModelProperty(value = "下班打卡的纬度")
    private String clockOutLatitude;

    @TableField(value = "clock_out_address")
    @ApiModelProperty(value = "下班打卡的地址")
    private String clockOutAddress;

    @TableField(value = "clock_out_source")
    @ApiModelProperty(value = "下班打卡来源", enumClass = ClockSource.class)
    private String clockOutSource;

}
