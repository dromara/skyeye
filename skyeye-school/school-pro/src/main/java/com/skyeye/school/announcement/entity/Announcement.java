package com.skyeye.school.announcement.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Announcement
 * @Description: 公告管理实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@RedisCacheField(name = "school:announcement", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_announcement")
@ApiModel(value = "公告管理实体类")
public class Announcement extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的id")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目数据的serviceClassName")
    private String objectKey;

    @TableField(value = "subject_classes_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "科目表与班级表的关系id", required = "required")
    private String subjectClassesId;

    @TableField("title")
    @ApiModelProperty(value = "公告标题",required = "required")
    private String title;

    @TableField("content")
    @ApiModelProperty(value = "公告内容",required = "required")
    private String content;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField("is_confirm")
    @ApiModelProperty(value = "公告是否需要确认", required = "required,num", enumClass = EnableEnum.class)
    private Integer isConfirm;

    @TableField(exist = false)
    @Property(value = "确认状态信息",enumClass = EnableEnum.class)
    private Map<String, Object> confirmMation;

    @TableField("confirm_num")
    @ApiModelProperty(value = "确认的人数")
    private int confirmNum;

    @TableField("un_confirm_num")
    @ApiModelProperty(value = "未确认的人数")
    private int unConfirmNum;

    @TableField(exist = false)
    @Property(value = "检测当前登录人是否确认公告")
    private Boolean checkConfirm;
}
