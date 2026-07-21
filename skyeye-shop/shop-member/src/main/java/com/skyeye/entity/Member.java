/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.AreaInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.level.entity.ShopMemberLevel;
import com.skyeye.store.entity.ShopStore;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Member
 * @Description: 会员实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/2 21:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"phone"})
@RedisCacheField(name = CacheConstants.SHOP_MEMBER_CACHE_KEY)
@TableName(value = "sys_member", autoResultMap = true)
@ApiModel("会员实体类")
public class Member extends AreaInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField(value = "avatar")
    @ApiModelProperty(value = "头像")
    private String avatar;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(value = "phone")
    @ApiModelProperty(value = "联系电话", required = "required,phone")
    private String phone;

    @TableField("password")
    @ApiModelProperty(value = "密码")
    private String password;

    @TableField("pwd_num_enc")
    @Property(value = "用户密码加密次数")
    private Integer pwdNumEnc;

    @TableField("level_id")
    @Property(value = "会员等级id")
    private String levelId;

    @TableField(exist = false)
    @Property(value = "会员等级信息")
    private ShopMemberLevel levelMation;

    @TableField("wechat_open_id")
    @ApiModelProperty(value = "微信的openId")
    private String wechatOpenId;

    @TableField(value = "email")
    @ApiModelProperty(value = "电子邮箱", required = "email")
    private String email;

    @TableField("enabled")
    @ApiModelProperty(value = "状态，参考#EnableEnum", required = "required,num")
    private Integer enabled;

    @TableField(value = "store_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "如果是门店工作人员录入，则为门店id")
    private String storeId;

    @TableField(exist = false)
    @ApiModelProperty(value = "门店信息")
    private ShopStore storeMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField(exist = false)
    @Property(value = "用户token")
    private String userToken;

    @TableField(exist = false)
    @Property(value = "是否已设置密码", enumClass = WhetherEnum.class)
    private Integer whetherPassword;

}
