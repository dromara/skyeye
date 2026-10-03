package com.skyeye.rest.crm.receivable.service;

import com.skyeye.base.rest.service.IService;

/**
 * @ClassName: ICrmReceivableService
 * @Description: 应收事项（跨服务）
 */
public interface ICrmReceivableService extends IService {

    long queryReceivableCount();
}
