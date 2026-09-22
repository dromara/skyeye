/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.follow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 会员门店关注（仅存会员与门店关联关系，展示信息查询时实时组装）
 */
@Data
@TableName(value = "shop_member_store_follow")
@ApiModel("会员门店关注")
public class MemberStoreFollow extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("member_id")
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @TableField("store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;

    @TableField(exist = false)
    @Property(value = "门店上架商品预览")
    private List<Map<String, Object>> goodsList;

}
