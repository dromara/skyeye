/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.quit.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Quit
 * @Description: 离职申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-25 18:08:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "boss:quit", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "boss_interview_quit", autoResultMap = true)
@ApiModel("离职申请实体类")
public class Quit extends SkyeyeFlowable {

    @TableField(value = "leave_time")
    @ApiModelProperty(value = "申请离职的日期", required = "required")
    private String leaveTime;

    @TableField(value = "leave_type")
    @ApiModelProperty(value = "离职类型，参考#UserQuitType", required = "required")
    private String leaveType;

    @TableField(value = "remark")
    @ApiModelProperty(value = "离职原因", required = "required")
    private String remark;

    @TableField(value = "manager_transfer_staff_id")
    @ApiModelProperty(value = "经理转让交接人(用户id)")
    private String managerTransferUserId;

    @TableField(exist = false)
    @Property(value = "经理转让交接人信息")
    private Map<String, Object> managerTransferUserMation;

}
