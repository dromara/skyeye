package com.skyeye.rest.crm.customer.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.crm.customer.rest.ICrmCustomerRest;
import com.skyeye.rest.crm.customer.service.ICrmCustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ICrmCustomerServiceImpl
 * @Description: 客户信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ICrmCustomerServiceImpl extends IServiceImpl implements ICrmCustomerService {

    @Autowired
    private ICrmCustomerRest iCrmCustomerRest;

    @Override
    public List<Map<String, Object>> queryCustomerListByIds(String ids) {
        Map<String, Object> params = Collections.singletonMap("ids", ids);
        return ExecuteFeignClient.get(() -> iCrmCustomerRest.queryCustomerListByIds(params)).getRows();
    }
}
