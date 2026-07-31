/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
        // shopMaterial -> shopMaterialStore -> storeId
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);// erp-shop-material
        Map<String, String> materialStoreMap = materialByIds.stream()
                .distinct().collect(Collectors.toMap(map -> {
                    Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                    return shopMaterialStore.get("id").toString();
                }, map -> {
                    Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                    return shopMaterialStore.get("storeId").toString();
                }));
        for (int i = 0; i < orderItemList.size(); i++) {
            orderItemList.get(i).setCommentState(WhetherEnum.DISABLE_USING.getKey());
            orderItemList.get(i).setState(ShopOrderItemOtherState.WAIT_PAY.getKey());
            orderItemList.get(i).setParentId(order.getId());
            orderItemList.get(i).setStoreId(materialStoreMap.containsKey(orderItemList.get(i).getMaterialStoreId()) ? materialStoreMap.get(orderItemList.get(i).getMaterialStoreId()) : "");
            orderItemList.get(i).setOddNumber(oddNumber.get(i));
        }
        super.createEntity(orderItemList, userId);
    }

    @Override
    public void updateCommentStateById(String id) {
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
                .set(MybatisPlusUtil.toColumns(OrderItem::getCommentState), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
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
            wrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getStoreId), commonPageInfo.getObjectId());
        }
    }

    /**
     * 快递计费方式有数量、重量、体积三种，当前只考虑数量
     *
     * @param inputObject
     * @param outputObject
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deliverGoodsById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String orderId = params.get("orderId").toString();
        String deliverNumber = params.get("deliverNumber").toString();
        String deliveryTemplateChargeId = params.get("deliveryTemplateChargeId").toString();
        String deliveryCompanyId = params.get("deliveryCompanyId").toString();
        Integer num = Integer.parseInt(params.get("num").toString());
        if (num <= CommonNumConstants.NUM_ZERO) {
            throw new CustomException("发货数量不可为负数或零");
        }
        List<OrderItem> orderItemList = queryOrderItemByParentId(orderId);
        if (CollectionUtil.isEmpty(orderItemList)) {
            throw new CustomException("该订单不存在");
        }
        OrderItem targetItem = orderItemList.stream().filter(item -> item.getId().equals(id)).findFirst().orElseGet(null);
        if (ObjectUtil.isEmpty(targetItem)) {
            throw new CustomException("该订单子单不存在");
        }
        if (targetItem.getState() == ShopOrderItemOtherState.WAIT_PAY.getKey() ||
                targetItem.getState() == ShopOrderItemOtherState.ALL_DELIVERED.getKey()) {
            throw new CustomException("该订单未支付或已全部发货");
        }
        int remainingNum = targetItem.getCount() - targetItem.getDeliverNum() - num;
        if (remainingNum < CommonNumConstants.NUM_ZERO) {
            throw new CustomException("该订单子单可发货数量不足");
        }
        // 设置数据
        targetItem.setDeliverNum(targetItem.getDeliverNum() + num);
        if (remainingNum > CommonNumConstants.NUM_ZERO) {
            // 还剩
            targetItem.setState(ShopOrderItemOtherState.PART_DELIVERED.getKey());
        } else {
            // 剩余为0
            targetItem.setState(ShopOrderItemOtherState.ALL_DELIVERED.getKey());
        }
        // 更新数据
        super.updateEntity(targetItem, inputObject.getLogParams().get("id").toString());
        // 创建快递信息
        itemDeliverHistoryService.insertEntity(targetItem, deliverNumber, deliveryTemplateChargeId, deliveryCompanyId, num);
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
        int num = itemDeliverHistoryList.stream().map(idh -> Integer.parseInt(idh.getNum())).reduce(CommonNumConstants.NUM_ZERO, Integer::sum);
        // 计算未发货数量
        int remainingNum = orderItem.getCount() - num;
        if (remainingNum == CommonNumConstants.NUM_ZERO) {
            orderItem.setSignNum(orderItem.getCount());
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
        item.setCanDeliverNum(item.getCount() - item.getDeliverNum());
        return orderItem;
    }
}