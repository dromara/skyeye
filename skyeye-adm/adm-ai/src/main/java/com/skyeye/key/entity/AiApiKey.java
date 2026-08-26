/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.key.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.knowledge.AiKnowledgeConfig;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.role.entity.Role;
import lombok.Data;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai配置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "skyeye_ai_api_key")
@ApiModel("API配置")
public class AiApiKey extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "api_app_id")
    @ApiModelProperty(value = "appId")
    private String apiAppId;

    @TableField(value = "`api_key`")
    @ApiModelProperty(value = "密钥（豆包：方舟对话 ark- Key；通义：AccessKeyId）")
    private String apiKey;

    @TableField(value = "`secret_key`")
    @ApiModelProperty(value = "secretKey（豆包：知识库 VIKING_API_KEY；通义：AccessKeySecret）")
    private String secretKey;

    @TableField(value = "`enabled`")
    @ApiModelProperty(value = "状态", required = "required")
    private String enabled;

    @TableField(value = "`platform`")
    @ApiModelProperty(value = "ai平台", enumClass = AiPlatformEnum.class, required = "required")
    private String platform;

    @TableField(value = "`url`")
    @ApiModelProperty(value = "API 地址")
    private String url;

    @TableField(value = "platform_knowledge_id")
    @ApiModelProperty(value = "平台知识库ID（百炼IndexId/千帆KB/豆包resource_id/讯飞repo）")
    private String platformKnowledgeId;

    @TableField(value = "platform_workspace_id")
    @ApiModelProperty(value = "通义业务空间ID（仅通义需要）")
    private String platformWorkspaceId;

    @TableField(value = "platform_category_id")
    @ApiModelProperty(value = "通义类目ID（仅通义需要）")
    private String platformCategoryId;

    @TableField(value = "`role_id`")
    @ApiModelProperty(value = "角色id")
    private String roleId;

    @TableField(exist = false)
    @Property("AI角色")
    private Role roleMation;

    /** 组装平台知识库调用参数 */
    public AiKnowledgeConfig toAiKnowledgeConfig() {
        return AiKnowledgeConfig.builder()
            .knowledgeId(platformKnowledgeId)
            .apiKey(apiKey)
            .secretKey(secretKey)
            .appId(apiAppId)
            .workspaceId(platformWorkspaceId)
            .categoryId(platformCategoryId)
            .build();
    }
}
