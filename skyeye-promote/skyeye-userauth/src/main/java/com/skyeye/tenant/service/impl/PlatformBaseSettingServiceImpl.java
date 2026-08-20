/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.classenum.PlatformBaseSettingGroup;
import com.skyeye.tenant.classenum.TenantOrgType;
import com.skyeye.tenant.constans.PlatformBaseSettingConst;
import com.skyeye.tenant.dao.PlatformBaseSettingDao;
import com.skyeye.tenant.entity.PlatformBaseSetting;
import com.skyeye.tenant.service.PlatformBaseSettingService;
import com.skyeye.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PlatformBaseSettingServiceImpl
 * @Description: 平台基础信息设置服务层（多租户模式下，仅平台租户可维护）
 * <p>
 * settingData 采用「分组 -> 设置项」的 JSON 结构，全局仅一条记录（单例配置）。
 * 新增设置项时：在 {@link PlatformBaseSettingGroup}、{@link PlatformBaseSettingConst} 中扩展，
 * 并在 {@link #validateSettingData} 中补充校验逻辑。
 */
@Service
@SkyeyeService(name = "平台基础信息设置", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class PlatformBaseSettingServiceImpl extends SkyeyeBusinessServiceImpl<PlatformBaseSettingDao, PlatformBaseSetting> implements PlatformBaseSettingService {

    @Lazy
    @Autowired
    private TenantService tenantService;

    /**
     * 未配置时的默认席位单价（元/席位）
     */
    private static final String DEFAULT_ACCOUNT_UNIT_PRICE = "0.00";

    /**
     * 个人组织默认初始化席位数
     */
    private static final int DEFAULT_PERSONAL_INIT_ACCOUNT_NUM = 1;

    /**
     * 个人组织默认最低购买席位数
     */
    private static final int DEFAULT_PERSONAL_MIN_BUY_ACCOUNT_NUM = 1;

    /**
     * 企业组织默认初始化席位数
     */
    private static final int DEFAULT_ENTERPRISE_INIT_ACCOUNT_NUM = 10;

    /**
     * 企业组织默认最低购买席位数
     */
    private static final int DEFAULT_ENTERPRISE_MIN_BUY_ACCOUNT_NUM = 5;

    /**
     * 默认允许账号自助创建组织
     */
    private static final int DEFAULT_ALLOW_USER_CREATE_ORG = WhetherEnum.ENABLE_USING.getKey();

    /**
     * 默认单个账号最多可自助创建的个人组织数量
     */
    private static final int DEFAULT_MAX_PERSONAL_ORG_PER_USER = 5;

    /**
     * 默认单个账号最多可自助创建的企业组织数量
     */
    private static final int DEFAULT_MAX_ENTERPRISE_ORG_PER_USER = 1;

    /**
     * 查询平台基础信息（管理端使用，需平台租户身份）
     */
    @Override
    public void queryPlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        PlatformBaseSetting platformBaseSetting = getSingletonSetting();
        outputObject.setBean(platformBaseSetting);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 保存平台基础信息（增量合并：仅覆盖本次提交的分组字段，不影响其他分组）
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updatePlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        PlatformBaseSetting entity = inputObject.getParams(PlatformBaseSetting.class);
        validateSettingData(entity.getSettingData());
        PlatformBaseSetting existing = getOne(new QueryWrapper<>(), false);
        String userId = inputObject.getLogParams().get("id").toString();
        String settingId;
        if (ObjectUtil.isEmpty(existing)) {
            entity.setSettingData(mergeSettingData(buildDefaultSettingData(), entity.getSettingData()));
            settingId = createEntity(entity, userId);
        } else {
            entity.setId(existing.getId());
            entity.setSettingData(mergeSettingData(existing.getSettingData(), entity.getSettingData()));
            settingId = updateEntity(entity, userId);
        }
        outputObject.setBean(selectById(settingId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 对外查询席位单价（普通租户可读，供购买订单等场景使用）
     */
    @Override
    @IgnoreTenant
    public void queryPlatformAccountUnitPrice(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        data.put(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE, getAccountUnitPrice());
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 按组织类型查询席位计费规则
     */
    @Override
    @IgnoreTenant
    public void queryPlatformTenantOrgSeatConfig(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Integer orgType = Integer.parseInt(params.get("orgType").toString());
        outputObject.setBean(buildOrgSeatConfigBean(orgType));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 获取平台配置的租户成员席位单价，供内部业务调用（如创建购买订单时默认带出单价）
     */
    @Override
    @IgnoreTenant
    public String getAccountUnitPrice() {
        Map<String, Object> tenantGroup = getTenantGroupSetting();
        Object price = tenantGroup.get(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE);
        return ObjectUtil.isEmpty(price) ? DEFAULT_ACCOUNT_UNIT_PRICE : price.toString();
    }

    @Override
    @IgnoreTenant
    public Integer getInitAccountNum(Integer orgType) {
        return getOrgTypeConfigInt(orgType, PlatformBaseSettingConst.KEY_INIT_ACCOUNT_NUM, getDefaultInitAccountNum(orgType));
    }

    @Override
    @IgnoreTenant
    public Integer getMinBuyAccountNum(Integer orgType) {
        return getOrgTypeConfigInt(orgType, PlatformBaseSettingConst.KEY_MIN_BUY_ACCOUNT_NUM, getDefaultMinBuyAccountNum(orgType));
    }

    @Override
    @IgnoreTenant
    public boolean isAllowUserCreateOrg() {
        Map<String, Object> tenantGroup = getTenantGroupSetting();
        Object allow = tenantGroup.get(PlatformBaseSettingConst.KEY_ALLOW_USER_CREATE_ORG);
        if (ObjectUtil.isEmpty(allow)) {
            return WhetherEnum.ENABLE_USING.getKey().equals(DEFAULT_ALLOW_USER_CREATE_ORG);
        }
        return WhetherEnum.ENABLE_USING.getKey().equals(NumberUtil.parseInt(allow.toString()));
    }

    @Override
    @IgnoreTenant
    public Integer getMaxPersonalOrgPerUser() {
        return getTenantGroupInt(PlatformBaseSettingConst.KEY_MAX_PERSONAL_ORG_PER_USER, DEFAULT_MAX_PERSONAL_ORG_PER_USER);
    }

    @Override
    @IgnoreTenant
    public Integer getMaxEnterpriseOrgPerUser() {
        return getTenantGroupInt(PlatformBaseSettingConst.KEY_MAX_ENTERPRISE_ORG_PER_USER, DEFAULT_MAX_ENTERPRISE_ORG_PER_USER);
    }

    @Override
    @IgnoreTenant
    public void queryPlatformTenantCreateConfig(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new CustomException("租户功能未开启");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        int personalCount = tenantService.countUserSelfCreatedTenantByOrgType(userId, TenantOrgType.PERSONAL.getKey());
        int enterpriseCount = tenantService.countUserSelfCreatedTenantByOrgType(userId, TenantOrgType.ENTERPRISE.getKey());
        Integer maxPersonal = getMaxPersonalOrgPerUser();
        Integer maxEnterprise = getMaxEnterpriseOrgPerUser();
        boolean allowUserCreateOrg = isAllowUserCreateOrg();
        Map<String, Object> data = new HashMap<>();
        data.put(PlatformBaseSettingConst.KEY_ALLOW_USER_CREATE_ORG, allowUserCreateOrg
            ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey());
        data.put(PlatformBaseSettingConst.KEY_MAX_PERSONAL_ORG_PER_USER, maxPersonal);
        data.put(PlatformBaseSettingConst.KEY_MAX_ENTERPRISE_ORG_PER_USER, maxEnterprise);
        data.put("userPersonalOrgCount", personalCount);
        data.put("userEnterpriseOrgCount", enterpriseCount);
        data.put("canCreatePersonal", allowUserCreateOrg && canCreateMore(personalCount, maxPersonal));
        data.put("canCreateEnterprise", allowUserCreateOrg && canCreateMore(enterpriseCount, maxEnterprise));
        data.put("canCreateOrg", allowUserCreateOrg && (
            canCreateMore(personalCount, maxPersonal) || canCreateMore(enterpriseCount, maxEnterprise)));
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public void queryPlatformAiRole(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        data.put(PlatformBaseSettingConst.KEY_AI_ROLE_ID, getAiRoleId());
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public String getAiRoleId() {
        Map<String, Object> aiGroup = getAiGroupSetting();
        Object roleId = aiGroup.get(PlatformBaseSettingConst.KEY_AI_ROLE_ID);
        return ObjectUtil.isEmpty(roleId) ? StrUtil.EMPTY : roleId.toString();
    }

    @Override
    public void validatorEntity(PlatformBaseSetting entity) {
        validateSettingData(entity.getSettingData());
    }

    /**
     * 获取单例配置；库中无记录时返回带默认值的对象（不落库）
     */
    private PlatformBaseSetting getSingletonSetting() {
        PlatformBaseSetting setting = getOne(new QueryWrapper<>(), false);
        if (ObjectUtil.isEmpty(setting)) {
            setting = new PlatformBaseSetting();
            setting.setSettingData(buildDefaultSettingData());
            return setting;
        }
        if (MapUtil.isEmpty(setting.getSettingData())) {
            setting.setSettingData(buildDefaultSettingData());
        } else {
            setting.setSettingData(mergeSettingData(buildDefaultSettingData(), setting.getSettingData()));
        }
        return setting;
    }

    /**
     * 构建各分组的默认配置，确保前端展示与合并时有完整结构
     */
    private Map<String, Map<String, Object>> buildDefaultSettingData() {
        Map<String, Map<String, Object>> settingData = new HashMap<>();
        Map<String, Object> tenantGroup = new HashMap<>();
        tenantGroup.put(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE, DEFAULT_ACCOUNT_UNIT_PRICE);
        tenantGroup.put(PlatformBaseSettingConst.KEY_ALLOW_USER_CREATE_ORG, DEFAULT_ALLOW_USER_CREATE_ORG);
        tenantGroup.put(PlatformBaseSettingConst.KEY_MAX_PERSONAL_ORG_PER_USER, DEFAULT_MAX_PERSONAL_ORG_PER_USER);
        tenantGroup.put(PlatformBaseSettingConst.KEY_MAX_ENTERPRISE_ORG_PER_USER, DEFAULT_MAX_ENTERPRISE_ORG_PER_USER);
        tenantGroup.put(PlatformBaseSettingConst.KEY_ORG_TYPE_CONFIG, buildDefaultOrgTypeConfig());
        settingData.put(PlatformBaseSettingGroup.TENANT.getKey(), tenantGroup);
        Map<String, Object> aiGroup = new HashMap<>();
        aiGroup.put(PlatformBaseSettingConst.KEY_AI_ROLE_ID, StrUtil.EMPTY);
        settingData.put(PlatformBaseSettingGroup.AI.getKey(), aiGroup);
        return settingData;
    }

    private Map<String, Map<String, Object>> buildDefaultOrgTypeConfig() {
        Map<String, Map<String, Object>> orgTypeConfig = new HashMap<>();
        orgTypeConfig.put(String.valueOf(TenantOrgType.PERSONAL.getKey()), buildOrgTypeItem(
            DEFAULT_PERSONAL_INIT_ACCOUNT_NUM, DEFAULT_PERSONAL_MIN_BUY_ACCOUNT_NUM));
        orgTypeConfig.put(String.valueOf(TenantOrgType.ENTERPRISE.getKey()), buildOrgTypeItem(
            DEFAULT_ENTERPRISE_INIT_ACCOUNT_NUM, DEFAULT_ENTERPRISE_MIN_BUY_ACCOUNT_NUM));
        return orgTypeConfig;
    }

    private Map<String, Object> buildOrgTypeItem(int initAccountNum, int minBuyAccountNum) {
        Map<String, Object> item = new HashMap<>();
        item.put(PlatformBaseSettingConst.KEY_INIT_ACCOUNT_NUM, initAccountNum);
        item.put(PlatformBaseSettingConst.KEY_MIN_BUY_ACCOUNT_NUM, minBuyAccountNum);
        return item;
    }

    private Map<String, Object> buildOrgSeatConfigBean(Integer orgType) {
        Map<String, Object> data = new HashMap<>();
        data.put("orgType", orgType);
        data.put(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE, getAccountUnitPrice());
        data.put(PlatformBaseSettingConst.KEY_INIT_ACCOUNT_NUM, getInitAccountNum(orgType));
        data.put(PlatformBaseSettingConst.KEY_MIN_BUY_ACCOUNT_NUM, getMinBuyAccountNum(orgType));
        return data;
    }

    private Map<String, Object> getTenantGroupSetting() {
        PlatformBaseSetting setting = getOne(new QueryWrapper<>(), false);
        if (ObjectUtil.isEmpty(setting) || MapUtil.isEmpty(setting.getSettingData())) {
            return buildDefaultSettingData().get(PlatformBaseSettingGroup.TENANT.getKey());
        }
        Map<String, Object> tenantGroup = setting.getSettingData().get(PlatformBaseSettingGroup.TENANT.getKey());
        if (MapUtil.isEmpty(tenantGroup)) {
            return buildDefaultSettingData().get(PlatformBaseSettingGroup.TENANT.getKey());
        }
        Map<String, Map<String, Object>> merged = mergeSettingData(buildDefaultSettingData(), setting.getSettingData());
        return merged.get(PlatformBaseSettingGroup.TENANT.getKey());
    }

    private Map<String, Object> getAiGroupSetting() {
        PlatformBaseSetting setting = getOne(new QueryWrapper<>(), false);
        if (ObjectUtil.isEmpty(setting) || MapUtil.isEmpty(setting.getSettingData())) {
            return buildDefaultSettingData().get(PlatformBaseSettingGroup.AI.getKey());
        }
        Map<String, Map<String, Object>> merged = mergeSettingData(buildDefaultSettingData(), setting.getSettingData());
        Map<String, Object> aiGroup = merged.get(PlatformBaseSettingGroup.AI.getKey());
        if (MapUtil.isEmpty(aiGroup)) {
            return buildDefaultSettingData().get(PlatformBaseSettingGroup.AI.getKey());
        }
        return aiGroup;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getOrgTypeConfigMap(Map<String, Object> tenantGroup) {
        Object orgTypeConfigObj = tenantGroup.get(PlatformBaseSettingConst.KEY_ORG_TYPE_CONFIG);
        if (!(orgTypeConfigObj instanceof Map)) {
            return buildDefaultOrgTypeConfig();
        }
        return (Map<String, Map<String, Object>>) orgTypeConfigObj;
    }

    private Integer getOrgTypeConfigInt(Integer orgType, String configKey, int defaultValue) {
        if (orgType == null) {
            return defaultValue;
        }
        Map<String, Object> tenantGroup = getTenantGroupSetting();
        Map<String, Map<String, Object>> orgTypeConfig = getOrgTypeConfigMap(tenantGroup);
        Map<String, Object> orgConfig = orgTypeConfig.get(String.valueOf(orgType));
        if (MapUtil.isEmpty(orgConfig) || ObjectUtil.isEmpty(orgConfig.get(configKey))) {
            return defaultValue;
        }
        return NumberUtil.parseInt(orgConfig.get(configKey).toString());
    }

    private int getDefaultInitAccountNum(Integer orgType) {
        if (TenantOrgType.PERSONAL.getKey().equals(orgType)) {
            return DEFAULT_PERSONAL_INIT_ACCOUNT_NUM;
        }
        return DEFAULT_ENTERPRISE_INIT_ACCOUNT_NUM;
    }

    private int getDefaultMinBuyAccountNum(Integer orgType) {
        if (TenantOrgType.PERSONAL.getKey().equals(orgType)) {
            return DEFAULT_PERSONAL_MIN_BUY_ACCOUNT_NUM;
        }
        return DEFAULT_ENTERPRISE_MIN_BUY_ACCOUNT_NUM;
    }

    /**
     * 增量合并设置数据：incoming 中的分组/字段覆盖 base 对应项，未提交的分组保持不变
     */
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> mergeSettingData(Map<String, Map<String, Object>> base,
                                                              Map<String, Map<String, Object>> incoming) {
        Map<String, Map<String, Object>> merged = new HashMap<>();
        if (MapUtil.isNotEmpty(base)) {
            base.forEach((groupKey, groupValue) -> merged.put(groupKey, new HashMap<>(groupValue)));
        }
        if (MapUtil.isEmpty(incoming)) {
            return merged;
        }
        incoming.forEach((groupKey, groupValue) -> {
            if (MapUtil.isEmpty(groupValue)) {
                return;
            }
            Map<String, Object> targetGroup = merged.computeIfAbsent(groupKey, key -> new HashMap<>());
            groupValue.forEach((itemKey, itemValue) -> {
                if (PlatformBaseSettingConst.KEY_ORG_TYPE_CONFIG.equals(itemKey) && itemValue instanceof Map) {
                    Map<String, Object> targetOrgConfig = (Map<String, Object>) targetGroup.computeIfAbsent(itemKey, k -> new HashMap<>());
                    ((Map<String, Object>) itemValue).forEach((orgTypeKey, orgConfig) -> {
                        if (orgConfig instanceof Map) {
                            Map<String, Object> targetOrg = (Map<String, Object>) targetOrgConfig.computeIfAbsent(orgTypeKey, k -> new HashMap<>());
                            targetOrg.putAll((Map<String, Object>) orgConfig);
                        }
                    });
                } else {
                    targetGroup.put(itemKey, itemValue);
                }
            });
        });
        return merged;
    }

    /**
     * 按分组校验设置项；新增分组时在此扩展校验规则
     */
    @SuppressWarnings("unchecked")
    private void validateSettingData(Map<String, Map<String, Object>> settingData) {
        if (MapUtil.isEmpty(settingData)) {
            return;
        }
        Map<String, Object> tenantGroup = settingData.get(PlatformBaseSettingGroup.TENANT.getKey());
        if (MapUtil.isNotEmpty(tenantGroup)) {
            validateTenantGroup(tenantGroup);
        }
        Map<String, Object> aiGroup = settingData.get(PlatformBaseSettingGroup.AI.getKey());
        if (MapUtil.isNotEmpty(aiGroup) && aiGroup.containsKey(PlatformBaseSettingConst.KEY_AI_ROLE_ID)) {
            Object roleId = aiGroup.get(PlatformBaseSettingConst.KEY_AI_ROLE_ID);
            if (ObjectUtil.isNotEmpty(roleId) && StrUtil.isBlank(roleId.toString().trim())) {
                aiGroup.put(PlatformBaseSettingConst.KEY_AI_ROLE_ID, StrUtil.EMPTY);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void validateTenantGroup(Map<String, Object> tenantGroup) {
        if (tenantGroup.containsKey(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE)) {
            Object accountUnitPrice = tenantGroup.get(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE);
            if (ObjectUtil.isEmpty(accountUnitPrice) || StrUtil.isBlank(accountUnitPrice.toString())) {
                throw new CustomException("成员席位单价不能为空");
            }
            validatePrice(accountUnitPrice.toString(), "成员席位单价");
        }
        if (tenantGroup.containsKey(PlatformBaseSettingConst.KEY_ALLOW_USER_CREATE_ORG)) {
            validateWhetherFlag(tenantGroup.get(PlatformBaseSettingConst.KEY_ALLOW_USER_CREATE_ORG), "是否允许账号创建组织");
        }
        if (tenantGroup.containsKey(PlatformBaseSettingConst.KEY_MAX_PERSONAL_ORG_PER_USER)) {
            validatePositiveInteger(tenantGroup.get(PlatformBaseSettingConst.KEY_MAX_PERSONAL_ORG_PER_USER),
                "个人组织数量上限");
        }
        if (tenantGroup.containsKey(PlatformBaseSettingConst.KEY_MAX_ENTERPRISE_ORG_PER_USER)) {
            validatePositiveInteger(tenantGroup.get(PlatformBaseSettingConst.KEY_MAX_ENTERPRISE_ORG_PER_USER),
                "企业组织数量上限");
        }
        Object orgTypeConfigObj = tenantGroup.get(PlatformBaseSettingConst.KEY_ORG_TYPE_CONFIG);
        if (ObjectUtil.isEmpty(orgTypeConfigObj) || !(orgTypeConfigObj instanceof Map)) {
            return;
        }
        Map<String, Object> orgTypeConfig = (Map<String, Object>) orgTypeConfigObj;
        for (TenantOrgType tenantOrgType : TenantOrgType.values()) {
            if (!Boolean.TRUE.equals(tenantOrgType.getShow())) {
                continue;
            }
            String orgTypeKey = String.valueOf(tenantOrgType.getKey());
            Object orgConfigObj = orgTypeConfig.get(orgTypeKey);
            if (ObjectUtil.isEmpty(orgConfigObj) || !(orgConfigObj instanceof Map)) {
                throw new CustomException(tenantOrgType.getValue() + "席位规则不能为空");
            }
            Map<String, Object> orgConfig = (Map<String, Object>) orgConfigObj;
            validatePositiveInteger(orgConfig.get(PlatformBaseSettingConst.KEY_INIT_ACCOUNT_NUM),
                tenantOrgType.getValue() + "初始化席位数");
            validatePositiveInteger(orgConfig.get(PlatformBaseSettingConst.KEY_MIN_BUY_ACCOUNT_NUM),
                tenantOrgType.getValue() + "最低购买席位数");
        }
    }

    private Integer getTenantGroupInt(String configKey, int defaultValue) {
        Map<String, Object> tenantGroup = getTenantGroupSetting();
        Object value = tenantGroup.get(configKey);
        if (ObjectUtil.isEmpty(value) || StrUtil.isBlank(value.toString())) {
            return defaultValue;
        }
        return NumberUtil.parseInt(value.toString());
    }

    private boolean canCreateMore(int currentCount, Integer maxCount) {
        if (maxCount == null || maxCount < CommonNumConstants.NUM_ONE) {
            return false;
        }
        return currentCount < maxCount;
    }

    /**
     * 校验金额格式：非负，最多两位小数
     */
    private void validatePrice(String price, String label) {
        if (!price.matches("^\\d+(\\.\\d{1,2})?$")) {
            throw new CustomException(label + "格式不正确，最多保留两位小数");
        }
        BigDecimal amount = NumberUtil.toBigDecimal(price);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(label + "不能小于0");
        }
    }

    /**
     * 校验正整数（>= 1）
     */
    private void validatePositiveInteger(Object value, String label) {
        if (ObjectUtil.isEmpty(value) || StrUtil.isBlank(value.toString())) {
            throw new CustomException(label + "不能为空");
        }
        if (!value.toString().matches("^\\d+$")) {
            throw new CustomException(label + "必须为正整数");
        }
        if (NumberUtil.parseInt(value.toString()) < CommonNumConstants.NUM_ONE) {
            throw new CustomException(label + "不能小于1");
        }
    }

    /**
     * 校验是否开关（0/1）
     */
    private void validateWhetherFlag(Object value, String label) {
        if (ObjectUtil.isEmpty(value) || StrUtil.isBlank(value.toString())) {
            throw new CustomException(label + "不能为空");
        }
        int flag = NumberUtil.parseInt(value.toString());
        if (!WhetherEnum.ENABLE_USING.getKey().equals(flag) && !WhetherEnum.DISABLE_USING.getKey().equals(flag)) {
            throw new CustomException(label + "取值无效");
        }
    }

    /**
     * 断言当前为平台租户，管理类接口调用前校验
     */
    private void assertPlatformTenant() {
        if (!tenantEnable) {
            throw new IllegalArgumentException("租户功能未开启");
        }
        String tenantId = TenantContext.getTenantId();
        if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
            throw new IllegalArgumentException("非平台租户不能访问");
        }
    }

}
