/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.team.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.team.classenum.ObjectPermissionFromType;
import lombok.Data;

/**
 * @ClassName: TeamObjectPermission
 * @Description: 业务对象权限实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/30 0:21
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "team_object_permission")
@ApiModel("业务对象权限实体类")
public class TeamObjectPermission extends OperatorUserInfo {

    @TableId("id")
    @Property("主键id")
    private String id;

    @TableField("team_id")
    @ApiModelProperty(value = "团队id，单独保存时必填(因为有和团队一起的保存)")
    private String teamId;

    @TableField("team_key")
    @ApiModelProperty(value = "团队类型，单独保存时必填(因为有和团队一起的保存)")
    private String teamKey;

    @TableField("object_id")
    @ApiModelProperty(value = "业务对象下面的子业务数据的id，例如：客户下的联系人，一般用于用户A给用户B赋权时有值")
    private String objectId;

    @TableField("object_key")
    @ApiModelProperty(value = "业务对象下面的子业务数据的key，例如：客户下的联系人，一般用于用户A给用户B赋权时有值")
    private String objectKey;

    @TableField("permission_value")
    @ApiModelProperty(value = "权限点，例如：联系人功能，添加，编辑等都是权限点", required = "required")
    private String permissionValue;

    @TableField("permission_key")
    @ApiModelProperty(value = "权限点的key", required = "required")
    private String permissionKey;

    @TableField("owner_id")
    @ApiModelProperty(value = "权限点拥有者id", required = "required")
    private String ownerId;

    @TableField("owner_key")
    @ApiModelProperty(value = "权限点拥有者的key，例如团队角色，用户等", required = "required")
    private String ownerKey;

    @TableField("from_type")
    @ApiModelProperty(value = "权限来源", enumClass = ObjectPermissionFromType.class, required = "required,num")
    private Integer fromType;

}
