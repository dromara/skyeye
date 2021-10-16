/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.erp.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: EditMaterialNormsObject
 * @Description: 计算库存时用到的实体
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 21:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class EditMaterialNormsObject implements Serializable {

    private static final long serialVersionUID = 4981302167042771956L;

    // 仓库id
    private String depotId;

    // 商品id
    private String materialId;

    // 规格id
    private String normsId;

    // 变化数量
    private String operNumber;

    // 1.入库 2.出库
    private int type;

}
