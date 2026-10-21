package com.skyeye.school.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: FloorInfo
 * @Description: 楼层教室服务管理实体层
 * @author: lqu
 * @date: 2023/9/5 17:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName("school_floor_info")
@ApiModel(description = "楼层教室服务管理实体类")
public class FloorInfo extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("location_id")
    @ApiModelProperty(value = "所属地点id",required = "required")
    private String locationId;

    @TableField("name")
    @ApiModelProperty(value = "名称",required = "required", fuzzyLike = true)
    private String name;

    @TableField("node_type")
    @ApiModelProperty(value = "节点，1楼层，2教室、3服务",required = "required")
    private Integer nodeType;

    @TableField("parent_id")
    @ApiModelProperty(value = "父级id",defaultValue = "0",required = "required")
    private String parentId;

    @TableField("level")
    @ApiModelProperty(value = "层级标识")
    private Integer  level;

    @TableField("status")
    @ApiModelProperty(value = "状态，1：正常，2：禁用",enumClass = EnableEnum.class)
    private Integer status;

    @TableField("sort_order")
    @ApiModelProperty(value = "排序id,默认1")
    private Integer sortOrder;

    @TableField("floor_index")
    @ApiModelProperty(value = "楼层序号，地上从1开始，地下为负数。仅楼层节点使用")
    private Integer floorIndex;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("model_color")
    @ApiModelProperty(value = "3D模型颜色，如 #13c2c2；仅教室/房间节点使用")
    private String modelColor;

    @TableField("logo")
    @ApiModelProperty(value = "房间logo相对路径；仅教室/房间节点使用")
    private String logo;

    @TableField("space_kind")
    @ApiModelProperty(value = "空间类型：0普通房间 1楼梯 2电梯；仅房间节点使用")
    private Integer spaceKind;

    @TableField("link_group_id")
    @ApiModelProperty(value = "竖向通道分组id，跨层楼梯/电梯共用同一值")
    private String linkGroupId;

    @TableField("space_dir")
    @ApiModelProperty(value = "楼梯上楼方向：0正向 1反向（沿轮廓长边翻转）")
    private Integer spaceDir;
}
