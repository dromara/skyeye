/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.archives.entity;

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
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Archives
 * @Description: 员工档案实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/20 20:46
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "objectKey", "archivesNumber"})
@RedisCacheField(name = "ehr:archives", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "sys_staff_archives")
@ApiModel("员工档案实体类")
public class Archives extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "archives_number")
    @ApiModelProperty(value = "档案编号", required = "required", fuzzyLike = true)
    private String archivesNumber;

    @TableField(value = "company_id")
    @ApiModelProperty(value = "管理单位id", required = "required")
    private String companyId;

    @TableField(exist = false)
    @Property(value = "管理单位信息")
    private Map<String, Object> companyMation;

    @TableField(value = "custody_place")
    @ApiModelProperty(value = "档案保管地")
    private String custodyPlace;

    @TableField(value = "archives_center")
    @ApiModelProperty(value = "档案室")
    private String archivesCenter;

    @TableField(value = "archives_time")
    @ApiModelProperty(value = "入档时间", required = "required")
    private String archivesTime;

    @TableField(value = "education_id")
    @ApiModelProperty(value = "档案学历id。数据来源：数据字典")
    private String educationId;

    @TableField(value = "whether_archives")
    @ApiModelProperty(value = "是否在档", required = "required,num", enumClass = WhetherEnum.class)
    private Integer whetherArchives;

    @TableField(value = "state")
    @ApiModelProperty(value = "是否有效", required = "required,num", enumClass = WhetherEnum.class)
    private Integer state;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(员工id)", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key(员工key)", required = "required")
    private String objectKey;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosure;

}
