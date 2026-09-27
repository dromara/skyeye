/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.classenum.MemberAuthStatus;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.entity.Member;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.dao.ShopStoreAccountDao;
import com.skyeye.finance.entity.ShopStoreAccount;
import com.skyeye.finance.entity.ShopStoreLedger;
import com.skyeye.finance.entity.ShopStoreWithdraw;
import com.skyeye.finance.enums.ShopStoreLedgerBizType;
import com.skyeye.finance.enums.ShopStoreLedgerDirection;
import com.skyeye.finance.enums.ShopStoreSettleStatus;
import com.skyeye.finance.enums.ShopStoreWithdrawStatus;
import com.skyeye.finance.service.ShopStoreAccountService;
import com.skyeye.finance.service.ShopStoreLedgerService;
import com.skyeye.finance.service.ShopStoreWithdrawService;
import com.skyeye.order.entity.OrderAfterSale;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.OrderAfterSaleStatus;
import com.skyeye.order.enums.ShopOrderItemOtherState;
import com.skyeye.order.service.OrderAfterSaleService;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.rest.pay.service.IPayService;
import com.skyeye.service.MemberService;
import com.skyeye.store.classenum.StoreNature;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.ShopStoreStaff;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.store.service.ShopStoreStaffService;
import com.skyeye.common.util.ToolUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopStoreAccountServiceImpl
 * @Description: 门店资金账户服务。金额单位：分；覆盖全部门店；
 * 入账门店为子单 storeId（卖家店）；流水幂等键为 bizType + bizId；
 * 提现审核通过后走支付中心银行卡转账。
 */
@Service
@SkyeyeService(name = "门店资金账户", groupName = "门店资金", tenant = TenantEnum.NO_ISOLATION)
public class ShopStoreAccountServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreAccountDao, ShopStoreAccount>
    implements ShopStoreAccountService {

    private static final Logger log = LoggerFactory.getLogger(ShopStoreAccountServiceImpl.class);

    /** 商城平台代收 PayApp */
    private static final String MALL_ORDER_PAY_APP_KEY = "mall-order";
    /** 一期银行卡自动打款默认走支付宝 PC 渠道（证书模式） */
    private static final String DEFAULT_TRANSFER_CHANNEL = "alipay_pc";
    /** PayTransferType.BANK_CARD */
    private static final int TRANSFER_TYPE_BANK_CARD = 3;
    /** PayTransferStatusResp */
    private static final int TRANSFER_STATUS_WAITING = 0;
    private static final int TRANSFER_STATUS_IN_PROGRESS = 10;
    private static final int TRANSFER_STATUS_SUCCESS = 20;
    private static final int TRANSFER_STATUS_CLOSED = 30;
    /** 确认收货后结算冻结天数，期满冻结转入可提现 */
    private static final int SETTLE_HOLD_DAYS = 7;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private ShopStoreStaffService shopStoreStaffService;

    @Autowired
    private ShopStoreLedgerService shopStoreLedgerService;

    @Autowired
    private ShopStoreWithdrawService shopStoreWithdrawService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    @Lazy
    private OrderAfterSaleService orderAfterSaleService;

    @Autowired
    private IPayService iPayService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private IQuartzService iQuartzService;

    /**
     * 兼容旧数据/回填：直接增加可提现。新单请走 holdOrderItemOnSign。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void creditOrderItem(OrderItem orderItem) {
        if (orderItem == null || StrUtil.isBlank(orderItem.getId()) || StrUtil.isBlank(orderItem.getStoreId())) {
            return;
        }
        // 已有收货冻结或历史入账则跳过
        if (shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_HOLD.getKey(), orderItem.getId())
            || shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_IN.getKey(), orderItem.getId())) {
            return;
        }
        long amount = resolvePayAmountFen(orderItem);
        if (amount <= 0) {
            return;
        }
        applyChange(orderItem.getStoreId(), ShopStoreLedgerBizType.ORDER_IN.getKey(), orderItem.getId(),
            amount, ShopStoreLedgerDirection.IN_AVAILABLE.getKey(),
            "订单入账 " + StrUtil.blankToDefault(orderItem.getOddNumber(), orderItem.getId()),
            (acc, amt) -> {
                acc.setAvailableAmount(nvl(acc.getAvailableAmount()) + amt);
                acc.setTotalIncome(nvl(acc.getTotalIncome()) + amt);
                return amt;
            });
    }

    /**
     * 确认收货整单签收：金额进冻结，SETTLE_HOLD_DAYS 天后解冻可提现
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void holdOrderItemOnSign(OrderItem orderItem) {
        if (orderItem == null || StrUtil.isBlank(orderItem.getId()) || StrUtil.isBlank(orderItem.getStoreId())) {
            return;
        }
        // 仅整单签收；部分签收不入账
        if (!ShopOrderItemOtherState.SIGN.getKey().equals(orderItem.getState())
            && !ShopOrderItemOtherState.UNEVALUATE.getKey().equals(orderItem.getState())
            && !ShopOrderItemOtherState.EVALUATED.getKey().equals(orderItem.getState())
            && !ShopOrderItemOtherState.PARTIALEVALUATION.getKey().equals(orderItem.getState())) {
            return;
        }
        if (shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_HOLD.getKey(), orderItem.getId())
            || shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_IN.getKey(), orderItem.getId())) {
            return;
        }
        long amount = resolvePayAmountFen(orderItem);
        if (amount <= 0) {
            return;
        }
        applyChange(orderItem.getStoreId(), ShopStoreLedgerBizType.ORDER_HOLD.getKey(), orderItem.getId(),
            amount, ShopStoreLedgerDirection.IN_FROZEN.getKey(),
            "收货冻结 " + StrUtil.blankToDefault(orderItem.getOddNumber(), orderItem.getId())
                + "（" + SETTLE_HOLD_DAYS + "天后可提现）",
            (acc, amt) -> {
                acc.setFrozenAmount(nvl(acc.getFrozenAmount()) + amt);
                acc.setTotalIncome(nvl(acc.getTotalIncome()) + amt);
                return amt;
            });
        // 到点自动结算：注册延迟任务
        if (shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_HOLD.getKey(), orderItem.getId())) {
            startSettleQuartz(orderItem.getId(), StrUtil.blankToDefault(orderItem.getOddNumber(), orderItem.getId()));
        }
    }

    /**
     * 单笔到点结算（Quartz 回调）；幂等
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseSettlementByOrderItemId(String orderItemId) {
        if (StrUtil.isBlank(orderItemId)) {
            return;
        }
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizType), ShopStoreLedgerBizType.ORDER_HOLD.getKey())
            .eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizId), orderItemId)
            .last("LIMIT 1");
        ShopStoreLedger hold = shopStoreLedgerService.getOne(qw, false);
        if (hold == null || StrUtil.isBlank(hold.getId())) {
            return;
        }
        // 定时任务到点触发，强制结算（幂等靠 ORDER_SETTLE）
        releaseOneHold(hold, true);
    }

    /**
     * 扫漏：释放全部已到期收货冻结
     */
    @Override
    public void releaseAllDueSettlements() {
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizType), ShopStoreLedgerBizType.ORDER_HOLD.getKey());
        List<ShopStoreLedger> holds = shopStoreLedgerService.list(qw);
        if (CollectionUtil.isEmpty(holds)) {
            return;
        }
        for (ShopStoreLedger hold : holds) {
            try {
                releaseOneHold(hold, false);
            } catch (Exception e) {
                log.error("扫漏结算失败 storeId={} itemId={}", hold.getStoreId(), hold.getBizId(), e);
            }
        }
    }

    /**
     * 售后退款成功出账：结算前优先扣冻结，否则扣可用；不足则扣到 0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void debitRefund(OrderAfterSale afterSale) {
        if (afterSale == null || StrUtil.isBlank(afterSale.getId()) || StrUtil.isBlank(afterSale.getStoreId())) {
            return;
        }
        long amount = parseFen(afterSale.getRefundAmount());
        if (amount <= 0) {
            amount = parseFen(afterSale.getApplyAmount());
        }
        if (amount <= 0) {
            return;
        }
        final long debitAmount = amount;
        final String storeId = afterSale.getStoreId();
        final String itemId = afterSale.getOrderItemId();
        final boolean holdOpen = StrUtil.isNotBlank(itemId)
            && shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_HOLD.getKey(), itemId)
            && !shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_SETTLE.getKey(), itemId)
            && !shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_IN.getKey(), itemId);
        // 结算前有收货冻结：先扣冻结再扣可用
        applyChange(storeId, ShopStoreLedgerBizType.REFUND_OUT.getKey(), afterSale.getId(),
            debitAmount, ShopStoreLedgerDirection.OUT_AVAILABLE.getKey(), "售后退款出账",
            (acc, amt) -> {
                long left = amt;
                long real = 0L;
                if (holdOpen) {
                    long frozen = nvl(acc.getFrozenAmount());
                    long fromFrozen = Math.min(frozen, left);
                    acc.setFrozenAmount(frozen - fromFrozen);
                    left -= fromFrozen;
                    real += fromFrozen;
                }
                if (left > 0) {
                    long available = nvl(acc.getAvailableAmount());
                    long fromAvail = Math.min(available, left);
                    acc.setAvailableAmount(available - fromAvail);
                    left -= fromAvail;
                    real += fromAvail;
                }
                if (real < amt) {
                    log.warn("门店{}退款出账余额不足，应扣{}实际扣{}", storeId, amt, real);
                }
                acc.setTotalRefund(nvl(acc.getTotalRefund()) + real);
                return real;
            });
    }

    /**
     * 商家端：资金概览（可用/冻结/累计收入退款提现）
     */
    @Override
    public void queryPersonalStoreFundSummary(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        assertStoreFundAccess(storeId);
        releaseDueSettlements(storeId);
        ShopStoreAccount account = getOrCreateAccount(storeId);
        Map<String, Object> bean = new HashMap<>();
        bean.put("storeId", storeId);
        bean.put("availableAmount", nvl(account.getAvailableAmount()));
        bean.put("frozenAmount", nvl(account.getFrozenAmount()));
        bean.put("totalIncome", nvl(account.getTotalIncome()));
        bean.put("totalRefund", nvl(account.getTotalRefund()));
        bean.put("totalWithdraw", nvl(account.getTotalWithdraw()));
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 商家端：资金流水分页（objectId=storeId）
     */
    @Override
    public void queryPersonalStoreLedgerPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        assertStoreFundAccess(storeId);
        releaseDueSettlements(storeId);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getStoreId), storeId)
            .orderByDesc(MybatisPlusUtil.toColumns(ShopStoreLedger::getCreateTime));
        List<ShopStoreLedger> list = shopStoreLedgerService.list(qw);
        enrichLedgerSettleInfo(list);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 商家端：订单对账，复用子单分页并附加退款额、净额、到账状态
     */
    @Override
    public void queryPersonalStoreReconcileList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        assertStoreFundAccess(storeId);
        releaseDueSettlements(storeId);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        // 复用子单分页查询（objectId=storeId）；type 空/0 表示全部
        List<Map<String, Object>> rows = orderItemService.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(rows)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(0L);
            return;
        }
        List<String> itemIds = rows.stream()
            .map(r -> r.get("id").toString())
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toList());
        // 查询收货冻结
        Set<String> holdIds = queryLedgerItemIdsByBizTypes(itemIds,
            Collections.singletonList(ShopStoreLedgerBizType.ORDER_HOLD.getKey()));
        // 查询到账、结算金额
        Set<String> arrivedIds = queryLedgerItemIdsByBizTypes(itemIds,
            Arrays.asList(ShopStoreLedgerBizType.ORDER_IN.getKey(), ShopStoreLedgerBizType.ORDER_SETTLE.getKey()));
        // 查询退款金额
        Map<String, Long> refundMap = queryRefundAmountByItemIds(itemIds);
        // 查询收货冻结创建时间
        Map<String, String> holdTimeMap = queryHoldCreateTimeMap(itemIds);
        // 遍历子单，计算净额、到账状态、退款金额
        for (Map<String, Object> row : rows) {
            String id = row.get("id").toString();
            long pay = resolvePayAmountFenFromMap(row);
            long refund = refundMap.getOrDefault(id, 0L);
            // 计算到账状态
            Integer settleStatus;
            if (arrivedIds.contains(id)) {
                settleStatus = ShopStoreSettleStatus.ARRIVED.getKey();
            } else if (holdIds.contains(id)) {
                settleStatus = ShopStoreSettleStatus.HOLDING.getKey();
            } else {
                settleStatus = ShopStoreSettleStatus.NONE.getKey();
            }
            // 设置到账状态
            row.put("settleStatus", settleStatus);
            // 设置已到账标志
            row.put("credited", ShopStoreSettleStatus.NONE.getKey().equals(settleStatus) ? 0 : 1);
            // 设置退款金额
            row.put("refundAmount", refund);
            // 设置净额
            row.put("netAmount", Math.max(pay - refund, 0));
            // 设置预计到账时间
            if (ShopStoreSettleStatus.HOLDING.getKey().equals(settleStatus) && holdTimeMap.containsKey(id)) {
                row.put("settleExpectTime", calcSettleExpectTime(holdTimeMap.get(id)));
            }
        }
        outputObject.setBeans(rows);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 商家端：申请提现——校验资金权限；个人店需实名；冻结可用余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        ShopStore store = assertStoreFundAccess(storeId);
        releaseDueSettlements(storeId);
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        // 个人店申请人须为店主且已实名；加盟店由门店后台菜单权限控制
        if (StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            assertMemberAuthed(userId);
        }

        long amount = parseFen(params.get("amount").toString());
        if (amount <= 0) {
            throw new CustomException("提现金额必须大于0");
        }
        String accountName = params.get("accountName").toString();
        String accountNo = params.get("accountNo").toString();
        String bankName = params.get("bankName") != null ? params.get("bankName").toString() : StrUtil.EMPTY;
        if (StrUtil.isBlank(accountName) || StrUtil.isBlank(accountNo)) {
            throw new CustomException("请填写收款户名和银行卡号");
        }
        if (StrUtil.isBlank(bankName)) {
            throw new CustomException("请填写开户行");
        }

        ShopStoreAccount account = getOrCreateAccount(storeId);
        if (nvl(account.getAvailableAmount()) < amount) {
            throw new CustomException("可用余额不足");
        }

        ShopStoreWithdraw withdraw = new ShopStoreWithdraw();
        withdraw.setStoreId(storeId);
        withdraw.setMemberId(userId);
        withdraw.setAmount(amount);
        withdraw.setState(ShopStoreWithdrawStatus.PENDING.getKey());
        withdraw.setAccountName(accountName.trim());
        withdraw.setAccountNo(accountNo.trim());
        withdraw.setBankName(bankName.trim());
        String withdrawId = shopStoreWithdrawService.createEntity(withdraw, userId);

        // 申请成功后冻结对应可用余额
        applyChange(storeId, ShopStoreLedgerBizType.WITHDRAW_FREEZE.getKey(), withdrawId,
            amount, ShopStoreLedgerDirection.FREEZE.getKey(), "提现冻结 " + store.getName(),
            (acc, amt) -> {
                if (nvl(acc.getAvailableAmount()) < amt) {
                    throw new CustomException("可用余额不足");
                }
                acc.setAvailableAmount(nvl(acc.getAvailableAmount()) - amt);
                acc.setFrozenAmount(nvl(acc.getFrozenAmount()) + amt);
                return amt;
            });
        outputObject.setBean(shopStoreWithdrawService.selectById(withdrawId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 商家端：我的提现记录分页（objectId=storeId）
     */
    @Override
    public void queryMyPersonalStoreWithdrawList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        assertStoreFundAccess(storeId);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<ShopStoreWithdraw> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getStoreId), storeId)
            .orderByDesc(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getCreateTime));
        List<ShopStoreWithdraw> list = shopStoreWithdrawService.list(qw);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 商家端：取消待审核提现并解冻
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMyPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        ShopStoreWithdraw withdraw = shopStoreWithdrawService.selectById(id);
        if (withdraw == null || StrUtil.isBlank(withdraw.getId())) {
            throw new CustomException("提现申请不存在");
        }
        ShopStore store = assertStoreFundAccess(withdraw.getStoreId());
        // 个人店仅申请人可取消；加盟店有门店资金权限即可
        if (StoreNature.PERSONAL.getKey().equals(store.getStoreNature())
            && !userId.equals(withdraw.getMemberId())) {
            throw new CustomException("无权操作该申请");
        }
        if (!ShopStoreWithdrawStatus.PENDING.getKey().equals(withdraw.getState())) {
            throw new CustomException("仅待审核申请可取消");
        }
        unfreezeWithdraw(withdraw, userId, "申请人取消提现");
        UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, id)
            .eq(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.PENDING.getKey())
            .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.CANCELLED.getKey());
        if (!shopStoreWithdrawService.update(uw)) {
            throw new CustomException("取消失败，请刷新重试");
        }
        shopStoreWithdrawService.refreshCache(id);
    }

    /**
     * 管理端：提现申请分页（MPJ 联表门店/会员，支持 state、keyword）
     */
    @Override
    public void queryPersonalStoreWithdrawList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<ShopStoreWithdraw> list = shopStoreWithdrawService.queryAdminPageList(pageInfo);
        shopStoreService.setDataMation(list, ShopStoreWithdraw::getStoreId);
        memberService.setDataMation(list, ShopStoreWithdraw::getMemberId);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 管理端：审核通过——先落库打款中，再发起银行卡转账（转账不在同一事务，避免渠道成功本地回滚）
     */
    @Override
    public void approvePersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String auditRemark = params.get("auditRemark") != null ? params.get("auditRemark").toString() : StrUtil.EMPTY;
        String auditUserId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        String outTransferNo = "WD" + ToolUtil.getSurFaceId();
        transactionTemplate.executeWithoutResult(status -> {
            ShopStoreWithdraw withdraw = shopStoreWithdrawService.selectById(id);
            if (withdraw == null || StrUtil.isBlank(withdraw.getId())) {
                throw new CustomException("提现申请不存在");
            }
            if (!ShopStoreWithdrawStatus.PENDING.getKey().equals(withdraw.getState())) {
                throw new CustomException("仅待审核申请可通过");
            }
            UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, id)
                .eq(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.PENDING.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.TRANSFERRING.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getAuditUserId), auditUserId)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getAuditTime), DateUtil.getTimeAndToString())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getAuditRemark), auditRemark)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getOutTransferNo), outTransferNo)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getTransferError), StrUtil.EMPTY);
            if (!shopStoreWithdrawService.update(uw)) {
                throw new CustomException("审核失败，请刷新重试");
            }
            shopStoreWithdrawService.refreshCache(id);
        });
        invokeBankTransfer(shopStoreWithdrawService.selectById(id));
    }

    /**
     * 管理端：打款失败重试 / 打款中查询同步
     */
    @Override
    public void retryPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        ShopStoreWithdraw withdraw = shopStoreWithdrawService.selectById(id);
        if (withdraw == null || StrUtil.isBlank(withdraw.getId())) {
            throw new CustomException("提现申请不存在");
        }
        Integer state = withdraw.getState();
        if (ShopStoreWithdrawStatus.TRANSFERRING.getKey().equals(state)) {
            syncTransferStatus(withdraw);
            return;
        }
        if (!ShopStoreWithdrawStatus.TRANSFER_FAILED.getKey().equals(state)) {
            throw new CustomException("仅打款失败或打款中的申请可重试");
        }
        String outTransferNo = "WD" + ToolUtil.getSurFaceId();
        transactionTemplate.executeWithoutResult(status -> {
            UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, id)
                .eq(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.TRANSFER_FAILED.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.TRANSFERRING.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getOutTransferNo), outTransferNo)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getChannelTransferNo), StrUtil.EMPTY)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getTransferError), StrUtil.EMPTY);
            if (!shopStoreWithdrawService.update(uw)) {
                throw new CustomException("重试失败，请刷新重试");
            }
            shopStoreWithdrawService.refreshCache(id);
        });
        invokeBankTransfer(shopStoreWithdrawService.selectById(id));
    }

    /**
     * 管理端：审核拒绝——待审核或打款失败可驳回解冻
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectPersonalStoreWithdraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String auditRemark = params.get("auditRemark").toString();
        if (StrUtil.isBlank(auditRemark)) {
            throw new CustomException("请填写拒绝原因");
        }
        String auditUserId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        ShopStoreWithdraw withdraw = shopStoreWithdrawService.selectById(id);
        if (withdraw == null || StrUtil.isBlank(withdraw.getId())) {
            throw new CustomException("提现申请不存在");
        }
        Integer state = withdraw.getState();
        boolean canReject = ShopStoreWithdrawStatus.PENDING.getKey().equals(state)
            || ShopStoreWithdrawStatus.TRANSFER_FAILED.getKey().equals(state);
        if (!canReject) {
            throw new CustomException("仅待审核或打款失败的申请可拒绝");
        }
        unfreezeWithdraw(withdraw, auditUserId, "审核拒绝：" + auditRemark);
        UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, id)
            .eq(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), state)
            .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.REJECTED.getKey())
            .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getAuditUserId), auditUserId)
            .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getAuditTime), DateUtil.getTimeAndToString())
            .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getAuditRemark), auditRemark);
        if (!shopStoreWithdrawService.update(uw)) {
            throw new CustomException("审核失败，请刷新重试");
        }
        shopStoreWithdrawService.refreshCache(id);
    }

    /**
     * 历史回填：已签收子单按结算规则补冻结/可提现；已完成售后补 REFUND_OUT
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void backfillPersonalStoreFinance(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ShopStore> storeQw = new QueryWrapper<>();
        List<ShopStore> stores = shopStoreService.list(storeQw);
        int creditCount = 0;
        int debitCount = 0;
        if (CollectionUtil.isEmpty(stores)) {
            Map<String, Object> bean = new HashMap<>();
            bean.put("creditCount", 0);
            bean.put("debitCount", 0);
            outputObject.setBean(bean);
            return;
        }
        List<String> storeIds = stores.stream().map(ShopStore::getId).collect(Collectors.toList());
        // 仅已签收及之后状态补账（支付未收货不入账）
        QueryWrapper<OrderItem> itemQw = new QueryWrapper<>();
        itemQw.in(MybatisPlusUtil.toColumns(OrderItem::getStoreId), storeIds)
            .in(MybatisPlusUtil.toColumns(OrderItem::getState),
                Arrays.asList(
                    ShopOrderItemOtherState.SIGN.getKey(),
                    ShopOrderItemOtherState.UNEVALUATE.getKey(),
                    ShopOrderItemOtherState.EVALUATED.getKey(),
                    ShopOrderItemOtherState.PARTIALEVALUATION.getKey()));
        List<OrderItem> items = orderItemService.list(itemQw);
        items.sort(Comparator.comparing(o -> StrUtil.blankToDefault(o.getLastUpdateTime(), o.getCreateTime()),
            Comparator.nullsLast(Comparator.naturalOrder())));
        String today = DateUtil.getTimeAndToString();
        for (OrderItem item : items) {
            if (shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_IN.getKey(), item.getId())
                || shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_HOLD.getKey(), item.getId())) {
                continue;
            }
            String signTime = StrUtil.blankToDefault(item.getLastUpdateTime(), item.getCreateTime());
            int days = StrUtil.isBlank(signTime) ? 0 : DateUtil.getDistanceDay(signTime, today);
            if (days >= SETTLE_HOLD_DAYS) {
                creditOrderItem(item);
            } else {
                holdOrderItemOnSign(item);
            }
            creditCount++;
        }
        for (String storeId : storeIds) {
            releaseDueSettlements(storeId);
        }
        // 已完成售后按 refund_amount 出账
        QueryWrapper<OrderAfterSale> asQw = new QueryWrapper<>();
        asQw.in(MybatisPlusUtil.toColumns(OrderAfterSale::getStoreId), storeIds)
            .eq(MybatisPlusUtil.toColumns(OrderAfterSale::getStatus), OrderAfterSaleStatus.DONE.getKey());
        List<OrderAfterSale> afterSales = orderAfterSaleService.list(asQw);
        afterSales.sort(Comparator.comparing(o -> StrUtil.blankToDefault(o.getCreateTime(), ""), Comparator.naturalOrder()));
        for (OrderAfterSale as : afterSales) {
            if (!shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.REFUND_OUT.getKey(), as.getId())) {
                debitRefund(as);
                debitCount++;
            }
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("creditCount", creditCount);
        bean.put("debitCount", debitCount);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 扫描门店收货冻结流水，期满且未结算的转入可提现
     */
    private void releaseDueSettlements(String storeId) {
        if (StrUtil.isBlank(storeId)) {
            return;
        }
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getStoreId), storeId)
            .eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizType), ShopStoreLedgerBizType.ORDER_HOLD.getKey());
        List<ShopStoreLedger> holds = shopStoreLedgerService.list(qw);
        if (CollectionUtil.isEmpty(holds)) {
            return;
        }
        for (ShopStoreLedger hold : holds) {
            releaseOneHold(hold, false);
        }
    }

    /**
     * @param force true=定时到点回调，跳过到期校验；false=需已满结算天数
     */
    private void releaseOneHold(ShopStoreLedger hold, boolean force) {
        if (hold == null || StrUtil.isBlank(hold.getBizId()) || StrUtil.isBlank(hold.getStoreId())) {
            return;
        }
        String itemId = hold.getBizId();
        if (shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_SETTLE.getKey(), itemId)
            || shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.ORDER_IN.getKey(), itemId)) {
            return;
        }
        if (!force && !isHoldDue(hold)) {
            return;
        }
        long refunded = sumRefundedFenByItemId(itemId);
        long releaseAmt = Math.max(nvl(hold.getAmount()) - refunded, 0L);
        applyChange(hold.getStoreId(), ShopStoreLedgerBizType.ORDER_SETTLE.getKey(), itemId,
            releaseAmt, ShopStoreLedgerDirection.UNFREEZE.getKey(),
            "结算可提现 " + itemId,
            (acc, amt) -> {
                if (amt <= 0) {
                    return 0L;
                }
                long frozen = nvl(acc.getFrozenAmount());
                long real = Math.min(frozen, amt);
                acc.setFrozenAmount(frozen - real);
                acc.setAvailableAmount(nvl(acc.getAvailableAmount()) + real);
                return real;
            });
    }

    private boolean isHoldDue(ShopStoreLedger hold) {
        String holdTime = StrUtil.blankToDefault(hold.getCreateTime(), DateUtil.getTimeAndToString());
        try {
            Date holdDate = DateUtil.getPointTime(holdTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
            Date dueDate = DateUtil.getAfDate(holdDate, SETTLE_HOLD_DAYS, "d");
            return !dueDate.after(new Date());
        } catch (Exception e) {
            return DateUtil.getDistanceDay(holdTime, DateUtil.getTimeAndToString()) >= SETTLE_HOLD_DAYS;
        }
    }

    /**
     * 收货冻结成功后，注册 SETTLE_HOLD_DAYS 天后的延迟结算任务
     */
    private void startSettleQuartz(String orderItemId, String title) {
        try {
            Date now = DateUtil.getPointTime(DateUtil.getTimeAndToString(), DateUtil.YYYY_MM_DD_HH_MM_SS);
            Date settleAt = DateUtil.getAfDate(now, SETTLE_HOLD_DAYS, "d");
            DateFormat df = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
            SysQuartzMation quartz = new SysQuartzMation();
            quartz.setName(orderItemId);
            quartz.setTitle(title);
            quartz.setDelayedTime(df.format(settleAt));
            quartz.setGroupId(QuartzConstants.QuartzMateMationJobType.SHOP_STORE_SETTLE.getTaskType());
            iQuartzService.startUpTaskQuartz(quartz);
            log.info("已注册门店结算任务 itemId={} settleAt={}", orderItemId, df.format(settleAt));
        } catch (Exception e) {
            log.error("注册门店结算任务失败 itemId={}", orderItemId, e);
        }
    }

    private long sumRefundedFenByItemId(String itemId) {
        if (StrUtil.isBlank(itemId)) {
            return 0L;
        }
        QueryWrapper<OrderAfterSale> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getOrderItemId), itemId)
            .eq(MybatisPlusUtil.toColumns(OrderAfterSale::getStatus), OrderAfterSaleStatus.DONE.getKey());
        List<OrderAfterSale> list = orderAfterSaleService.list(qw);
        long sum = 0L;
        for (OrderAfterSale as : list) {
            if (!shopStoreLedgerService.existsByBiz(ShopStoreLedgerBizType.REFUND_OUT.getKey(), as.getId())) {
                continue;
            }
            long amt = parseFen(as.getRefundAmount());
            if (amt <= 0) {
                amt = parseFen(as.getApplyAmount());
            }
            sum += Math.max(amt, 0L);
        }
        return sum;
    }

    /**
     * 调用支付中心银行卡转账，并按同步结果更新提现单
     */
    private void invokeBankTransfer(ShopStoreWithdraw withdraw) {
        Map<String, Object> data = new HashMap<>();
        data.put("outTransferNo", withdraw.getOutTransferNo());
        data.put("price", String.valueOf(nvl(withdraw.getAmount())));
        data.put("subject", "门店提现");
        data.put("userName", withdraw.getAccountName());
        data.put("type", TRANSFER_TYPE_BANK_CARD);
        data.put("bankAccountNo", withdraw.getAccountNo());
        Map<String, String> extras = new HashMap<>();
        extras.put("inst_name", withdraw.getBankName());
        extras.put("account_type", "2");
        String channelExtras = JSONUtil.toJsonStr(extras);
        try {
            Map<String, Object> result = iPayService.transfer(data, DEFAULT_TRANSFER_CHANNEL, channelExtras, MALL_ORDER_PAY_APP_KEY).getBean();
            applyTransferResp(withdraw.getId(), result);
        } catch (Exception e) {
            log.error("提现转账发起失败 withdrawId={}", withdraw.getId(), e);
            markTransferFailed(withdraw.getId(), StrUtil.sub(e.getMessage(), 0, 500));
        }
    }

    /**
     * 查询渠道转账状态并落库
     */
    private void syncTransferStatus(ShopStoreWithdraw withdraw) {
        if (StrUtil.isBlank(withdraw.getOutTransferNo())) {
            throw new CustomException("缺少转账单号，无法查询");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("outTransferNo", withdraw.getOutTransferNo());
        data.put("type", TRANSFER_TYPE_BANK_CARD);
        try {
            Map<String, Object> result = iPayService.getTransfer(data, DEFAULT_TRANSFER_CHANNEL, MALL_ORDER_PAY_APP_KEY).getBean();
            applyTransferResp(withdraw.getId(), result);
        } catch (Exception e) {
            log.error("查询转账状态失败 withdrawId={}", withdraw.getId(), e);
            throw new CustomException("查询打款状态失败：" + StrUtil.sub(e.getMessage(), 0, 200));
        }
    }

    @SuppressWarnings("unchecked")
    private void applyTransferResp(String withdrawId, Map<String, Object> result) {
        if (result == null || result.get("payTransferRespDTO") == null) {
            markTransferFailed(withdrawId, "支付中心无转账结果");
            return;
        }
        Map<String, Object> resp = JSONUtil.toBean(result.get("payTransferRespDTO").toString(), Map.class);
        Integer status = resp.get("status") != null ? Integer.parseInt(resp.get("status").toString()) : null;
        String channelTransferNo = resp.get("channelTransferNo") != null ? resp.get("channelTransferNo").toString() : StrUtil.EMPTY;
        String errMsg = StrUtil.blankToDefault(
            resp.get("channelErrorMsg") != null ? resp.get("channelErrorMsg").toString() : null, StrUtil.EMPTY);
        if (status != null && status == TRANSFER_STATUS_SUCCESS) {
            markTransferSuccess(withdrawId, channelTransferNo);
        } else if (status != null && (status == TRANSFER_STATUS_WAITING || status == TRANSFER_STATUS_IN_PROGRESS)) {
            UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, withdrawId)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.TRANSFERRING.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getChannelTransferNo), channelTransferNo)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getTransferError), StrUtil.EMPTY);
            shopStoreWithdrawService.update(uw);
            shopStoreWithdrawService.refreshCache(withdrawId);
        } else if (status != null && status == TRANSFER_STATUS_CLOSED) {
            markTransferFailed(withdrawId, StrUtil.blankToDefault(errMsg, "渠道转账关闭"));
        } else {
            markTransferFailed(withdrawId, StrUtil.blankToDefault(errMsg, "未知转账状态:" + status));
        }
    }

    private void markTransferSuccess(String withdrawId, String channelTransferNo) {
        transactionTemplate.executeWithoutResult(status -> {
            ShopStoreWithdraw withdraw = shopStoreWithdrawService.selectById(withdrawId);
            if (withdraw == null || StrUtil.isBlank(withdraw.getId())) {
                return;
            }
            if (ShopStoreWithdrawStatus.APPROVED.getKey().equals(withdraw.getState())) {
                return;
            }
            long amount = nvl(withdraw.getAmount());
            applyChange(withdraw.getStoreId(), ShopStoreLedgerBizType.WITHDRAW_DONE.getKey(), withdrawId,
                amount, ShopStoreLedgerDirection.FREEZE_TO_WITHDRAW.getKey(), "提现打款成功",
                (acc, amt) -> {
                    if (nvl(acc.getFrozenAmount()) < amt) {
                        throw new CustomException("冻结余额不足，无法完成提现");
                    }
                    acc.setFrozenAmount(nvl(acc.getFrozenAmount()) - amt);
                    acc.setTotalWithdraw(nvl(acc.getTotalWithdraw()) + amt);
                    return amt;
                });
            UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, withdrawId)
                .in(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState),
                    Arrays.asList(ShopStoreWithdrawStatus.TRANSFERRING.getKey(), ShopStoreWithdrawStatus.TRANSFER_FAILED.getKey()))
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.APPROVED.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getChannelTransferNo), channelTransferNo)
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getTransferError), StrUtil.EMPTY);
            if (!shopStoreWithdrawService.update(uw)) {
                log.warn("提现成功状态更新冲突 withdrawId={}", withdrawId);
            }
            shopStoreWithdrawService.refreshCache(withdrawId);
        });
    }

    private void markTransferFailed(String withdrawId, String error) {
        transactionTemplate.executeWithoutResult(status -> {
            UpdateWrapper<ShopStoreWithdraw> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, withdrawId)
                .in(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState),
                    Arrays.asList(ShopStoreWithdrawStatus.TRANSFERRING.getKey(), ShopStoreWithdrawStatus.TRANSFER_FAILED.getKey()))
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getState), ShopStoreWithdrawStatus.TRANSFER_FAILED.getKey())
                .set(MybatisPlusUtil.toColumns(ShopStoreWithdraw::getTransferError), StrUtil.blankToDefault(error, "打款失败"));
            shopStoreWithdrawService.update(uw);
            shopStoreWithdrawService.refreshCache(withdrawId);
        });
    }

    /**
     * 提现解冻：bizId 使用 withdrawId+"_UF"，与冻结流水区分唯一键
     */
    private void unfreezeWithdraw(ShopStoreWithdraw withdraw, String userId, String remark) {
        applyChange(withdraw.getStoreId(), ShopStoreLedgerBizType.WITHDRAW_UNFREEZE.getKey(), withdraw.getId() + "_UF",
            nvl(withdraw.getAmount()), ShopStoreLedgerDirection.UNFREEZE.getKey(), remark,
            (acc, amt) -> {
                long frozen = nvl(acc.getFrozenAmount());
                long real = Math.min(frozen, amt);
                acc.setFrozenAmount(frozen - real);
                acc.setAvailableAmount(nvl(acc.getAvailableAmount()) + real);
                return real;
            });
    }

    @FunctionalInterface
    private interface AccountMutator {
        /**
         * @return 实际记入流水的金额（分）
         */
        long mutate(ShopStoreAccount account, long amount);
    }

    /**
     * 账户变更核心：bizType+bizId 幂等；乐观锁重试；先改余额再写流水
     */
    private void applyChange(String storeId, Integer bizType, String bizId, long amount, int direction,
                             String remark, AccountMutator mutator) {
        // 已存在同业务流水则直接跳过（支付/退款回调幂等）
        if (shopStoreLedgerService.existsByBiz(bizType, bizId)) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            ShopStoreAccount account = getOrCreateAccount(storeId);
            long beforeAvailable = nvl(account.getAvailableAmount());
            long beforeFrozen = nvl(account.getFrozenAmount());
            long ledgerAmount = mutator.mutate(account, amount);
            if (ledgerAmount < 0) {
                return;
            }
            UpdateWrapper<ShopStoreAccount> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, account.getId())
                .eq(MybatisPlusUtil.toColumns(ShopStoreAccount::getVersion), account.getVersion())
                .set(MybatisPlusUtil.toColumns(ShopStoreAccount::getAvailableAmount), account.getAvailableAmount())
                .set(MybatisPlusUtil.toColumns(ShopStoreAccount::getFrozenAmount), account.getFrozenAmount())
                .set(MybatisPlusUtil.toColumns(ShopStoreAccount::getTotalIncome), account.getTotalIncome())
                .set(MybatisPlusUtil.toColumns(ShopStoreAccount::getTotalRefund), account.getTotalRefund())
                .set(MybatisPlusUtil.toColumns(ShopStoreAccount::getTotalWithdraw), account.getTotalWithdraw())
                .set(MybatisPlusUtil.toColumns(ShopStoreAccount::getVersion), account.getVersion() + 1);
            boolean ok = update(uw);
            if (!ok) {
                // 乐观锁冲突，重试
                continue;
            }
            // 二次幂等：并发下另一线程可能已写流水
            if (shopStoreLedgerService.existsByBiz(bizType, bizId)) {
                log.warn("流水已存在但仍更新了账户，storeId={} bizType={} bizId={} beforeAvail={} beforeFrozen={}",
                    storeId, bizType, bizId, beforeAvailable, beforeFrozen);
                return;
            }
            ShopStoreLedger ledger = new ShopStoreLedger();
            ledger.setStoreId(storeId);
            ledger.setBizType(bizType);
            ledger.setBizId(bizId);
            ledger.setAmount(ledgerAmount);
            ledger.setDirection(direction);
            ledger.setAvailableAfter(nvl(account.getAvailableAmount()));
            ledger.setFrozenAfter(nvl(account.getFrozenAmount()));
            ledger.setRemark(remark);
            try {
                shopStoreLedgerService.createEntity(ledger, "system");
            } catch (Exception e) {
                // uk 冲突视为幂等成功
                if (shopStoreLedgerService.existsByBiz(bizType, bizId)) {
                    return;
                }
                throw e;
            }
            return;
        }
        throw new CustomException("账户更新冲突，请重试");
    }

    /**
     * 按门店获取账户，不存在则创建零余额账户
     */
    private ShopStoreAccount getOrCreateAccount(String storeId) {
        QueryWrapper<ShopStoreAccount> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreAccount::getStoreId), storeId);
        ShopStoreAccount one = getOne(qw, false);
        if (one != null && StrUtil.isNotBlank(one.getId())) {
            return one;
        }
        ShopStoreAccount account = new ShopStoreAccount();
        account.setStoreId(storeId);
        account.setAvailableAmount(0L);
        account.setFrozenAmount(0L);
        account.setTotalIncome(0L);
        account.setTotalRefund(0L);
        account.setTotalWithdraw(0L);
        account.setVersion(0);
        createEntity(account, "system");
        return getOne(qw, false);
    }

    /**
     * 资金操作权限：店主 / 本店员工 / 加盟企业店（入口靠菜单）。
     * 门店工作台员工账号与个人店 createId 不一致，不能只按 createId 拦。
     */
    private ShopStore assertStoreFundAccess(String storeId) {
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        ShopStore store = shopStoreService.selectById(storeId);
        if (store == null || StrUtil.isBlank(store.getId())) {
            throw new CustomException("门店不存在");
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if (userId.equals(store.getCreateId())) {
            return store;
        }
        Object staffIdObj = InputObject.getLogParamsStatic().get("staffId");
        String staffId = staffIdObj == null ? StrUtil.EMPTY : staffIdObj.toString();
        if (StrUtil.isNotBlank(staffId) && !"tmpUserStaffId".equals(staffId) && isStoreStaff(storeId, staffId)) {
            return store;
        }
        // 加盟/企业门店：工作台员工账号与 createId 不一致，门店存在且入口有菜单即可
        if (!StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            return store;
        }
        throw new CustomException("无权操作该门店");
    }

    private boolean isStoreStaff(String storeId, String staffId) {
        List<ShopStoreStaff> staffList = shopStoreStaffService.getShopStoresByStoreId(storeId);
        if (CollectionUtil.isEmpty(staffList)) {
            return false;
        }
        return staffList.stream().anyMatch(s -> staffId.equals(s.getStaffId()));
    }

    /**
     * 提现前校验会员已实名
     */
    private void assertMemberAuthed(String memberId) {
        Member member = memberService.selectById(memberId);
        if (member == null || StrUtil.isBlank(member.getId())) {
            throw new CustomException("会员不存在");
        }
        if (!MemberAuthStatus.AUTHED.getKey().equals(member.getAuthStatus())) {
            throw new CustomException("请先完成实名认证后再提现");
        }
    }

    /**
     * 流水补充订单号、到账状态
     */
    private void enrichLedgerSettleInfo(List<ShopStoreLedger> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> orderItemIds = list.stream()
            .filter(l -> isOrderFundBizType(l.getBizType()))
            .map(ShopStoreLedger::getBizId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orderItemIds)) {
            return;
        }
        Map<String, OrderItem> itemMap = orderItemService.selectByIds(orderItemIds.toArray(new String[0]))
            .stream()
            .filter(i -> i != null && StrUtil.isNotBlank(i.getId()))
            .collect(Collectors.toMap(OrderItem::getId, i -> i, (a, b) -> a));
        Set<String> arrivedIds = queryLedgerItemIdsByBizTypes(orderItemIds,
            Arrays.asList(ShopStoreLedgerBizType.ORDER_IN.getKey(), ShopStoreLedgerBizType.ORDER_SETTLE.getKey()));
        for (ShopStoreLedger ledger : list) {
            if (!isOrderFundBizType(ledger.getBizType()) || StrUtil.isBlank(ledger.getBizId())) {
                continue;
            }
            OrderItem item = itemMap.get(ledger.getBizId());
            if (item != null) {
                ledger.setOddNumber(item.getOddNumber());
            }
            Integer bizType = ledger.getBizType();
            if (ShopStoreLedgerBizType.ORDER_SETTLE.getKey().equals(bizType)
                || ShopStoreLedgerBizType.ORDER_IN.getKey().equals(bizType)
                || arrivedIds.contains(ledger.getBizId())) {
                ledger.setSettleStatus(ShopStoreSettleStatus.ARRIVED.getKey());
            } else if (ShopStoreLedgerBizType.ORDER_HOLD.getKey().equals(bizType)) {
                ledger.setSettleStatus(ShopStoreSettleStatus.HOLDING.getKey());
                ledger.setSettleExpectTime(calcSettleExpectTime(ledger.getCreateTime()));
            }
        }
    }

    private boolean isOrderFundBizType(Integer bizType) {
        return ShopStoreLedgerBizType.ORDER_IN.getKey().equals(bizType)
            || ShopStoreLedgerBizType.ORDER_HOLD.getKey().equals(bizType)
            || ShopStoreLedgerBizType.ORDER_SETTLE.getKey().equals(bizType);
    }

    private Set<String> queryLedgerItemIdsByBizTypes(List<String> itemIds, List<Integer> bizTypes) {
        if (CollectionUtil.isEmpty(itemIds) || CollectionUtil.isEmpty(bizTypes)) {
            return Collections.emptySet();
        }
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.in(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizType), bizTypes)
            .in(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizId), itemIds);
        return shopStoreLedgerService.list(qw).stream()
            .map(ShopStoreLedger::getBizId)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toSet());
    }

    private Map<String, String> queryHoldCreateTimeMap(List<String> itemIds) {
        if (CollectionUtil.isEmpty(itemIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizType), ShopStoreLedgerBizType.ORDER_HOLD.getKey())
            .in(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizId), itemIds);
        Map<String, String> map = new HashMap<>();
        for (ShopStoreLedger hold : shopStoreLedgerService.list(qw)) {
            if (StrUtil.isNotBlank(hold.getBizId()) && !map.containsKey(hold.getBizId())) {
                map.put(hold.getBizId(), hold.getCreateTime());
            }
        }
        return map;
    }

    private String calcSettleExpectTime(String holdTime) {
        if (StrUtil.isBlank(holdTime)) {
            return null;
        }
        try {
            Date holdDate = DateUtil.getPointTime(holdTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
            Date expect = DateUtil.getAfDate(holdDate, SETTLE_HOLD_DAYS, "d");
            DateFormat df = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
            return df.format(expect);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 已完成售后按子单汇总退款金额（分）
     */
    private Map<String, Long> queryRefundAmountByItemIds(List<String> itemIds) {
        if (CollectionUtil.isEmpty(itemIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<OrderAfterSale> qw = new QueryWrapper<>();
        qw.in(MybatisPlusUtil.toColumns(OrderAfterSale::getOrderItemId), itemIds)
            .eq(MybatisPlusUtil.toColumns(OrderAfterSale::getStatus), OrderAfterSaleStatus.DONE.getKey());
        List<OrderAfterSale> list = orderAfterSaleService.list(qw);
        Map<String, Long> map = new HashMap<>();
        for (OrderAfterSale as : list) {
            long amt = parseFen(as.getRefundAmount());
            if (amt <= 0) {
                amt = parseFen(as.getApplyAmount());
            }
            map.merge(as.getOrderItemId(), amt, Long::sum);
        }
        return map;
    }

    /**
     * 子单入账金额：改价优先，否则实付
     */
    private long resolvePayAmountFen(OrderItem item) {
        if (StrUtil.isNotBlank(item.getAdjustPrice())
            && !StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), item.getAdjustPrice())) {
            return parseFen(item.getAdjustPrice());
        }
        return parseFen(item.getPayPrice());
    }

    private long resolvePayAmountFenFromMap(Map<String, Object> row) {
        Object adjustObj = row.get("adjustPrice");
        String adjust = adjustObj != null ? adjustObj.toString() : null;
        if (StrUtil.isNotBlank(adjust) && !StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), adjust)) {
            return parseFen(adjust);
        }
        Object payObj = row.get("payPrice");
        return parseFen(payObj != null ? payObj.toString() : null);
    }

    /**
     * 金额字符串转分，非法或空返回 0
     */
    private long parseFen(String val) {
        if (StrUtil.isBlank(val)) {
            return 0L;
        }
        try {
            return Math.round(Double.parseDouble(val.trim()));
        } catch (Exception e) {
            return 0L;
        }
    }

    private long nvl(Long v) {
        return v == null ? 0L : v;
    }
}
