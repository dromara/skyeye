package com.skyeye.receivepayment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.common.enumeration.FlowableStateEnum;

/**
 * @ClassName: ReceivePayment
 * @Description: 收付款实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ifs_receive_payment", autoResultMap = true)
@ApiModel("收付款实体类")
public class ReceivePayment extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @Property(value = "单号，仅用于展示使用")
    private String name;

    @TableField("odd_number")
    @ApiModelProperty(value = "单据编号",fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(客户/供应商)", required = "required")
    private String objectId;

    @TableField(exist = false)
    @Property("客户/供应商信息")
    private Map<String, Object> objectMation;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "contract_id")
    @ApiModelProperty(value = "合同ID")
    private String contractId;

    @TableField(exist = false)
    @Property(value = "合同")
    private Map<String, Object> contractMation;

    @TableField(value = "price")
    @ApiModelProperty(value = "回付款金额", required = "double", defaultValue = "0")
    private String price;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "支付方式，参考数据字典")
    private String typeId;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField("from_id")
    @ApiModelProperty(value = "来源ID（付款id,回款id）")
    private String fromId;

    @TableField(exist = false)
    @Property(value = "来源信息")
    private Map<String, Object> fromMation;

    @TableField("from_child_id")
    @ApiModelProperty(value = "来源子ID（付款子id,回款子id）")
    private String fromChildId;

    @TableField(exist = false)
    @Property(value = "来源子信息")
    private Map<String, Object> fromChildMation;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = FlowableStateEnum.class)
    private String state;

    @TableField("collection_time")
    @ApiModelProperty(value = "回/付款日期")
    private String collectionTime;

    @TableField(value = "create_id")
    @ApiModelProperty("创建人id")
    private String createId;

    @Property("创建人姓名")
    @TableField(exist = false)
    private String createName;

    @TableField(exist = false)
    @Property(value = "创建人信息")
    private Map<String, Object> createMation;

    @TableField(value = "create_time")
    @ApiModelProperty("创建时间")
    private String createTime;

    @TableField(value = "last_update_id")
    @ApiModelProperty("最后更新人id")
    private String lastUpdateId;

    @Property("最后更新人姓名")
    @TableField(exist = false)
    private String lastUpdateName;

    @TableField(exist = false)
    @Property(value = "最后更新人信息")
    private Map<String, Object> lastUpdateMation;

    @TableField(value = "last_update_time")
    @ApiModelProperty("最后更新日期")
    private String lastUpdateTime;

    @TableField(value = "invoice_price")
    @ApiModelProperty(value = "已开票金额")
    private String invoicePrice;
}
