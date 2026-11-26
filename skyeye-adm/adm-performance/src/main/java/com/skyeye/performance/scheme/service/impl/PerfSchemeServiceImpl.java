package com.skyeye.performance.scheme.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.ApplicableObjectsType;
import com.skyeye.common.util.JsonConfigUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.performance.assessment.service.PerfAssessmentService;
import com.skyeye.performance.classenum.PerfCycleType;
import com.skyeye.performance.classenum.PerfItemSourceType;
import com.skyeye.performance.classenum.PerfSchemeType;
import com.skyeye.performance.cycle.dao.PerfCycleDao;
import com.skyeye.performance.cycle.entity.PerfCycle;
import com.skyeye.performance.scheme.dao.PerfSchemeDao;
import com.skyeye.performance.scheme.entity.PerfScheme;
import com.skyeye.performance.scheme.entity.PerfSchemeApplicableObjects;
import com.skyeye.performance.scheme.entity.PerfSchemeItem;
import com.skyeye.performance.scheme.service.PerfSchemeApplicableObjectsService;
import com.skyeye.performance.scheme.service.PerfSchemeItemService;
import com.skyeye.performance.scheme.service.PerfSchemeService;
import com.skyeye.rest.pro.service.ISysEveUserStaffService;
import com.skyeye.common.tenant.context.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "绩效考核方案", groupName = "员工绩效")
public class PerfSchemeServiceImpl extends SkyeyeBusinessServiceImpl<PerfSchemeDao, PerfScheme>
    implements PerfSchemeService {

    @Autowired
    private PerfSchemeItemService perfSchemeItemService;

    @Autowired
    private PerfSchemeApplicableObjectsService perfSchemeApplicableObjectsService;

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ISysEveUserStaffService iSysEveUserStaffService;

    @Autowired
    @Lazy
    private PerfAssessmentService perfAssessmentService;

    @Autowired
    private PerfCycleDao perfCycleDao;

    @Value("${skyeye.tenant.enable:false}")
    private Boolean tenantEnable;

    @Override
    protected QueryWrapper<PerfScheme> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PerfScheme> wrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            wrapper.eq(MybatisPlusUtil.toColumns(PerfScheme::getEnabled), commonPageInfo.getEnabled());
        }
        return wrapper;
    }

    @Override
    public void validatorEntity(PerfScheme entity) {
        if (CollectionUtil.isEmpty(entity.getSchemeItemList())) {
            throw new CustomException("请配置考核指标");
        }
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (PerfSchemeItem item : entity.getSchemeItemList()) {
            if (PerfItemSourceType.SURVEY.getKey().equals(item.getSourceType())
                && StrUtil.isBlank(item.getSourceConfig())) {
                throw new CustomException("问卷来源指标「" + item.getName() + "」请填写问卷ID");
            }
            if (PerfItemSourceType.PIECEWORK.getKey().equals(item.getSourceType())) {
                validatePieceworkConfig(item);
            }
            totalWeight = totalWeight.add(item.getWeight());
        }
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new CustomException("指标权重合计必须为100%");
        }
        if (PerfSchemeType.OKR.getKey().equals(entity.getSchemeType())
            && StrUtil.isBlank(entity.getObjective())) {
            throw new CustomException("OKR 方案请填写目标（Objective）");
        }
        if (PerfSchemeType.DEGREE360.getKey().equals(entity.getSchemeType())) {
            validateRaterRule(entity.getRaterRule());
        }
        if (StrUtil.isEmpty(entity.getGradeRule())) {
            throw new CustomException("请配置等级规则");
        }
        java.util.List<cn.hutool.json.JSONObject> gradeRules = JsonConfigUtil.parseObjList(entity.getGradeRule());
        if (CollectionUtil.isEmpty(gradeRules)) {
            throw new CustomException("请至少配置一条等级规则");
        }
        for (cn.hutool.json.JSONObject rule : gradeRules) {
            if (StrUtil.isEmpty(rule.getStr("grade"))) {
                throw new CustomException("等级名称不能为空");
            }
            BigDecimal min = rule.getBigDecimal("min", BigDecimal.ZERO);
            BigDecimal max = rule.getBigDecimal("max", new BigDecimal("100"));
            if (min.compareTo(max) > 0) {
                throw new CustomException("等级「" + rule.getStr("grade") + "」最低分不能大于最高分");
            }
        }
        QueryWrapper<PerfScheme> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfScheme::getName), entity.getName());
        if (StrUtil.isNotEmpty(entity.getId())) {
            wrapper.ne(CommonConstants.ID, entity.getId());
        }
        if (getOne(wrapper, false) != null) {
            throw new CustomException("方案名称已存在");
        }
    }

    @Override
    public void writePostpose(PerfScheme entity, String userId) {
        super.writePostpose(entity, userId);
        perfSchemeItemService.saveSchemeItems(entity.getId(), entity.getSchemeItemList());
        perfSchemeApplicableObjectsService.saveApplicableObjects(entity.getId(), entity.getApplicableObjectsList());
    }

    @Override
    public void deletePreExecution(String id) {
        QueryWrapper<PerfCycle> cycleWrapper = new QueryWrapper<>();
        cycleWrapper.eq(MybatisPlusUtil.toColumns(PerfCycle::getSchemeId), id);
        if (perfCycleDao.selectCount(cycleWrapper) > 0) {
            throw new CustomException("该方案已关联考核周期，无法删除");
        }
        if (perfAssessmentService.countBySchemeId(id) > 0) {
            throw new CustomException("该方案已生成考核单，无法删除");
        }
    }

    @Override
    public void deletePostpose(String id) {
        perfSchemeItemService.deleteBySchemeId(id);
        perfSchemeApplicableObjectsService.deleteBySchemeId(id);
    }

    @Override
    public PerfScheme getDataFromDb(String id) {
        PerfScheme scheme = super.getDataFromDb(id);
        scheme.setSchemeItemList(perfSchemeItemService.queryBySchemeId(id));
        scheme.setApplicableObjectsList(perfSchemeApplicableObjectsService.queryBySchemeId(id));
        return scheme;
    }

    @Override
    public PerfScheme selectById(String id) {
        PerfScheme scheme = super.selectById(id);
        scheme.setSchemeTypeMation(PerfSchemeType.getMation(scheme.getSchemeType()));
        scheme.setCycleTypeMation(PerfCycleType.getMation(scheme.getCycleType()));
        if (CollectionUtil.isNotEmpty(scheme.getSchemeItemList())) {
            scheme.getSchemeItemList().forEach(item ->
                item.setSourceTypeMation(PerfItemSourceType.getMation(item.getSourceType())));
        }
        setApplicable(scheme.getApplicableObjectsList());
        return scheme;
    }

    private void setApplicable(List<PerfSchemeApplicableObjects> applicableObjectsList) {
        if (CollectionUtil.isEmpty(applicableObjectsList)) {
            return;
        }
        Map<Integer, List<PerfSchemeApplicableObjects>> listMap = applicableObjectsList.stream()
            .collect(Collectors.groupingBy(PerfSchemeApplicableObjects::getObjectType));
        listMap.forEach((key, value) -> {
            List<String> ids = value.stream().map(PerfSchemeApplicableObjects::getObjectId).collect(Collectors.toList());
            if (ApplicableObjectsType.STAFF.getKey().equals(key)) {
                Map<String, Map<String, Object>> staffMaps = iAuthUserService.queryUserMationListByStaffIds(ids);
                setObjectMation(applicableObjectsList, staffMaps, key);
            } else if (ApplicableObjectsType.DEPARTMENT.getKey().equals(key)) {
                String departmentIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
                Map<String, Map<String, Object>> departMent = iDepmentService.queryDataMationForMapByIds(departmentIdStr);
                setObjectMation(applicableObjectsList, departMent, key);
            } else if (ApplicableObjectsType.COMPANY.getKey().equals(key)) {
                String companyIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(ids);
                Map<String, Map<String, Object>> company = iCompanyService.queryDataMationForMapByIds(companyIdStr);
                setObjectMation(applicableObjectsList, company, key);
            }
        });
    }

    private static void setObjectMation(List<PerfSchemeApplicableObjects> list,
                                        Map<String, Map<String, Object>> temMap, Integer key) {
        if (CollectionUtil.isEmpty(temMap)) {
            return;
        }
        list.forEach(item -> {
            if (key.equals(item.getObjectType())) {
                item.setObjectMation(temMap.get(item.getObjectId()));
            }
        });
    }

    private void validatePieceworkConfig(PerfSchemeItem item) {
        if (StrUtil.isBlank(item.getSourceConfig())) {
            throw new CustomException("计件来源指标「" + item.getName() + "」请配置目标产量/工时");
        }
        String raw = item.getSourceConfig().trim();
        if (!raw.startsWith("{")) {
            throw new CustomException("计件来源指标「" + item.getName()
                + "」参数格式错误，例：{\"targetNum\":1000,\"isNumTime\":1}");
        }
        cn.hutool.json.JSONObject cfg = JsonConfigUtil.parseConfig(raw);
        if (cfg.isEmpty()) {
            throw new CustomException("计件来源指标「" + item.getName()
                + "」参数格式错误，例：{\"targetNum\":1000,\"isNumTime\":1}");
        }
        BigDecimal targetNum = cfg.getBigDecimal("targetNum");
        if (targetNum == null || targetNum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("计件来源指标「" + item.getName() + "」targetNum 必须大于0");
        }
    }

    private void validateRaterRule(String raterRule) {
        if (StrUtil.isBlank(raterRule)) {
            throw new CustomException("360方案请配置评价人权重规则");
        }
        java.util.List<cn.hutool.json.JSONObject> rules = JsonConfigUtil.parseObjList(raterRule);
        if (CollectionUtil.isEmpty(rules)) {
            throw new CustomException("360方案请至少配置一类评价人");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (cn.hutool.json.JSONObject rule : rules) {
            if (StrUtil.isEmpty(rule.getStr("role"))) {
                throw new CustomException("评价人角色不能为空");
            }
            BigDecimal weight = rule.getBigDecimal("weight", BigDecimal.ZERO);
            if (weight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException("评价人权重必须大于0");
            }
            total = total.add(weight);
        }
        if (total.compareTo(new BigDecimal("100")) != 0) {
            throw new CustomException("360评价人权重合计必须为100%");
        }
    }

    @Override
    public List<String> resolveStaffIds(String schemeId) {
        List<PerfSchemeApplicableObjects> objects = perfSchemeApplicableObjectsService.queryBySchemeId(schemeId);
        if (CollectionUtil.isEmpty(objects)) {
            throw new CustomException("方案未配置适用对象");
        }
        Set<String> staffIds = new HashSet<>();
        List<String> deptIds = new ArrayList<>();
        List<String> companyIds = new ArrayList<>();
        for (PerfSchemeApplicableObjects obj : objects) {
            if (ApplicableObjectsType.STAFF.getKey().equals(obj.getObjectType())) {
                staffIds.add(obj.getObjectId());
            } else if (ApplicableObjectsType.DEPARTMENT.getKey().equals(obj.getObjectType())) {
                deptIds.add(obj.getObjectId());
            } else if (ApplicableObjectsType.COMPANY.getKey().equals(obj.getObjectType())) {
                companyIds.add(obj.getObjectId());
            }
        }
        if (CollectionUtil.isNotEmpty(deptIds) || CollectionUtil.isNotEmpty(companyIds)) {
            String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
            List<Map<String, Object>> tenantUsers = iSysEveUserStaffService.queryTenantUserByTenantId(tenantId, null);
            if (CollectionUtil.isNotEmpty(tenantUsers)) {
                for (Map<String, Object> user : tenantUsers) {
                    String staffId = String.valueOf(user.getOrDefault("staffId", StrUtil.EMPTY));
                    if (StrUtil.isEmpty(staffId)) {
                        continue;
                    }
                    String departmentId = String.valueOf(user.getOrDefault("departmentId", StrUtil.EMPTY));
                    String companyId = String.valueOf(user.getOrDefault("companyId", StrUtil.EMPTY));
                    if (deptIds.contains(departmentId) || companyIds.contains(companyId)) {
                        staffIds.add(staffId);
                    }
                }
            }
        }
        if (CollectionUtil.isEmpty(staffIds)) {
            throw new CustomException("适用对象未解析到任何员工");
        }
        return new ArrayList<>(staffIds);
    }
}
