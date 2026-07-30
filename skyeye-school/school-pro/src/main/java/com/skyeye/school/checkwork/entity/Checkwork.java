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
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.subject.entity.SubjectClasses;
import lombok.Data;

import java.util.List;

import com.skyeye.school.checkwork.classenum.CheckworkType;

/**
 * @ClassName: Checkwork
 * @Description: 考勤管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/24 10:24
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "school:checkwork", cacheTime = RedisConstants.TEN_DAY_SECONDS)
@TableName(value = "school_checkwork")
@ApiModel(value = "考勤管理实体类")
public class Checkwork extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("sub_class_link_id")
    @ApiModelProperty(value = "科目表与班级表关系id", required = "required")
    private String subClassLinkId;

    @TableField(exist = false)
    @Property("科目表与班级关系信息")
    private SubjectClasses subClassLinkMation;

    @TableField("type")
    @ApiModelProperty(value = "考勤方式", required = "required,num", enumClass = CheckworkType.class)
    private Integer type;

    @TableField("source_code")
    @Property(value = "考勤码")
    private String sourceCode;

    @TableField("qr_code_url")
    @Property(value = "扫码考勤时的二维码地址")
    private String qrCodeUrl;

    @TableField("code_number")
    @ApiModelProperty(value = "数字考勤时设置的数字编码")
    private String codeNumber;

    @TableField("maintain_time")
    @ApiModelProperty(value = "维持时间(分钟)", required = "required,num")
    private Integer maintainTime;

    @TableField(exist = false)
    @Property("打卡的学生信息")
    private List<CheckworkSign> checkworkSignList;

}
