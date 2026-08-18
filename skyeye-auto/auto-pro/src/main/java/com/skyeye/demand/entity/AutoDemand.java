/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.demand.classenum.AutoDemandRoleStateEnum;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.version.entity.AutoVersion;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: AutoDemand
 * @Description: 需求表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "auto:demand", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "auto_demand", autoResultMap = true)
@ApiModel("需求表实体类")
public class AutoDemand extends SkyeyeTeamAuth implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty("编号，主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField(value = "no")
    @Property(value = "单据编号", fuzzyLike = true)
    private String no;

    @TableField(value = "content")
    @ApiModelProperty(value = "需求内容")
    private String content;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", required = "required", enumClass = AutoDemandStateEnum.class)
    private String state;

    @TableField(value = "version_id")
    @ApiModelProperty(value = "版本id", required = "required")
    private String versionId;

    @TableField(exist = false)
    @Property(value = "版本信息")
    private AutoVersion versionMation;

    @TableField(value = "module_id")
    @ApiModelProperty(value = "模块id", required = "required")
    private String moduleId;

    @TableField(exist = false)
    @Property(value = "模块信息")
    private AutoModule moduleMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField("test_join_analysis")
    @ApiModelProperty(value = "测试是否参与需求分析", required = "required,num", enumClass = IsDefaultEnum.class)
    private Integer testJoinAnalysis;

    @TableField("total_score")
    @ApiModelProperty(value = "总积分", required = "required")
    private String totalScore;

    @TableField("front_ratio")
    @ApiModelProperty(value = "前端积分比例", required = "required")
    private Integer frontRatio;

    @TableField("back_ratio")
    @ApiModelProperty(value = "后端积分比例", required = "required")
    private Integer backRatio;

    @TableField("test_ratio")
    @ApiModelProperty(value = "测试积分比例", required = "required")
    private Integer testRatio;

    @TableField(exist = false)
    @Property(value = "未分配积分")
    private String unallocatedScore;

    @TableField("front_handle_id")
    @ApiModelProperty(value = "前端负责人id")
    private String frontHandleId;

    @TableField(exist = false)
    @Property(value = "前端负责人信息")
    private Map<String, Object> frontHandleMation;

    @TableField("front_estimate_start_time")
    @ApiModelProperty(value = "前端预计开始时间")
    private String frontEstimateStartTime;

    @TableField("front_estimate_end_time")
    @ApiModelProperty(value = "前端预计结束时间")
    private String frontEstimateEndTime;

    @TableField("front_actual_start_time")
    @Property(value = "前端实际开始时间")
    private String frontActualStartTime;

    @TableField("front_actual_end_time")
    @Property(value = "前端实际结束时间")
    private String frontActualEndTime;

    @TableField("front_state")
    @ApiModelProperty(value = "前端状态", enumClass = AutoDemandRoleStateEnum.class)
    private String frontState;

    @TableField("front_init_score")
    @ApiModelProperty(value = "前端初始积分")
    private String frontInitScore;

    @TableField("front_earned_score")
    @ApiModelProperty(value = "前端已获得积分")
    private String frontEarnedScore;

    @TableField("back_handle_id")
    @ApiModelProperty(value = "后端负责人id")
    private String backHandleId;

    @TableField(exist = false)
    @Property(value = "后端负责人信息")
    private Map<String, Object> backHandleMation;

    @TableField("back_estimate_start_time")
    @ApiModelProperty(value = "后端预计开始时间")
    private String backEstimateStartTime;

    @TableField("back_estimate_end_time")
    @ApiModelProperty(value = "后端预计结束时间")
    private String backEstimateEndTime;

    @TableField("back_actual_start_time")
    @Property(value = "后端实际开始时间")
    private String backActualStartTime;

    @TableField("back_actual_end_time")
    @Property(value = "后端实际结束时间")
    private String backActualEndTime;

    @TableField("back_state")
    @ApiModelProperty(value = "后端状态", enumClass = AutoDemandRoleStateEnum.class)
    private String backState;

    @TableField("back_init_score")
    @ApiModelProperty(value = "后端初始积分")
    private String backInitScore;

    @TableField("back_earned_score")
    @ApiModelProperty(value = "后端已获得积分")
    private String backEarnedScore;

    @TableField("test_handle_id")
    @ApiModelProperty(value = "测试负责人id")
    private String testHandleId;

    @TableField(exist = false)
    @Property(value = "测试负责人信息")
    private Map<String, Object> testHandleMation;

    @TableField("test_estimate_start_time")
    @ApiModelProperty(value = "测试预计开始时间")
    private String testEstimateStartTime;

    @TableField("test_estimate_end_time")
    @ApiModelProperty(value = "测试预计结束时间")
    private String testEstimateEndTime;

    @TableField("test_actual_start_time")
    @Property(value = "测试实际开始时间")
    private String testActualStartTime;

    @TableField("test_actual_end_time")
    @Property(value = "测试实际结束时间")
    private String testActualEndTime;

    @TableField("test_state")
    @ApiModelProperty(value = "测试状态", enumClass = AutoDemandRoleStateEnum.class)
    private String testState;

    @TableField("test_init_score")
    @ApiModelProperty(value = "测试初始积分")
    private String testInitScore;

    @TableField("test_earned_score")
    @ApiModelProperty(value = "测试已获得积分")
    private String testEarnedScore;

}


