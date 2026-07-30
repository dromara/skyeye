/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.post.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.comment.entity.Comment;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.picture.entity.Picture;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Post
 * @Description: 帖子实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "wall:post", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "wall_post")
@ApiModel(value = "帖子实体类")
public class Post extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("content")
    @ApiModelProperty(value = "发帖内容", required = "required")
    private String content;

    @TableField("title")
    @ApiModelProperty(value = "发帖标题", required = "required")
    private String title;

    @TableField("ip")
    @Property(value = "IP属地")
    private String ip;

    @TableField("anonymity")
    @ApiModelProperty(value = "是否匿名", required = "required,num", enumClass = WhetherEnum.class)
    private Integer anonymity;

    @TableField("city")
    @Property(value = "城市")
    private String city;

    @TableField("address")
    @Property(value = "地址")
    private String address;

    @TableField("upvote_num")
    @Property(value = "点赞数量")
    private String upvoteNum;

    @TableField("comment_num")
    @Property(value = "评论数量")
    private String commentNum;

    @TableField("view_num")
    @Property(value = "浏览数量")
    private String viewNum;

    @TableField("share_num")
    @ApiModelProperty(value = "分享数量，默认0", defaultValue = "0")
    private String shareNum;

    @TableField("type_id")
    @ApiModelProperty(value = "帖子类型")
    private String typeId;

    @TableField("circle_id")
    @ApiModelProperty(value = "圈子id")
    private String circleId;

    @TableField(exist = false)
    @Property(value = "圈子信息")
    private Map<String,Object> circleMation;

    @TableField("login_identity")
    @Property(value = "登录身份")
    private String loginIdentity;

    @TableField(exist = false)
    @ApiModelProperty(value = "九宫格图片", required = "json")
    private List<Picture> pictureList;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论列表", required = "json")
    private List<Comment> commentList;

    @TableField(exist = false)
    @Property(value = "当前登陆人是否点赞")
    private Boolean checkUpvote;

    @TableField(exist = false)
    @Property(value = "业务逻辑对象")
    private String objectKey;
}