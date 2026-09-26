package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.delivery.entity.ShopDeliveryTemplateCharge;
import com.skyeye.delivery.service.ShopDeliveryCompanyService;
import com.skyeye.delivery.service.ShopDeliveryTemplateChargeService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.ItemDeliverHistoryDao;
import com.skyeye.order.entity.ItemDeliverHistory;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.service.ItemDeliverHistoryService;
import com.skyeye.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ItemDeliverHistoryServiceImpl
 * @Description: 商品订单子单项快递信息管理--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单子单项快递信息管理", groupName = "OrderItemServiceImpl", tenant = TenantEnum.NO_ISOLATION)
public class ItemDeliverHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ItemDeliverHistoryDao, ItemDeliverHistory> implements ItemDeliverHistoryService {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ShopDeliveryCompanyService shopDeliveryCompanyService;

    @Autowired
    private ShopDeliveryTemplateChargeService shopDeliveryTemplateChargeService;

    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        orderItemService.setMationForMap(beans, "orderItemId", "orderItemMation");
        shopDeliveryCompanyService.setMationForMap(beans, "deliverCompanyId", "deliverCompanyMation");
        return beans;
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ItemDeliverHistory> wrapper) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 快递公司判断
        if (StrUtil.isNotEmpty(commonPageInfo.getCompanyId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getDeliverCompanyId), commonPageInfo.getCompanyId());
        }
        // 总单判断
        if (StrUtil.isNotEmpty(commonPageInfo.getTypeId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getOrderId), commonPageInfo.getTypeId());
        }
        // 子单判断
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getOrderItemId), commonPageInfo.getObjectId());
        }
    }

    @Override
    @IgnoreTenant
    public void queryMyItemDeliverHistoryPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String deliverCompanyId = commonPageInfo.getCompanyId();
        String orderId = commonPageInfo.getTypeId();
        String orderItemId = commonPageInfo.getObjectId();
        String currenUserId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        MPJLambdaWrapper<ItemDeliverHistory> wrapper = JoinWrappers.lambda("idh", ItemDeliverHistory.class);
        // 订单单子项判断
        if (StrUtil.isNotEmpty(orderItemId)) {
            wrapper.innerJoin(OrderItem.class, "oi", OrderItem::getId, ItemDeliverHistory::getOrderItemId)
                    .eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getOrderItemId), orderItemId)
                    .eq("oi." + MybatisPlusUtil.toColumns(OrderItem::getCreateId), currenUserId);
            if (tenantEnable) {
                wrapper.eq("oi." + CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
            }
        }
        // 订单判断
        if (StrUtil.isNotEmpty(orderId)) {
            wrapper.innerJoin(Order.class, "o", Order::getId, ItemDeliverHistory::getOrderId)
                    .eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getOrderId), orderId)
                    .eq("o." + MybatisPlusUtil.toColumns(Order::getCreateId), currenUserId);
        }
        // 快递公司判断
        if (StrUtil.isNotEmpty(deliverCompanyId)) {
            if (StrUtil.isEmpty(orderId) && StrUtil.isEmpty(orderItemId)) {
                // 不考虑总单和子单时
                wrapper.innerJoin(OrderItem.class, "oi", OrderItem::getId, ItemDeliverHistory::getOrderItemId)
                        .eq("oi." + MybatisPlusUtil.toColumns(OrderItem::getCreateId), currenUserId);
            }
            wrapper.eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getDeliverCompanyId), deliverCompanyId);
        }
        List<ItemDeliverHistory> beans = baseMapper.selectJoinList(ItemDeliverHistory.class, wrapper);
        orderItemService.setDataMation(beans, ItemDeliverHistory::getOrderItemId);
        shopDeliveryCompanyService.setDataMation(beans, ItemDeliverHistory::getDeliverCompanyId);
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 订单子单发货时，创建快递信息
     *
     * @param orderItem                订单子单信息
     * @param deliverNumber            快递单号
     * @param deliveryTemplateChargeId 运费模板收费信息
     * @param deliveryCompanyId        快递公司信息
     * @param num                      发货数量
     */
    @Override
    public void insertEntity(OrderItem orderItem, String deliverNumber, String deliveryTemplateChargeId, String deliveryCompanyId, String num) {
        ShopDeliveryTemplateCharge shopDeliveryTemplateCharge = shopDeliveryTemplateChargeService.selectById(deliveryTemplateChargeId);
        if (StrUtil.isEmpty(shopDeliveryTemplateCharge.getId())) {
            throw new CustomException("快递运费模板计费配置信息不存在: " + deliveryTemplateChargeId);
        }
        ItemDeliverHistory itemDeliverHistory = new ItemDeliverHistory();
        itemDeliverHistory.setOrderId(orderItem.getParentId());
        itemDeliverHistory.setOrderItemId(orderItem.getId());
        itemDeliverHistory.setDeliverCompanyId(deliveryCompanyId);
        itemDeliverHistory.setDeliverTemplateChargeId(deliveryTemplateChargeId);
        itemDeliverHistory.setDeliverNumber(deliverNumber);
        itemDeliverHistory.setNum(num);
        itemDeliverHistory.setPrice(shopDeliveryTemplateCharge.getStartPrice());
        String startCount = String.valueOf(shopDeliveryTemplateCharge.getStartCount() == null
            ? CommonNumConstants.NUM_ZERO : shopDeliveryTemplateCharge.getStartCount());
        if (CalculationUtil.compareTo(num, startCount, CommonNumConstants.NUM_TWO, java.math.RoundingMode.HALF_UP) > 0) {
            String extraCount = CalculationUtil.subtract(num, startCount, CommonNumConstants.NUM_TWO);
            String extraPrice = CalculationUtil.multiply(shopDeliveryTemplateCharge.getExtraPrice(), extraCount, CommonNumConstants.NUM_SIX);
            itemDeliverHistory.setPrice(CalculationUtil.add(shopDeliveryTemplateCharge.getStartPrice(), extraPrice, CommonNumConstants.NUM_SIX));
        }
        super.createEntity(itemDeliverHistory, InputObject.getLogParamsStatic().get("id").toString());
    }

    @Override
    public List<ItemDeliverHistory> queryListByItemId(String itemId) {
        if (StrUtil.isEmpty(itemId)) {
            return new ArrayList<>();
        }
        QueryWrapper<ItemDeliverHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ItemDeliverHistory::getOrderItemId), itemId);
        return list(queryWrapper);
    }

    @Override
    public List<ItemDeliverHistory> queryListByItemId(List<String> itemIdList) {
        if (CollectionUtil.isEmpty(itemIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<ItemDeliverHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ItemDeliverHistory::getOrderItemId), itemIdList);
        return list(queryWrapper);
    }
}
