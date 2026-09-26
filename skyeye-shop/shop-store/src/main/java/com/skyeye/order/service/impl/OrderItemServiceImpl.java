/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderItemDao;
import com.skyeye.order.entity.ItemDeliverHistory;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderComment;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.ItemSignState;
import com.skyeye.order.enums.OrderCommentType;
import com.skyeye.order.enums.ShopOrderItemOtherState;
import com.skyeye.order.service.ItemDeliverHistoryService;
import com.skyeye.order.service.OrderCommentService;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.rest.shopstock.service.IShopStockService;
import com.skyeye.store.classenum.StoreNature;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: OrderItemServiceImpl
 * @Description: 商品订单子单项管理--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单子单项管理", groupName = "商品订单子单项管理", tenant = TenantEnum.NO_ISOLATION)
public class OrderItemServiceImpl extends SkyeyeBusinessServiceImpl<OrderItemDao, OrderItem> implements OrderItemService {

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private OrderCommentService orderCommentService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemDeliverHistoryService itemDeliverHistoryService;

    @Autowired
    private IShopStockService iShopStockService;

    @Override
    public void deleteByPerentIds(List<String> ids) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getParentId), ids);
        remove(queryWrapper);
    }

    @Override
    public List<OrderItem> queryListByStateAndOrderId(String orderId, Integer state) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), orderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getCommentState), state);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<OrderItem>> queryListByParentId(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return new HashMap<>();
        }
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getParentId), idList);
        List<OrderItem> mapList = list(queryWrapper);
        if (CollectionUtil.isEmpty(mapList)) {
            return new HashMap<>();
        }
        List<OrderItem> orderItemList = setDateForItemLIst(mapList);
        Map<String, List<OrderItem>> result = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getParentId));
        return result;
    }

    @Override
    public List<OrderItem> setDateForItemLIst(List<OrderItem> list) {
        // 计算评价信息
        List<String> orderItemIds = list.stream().map(OrderItem::getId).collect(Collectors.toList());
        List<OrderComment> orderCommentList = orderCommentService.queryListByOrderItemIdAndType(orderItemIds, OrderCommentType.CUSTOMERLATER.getKey());
        List<String> commentIdList = orderCommentList.stream().map(OrderComment::getOrderItemId).collect(Collectors.toList());
        for (OrderItem map : list) {
            if (commentIdList.contains(map.getId())) {
                map.setIsAdditionalReview(true);
            } else {
                map.setIsAdditionalReview(false);
            }
        }
        // 设置门店、规格
        shopStoreService.setDataMation(list, OrderItem::getStoreId);
        iMaterialNormsService.setDataMation(list, OrderItem::getNormsId);
        List<String> materialStoreIds = list.stream().map(OrderItem::getMaterialStoreId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);// erp-shop-material 拿价钱logo
        Map<String, Map<String, Object>> materialStoreMap = materialByIds.stream()
            .distinct().collect(Collectors.toMap(map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore.get("id").toString();
            }, map -> map));
        list.forEach(map -> {
            map.setShopMaterial(materialStoreMap.containsKey(map.getMaterialStoreId()) ? materialStoreMap.get(map.getMaterialStoreId()) : new HashMap<>());
        });
        // 快递单号操作
        List<ItemDeliverHistory> itemDeliverHistories = itemDeliverHistoryService.queryListByItemId(orderItemIds);
        Map<String, List<String>> deliverMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(itemDeliverHistories)) {
            // 只收集快递单号
            deliverMap = itemDeliverHistories.stream().filter(bean -> StrUtil.isNotEmpty(bean.getDeliverNumber()))
                .collect(Collectors.groupingBy(ItemDeliverHistory::getOrderItemId, Collectors.mapping(ItemDeliverHistory::getDeliverNumber, Collectors.toList())));
        }
        for (OrderItem orderItem : list) {
            orderItem.setDeliverNumberList(deliverMap.getOrDefault(orderItem.getId(), new ArrayList<>()));
        }
        return list;
    }

    @Override
    public void setValueAndCreateEntity(Order order, String userId) {
        List<OrderItem> orderItemList = order.getOrderItemList();
        // 订单编号
        List<String> oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), BeanUtil.beanToMap(orderItemList.get(CommonNumConstants.NUM_ZERO)), orderItemList.size());

        List<String> materialStoreIds = orderItemList.stream().map(OrderItem::getMaterialStoreId).distinct().collect(Collectors.toList());
        // shopMaterial -> shopMaterialStore -> storeId / sourceStoreId
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);// erp-shop-material
        Map<String, Map<String, Object>> materialStoreInfoMap = materialByIds.stream()
            .distinct().collect(Collectors.toMap(map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore.get("id").toString();
            }, map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore;
            }, (a, b) -> a));
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            Map<String, Object> storeInfo = materialStoreInfoMap.get(orderItem.getMaterialStoreId());
            String sellStoreId = storeInfo != null && storeInfo.get("storeId") != null
                ? storeInfo.get("storeId").toString() : "";
            String sourceStoreId = storeInfo != null && storeInfo.get("sourceStoreId") != null
                ? storeInfo.get("sourceStoreId").toString() : "";
            // 下单前校验可售（平台货校验供货方库存）
            Map<String, Object> checkParams = new HashMap<>();
            checkParams.put("materialStoreId", orderItem.getMaterialStoreId());
            checkParams.put("normsId", orderItem.getNormsId());
            checkParams.put("count", orderItem.getCount());
            iShopStockService.checkShopStockForSale(checkParams);

            orderItem.setCommentState(WhetherEnum.DISABLE_USING.getKey());
            orderItem.setState(ShopOrderItemOtherState.WAIT_PAY.getKey());
            orderItem.setParentId(order.getId());
            orderItem.setStoreId(sellStoreId);
            orderItem.setSourceStoreId(StrUtil.isBlank(sourceStoreId) ? null : sourceStoreId);
            orderItem.setOddNumber(oddNumber.get(i));
        }
        super.createEntity(orderItemList, userId);
    }

    @Override
    public void updateCommentStateById(String id) {
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(OrderItem::getCommentState), WhetherEnum.ENABLE_USING.getKey())
            .set(MybatisPlusUtil.toColumns(OrderItem::getState), ShopOrderItemOtherState.EVALUATED.getKey());
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public List<OrderItem> queryOrderItemByParentId(String orderId) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), orderId);
        return list(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<OrderItem> list = JSONUtil.toList(JSONUtil.toJsonStr(beans), OrderItem.class);
        // 设置规格、商品等信息
        List<OrderItem> orderItemList = setDateForItemLIst(list);
        List<Map<String, Object>> result = JSONUtil.toList(JSONUtil.toJsonStr(orderItemList), null);
        return result;
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<OrderItem> wrapper) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            // dropship=1：按供货门店 sourceStoreId；否则按售出门店 storeId
            if (isDropshipQuery(commonPageInfo)) {
                wrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getSourceStoreId), commonPageInfo.getObjectId());
            } else {
                wrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getStoreId), commonPageInfo.getObjectId());
            }
        }
        // 与商城/商家工作台订单列表 type 口径一致, 状态筛选：0全部；1待付款；2待发货；3待收货；4已完成；5已取消；6售后中；7售后完成
        List<Integer> stateList = resolveOrderItemStateList(commonPageInfo.getType());
        if (CollectionUtil.isNotEmpty(stateList)) {
            wrapper.in(MybatisPlusUtil.toColumns(OrderItem::getState), stateList);
        }
        if (StrUtil.isNotBlank(commonPageInfo.getKeyword())) {
            wrapper.like(MybatisPlusUtil.toColumns(OrderItem::getOddNumber), commonPageInfo.getKeyword());
        }
    }

    private boolean isDropshipQuery(CommonPageInfo commonPageInfo) {
        String flag = commonPageInfo.getCustomParamsMapStr("dropship");
        if (StrUtil.isBlank(flag)) {
            return false;
        }
        return CommonNumConstants.NUM_ONE.toString().equals(flag) || "true".equalsIgnoreCase(flag);
    }

    /**
     * 订单子单状态筛选：0全部；1待付款；2待发货；3待收货；4已完成；5已取消；6售后中；7售后完成
     */
    private List<Integer> resolveOrderItemStateList(String type) {
        switch (StrUtil.isEmpty(type) ? CommonNumConstants.NUM_ZERO.toString() : type) {
            case "1":
                return Arrays.asList(ShopOrderItemOtherState.WAIT_PAY.getKey());
            case "2":
                return Arrays.asList(ShopOrderItemOtherState.WAIT_DELIVER.getKey(),
                    ShopOrderItemOtherState.PART_DELIVERED.getKey());
            case "3":
                return Arrays.asList(ShopOrderItemOtherState.ALL_DELIVERED.getKey(),
                    ShopOrderItemOtherState.TRANSPORTING.getKey());
            case "4":
                return Arrays.asList(ShopOrderItemOtherState.UNEVALUATE.getKey(), ShopOrderItemOtherState.EVALUATED.getKey(),
                    ShopOrderItemOtherState.PARTIALEVALUATION.getKey(), ShopOrderItemOtherState.SIGN.getKey(),
                    ShopOrderItemOtherState.COMPLETED.getKey(), ShopOrderItemOtherState.PARTIALLYDONE.getKey());
            case "5":
                return Arrays.asList(ShopOrderItemOtherState.CANCELED.getKey());
            case "6":
                return Arrays.asList(ShopOrderItemOtherState.REFUNDING.getKey(), ShopOrderItemOtherState.SALESRETURNING.getKey(),
                    ShopOrderItemOtherState.EXCHANGEING.getKey());
            case "7":
                return Arrays.asList(ShopOrderItemOtherState.REFUND.getKey(), ShopOrderItemOtherState.SALESRETURNED.getKey(),
                    ShopOrderItemOtherState.EXCHANGED.getKey());
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 订单子单发货。
     * <p>
     * 个人门店：发货前经 Feign 按 stockMode 扣库存，库存不足则整单失败。<br>
     * 加盟门店：保持原逻辑，不在此扣库存。<br>
     * 物流字段（快递公司/运费配置/单号）均为可选；三者齐全时才写入发货历史。
     * </p>
     *
     * @param inputObject  入参：id、orderId、num 必填；deliverNumber、deliveryCompanyId、deliveryTemplateChargeId 可选
     * @param outputObject 出参
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deliverGoodsById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String orderId = params.get("orderId").toString();
        // 物流信息可选：个人店未配置快递模板时允许仅发货扣库存
        String deliverNumber = params.get("deliverNumber").toString();
        String deliveryTemplateChargeId = params.get("deliveryTemplateChargeId").toString();
        String deliveryCompanyId = params.get("deliveryCompanyId").toString();
        String num = params.get("num") == null ? "" : params.get("num").toString().trim();
        if (StrUtil.isBlank(num)
            || CalculationUtil.compareTo(num, CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, java.math.RoundingMode.HALF_UP) <= 0) {
            throw new CustomException("发货数量不可为负数或零");
        }
        List<OrderItem> orderItemList = queryOrderItemByParentId(orderId);
        if (CollectionUtil.isEmpty(orderItemList)) {
            throw new CustomException("该订单不存在");
        }
        OrderItem targetItem = orderItemList.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
        if (ObjectUtil.isEmpty(targetItem)) {
            throw new CustomException("该订单子单不存在");
        }
        if (targetItem.getState() == ShopOrderItemOtherState.WAIT_PAY.getKey() ||
            targetItem.getState() == ShopOrderItemOtherState.ALL_DELIVERED.getKey()) {
            throw new CustomException("该订单未支付或已全部发货");
        }
        String delivered = StrUtil.blankToDefault(targetItem.getDeliverNum(), CommonNumConstants.NUM_ZERO.toString());
        String count = StrUtil.blankToDefault(targetItem.getCount(), CommonNumConstants.NUM_ZERO.toString());
        String remainingNum = CalculationUtil.subtract(
            CalculationUtil.subtract(count, delivered, CommonNumConstants.NUM_TWO),
            num,
            CommonNumConstants.NUM_TWO);
        if (CalculationUtil.compareTo(remainingNum, CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, java.math.RoundingMode.HALF_UP) < 0) {
            throw new CustomException("该订单子单可发货数量不足");
        }
        String memberId = inputObject.getLogParams().get("id").toString();
        // 平台货分销代销：仅供货方门店主可发货；自建商品仍由卖货门店发货
        if (StrUtil.isNotBlank(targetItem.getSourceStoreId())) {
            ShopStore sourceStore = shopStoreService.selectById(targetItem.getSourceStoreId());
            if (sourceStore == null || StrUtil.isEmpty(sourceStore.getId())) {
                throw new CustomException("供货方门店不存在，无法发货");
            }
            if (!memberId.equals(sourceStore.getCreateId())) {
                throw new CustomException("平台货源订单请由供货方发货");
            }
        } else {
            ShopStore store = shopStoreService.selectById(targetItem.getStoreId());
            if (store != null && StoreNature.PERSONAL.getKey().equals(store.getStoreNature())
                && !memberId.equals(store.getCreateId())) {
                throw new CustomException("无权发货该订单");
            }
        }
        // 个人门店发货前按库存模式扣减（加盟店自营保持原逻辑；平台货扣供货方库存）
        ShopStore sellStore = shopStoreService.selectById(targetItem.getStoreId());
        boolean needDeduct = sellStore != null && StoreNature.PERSONAL.getKey().equals(sellStore.getStoreNature());
        if (!needDeduct && StrUtil.isNotBlank(targetItem.getSourceStoreId())) {
            // 供货方可能是企业店，平台货代发也要扣库存
            needDeduct = true;
        }
        if (needDeduct) {
            Map<String, Object> stockParams = new HashMap<>();
            stockParams.put("storeId", targetItem.getStoreId());
            stockParams.put("materialStoreId", targetItem.getMaterialStoreId());
            stockParams.put("materialId", targetItem.getMaterialId());
            stockParams.put("normsId", targetItem.getNormsId());
            stockParams.put("count", num);
            iShopStockService.deductShopStockOnShip(stockParams);
        }

        // 更新已发数量与履约状态：还有剩余 → 部分发货；否则 → 全部发货
        targetItem.setDeliverNum(CalculationUtil.add(delivered, num, CommonNumConstants.NUM_TWO));
        if (CalculationUtil.compareTo(remainingNum, CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, java.math.RoundingMode.HALF_UP) > 0) {
            targetItem.setState(ShopOrderItemOtherState.PART_DELIVERED.getKey());
        } else {
            targetItem.setState(ShopOrderItemOtherState.ALL_DELIVERED.getKey());
        }
        super.updateEntity(targetItem, inputObject.getLogParams().get("id").toString());
        // 有完整物流信息时再写快递单历史
        if (StrUtil.isNotBlank(deliveryTemplateChargeId) && StrUtil.isNotBlank(deliveryCompanyId)
            && StrUtil.isNotBlank(deliverNumber)) {
            itemDeliverHistoryService.insertEntity(targetItem, deliverNumber, deliveryTemplateChargeId, deliveryCompanyId, num);
        }
    }

    @Override
    public void updateDeliverStateByParentId(String parentId, Integer state) {
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(OrderItem::getState), state);
        List<OrderItem> list = list(updateWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        update(updateWrapper);
        List<String> itemIdList = list.stream().map(OrderItem::getId).collect(Collectors.toList());
        refreshCache(itemIdList);
    }

    @Override
    public void changeOrderItemAdjustPrice(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String adjustPrice = params.get("adjustPrice").toString();
        if (Double.parseDouble(adjustPrice) < CommonNumConstants.NUM_ZERO) {
            throw new CustomException("所调价格不可为负数和得等于0");
        }
        // 元 -> 分
        adjustPrice = CalculationUtil.multiply(adjustPrice, "100", CommonNumConstants.NUM_SIX);
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(OrderItem::getAdjustPrice), adjustPrice);
        OrderItem oldItem = getOne(updateWrapper);
        if (oldItem.getState() != ShopOrderItemOtherState.WAIT_PAY.getKey()) {
            throw new CustomException("该不处于待发货状态，不可修改调价.");
        }
        // 更新数据
        update(updateWrapper);
        refreshCache(id);
        // 计算前后得价格差值
        String interpolation;
        if (StrUtil.isEmpty(oldItem.getAdjustPrice()) || Double.parseDouble(oldItem.getAdjustPrice()) <= CommonNumConstants.NUM_ZERO) {
            // 第一次调价   新的调价 -价格旧价格
            interpolation = CalculationUtil.subtract(adjustPrice, oldItem.getPayPrice(), CommonNumConstants.NUM_SIX);
        } else {
            // 不是第一次调价 新的调价 -价格旧价格
            interpolation = CalculationUtil.subtract(adjustPrice, oldItem.getAdjustPrice(), CommonNumConstants.NUM_SIX);
        }
        orderService.changeAdjustPriceById(oldItem.getParentId(), interpolation);
    }

    @Override
    public void signOrderItem(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("orderId").toString();
        String itemId = params.get("itemId").toString();
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), orderId);
        List<OrderItem> orderItemList = list(queryWrapper);
        if (CollectionUtil.isEmpty(orderItemList)) {
            throw new CustomException("总单据id不存在");
        }
        // 取出要签收的子单
        OrderItem orderItem = orderItemList.stream().filter(item -> item.getId().equals(itemId)).findFirst().orElse(null);
        if (ObjectUtil.isEmpty(orderItem)) {
            throw new CustomException("该订单子单不存在");
        }
        String currenUserId = InputObject.getLogParamsStatic().get("id").toString();
        if (!orderItem.getCreateId().equals(currenUserId)) {
            throw new CustomException("该订单子单不属于当前账号");
        }
        if (orderItem.getState() == ShopOrderItemOtherState.WAIT_PAY.getKey() || orderItem.getState() == ShopOrderItemOtherState.WAIT_DELIVER.getKey()) {
            throw new CustomException("该订单未支付或未发货");
        }
        List<ItemDeliverHistory> itemDeliverHistoryList = itemDeliverHistoryService.queryListByItemId(itemId);
        // 算出已发货总数
        String num = CommonNumConstants.NUM_ZERO.toString();
        for (ItemDeliverHistory idh : itemDeliverHistoryList) {
            num = CalculationUtil.add(num, StrUtil.blankToDefault(idh.getNum(), CommonNumConstants.NUM_ZERO.toString()), CommonNumConstants.NUM_TWO);
        }
        // 计算未发货数量
        String count = StrUtil.blankToDefault(orderItem.getCount(), CommonNumConstants.NUM_ZERO.toString());
        String remainingNum = CalculationUtil.subtract(count, num, CommonNumConstants.NUM_TWO);
        if (CalculationUtil.compareTo(remainingNum, CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, java.math.RoundingMode.HALF_UP) == 0) {
            orderItem.setSignNum(count);
            orderItem.setSignState(ItemSignState.ALL_SIGN.getKey());
            orderItem.setState(ShopOrderItemOtherState.SIGN.getKey());
        } else {
            orderItem.setSignNum(num);
            orderItem.setSignState(ItemSignState.PART_SIGN.getKey());
            orderItem.setState(ShopOrderItemOtherState.PART_SIGN.getKey());
        }
        super.updateEntity(orderItem, currenUserId);
    }

    @Override
    public OrderItem selectById(String id) {
        OrderItem orderItem = super.selectById(id);
        if (StrUtil.isEmpty(orderItem.getId())) {
            throw new CustomException("该订单子单不存在");
        }
        OrderItem item = setDateForItemLIst(Arrays.asList(orderItem)).get(CommonNumConstants.NUM_ZERO);
        String count = StrUtil.blankToDefault(item.getCount(), CommonNumConstants.NUM_ZERO.toString());
        String delivered = StrUtil.blankToDefault(item.getDeliverNum(), CommonNumConstants.NUM_ZERO.toString());
        item.setCanDeliverNum(CalculationUtil.subtract(count, delivered, CommonNumConstants.NUM_TWO));
        return orderItem;
    }

    @Override
    public void queryMaterialStoreDailySales(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String materialId = commonPageInfo.getCustomParamsMapStr("materialId");
        String startDate = commonPageInfo.getStartTime();
        String endDate = commonPageInfo.getEndTime();
        // 补全当天结束时间，避免漏掉当天订单
        String start = startDate.length() <= 10 ? startDate + " 00:00:00" : startDate;
        String end = endDate.length() <= 10 ? endDate + " 23:59:59" : endDate;
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Map<String, Object>> rows = skyeyeBaseMapper.queryMaterialStoreDailySales(materialId, start, end);
        if (CollectionUtil.isEmpty(rows)) {
            return;
        }
        List<String> storeIds = rows.stream()
            .map(r -> MapUtil.getStr(r, "storeId"))
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, ShopStore> storeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(storeIds)) {
            List<ShopStore> stores = shopStoreService.selectByIds(storeIds.toArray(new String[0]));
            if (CollectionUtil.isNotEmpty(stores)) {
                storeMap = stores.stream().collect(Collectors.toMap(ShopStore::getId, s -> s, (a, b) -> a));
            }
        }
        for (Map<String, Object> row : rows) {
            String storeId = MapUtil.getStr(row, "storeId");
            ShopStore store = storeMap.get(storeId);
            row.put("storeName", store == null ? storeId : store.getName());
            // 分转元展示
            String fen = MapUtil.getStr(row, "saleAmountFen", "0");
            row.put("saleAmount", CalculationUtil.divide(fen, "100", CommonNumConstants.NUM_TWO));
        }
        outputObject.setBeans(rows);
        outputObject.settotal(pages.getTotal());
    }
}