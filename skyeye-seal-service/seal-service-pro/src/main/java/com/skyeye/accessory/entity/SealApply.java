/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SealApply
 * @Description: 配件申请单实体类；otherState 出入库状态参考 {@link com.skyeye.depot.classenum.DepotOutState}
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/17 17:13
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "seal:apply", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_apply")
@ApiModel("配件申请单实体类")
public class SealApply extends SkyeyeFlowable {

    @TableField(value = "object_id")
    @ApiModelProperty(value = "工单id")
    private String objectId;

    @TableField(value = "object_key")
    @ApiModelProperty(value = "工单的key")
    private String objectKey;

    @TableField(value = "apply_time")
    @ApiModelProperty(value = "申领日期", required = "required")
    private String applyTime;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总金额")
    private String allPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "配件申领明细", required = "required,json")
    private List<ApplyLink> applyLinkList;

    @TableField("other_state")
    @Property(value = "出入库状态")
    private Integer otherState;

}
