/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.erp.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @ClassName: TransmitObject
 * @Description: 价格信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/6 22:48
 * 
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class TransmitObject implements Serializable {

    private static final long serialVersionUID = 8791538327978796421L;

    /**
     * 主单据总价
     */
    private BigDecimal allPrice = new BigDecimal("0");

    /**
     * 主单据价税合计
     */
    private BigDecimal taxLastMoneyPrice = new BigDecimal("0");

}
