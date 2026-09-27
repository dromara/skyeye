package com.skyeye.finance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.entity.ShopStoreAccount;
import com.skyeye.order.entity.OrderAfterSale;
import com.skyeye.order.entity.OrderItem;

public interface ShopStoreAccountService extends SkyeyeBusinessService<ShopStoreAccount> {

    /**
     * 历史/兼容：直接增加可提现（幂等）。新流程请用 holdOrderItemOnSign。
     */
    void creditOrderItem(OrderItem orderItem);

    /**
     * 确认收货（整单签收）：入账到冻结，结算期满后才可提现（幂等）
     */
    void holdOrderItemOnSign(OrderItem orderItem);

    /**
     * 定时任务：按子单结算（收货冻结期满 → 可提现）
     */
    void releaseSettlementByOrderItemId(String orderItemId);

    /**
     * 定时扫漏：全量释放已到期的收货冻结
     */
    void releaseAllDueSettlements();

    /**
     * 售后退款成功：出账（幂等）
     */
    void debitRefund(OrderAfterSale afterSale);

    void queryPersonalStoreFundSummary(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreLedgerPageList(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreReconcileList(InputObject inputObject, OutputObject outputObject);

    void applyPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject);

    /**
     * 商家端：查询门店默认收款账户
     */
    void queryPersonalStorePayee(InputObject inputObject, OutputObject outputObject);

    /**
     * 商家端：保存门店默认收款账户
     */
    void savePersonalStorePayee(InputObject inputObject, OutputObject outputObject);

    void queryMyPersonalStoreWithdrawList(InputObject inputObject, OutputObject outputObject);

    void cancelMyPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreWithdrawList(InputObject inputObject, OutputObject outputObject);

    void approvePersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject);

    void rejectPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject);

    /**
     * 管理端：打款失败重试 / 打款中状态同步
     */
    void retryPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject);

    /**
     * 历史订单/售后回填账本（全门店）
     */
    void backfillPersonalStoreFinance(InputObject inputObject, OutputObject outputObject);
}
