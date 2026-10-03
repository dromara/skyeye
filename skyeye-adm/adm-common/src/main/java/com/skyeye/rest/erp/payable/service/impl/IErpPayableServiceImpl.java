package com.skyeye.rest.erp.payable.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.rest.erp.payable.rest.IErpPayableRest;
import com.skyeye.rest.erp.payable.service.IErpPayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: IErpPayableServiceImpl
 * @Description: 应付事项（跨服务）
 */
@Service
public class IErpPayableServiceImpl extends IServiceImpl implements IErpPayableService {

    @Autowired
    private IErpPayableRest iErpPayableRest;

    @Override
    public long queryPayableCount() {
        ResultEntity result = ExecuteFeignClient.get(() -> iErpPayableRest.queryPayableCount());
        Integer total = result.getTotal();
        return total == null ? 0L : total.longValue();
    }
}
