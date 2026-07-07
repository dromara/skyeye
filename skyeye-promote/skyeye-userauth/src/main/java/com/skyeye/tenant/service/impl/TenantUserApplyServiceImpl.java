/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.annotation.tenant.TenantIsolation;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.skyeye.tenant.classenum.TenantUserApplyStatus;
import com.skyeye.tenant.dao.TenantUserApplyDao;
import com.skyeye.tenant.entity.Tenant;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.entity.TenantUserApply;
import com.skyeye.tenant.service.TenantService;
import com.skyeye.tenant.service.TenantUserApplyService;
import com.skyeye.tenant.service.TenantUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantUserApplyServiceImpl
 * @Description: 用户申请加入租户服务实现
 */
@Slf4j
@Service
@SkyeyeService(name = "租户加入申请管理", groupName = "租户管理")
public class TenantUserApplyServiceImpl extends SkyeyeBusinessServiceImpl<TenantUserApplyDao, TenantUserApply> implements TenantUserApplyService {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void applyToJoinTenant(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new CustomException("租户功能未开启");
        }
        Map<String, Object> params = inputObject.getParams();
        String tenantId = params.get("tenantId").toString();
        String applyMessage = params.containsKey("applyMessage") && params.get("applyMessage") != null
            ? params.get("applyMessage").toString() : StrUtil.EMPTY;
        String staffId = inputObject.getLogParams().get("staffId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        if (StrUtil.isBlank(staffId)) {
            throw new CustomException("当前用户未绑定员工信息，无法申请加入");
        }

        Tenant tenant = tenantService.selectById(tenantId);
        if (ObjectUtil.isEmpty(tenant) || StrUtil.isEmpty(tenant.getId())) {
            throw new CustomException("组织不存在");
        }
        if (!WhetherEnum.ENABLE_USING.getKey().equals(tenant.getWhetherSearchable())) {
            throw new CustomException("该组织未开放搜索加入");
        }

        TenantUser existUser = tenantUserService.queryTenantUserByStaffId(staffId, tenantId);
        if (existUser != null && !UserStaffState.QUIT.getKey().equals(existUser.getState())) {
            throw new CustomException("您已是该组织成员");
        }

        TenantUserApply pendingApply = queryPendingApply(staffId, tenantId);
        if (pendingApply != null) {
            throw new CustomException("您已提交加入申请，请等待审核");
        }

        TenantUserApply apply = new TenantUserApply();
        apply.setTenantId(tenantId);
        apply.setStaffId(staffId);
        apply.setApplyMessage(applyMessage);

        Map<String, Object> result = new HashMap<>();
        try {
            TenantContext.setTenantId(tenantId);
            if (WhetherEnum.DISABLE_USING.getKey().equals(tenant.getWhetherJoinNeedAudit())) {
                tenantService.checkTenantAccountNum(tenantId);
                apply.setState(TenantUserApplyStatus.APPROVED.getKey());
                apply.setAuditUserId(userId);
                apply.setAuditTime(DateUtil.getTimeAndToString());
                apply.setAuditRemark("免审核自动通过");
                String applyId = createEntity(apply, userId);
                createTenantUserFromApply(tenantId, staffId, applyId, userId);
                result.put("joined", true);
                result.put("message", "已成功加入组织");
            } else {
                apply.setState(TenantUserApplyStatus.PENDING.getKey());
                createEntity(apply, userId);
                result.put("joined", false);
                result.put("message", "申请已提交，请等待管理员审核");
            }
        } finally {
            TenantContext.clear();
        }
        outputObject.setBean(result);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelMyTenantUserApply(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String staffId = inputObject.getLogParams().get("staffId").toString();
        TenantUserApply apply = selectById(id);
        if (apply == null || StrUtil.isEmpty(apply.getId())) {
            throw new CustomException("申请记录不存在");
        }
        if (!StrUtil.equals(staffId, apply.getStaffId())) {
            throw new CustomException("无权操作该申请");
        }
        if (!TenantUserApplyStatus.PENDING.getKey().equals(apply.getState())) {
            throw new CustomException("仅待审核的申请可取消");
        }
        UpdateWrapper<TenantUserApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.PENDING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.CANCELLED.getKey());
        update(updateWrapper);
    }

    @Override
    @TenantIsolation(TenantEnum.STRONG_ISOLATION)
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approveTenantUserApply(InputObject inputObject, OutputObject outputObject) {
        validateCurrentTenantAdmin(inputObject);
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String auditRemark = params.containsKey("auditRemark") && params.get("auditRemark") != null
            ? params.get("auditRemark").toString() : StrUtil.EMPTY;
        String userId = inputObject.getLogParams().get("id").toString();
        String tenantId = TenantContext.getTenantId();

        TenantUserApply apply = selectById(id);
        validatePendingApply(apply, tenantId);

        tenantService.checkTenantAccountNum(tenantId);
        TenantUser existUser = tenantUserService.queryTenantUserByStaffId(apply.getStaffId(), tenantId);
        if (existUser != null && !UserStaffState.QUIT.getKey().equals(existUser.getState())) {
            markApplyState(id, TenantUserApplyStatus.REJECTED.getKey(), userId, auditRemark);
            throw new CustomException("该用户已是组织成员");
        }

        UpdateWrapper<TenantUserApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.PENDING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.APPROVED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getAuditUserId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getAuditTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getAuditRemark), auditRemark);
        if (!update(updateWrapper)) {
            throw new CustomException("申请状态已变更，请刷新后重试");
        }
        createTenantUserFromApply(tenantId, apply.getStaffId(), id, userId);
    }

    @Override
    @TenantIsolation(TenantEnum.STRONG_ISOLATION)
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void rejectTenantUserApply(InputObject inputObject, OutputObject outputObject) {
        validateCurrentTenantAdmin(inputObject);
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String auditRemark = params.containsKey("auditRemark") && params.get("auditRemark") != null
            ? params.get("auditRemark").toString() : StrUtil.EMPTY;
        String userId = inputObject.getLogParams().get("id").toString();
        String tenantId = TenantContext.getTenantId();

        TenantUserApply apply = selectById(id);
        validatePendingApply(apply, tenantId);
        markApplyState(id, TenantUserApplyStatus.REJECTED.getKey(), userId, auditRemark);
    }

    @Override
    @TenantIsolation(TenantEnum.STRONG_ISOLATION)
    public void queryTenantUserApplyList(InputObject inputObject, OutputObject outputObject) {
        validateCurrentTenantAdmin(inputObject);
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<TenantUserApply> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), Integer.parseInt(commonPageInfo.getState()));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantUserApply::getCreateTime));
        List<TenantUserApply> list = list(queryWrapper);
        fillStaffMation(list);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryMyTenantUserApplyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String staffId = inputObject.getLogParams().get("staffId").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<TenantUserApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getStaffId), staffId);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), Integer.parseInt(commonPageInfo.getState()));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantUserApply::getCreateTime));
        List<TenantUserApply> list = list(queryWrapper);
        fillTenantMation(list);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    private void fillTenantMation(List<TenantUserApply> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> tenantIds = list.stream().map(TenantUserApply::getTenantId).distinct().collect(Collectors.toList());
        List<Tenant> tenants = tenantService.selectByIds(tenantIds.toArray(new String[0]));
        Map<String, Tenant> tenantMap = tenants.stream().collect(Collectors.toMap(Tenant::getId, t -> t, (a, b) -> a));
        list.forEach(apply -> {
            Tenant tenant = tenantMap.get(apply.getTenantId());
            if (tenant != null) {
                apply.setTenantName(tenant.getName());
                apply.setTenantLogo(tenant.getLogo());
            }
        });
    }

    @Override
    @IgnoreTenant
    public Map<String, Integer> queryApplyStatusMapByStaffId(String staffId, List<String> tenantIds) {
        Map<String, Integer> result = new HashMap<>();
        if (StrUtil.isBlank(staffId) || CollectionUtil.isEmpty(tenantIds)) {
            return result;
        }
        QueryWrapper<TenantUserApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getStaffId), staffId);
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantUserApply::getTenantId), tenantIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantUserApply::getState),
            Arrays.asList(TenantUserApplyStatus.PENDING.getKey(), TenantUserApplyStatus.REJECTED.getKey()));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantUserApply::getCreateTime));
        List<TenantUserApply> list = list(queryWrapper);
        for (TenantUserApply apply : list) {
            result.putIfAbsent(apply.getTenantId(), apply.getState());
        }
        return result;
    }

    @Override
    @IgnoreTenant
    public Map<String, TenantUserApply> queryPendingApplyMapByStaffId(String staffId, List<String> tenantIds) {
        Map<String, TenantUserApply> result = new HashMap<>();
        if (StrUtil.isBlank(staffId) || CollectionUtil.isEmpty(tenantIds)) {
            return result;
        }
        QueryWrapper<TenantUserApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getStaffId), staffId);
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantUserApply::getTenantId), tenantIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.PENDING.getKey());
        List<TenantUserApply> list = list(queryWrapper);
        for (TenantUserApply apply : list) {
            result.putIfAbsent(apply.getTenantId(), apply);
        }
        return result;
    }

    @Override
    @IgnoreTenant
    public TenantUserApply selectById(String id) {
        return super.selectById(id);
    }

    private TenantUserApply queryPendingApply(String staffId, String tenantId) {
        QueryWrapper<TenantUserApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getStaffId), staffId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getTenantId), tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.PENDING.getKey());
        return getOne(queryWrapper, false);
    }

    private void validatePendingApply(TenantUserApply apply, String tenantId) {
        if (apply == null || StrUtil.isEmpty(apply.getId())) {
            throw new CustomException("申请记录不存在");
        }
        if (!StrUtil.equals(tenantId, apply.getTenantId())) {
            throw new CustomException("申请记录不属于当前组织");
        }
        if (!TenantUserApplyStatus.PENDING.getKey().equals(apply.getState())) {
            throw new CustomException("该申请已处理");
        }
    }

    private void markApplyState(String id, Integer state, String userId, String auditRemark) {
        UpdateWrapper<TenantUserApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUserApply::getState), TenantUserApplyStatus.PENDING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getAuditUserId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getAuditTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserApply::getAuditRemark), auditRemark);
        if (!update(updateWrapper)) {
            throw new CustomException("申请状态已变更，请刷新后重试");
        }
    }

    private void createTenantUserFromApply(String tenantId, String staffId, String applyId, String userId) {
        try {
            TenantContext.setTenantId(tenantId);
            TenantUser tenantUser = new TenantUser();
            tenantUser.setStaffId(staffId);
            tenantUser.setIsAdmin(WhetherEnum.DISABLE_USING.getKey());
            tenantUser.setState(UserStaffState.ON_THE_JOB.getKey());
            String currentTime = DateUtil.getTimeAndToString();
            tenantUser.setWorkTime(currentTime);
            tenantUser.setEntryTime(currentTime);
            tenantUser.setTrialTime(currentTime);
            tenantUser.setTenantUserApplyId(applyId);
            tenantUserService.createEntity(tenantUser, userId);
        } finally {
            TenantContext.clear();
        }
    }

    private void validateCurrentTenantAdmin(InputObject inputObject) {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("请先选择组织");
        }
        String staffId = inputObject.getLogParams().get("staffId").toString();
        TenantUser tenantUser = tenantUserService.queryTenantUserByStaffId(staffId, tenantId);
        if (tenantUser == null || !WhetherEnum.ENABLE_USING.getKey().equals(tenantUser.getIsAdmin())) {
            throw new CustomException("仅组织管理员可操作");
        }
    }

    private void fillStaffMation(List<TenantUserApply> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> staffIds = list.stream().map(TenantUserApply::getStaffId).distinct().collect(Collectors.toList());
        // 申请人尚未加入当前组织，不能走 iAuthUserService.queryUserMationListByStaffIds（会按 tenant_user 过滤）
        Map<String, SysEveUserStaff> staffMap = sysEveUserStaffService.getUserIdsByStaffIds(staffIds);
        list.forEach(apply -> {
            SysEveUserStaff staff = staffMap.get(apply.getStaffId());
            if (staff == null) {
                return;
            }
            Map<String, Object> staffMation = new HashMap<>();
            staffMation.put("staffId", staff.getId());
            staffMation.put("userName", staff.getUserName());
            staffMation.put("phone", staff.getPhone());
            staffMation.put("userPhoto", staff.getUserPhoto());
            apply.setStaffMation(staffMation);
        });
    }

}
