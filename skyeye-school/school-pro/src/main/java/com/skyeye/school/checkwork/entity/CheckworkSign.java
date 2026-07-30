/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.checkwork.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.school.checkwork.classenum.CheckworkSignState;

/**
 * @ClassName: CheckworkSign
 * @Description: 学生考勤签到实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/24 10:41
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "school_checkwork_sign")
@ApiModel(value = "学生考勤签到实体类")
public class CheckworkSign extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("checkwork_id")
    @ApiModelProperty(value = "签到信息id", required = "required")
    private String checkworkId;

    @TableField(exist = false)
    @Property("签到信息")
    private Checkwork checkworkMation;

    @TableField("user_id")
    @Property(value = "学生id")
    private String userId;

    @TableField(exist = false)
    @Property(value = "学生信息")
    private Map<String, Object> userMation;

    @TableField("sign_time")
    @Property(value = "签到时间")
    private String signTime;

    @TableField("state")
    @Property(value = "签到状态", enumClass = CheckworkSignState.class)
    private Integer state;

}
