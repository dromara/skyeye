package com.skyeye.payable.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.payable.entity.Payable;

/**
 * @ClassName: PayableService
 * @Description: 应付事项服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PayableService extends SkyeyeBusinessService<Payable> {
    void queryPayableByContractId(InputObject inputObject, OutputObject outputObject);

    void updatePayablePaidPrice(String payableId, String price);

    void updatePayableById(InputObject inputObject, OutputObject outputObject);

    void queryPayableCount(InputObject inputObject, OutputObject outputObject);
}
