package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderAfterSaleDao;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderAfterSale;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.OrderAfterSaleStatus;
import com.skyeye.order.enums.OrderAfterSaleType;
import com.skyeye.order.enums.ShopOrderItemOtherState;
import com.skyeye.order.service.OrderAfterSaleService;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.rest.pay.service.IPayService;
import com.skyeye.rest.shopstock.service.IShopStockService;
import com.skyeye.service.MemberService;
import com.skyeye.store.classenum.StoreNature;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;

@Service
@SkyeyeService(name = "商城商品售后", groupName = "商城商品售后", tenant = TenantEnum.NO_ISOLATION)
public class OrderAfterSaleServiceImpl extends SkyeyeBusinessServiceImpl<OrderAfterSaleDao, OrderAfterSale>
    implements OrderAfterSaleService {

    private static final String MALL_ORDER_PAY_APP_KEY = "mall-order";
    /**
     * 与 PayRefundStatusResp 对齐：0等待 1成功 2失败
     */
    private static final int REFUND_WAITING = 0;
    private static final int REFUND_SUCCESS = 1;
    private static final int REFUND_FAILURE = 2;

    private static final List<Integer> ACTIVE_STATUS = Arrays.asList(
        OrderAfterSaleStatus.WAIT_MERCHANT.getKey(),
        OrderAfterSaleStatus.WAIT_BUYER_RETURN.getKey(),
        OrderAfterSaleStatus.WAIT_MERCHANT_RECEIVE.getKey(),
        OrderAfterSaleStatus.REFUNDING.getKey());

    private static final List<Integer> REFUND_ONLY_STATES = Arrays.asList(
        ShopOrderItemOtherState.WAIT_DELIVER.getKey(),
        ShopOrderItemOtherState.PART_DELIVERED.getKey());

    private static final List<Integer> RETURN_EXCHANGE_STATES = Arrays.asList(
        ShopOrderItemOtherState.ALL_DELIVERED.getKey(),
        ShopOrderItemOtherState.TRANSPORTING.getKey(),
        ShopOrderItemOtherState.SIGN.getKey(),
        ShopOrderItemOtherState.COMPLETED.getKey(),
        ShopOrderItemOtherState.UNEVALUATE.getKey(),
        ShopOrderItemOtherState.EVALUATED.getKey(),
        ShopOrderItemOtherState.PARTIALLYDONE.getKey(),
        ShopOrderItemOtherState.PARTIALEVALUATION.getKey(),
        ShopOrderItemOtherState.PART_SIGN.getKey());

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private IPayService iPayService;

    @Autowired
    private IShopStockService iShopStockService;

    @Autowired
    private MemberService memberService;

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void applyOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String orderItemId = MapUtilStr(params, "orderItemId");
        Integer type = Integer.parseInt(MapUtilStr(params, "type"));
        String reasonText = MapUtilStr(params, "reasonText");
        String applyCount = StrUtil.blankToDefault(MapUtilStr(params, "applyCount"), CommonNumConstants.NUM_ZERO.toString());
        String evidence = MapUtilStr(params, "evidence");
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();

        OrderItem item = orderItemService.selectById(orderItemId);
        if (item == null || StrUtil.isEmpty(item.getId())) {
            throw new CustomException("订单子单不存在");
        }
        if (!userId.equals(item.getCreateId())) {
            throw new CustomException("无权申请该订单售后");
        }
        assertNoActiveAfterSale(orderItemId);
        validateApplyEligibility(item, type);

        String itemPay = resolveItemPayPrice(item);
        // 申请金额固定为实付，买家不可自填；商家同意时可下调
        String applyAmount = OrderAfterSaleType.EXCHANGE.getKey().equals(type)
            ? CommonNumConstants.NUM_ZERO.toString() : itemPay;
        if (CalculationUtil.compareTo(applyAmount, CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) <= 0
            && !OrderAfterSaleType.EXCHANGE.getKey().equals(type)) {
            throw new CustomException("退款金额必须大于0");
        }
        if (CalculationUtil.compareTo(applyCount, CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) <= 0) {
            applyCount = item.getCount();
        }
        if (CalculationUtil.compareTo(applyCount, item.getCount(), CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) > 0) {
            throw new CustomException("申请数量不能超过购买数量");
        }

        OrderAfterSale afterSale = new OrderAfterSale();
        afterSale.setOrderItemId(orderItemId);
        afterSale.setParentId(item.getParentId());
        afterSale.setStoreId(item.getStoreId());
        afterSale.setSourceStoreId(item.getSourceStoreId());
        afterSale.setType(type);
        afterSale.setReasonText(reasonText);
        afterSale.setApplyAmount(applyAmount);
        afterSale.setApplyCount(applyCount);
        afterSale.setEvidence(evidence);
        afterSale.setStatus(OrderAfterSaleStatus.WAIT_MERCHANT.getKey());
        afterSale.setPriorItemState(item.getState());
        afterSale.setRefundAmount(applyAmount);
        createEntity(afterSale, userId);

        Integer itemState = OrderAfterSaleType.REFUND_ONLY.getKey().equals(type)
            ? ShopOrderItemOtherState.REFUNDING.getKey()
            : (OrderAfterSaleType.RETURN_REFUND.getKey().equals(type)
            ? ShopOrderItemOtherState.SALESRETURNING.getKey()
            : ShopOrderItemOtherState.EXCHANGEING.getKey());
        orderItemService.editStateById(orderItemId, String.valueOf(itemState));

        outputObject.setBean(selectById(afterSale.getId()));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void cancelOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        OrderAfterSale afterSale = selectById(id);
        assertBuyerOwner(afterSale, userId);
        if (!OrderAfterSaleStatus.WAIT_MERCHANT.getKey().equals(afterSale.getStatus())
            && !OrderAfterSaleStatus.WAIT_BUYER_RETURN.getKey().equals(afterSale.getStatus())) {
            throw new CustomException("当前状态不可撤销");
        }
        afterSale.setStatus(OrderAfterSaleStatus.CANCELLED.getKey());
        updateEntity(afterSale, userId);
        restoreItemState(afterSale);
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOrderAfterSaleByItemId(InputObject inputObject, OutputObject outputObject) {
        String orderItemId = inputObject.getParams().get("orderItemId").toString();
        QueryWrapper<OrderAfterSale> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getOrderItemId), orderItemId);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(OrderAfterSale::getCreateTime));
        List<OrderAfterSale> list = list(wrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            memberService.setDataMation(list, OrderAfterSale::getCreateId);
        }
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
        if (CollectionUtil.isNotEmpty(list)) {
            outputObject.setBean(list.get(0));
        }
    }

    @Override
    public void queryOrderAfterSaleById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        OrderAfterSale afterSale = selectById(id);
        if (afterSale == null || StrUtil.isEmpty(afterSale.getId())) {
            throw new CustomException("售后单不存在");
        }
        OrderItem item = orderItemService.selectById(afterSale.getOrderItemId());
        List<OrderItem> enriched = orderItemService.setDateForItemLIst(Arrays.asList(item));
        afterSale.setOrderItemMation(CollectionUtil.isNotEmpty(enriched) ? enriched.get(0) : item);
        outputObject.setBean(afterSale);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryMyOrderAfterSaleList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        QueryWrapper<OrderAfterSale> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getCreateId), userId);
        applyAfterSaleListFilter(wrapper, pageInfo);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(OrderAfterSale::getCreateTime));
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<OrderAfterSale> list = list(wrapper);
        fillOrderItemMation(list);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryStoreOrderAfterSaleList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        ShopStore store = shopStoreService.selectById(storeId);
        if (store == null || StrUtil.isEmpty(store.getId())) {
            throw new CustomException("门店不存在");
        }
        Object staffId = InputObject.getLogParamsStatic().get("staffId");
        boolean isStaff = staffId != null && StrUtil.isNotBlank(staffId.toString())
            && !"tmpUserStaffId".equals(staffId.toString());
        if (!userId.equals(store.getCreateId()) && !isStaff) {
            throw new CustomException("无权查看该门店售后");
        }
        QueryWrapper<OrderAfterSale> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getStoreId), storeId)
            .or().eq(MybatisPlusUtil.toColumns(OrderAfterSale::getSourceStoreId), storeId));
        applyAfterSaleListFilter(wrapper, pageInfo);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(OrderAfterSale::getCreateTime));
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<OrderAfterSale> list = list(wrapper);
        memberService.setDataMation(list, OrderAfterSale::getCreateId);
        fillOrderItemMation(list);
        outputObject.settotal(pages.getTotal());
        outputObject.setBeans(list);
    }

    private void applyAfterSaleListFilter(QueryWrapper<OrderAfterSale> wrapper, CommonPageInfo pageInfo) {
        String type = pageInfo.getType();
        if (StrUtil.isNotBlank(type)) {
            if ("1".equals(type)) {
                wrapper.in(MybatisPlusUtil.toColumns(OrderAfterSale::getStatus), ACTIVE_STATUS);
            } else {
                wrapper.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getStatus), Integer.valueOf(type));
            }
        }
        String keyword = pageInfo.getKeyword();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(MybatisPlusUtil.toColumns(OrderAfterSale::getReasonText), keyword)
                .or().like(MybatisPlusUtil.toColumns(OrderAfterSale::getParentId), keyword)
                .or().like(MybatisPlusUtil.toColumns(OrderAfterSale::getOrderItemId), keyword)
                .or().like(MybatisPlusUtil.toColumns(OrderAfterSale::getReturnDeliverNumber), keyword));
        }
    }

    private void fillOrderItemMation(List<OrderAfterSale> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> itemIds = list.stream().map(OrderAfterSale::getOrderItemId)
            .filter(StrUtil::isNotBlank).distinct().collect(java.util.stream.Collectors.toList());
        if (CollectionUtil.isEmpty(itemIds)) {
            return;
        }
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.in(CommonConstants.ID, itemIds);
        List<OrderItem> items = orderItemService.list(itemWrapper);
        List<OrderItem> enriched = orderItemService.setDateForItemLIst(items);
        Map<String, OrderItem> itemMap = new HashMap<>();
        for (OrderItem item : enriched) {
            itemMap.put(item.getId(), item);
        }
        for (OrderAfterSale afterSale : list) {
            afterSale.setOrderItemMation(itemMap.get(afterSale.getOrderItemId()));
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void agreeOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = MapUtilStr(params, "id");
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        OrderAfterSale afterSale = selectById(id);
        assertMerchantHandler(afterSale, userId);
        if (!OrderAfterSaleStatus.WAIT_MERCHANT.getKey().equals(afterSale.getStatus())) {
            throw new CustomException("当前状态不可同意");
        }

        String refundAmount = StrUtil.blankToDefault(MapUtilStr(params, "refundAmount"), afterSale.getApplyAmount());
        if (!OrderAfterSaleType.EXCHANGE.getKey().equals(afterSale.getType())
            && CalculationUtil.compareTo(refundAmount, afterSale.getApplyAmount(), CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) > 0) {
            throw new CustomException("退款金额不能高于申请金额");
        }
        afterSale.setRefundAmount(refundAmount);

        // 是否退货由售后类型决定；商家仅决定是否回补库存
        String restoreStockStr = MapUtilStr(params, "restoreStock");
        afterSale.setRestoreStock(StrUtil.isNotBlank(restoreStockStr)
            ? Integer.valueOf(restoreStockStr) : defaultRestoreStock(afterSale));

        if (OrderAfterSaleType.REFUND_ONLY.getKey().equals(afterSale.getType())) {
            startChannelRefund(afterSale, userId);
        } else {
            afterSale.setStatus(OrderAfterSaleStatus.WAIT_BUYER_RETURN.getKey());
            updateEntity(afterSale, userId);
        }
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void rejectOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = MapUtilStr(params, "id");
        String rejectReason = MapUtilStr(params, "rejectReason");
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        OrderAfterSale afterSale = selectById(id);
        assertMerchantHandler(afterSale, userId);
        if (!OrderAfterSaleStatus.WAIT_MERCHANT.getKey().equals(afterSale.getStatus())
            && !OrderAfterSaleStatus.WAIT_BUYER_RETURN.getKey().equals(afterSale.getStatus())
            && !OrderAfterSaleStatus.WAIT_MERCHANT_RECEIVE.getKey().equals(afterSale.getStatus())) {
            throw new CustomException("当前状态不可拒绝");
        }
        if (StrUtil.isBlank(rejectReason)) {
            throw new CustomException("请填写拒绝原因");
        }
        afterSale.setRejectReason(rejectReason);
        afterSale.setStatus(OrderAfterSaleStatus.REJECTED.getKey());
        updateEntity(afterSale, userId);
        restoreItemState(afterSale);
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void fillAfterSaleReturnLogistics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = MapUtilStr(params, "id");
        String returnDeliverNumber = MapUtilStr(params, "returnDeliverNumber");
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        OrderAfterSale afterSale = selectById(id);
        assertBuyerOwner(afterSale, userId);
        if (!OrderAfterSaleStatus.WAIT_BUYER_RETURN.getKey().equals(afterSale.getStatus())) {
            throw new CustomException("当前状态不可填写退货单号");
        }
        if (StrUtil.isBlank(returnDeliverNumber)) {
            throw new CustomException("请填写退货快递单号");
        }
        afterSale.setReturnDeliverNumber(returnDeliverNumber);
        afterSale.setStatus(OrderAfterSaleStatus.WAIT_MERCHANT_RECEIVE.getKey());
        updateEntity(afterSale, userId);
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void confirmAfterSaleReturn(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        OrderAfterSale afterSale = selectById(id);
        assertMerchantHandler(afterSale, userId);
        if (!OrderAfterSaleStatus.WAIT_MERCHANT_RECEIVE.getKey().equals(afterSale.getStatus())) {
            throw new CustomException("当前状态不可确认收货");
        }
        if (OrderAfterSaleType.EXCHANGE.getKey().equals(afterSale.getType())) {
            if (isYes(afterSale.getRestoreStock())) {
                restoreStockIfNeeded(afterSale);
            }
            afterSale.setStatus(OrderAfterSaleStatus.DONE.getKey());
            updateEntity(afterSale, userId);
            orderItemService.editStateById(afterSale.getOrderItemId(),
                String.valueOf(ShopOrderItemOtherState.EXCHANGED.getKey()));
        } else if (OrderAfterSaleType.RETURN_REFUND.getKey().equals(afterSale.getType())) {
            startChannelRefund(afterSale, userId);
        } else {
            throw new CustomException("售后类型不支持确认收货");
        }
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void notifyOrderAfterSaleRefundSuccess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String outRefundNo = MapUtilStr(params, "outRefundNo");
        if (StrUtil.isBlank(outRefundNo)) {
            outRefundNo = MapUtilStr(params, "outTradeNo");
        }
        if (StrUtil.isBlank(outRefundNo)) {
            throw new CustomException("退款单号不能为空");
        }
        QueryWrapper<OrderAfterSale> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getOutRefundNo), outRefundNo);
        OrderAfterSale afterSale = getOne(wrapper, false);
        if (afterSale == null || StrUtil.isEmpty(afterSale.getId())) {
            throw new CustomException("售后退款单不存在");
        }
        if (OrderAfterSaleStatus.DONE.getKey().equals(afterSale.getStatus())) {
            outputObject.setBean(afterSale);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        completeRefundAfterSale(afterSale, MapUtilStr(params, "channelRefundNo"), "system");
        outputObject.setBean(selectById(afterSale.getId()));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void startChannelRefund(OrderAfterSale afterSale, String userId) {
        OrderItem item = orderItemService.selectById(afterSale.getOrderItemId());
        Order order = orderService.selectById(afterSale.getParentId());
        if (order == null || StrUtil.isBlank(order.getPayType())) {
            throw new CustomException("订单支付信息不完整，无法退款");
        }
        String outRefundNo = "R" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        afterSale.setOutRefundNo(outRefundNo);
        afterSale.setStatus(OrderAfterSaleStatus.REFUNDING.getKey());
        afterSale.setRefundChannelStatus(REFUND_WAITING);
        updateEntity(afterSale, userId);

        Map<String, Object> data = new HashMap<>();
        // 支付时 outTradeNo 使用主单或子单 oddNumber，优先子单
        String outTradeNo = StrUtil.blankToDefault(item.getOddNumber(), order.getOddNumber());
        data.put("oddNumber", outTradeNo);
        data.put("outRefundNo", outRefundNo);
        data.put("payPrice", StrUtil.blankToDefault(order.getPayPrice(), resolveItemPayPrice(item)));
        data.put("refundPrice", afterSale.getRefundAmount());
        data.put("reason", StrUtil.blankToDefault(afterSale.getReasonText(), "商品售后退款"));

        Map<String, Object> refundResult;
        try {
            refundResult = iPayService.refund(data, order.getPayType(), StrUtil.EMPTY, MALL_ORDER_PAY_APP_KEY).getBean();
        } catch (Exception ex) {
            afterSale.setRefundChannelStatus(REFUND_FAILURE);
            updateEntity(afterSale, userId);
            throw new CustomException("发起退款失败：" + ex.getMessage());
        }

        Map<String, Object> resp = JSONUtil.toBean(refundResult.get("payRefundRespDTO").toString(), null);
        Integer status = Integer.parseInt(resp.get("status").toString());
        String channelRefundNo = resp.get("channelRefundNo") != null ? resp.get("channelRefundNo").toString() : null;
        afterSale.setChannelRefundNo(channelRefundNo);
        afterSale.setRefundChannelStatus(status);
        if (REFUND_SUCCESS == status) {
            completeRefundAfterSale(afterSale, channelRefundNo, userId);
        } else if (REFUND_FAILURE == status) {
            updateEntity(afterSale, userId);
            throw new CustomException("通道退款失败，请稍后重试");
        } else {
            updateEntity(afterSale, userId);
        }
    }

    private void completeRefundAfterSale(OrderAfterSale afterSale, String channelRefundNo, String userId) {
        if (StrUtil.isNotBlank(channelRefundNo)) {
            afterSale.setChannelRefundNo(channelRefundNo);
        }
        afterSale.setRefundChannelStatus(REFUND_SUCCESS);
        afterSale.setStatus(OrderAfterSaleStatus.DONE.getKey());
        updateEntity(afterSale, userId);

        Integer doneState = OrderAfterSaleType.RETURN_REFUND.getKey().equals(afterSale.getType())
            ? ShopOrderItemOtherState.SALESRETURNED.getKey()
            : ShopOrderItemOtherState.REFUND.getKey();
        orderItemService.editStateById(afterSale.getOrderItemId(), String.valueOf(doneState));

        // 按商家同意时的 restoreStock 决定是否回补
        if (isYes(afterSale.getRestoreStock())) {
            restoreStockIfNeeded(afterSale);
        }
    }

    private Integer defaultRestoreStock(OrderAfterSale afterSale) {
        if (OrderAfterSaleType.RETURN_REFUND.getKey().equals(afterSale.getType())
            || OrderAfterSaleType.EXCHANGE.getKey().equals(afterSale.getType())) {
            return WhetherEnum.ENABLE_USING.getKey();
        }
        // 仅退款：默认已发过货才建议回补，仍可由商家改
        return needRestoreStockForRefundOnly(afterSale)
            ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey();
    }

    private boolean isYes(Integer flag) {
        return WhetherEnum.ENABLE_USING.getKey().equals(flag);
    }

    private boolean needRestoreStockForRefundOnly(OrderAfterSale afterSale) {
        if (!OrderAfterSaleType.REFUND_ONLY.getKey().equals(afterSale.getType())) {
            return false;
        }
        Integer prior = afterSale.getPriorItemState();
        return ShopOrderItemOtherState.PART_DELIVERED.getKey().equals(prior)
            || ShopOrderItemOtherState.ALL_DELIVERED.getKey().equals(prior)
            || ShopOrderItemOtherState.TRANSPORTING.getKey().equals(prior);
    }

    private void restoreStockIfNeeded(OrderAfterSale afterSale) {
        OrderItem item = orderItemService.selectById(afterSale.getOrderItemId());
        if (item == null) {
            return;
        }
        ShopStore sellStore = shopStoreService.selectById(item.getStoreId());
        boolean needRestore = sellStore != null && StoreNature.PERSONAL.getKey().equals(sellStore.getStoreNature());
        if (!needRestore && StrUtil.isNotBlank(item.getSourceStoreId())) {
            needRestore = true;
        }
        if (!needRestore) {
            return;
        }
        Map<String, Object> stockParams = new HashMap<>();
        stockParams.put("storeId", item.getStoreId());
        stockParams.put("materialStoreId", item.getMaterialStoreId());
        stockParams.put("materialId", item.getMaterialId());
        stockParams.put("normsId", item.getNormsId());
        stockParams.put("count", afterSale.getApplyCount());
        iShopStockService.restoreShopStockOnRefund(stockParams);
    }

    private void restoreItemState(OrderAfterSale afterSale) {
        if (afterSale.getPriorItemState() != null) {
            orderItemService.editStateById(afterSale.getOrderItemId(), String.valueOf(afterSale.getPriorItemState()));
        }
    }

    private void assertNoActiveAfterSale(String orderItemId) {
        QueryWrapper<OrderAfterSale> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(OrderAfterSale::getOrderItemId), orderItemId);
        wrapper.in(MybatisPlusUtil.toColumns(OrderAfterSale::getStatus), ACTIVE_STATUS);
        if (count(wrapper) > 0) {
            throw new CustomException("该子单已有进行中的售后，请勿重复申请");
        }
    }

    private void validateApplyEligibility(OrderItem item, Integer type) {
        Integer state = item.getState();
        if (OrderAfterSaleType.REFUND_ONLY.getKey().equals(type)) {
            if (!REFUND_ONLY_STATES.contains(state)) {
                throw new CustomException("当前订单状态不支持仅退款，请选择退货退款或换货");
            }
            return;
        }
        if (OrderAfterSaleType.RETURN_REFUND.getKey().equals(type)
            || OrderAfterSaleType.EXCHANGE.getKey().equals(type)) {
            if (!RETURN_EXCHANGE_STATES.contains(state)) {
                throw new CustomException("当前订单状态不支持退货/换货");
            }
            return;
        }
        throw new CustomException("不支持的售后类型");
    }

    private String resolveItemPayPrice(OrderItem item) {
        if (StrUtil.isNotBlank(item.getAdjustPrice())
            && !StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), item.getAdjustPrice())) {
            return item.getAdjustPrice();
        }
        return StrUtil.blankToDefault(item.getPayPrice(), CommonNumConstants.NUM_ZERO.toString());
    }

    private void assertBuyerOwner(OrderAfterSale afterSale, String userId) {
        if (afterSale == null || StrUtil.isEmpty(afterSale.getId())) {
            throw new CustomException("售后单不存在");
        }
        if (!userId.equals(afterSale.getCreateId())) {
            throw new CustomException("无权操作该售后单");
        }
    }

    private void assertMerchantHandler(OrderAfterSale afterSale, String userId) {
        if (afterSale == null || StrUtil.isEmpty(afterSale.getId())) {
            throw new CustomException("售后单不存在");
        }
        String handleStoreId = StrUtil.isNotBlank(afterSale.getSourceStoreId())
            ? afterSale.getSourceStoreId() : afterSale.getStoreId();
        ShopStore store = shopStoreService.selectById(handleStoreId);
        if (store == null || StrUtil.isEmpty(store.getId())) {
            throw new CustomException("门店不存在");
        }
        // 门店主可处理；管理端员工（有 staffId）也可处理
        if (userId.equals(store.getCreateId())) {
            return;
        }
        Object staffId = InputObject.getLogParamsStatic().get("staffId");
        boolean isStaff = staffId != null && StrUtil.isNotBlank(staffId.toString())
            && !"tmpUserStaffId".equals(staffId.toString());
        if (!isStaff) {
            throw new CustomException("无权处理该售后单");
        }
    }

    private String MapUtilStr(Map<String, Object> params, String key) {
        Object val = params.get(key);
        return val == null ? null : val.toString();
    }
}
