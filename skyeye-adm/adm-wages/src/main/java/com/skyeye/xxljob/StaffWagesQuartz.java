/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.WagesConstant;
import com.skyeye.common.enumeration.AbnormalCheckworkType;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.*;
import com.skyeye.eve.field.classenum.WagesTypeEnum;
import com.skyeye.eve.field.dao.FieldStaffLinkDao;
import com.skyeye.eve.field.entity.FieldType;
import com.skyeye.eve.field.service.FieldStaffLinkService;
import com.skyeye.eve.model.classenum.WagesModelFieldType;
import com.skyeye.eve.model.dao.CompanyTaxRateDao;
import com.skyeye.eve.model.dao.SysEveUserStaffDao;
import com.skyeye.eve.model.dao.WagesModelFieldDao;
import com.skyeye.eve.model.entity.ModelApplicableObjects;
import com.skyeye.eve.model.entity.WagesModel;
import com.skyeye.eve.model.entity.WagesModelField;
import com.skyeye.eve.model.service.WagesModelService;
import com.skyeye.eve.payment.classenum.PaymentHistoryState;
import com.skyeye.eve.payment.classenum.PaymentHistoryType;
import com.skyeye.eve.payment.entity.WagesPaymentHistory;
import com.skyeye.eve.payment.service.WagesPaymentHistoryService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.eve.social.entity.ApplicableObjects;
import com.skyeye.eve.social.entity.SocialSecurityFund;
import com.skyeye.eve.social.service.WagesSocialSecurityFundService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveTimeSlotService;
import com.skyeye.reward.entity.RewardPunish;
import com.skyeye.reward.service.RewardPunishService;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.util.CheckWorkHourCalcUtil;
import com.skyeye.worktime.util.CheckWorkTimePeriodUtil;
import com.skyeye.worktime.service.CheckWorkTimeService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: StaffWagesQuartz
 * @Description: 定时统计上个月员工的薪资情况
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 18:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
public class StaffWagesQuartz {

    private static Logger LOGGER = LoggerFactory.getLogger(StaffWagesQuartz.class);

    @Autowired
    private FieldStaffLinkDao wagesStaffMationDao;

    @Autowired
    private JedisClientService jedisClient;

    @Autowired
    private CompanyTaxRateDao companyTaxRateDao;

    @Autowired
    private WagesModelFieldDao wagesModelFieldDao;

    @Autowired
    private WagesSocialSecurityFundService wagesSocialSecurityFundService;

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

    @Autowired
    private WagesModelService wagesModelService;

    @Autowired
    private FieldStaffLinkService wagesStaffMationService;

    @Autowired
    private WagesPaymentHistoryService wagesPaymentHistoryService;

    @Autowired
    private ITenantService iTenantService;

    @Autowired
    protected IAuthUserService iAuthUserService;

    @Autowired
    private RewardPunishService rewardPunishService;

    @Autowired
    private LeaveTimeSlotService leaveTimeSlotService;

    /**
     * 当前薪资统计中的员工id存储在redis的key，因为存在多台机器同时处理员工薪资的情况，所以不去主动删除该缓存信息，等待自动失效即可
     */
    private static final String IN_STATISTICS_STAFF_REDIS_KEY = "inStatisticsWagesStaff";

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 每月十号的凌晨两点开始执行薪资统计任务
     * 1. 员工的薪资必须是已设定状态 design_wages = 2
     */
    @XxlJob("staffWagesQuartz")
    public void statisticsStaffWages() {
        try {
            if (tenantEnable) {
                //  开启多租户
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    String tenantId = tenant.get("id").toString();
                    TenantContext.setTenantId(tenantId);
                    calcUserStaffWages(tenantId);
                });
            } else {
                // 未开启多租户
                calcUserStaffWages(null);
            }
        } catch (Exception e) {
            LOGGER.warn("StaffWagesQuartz error.", e);
        }
        LOGGER.info("statistics staff wages month is end");
    }

    private void calcUserStaffWages(String tenantId) {
        // 获取上个月的年月
        String lastMonthDate = DateUtil.getLastMonthDate();
//        String lastMonthDate = "2025-06";
        LOGGER.info("statistics staff wages month is {}", lastMonthDate);
        // 个人所得税缴纳比例
        Map<String, List<Map<String, Object>>> taxRate = getTaxRate(tenantId);
        // 所有启用中的薪资模板适用对象关系以及模板要素字段
        List<WagesModel> wagesModelList = wagesModelService.queryWagesModelByDate(lastMonthDate);
        // 所有启动中的社保公积金适用对象关系以及社保公积金参数信息
        List<SocialSecurityFund> socialSecurityFund = wagesSocialSecurityFundService.querySocialSecurityFundByDate(lastMonthDate);
        // 系统基础信息
        Map<String, Object> systemFoundationSettings = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        // 所有的考勤班次信息
        List<CheckWorkTime> workTime = checkWorkTimeService.getAllCheckWorkTime(lastMonthDate);
        while (true) {
            // 获取一条未生成薪资的员工数据
            Map<String, Object> staff = wagesStaffMationDao.queryNoWagesLastMonthByLastMonthDate(lastMonthDate, getStaffIdsFromRedis(tenantId), tenantId);
            System.out.println(JSONUtil.toJsonStr(staff));
            // 如果已经没有要统计薪资的员工，则停止统计
            if (staff == null) {
                break;
            }
            String staffId = staff.get("id").toString();
            // 判断该员工的薪资统计是否在处理中，如果在处理中，则进行下一条
            if (isInStatisticsRedisMation(staffId, tenantId)) {
                continue;
            }
            try {
                // 锁定该员工为处理中
                addStaffIdInStatisticsRedisMation(staffId, tenantId);
                if (tenantEnable) {
                    staff = iAuthUserService.queryDataMationById(staff.get("userId").toString());
                    if (CollectionUtil.isEmpty(staff)) {
                        continue;
                    }
                    staff.put("userId", staff.get("id"));
                    staff.put("id", staffId);
                }
                // 开始统计
                calcStaffWages(staff, wagesModelList, socialSecurityFund, systemFoundationSettings, workTime, lastMonthDate, taxRate, tenantId);
                // 将指定员工月度清零的薪资字段设置为0
                wagesStaffMationDao.editStaffMonthlyClearingWagesByStaffId(staffId, tenantId);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warn("deal with staff failed, staffId is {}", staffId, e);
                break;
            } finally {
                // 从正在处理中的员工集合数据中移除，说明该员工数据已经处理完成
                removeStaffIdInStatisticsRedisMation(staffId, tenantId);
            }
        }
        deleteStatisticsRedisMation(tenantId);
    }

    /**
     * 统计员工的薪资
     *
     * @param staff                    员工信息
     * @param wagesModelList           所有启用中的薪资模板
     * @param socialSecurityFund       所有启动中的社保公积金适用对象关系以及社保公积金参数信息
     * @param systemFoundationSettings 系统基础信息
     * @param workTime                 考勤制度
     * @param lastMonthDate            上个月的年月
     * @param taxRate                  个人所得税缴纳比例
     */
    private void calcStaffWages(Map<String, Object> staff, List<WagesModel> wagesModelList, List<SocialSecurityFund> socialSecurityFund,
                                Map<String, Object> systemFoundationSettings, List<CheckWorkTime> workTime, String lastMonthDate, Map<String,
            List<Map<String, Object>>> taxRate, String tenantId) {
        String companyId = staff.getOrDefault("companyId", StrUtil.EMPTY).toString();
        String departmentId = staff.getOrDefault("departmentId", StrUtil.EMPTY).toString();
        String staffId = staff.get("id").toString();
        // 员工应发薪资
        String actWages = staff.get("actWages").toString();
        LOGGER.info("staffId is {}, actWages is {}", staffId, actWages);
        // 获取该员工具备的模板id
        List<String> modelIds = getModelIdsForStaff(companyId, departmentId, staffId, wagesModelList);
        // 该员工拥有的所有薪资要素字段以及对应的值
        List<Map<String, Object>> staffModelField = getUserStaffWagesModelField(modelIds, staffId, tenantId);
        Map<String, String> staffModelFieldMap = convert2Map(staffModelField);
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_SALARY.getKey(), actWages);
        // 获取该员工应该缴纳的社保公积金的金额
        String socialSecurityFundMoney = getSocialSecurityFundMoney(companyId, departmentId, staffId, socialSecurityFund, staffModelFieldMap);
        LOGGER.info("staffId is {}, socialSecurityFundMoney is {}", staffId, socialSecurityFundMoney);
        // 计算员工的考勤相关应扣的薪资
        String staffCheckWorkMoney = calcStaffCheckWork(staffId, systemFoundationSettings, workTime, staffModelFieldMap, lastMonthDate, tenantId);
        LOGGER.info("staffId is {}, staffCheckWorkMoney is {}", staffId, staffCheckWorkMoney);
        // 计算员工的奖惩金额
        String rewardPunishMoney = calcRewardPunishMoney(staffId, lastMonthDate);
        LOGGER.info("staffId is {}, rewardPunishMoney is {}", staffId, rewardPunishMoney);
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_REWARD_PUNISH_MONEY.getKey(), rewardPunishMoney);
        // 开始计算上月实发工资
        String monthlyStandardRealMoney = calcMonthRealMoney(lastMonthDate, taxRate, companyId, actWages,
            staffModelField, staffModelFieldMap, socialSecurityFundMoney, staffCheckWorkMoney, rewardPunishMoney);
        // 实发薪资
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_REAL_MONEY.getKey(), monthlyStandardRealMoney);
        // 开始输出json
        outputJsonToSQL(wagesModelList, staffModelFieldMap, staffId, lastMonthDate, modelIds);
    }

    private List<String> getModelIdsForStaff(String companyId, String departmentId, String staffId, List<WagesModel> wagesModelList) {
        List<String> temIds = CollectionUtil.newArrayList(companyId, departmentId, staffId).stream()
            .filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> modelIds = wagesModelList.stream().filter(bean -> {
            if (CollectionUtil.isEmpty(bean.getApplicableObjectsList())) {
                return false;
            }
            List<String> objectIds = bean.getApplicableObjectsList().stream().filter(item -> ObjectUtil.isNotEmpty(item)).map(ModelApplicableObjects::getObjectId).collect(Collectors.toList());
            return objectIds.stream().anyMatch(str -> temIds.contains(str));
        }).map(WagesModel::getId).distinct().collect(Collectors.toList());
        return modelIds;
    }

    /**
     * 开始计算上月实发工资
     *
     * @param lastMonthDate           上月的日期
     * @param taxRate                 个人所得税缴纳比例
     * @param companyId               企业id
     * @param actWages                员工应发薪资
     * @param staffModelField         该员工拥有的所有薪资要素字段以及对应的值
     * @param staffModelFieldMap      该员工拥有的所有薪资要素字段以及对应的值转成的map
     * @param socialSecurityFundMoney 该员工应该缴纳的社保公积金的金额
     * @param staffCheckWorkMoney     员工的考勤相关应扣的薪资，如果为负数，则说明加班和销假>请假以及异常考勤
     * @param rewardPunishMoney       员工的奖惩金额，正数为奖励，负数为惩罚
     * @return
     */
    private String calcMonthRealMoney(String lastMonthDate, Map<String, List<Map<String, Object>>> taxRate,
                                      String companyId, String actWages, List<Map<String, Object>> staffModelField,
                                      Map<String, String> staffModelFieldMap, String socialSecurityFundMoney, String staffCheckWorkMoney, String rewardPunishMoney) {
        String monthlyStandardRealMoney = CalculationUtil.subtract(actWages, socialSecurityFundMoney, CommonNumConstants.NUM_TWO);
        monthlyStandardRealMoney = CalculationUtil.subtract(monthlyStandardRealMoney, staffCheckWorkMoney, CommonNumConstants.NUM_TWO);
        // 奖惩金额：正数为奖励（增加），负数为惩罚（减少）
        monthlyStandardRealMoney = CalculationUtil.add(monthlyStandardRealMoney, rewardPunishMoney, CommonNumConstants.NUM_TWO);
        for (Map<String, Object> bean : staffModelField) {
            if (IsDefaultEnum.IS_DEFAULT.getKey().equals(bean.get("monthlyClearing").toString())) {
                // 只算自动清零的
                if (WagesTypeEnum.SALARY_INCREASE.getKey().equals(bean.get("wagesType").toString())) {
                    // 薪资增加
                    monthlyStandardRealMoney =
                        CalculationUtil.add(monthlyStandardRealMoney, bean.get("amountMoney").toString(), CommonNumConstants.NUM_TWO);
                } else if (WagesTypeEnum.SALARY_REDUCTION.getKey().equals(bean.get("wagesType").toString())) {
                    // 薪资减少
                    monthlyStandardRealMoney =
                        CalculationUtil.subtract(monthlyStandardRealMoney, bean.get("amountMoney").toString(), CommonNumConstants.NUM_TWO);
                }
            }
        }
        monthlyStandardRealMoney =
            calcTaxRate(monthlyStandardRealMoney, taxRate, companyId, lastMonthDate, staffModelFieldMap);
        return monthlyStandardRealMoney;
    }

    /**
     * 输出json
     *
     * @param wagesModelList     所有启用中的薪资模板
     * @param staffModelFieldMap 薪资数据
     * @param staffId            员工id
     * @param lastMonthDate      上个月的年月
     * @param modelIds           员工拥有的模板的模型id
     */
    private void outputJsonToSQL(List<WagesModel> wagesModelList, Map<String, String> staffModelFieldMap, String staffId, String lastMonthDate,
                                 List<String> modelIds) {
        WagesPaymentHistory wagesPaymentHistory = new WagesPaymentHistory();
        wagesPaymentHistory.setStaffId(staffId);
        wagesPaymentHistory.setCreateTime(DateUtil.getTimeAndToString());
        wagesPaymentHistory.setPayMonth(lastMonthDate);
        wagesPaymentHistory.setActWages(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_REAL_MONEY.getKey()));
        wagesPaymentHistory.setState(PaymentHistoryState.UNISSUED.getKey());
        wagesPaymentHistory.setType(PaymentHistoryType.SYSTEM_ACCOUNTING.getKey());
        List<WagesModel> staffModel = wagesModelList.stream().filter(bean -> modelIds.contains(bean.getId()))
            .collect(Collectors.toList());
        wagesPaymentHistory.setWagesJson(getWagesJson(staffModel, staffModelFieldMap));
        wagesPaymentHistoryService.createEntity(wagesPaymentHistory, StrUtil.EMPTY);
    }

    /**
     * 获取员工的json薪资列表
     *
     * @param staffModel         该员工拥有的所有薪资模板
     * @param staffModelFieldMap 薪资数据
     * @return
     */
    private List<Map<String, Object>> getWagesJson(List<WagesModel> staffModel, Map<String, String> staffModelFieldMap) {
        List<Map<String, Object>> beans = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(staffModel)) {
            for (WagesModel model : staffModel) {
                Map<String, Object> bean = new HashMap<>();
                bean.put("name", model.getName());
                bean.put("modelId", model.getId());
                model.getWagesModelFieldList().forEach(wagesModelField -> {
                    if (ToolUtil.isBlank(wagesModelField.getFormula())) {
                        wagesModelField.setMoneyValue(staffModelFieldMap.get(wagesModelField.getFieldKey()));
                    } else {
                        wagesModelField.setMoneyValue(String.valueOf(ReflexUtil.convertToCode(wagesModelField.getFormula(), staffModelFieldMap)));
                    }
                });
                bean.put("childFields", model.getWagesModelFieldList());
                beans.add(bean);
            }
        }
        beans.addAll(getSocialSecurityFundWagesJson(staffModelFieldMap));
        beans.addAll(getTaxRateWagesJson(staffModelFieldMap));
        beans.addAll(getRewardPunishWagesJson(staffModelFieldMap));
        return beans;
    }

    /**
     * 获取社保公积金信息
     *
     * @param staffModelFieldMap 薪资数据
     * @return
     */
    private List<Map<String, Object>> getSocialSecurityFundWagesJson(Map<String, String> staffModelFieldMap) {
        List<Map<String, Object>> beans = new ArrayList<>();
        Map<String, Object> bean = new HashMap<>();
        bean.put("name", "社保公积金");
        List<WagesModelField> childFields = new ArrayList<>();
        WagesModelField childField = new WagesModelField();
        FieldType fieldKeyMation = new FieldType();
        fieldKeyMation.setName("个人缴纳社保");
        childField.setFieldKeyMation(fieldKeyMation);
        childField.setMoneyValue(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE.getKey()));
        childField.setFieldKey(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE.getKey());
        childField.setFieldType(WagesModelFieldType.FIELD.getKey());
        childField.setOrderBy(CommonNumConstants.NUM_ONE);
        childFields.add(childField);

        WagesModelField childField2 = new WagesModelField();
        FieldType fieldKeyMation2 = new FieldType();
        fieldKeyMation2.setName("个人缴纳公积金");
        childField2.setFieldKeyMation(fieldKeyMation2);
        childField2.setMoneyValue(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION.getKey()));
        childField2.setFieldKey(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION.getKey());
        childField2.setFieldType(WagesModelFieldType.FIELD.getKey());
        childField2.setOrderBy(CommonNumConstants.NUM_TWO);
        childFields.add(childField2);

        WagesModelField childField3 = new WagesModelField();
        FieldType fieldKeyMation3 = new FieldType();
        fieldKeyMation3.setName("单位缴纳社保");
        childField3.setFieldKeyMation(fieldKeyMation3);
        childField3.setMoneyValue(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE_COMPANY.getKey()));
        childField3.setFieldKey(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE_COMPANY.getKey());
        childField3.setFieldType(WagesModelFieldType.FIELD.getKey());
        childField3.setOrderBy(CommonNumConstants.NUM_THREE);
        childFields.add(childField3);

        WagesModelField childField4 = new WagesModelField();
        FieldType fieldKeyMation4 = new FieldType();
        fieldKeyMation4.setName("单位缴纳公积金");
        childField4.setFieldKeyMation(fieldKeyMation4);
        childField4.setMoneyValue(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION_COMPANY.getKey()));
        childField4.setFieldKey(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION_COMPANY.getKey());
        childField4.setFieldType(WagesModelFieldType.FIELD.getKey());
        childField4.setOrderBy(CommonNumConstants.NUM_FOUR);
        childFields.add(childField4);

        bean.put("childFields", childFields);
        beans.add(bean);
        return beans;
    }

    /**
     * 获取个人缴税信息
     *
     * @param staffModelFieldMap 薪资数据
     * @return
     */
    private List<Map<String, Object>> getTaxRateWagesJson(Map<String, String> staffModelFieldMap) {
        List<Map<String, Object>> beans = new ArrayList<>();
        Map<String, Object> bean = new HashMap<>();
        bean.put("name", "个人缴税");
        List<WagesModelField> childFields = new ArrayList<>();
        WagesModelField childField = new WagesModelField();
        FieldType fieldKeyMation = new FieldType();
        fieldKeyMation.setName("个人缴纳税额");
        childField.setFieldKeyMation(fieldKeyMation);
        childField.setMoneyValue(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_TAX_RATE_BY_PERSON.getKey()));
        childField.setFieldKey(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_TAX_RATE_BY_PERSON.getKey());
        childField.setFieldType(WagesModelFieldType.FIELD.getKey());
        childField.setOrderBy(CommonNumConstants.NUM_ONE);
        childFields.add(childField);
        bean.put("childFields", childFields);
        beans.add(bean);
        return beans;
    }

    /**
     * 获取奖惩信息
     *
     * @param staffModelFieldMap 薪资数据
     * @return
     */
    private List<Map<String, Object>> getRewardPunishWagesJson(Map<String, String> staffModelFieldMap) {
        List<Map<String, Object>> beans = new ArrayList<>();
        Map<String, Object> bean = new HashMap<>();
        bean.put("name", "奖惩信息");
        List<WagesModelField> childFields = new ArrayList<>();
        WagesModelField childField = new WagesModelField();
        FieldType fieldKeyMation = new FieldType();
        fieldKeyMation.setName("奖惩金额");
        childField.setFieldKeyMation(fieldKeyMation);
        childField.setMoneyValue(staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_REWARD_PUNISH_MONEY.getKey()));
        childField.setFieldKey(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_REWARD_PUNISH_MONEY.getKey());
        childField.setFieldType(WagesModelFieldType.FIELD.getKey());
        childField.setOrderBy(CommonNumConstants.NUM_ONE);
        childFields.add(childField);
        bean.put("childFields", childFields);
        beans.add(bean);
        return beans;
    }

    /**
     * 获取减去个人所得税之后的钱
     *
     * @param monthlyStandardRealMoney 未缴税的金额
     * @param taxRate                  缴税信息
     * @param companyId                公司id
     * @param lastMonthDate            上个月的年月
     * @param staffModelFieldMap       薪资数据
     * @return
     */
    private String calcTaxRate(String monthlyStandardRealMoney, Map<String, List<Map<String, Object>>> taxRate, String companyId, String lastMonthDate,
                               Map<String, String> staffModelFieldMap) {
        List<Map<String, Object>> companyTaxRate = taxRate.get(companyId);
        if (CollectionUtil.isEmpty(companyTaxRate)) {
            return monthlyStandardRealMoney;
        }
        String finalMonthlyStandardRealMoney = monthlyStandardRealMoney;
        List<Map<String, Object>> staffTaxRate = companyTaxRate.stream().filter(bean -> {
            String intervalStr = String.format(Locale.ROOT, "[%s, %s)", bean.get("minMoney").toString(), bean.get("maxMoney").toString());
            return IntervalUtil.isInTheInterval(finalMonthlyStandardRealMoney, intervalStr);
        }).collect(Collectors.toList());
        // 缴纳的税额
        String taxRateMoney = "0";
        if (staffTaxRate != null && !staffTaxRate.isEmpty()) {
            taxRateMoney = CalculationUtil.multiply(monthlyStandardRealMoney,
                CalculationUtil.divide(getMonthRate(lastMonthDate, staffTaxRate), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_FOUR);
        }
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_TAX_RATE_BY_PERSON.getKey(), taxRateMoney);
        monthlyStandardRealMoney = CalculationUtil.subtract(monthlyStandardRealMoney,
            taxRateMoney, CommonNumConstants.NUM_FOUR);
        return monthlyStandardRealMoney;
    }

    /**
     * 计算员工的奖惩金额
     *
     * @param staffId      员工ID
     * @param accountMonth 年月（如：2025-01）
     * @return 奖惩金额（正数为奖励，负数为惩罚）
     */
    private String calcRewardPunishMoney(String staffId, String accountMonth) {
        List<RewardPunish> rewardPunishList = rewardPunishService.queryUnAccountedByStaffIdAndMonth(staffId, accountMonth);
        if (CollectionUtil.isEmpty(rewardPunishList)) {
            return "0";
        }
        String totalRewardPunishMoney = "0";
        List<String> rewardPunishIds = new ArrayList<>();
        for (RewardPunish rp : rewardPunishList) {
            if (StrUtil.isNotBlank(rp.getPrice())) {
                // 根据 rewardPunishType 判断是奖励还是惩罚
                // price 字段都是正数，需要通过 rewardPunishType 来区分
                if (rp.getRewardPunishType() != null) {
                    if (rp.getRewardPunishType().equals(com.skyeye.reward.classenum.RewardPunishType.REWARD.getKey())) {
                        // 奖励：增加金额（正数）
                        totalRewardPunishMoney = CalculationUtil.add(totalRewardPunishMoney, rp.getPrice(), CommonNumConstants.NUM_TWO);
                    } else if (rp.getRewardPunishType().equals(com.skyeye.reward.classenum.RewardPunishType.PUNISH.getKey())) {
                        // 惩罚：减少金额（负数）
                        totalRewardPunishMoney = CalculationUtil.subtract(totalRewardPunishMoney, rp.getPrice(), CommonNumConstants.NUM_TWO);
                    }
                }
                // 收集需要标记的奖惩记录ID
                rewardPunishIds.add(rp.getId());
            }
        }
        // 批量标记为已计入薪资
        if (CollectionUtil.isNotEmpty(rewardPunishIds)) {
            rewardPunishService.markAsAccountedBatch(rewardPunishIds, accountMonth);
        }
        return totalRewardPunishMoney;
    }

    /**
     * 获取上个月的税率
     *
     * @param lastMonthDate 上个月的年月
     * @param staffTaxRate  员工的个人缴纳税率
     * @return
     */
    private String getMonthRate(String lastMonthDate, List<Map<String, Object>> staffTaxRate) {
        Map<String, Object> taxRate = staffTaxRate.get(0);
        String month = lastMonthDate.split("-")[1];
        String key = "";
        switch (month) {
            case "01":
                key = "janRate";
                break;
            case "02":
                key = "febRate";
                break;
            case "03":
                key = "marRate";
                break;
            case "04":
                key = "aprRate";
                break;
            case "05":
                key = "mayRate";
                break;
            case "06":
                key = "junRate";
                break;
            case "07":
                key = "julRate";
                break;
            case "08":
                key = "augRate";
                break;
            case "09":
                key = "septRate";
                break;
            case "10":
                key = "octRate";
                break;
            case "11":
                key = "novRate";
                break;
            case "12":
                key = "decRate";
                break;
        }
        return taxRate.get(key).toString();
    }

    /**
     * 计算员工的考勤相关应扣的薪资
     *
     * @param staffId                  员工id
     * @param systemFoundationSettings 系统基础信息
     * @param workTime                 考勤制度
     * @param staffModelFieldMap       该员工拥有的所有薪资要素字段以及对应的值
     * @param lastMonthDate            上个月的年月
     * @return 如果返回值为负数，则说明加班和销假>请假以及异常考勤
     */
    private String calcStaffCheckWork(String staffId, Map<String, Object> systemFoundationSettings, List<CheckWorkTime> workTime,
                                      Map<String, String> staffModelFieldMap, String lastMonthDate, String tenantId) {
        // 1.获取该员工拥有的考勤班次id集合
        List<Map<String, Object>> staffTimeIdMation = sysEveUserStaffDao.queryStaffCheckWorkTimeRelationByStaffId(staffId, tenantId);
        List<String> userTimeIds = staffTimeIdMation.stream()
            .map(p -> p.get("timeId").toString()).collect(Collectors.toList());
        List<CheckWorkTime> staffWorkTime = workTime.stream()
            .filter(bean -> userTimeIds.contains(bean.getId()))
            .collect(Collectors.toList());
        // 2.获取上个月指定员工的所有考勤记录信息
        List<Map<String, Object>> lastMonthCheckWork = wagesStaffMationDao.queryLastMonthCheckWork(staffId, lastMonthDate, tenantId);
        // 3.设置应出勤的班次以及小时
        Map<String, Object> monthBe = wagesStaffMationService.setLastMonthBe(staffWorkTime, lastMonthDate);
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_NUM.getKey(),
            monthBe.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_NUM.getKey()).toString());
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey(),
            monthBe.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey()).toString());
        // 上个月迟到的分钟集合
        List<String> lateMinute = new ArrayList<>();
        // 上个月早退的分钟集合
        List<String> earlyMinute = new ArrayList<>();
        // 4.设置员工考勤的对应数据信息
        setStaffCheckWorkMation(lastMonthCheckWork, staffModelFieldMap, lateMinute, earlyMinute, staffWorkTime);
        // 5.计算考勤的扣薪情况
        String checkWorkMoney = calcCheckWorkMation(lateMinute, earlyMinute, staffModelFieldMap, systemFoundationSettings);
        // 6.计算请假的扣薪情况
        String leaveMoney = calcLeaveTimeMation(systemFoundationSettings, staffId, lastMonthDate, staffModelFieldMap, tenantId);
        // 7.计算销假应退还给员工的薪资
        String cancleLeaveMoney = calcCancleLeaveTimeMation(staffId, lastMonthDate, staffModelFieldMap, tenantId);
        // 计算请假以及异常考勤应结算的钱
        String shouldSubtractMoney = CalculationUtil.add(CommonNumConstants.NUM_TWO, checkWorkMoney, leaveMoney);
        return CalculationUtil.subtract(shouldSubtractMoney, cancleLeaveMoney, CommonNumConstants.NUM_TWO);
    }

    /**
     * 计算销假应退还给员工的薪资
     *
     * @param staffId            员工id
     * @param lastMonthDate      上个月的日期,格式为yyyy-MM
     * @param staffModelFieldMap 该员工拥有的所有薪资要素字段以及对应的值
     * @return
     */
    private String calcCancleLeaveTimeMation(String staffId, String lastMonthDate, Map<String, String> staffModelFieldMap, String tenantId) {
        // 获取上个月指定员工的所有审批通过销假记录信息
        List<Map<String, Object>> cancleLeaveTime = wagesStaffMationDao.queryLastMonthCancleLeaveTime(staffId, lastMonthDate,
            FlowableStateEnum.PASS.getKey(), FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
        // 上个月应出勤的小时数
        String lastMonthBeHour = staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey());
        // 判断是否为0
        if (Double.parseDouble(lastMonthBeHour) == CommonNumConstants.NUM_ZERO) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        // 获取月标准小时薪资计算
        String hourWages = CalculationUtil.divide(
            staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_SALARY.getKey()),
            lastMonthBeHour, CommonNumConstants.NUM_TWO);
        // 销假要退还给员工的钱
        String cancleLeaveMoney = "0";
        for (Map<String, Object> bean : cancleLeaveTime) {
            // 优先使用审批通过的 cancelHour（含跨午夜销假）；无则回退时刻差
            String cancelHourTime;
            Object cancelHourObj = bean.get("cancelHour");
            if (cancelHourObj != null && StrUtil.isNotBlank(cancelHourObj.toString())) {
                cancelHourTime = cancelHourObj.toString();
            } else if (bean.get("cancelStartTime") != null && bean.get("cancelEndTime") != null) {
                cancelHourTime = resolveCancelHourTime(bean);
            } else {
                cancelHourTime = "0";
            }
            cancleLeaveMoney = CalculationUtil.add(
                cancleLeaveMoney,
                CalculationUtil.multiply(cancelHourTime, hourWages, CommonNumConstants.NUM_FOUR), CommonNumConstants.NUM_FOUR);
        }
        return cancleLeaveMoney;
    }

    /**
     * 销假时长（小时）：优先 cancelHour；否则按班次与 cancelDay 重算（支持跨午夜）；最后回退时刻差
     */
    private String resolveCancelHourTime(Map<String, Object> bean) {
        Object timeIdObj = bean.get("timeId");
        Object cancelDayObj = bean.get("cancelDay");
        if (timeIdObj != null && cancelDayObj != null && StrUtil.isNotBlank(timeIdObj.toString())) {
            CheckWorkTime workTime = checkWorkTimeService.selectById(timeIdObj.toString());
            if (workTime != null) {
                String cancelDay = cancelDayObj.toString();
                if (cancelDay.length() >= 10) {
                    cancelDay = cancelDay.substring(0, 10);
                }
                return CheckWorkHourCalcUtil.calcCancelHour(cancelDay,
                    bean.get("cancelStartTime").toString(),
                    bean.get("cancelEndTime").toString(),
                    workTime);
            }
        }
        String cancelMinute = DateUtil.getDistanceMinuteByHMS(
            bean.get("cancelStartTime").toString(), bean.get("cancelEndTime").toString());
        return CalculationUtil.divide(cancelMinute, "60", CommonNumConstants.NUM_TWO);
    }

    /**
     * 计算考勤的扣薪情况
     *
     * @param lateMinute               上个月迟到的分钟集合
     * @param earlyMinute              上个月早退的分钟集合
     * @param staffModelFieldMap       该员工拥有的所有薪资要素字段以及对应的值
     * @param systemFoundationSettings 系统基础信息
     * @return
     */
    private String calcCheckWorkMation(List<String> lateMinute, List<String> earlyMinute, Map<String, String> staffModelFieldMap,
                                       Map<String, Object> systemFoundationSettings) {
        // 异常考勤制度管理信息
        List<Map<String, Object>> abnormalMation = JSONUtil.toList(systemFoundationSettings.get("abnormalMation").toString(), null);
        // 1.早退
        List<Map<String, Object>> leaveearly = abnormalMation.stream()
            .filter(bean -> AbnormalCheckworkType.ABNORMAL_LEAVEEARLY.getKey().equals(bean.get("abnormalType").toString()))
            .collect(Collectors.toList());
        String leaveearlyMoney = calcMoney(earlyMinute, leaveearly, staffModelFieldMap, WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_EARLY_NUM.getKey());
        // 2.迟到
        List<Map<String, Object>> late = abnormalMation.stream()
            .filter(bean -> AbnormalCheckworkType.ABNORMAL_LATE.getKey().equals(bean.get("abnormalType").toString()))
            .collect(Collectors.toList());
        String lateMoney = calcMoney(lateMinute, late, staffModelFieldMap, WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_LATE_NUM.getKey());
        // 3.旷工
        List<Map<String, Object>> miner = abnormalMation.stream()
            .filter(bean -> AbnormalCheckworkType.ABNORMAL_MINER.getKey().equals(bean.get("abnormalType").toString()))
            .collect(Collectors.toList());
        String minerMoney = calcMinerMoney(miner, staffModelFieldMap);
        return CalculationUtil.add(CommonNumConstants.NUM_FOUR, leaveearlyMoney, lateMoney, minerMoney);
    }

    /**
     * 计算异常考勤的扣薪金额
     *
     * @param minute             异常的时间
     * @param abnormalTypes      异常类型
     * @param staffModelFieldMap 该员工拥有的所有薪资要素字段以及对应的值
     * @param key                异常类型的次数key
     * @return
     */
    private String calcMoney(List<String> minute, List<Map<String, Object>> abnormalTypes, Map<String, String> staffModelFieldMap,
                             String key) {
        if (abnormalTypes != null && !abnormalTypes.isEmpty()) {
            Map<String, Object> abnormalType = abnormalTypes.get(0);
            // 扣费类型  1.按次扣费  2.按时间扣费
            String abnormal = abnormalType.get("abnormal").toString();
            if ("1".equals(abnormal)) {
                // 次数
                String num = staffModelFieldMap.get(key);
                // 次数*扣款金额，保留四位小数
                return CalculationUtil.multiply(num, abnormalType.get("abnormalMoney").toString(), CommonNumConstants.NUM_FOUR);
            } else if ("2".equals(abnormal)) {
                // 与默认的一样(按时间扣费)
            }
        }
        String resultMoney = CommonNumConstants.NUM_ZERO.toString();
        // 上个月应出勤的小时数
        String lastMonthBeHour = staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey());
        // 判断是否为0
        if (Double.parseDouble(lastMonthBeHour) == CommonNumConstants.NUM_ZERO) {
            return resultMoney;
        }
        if (CollectionUtil.isEmpty(minute)) {
            return resultMoney;
        }
        // 获取月标准小时薪资计算
        String hourWages = CalculationUtil.divide(
            staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_SALARY.getKey()),
            lastMonthBeHour, CommonNumConstants.NUM_FOUR);
        for (String str : minute) {
            resultMoney = CalculationUtil.add(
                resultMoney,
                CalculationUtil.multiply(CommonNumConstants.NUM_TWO, CalculationUtil.divide(str, "60", CommonNumConstants.NUM_TWO), hourWages), CommonNumConstants.NUM_FOUR);
        }
        return resultMoney;
    }

    /**
     * 计算异常考勤旷工的扣薪金额
     *
     * @param abnormalTypes      异常类型
     * @param staffModelFieldMap 该员工拥有的所有薪资要素字段以及对应的值
     * @return
     */
    private String calcMinerMoney(List<Map<String, Object>> abnormalTypes, Map<String, String> staffModelFieldMap) {
        if (abnormalTypes != null && !abnormalTypes.isEmpty()) {
            Map<String, Object> abnormalType = abnormalTypes.get(0);
            // 扣费类型  1.按次扣费  2.按时间扣费
            String abnormal = abnormalType.get("abnormal").toString();
            if ("1".equals(abnormal)) {
                // 缺勤次数
                String num = staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_DUTY_NUM.getKey());
                // 次数*扣款金额，保留四位小数
                return CalculationUtil.multiply(num, abnormalType.get("abnormalMoney").toString(), CommonNumConstants.NUM_FOUR);
            } else if ("2".equals(abnormal)) {
                // 与默认的一样(按时间扣费)
            }
        }
        // 上个月应出勤的小时数
        String lastMonthBeHour = staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey());
        // 判断是否为0
        if (Double.parseDouble(lastMonthBeHour) == CommonNumConstants.NUM_ZERO) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        // 获取月标准小时薪资计算
        String hourWages = CalculationUtil.divide(
            staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_SALARY.getKey()),
            lastMonthBeHour, CommonNumConstants.NUM_FOUR);
        // 实际缺勤多少小时[应出勤(小时) - 应实际出勤(小时)]
        String dutyHour = CalculationUtil.subtract(
            lastMonthBeHour,
            staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_REAL_HOUR.getKey()), CommonNumConstants.NUM_FOUR);
        return CalculationUtil.multiply(dutyHour, hourWages, CommonNumConstants.NUM_FOUR);
    }

    /**
     * 计算请假的扣薪情况
     *
     * @param systemFoundationSettings 系统基础信息
     * @param staffId                  员工id
     * @param lastMonthDate            上个月的年月
     * @param staffModelFieldMap       该员工拥有的所有薪资要素字段以及对应的值
     * @return 请假的扣薪金额
     */
    private String calcLeaveTimeMation(Map<String, Object> systemFoundationSettings, String staffId, String lastMonthDate, Map<String, String> staffModelFieldMap,
                                       String tenantId) {
        // 获取上个月指定员工的所有审批通过请假记录信息
        List<LeaveTimeSlot> leaveTime = leaveTimeSlotService.queryLastMonthLeaveTime(staffId, lastMonthDate,
            FlowableStateEnum.PASS.getKey(), FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
        // 企业假期类型以及扣薪信息
        List<Map<String, Object>> holidaysTypeJson = JSONUtil.toList(systemFoundationSettings.get("holidaysTypeJson").toString(), null);
        Map<String, List<LeaveTimeSlot>> leaveTimeGroupType = leaveTime.stream()
            .collect(Collectors.groupingBy(map -> map.getLeaveType()));
        // 上个月应出勤的小时数
        String lastMonthBeHour = staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_HOUR.getKey());
        // 判断是否为0
        if (Double.parseDouble(lastMonthBeHour) == CommonNumConstants.NUM_ZERO) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        // 获取月标准小时薪资计算
        String hourWages = CalculationUtil.divide(
            staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_STANDARD_SALARY.getKey()),
            lastMonthBeHour, CommonNumConstants.NUM_TWO);
        // 计算请假的小时以及请假扣的钱
        String allLeaveHourTime = "0";
        String allLeaveHourMoney = "0";
        for (Map.Entry<String, List<LeaveTimeSlot>> entry : leaveTimeGroupType.entrySet()) {
            String leaveType = entry.getKey();
            List<Map<String, Object>> holidays = holidaysTypeJson.stream().filter(bean -> leaveType.equals(bean.get("holidayNo").toString())).collect(Collectors.toList());
            for (LeaveTimeSlot bean : entry.getValue()) {
                // selectById 含 checkWorkTimeWeekList，供跨天请假逐日工作日过滤
                CheckWorkTime workTime = checkWorkTimeService.selectById(bean.getTimeId());
                if (workTime == null) {
                    continue;
                }
                long totalMinutes = calcLeaveMinutesInMonth(bean, lastMonthDate, workTime);
                if (totalMinutes <= 0) {
                    continue;
                }
                String leaveHourTime = CalculationUtil.divide(String.valueOf(totalMinutes), "60", CommonNumConstants.NUM_TWO);
                allLeaveHourTime = CalculationUtil.add(allLeaveHourTime, leaveHourTime, CommonNumConstants.NUM_TWO);
                if (holidays != null && !holidays.isEmpty()) {
                    // 该扣薪规则存在，获取扣钱百分比
                    String percentage = CalculationUtil.divide(holidays.get(0).get("offPercentageMoney").toString(), "100", CommonNumConstants.NUM_FOUR);
                    allLeaveHourMoney = CalculationUtil.add(
                        allLeaveHourMoney,
                        CalculationUtil.multiply(CommonNumConstants.NUM_FOUR, leaveHourTime, hourWages, percentage), CommonNumConstants.NUM_FOUR);
                } else {
                    allLeaveHourMoney = CalculationUtil.add(
                        allLeaveHourMoney,
                        CalculationUtil.multiply(leaveHourTime, hourWages, CommonNumConstants.NUM_FOUR), CommonNumConstants.NUM_FOUR);
                }
            }
        }
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_HOLIDAY_HOUR.getKey(), String.valueOf(allLeaveHourTime));
        return allLeaveHourMoney;
    }

    /**
     * 计算请假时间段在指定月份内与工作时间的交集分钟数（复用考勤 CheckWorkHourCalcUtil，支持跨天班次）
     */
    private long calcLeaveMinutesInMonth(LeaveTimeSlot slot, String lastMonthDate, CheckWorkTime workTime) {
        try {
            LocalDateTime leaveStart = DateUtil.parseLeaveDateTime(slot.getLeaveStartTime());
            LocalDateTime leaveEnd = DateUtil.parseLeaveDateTime(slot.getLeaveEndTime());
            if (leaveStart == null || leaveEnd == null) {
                return 0;
            }
            YearMonth ym = YearMonth.parse(lastMonthDate);
            LocalDate monthFirst = ym.atDay(1);
            LocalDate monthLast = ym.atEndOfMonth();
            LocalDateTime rangeStart = leaveStart.toLocalDate().isBefore(monthFirst) ? monthFirst.atStartOfDay() : leaveStart;
            LocalDateTime rangeEnd = leaveEnd.toLocalDate().isAfter(monthLast) ? monthLast.atTime(23, 59, 59) : leaveEnd;
            if (!rangeStart.isBefore(rangeEnd)) {
                return 0;
            }
            // 与 adm-checkwork 请假工时同一算法，跨天班次按逐日交集 + 午休扣除
            return CheckWorkHourCalcUtil.calcLeaveMinutesInRange(rangeStart, rangeEnd, workTime);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 设置员工考勤的对应数据信息
     *
     * @param lastMonthCheckWork 上个月指定员工的所有考勤记录信息
     * @param staffModelFieldMap 员工拥有的所有薪资要素字段以及对应的值
     * @param lateMinute         上个月迟到的分钟集合
     * @param earlyMinute        上个月早退的分钟集合
     * @param staffWorkTime      该员工拥有的考勤班次集合
     */
    private void setStaffCheckWorkMation(List<Map<String, Object>> lastMonthCheckWork, Map<String, String> staffModelFieldMap,
                                         List<String> lateMinute, List<String> earlyMinute, List<CheckWorkTime> staffWorkTime) {
        // 全勤以及工时不足的都算为实际出勤
        int lastMonthRealNum = 0;
        String lastMonthRealHour = "0";
        int lastMonthLateNum = 0;
        int lastMonthEarlyNum = 0;
        String lastMonthBeRealHour = "0";
        for (Map<String, Object> bean : lastMonthCheckWork) {
            String state = bean.get("state").toString();
            // 能匹配到，说明不是加班的打卡
            List<CheckWorkTime> workTimes = staffWorkTime.stream()
                .filter(item -> bean.get("timeId").toString().equals(item.getId()))
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(workTimes)) {
                continue;
            }
            CheckWorkTime workTime = workTimes.get(0);
            String startTime = DateUtil.formatDate(workTime.getStartTime());
            String endTime = DateUtil.formatDate(workTime.getEndTime());
            boolean crossDay = CheckWorkTimePeriodUtil.resolveShiftCrossDay(
                startTime, endTime, workTime.getIsNextDay());
            // 全勤以及工时不足的都算为实际出勤
            if ("1".equals(state) || "3".equals(state)) {
                lastMonthRealNum++;
                String time = String.valueOf(CheckWorkTimePeriodUtil.getPunchDurationMinutes(
                    bean.get("clockIn").toString(), bean.get("clockOut").toString()));
                lastMonthRealHour = CalculationUtil.add(lastMonthRealHour, time, CommonNumConstants.NUM_TWO);

                lastMonthBeRealHour = CalculationUtil.add(lastMonthBeRealHour,
                    String.valueOf(CheckWorkHourCalcUtil.calcStandardWorkMinutes(workTime)), CommonNumConstants.NUM_TWO);
            }
            // 迟到
            if ("2".equals(bean.get("clockInState").toString())) {
                lastMonthLateNum++;
                lateMinute.add(String.valueOf(CheckWorkTimePeriodUtil.getLateMinutes(
                    bean.get("clockIn").toString(), startTime, crossDay)));
            }
            // 早退
            if ("2".equals(bean.get("clockOutState").toString())) {
                lastMonthEarlyNum++;
                earlyMinute.add(String.valueOf(CheckWorkTimePeriodUtil.getEarlyLeaveMinutes(
                    bean.get("clockOut").toString(), startTime, endTime, crossDay)));
            }
        }
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_REAL_NUM.getKey(), String.valueOf(lastMonthRealNum));
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_REAL_HOUR.getKey(), CalculationUtil.divide(lastMonthRealHour, "60", CommonNumConstants.NUM_TWO));
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_REAL_HOUR.getKey(), CalculationUtil.divide(lastMonthBeRealHour, "60", CommonNumConstants.NUM_TWO));
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_LATE_NUM.getKey(), String.valueOf(lastMonthLateNum));
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_EARLY_NUM.getKey(), String.valueOf(lastMonthEarlyNum));
        // 应出勤班次
        String lastMonthBeNum = staffModelFieldMap.get(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_BE_NUM.getKey());
        // 缺勤的次数
        staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.LAST_MONTH_DUTY_NUM.getKey(),
            CalculationUtil.subtract(lastMonthBeNum, String.valueOf(lastMonthRealNum), CommonNumConstants.NUM_TWO));
    }

    /**
     * 将员工拥有的所有薪资要素字段以及对应的值转换成map
     *
     * @param staffModelField 该员工应该缴纳的社保公积金的金额
     * @return
     */
    private Map<String, String> convert2Map(List<Map<String, Object>> staffModelField) {
        Map<String, String> staffModelFieldMap = staffModelField.stream()
            .collect(Collectors.toMap(bean -> bean.get("fieldKey").toString(), bean -> bean.get("amountMoney").toString()));
        return staffModelFieldMap;
    }

    /**
     * 获取该员工应该缴纳的社保公积金的金额
     *
     * @param companyId          企业id
     * @param departmentId       部门id
     * @param staffId            员工id
     * @param socialSecurityFund 所有启动中的社保公积金适用对象关系以及社保公积金参数信息
     * @param staffModelFieldMap 薪资数据
     * @return
     */
    private String getSocialSecurityFundMoney(String companyId, String departmentId, String staffId, List<SocialSecurityFund> socialSecurityFund,
                                              Map<String, String> staffModelFieldMap) {
        // 根据公司id，部门id，员工id找到适用该员工的社保公积金缴纳信息
        List<SocialSecurityFund> applyThisUserStaffSocialSecurityFund = socialSecurityFund.stream().filter(bean -> {
            if (CollectionUtil.isEmpty(bean.getApplicableObjectsList())) {
                return false;
            }
            List<String> objectIds = bean.getApplicableObjectsList().stream().filter(item -> ObjectUtil.isNotEmpty(item)).map(ApplicableObjects::getObjectId).collect(Collectors.toList());
            return (objectIds.indexOf(companyId) > -1 || objectIds.indexOf(departmentId) > -1
                || objectIds.indexOf(staffId) > -1);
        }).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(applyThisUserStaffSocialSecurityFund)) {
            // 因为根据公司id，部门id，员工id会找到多个信息，所以筛选值sortNo最大那个社保公积金
            SocialSecurityFund staffSocialSecurityFund = applyThisUserStaffSocialSecurityFund.stream()
                .max(Comparator.comparingInt(SocialSecurityFund::getOrderBy)).get();
            // 养老保险
            String insuranceEndowment = CalculationUtil.multiply(staffSocialSecurityFund.getEndowmentBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getEndowmentPerson(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            // 失业保险
            String insuranceUnemployment = CalculationUtil.multiply(staffSocialSecurityFund.getUnemploymentBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getUnemploymentPerson(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            // 工伤保险
            String insuranceEmployment = CalculationUtil.multiply(staffSocialSecurityFund.getEmploymentBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getEmploymentPerson(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            // 生育保险
            String insuranceMaternity = CalculationUtil.multiply(staffSocialSecurityFund.getMaternityBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getMaternityPerson(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            // 医疗保险
            String insuranceMedical = CalculationUtil.multiply(staffSocialSecurityFund.getMedicalBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getMedicalPerson(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            String insurance = CalculationUtil.add(CommonNumConstants.NUM_TWO, insuranceEndowment, insuranceUnemployment, insuranceEmployment, insuranceMaternity,
                insuranceMedical);
            staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE.getKey(), insurance);
            // 单位社保
            String insuranceCompany = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                CalculationUtil.multiply(staffSocialSecurityFund.getEndowmentBase(),
                    CalculationUtil.divide(staffSocialSecurityFund.getEndowmentCompany(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO),
                CalculationUtil.multiply(staffSocialSecurityFund.getUnemploymentBase(),
                    CalculationUtil.divide(staffSocialSecurityFund.getUnemploymentCompany(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO),
                CalculationUtil.multiply(staffSocialSecurityFund.getEmploymentBase(),
                    CalculationUtil.divide(staffSocialSecurityFund.getEmploymentCompany(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO),
                CalculationUtil.multiply(staffSocialSecurityFund.getMaternityBase(),
                    CalculationUtil.divide(staffSocialSecurityFund.getMaternityCompany(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO),
                CalculationUtil.multiply(staffSocialSecurityFund.getMedicalBase(),
                    CalculationUtil.divide(staffSocialSecurityFund.getMedicalCompany(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO));
            staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_INSURANCE_COMPANY.getKey(), insuranceCompany);
            // 公积金
            String accumulation = CalculationUtil.multiply(staffSocialSecurityFund.getAccumulationBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getAccumulationPersonScale(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION.getKey(), accumulation);
            String accumulationCompany = CalculationUtil.multiply(staffSocialSecurityFund.getAccumulationBase(),
                CalculationUtil.divide(staffSocialSecurityFund.getAccumulationCompanyScale(), "100", CommonNumConstants.NUM_TWO), CommonNumConstants.NUM_TWO);
            staffModelFieldMap.put(WagesConstant.DEFAULT_WAGES_FIELD_TYPE.MONTHLY_SOCIAL_SECURITY_FUND_ACCUMULATION_COMPANY.getKey(), accumulationCompany);
            String result = CalculationUtil.add(CommonNumConstants.NUM_TWO, insurance, accumulation, staffSocialSecurityFund.getInsTotalSeriouslyIllIndividual());
            return result;
        }
        return "0";
    }

    /**
     * 获取该员工拥有的所有薪资要素字段以及对应的值
     *
     * @param modelIds 薪资模板id
     * @param staffId  员工id
     * @return
     */
    private List<Map<String, Object>> getUserStaffWagesModelField(List<String> modelIds, String staffId, String tenantId) {
        if (modelIds == null || modelIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 获取薪资要素字段以及对应的值
        List<Map<String, Object>> modelField = wagesModelFieldDao.queryWagesModelFieldByModelIdsAndStaffId(modelIds, staffId, null, tenantId);
        return modelField;
    }

    /**
     * 获取所有公司个人所得税缴纳比例
     *
     * @return
     */
    private Map<String, List<Map<String, Object>>> getTaxRate(String tenantId) {
        List<Map<String, Object>> companyTaxRate = companyTaxRateDao.queryAllCompanyTaxRate(tenantId);
        return companyTaxRate.stream()
            .collect(Collectors.groupingBy(map -> map.get("companyId").toString()));
    }

    /**
     * 加入到redis缓存
     *
     * @param staffIds 员工ids
     */
    private void setToRedis(List<String> staffIds, String tenantId) {
        // 默认存储时间为六个小时
        String redisKey = getRedisKey(tenantId);
        jedisClient.set(redisKey, JSONUtil.toJsonStr(staffIds), 60 * 60 * 6);
    }

    /**
     * 移除指定的员工id存储在redis的缓存
     *
     * @param staffId 员工id
     */
    private void removeStaffIdInStatisticsRedisMation(String staffId, String tenantId) {
        List<String> staffIds = getStaffIdsFromRedis(tenantId);
        staffIds = staffIds.stream().filter(str -> !staffId.equals(str)).collect(Collectors.toList());
        setToRedis(staffIds, tenantId);
    }

    /**
     * 添加指定的员工id存储在redis的缓存
     *
     * @param staffId 员工id
     */
    private void addStaffIdInStatisticsRedisMation(String staffId, String tenantId) {
        List<String> staffIds = getStaffIdsFromRedis(tenantId);
        staffIds.add(staffId);
        setToRedis(staffIds, tenantId);
    }

    /**
     * 判断该员工的薪资信息是否在处理中
     *
     * @param staffId 员工id
     * @return true：处理中，false：未在处理
     */
    private boolean isInStatisticsRedisMation(String staffId, String tenantId) {
        List<String> staffIds = getStaffIdsFromRedis(tenantId);
        return staffIds.contains(staffId);
    }

    private void deleteStatisticsRedisMation(String tenantId) {
        String redisKey = getRedisKey(tenantId);
        jedisClient.del(redisKey);
    }

    private List<String> getStaffIdsFromRedis(String tenantId) {
        List<String> staffIds = new ArrayList<>();
        String redisKey = getRedisKey(tenantId);
        if (jedisClient.exists(redisKey)) {
            staffIds = JSONUtil.toList(JSONUtil.parseArray(jedisClient.get(redisKey)), null);
        }
        return staffIds;
    }

    /**
     * 获取Redis的键，根据租户ID区分
     *
     * @param tenantId 租户ID
     * @return Redis的键
     */
    private String getRedisKey(String tenantId) {
        if (!tenantEnable) {
            return IN_STATISTICS_STAFF_REDIS_KEY;
        }
        return IN_STATISTICS_STAFF_REDIS_KEY + ":" + tenantId;
    }

}
