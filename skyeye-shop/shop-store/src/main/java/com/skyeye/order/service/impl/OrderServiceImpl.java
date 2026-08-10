/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.entity.CouponStore;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.entity.CouponUseMaterial;
import com.skyeye.coupon.enums.CouponStoreCoverage;
import com.skyeye.coupon.enums.CouponUseState;
import com.skyeye.coupon.enums.CouponValidityType;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.enums.PromotionMaterialScope;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponStoreService;
import com.skyeye.coupon.service.CouponUseMaterialService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderDao;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.*;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.rest.pay.service.IPayService;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.entity.ShopAddress;
import com.skyeye.store.entity.ShopAddressHistory;
import com.skyeye.store.service.ShopAddressHistoryService;
import com.skyeye.store.service.ShopAddressService;
import com.skyeye.store.service.ShopTradeCartService;
import com.xxl.job.core.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: OrderServiceImpl
 * @Description: 商品订单管理--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单管理", groupName = "商品订单管理", tenant = TenantEnum.NO_ISOLATION)
public class OrderServiceImpl extends SkyeyeBusinessServiceImpl<OrderDao, Order> implements OrderService {

    /**
     * 商城 PayApp.appKey，与 PayApp 配置、queryEnabledPayChannelList 一致
     */
    private static final String MALL_ORDER_PAY_APP_KEY = "mall-order";

    private static final Integer PAY_STATUS_SUCCESS = 10;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private IPayService iPayService;

    @Autowired
    private ShopAddressService shopAddressService;

    @Autowired
    private ShopAddressHistoryService shopAddressHistoryService;

    @Autowired
    private CouponUseMaterialService couponUseMaterialService;

    @Autowired
    private CouponStoreService couponStoreService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private ShopTradeCartService shopTradeCartService;

    @Override
    public void createPrepose(Order order) {
        if (order == null && ObjUtil.isEmpty(order)) {
            throw new CustomException("订单对象不能为空");
        }
        // 订单编号
        Map<String, Object> business = BeanUtil.beanToMap(order);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        order.setOddNumber(oddNumber);
        order.setCount(CommonNumConstants.NUM_ZERO);// 商品总数
        order.setCommentState(ShopOrderCommentState.UNFINISHED.getKey());// 评价状态
        order.setTotalPrice(CommonNumConstants.NUM_ZERO.toString());
        order.setDiscountPrice(CommonNumConstants.NUM_ZERO.toString());
        order.setDeliveryPrice(CommonNumConstants.NUM_ZERO.toString());
        order.setPayPrice(CommonNumConstants.NUM_ZERO.toString());
        // 收货人信息
        ShopAddress shopAddress = shopAddressService.selectById(order.getAddressId());
        order.setReceiverName(shopAddress.getName());
        order.setReceiverMobile(shopAddress.getMobile());
        // 调价
        order.setAdjustPrice("0");
        // 子单的优惠券操作
        checkAndSetItemCouponUse(order);
        // ip
        order.setUserIp(IpUtil.getLocalAddress().toString());
        order.setState(ShopOrderState.UNPAID.getKey());
        //  物流联通后，此项需要修改
        checkAndSetDeliveryPrice(order);
        // 积分操作方法， 此方法未进行任何操作，可对此方法进行任何操作
        checkAndSetVariable(order);
        // 活动信息及积分操作方法
        checkAndSetActive(order);
    }

    private void checkAndSetItemCouponUse(Order order) {// 子单的优惠券操作
        List<OrderItem> orderItemList = order.getOrderItemList();
        if (orderItemList == null || orderItemList.isEmpty()) {
            throw new CustomException("订单子项列表不能为空");
        }
        // 设置商品信息、商品规格信息和优惠券信息
        List<String> normsIdList = orderItemList.stream().map(OrderItem::getNormsId).collect(Collectors.toList());
        List<Map<String, Object>> normsListMap = iShopMaterialNormsService.queryShopMaterialByNormsIdList(Joiner.on(CommonCharConstants.COMMA_MARK).join(normsIdList));
        Map<String, String> normsPriceMap = normsListMap.stream()
            .collect(Collectors.toMap(map -> map.get("normsId").toString(), map -> map.get("salePrice").toString()));
        for (OrderItem orderItem : orderItemList) {// 计算每一个子单的总价
            if (!normsPriceMap.containsKey(orderItem.getNormsId())) {
                throw new CustomException("商城不存在normsId: " + orderItem.getNormsId());
            }
            // 获取子单单价  元 -> 分
            String salePrice = CalculationUtil.multiply(normsPriceMap.get(orderItem.getNormsId()), "100");
            // 设置子单总价
            String price = CalculationUtil.multiply(String.valueOf(orderItem.getCount()), salePrice, CommonNumConstants.NUM_SIX);
            orderItem.setPrice(price);
            orderItem.setPayPrice(price);
            orderItem.setDiscountPrice("0");
            // 总单商品数量、子单状态、总单原价、总单应付金额
            order.setCount(order.getCount() + orderItem.getCount());
            orderItem.setCommentState(ShopOrderCommentState.UNFINISHED.getKey());
            order.setTotalPrice(CalculationUtil.add(order.getTotalPrice(), orderItem.getPrice(), CommonNumConstants.NUM_SIX));
            order.setPayPrice(CalculationUtil.add(order.getPayPrice(), orderItem.getPayPrice(), CommonNumConstants.NUM_SIX));
        }
        checkCouponUseMaterial(order);//  将总单的couponUserId赋值到对应子单
    }

    private void checkAndSetDeliveryPrice(Order order) {
        order.setDeliveryPrice(StrUtil.isEmpty(order.getDeliveryPrice()) ? "0" : order.getDeliveryPrice());
    }

    private void checkAndSetVariable(Order order) {
    }

    private void checkAndSetActive(Order order) {
    }

    private void checkCouponUseMaterial(Order order) {
        String couponUseId = order.getCouponUseId();//优惠券id
        double totalPrice = Double.parseDouble(order.getTotalPrice());//总单原价
        if (StrUtil.isEmpty(couponUseId)) {//没有使用优惠券
            return;
        }
        CouponUse couponUse = couponUseService.selectById(couponUseId);//优惠券信息
        if (ObjectUtil.isEmpty(couponUse)) {
            throw new CustomException("优惠券不存在");
        } else if (couponUse.getState() != CouponUseState.UNUSED.getKey()) {
            throw new CustomException("该优惠券已使用或已过期");
        } else if (Objects.equals(couponUse.getDiscountType(), PromotionDiscountType.PERCENT.getKey())
            && Double.parseDouble(couponUse.getUsePrice()) > totalPrice) {
            throw new CustomException("优惠券不满足使用金额");
        }
        List<OrderItem> orderItemList = order.getOrderItemList();//子单列表
        OrderItem orderItem = null;//优惠券使用商品
        if (Objects.equals(couponUse.getProductScope(), PromotionMaterialScope.ALL.getKey())) {// 全部商品
            orderItem = orderItemList.stream().max(Comparator.comparing(OrderItem::getPrice)).orElse(null);// 获取优惠券使用商品列表中，价格最高的商品
            setOrderAndOrderItem(couponUse, order, orderItem);// 操作订单和子单的优惠券
        } else if (Objects.equals(couponUse.getProductScope(), PromotionMaterialScope.SPU.getKey())) {// 指定商品
            List<String> couponUseMaterialIds = couponUseMaterialService.queryListByCouponIds(Collections.singletonList(couponUseId))
                .stream().map(CouponUseMaterial::getMaterialId).collect(Collectors.toList());// 收集子单商品id
            List<OrderItem> newOrderItemList = new ArrayList<>();
            for (OrderItem item : orderItemList) {// 筛选出优惠券可用的商品
                if (couponUseMaterialIds.contains(item.getMaterialId())) {
                    newOrderItemList.add(item);
                }
            }
            if (CollectionUtil.isEmpty(newOrderItemList)) {
                throw new CustomException("商品列表不存在满足优惠券的使用对象");
            }
            orderItem = newOrderItemList.stream().max(Comparator.comparing(OrderItem::getPrice)).orElse(null);// 获取优惠券使用商品列表中，价格最高的商品
            setOrderAndOrderItem(couponUse, order, orderItem);// 操作订单和子单的优惠券
        }
        // 删除优惠券定时任务
        deleteJobForCouponUse(couponUse);
    }

    /**
     * 指定门店券：只保留适用门店下的子单
     */
    private List<OrderItem> filterOrderItemsByCouponStore(CouponUse couponUse, List<OrderItem> orderItemList) {
        Coupon coupon = couponService.selectById(couponUse.getCouponId());
        if (ObjectUtil.isEmpty(coupon)
            || !Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.SPECIFIED_STORE.getKey())) {
            return orderItemList;
        }
        List<String> couponStoreIds = couponStoreService.queryListByCouponId(coupon.getId()).stream()
            .map(CouponStore::getStoreId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        List<OrderItem> storeEligibleItems = orderItemList.stream()
            .filter(item -> couponStoreIds.contains(item.getStoreId()))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(storeEligibleItems)) {
            throw new CustomException("当前门店不在优惠券适用范围内");
        }
        return storeEligibleItems;
    }

    @Autowired
    private CouponService couponService;

    private void deleteJobForCouponUse(CouponUse couponUse) {
        Coupon coupon = couponService.getById(couponUse.getCouponId());
        if (coupon.getValidityType() == CouponValidityType.TERM.getKey()) {
            // 删除优惠券定时任务
            iQuartzService.stopAndDeleteTaskQuartz(couponUse.getId());
        }
    }

    private void setOrderAndOrderItem(CouponUse couponUse, Order order, OrderItem targetOrderItem) {
        if (targetOrderItem == null) {
            throw new CustomException("目标订单子项不能为空");
        }
        if (Objects.equals(couponUse.getDiscountType(), PromotionDiscountType.PERCENT.getKey())) {// 百分比折扣
            for (OrderItem item : order.getOrderItemList()) {// 找到目标子单
                if (item.getNormsId().equals(targetOrderItem.getNormsId())) {
                    item.setCouponUseId(order.getCouponUseId());
                    couponUseService.UpdateUsedCount(order.getCouponUseId());// 修改优惠券使用次数
                    // 操作优惠券
                    String discountPercentInt = CalculationUtil.divide(couponUse.getDiscountPercent().toString(), "100", CommonNumConstants.NUM_SIX);
                    // 百分比的折后价
                    String percentPrice = CalculationUtil.multiply(targetOrderItem.getPrice(), discountPercentInt, CommonNumConstants.NUM_SIX);
                    // 百分比折扣的优惠价格
                    String percentDiscountPrice = CalculationUtil.subtract(targetOrderItem.getPrice(), percentPrice, CommonNumConstants.NUM_SIX);
                    // 折扣上限
                    String discountLimitPrice = couponUse.getDiscountLimitPrice();
                    // 折扣上限的折后价
                    String limitPrice = CalculationUtil.subtract(targetOrderItem.getPrice(), discountLimitPrice, CommonNumConstants.NUM_SIX);
                    // 是否超过折扣上限
                    String highPrice = CalculationUtil.getMax(percentDiscountPrice, discountLimitPrice, CommonNumConstants.NUM_SIX);
                    // 设置应支付价格和优惠价格
                    if (Double.parseDouble(highPrice) == Double.parseDouble(discountLimitPrice)) { // 未超过优惠价
                        item.setPayPrice(percentPrice);
                        item.setCouponPrice(percentDiscountPrice);
                        // 修改总单总价
                        order.setPayPrice(CalculationUtil.subtract(order.getPayPrice(), percentDiscountPrice, CommonNumConstants.NUM_SIX));
                        order.setCouponPrice(percentDiscountPrice);
                    } else {// 超过优惠价
                        item.setPayPrice(limitPrice);
                        item.setCouponPrice(discountLimitPrice);
                        // 修改总单总价
                        order.setPayPrice(CalculationUtil.subtract(order.getPayPrice(), discountLimitPrice, CommonNumConstants.NUM_SIX));
                        order.setCouponPrice(discountLimitPrice);
                    }
                    break;
                }
            }
        } else {// 满减：只在适用商品/门店子单间按原价占比分摊
            List<OrderItem> eligibleItems = order.getOrderItemList();
            if (Objects.equals(couponUse.getProductScope(), PromotionMaterialScope.SPU.getKey())) {
                List<String> couponUseMaterialIds = couponUseMaterialService.queryListByCouponIds(Collections.singletonList(order.getCouponUseId()))
                    .stream().map(CouponUseMaterial::getMaterialId).collect(Collectors.toList());
                eligibleItems = order.getOrderItemList().stream()
                    .filter(item -> couponUseMaterialIds.contains(item.getMaterialId()))
                    .collect(Collectors.toList());
            }
            // 再按适用门店过滤
            eligibleItems = filterOrderItemsByCouponStore(couponUse, eligibleItems);
            // 满减门槛按适用子单合计校验
            String eligibleTotalPrice = CommonNumConstants.NUM_ZERO.toString();
            for (OrderItem item : eligibleItems) {
                eligibleTotalPrice = CalculationUtil.add(eligibleTotalPrice, item.getPrice(), CommonNumConstants.NUM_TWO);
            }
            if (Double.parseDouble(couponUse.getUsePrice()) > Double.parseDouble(eligibleTotalPrice)) {
                throw new CustomException("优惠券不满足使用金额");
            }
            couponUseService.UpdateUsedCount(order.getCouponUseId());
            allocateFullReductionCouponToItems(order, couponUse.getDiscountPrice(), eligibleItems, eligibleTotalPrice);
        }
    }

    /**
     * 满减券只在适用商品/门店子单间按原价占比分摊
     */
    private void allocateFullReductionCouponToItems(Order order, String discountPrice, List<OrderItem> eligibleItems,
                                                    String eligibleTotalPrice) {
        String orderPayPrice = CalculationUtil.subtract(order.getTotalPrice(), discountPrice, CommonNumConstants.NUM_TWO);
        order.setPayPrice(orderPayPrice);
        order.setCouponPrice(discountPrice);

        if (eligibleItems.size() == CommonNumConstants.NUM_ONE) {
            OrderItem only = eligibleItems.get(CommonNumConstants.NUM_ZERO);
            only.setCouponUseId(order.getCouponUseId());
            only.setCouponPrice(discountPrice);
            only.setPayPrice(CalculationUtil.subtract(only.getPrice(), discountPrice, CommonNumConstants.NUM_TWO));
            return;
        }

        String allocatedCoupon = CommonNumConstants.NUM_ZERO.toString();
        for (OrderItem item : eligibleItems.subList(CommonNumConstants.NUM_ZERO, eligibleItems.size() - CommonNumConstants.NUM_ONE)) {
            item.setCouponUseId(order.getCouponUseId());
            String itemCoupon = CalculationUtil.divide(
                CalculationUtil.multiply(discountPrice, item.getPrice(), CommonNumConstants.NUM_SIX),
                eligibleTotalPrice, CommonNumConstants.NUM_TWO);
            String itemPay = CalculationUtil.subtract(item.getPrice(), itemCoupon, CommonNumConstants.NUM_TWO);
            item.setCouponPrice(itemCoupon);
            item.setPayPrice(itemPay);
            allocatedCoupon = CalculationUtil.add(allocatedCoupon, itemCoupon, CommonNumConstants.NUM_TWO);
        }
        OrderItem lastItem = eligibleItems.get(eligibleItems.size() - CommonNumConstants.NUM_ONE);
        lastItem.setCouponUseId(order.getCouponUseId());
        String lastCoupon = CalculationUtil.subtract(discountPrice, allocatedCoupon, CommonNumConstants.NUM_TWO);
        lastItem.setCouponPrice(lastCoupon);
        lastItem.setPayPrice(CalculationUtil.subtract(lastItem.getPrice(), lastCoupon, CommonNumConstants.NUM_TWO));
    }

    @Override
    public void createPostpose(Order order, String userId) {
        orderItemService.setValueAndCreateEntity(order, userId);
        couponUseService.updateState(order.getCouponUseId());// 更新用户领取的优惠券状态
        log.info("订单id:" + order.getId() + "创建定时任务-- 开始");
        startUpTaskQuartz(order.getId(), order.getOddNumber(), DateUtil.getTimeAndToString());
        log.info("订单id:" + order.getId() + "创建定时任务-- 结束");
        shopTradeCartService.deleteMySelect(userId);
    }

    private void startUpTaskQuartz(String name, String title, String delayedTime) {
        /// 处理日期  此处delayedTime为当前日期
        Date stringToDate = DateUtil.getPointTime(delayedTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
        Date afterOneDay = DateUtil.getAfDate(stringToDate, 1, "d");
        DateFormat df = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
        String lastTime = df.format(afterOneDay);
        // 正式准备启动定时任务
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(lastTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.SHOP_ORDER_CREATE.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Integer> stateList = new ArrayList<>();
        switch (commonPageInfo.getType()) {
            case "1": // 待支付
                stateList = Arrays.asList(new Integer[]{ShopOrderItemOtherState.WAIT_PAY.getKey()}); // 待支付
                break;
            case "2": // 待发货
                stateList = Arrays.asList(new Integer[]{ShopOrderItemOtherState.WAIT_DELIVER.getKey()}); // 待发货
                break;
            case "3": // 待收货
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.ALL_DELIVERED.getKey(), // 全部发货
                    ShopOrderItemOtherState.TRANSPORTING.getKey()});// 运输中
                break;
            case "4": // 评价
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.UNEVALUATE.getKey(), // 待评价
                    ShopOrderItemOtherState.EVALUATED.getKey(), // 已评价
                    ShopOrderItemOtherState.PARTIALEVALUATION.getKey(), // 部分评价
                    ShopOrderItemOtherState.SIGN.getKey(), // 已签收
                    ShopOrderItemOtherState.COMPLETED.getKey(), // 已完成
                    ShopOrderItemOtherState.PARTIALLYDONE.getKey()}); // 部分完成
                break;
            case "5": // 已取消
                stateList = Arrays.asList(new Integer[]{ShopOrderItemOtherState.CANCELED.getKey()});
                break;
            case "6": // 处理中
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.REFUNDING.getKey(),  // 退款中
                    ShopOrderItemOtherState.SALESRETURNING.getKey(),//退货中
                    ShopOrderItemOtherState.EXCHANGEING.getKey()});//换货中
                break;
            case "7": // 申请记录
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.REFUND.getKey(),     // 已退款
                    ShopOrderItemOtherState.SALESRETURNED.getKey(),//已退货
                    ShopOrderItemOtherState.EXCHANGED.getKey()});//已换货
        }
        MPJLambdaWrapper<Order> wrapper = JoinWrappers.lambda("o", Order.class);
        if (CollectionUtil.isNotEmpty(stateList)) {
            wrapper.innerJoin(OrderItem.class, "oi", OrderItem::getParentId, Order::getId)
                .in("oi." + MybatisPlusUtil.toColumns(OrderItem::getState), stateList)
                .distinct();
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            wrapper.like(Order::getOddNumber, commonPageInfo.getKeyword());
        }
        wrapper.selectAll(Order.class);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(Order::getCreateTime));
        List<Order> list = skyeyeBaseMapper.selectJoinList(Order.class, wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return CollectionUtil.newArrayList();
        }
        List<String> idList = list.stream().map(Order::getId).collect(Collectors.toList());
        Map<String, List<OrderItem>> mapByIds = orderItemService.queryListByParentId(idList);
        for (Order order : list) {
            order.setOrderItemList(mapByIds.containsKey(order.getId()) ? mapByIds.get(order.getId()) : new ArrayList<>());
        }
        iAreaService.setDataMation(list, Order::getProvinceId);
        iAreaService.setDataMation(list, Order::getCityId);
        iAreaService.setDataMation(list, Order::getAreaId);
        iAreaService.setDataMation(list, Order::getTownshipId);
        setAddressMationForList(list);
        // 分页查询时获取数据
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }

    @Override
    public void queryOrderPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Integer> stateList = new ArrayList<>();
        switch (StrUtil.isEmpty(commonPageInfo.getType()) ? CommonNumConstants.NUM_ZERO.toString() : commonPageInfo.getType()) {
            case "1": // 待支付
                stateList = Arrays.asList(ShopOrderItemOtherState.WAIT_PAY.getKey()); // 待支付
                break;
            case "2": // 待发货
                stateList = Arrays.asList(ShopOrderItemOtherState.WAIT_DELIVER.getKey()); // 待发货
                break;
            case "3": // 待收货
                stateList = Arrays.asList(ShopOrderItemOtherState.ALL_DELIVERED.getKey(), // 全部发货
                    ShopOrderItemOtherState.TRANSPORTING.getKey());// 运输中
                break;
            case "4": // 评价
                stateList = Arrays.asList(ShopOrderItemOtherState.UNEVALUATE.getKey(), // 待评价
                    ShopOrderItemOtherState.EVALUATED.getKey(), // 已评价
                    ShopOrderItemOtherState.PARTIALEVALUATION.getKey(), // 部分评价
                    ShopOrderItemOtherState.SIGN.getKey(), // 已签收
                    ShopOrderItemOtherState.COMPLETED.getKey(), // 已完成
                    ShopOrderItemOtherState.PARTIALLYDONE.getKey()); // 部分完成
                break;
            case "5": // 已取消
                stateList = Arrays.asList(ShopOrderItemOtherState.CANCELED.getKey());
                break;
            case "6": // 处理中
                stateList = Arrays.asList(ShopOrderItemOtherState.REFUNDING.getKey(),  // 退款中
                    ShopOrderItemOtherState.SALESRETURNING.getKey(),//退货中
                    ShopOrderItemOtherState.EXCHANGEING.getKey());//换货中
                break;
            case "7": // 申请记录
                stateList = Arrays.asList(ShopOrderItemOtherState.REFUND.getKey(),     // 已退款
                    ShopOrderItemOtherState.SALESRETURNED.getKey(),//已退货
                    ShopOrderItemOtherState.EXCHANGED.getKey());//已换货
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getCreateId), userId);
        if (CollectionUtil.isNotEmpty(stateList)) {
            wrapper.in(MybatisPlusUtil.toColumns(OrderItem::getState), stateList);
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(OrderItem::getCreateTime));
        List<OrderItem> orderItemList = orderItemService.list(wrapper);
        if (CollectionUtil.isEmpty(orderItemList)) {
            return;
        }
        orderItemList = orderItemService.setDateForItemLIst(orderItemList);
        List<Map<String, Object>> shopMaterialStoreList = new ArrayList<>(orderItemList.stream()
            .filter(item -> StrUtil.isNotBlank(item.getMaterialStoreId()) && CollectionUtil.isNotEmpty(item.getShopMaterial()))
            .collect(Collectors.toMap(OrderItem::getMaterialStoreId, OrderItem::getShopMaterial, (a, b) -> a, LinkedHashMap::new))
            .values());
        outputObject.setCustomBeans("ShopMaterialStoreList", shopMaterialStoreList);
        outputObject.setBeans(orderItemList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void updateOrderToPayState(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        // 先判断是否子单（叶子）；不是再按父单处理
        OrderItem orderItem = orderItemService.getById(id);
        if (ObjectUtil.isNotEmpty(orderItem) && StrUtil.isNotEmpty(orderItem.getId())) {
            // 子单：只改自己；若父单下已无其他未付子单，再完结父单
            if (!Objects.equals(orderItem.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey())) {
                throw new CustomException("该子单状态不为待支付，不可完成支付");
            }
            Order parentOrder = super.selectById(orderItem.getParentId());
            if (ObjectUtil.isEmpty(parentOrder) || StrUtil.isEmpty(parentOrder.getId())) {
                throw new CustomException("父订单不存在");
            }
            Integer parentState = parentOrder.getState();
            if (!isParentOrderPayable(parentState)) {
                throw new CustomException("当前订单状态不为待支付、部分支付或支付失败状态，不可修改");
            }
            orderItemService.editStateById(orderItem.getId(), String.valueOf(ShopOrderItemOtherState.WAIT_DELIVER.getKey()));

            List<OrderItem> orderItemList = orderItemService.queryOrderItemByParentId(orderItem.getParentId());
            boolean hasOtherUnpaid = orderItemList.stream()
                .anyMatch(item -> Objects.equals(item.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey()));
            UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, orderItem.getParentId());
            if (!hasOtherUnpaid) {
                updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.PAY_SUCCESS.getKey());
                update(updateWrapper);
                refreshCache(orderItem.getParentId());
                log.info("订单id" + orderItem.getParentId() + "支付成功--删除定时任务-- 开始");
                iQuartzService.stopAndDeleteTaskQuartz(orderItem.getParentId());
                log.info("订单id" + orderItem.getParentId() + "支付成功--删除定时任务-- 结束");
            } else {
                // 仍有待支付子单：主单为部分支付
                updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.PARTIAL_PAID.getKey());
                update(updateWrapper);
                refreshCache(orderItem.getParentId());
            }
            return;
        }

        // 父单：仅待支付子单改为待发货，再完结父单
        Order order = selectById(id);
        Integer state = order.getState();
        if (isParentOrderPayable(state)) {
            List<OrderItem> waitPayList = orderItemService.queryOrderItemByParentId(id).stream()
                .filter(item -> Objects.equals(item.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey()))
                .collect(Collectors.toList());
            for (OrderItem item : waitPayList) {
                orderItemService.editStateById(item.getId(), String.valueOf(ShopOrderItemOtherState.WAIT_DELIVER.getKey()));
            }
            UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.PAY_SUCCESS.getKey());
            update(updateWrapper);
            refreshCache(id);
            log.info("订单id" + id + "支付成功--删除定时任务-- 开始");
            iQuartzService.stopAndDeleteTaskQuartz(id);
            log.info("订单id" + id + "支付成功--删除定时任务-- 结束");
        } else {
            throw new CustomException("当前订单状态不为待支付、部分支付或支付失败状态，不可修改");
        }
    }

    @Override
    public void deletePostpose(List<String> ids) {
        orderItemService.deleteByPerentIds(ids);
    }

    @Override
    @IgnoreTenant
    public Order selectById(String id) {
        Order order = super.selectById(id);
        Map<String, List<OrderItem>> orderItemList = orderItemService.queryListByParentId(Collections.singletonList(id));
        order.setOrderItemList(orderItemList.get(order.getId()));
        iAreaService.setDataMation(order, Order::getProvinceId);
        iAreaService.setDataMation(order, Order::getCityId);
        iAreaService.setDataMation(order, Order::getAreaId);
        iAreaService.setDataMation(order, Order::getTownshipId);
        List<Order> orderList = setAddressMationForList(Collections.singletonList(order));
        refreshCache(id);
        return orderList.get(CommonNumConstants.NUM_ZERO);
    }

    private List<Order> setAddressMationForList(List<Order> orderList) {
        Map<String, Map<String, Object>> addressMap = new HashMap<>();
        List<String> addressTableIdList = orderList.stream().filter(order -> Objects.equals(order.getAddressFromType(), AddressFromTypeEnums.ADDRESS_TABLE.getKey()))
            .map(Order::getAddressId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(addressTableIdList)) {
            addressMap.putAll(shopAddressService.queryListByIds(addressTableIdList));
        }
        List<String> addressHistoryIdList = orderList.stream().filter(order -> Objects.equals(order.getAddressFromType(), AddressFromTypeEnums.ADDRESS_HISTORY_TABLE.getKey()))
            .map(Order::getAddressId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(addressHistoryIdList)) {
            addressMap.putAll(shopAddressHistoryService.queryListByIds(addressHistoryIdList));
        }
        if (CollectionUtil.isNotEmpty(addressMap)) {
            orderList.forEach(order -> {
                if (addressMap.containsKey(order.getAddressId())) {
                    order.setAddressMation(addressMap.get(order.getAddressId()));
                }
            });
        }
        return orderList;
    }

    @Override
    public void cancelOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Object cancelType = params.get("cancelType");
        OrderItem orderItem = orderItemService.getById(id);
        if (ObjectUtil.isEmpty(orderItem) || StrUtil.isEmpty(orderItem.getId())) {
            throw new CustomException("子单不存在");
        }
        if (!Objects.equals(orderItem.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey())
            && !Objects.equals(orderItem.getState(), ShopOrderItemOtherState.WAIT_DELIVER.getKey())) {
            throw new CustomException("该子单不可取消");
        }
        // 只取消当前子单
        orderItemService.editStateById(orderItem.getId(), String.valueOf(ShopOrderItemOtherState.CANCELED.getKey()));
        List<OrderItem> itemList = orderItemService.queryOrderItemByParentId(orderItem.getParentId());
        boolean allCanceled = CollectionUtil.isNotEmpty(itemList)
            && itemList.stream().allMatch(item -> Objects.equals(item.getState(), ShopOrderItemOtherState.CANCELED.getKey()));
        if (!allCanceled) {
            return;
        }
        // 全部子单都取消后，回写主单取消信息并停掉超时任务
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, orderItem.getParentId());
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (!Objects.equals(one.getState(), ShopOrderState.PAY_SUCCESS.getKey())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNPAID.getKey());
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelType), cancelType);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        log.info("订单id" + one.getId() + "取消订单--取消定时任务-- 开始");
        iQuartzService.stopAndDeleteTaskQuartz(one.getId());
        log.info("订单id" + one.getId() + "取消订单--取消定时任务-- 结束");
        refreshCache(one.getId());
    }

    @Override
    public void finishOrder(InputObject inputObject, OutputObject outputObject) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, inputObject.getParams().get("id"));
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        List<Integer> stateList = Arrays.asList(ShopOrderItemOtherState.SIGN.getKey(),
            ShopOrderItemOtherState.UNEVALUATE.getKey(), ShopOrderItemOtherState.EVALUATED.getKey());
        List<OrderItem> itemList = orderItemService.queryOrderItemByParentId(one.getId());
        boolean canFinish = CollectionUtil.isNotEmpty(itemList)
            && itemList.stream().allMatch(item -> stateList.contains(item.getState()));
        if (canFinish) {// 子单均处于签收、待评价、已评价状态时，才可以完成订单
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getFinishTime), DateUtil.getTimeAndToString());
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getReceiveTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            orderItemService.updateDeliverStateByParentId(one.getId(), ShopOrderItemOtherState.COMPLETED.getKey());
            refreshCache(one.getId());
        } else {
            throw new CustomException("不可完成订单。");
        }
    }

    /**
     * 商城订单发起支付（Feign 调用 promote 统一 PayService，逻辑同租户 payTenantSelfPurchaseOrder）。
     * id 可为子单 id或主单 id。
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void payOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String channelCode = params.get("channelCode").toString();
        String returnUrl = params.get("returnUrl").toString();
        String channelExtras = params.get("channelExtras").toString();
        // 子单单独支付：发起支付后落单复用 updateOrderToPayState
        OrderItem orderItem = orderItemService.getById(id);
        if (ObjectUtil.isNotEmpty(orderItem) && StrUtil.isNotEmpty(orderItem.getId())) {
            if (!Objects.equals(orderItem.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey())) {
                throw new CustomException("该子单不可支付");
            }
            Order parentOrder = super.selectById(orderItem.getParentId());
            if (ObjectUtil.isEmpty(parentOrder) || StrUtil.isEmpty(parentOrder.getId())) {
                throw new CustomException("父订单不存在");
            }
            if (!isParentOrderPayable(parentOrder.getState())) {
                throw new CustomException("当前订单状态不为待支付、部分支付或支付失败状态，不可支付");
            }
            Map<String, Object> payData = buildShopOrderItemPayData(orderItem);
            Map<String, Object> payResult = iPayService.payment(payData, channelCode, returnUrl, channelExtras,
                StrUtil.EMPTY, MALL_ORDER_PAY_APP_KEY).getBean();
            handleShopOrderItemPayResult(inputObject, outputObject, orderItem, parentOrder, channelCode, payResult);
            return;
        }
        Order one = getPayableShopOrder(id);
        Map<String, Object> payResult = initiateShopOrderPayment(one, channelCode, returnUrl, channelExtras);
        handleShopOrderPayResult(inputObject, outputObject, id, one, channelCode, payResult, true);
    }

    /**
     * 商城支付成功业务回调（配置在 PayApp.orderNotifyUrl，由 promote PayNotify 转发）。
     * outTradeNo 可能是主单编号或子单编号。
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void notifyOrderPaySuccess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String outTradeNo = params.get("outTradeNo").toString();
        String channelCode = params.get("channelCode").toString();
        Map<String, Object> payChannelMap = new HashMap<>();
        Map<String, Object> payOrderRespDTO = new HashMap<>();
        if (params.containsKey("successTime")) {
            payOrderRespDTO.put("successTime", params.get("successTime"));
        }
        // 先按子单编号匹配，落单复用 updateOrderToPayState
        QueryWrapper<OrderItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq(MybatisPlusUtil.toColumns(OrderItem::getOddNumber), outTradeNo);
        OrderItem orderItem = orderItemService.getOne(itemQuery, false);
        if (ObjectUtil.isNotEmpty(orderItem)) {
            if (!Objects.equals(orderItem.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey())) {
                return;
            }
            Order parentOrder = super.selectById(orderItem.getParentId());
            if (ObjectUtil.isNotEmpty(parentOrder)) {
                fillOrderPayChannelInfo(parentOrder.getId(), parentOrder.getPayPrice(), payChannelMap, channelCode, payOrderRespDTO);
            }
            params.put("id", orderItem.getId());
            updateOrderToPayState(inputObject, outputObject);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        Order order = queryOrderByOddNumber(outTradeNo);
        if (!isParentOrderPayable(order.getState())) {
            // 已全部支付成功或已取消，幂等忽略
            return;
        }
        completeShopOrderAfterPay(inputObject, outputObject, order, payChannelMap, channelCode, payOrderRespDTO);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 补充展示用 subject/body，oddNumber、payPrice 沿用订单实体字段
     */
    private Map<String, Object> buildShopOrderPayData(Order order) {
        Map<String, Object> payData = BeanUtil.beanToMap(order);
        payData.put("subject", "商城商品订单");
        payData.put("body", "商城订单-" + order.getOddNumber());
        return payData;
    }

    /**
     * 子单支付参数：subject/body，改价优先
     */
    private Map<String, Object> buildShopOrderItemPayData(OrderItem orderItem) {
        Map<String, Object> payData = BeanUtil.beanToMap(orderItem);
        if (StrUtil.isNotBlank(orderItem.getAdjustPrice())
            && !StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), orderItem.getAdjustPrice())) {
            payData.put("payPrice", orderItem.getAdjustPrice());
        }
        payData.put("subject", "商城商品订单");
        payData.put("body", "商城子单-" + orderItem.getOddNumber());
        return payData;
    }

    private Order queryOrderByOddNumber(String oddNumber) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Order::getOddNumber), oddNumber);
        Order order = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("订单不存在");
        }
        return selectById(order.getId());
    }

    /**
     * 更新主单支付渠道、支付时间、渠道费率及支付中心扩展单号。
     */
    private void fillOrderPayChannelInfo(String orderId, String payPrice, Map<String, Object> payChannel,
                                         String channelCode, Map<String, Object> payOrderRespDTO) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, orderId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), channelCode);
        if (payOrderRespDTO.get("successTime") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayTime), payOrderRespDTO.get("successTime").toString());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayTime), DateUtil.getTimeAndToString());
        }
        if (payChannel != null && payChannel.get("feeRate") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getChannelFeeRate), payChannel.get("feeRate").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getChannelFeePrice),
                CalculationUtil.multiply(payPrice, payChannel.get("feeRate").toString()));
        }
        if (payOrderRespDTO.get("id") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getExtensionId), payOrderRespDTO.get("id").toString());
        }
        if (payOrderRespDTO.get("no") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getExtensionNo), payOrderRespDTO.get("no").toString());
        }
        update(updateWrapper);
        refreshCache(orderId);
    }

    /**
     * 整单支付成功落单：渠道信息和复用 updateOrderToPayState
     */
    private void completeShopOrderAfterPay(InputObject inputObject, OutputObject outputObject, Order one,
                                           Map<String, Object> payChannel, String channelCode,
                                           Map<String, Object> payOrderRespDTO) {
        fillOrderPayChannelInfo(one.getId(), one.getPayPrice(), payChannel, channelCode, payOrderRespDTO);
        inputObject.getParams().put("id", one.getId());
        updateOrderToPayState(inputObject, outputObject);
    }

    @Override
    public void updateCommonState(String id, Integer state) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCommentState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void generatePayOrderRrCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String channelCode = params.get("channelCode").toString();
        String returnUrl = params.get("returnUrl").toString();
        String channelExtras = params.get("channelExtras").toString();
        Order one = getPayableShopOrder(id);
        Map<String, Object> payResult = initiateShopOrderPayment(one, channelCode, returnUrl, channelExtras);
        Map<String, Object> payChannel = JSONUtil.toBean(payResult.get("payChannel").toString(), null);
        Map<String, Object> payOrderRespDTO = JSONUtil.toBean(payResult.get("payOrderRespDTO").toString(), null);

        // 仅发起预下单并返回二维码/跳转信息，不在此接口落单；落单由 payOrder 同步成功或 notifyOrderPaySuccess 完成
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), channelCode);
        update(updateWrapper);
        refreshCache(id);
        outputObject.setBean(buildShopOrderPayWaitingResult(id, payOrderRespDTO, payChannel));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Order getPayableShopOrder(String id) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        Order one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (!isParentOrderPayable(one.getState())) {
            throw new CustomException("该订单不可支付。");
        }
        return one;
    }

    /**
     * 主单是否仍可继续支付
     */
    private boolean isParentOrderPayable(Integer state) {
        return Objects.equals(state, ShopOrderState.UNPAID.getKey())
            || Objects.equals(state, ShopOrderState.PARTIAL_PAID.getKey())
            || Objects.equals(state, ShopOrderState.FAIRPAID.getKey());
    }

    private Map<String, Object> initiateShopOrderPayment(Order one, String channelCode, String returnUrl,
                                                         String channelExtras) {
        if (!StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), one.getAdjustPrice())) {
            one.setPayPrice(CalculationUtil.multiply(one.getAdjustPrice(), CommonNumConstants.ONE_HUNDRED.toString()));
        }
        // 整单支付：应付金额只合计仍待支付子单
        List<OrderItem> waitPayList = orderItemService.queryOrderItemByParentId(one.getId()).stream()
            .filter(item -> Objects.equals(item.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey()))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(waitPayList)) {
            throw new CustomException("没有待支付的子单，不可整单支付");
        }
        String remainPayPrice = CommonNumConstants.NUM_ZERO.toString();
        for (OrderItem item : waitPayList) {
            String itemPayPrice = StrUtil.isNotBlank(item.getAdjustPrice())
                && !StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), item.getAdjustPrice())
                ? item.getAdjustPrice() : item.getPayPrice();
            remainPayPrice = CalculationUtil.add(remainPayPrice,
                StrUtil.blankToDefault(itemPayPrice, CommonNumConstants.NUM_ZERO.toString()),
                CommonNumConstants.NUM_SIX);
        }
        one.setPayPrice(remainPayPrice);
        Map<String, Object> payData = buildShopOrderPayData(one);
        return iPayService.payment(payData, channelCode, returnUrl, channelExtras, StrUtil.EMPTY, MALL_ORDER_PAY_APP_KEY).getBean();
    }

    /**
     * 子单支付结果处理：同步成功则填渠道和updateOrderToPayState
     */
    private void handleShopOrderItemPayResult(InputObject inputObject, OutputObject outputObject,
                                              OrderItem orderItem, Order parentOrder, String channelCode,
                                              Map<String, Object> payResult) {
        Map<String, Object> payChannel = JSONUtil.toBean(payResult.get("payChannel").toString(), null);
        Map<String, Object> payOrderRespDTO = JSONUtil.toBean(payResult.get("payOrderRespDTO").toString(), null);
        Integer payStatus = Integer.parseInt(payOrderRespDTO.get("status").toString());
        if (PAY_STATUS_SUCCESS.equals(payStatus)) {
            fillOrderPayChannelInfo(parentOrder.getId(), parentOrder.getPayPrice(), payChannel, channelCode, payOrderRespDTO);
            inputObject.getParams().put("id", orderItem.getId());
            updateOrderToPayState(inputObject, outputObject);
            outputObject.setBean(selectById(parentOrder.getId()));
        } else {
            UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, parentOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), channelCode);
            update(updateWrapper);
            refreshCache(parentOrder.getId());
            outputObject.setBean(buildShopOrderPayWaitingResult(orderItem.getId(), payOrderRespDTO, payChannel));
        }
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * @param returnFullOrderOnSyncSuccess payOrder 同步成功返回完整订单；generatePayOrderRrCode 返回 payOrderRespDTO 结构
     */
    private void handleShopOrderPayResult(InputObject inputObject, OutputObject outputObject, String id, Order one,
                                          String channelCode, Map<String, Object> payResult,
                                          boolean returnFullOrderOnSyncSuccess) {
        Map<String, Object> payChannel = JSONUtil.toBean(payResult.get("payChannel").toString(), null);
        Map<String, Object> payOrderRespDTO = JSONUtil.toBean(payResult.get("payOrderRespDTO").toString(), null);
        Integer payStatus = Integer.parseInt(payOrderRespDTO.get("status").toString());

        if (PAY_STATUS_SUCCESS.equals(payStatus)) {
            completeShopOrderAfterPay(inputObject, outputObject, one, payChannel, channelCode, payOrderRespDTO);
            if (returnFullOrderOnSyncSuccess) {
                outputObject.setBean(selectById(id));
            } else {
                Map<String, Object> result = buildShopOrderPayWaitingResult(id, payOrderRespDTO, payChannel);
                result.put("state", ShopOrderState.PAY_SUCCESS.getKey());
                outputObject.setBean(result);
            }
        } else {
            UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), channelCode);
            update(updateWrapper);
            refreshCache(id);
            outputObject.setBean(buildShopOrderPayWaitingResult(id, payOrderRespDTO, payChannel));
        }
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Map<String, Object> buildShopOrderPayWaitingResult(String id, Map<String, Object> payOrderRespDTO,
                                                               Map<String, Object> payChannel) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", id);
        result.put("state", ShopOrderState.UNPAID.getKey());
        result.put("appKey", MALL_ORDER_PAY_APP_KEY);
        result.put("payOrderRespDTO", payOrderRespDTO);
        result.put("payChannel", payChannel);
        return result;
    }

    @Override
    public void setOrderCancle(String orderId) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, orderId);
        List<OrderItem> itemList = orderItemService.queryOrderItemByParentId(orderId);
        // 超时只取消仍待支付的子单，已支付/待发货的子单不动
        for (OrderItem item : itemList) {
            if (Objects.equals(item.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey())) {
                orderItemService.editStateById(item.getId(), String.valueOf(ShopOrderItemOtherState.CANCELED.getKey()));
            }
        }
        itemList = orderItemService.queryOrderItemByParentId(orderId);
        boolean hasPaidItem = itemList.stream()
            .anyMatch(item -> !Objects.equals(item.getState(), ShopOrderItemOtherState.WAIT_PAY.getKey())
                && !Objects.equals(item.getState(), ShopOrderItemOtherState.CANCELED.getKey()));
        if (hasPaidItem) {
            // 部分子单已支付、其余超时取消：主单为部分支付，并记录超时信息
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.PARTIAL_PAID.getKey())
                .set(MybatisPlusUtil.toColumns(Order::getCancelType), ShopOrderCancelType.PAY_TIMEOUT.getKey())
                .set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
        } else {
            // 全部未付或已取消：主单回到待支付并记录超时取消
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNPAID.getKey())
                .set(MybatisPlusUtil.toColumns(Order::getCancelType), ShopOrderCancelType.PAY_TIMEOUT.getKey())
                .set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
        }
        update(updateWrapper);
        refreshCache(orderId);
    }

    @Override
    public List<Order> queryOrderList(String orderId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Order::getId), orderId);
        return list(queryWrapper);
    }

    @Override
    public void updateByAddressId(Map<String, String> addressOldNew) {
        List<String> oldAddressIdList = new ArrayList<>(addressOldNew.keySet());
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Order::getAddressId), oldAddressIdList);
        List<Order> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (Order order : list) {
            order.setAddressId(addressOldNew.get(order.getAddressId()));
            order.setAddressFromType(AddressFromTypeEnums.ADDRESS_HISTORY_TABLE.getKey());
        }
        super.updateEntity(list, InputObject.getLogParamsStatic().get("id").toString());
    }

    @Override
    public void changeOrderAddress(InputObject inputObject, OutputObject outputObject) {
        ShopAddressHistory shopAddressHistory = inputObject.getParams(ShopAddressHistory.class);
        Order order = super.selectById(shopAddressHistory.getOrderId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("订单不存在");
        }
        List<Integer> stateList = Arrays.asList(ShopOrderState.UNPAID.getKey(), ShopOrderState.FAIRPAID.getKey(),
            ShopOrderState.PAY_SUCCESS.getKey(), ShopOrderState.PARTIAL_PAID.getKey());
        if (!stateList.contains(order.getState())) {
            throw new CustomException("订单的当前状态不允许修改收货地址");
        }
        shopAddressHistory.setOrderId(order.getId());
        shopAddressHistory.setId(null);
        shopAddressHistoryService.createEntity(shopAddressHistory, inputObject.getLogParams().get("id").toString());
        order.setAddressId(shopAddressHistory.getId());
        super.updateEntity(order, inputObject.getLogParams().get("id").toString());
        outputObject.setBean(order);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void changeAdjustPriceById(String id, String interpolation) {
        UpdateWrapper<Order> wrapper = new UpdateWrapper<>();
        wrapper.eq(CommonConstants.ID, id);
        Order oldOrder = getOne(wrapper);
        if (StrUtil.isEmpty(oldOrder.getAdjustPrice()) || Double.parseDouble(oldOrder.getAdjustPrice()) <= CommonNumConstants.NUM_ZERO) {
            // 第一次调价
            interpolation = CalculationUtil.add(oldOrder.getPayPrice(), interpolation, CommonNumConstants.NUM_SIX);
        } else {
            // 不是第一次调价
            interpolation = CalculationUtil.add(oldOrder.getAdjustPrice(), interpolation, CommonNumConstants.NUM_SIX);
        }
        // 确保不为负数
        interpolation = Double.parseDouble(interpolation) < CommonNumConstants.NUM_ZERO ? "0" : interpolation;
        wrapper.set(MybatisPlusUtil.toColumns(Order::getAdjustPrice), interpolation);
        update(wrapper);
        refreshCache(id);
    }
}