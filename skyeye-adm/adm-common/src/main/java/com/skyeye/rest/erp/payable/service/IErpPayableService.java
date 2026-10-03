package com.skyeye.rest.erp.payable.service;

import com.skyeye.base.rest.service.IService;

/**
 * @ClassName: IErpPayableService
 * @Description: 应付事项（跨服务）
 */
public interface IErpPayableService extends IService {

    long queryPayableCount();
}
