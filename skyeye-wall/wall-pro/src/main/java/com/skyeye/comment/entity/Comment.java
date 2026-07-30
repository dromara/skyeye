/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.comment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.user.entity.User;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Comment
 * @Description: 评论实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_comment")
@ApiModel(value = "评论实体类")
public class Comment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("content")
    @ApiModelProperty(value = "评论内容")
    private String content;

    @TableField("ip")
    @Property(value = "IP属地")
    private String ip;

    @TableField("upvote_num")
    @Property(value = "点赞数量")
    private String upvoteNum;

    @TableField("anonymity")
    @ApiModelProperty(value = "是否匿名", required = "required,num", enumClass = WhetherEnum.class)
    private Integer anonymity;

    @TableField("comment_id")
    @ApiModelProperty(value = "被评论的评论id")
    private String commentId;

    @TableField("parent_id")
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    @TableField("post_id")
    @ApiModelProperty(value = "被评论的帖子id", required = "required")
    private String postId;

    @TableField("user_id")
    @Property(value = "被回复人id")
    private String userId;

    @TableField("login_identity")
    @Property(value = "登录身份")
    private String loginIdentity;

    @TableField(exist = false)
    @Property(value = "被回复人信息")
    private User userMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论图片", required = "json")
    private Map<String, Object> picture;

    @TableField(exist = false)
    @Property(value = "当前登陆人是否点赞")
    private Boolean checkUpvote;

}