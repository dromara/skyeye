/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.classenum.MemberAuthStatus;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.entity.Member;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.platform.service.IPlatformBaseSettingService;
import com.skyeye.service.MemberService;
import com.skyeye.store.classenum.ShopStoreApplyStatus;
import com.skyeye.store.classenum.StoreNature;
import com.skyeye.store.constans.ShopStoreConst;
import com.skyeye.store.dao.ShopStoreApplyDao;
import com.skyeye.store.entity.ShopArea;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.ShopStoreApply;
import com.skyeye.store.service.ShopAreaService;
import com.skyeye.store.service.ShopStoreApplyService;
import com.skyeye.store.service.ShopStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopStoreApplyServiceImpl
 * @Description: 个人开店申请服务实现
 */
@Slf4j
@Service
@SkyeyeService(name = "个人开店申请", groupName = "门店管理")
public class ShopStoreApplyServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreApplyDao, ShopStoreApply> implements ShopStoreApplyService {

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private ShopAreaService shopAreaService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private IPlatformBaseSettingService iPlatformBaseSettingService;

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void applyPersonalStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = inputObject.getLogParams().get("id").toString();
        String storeName = params.get("storeName").toString();
        if (StrUtil.isBlank(storeName)) {
            throw new CustomException("店铺名称不能为空");
        }

        Member member = memberService.selectById(memberId);
        if (member == null || !java.util.Objects.equals(member.getAuthStatus(), MemberAuthStatus.AUTHED.getKey())) {
            throw new CustomException("请先完成实名认证后再申请开店");
        }

        checkQuotaOrThrow(memberId);

        ShopStoreApply pending = queryPendingApply(memberId);
        if (pending != null) {
            throw new CustomException("您已有待审核的开店申请，请等待审核");
        }

        ShopStoreApply apply = new ShopStoreApply();
        apply.setTenantId(TenantTypeEnum.SHOP.getCode());
        apply.setMemberId(memberId);
        apply.setStoreName(storeName);
        apply.setLogo(params.get("logo").toString());
        apply.setRemark(params.get("remark").toString());
        apply.setContactName(params.get("contactName").toString());
        apply.setContactPhone(params.get("contactPhone").toString());
        apply.setProvinceId(params.get("provinceId").toString());
        apply.setCityId(params.get("cityId").toString());
        apply.setAreaId(params.get("areaId").toString());
        apply.setTownshipId(params.get("townshipId").toString());
        apply.setAbsoluteAddress(params.get("absoluteAddress").toString());
        Integer onlineOpen = Integer.parseInt(params.get("onlineOpen").toString());
        Integer offlineOpen = Integer.parseInt(params.get("offlineOpen").toString());
        if (WhetherEnum.ENABLE_USING.getKey().equals(offlineOpen)
            && (StrUtil.isBlank(apply.getProvinceId()) || StrUtil.isBlank(apply.getAbsoluteAddress()))) {
            throw new CustomException("开启线下门店要填写经营地址");
        }
        apply.setOnlineOpen(onlineOpen);
        apply.setOfflineOpen(offlineOpen);
        apply.setApplyType(1);
        apply.setState(ShopStoreApplyStatus.PENDING.getKey());

        try {
            TenantContext.setTenantId(TenantTypeEnum.SHOP.getCode());
            createEntity(apply, memberId);
        } finally {
            TenantContext.clear();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", apply.getId());
        result.put("message", "申请已提交，请等待平台审核");
        outputObject.setBean(result);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelMyPersonalStoreApply(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String memberId = inputObject.getLogParams().get("id").toString();
        ShopStoreApply apply = selectById(id);
        if (apply == null || StrUtil.isEmpty(apply.getId())) {
            throw new CustomException("申请记录不存在");
        }
        if (!StrUtil.equals(memberId, apply.getMemberId())) {
            throw new CustomException("无权操作该申请");
        }
        if (!ShopStoreApplyStatus.PENDING.getKey().equals(apply.getState())) {
            throw new CustomException("仅待审核的申请可取消");
        }
        UpdateWrapper<ShopStoreApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.PENDING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.CANCELLED.getKey());
        if (!update(updateWrapper)) {
            throw new CustomException("申请状态已变更，请刷新后重试");
        }
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approvePersonalStoreApply(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String auditRemark = params.get("auditRemark").toString();
        String auditUserId = inputObject.getLogParams().get("id").toString();

        ShopStoreApply apply = selectById(id);
        validatePendingApply(apply);
        checkQuotaOrThrow(apply.getMemberId());

        UpdateWrapper<ShopStoreApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.PENDING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.APPROVED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getAuditUserId), auditUserId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getAuditTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getAuditRemark), auditRemark);
        if (!update(updateWrapper)) {
            throw new CustomException("申请状态已变更，请刷新后重试");
        }

        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;

        String storeId;
        if (Integer.valueOf(2).equals(apply.getApplyType())) {
            applyStoreChange(apply);
            storeId = apply.getStoreId();
        } else {
            storeId = createPersonalStoreFromApply(apply);
        }

        if (tenantEnable) {
            // 因为上面创建个人门店，清空了租户id，所以这里要重新设置回去
            TenantContext.setTenantId(tenantId);
        }
        UpdateWrapper<ShopStoreApply> storeIdWrapper = new UpdateWrapper<>();
        storeIdWrapper.eq(CommonConstants.ID, id);
        storeIdWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getStoreId), storeId);
        update(storeIdWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("storeId", storeId);
        outputObject.setBean(result);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void rejectPersonalStoreApply(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String auditRemark = params.get("auditRemark").toString();
        String auditUserId = inputObject.getLogParams().get("id").toString();
        ShopStoreApply apply = selectById(id);
        validatePendingApply(apply);
        markApplyState(id, ShopStoreApplyStatus.REJECTED.getKey(), auditUserId, auditRemark);
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreApplyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ShopStoreApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getTenantId), TenantTypeEnum.SHOP.getCode());
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), Integer.parseInt(commonPageInfo.getState()));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                .like(MybatisPlusUtil.toColumns(ShopStoreApply::getStoreName), commonPageInfo.getKeyword())
                .or()
                .like(MybatisPlusUtil.toColumns(ShopStoreApply::getContactPhone), commonPageInfo.getKeyword())
                .or()
                .like(MybatisPlusUtil.toColumns(ShopStoreApply::getContactName), commonPageInfo.getKeyword()));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopStoreApply::getCreateTime));
        List<ShopStoreApply> list = list(queryWrapper);
        fillMemberMation(list);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryMyPersonalStoreApplyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String memberId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ShopStoreApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getMemberId), memberId);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), Integer.parseInt(commonPageInfo.getState()));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopStoreApply::getCreateTime));
        List<ShopStoreApply> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryMyPersonalStoreQuota(InputObject inputObject, OutputObject outputObject) {
        String memberId = inputObject.getLogParams().get("id").toString();
        Integer max = iPlatformBaseSettingService.getMaxPersonalStorePerMember();
        int approvedCount = countMemberStores(memberId);
        int pendingCount = countMemberPendingApplies(memberId);
        Map<String, Object> data = new HashMap<>();
        data.put("maxPersonalStorePerMember", max);
        data.put("approvedStoreCount", approvedCount);
        data.put("pendingApplyCount", pendingCount);
        data.put("usedCount", approvedCount + pendingCount);
        data.put("canApply", canApplyMore(approvedCount + pendingCount, max));
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String createPersonalStoreFromApply(ShopStoreApply apply) {
        try {
            TenantContext.setTenantId(TenantTypeEnum.SHOP.getCode());
            String areaId = getOrCreatePersonalDefaultArea(apply.getMemberId());
            ShopStore store = new ShopStore();
            store.setName(apply.getStoreName());
            store.setLogo(apply.getLogo());
            store.setRemark(apply.getRemark());
            store.setShopAreaId(areaId);
            store.setEnabled(EnableEnum.ENABLE_USING.getKey());
            store.setStoreNature(StoreNature.PERSONAL.getKey());
            store.setOnlineOpen(apply.getOnlineOpen() == null ? WhetherEnum.ENABLE_USING.getKey() : apply.getOnlineOpen());
            store.setOfflineOpen(apply.getOfflineOpen() == null ? WhetherEnum.DISABLE_USING.getKey() : apply.getOfflineOpen());
            store.setProvinceId(apply.getProvinceId());
            store.setCityId(apply.getCityId());
            store.setAreaId(apply.getAreaId());
            store.setTownshipId(apply.getTownshipId());
            store.setAbsoluteAddress(apply.getAbsoluteAddress());
            // createId 必须写成申请人会员 id，后续「我的门店」按 tenant_id + create_id 查询
            return shopStoreService.createEntity(store, apply.getMemberId());
        } finally {
            TenantContext.clear();
        }
    }

    private String getOrCreatePersonalDefaultArea(String operatorId) {
        QueryWrapper<ShopArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopArea::getName), ShopStoreConst.PERSONAL_STORE_DEFAULT_AREA_NAME);
        queryWrapper.last("LIMIT 1");
        ShopArea exist = shopAreaService.getOne(queryWrapper, false);
        if (exist != null && StrUtil.isNotEmpty(exist.getId())) {
            return exist.getId();
        }
        ShopArea area = new ShopArea();
        area.setName(ShopStoreConst.PERSONAL_STORE_DEFAULT_AREA_NAME);
        area.setEnabled(EnableEnum.ENABLE_USING.getKey());
        return shopAreaService.createEntity(area, operatorId);
    }

    private void checkQuotaOrThrow(String memberId) {
        Integer max = iPlatformBaseSettingService.getMaxPersonalStorePerMember();
        int used = countMemberStores(memberId) + countMemberPendingApplies(memberId);
        if (!canApplyMore(used, max)) {
            throw new CustomException("已达到个人门店数量上限（" + max + "），无法继续申请");
        }
    }

    private boolean canApplyMore(int usedCount, Integer maxCount) {
        // 0 表示不限制
        if (maxCount == null || maxCount == 0) {
            return true;
        }
        if (maxCount < 0) {
            return true;
        }
        return usedCount < maxCount;
    }

    private int countMemberStores(String memberId) {
        String oldTenantId = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(TenantTypeEnum.SHOP.getCode());
            QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getCreateId), memberId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getStoreNature), StoreNature.PERSONAL.getKey());
            return (int) shopStoreService.count(queryWrapper);
        } finally {
            if (StrUtil.isNotBlank(oldTenantId)) {
                TenantContext.setTenantId(oldTenantId);
            } else {
                TenantContext.clear();
            }
        }
    }

    private int countMemberPendingApplies(String memberId) {
        QueryWrapper<ShopStoreApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.PENDING.getKey());
        return (int) count(queryWrapper);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void applyPersonalStoreChange(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = inputObject.getLogParams().get("id").toString();
        String storeId = params.get("storeId").toString();
        ShopStore store = shopStoreService.selectById(storeId);
        if (store == null || StrUtil.isEmpty(store.getId())) {
            throw new CustomException("门店不存在");
        }
        if (!memberId.equals(store.getCreateId()) || !StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            throw new CustomException("只能变更自己的个人门店");
        }
        if (queryPendingApply(memberId) != null) {
            throw new CustomException("您已有待审核的申请，请等待审核后再提交");
        }
        Integer onlineOpen = Integer.parseInt(params.get("onlineOpen").toString());
        Integer offlineOpen = Integer.parseInt(params.get("offlineOpen").toString());
        String provinceId = params.get("provinceId") == null ? "" : params.get("provinceId").toString();
        String absoluteAddress = params.get("absoluteAddress") == null ? "" : params.get("absoluteAddress").toString();
        if (WhetherEnum.ENABLE_USING.getKey().equals(offlineOpen) && (StrUtil.isBlank(provinceId) || StrUtil.isBlank(absoluteAddress))) {
            throw new CustomException("开启线下门店要填写经营地址");
        }
        ShopStoreApply apply = new ShopStoreApply();
        apply.setTenantId(TenantTypeEnum.SHOP.getCode());
        apply.setMemberId(memberId);
        apply.setStoreName(store.getName());
        apply.setLogo(store.getLogo());
        apply.setRemark(params.get("remark") == null ? "" : params.get("remark").toString());
        apply.setContactName("");
        apply.setContactPhone("");
        apply.setProvinceId(provinceId);
        apply.setCityId(params.get("cityId") == null ? "" : params.get("cityId").toString());
        apply.setAreaId(params.get("areaId") == null ? "" : params.get("areaId").toString());
        apply.setTownshipId(params.get("townshipId") == null ? "" : params.get("townshipId").toString());
        apply.setAbsoluteAddress(absoluteAddress);
        apply.setOnlineOpen(onlineOpen);
        apply.setOfflineOpen(offlineOpen);
        apply.setApplyType(2);
        apply.setStoreId(storeId);
        apply.setState(ShopStoreApplyStatus.PENDING.getKey());
        try {
            TenantContext.setTenantId(TenantTypeEnum.SHOP.getCode());
            createEntity(apply, memberId);
        } finally {
            TenantContext.clear();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", apply.getId());
        result.put("message", "变更申请已提交，审核通过后才会改线上线下和经营地址");
        outputObject.setBean(result);
    }

    private void applyStoreChange(ShopStoreApply apply) {
        UpdateWrapper<ShopStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, apply.getStoreId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineOpen), apply.getOnlineOpen());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOfflineOpen), apply.getOfflineOpen());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getProvinceId), apply.getProvinceId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getCityId), apply.getCityId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getAreaId), apply.getAreaId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getTownshipId), apply.getTownshipId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getAbsoluteAddress), apply.getAbsoluteAddress());
        if (!WhetherEnum.ENABLE_USING.getKey().equals(apply.getOfflineOpen())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStore::getOnlineBookAppoint), WhetherEnum.DISABLE_USING.getKey());
        }
        shopStoreService.update(updateWrapper);
        shopStoreService.refreshCache(apply.getStoreId());
    }

    private ShopStoreApply queryPendingApply(String memberId) {
        QueryWrapper<ShopStoreApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.PENDING.getKey());
        return getOne(queryWrapper, false);
    }

    private void validatePendingApply(ShopStoreApply apply) {
        if (apply == null || StrUtil.isEmpty(apply.getId())) {
            throw new CustomException("申请记录不存在");
        }
        if (!ShopStoreApplyStatus.PENDING.getKey().equals(apply.getState())) {
            throw new CustomException("该申请已处理");
        }
    }

    private void markApplyState(String id, Integer state, String userId, String auditRemark) {
        UpdateWrapper<ShopStoreApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreApply::getState), ShopStoreApplyStatus.PENDING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getAuditUserId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getAuditTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreApply::getAuditRemark), auditRemark);
        if (!update(updateWrapper)) {
            throw new CustomException("申请状态已变更，请刷新后重试");
        }
    }

    private void fillMemberMation(List<ShopStoreApply> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> memberIds = list.stream().map(ShopStoreApply::getMemberId).distinct().collect(Collectors.toList());
        List<Member> members = memberService.selectByIds(memberIds.toArray(new String[0]));
        Map<String, Member> memberMap = members.stream().collect(Collectors.toMap(Member::getId, m -> m, (a, b) -> a));
        list.forEach(apply -> {
            Member member = memberMap.get(apply.getMemberId());
            if (member == null) {
                return;
            }
            Map<String, Object> memberMation = new HashMap<>();
            memberMation.put("id", member.getId());
            memberMation.put("name", member.getName());
            memberMation.put("phone", member.getPhone());
            memberMation.put("avatar", member.getAvatar());
            apply.setMemberMation(memberMation);
        });
    }

}
