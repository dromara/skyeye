/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.search.CommonPageInfo;
import lombok.Data;

import java.io.Serializable;

import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.pick.classenum.PickNormsCodeUseState;

/**
 * @ClassName: MaterialNormsCodeQueryDo
 * @Description: 商品规格一物一码查询条件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/21 21:19
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("商品规格一物一码查询条件实体类")
public class MaterialNormsCodeQueryDo extends CommonPageInfo implements Serializable {

    @ApiModelProperty(value = "产品id")
    private String materialId;

    @ApiModelProperty("规格id")
    private String normsId;

    @ApiModelProperty(value = "部门id")
    private String departmentId;

    @ApiModelProperty(value = "车间id")
    private String farmId;

    @ApiModelProperty(value = "由这个仓库生成/所属仓库id")
    private String depotId;

    @ApiModelProperty(value = "来源单据的id")
    private String fromObjectId;

    @ApiModelProperty(value = "出库单据的id")
    private String toObjectId;

    @ApiModelProperty(value = "库存状态", enumClass = MaterialNormsCodeInDepot.class)
    private Integer inDepot;

    @ApiModelProperty(value = "加工时，物料使用状态", enumClass = PickNormsCodeUseState.class)
    private Integer pickUseState;

    @ApiModelProperty(value = "加工使用结果状态", enumClass = MachinProcedureAcceptChildType.class)
    private Integer pickState;

    @ApiModelProperty(value = "获取的数量")
    private Integer number;

    @ApiModelProperty(value = "门店ID")
    private String storeId;

}
