/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import com.skyeye.personnel.classenum.UserInstallMenuSize;
import com.skyeye.personnel.classenum.UserInstallTaskPosition;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: SysEveUserInstall
 * @Description: 用户个人配置信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 11:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user_install")
@ApiModel("用户个人配置信息")
public class SysEveUserInstall extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("user_id")
    @Property(value = "用户id")
    private String userId;

    @TableField("win_bg_pic_url")
    @Property(value = "win系统背景图片")
    private String winBgPicUrl;

    @TableField("win_bg_pic_vague")
    @Property(value = "背景图片是否雾化  1正常0模糊")
    private String winBgPicVague;

    @TableField("win_bg_pic_vague_value")
    @Property(value = "雾化值  1~9")
    private Integer winBgPicVagueValue;

    @TableField("win_lock_bg_pic_url")
    @Property(value = "win系统锁屏时的背景图片")
    private String winLockBgPicUrl;

    @TableField("win_theme_color")
    @Property(value = "win系统的主题颜色")
    private String winThemeColor;

    @TableField("win_bottom_menu_icon")
    @Property(value = "是否只展示图标", enumClass = WhetherEnum.class)
    private String winBottomMenuIcon;

    @TableField("win_start_menu_size")
    @Property(value = "开始菜单尺寸", enumClass = UserInstallMenuSize.class)
    private String winStartMenuSize;

    @TableField("win_task_position")
    @Property(value = "任务栏在屏幕的位置", enumClass = UserInstallTaskPosition.class)
    private String winTaskPosition;

}
