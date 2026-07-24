/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.family.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.SexEnum;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Family
 * @Description: 员工家庭情况实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 11:45
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "ehr:family", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "sys_staff_family")
@ApiModel("员工家庭情况实体类")
public class Family extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "relationship_id")
    @ApiModelProperty(value = "与本人关系id，参考数据字典", required = "required")
    private String relationshipId;

    @TableField(exist = false)
    @Property(value = "与本人关系信息")
    private Map<String, Object> relationshipMation;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "姓名", required = "required")
    private String name;

    @TableField(value = "card_type")
    @ApiModelProperty(value = "证件类型id，参考数据字典", required = "required")
    private String cardType;

    @TableField(exist = false)
    @Property(value = "证件类型信息")
    private Map<String, Object> cardTypeMation;

    @TableField(value = "card_number")
    @ApiModelProperty(value = "证件号码", required = "required")
    private String cardNumber;

    @TableField(value = "sex")
    @ApiModelProperty(value = "性别", required = "required", enumClass = SexEnum.class)
    private Integer sex;

    @TableField(value = "work_unit")
    @ApiModelProperty(value = "工作单位", required = "required")
    private String workUnit;

    @TableField(value = "job")
    @ApiModelProperty(value = "职位", required = "required")
    private String job;

    @TableField(value = "politic_id")
    @ApiModelProperty(value = "政治面貌id，参考数据字典", required = "required")
    private String politicId;

    @TableField(exist = false)
    @Property(value = "政治面貌信息")
    private Map<String, Object> politicMation;

    @TableField(value = "emergency_contact")
    @ApiModelProperty(value = "是否紧急联系人", required = "required", enumClass = WhetherEnum.class)
    private String emergencyContact;

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
