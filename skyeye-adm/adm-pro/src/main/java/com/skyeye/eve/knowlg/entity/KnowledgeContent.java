/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.knowlg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.eve.knowlg.classenum.KnowlgContentState;

/**
 * @ClassName: KnowledgeContent
 * @Description: 知识库实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/14 11:06
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "knowledge:content", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "knowlg_content")
@ApiModel("知识库实体类")
public class KnowledgeContent extends BaseGeneralInfo {

    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "分类id", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "分类信息")
    private Map<String, Object> typeMation;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = KnowlgContentState.class)
    private Integer state;

    @TableField(value = "examine_nopass_reason")
    @ApiModelProperty(value = "审核不通过时的原因")
    private String examineNopassReason;

    @TableField(value = "examine_id")
    @Property("审核人id")
    private String examineId;

    @TableField(exist = false)
    @Property("审核人信息")
    private Map<String, Object> examineMation;

    @TableField(value = "examine_time")
    @Property("审核时间")
    private String examineTime;

    @TableField(value = "read_num")
    @Property("阅读量")
    private String readNum;

    @TableField(value = "label")
    @ApiModelProperty(value = "知识库标签")
    private String label;

    @TableField(exist = false)
    @ApiModelProperty(value = "上传文件创建知识库")
    private String filePath;

}
