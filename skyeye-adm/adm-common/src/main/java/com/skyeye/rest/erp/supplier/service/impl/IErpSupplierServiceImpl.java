package com.skyeye.rest.erp.supplier.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.supplier.rest.IErpSupplierRest;
import com.skyeye.rest.erp.supplier.service.IErpSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: IErpSupplierServiceImpl
 * @Description: 供应商信息信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IErpSupplierServiceImpl extends IServiceImpl implements IErpSupplierService {

    @Autowired
    private IErpSupplierRest iErpSupplierRest;

    @Override
    public List<Map<String, Object>> querySupplierListByIds(String ids) {
        Map<String, Object> params = Collections.singletonMap("ids", ids);
        return ExecuteFeignClient.get(() -> iErpSupplierRest.querySupplierListByIds(params)).getRows();
    }
}
