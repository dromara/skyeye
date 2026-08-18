/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.bug.classenum.BugNecessaryToPresent;
import com.skyeye.bug.classenum.BugState;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.environment.entity.AutoEnvironment;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.version.entity.AutoVersion;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: AutoBug
 * @Description: bug管理---objectId就是项目id
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/18 22:00
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "auto:bug", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "auto_bug", autoResultMap = true)
@ApiModel("bug管理")
public class AutoBug extends SkyeyeTeamAuth implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField("no")
    @Property(value = "单据编号")
    private String no;

    @TableField("content")
    @ApiModelProperty(value = "bug内容", required = "required")
    private String content;

    @TableField("state")
    @ApiModelProperty(value = "状态", required = "required", enumClass = BugState.class)
    private String state;

    @TableField("version_id")
    @ApiModelProperty(value = "版本id", required = "required")
    private String versionId;

    @TableField(exist = false)
    @Property(value = "版本信息")
    private AutoVersion versionMation;

    @TableField("demand_id")
    @ApiModelProperty(value = "关联需求id")
    private String demandId;

    @TableField(exist = false)
    @Property(value = "关联需求信息")
    private AutoDemand demandMation;

    @TableField("environment_id")
    @ApiModelProperty(value = "环境id", required = "required")
    private String environmentId;

    @TableField(exist = false)
    @Property(value = "环境信息")
    private AutoEnvironment environmentMation;

    @TableField("handle_id")
    @ApiModelProperty(value = "处理人id")
    private String handleId;

    @TableField(exist = false)
    @Property(value = "处理人信息")
    private Map<String, Object> handleMation;

    @TableField("module_id")
    @ApiModelProperty(value = "模块id", required = "required")
    private String moduleId;

    @TableField(exist = false)
    @Property(value = "模块信息")
    private AutoModule moduleMation;

    @TableField("necessary_to_present")
    @ApiModelProperty(value = "必现类型", required = "required", enumClass = BugNecessaryToPresent.class)
    private String necessaryToPresent;

    @TableField("bug_reason")
    @ApiModelProperty(value = "bug的原因")
    private String bugReason;

    @TableField("bug_reason_type")
    @ApiModelProperty(value = "bug原因的类型，参考数据字典")
    private String bugReasonType;

    @TableField("terminal_occurrence")
    @ApiModelProperty(value = "bug出现的终端，参考数据字典")
    private String terminalOccurrence;

    @TableField("severity")
    @ApiModelProperty(value = "严重性，参考数据字典")
    private String severity;

    @TableField("is_non_issue")
    @ApiModelProperty(value = "是否非问题", required = "required,num", enumClass = IsDefaultEnum.class)
    private Integer isNonIssue;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

}
