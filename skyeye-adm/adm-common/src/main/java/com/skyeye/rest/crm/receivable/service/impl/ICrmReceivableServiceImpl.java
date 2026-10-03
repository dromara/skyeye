package com.skyeye.rest.crm.receivable.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.rest.crm.receivable.rest.ICrmReceivableRest;
import com.skyeye.rest.crm.receivable.service.ICrmReceivableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ICrmReceivableServiceImpl
 * @Description: 应收事项（跨服务）
 */
@Service
public class ICrmReceivableServiceImpl extends IServiceImpl implements ICrmReceivableService {

    @Autowired
    private ICrmReceivableRest iCrmReceivableRest;

    @Override
    public long queryReceivableCount() {
        ResultEntity result = ExecuteFeignClient.get(() -> iCrmReceivableRest.queryReceivableCount());
        Integer total = result.getTotal();
        return total == null ? 0L : total.longValue();
    }
}
