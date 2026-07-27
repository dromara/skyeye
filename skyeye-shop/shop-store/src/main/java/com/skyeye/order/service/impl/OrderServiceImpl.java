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
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.entity.CouponUseMaterial;
import com.skyeye.coupon.enums.CouponUseState;
import com.skyeye.coupon.enums.CouponValidityType;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.enums.PromotionMaterialScope;
import com.skyeye.coupon.service.CouponService;
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
        } else if (Double.parseDouble(couponUse.getUsePrice()) > totalPrice) {
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
        } else {// 满减 直接在总单减去价格,子单不做处理
            couponUseService.UpdateUsedCount(order.getCouponUseId());// 修改优惠券使用次数
            String discountPrice = couponUse.getDiscountPrice();
            // 折后价
            String afterPrice = CalculationUtil.subtract(order.getTotalPrice(), discountPrice, CommonNumConstants.NUM_SIX);
            order.setPayPrice(afterPrice);
            order.setCouponPrice(discountPrice);
        }
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
            case "1": // 未支付
                stateList = Arrays.asList(new Integer[]{ShopOrderItemOtherState.WAIT_PAY.getKey()});
                break;
            case "2": // 待收货
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.WAIT_DELIVER.getKey(),// 待发货
                    ShopOrderItemOtherState.ALL_DELIVERED.getKey(), //  已发货
                    ShopOrderItemOtherState.TRANSPORTING.getKey()});//运输中
                break;
            case "3":// 已完成
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.SIGN.getKey(),       // 已签收
                    ShopOrderItemOtherState.COMPLETED.getKey(),  // 已完成
                    ShopOrderItemOtherState.UNEVALUATE.getKey(), // 待评价
                    ShopOrderItemOtherState.EVALUATED.getKey(),// 已评价
                    ShopOrderItemOtherState.PARTIALLYDONE.getKey(),//部分完成
                    ShopOrderItemOtherState.PARTIALEVALUATION.getKey()});//部分评价
                break;
            case "4":// 已取消
                stateList = Arrays.asList(new Integer[]{ShopOrderItemOtherState.CANCELED.getKey()});
                break;
            case "5":// 处理中
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.REFUNDING.getKey(),  // 退款中
                    ShopOrderItemOtherState.SALESRETURNING.getKey(),//退货中
                    ShopOrderItemOtherState.EXCHANGEING.getKey()});//换货中
                break;
            case "6": // 申请记录
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderItemOtherState.REFUND.getKey(),     // 已退款
                    ShopOrderItemOtherState.SALESRETURNED.getKey(),//已退货
                    ShopOrderItemOtherState.EXCHANGED.getKey()});//已换货
        }
        QueryWrapper<Order> wrapper = super.getQueryWrapper(commonPageInfo);
        if (CollectionUtil.isNotEmpty(stateList)) { // 状态在子单，先筛出符合 Tab 的订单 id
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getState), stateList);
            List<OrderItem> orderItemList = orderItemService.list(itemWrapper);
            if (CollectionUtil.isEmpty(orderItemList)) {
                return CollectionUtil.newArrayList();
            }
            List<String> orderIdList = orderItemList.stream().map(OrderItem::getParentId).distinct().collect(Collectors.toList());
            wrapper.in(CommonConstants.ID, orderIdList);
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(Order::getCreateTime));
        List<Order> list = list(wrapper);
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
            case "1": // 未支付
                stateList = Arrays.asList(ShopOrderItemOtherState.WAIT_PAY.getKey());
                break;
            case "2": // 待收货
                stateList = Arrays.asList(ShopOrderItemOtherState.WAIT_DELIVER.getKey(),// 待发货
                    ShopOrderItemOtherState.ALL_DELIVERED.getKey(), //  已发货
                    ShopOrderItemOtherState.TRANSPORTING.getKey());//运输中
                break;
            case "3":// 已完成
                stateList = Arrays.asList(ShopOrderItemOtherState.SIGN.getKey(),       // 已签收
                    ShopOrderItemOtherState.COMPLETED.getKey(),  // 已完成
                    ShopOrderItemOtherState.UNEVALUATE.getKey(), // 待评价
                    ShopOrderItemOtherState.EVALUATED.getKey(),// 已评价
                    ShopOrderItemOtherState.PARTIALLYDONE.getKey(),//部分完成
                    ShopOrderItemOtherState.PARTIALEVALUATION.getKey());//部分评价
                break;
            case "4":// 已取消
                stateList = Arrays.asList(ShopOrderItemOtherState.CANCELED.getKey());
                break;
            case "5":// 处理中
                stateList = Arrays.asList(ShopOrderItemOtherState.REFUNDING.getKey(),  // 退款中
                    ShopOrderItemOtherState.SALESRETURNING.getKey(),//退货中
                    ShopOrderItemOtherState.EXCHANGEING.getKey());//换货中
                break;
            case "6": // 申请记录
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
        String orderId = inputObject.getParams().get("id").toString();
        //获取订单当前状态
        Order order = selectById(orderId);
        Integer state = order.getState();
        if (ShopOrderState.UNPAID.getKey() == state || ShopOrderState.FAIRPAID.getKey() == state) {
            UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, orderId);
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.PAY_SUCCESS.getKey());
            update(updateWrapper);
            refreshCache(orderId);
            // 修改订单子单信息为待发货状态
            orderItemService.updateDeliverStateByParentId(orderId, ShopOrderItemOtherState.WAIT_DELIVER.getKey());
        } else {
            throw new CustomException("当前订单状态不为待支付或支付失败状态，不可修改");
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
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.get("id"));
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        // 可取消：待支付、支付失败；或支付成功且子单仍为待发货
        boolean canCancel = Objects.equals(one.getState(), ShopOrderState.UNPAID.getKey())
            || Objects.equals(one.getState(), ShopOrderState.FAIRPAID.getKey());
        if (Objects.equals(one.getState(), ShopOrderState.PAY_SUCCESS.getKey())) {
            List<OrderItem> itemList = orderItemService.queryOrderItemByParentId(one.getId());
            canCancel = CollectionUtil.isNotEmpty(itemList)
                && itemList.stream().allMatch(item -> Objects.equals(item.getState(), ShopOrderItemOtherState.WAIT_DELIVER.getKey()));
        }
        if (canCancel) {
            // 未支付/支付失败取消：主单回到待支付；已支付成功取消：主单保持支付成功
            if (!Objects.equals(one.getState(), ShopOrderState.PAY_SUCCESS.getKey())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNPAID.getKey());
            }
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelType), params.get("cancelType"));
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            orderItemService.updateDeliverStateByParentId(one.getId(), ShopOrderItemOtherState.CANCELED.getKey());
            log.info("订单id" + one.getId() + "取消订单--取消定时任务-- 开始");
            iQuartzService.stopAndDeleteTaskQuartz(one.getId());// 删除任务
            log.info("订单id" + one.getId() + "取消订单--取消定时任务-- 结束");
            refreshCache(params.get("id").toString());
        } else {
            throw new CustomException("订单不可取消");
        }
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
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void payOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String channelCode = params.get("channelCode").toString();
        String returnUrl = params.get("returnUrl").toString();
        String channelExtras = params.get("channelExtras").toString();
        Order one = getPayableShopOrder(id);
        Map<String, Object> payResult = initiateShopOrderPayment(one, channelCode, returnUrl, channelExtras);
        handleShopOrderPayResult(id, one, channelCode, payResult, outputObject, true);
    }

    /**
     * 商城支付成功业务回调（配置在 PayApp.orderNotifyUrl，由 promote PayNotify 转发）。
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void notifyOrderPaySuccess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String outTradeNo = params.get("outTradeNo").toString();
        String channelCode = params.get("channelCode").toString();
        Order order = queryOrderByOddNumber(outTradeNo);
        if (!Objects.equals(order.getState(), ShopOrderState.UNPAID.getKey())) {
            // 已支付或已取消，幂等忽略
            return;
        }
        Map<String, Object> payChannelMap = new HashMap<>();
        if (StrUtil.isNotBlank(channelCode)) {
            // 渠道费率等信息在回调阶段可选填充
        }
        Map<String, Object> payOrderRespDTO = new HashMap<>();
        if (params.containsKey("successTime")) {
            payOrderRespDTO.put("successTime", params.get("successTime"));
        }
        completeShopOrderAfterPay(order, payChannelMap, channelCode, payOrderRespDTO);
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
     * 商城支付成功落单：主单待支付 → 支付成功，子单 → 待发货；更新渠道信息与支付时间，取消支付超时定时任务。
     */
    private void completeShopOrderAfterPay(Order one, Map<String, Object> payChannel, String channelCode,
                                           Map<String, Object> payOrderRespDTO) {
        String id = one.getId();
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.PAY_SUCCESS.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), channelCode);
        if (payOrderRespDTO.get("successTime") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayTime), payOrderRespDTO.get("successTime").toString());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayTime), DateUtil.getTimeAndToString());
        }
        if (payChannel.get("feeRate") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getChannelFeeRate), payChannel.get("feeRate").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getChannelFeePrice), CalculationUtil.multiply(
                one.getPayPrice(), payChannel.get("feeRate").toString()));
        }
        if (payOrderRespDTO.get("id") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getExtensionId), payOrderRespDTO.get("id").toString());
        }
        if (payOrderRespDTO.get("no") != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getExtensionNo), payOrderRespDTO.get("no").toString());
        }
        update(updateWrapper);
        refreshCache(id);
        orderItemService.updateDeliverStateByParentId(id, ShopOrderItemOtherState.WAIT_DELIVER.getKey());
        // 支付成功后删除「未支付自动取消」的 quartz 任务
        log.info("订单id" + id + "支付成功--删除定时任务-- 开始");
        iQuartzService.stopAndDeleteTaskQuartz(id);
        log.info("订单id" + id + "支付成功--删除定时任务-- 结束");
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
        if (!Objects.equals(one.getState(), ShopOrderState.UNPAID.getKey())) {
            throw new CustomException("该订单不可支付。");
        }
        return one;
    }

    private Map<String, Object> initiateShopOrderPayment(Order one, String channelCode, String returnUrl,
                                                         String channelExtras) {
        if (!StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), one.getAdjustPrice())) {
            one.setPayPrice(CalculationUtil.multiply(one.getAdjustPrice(), CommonNumConstants.ONE_HUNDRED.toString()));
        }
        Map<String, Object> payData = buildShopOrderPayData(one);
        return iPayService.payment(payData, channelCode, returnUrl, channelExtras, StrUtil.EMPTY, MALL_ORDER_PAY_APP_KEY).getBean();
    }

    /**
     * @param returnFullOrderOnSyncSuccess payOrder 同步成功返回完整订单；generatePayOrderRrCode 返回 payOrderRespDTO 结构
     */
    private void handleShopOrderPayResult(String id, Order one, String channelCode,
                                          Map<String, Object> payResult, OutputObject outputObject,
                                          boolean returnFullOrderOnSyncSuccess) {
        Map<String, Object> payChannel = JSONUtil.toBean(payResult.get("payChannel").toString(), null);
        Map<String, Object> payOrderRespDTO = JSONUtil.toBean(payResult.get("payOrderRespDTO").toString(), null);
        Integer payStatus = Integer.parseInt(payOrderRespDTO.get("status").toString());

        if (PAY_STATUS_SUCCESS.equals(payStatus)) {
            completeShopOrderAfterPay(one, payChannel, channelCode, payOrderRespDTO);
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
        // 支付超时取消：主单回到待支付
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNPAID.getKey())
            .set(MybatisPlusUtil.toColumns(Order::getCancelType), ShopOrderCancelType.PAY_TIMEOUT.getKey())
            .set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        orderItemService.updateDeliverStateByParentId(orderId, ShopOrderItemOtherState.CANCELED.getKey());
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
            ShopOrderState.PAY_SUCCESS.getKey());
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