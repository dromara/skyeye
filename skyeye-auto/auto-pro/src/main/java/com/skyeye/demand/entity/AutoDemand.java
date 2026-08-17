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

    @TableField("handle_id")
    @ApiModelProperty(value = "处理人id")
    private String handleId;

    @TableField(exist = false)
    @Property(value = "处理人信息")
    private Map<String, Object> handleMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

}


