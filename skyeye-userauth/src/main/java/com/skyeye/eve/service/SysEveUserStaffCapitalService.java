/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: SysEveUserStaffCapitalService
 * @Description: 员工非工资型的额外资金结算池服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/2 16:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserStaffCapitalService {

    /**
     * 新增员工待结算资金池信息
     *
     * @param staffId 员工id
     * @param companyId 企业id
     * @param departmentId 部门id
     * @param monthTime 指定年月，格式为：yyyy-MM
     * @param type 该资金来源类型
     * @param money 金额
     * @throws Exception
     */
    void addMonthMoney2StaffCapital(String staffId, String companyId, String departmentId, String monthTime, int type, String money) throws Exception;

    void queryStaffCapitalWaitPayMonthList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
