/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.otherwise.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.common.enumeration.PayTypeEnum;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: ErpOtherWiseOrder
 * @Description: 其他微服务创建ERP单据的实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/19 18:28
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("其他微服务创建ERP单据的实体类")
public class ErpOtherWiseOrder extends OperatorUserInfo {

    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑", required = "required")
    private String id;

    @ApiModelProperty(value = "单据编号", required = "required")
    private String oddNumber;

    @ApiModelProperty(value = "状态", required = "required")
    private String state;

    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    @ApiModelProperty(value = "出入库类型", required = "required", enumClass = DepotPutOutType.class)
    private Integer type;

    @ApiModelProperty(value = "订单类型，每个服务类的serviceClassName", required = "required")
    private String idKey;

    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "业务员id")
    private String salesman;

    @ApiModelProperty(value = "来源单据类型")
    private Integer fromTypeId;

    @ApiModelProperty(value = "来源单据id")
    private String fromId;

    @ApiModelProperty(value = "实际完成时间")
    private String realComplateTime;

    @ApiModelProperty(value = "商品列表,json串", required = "required,json")
    private List<ErpOtherWiseOrderItem> erpOrderItemList;

    @ApiModelProperty(value = "质检类型", enumClass = OrderQualityInspectionType.class)
    private Integer qualityInspection;

    @ApiModelProperty(value = "是否需要出入库", enumClass = WhetherEnum.class)
    private Integer needDepot;

    @ApiModelProperty("其他状态信息，根据单据类型不同，状态信息表达含义不同。")
    private Integer otherState;

    @ApiModelProperty(value = "部门id")
    private String departmentId;

    @ApiModelProperty(value = "车间id")
    private String farmId;

    @ApiModelProperty(value = "关联项目id")
    private String projectId;

    @ApiModelProperty(value = "优惠率,默认为0.00", required = "double")
    private String discount;

    @ApiModelProperty(value = "优惠金额/折损扣费,默认为0.00", required = "double")
    private String discountMoney;

    @ApiModelProperty(value = "是否需要统筹", enumClass = WhetherEnum.class)
    private Integer needOverPlan;

    @ApiModelProperty(value = "计划完成时间")
    private String planComplateTime;

    @ApiModelProperty(value = "付款类型", required = "num", defaultValue = "0", enumClass = PayTypeEnum.class)
    private String payType;

    @ApiModelProperty(value = "关联的客户/供应商/会员id")
    private String holderId;

    @ApiModelProperty(value = "关联的客户/供应商/会员的className")
    private String holderKey;

    @ApiModelProperty(value = "合计总金额(减去优惠后的金额，加上其他金额)")
    private String totalPrice;

    @ApiModelProperty(value = "账户id")
    private String accountId;

}
