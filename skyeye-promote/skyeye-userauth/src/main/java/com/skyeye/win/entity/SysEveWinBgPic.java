/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import com.skyeye.win.enums.PicTypeEnum;

/**
 * @ClassName: SysEveWinBgPic
 * @Description: win系统桌面图片实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 22:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "sys:winBgPic", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "sys_eve_win_bg_pic")
@ApiModel("win系统桌面图片实体类")
public class SysEveWinBgPic extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("pic_url")
    @ApiModelProperty(value = "系统图片路径", required = "required")
    private String picUrl;

    @TableField("pic_type")
    @ApiModelProperty(value = "图片类型", required = "required,num", enumClass = PicTypeEnum.class)
    private Integer picType;

}
