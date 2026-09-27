package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.OrderAfterSale;

public interface OrderAfterSaleService extends SkyeyeBusinessService<OrderAfterSale> {

    void applyOrderAfterSale(InputObject inputObject, OutputObject outputObject);

    void cancelOrderAfterSale(InputObject inputObject, OutputObject outputObject);

    void queryOrderAfterSaleByItemId(InputObject inputObject, OutputObject outputObject);

    void queryOrderAfterSaleById(InputObject inputObject, OutputObject outputObject);

    void queryMyOrderAfterSaleList(InputObject inputObject, OutputObject outputObject);

    void queryStoreOrderAfterSaleList(InputObject inputObject, OutputObject outputObject);

    void agreeOrderAfterSale(InputObject inputObject, OutputObject outputObject);

    void rejectOrderAfterSale(InputObject inputObject, OutputObject outputObject);

    void fillAfterSaleReturnLogistics(InputObject inputObject, OutputObject outputObject);

    void confirmAfterSaleReturn(InputObject inputObject, OutputObject outputObject);

    void notifyOrderAfterSaleRefundSuccess(InputObject inputObject, OutputObject outputObject);
}
