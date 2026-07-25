/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.search.CommonPageInfo;
import lombok.Data;

import com.skyeye.personnel.classenum.StaffWagesStateEnum;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: SysEveUserStaffQuery
 * @Description: 员工查询实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/23 10:37
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("员工查询实体类")
public class SysEveUserStaffQuery extends CommonPageInfo {

    @ApiModelProperty(value = "薪资设定情况", enumClass = StaffWagesStateEnum.class)
    private Integer designWages;

    @ApiModelProperty(value = "是否绑定账号", enumClass = WhetherEnum.class)
    private Integer bindAccount;

}
