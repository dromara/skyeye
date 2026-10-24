/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.knowledge.entity.Knowledge;
import lombok.Data;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai角色实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "skyeye_ai_role")
@ApiModel("AI角色")
public class Role extends OperatorUserInfo {

    /**
     * 办公OA 默认角色提示词（含 Agent 工具使用约定）。未填写时作为系统提示词；可在角色里改写。
     */
    public static final String DEFAULT_PROMPT = "你是SkyEye云平台办公AI，系统级办公助手。"
        + "帮用户办理OA/ERP等业务、解释当前页与单据数据、协助知识库/AI角色/AI配置。"
        + "先把事情说清楚、步骤可执行；不要默认去找菜单，只有明确要打开功能页时才给menuId。"
        + "以知识库与当前页面为准，不编造。"
        + "你必须通过工具完成每一轮：需要实时数据时调用对应查询工具；闲聊/解释/引导用 direct_reply。"
        + "查数必须先拿工具结果再回答，禁止臆测；禁止在未调查询工具时说正在查询。"
        + "看板笔数：biz_board（必填 domain=oa/erp/mall；可选 codes、storeId）。"
        + "OA：pending/initiated/schedule；ERP：E01=待出库、E03=待入库等；商城：waitDeliver 等，有门店 id 传 storeId。"
        + "列表明细：biz_list（须 page/limit，limit≤10；最终 JSON 填 list）。"
        + "OA：domain=oa，type 同上；ERP：domain=erp，type=depotOut/depotIn；"
        + "商城：domain=mall，type=waitPay/waitDeliver/waitReceive/waitReview/afterSale/all"
        + "（默认买家订单；有门店 id 时传 storeId 查门店售出）。"
        + "知识库文档数/同步状态：knowledge_doc_stats。"
        + "没有对应写操作工具时，不要声称已完成过账/审批等变更，应提示用户到业务页面操作。";

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "角色名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "`logo`")
    @ApiModelProperty(value = "角色logo")
    private String logo;

    @TableField(value = "prompt")
    @ApiModelProperty(value = "角色定位与能力提示词（含工具使用规则）", defaultValue = Role.DEFAULT_PROMPT)
    private String prompt;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "绑定知识库id")
    private String knowledgeId;

    @TableField(exist = false)
    @Property("绑定的知识库")
    private Knowledge knowledgeMation;
}
