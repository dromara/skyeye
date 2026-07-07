/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.annotation.tenant.TenantIsolation;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.PropertiesUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.classenum.TenantOrgType;
import com.skyeye.tenant.dao.TenantDao;
import com.skyeye.tenant.entity.*;
import com.skyeye.tenant.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantServiceImpl
 * @Description: 租户服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/28 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "租户管理", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class TenantServiceImpl extends SkyeyeBusinessServiceImpl<TenantDao, Tenant> implements TenantService {

    @Autowired
    private TenantAppLinkService tenantAppLinkService;

    @Autowired
    private TenantAppService tenantAppService;

    @Autowired
    private TenantAppMenuService tenantAppMenuService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    @Lazy
    @Autowired
    private TenantAppBuyOrderService tenantAppBuyOrderService;

    @Autowired
    private TenantUserApplyService tenantUserApplyService;

    @Override
    public void createPrepose(Tenant entity) {
        if (entity.getOrgType() == null) {
            entity.setOrgType(TenantOrgType.ENTERPRISE.getKey());
        }
        entity.setAccountNum(platformBaseSettingService.getInitAccountNum(entity.getOrgType()));
        if (entity.getWhetherHasPassedBuyOrder() == null) {
            entity.setWhetherHasPassedBuyOrder(WhetherEnum.DISABLE_USING.getKey());
        }
        if (entity.getWhetherSearchable() == null) {
            entity.setWhetherSearchable(WhetherEnum.DISABLE_USING.getKey());
        }
        if (entity.getWhetherJoinNeedAudit() == null) {
            entity.setWhetherJoinNeedAudit(WhetherEnum.ENABLE_USING.getKey());
        }
    }

    @Override
    protected void createPostpose(Tenant entity, String userId) {
        // 创建消息通知，同步数据到该租户下
        Map<String, Object> jobBody = new HashMap<>();
        jobBody.put("whetherCreatTask", false);
        jobBody.put("content", JSONUtil.toJsonStr(entity));
        jobBody.put("userId", userId);
        String topic = PropertiesUtil.getPropertiesValue("${topic.synchronize-data-to-tenants}");
        jobBody.put("topic", topic);
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(jobBody));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
        log.info("租户创建成功，同步数据到该租户下");
    }

    @Override
    protected void deletePreExecution(String id) {
        if (StrUtil.equals(id, TenantTypeEnum.PLATFORM.getCode())) {
            throw new CustomException("平台租户不能删除");
        }
        assertNoActiveBuyOrders(id);
        super.deletePreExecution(id);
    }

    @Override
    protected void deletePreExecution(List<String> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            ids.forEach(this::deletePreExecution);
        }
    }

    /**
     * 存在非草稿、非作废的应用购买订单时禁止删除租户，避免订单与租户数据不一致。
     */
    private void assertNoActiveBuyOrders(String tenantId) {
        if (tenantAppBuyOrderService.countActiveBuyOrdersByBuyTenantId(tenantId) > 0) {
            throw new CustomException("该租户存在应用购买订单，无法删除。请先处理或作废相关订单。");
        }
    }


    @Override
    @IgnoreTenant
    public Tenant selectById(String id) {
        Tenant tenant = super.selectById(id);
        List<TenantAppLink> tenantAppLinkList = tenantAppLinkService.selectByTenantId(id);
        if (CollectionUtil.isNotEmpty(tenantAppLinkList)) {
            List<String> appIds = tenantAppLinkList.stream().map(TenantAppLink::getAppId).collect(Collectors.toList());
            Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
            tenantAppLinkList.forEach(tenantAppLink -> {
                tenantAppLink.setAppMation(tenantAppMap.get(tenantAppLink.getAppId()));
            });
        }
        tenant.setTenantAppLinkList(tenantAppLinkList);
        return tenant;
    }

    @Override
    @IgnoreTenant
    public void selectByIds(InputObject inputObject, OutputObject outputObject) {
        super.selectByIds(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public List<Tenant> selectByIds(String... ids) {
        return super.selectByIds(ids);
    }

    @Override
    @IgnoreTenant
    public void markHasPassedAppBuyOrder(String tenantId) {
        if (StrUtil.isEmpty(tenantId)) {
            return;
        }
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, tenantId);
        Tenant tenant = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenant) || StrUtil.isEmpty(tenant.getId())) {
            return;
        }
        if (WhetherEnum.ENABLE_USING.getKey().equals(tenant.getWhetherHasPassedBuyOrder())) {
            refreshCache(tenantId);
            return;
        }
        tenant.setWhetherHasPassedBuyOrder(WhetherEnum.ENABLE_USING.getKey());
        updateById(tenant);
        refreshCache(tenantId);
    }

    @Override
    public void editTenantAccountNumber(String tenantId, Integer accountNumber) {
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, tenantId);
        Tenant tenant = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(tenant) && StrUtil.isNotEmpty(tenant.getId())) {
            Integer accountNum = tenant.getAccountNum() + accountNumber;
            tenant.setAccountNum(accountNum);
            updateById(tenant);
            refreshCache(tenantId);
        } else {
            throw new CustomException("租户不存在");
        }
    }

    @Override
    public void queryAllTenantList(InputObject inputObject, OutputObject outputObject) {
        List<Tenant> tenants = queryAllData();
        outputObject.setBeans(tenants);
        outputObject.settotal(tenants.size());
    }

    @Override
    @IgnoreTenant
    public List<String> queryAllMenuListByTenantId(String tenantId, Integer type) {
        if (StrUtil.isEmpty(tenantId)) {
            return null;
        }
        // 查询租户下的所有应用
        List<TenantAppLink> tenantAppLinkList = tenantAppLinkService.selectByTenantId(tenantId);
        if (CollectionUtil.isEmpty(tenantAppLinkList)) {
            return null;
        }
        // 校验应用是否过期，将过期的应用提出掉
        String currentTime = DateUtil.getYmdTimeAndToString();
        List<String> appIds = tenantAppLinkList.stream().filter(tenantAppLink -> {
            if (DateUtil.getDistanceDay(tenantAppLink.getStartTime(), currentTime) >= 0 && DateUtil.getDistanceDay(currentTime, tenantAppLink.getEndTime()) >= 0) {
                // startTime <= 当前时间 <= endTime
                return true;
            }
            return false;
        }).map(TenantAppLink::getAppId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(appIds)) {
            return null;
        }
        List<String> menuIds = tenantAppMenuService.selectObjectIdsByAppId(appIds, type);
        return menuIds;
    }

    @Override
    @IgnoreTenant
    public void checkTenantAccountNum(String tenantId) {
        if (StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
            // 平台租户不限制账号数量
            return;
        }
        Tenant tenant = selectById(tenantId);
        if (ObjectUtil.isEmpty(tenant) || StrUtil.isEmpty(tenant.getId())) {
            throw new CustomException("租户不存在");
        }
        // 获取租户下的所有用户数量
        long count = tenantUserService.getTenantUserCountByTenantId(tenantId);
        if (count >= tenant.getAccountNum()) {
            throw new CustomException("租户账号数量已达上限");
        }
    }

    @Override
    @TenantIsolation(TenantEnum.STRONG_ISOLATION)
    public void queryCurrentTenantInfo(InputObject inputObject, OutputObject outputObject) {
        String tenantId = TenantContext.getTenantId();
        Map<String, Object> result = new HashMap<>();
        result.put("isAdmin", CommonNumConstants.NUM_ZERO);
        if (StrUtil.isBlank(tenantId)) {
            outputObject.setBean(result);
            return;
        }
        String staffId = inputObject.getLogParams().get("staffId").toString();
        TenantUser tenantUser = tenantUserService.queryTenantUserByStaffId(staffId, tenantId);
        if (tenantUser == null || !WhetherEnum.ENABLE_USING.getKey().equals(tenantUser.getIsAdmin())) {
            // 非租户管理员，返回空
            outputObject.setBean(result);
            return;
        }
        Tenant tenant = selectById(tenantId);
        if (ObjectUtil.isEmpty(tenant)) {
            // 租户不存在，返回空
            outputObject.setBean(result);
            return;
        }
        result.put("isAdmin", CommonNumConstants.NUM_ONE);
        fillCurrentOrgInfo(result, tenant);
        result.put("userCount", tenantUserService.getTenantUserCountByTenantId(tenantId));
        outputObject.setBean(result);
    }

    private void fillCurrentOrgInfo(Map<String, Object> result, Tenant tenant) {
        result.put("id", tenant.getId());
        result.put("name", tenant.getName());
        result.put("logo", tenant.getLogo());
        result.put("remark", tenant.getRemark());
        result.put("accountNum", tenant.getAccountNum());
        result.put("orgType", tenant.getOrgType() != null ? tenant.getOrgType() : TenantOrgType.ENTERPRISE.getKey());
        result.put("contactName", tenant.getContactName());
        result.put("contactPhone", tenant.getContactPhone());
        result.put("contactEmail", tenant.getContactEmail());
        result.put("address", tenant.getAddress());
        result.put("website", tenant.getWebsite());
        result.put("industry", tenant.getIndustry());
        result.put("creditCode", tenant.getCreditCode());
        result.put("legalPerson", tenant.getLegalPerson());
        result.put("whetherSearchable", tenant.getWhetherSearchable() != null ? tenant.getWhetherSearchable() : WhetherEnum.DISABLE_USING.getKey());
        result.put("whetherJoinNeedAudit", tenant.getWhetherJoinNeedAudit() != null ? tenant.getWhetherJoinNeedAudit() : WhetherEnum.ENABLE_USING.getKey());
    }

    @Override
    @TenantIsolation(TenantEnum.STRONG_ISOLATION)
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updateCurrentTenantInfo(InputObject inputObject, OutputObject outputObject) {
        String tenantId = TenantContext.getTenantId();
        validateCurrentTenantAdmin(tenantId, inputObject);
        Map<String, Object> params = inputObject.getParams();
        Tenant tenant = selectById(tenantId);
        if (ObjectUtil.isEmpty(tenant)) {
            throw new CustomException("组织不存在.");
        }
        applyCurrentOrgFields(tenant, params);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        updateEntity(tenant, userId);
    }

    private void applyCurrentOrgFields(Tenant tenant, Map<String, Object> params) {
        tenant.setName(params.get("name").toString().trim());
        tenant.setLogo(params.get("logo").toString());
        tenant.setRemark(params.get("remark").toString());
        tenant.setContactName(params.get("contactName").toString());
        tenant.setContactPhone(params.get("contactPhone").toString());
        tenant.setContactEmail(params.get("contactEmail").toString());
        tenant.setWhetherSearchable(Integer.parseInt(params.get("whetherSearchable").toString()));
        tenant.setWhetherJoinNeedAudit(Integer.parseInt(params.get("whetherJoinNeedAudit").toString()));
        if (TenantOrgType.PERSONAL.getKey().equals(tenant.getOrgType())) {
            // 如果是个人组织，则清空企业相关字段
            tenant.setCreditCode(null);
            tenant.setLegalPerson(null);
            tenant.setAddress(null);
            tenant.setWebsite(null);
            tenant.setIndustry(null);
            return;
        }
        // 如果是企业组织，则更新企业相关字段
        tenant.setCreditCode(params.get("creditCode").toString());
        tenant.setLegalPerson(params.get("legalPerson").toString());
        tenant.setAddress(params.get("address").toString());
        tenant.setWebsite(params.get("website").toString());
        tenant.setIndustry(params.get("industry").toString());
    }

    private void validateCurrentTenantAdmin(String tenantId, InputObject inputObject) {
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("请先选择组织.");
        }
        String staffId = inputObject.getLogParams().get("staffId").toString();
        TenantUser tenantUser = tenantUserService.queryTenantUserByStaffId(staffId, tenantId);
        if (tenantUser == null || !WhetherEnum.ENABLE_USING.getKey().equals(tenantUser.getIsAdmin())) {
            throw new CustomException("仅组织管理员可操作.");
        }
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createCurrentTenant(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new CustomException("租户功能未开启");
        }
        Map<String, Object> params = inputObject.getParams();
        String name = params.get("name").toString().trim();
        Integer orgType = Integer.parseInt(params.get("orgType").toString());
        if (!TenantOrgType.PERSONAL.getKey().equals(orgType) && !TenantOrgType.ENTERPRISE.getKey().equals(orgType)) {
            throw new CustomException("组织类型无效");
        }

        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setOrgType(orgType);
        tenant.setLogo(params.get("logo").toString());
        tenant.setRemark(params.get("remark").toString());
        tenant.setContactName(params.get("contactName").toString());
        tenant.setContactPhone(params.get("contactPhone").toString());
        tenant.setContactEmail(params.get("contactEmail").toString());
        if (TenantOrgType.ENTERPRISE.getKey().equals(orgType)) {
            tenant.setCreditCode(params.get("creditCode").toString());
            tenant.setLegalPerson(params.get("legalPerson").toString());
            tenant.setAddress(params.get("address").toString());
            tenant.setWebsite(params.get("website").toString());
            tenant.setIndustry(params.get("industry").toString());
        }

        String userId = inputObject.getLogParams().get("id").toString();
        String staffId = inputObject.getLogParams().get("staffId").toString();
        if (StrUtil.isBlank(staffId)) {
            throw new CustomException("当前用户未绑定员工信息，无法创建组织");
        }

        String tenantId = createEntity(tenant, userId);
        try {
            TenantContext.setTenantId(tenantId);
            TenantUser tenantUser = new TenantUser();
            tenantUser.setStaffId(staffId);
            tenantUser.setIsAdmin(WhetherEnum.ENABLE_USING.getKey());
            tenantUser.setState(UserStaffState.ON_THE_JOB.getKey());
            String currentTime = DateUtil.getTimeAndToString();
            tenantUser.setWorkTime(currentTime);
            tenantUser.setEntryTime(currentTime);
            tenantUser.setTrialTime(currentTime);
            tenantUserService.createEntity(tenantUser, userId);
        } finally {
            TenantContext.clear();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", tenantId);
        result.put("name", tenant.getName());
        result.put("logo", tenant.getLogo());
        result.put("orgType", tenant.getOrgType());
        result.put("isAdmin", CommonNumConstants.NUM_ONE);
        outputObject.setBean(result);
    }

    @Override
    @IgnoreTenant
    public void queryAllTenantListByKeyword(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String keyword = params.get("keyword").toString();
        if (StrUtil.isEmpty(keyword)) {
            return;
        }
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(Tenant::getName), keyword);
        List<Tenant> tenantList = list(queryWrapper);
        outputObject.setBeans(tenantList);
        outputObject.settotal(tenantList.size());
    }

    @Override
    @IgnoreTenant
    public void searchSearchableTenantList(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new CustomException("租户功能未开启");
        }
        Map<String, Object> params = inputObject.getParams();
        String keyword = params.get("keyword").toString().trim();
        if (StrUtil.isEmpty(keyword)) {
            return;
        }
        String staffId = inputObject.getLogParams().get("staffId").toString();

        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(Tenant::getName), keyword);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Tenant::getWhetherSearchable), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.ne(CommonConstants.ID, TenantTypeEnum.PLATFORM.getCode());
        List<Tenant> tenantList = list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantList)) {
            return;
        }

        List<String> tenantIds = tenantList.stream().map(Tenant::getId).collect(Collectors.toList());
        QueryWrapper<TenantUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        userQueryWrapper.in(MybatisPlusUtil.toColumns(TenantUser::getTenantId), tenantIds);
        userQueryWrapper.ne(MybatisPlusUtil.toColumns(TenantUser::getState), UserStaffState.QUIT.getKey());
        List<TenantUser> joinedList = tenantUserService.list(userQueryWrapper);
        Set<String> joinedIds = joinedList.stream().map(TenantUser::getTenantId).collect(Collectors.toSet());
        Map<String, TenantUserApply> pendingApplyMap = tenantUserApplyService.queryPendingApplyMapByStaffId(staffId, tenantIds);

        List<Map<String, Object>> beans = new ArrayList<>();
        for (Tenant tenant : tenantList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", tenant.getId());
            item.put("name", tenant.getName());
            item.put("logo", tenant.getLogo());
            item.put("remark", tenant.getRemark());
            item.put("orgType", tenant.getOrgType());
            item.put("whetherJoinNeedAudit", tenant.getWhetherJoinNeedAudit());
            if (joinedIds.contains(tenant.getId())) {
                item.put("joinFlag", 1);
            } else if (pendingApplyMap.containsKey(tenant.getId())) {
                item.put("joinFlag", 2);
                item.put("pendingApplyId", pendingApplyMap.get(tenant.getId()).getId());
            } else {
                item.put("joinFlag", 0);
            }
            beans.add(item);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}
