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
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderCommentDao;
import com.skyeye.order.entity.OrderComment;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.OrderCommentType;
import com.skyeye.order.enums.ShopOrderCommentState;
import com.skyeye.order.enums.ShopOrderItemOtherState;
import com.skyeye.order.service.OrderCommentService;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.service.MemberService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName: OrderCommentServiceImpl
 * @Description: 商品订单评价管理--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单评价管理", groupName = "商品订单评价管理", tenant = TenantEnum.NO_ISOLATION)
public class OrderCommentServiceImpl extends SkyeyeBusinessServiceImpl<OrderCommentDao, OrderComment> implements OrderCommentService {

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Override
    public void validatorEntity(OrderComment orderComment) {
        Integer commentType = orderComment.getType();
        if (commentType != OrderCommentType.MERCHANT.getKey()
            && commentType != OrderCommentType.CUSTOMERLATER.getKey()
            && commentType != OrderCommentType.CUSTOMERFiRST.getKey()) {
            throw new CustomException("type值非法");
        }
        if (commentType == OrderCommentType.MERCHANT.getKey() ||
            commentType == OrderCommentType.CUSTOMERLATER.getKey()) {
            if (StrUtil.isEmpty(orderComment.getParentId())) {
                throw new CustomException("商家回复评价和客户追评，父级评价id不能为空.");
            }
        }
        if (commentType == OrderCommentType.CUSTOMERFiRST.getKey()) {
            if (StrUtil.isNotEmpty(orderComment.getParentId())) {
                throw new CustomException("客户的评价无需父级id");
            }
        }
    }

    @Override
    public void createPrepose(OrderComment entity) {
        OrderItem orderItem = orderItemService.selectById(entity.getOrderItemId());
        if (StrUtil.isEmpty(orderItem.getId())) {
            throw new CustomException("所评价的子订单不存在");
        }
        // 客户评价判断
        if (orderItem.getCommentState() == WhetherEnum.DISABLE_USING.getKey()) {// 子订单未评价
            if (entity.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {
                throw new CustomException("客户追评，需先进行首评。");
            }
            if (entity.getType() == OrderCommentType.CUSTOMERFiRST.getKey()) {// 客户首评
                Integer start = entity.getStart();
                if (ObjectUtil.isEmpty(entity.getStart())) {
                    throw new CustomException("首评的星级不能为空");
                }
                if (start < 0 || start > 5) {
                    throw new CustomException("评价星级为1-5");
                }
            }
        } else if (orderItem.getCommentState() == WhetherEnum.ENABLE_USING.getKey()) {// 子订单已评价
            if (entity.getType() == OrderCommentType.CUSTOMERFiRST.getKey()) {// 再次进行首评
                throw new CustomException("首评只能评价一次。");
            }
            if (entity.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {// 追评
                QueryWrapper<OrderComment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getOrderItemId), entity.getOrderItemId())
                    .eq(MybatisPlusUtil.toColumns(OrderComment::getCreateId), InputObject.getLogParamsStatic().get("id").toString())
                    .and(wrap -> {
                        String parentId = MybatisPlusUtil.toColumns(OrderComment::getParentId);
                        wrap.isNotNull(parentId).ne(parentId, StrUtil.EMPTY);
                    });
                OrderComment one = getOne(queryWrapper);
                if (ObjectUtil.isNotEmpty(one)) {// 客户已追评
                    throw new CustomException("追评只能追评一次");
                }
                entity.setStart(null);
            }
        }
        entity.setStoreId(ObjectUtil.isEmpty(orderItem) ? "" : orderItem.getStoreId());// 设置门店id
        if (entity.getType() == OrderCommentType.CUSTOMERFiRST.getKey() ||
            entity.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {// 顾客新增的评价，商家均未回复
            entity.setIsComment(WhetherEnum.DISABLE_USING.getKey());
        }
    }

    @Override
    public void createPostpose(OrderComment orderComment, String userId) {
        if (orderComment.getType() == OrderCommentType.MERCHANT.getKey()) {// 商家回复时，修改客户评价状态为已评价
            UpdateWrapper<OrderComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, orderComment.getParentId());
            updateWrapper.set(MybatisPlusUtil.toColumns(OrderComment::getIsComment), WhetherEnum.ENABLE_USING.getKey());
            update(updateWrapper);
        } else if (orderComment.getType() == OrderCommentType.CUSTOMERFiRST.getKey()) {// 客户首评
            orderItemService.updateCommentStateById(orderComment.getOrderItemId());// 修改此子订单的评价状态为已评价
            List<OrderItem> orderItemList = orderItemService.queryListByStateAndOrderId(orderComment.getOrderId(), WhetherEnum.DISABLE_USING.getKey());
            boolean allMatch = orderItemList.stream()
                .allMatch(Orderitem -> Orderitem.getCommentState() == WhetherEnum.ENABLE_USING.getKey());
            if (allMatch) {
                orderService.updateCommonState(orderComment.getOrderId(), ShopOrderCommentState.FINISHED.getKey());
                orderItemService.updateDeliverStateByParentId(orderComment.getOrderId(), ShopOrderItemOtherState.EVALUATED.getKey());
            } else {
                orderService.updateCommonState(orderComment.getOrderId(), ShopOrderCommentState.PORTION.getKey());
                orderItemService.updateDeliverStateByParentId(orderComment.getOrderId(), ShopOrderItemOtherState.PARTIALEVALUATION.getKey());
            }
        }
    }

    @Override
    @IgnoreTenant
    public OrderComment selectById(String id) {
        OrderComment orderComment = super.selectById(id);
        if (ObjectUtil.isEmpty(orderComment)) {
            throw new CustomException("信息不存在");
        }
        iMaterialService.setDataMation(orderComment, OrderComment::getMaterialId);
        iMaterialNormsService.setDataMation(orderComment, OrderComment::getNormsId);
        memberService.setDataMation(orderComment, OrderComment::getCreateId);
        shopStoreService.setDataMation(orderComment, OrderComment::getStoreId);
        refreshCache(id);
        return orderComment;
    }

    @Override
    public void queryOrderCommentPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String typeId = commonPageInfo.getTypeId();
        String objectId = commonPageInfo.getObjectId();
        // 查首评论
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<OrderComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrap -> {
            wrap.eq(MybatisPlusUtil.toColumns(OrderComment::getMaterialId), typeId) // 商品id
                .eq(MybatisPlusUtil.toColumns(OrderComment::getParentId), StrUtil.EMPTY)
                .or().eq(MybatisPlusUtil.toColumns(OrderComment::getOrderItemId), typeId)// 订单子单id
                .or().eq(MybatisPlusUtil.toColumns(OrderComment::getOrderId), typeId);// 订单id
        }).orderByDesc(MybatisPlusUtil.toColumns(OrderComment::getCreateTime));
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getNormsId), objectId);
        }
        List<OrderComment> listFirst = list(queryWrapper);
        if (CollectionUtil.isEmpty(listFirst)) {
            return;
        }
        setValue(listFirst);
        List<String> firstId = listFirst.stream().map(OrderComment::getId).collect(Collectors.toList());
        QueryWrapper<OrderComment> queryWrapperLater = new QueryWrapper<>();// 追评
        queryWrapperLater
            .in(MybatisPlusUtil.toColumns(OrderComment::getParentId), firstId)
            .eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.CUSTOMERLATER.getKey())
            .orderByDesc(MybatisPlusUtil.toColumns(OrderComment::getCreateTime));
        List<OrderComment> listLater = list(queryWrapperLater);
        setValue(listLater);
        // 商家回复
        List<String> idList = Stream.of(listFirst.stream(), listLater.stream()).flatMap(s -> s).map(OrderComment::getId).collect(Collectors.toList());
        QueryWrapper<OrderComment> queryWrapperMerchant = new QueryWrapper<>();
        queryWrapperMerchant.in(MybatisPlusUtil.toColumns(OrderComment::getParentId), idList)
            .eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.MERCHANT.getKey());
        List<OrderComment> marchantList = list(queryWrapperMerchant);
        setValue(marchantList);
        setAdditionalReviewAndMerchantReply(listFirst, listLater, marchantList);
        outputObject.setBeans(listFirst);
        outputObject.settotal(pages.getTotal());

    }

    @Override
    public List<OrderComment> queryListByOrderItemIdAndType(List<String> orderItemIds, Integer type) {
        if (CollectionUtil.isEmpty(orderItemIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<OrderComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderComment::getOrderItemId), orderItemIds)
            .eq(MybatisPlusUtil.toColumns(OrderComment::getType), type);
        List<OrderComment> list = list(queryWrapper);
        return CollectionUtil.isEmpty(list) ? new ArrayList<>() : list;
    }

    @Override
    public void queryMyOrderCommentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<OrderComment> queryWrapperFirst = new QueryWrapper<>();// 客户首评
        queryWrapperFirst
            .eq(MybatisPlusUtil.toColumns(OrderComment::getCreateId), inputObject.getLogParams().get("id").toString())
            .eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.CUSTOMERFiRST.getKey())
            .orderByDesc(MybatisPlusUtil.toColumns(OrderComment::getCreateTime));
        List<OrderComment> listFirst = list(queryWrapperFirst);
        setValue(listFirst);
        if (CollectionUtil.isEmpty(listFirst)) {
            return;
        }
        QueryWrapper<OrderComment> queryWrapperLater = new QueryWrapper<>();// 追评
        queryWrapperLater
            .eq(MybatisPlusUtil.toColumns(OrderComment::getCreateId), inputObject.getLogParams().get("id").toString())
            .eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.CUSTOMERLATER.getKey())
            .orderByDesc(MybatisPlusUtil.toColumns(OrderComment::getCreateTime));
        List<OrderComment> listLater = list(queryWrapperLater);
        setValue(listLater);
        // 商家回复
        List<String> idList = Stream.of(listFirst.stream(), listLater.stream()).flatMap(s -> s).map(OrderComment::getId).collect(Collectors.toList());
        QueryWrapper<OrderComment> queryWrapperMerchant = new QueryWrapper<>();
        queryWrapperMerchant.in(MybatisPlusUtil.toColumns(OrderComment::getParentId), idList)
            .eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.MERCHANT.getKey());
        List<OrderComment> marchantList = list(queryWrapperMerchant);
        setValue(marchantList);
        setAdditionalReviewAndMerchantReply(listFirst, listLater, marchantList);
        outputObject.setBeans(listFirst);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryOrderCommentPageListPC(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        MPJLambdaWrapper<OrderComment> mpjLambdaWrapper = JoinWrappers.lambda("oc", OrderComment.class);
        if (StrUtil.isNotEmpty(tenantId)) {
            mpjLambdaWrapper.innerJoin("erp_material material ON oc.material_id = material.id")
                .eq("material.tenant_id", tenantId);
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            // keyword作为订单编号的查询条件
            mpjLambdaWrapper.innerJoin(OrderItem.class, "oi", OrderItem::getId, OrderComment::getOrderItemId)
                .like(MybatisPlusUtil.toColumns(OrderItem::getOddNumber), commonPageInfo.getKeyword());
            if (StrUtil.isNotEmpty(tenantId)) {
                mpjLambdaWrapper.eq("oi." + CommonConstants.TENANT_ID_FIELD, tenantId);
            }
        }
        // 只查顾客的评价
        mpjLambdaWrapper.and(wrap -> {
            String type = "oc." + MybatisPlusUtil.toColumns(OrderComment::getType);
            wrap.eq(type, OrderCommentType.CUSTOMERFiRST.getKey())
                .or().eq(type, OrderCommentType.CUSTOMERLATER.getKey());
        });
        if (StrUtil.equals(commonPageInfo.getType(), "Store")) {
            // 门店下的订单
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getStoreId), commonPageInfo.getHolderId());
        } else if (StrUtil.equals(commonPageInfo.getType(), "All")) {
            // 所有订单
        }
        // 查询OrderComment中的字段
        mpjLambdaWrapper.select(OrderComment::getId, OrderComment::getParentId
            , OrderComment::getNormsId, OrderComment::getMaterialId
            , OrderComment::getStoreId, OrderComment::getOrderId
            , OrderComment::getOrderItemId, OrderComment::getType
            , OrderComment::getStart, OrderComment::getIsComment
            , OrderComment::getContext, OrderComment::getCreateId
            , OrderComment::getCreateTime, OrderComment::getLastUpdateId, OrderComment::getLastUpdateTime);

        mpjLambdaWrapper.orderByDesc(OrderComment::getCreateTime);
        List<Map<String, Object>> mapList = skyeyeBaseMapper.selectJoinMaps(mpjLambdaWrapper);
        List<OrderComment> beans = BeanUtil.copyToList(mapList, OrderComment.class);
        iMaterialService.setDataMation(beans, OrderComment::getMaterialId);
        iMaterialNormsService.setDataMation(beans, OrderComment::getNormsId);
        memberService.setDataMation(beans, OrderComment::getCreateId);
        shopStoreService.setDataMation(beans, OrderComment::getStoreId);

        orderItemService.setDataMation(beans, OrderComment::getOrderItemId);

        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    private void setValue(List<OrderComment> list) {
        iMaterialService.setDataMation(list, OrderComment::getMaterialId);
        iMaterialNormsService.setDataMation(list, OrderComment::getNormsId);
        memberService.setDataMation(list, OrderComment::getCreateId);
        shopStoreService.setDataMation(list, OrderComment::getStoreId);
        orderItemService.setDataMation(list, OrderComment::getOrderItemId);
    }

    private void setAdditionalReviewAndMerchantReply(List<OrderComment> orderCommentCustomer, List<OrderComment> orderCommentLater, List<OrderComment> orderCommentMerchant) {
        // 追评放商家回复
        Map<String, List<OrderComment>> merchantMapList = orderCommentMerchant.stream().collect(Collectors.groupingBy(OrderComment::getParentId));
        for (OrderComment orderComment : orderCommentLater) {
            if (merchantMapList.containsKey(orderComment.getId())) {
                List<Map<String, Object>> merchantReplyList = merchantMapList.get(orderComment.getId()).stream()
                    .map(BeanUtil::beanToMap).collect(Collectors.toList());
                orderComment.setMerchantReply(merchantReplyList);
            }
        }
        // 首评放追评和商家回复
        Map<String, Map<String, Object>> laterMap = orderCommentLater.stream()
            .collect(Collectors.toMap(OrderComment::getParentId, o -> JSONUtil.toBean(JSONUtil.toJsonStr(o), null), (key1, key2) -> key2));
        for (OrderComment orderComment : orderCommentCustomer) {
            String id = orderComment.getId();
            if (laterMap.containsKey(id)) {// 追评
                orderComment.setAdditionalReview(laterMap.get(id));
            }
            if (merchantMapList.containsKey(id)) {// 商家回复
                List<Map<String, Object>> merchantReplyList = merchantMapList.get(id).stream()
                    .map(BeanUtil::beanToMap).collect(Collectors.toList());
                orderComment.setMerchantReply(merchantReplyList);
            }
        }
    }
}