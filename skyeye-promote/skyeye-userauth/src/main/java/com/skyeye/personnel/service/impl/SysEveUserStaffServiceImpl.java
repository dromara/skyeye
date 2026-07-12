/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

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
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.CompanyDepartmentService;
import com.skyeye.organization.service.CompanyJobScoreService;
import com.skyeye.organization.service.CompanyJobService;
import com.skyeye.organization.service.CompanyMationService;
import com.skyeye.personnel.classenum.StaffWagesStateEnum;
import com.skyeye.personnel.classenum.UserIsTermOfValidity;
import com.skyeye.personnel.classenum.UserLockState;
import com.skyeye.personnel.dao.SysEveUserStaffDao;
import com.skyeye.personnel.entity.SysEveUser;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.entity.SysEveUserStaffQuery;
import com.skyeye.personnel.service.SysEveUserService;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.skyeye.personnel.service.SysEveUserStaffTimeService;
import com.skyeye.rest.wages.service.IWagesService;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.service.TenantUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveUserStaffServiceImpl
 * @Description: 员工管理服务类--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工管理", groupName = "员工管理", tenant = TenantEnum.NO_ISOLATION)
public class SysEveUserStaffServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserStaffDao, SysEveUserStaff> implements SysEveUserStaffService {

    @Autowired
    private SysEveUserService sysEveUserService;

    @Value("${skyeye.jobNumberPrefix}")
    private String jobNumberPrefix;

    @Autowired
    private CompanyMationService companyMationService;

    @Autowired
    private CompanyDepartmentService companyDepartmentService;

    @Autowired
    private CompanyJobService companyJobService;

    @Autowired
    private CompanyJobScoreService companyJobScoreService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private SysEveUserStaffTimeService sysEveUserStaffTimeService;

    @Autowired
    private IWagesService iWagesService;

    @Override
    public void querySysUserStaffList(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            // 单租户模式下
            super.queryPageList(inputObject, outputObject);
        } else {
            SysEveUserStaffQuery sysEveUserStaffQuery = inputObject.getParams(SysEveUserStaffQuery.class);
            Page page = PageHelper.startPage(sysEveUserStaffQuery.getPage(), sysEveUserStaffQuery.getLimit());
            MPJLambdaWrapper<SysEveUserStaff> wrapper = JoinWrappers.lambda("userStaff", SysEveUserStaff.class);
            if (StrUtil.isNotEmpty(sysEveUserStaffQuery.getTenantId())) {
                wrapper.innerJoin(TenantUser.class, "tru", TenantUser::getStaffId, SysEveUserStaff::getId);
                // 要适配人事下的员工管理
                wrapper.eq("tru." + CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
            }
            if (sysEveUserStaffQuery.getDesignWages() != null) {
                wrapper.eq(TenantUser::getDesignWages, sysEveUserStaffQuery.getDesignWages());
            }
            if (StrUtil.isNotEmpty(sysEveUserStaffQuery.getType())) {
                // 员工类型，参考#UserStaffType
                wrapper.eq(TenantUser::getType, sysEveUserStaffQuery.getType());
            }
            if (StrUtil.isNotEmpty(sysEveUserStaffQuery.getKeyword())) {
                wrapper.and(item -> {
                    item.like(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserName), sysEveUserStaffQuery.getKeyword());
                    if (StrUtil.isNotEmpty(sysEveUserStaffQuery.getTenantId())) {
                        item.or().like("tru.job_number ", sysEveUserStaffQuery.getKeyword());
                    }
                });
            }
            wrapper.orderByDesc(MybatisPlusUtil.toColumns(SysEveUserStaff::getCreateTime));
            List<SysEveUserStaff> sysEveUserStaffList = skyeyeBaseMapper.selectJoinList(SysEveUserStaff.class, wrapper);
            if (StrUtil.isNotEmpty(sysEveUserStaffQuery.getTenantId())) {
                tenantUserService.setThisTenantUserToDefault(sysEveUserStaffList);
            }
            List<Map<String, Object>> userList = sysEveUserStaffList.stream().map(s -> BeanUtil.beanToMap(s)).collect(Collectors.toList());
            setUserStaffCommonInfo(userList);
            outputObject.setBeans(userList);
            outputObject.settotal(page.getTotal());
        }
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<SysEveUserStaff> wrapper) {
        SysEveUserStaffQuery sysEveUserStaffQuery = inputObject.getParams(SysEveUserStaffQuery.class);
        if (sysEveUserStaffQuery.getDesignWages() != null) {
            wrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getDesignWages), sysEveUserStaffQuery.getDesignWages());
        }
        if (sysEveUserStaffQuery.getBindAccount() == WhetherEnum.DISABLE_USING.getKey()) {
            // 未绑定账号
            String userIdKey = MybatisPlusUtil.toColumns(SysEveUserStaff::getUserId);
            wrapper.and(wra -> {
                wra.isNull(userIdKey).or().eq(userIdKey, StrUtil.EMPTY);
            });
        }
        if (StrUtil.isNotEmpty(sysEveUserStaffQuery.getType())) {
            // 员工类型，参考#UserStaffType
            wrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getType), sysEveUserStaffQuery.getType());
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        setUserStaffCommonInfo(beans);
        return beans;
    }

    private void setUserStaffCommonInfo(List<Map<String, Object>> beans) {
        beans.forEach(bean -> {
            bean.put(CommonConstants.NAME, bean.get("jobNumber").toString() + "_" + bean.get("userName").toString());
        });
        // 设置组织信息
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        companyDepartmentService.setNameMationForMap(beans, "departmentId", "departmentName", StrUtil.EMPTY);
        companyJobService.setNameMationForMap(beans, "jobId", "jobName", StrUtil.EMPTY);
        companyJobScoreService.setNameMationForMap(beans, "jobScoreId", "jobScoreName", StrUtil.EMPTY);
    }

    @Override
    public void validatorEntity(SysEveUserStaff entity) {
        super.validatorEntity(entity);
        if (!tenantEnable) {
            // 单租模式下，校验公司、部门、职位、类型是否为空
            if (StrUtil.isEmpty(entity.getCompanyId())) {
                throw new CustomException("请选择公司.");
            }
            if (StrUtil.isEmpty(entity.getDepartmentId())) {
                throw new CustomException("请选择部门.");
            }
            if (StrUtil.isEmpty(entity.getJobId())) {
                throw new CustomException("请选择职位.");
            }
            if (entity.getType() == null) {
                throw new CustomException("请传入员工类型.");
            }
        }
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getPhone), entity.getPhone());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        SysEveUserStaff checkUserStaff = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkUserStaff)) {
            throw new CustomException("该手机号已存在，请更换.");
        }
    }

    @Override
    public void createPrepose(SysEveUserStaff entity) {
        // 设置新的工号
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        String jobNumberKey = MybatisPlusUtil.toColumns(SysEveUserStaff::getJobNumber);
        queryWrapper.select("max(0 + RIGHT(" + jobNumberKey + ", 6)) AS " + jobNumberKey);
        SysEveUserStaff sysEveUserStaff = getOne(queryWrapper, false);
        entity.setJobNumber(jobNumberPrefix + CalculationUtil.add(sysEveUserStaff.getJobNumber(), CommonNumConstants.NUM_ONE.toString(), CommonNumConstants.NUM_ZERO));
        entity.setActWages(CommonNumConstants.NUM_ZERO.toString());
        entity.setAnnualLeave(CommonNumConstants.NUM_ZERO.toString());
        entity.setHolidayNumber(CommonNumConstants.NUM_ZERO.toString());
        entity.setRetiredHolidayNumber(CommonNumConstants.NUM_ZERO.toString());
        entity.setDesignWages(StaffWagesStateEnum.WAIT_DESIGN_WAGES.getKey());
    }

    @Override
    public void createPostpose(SysEveUserStaff entity, String userId) {
        // 新增员工薪资字段信息
        if (!tenantEnable) {
            // 单租户模式才去新增员工薪资字段信息，多租户模式在其他地方保存
            iWagesService.addWagesStaffMationByStaffId(entity.getId());
        }
        // 是否自动注册账号
        if (entity.getWhetherRegister() == WhetherEnum.ENABLE_USING.getKey()) {
            // 自动注册账号
            if (StrUtil.isEmpty(entity.getPassword())) {
                throw new CustomException("请输入密码.");
            }
            SysEveUser sysEveUser = new SysEveUser();
            sysEveUser.setStaffId(entity.getId());
            sysEveUser.setUserCode(entity.getPhone());
            sysEveUser.setPassword(entity.getPassword());
            sysEveUser.setIsTermOfValidity(UserIsTermOfValidity.LONG_TERM.getKey());
            sysEveUserService.addNewUser(entity.getCreateId(), sysEveUser);
        }
    }

    @Override
    public void updatePrepose(SysEveUserStaff entity) {
        SysEveUserStaff oldData = selectById(entity.getId());
        entity.setUserId(oldData.getUserId());
        entity.setQuitTime(oldData.getQuitTime());
        entity.setQuitReason(oldData.getQuitReason());
        entity.setBecomeWorkerTime(oldData.getBecomeWorkerTime());
        entity.setType(oldData.getType());
        entity.setDesignWages(oldData.getDesignWages());
        entity.setActWages(oldData.getActWages());
        entity.setAnnualLeave(oldData.getAnnualLeave());
        entity.setAnnualLeaveStatisTime(oldData.getAnnualLeaveStatisTime());
        entity.setHolidayNumber(oldData.getHolidayNumber());
        entity.setHolidayStatisTime(oldData.getHolidayStatisTime());
        entity.setRetiredHolidayNumber(oldData.getRetiredHolidayNumber());
        entity.setRetiredHolidayStatisTime(oldData.getRetiredHolidayStatisTime());
        entity.setInterviewArrangementId(oldData.getInterviewArrangementId());
    }

    @Override
    public void writePostpose(SysEveUserStaff entity, String userId) {
        super.writePostpose(entity, userId);
        // 员工考勤时间段
        if (!tenantEnable) {
            // 单租户模式才去保存员工考勤时间段信息，多租户模式在其他地方调用
            sysEveUserStaffTimeService.saveUserStaffCheckWorkTime(entity.getTimeIdList(), entity.getId());
        }
        if (StrUtil.isNotBlank(entity.getUserId())) {
            // 删除用户的缓存信息
            iAuthUserService.removeCacheById(entity.getUserId());
        }
    }

    @Override
    @IgnoreTenant
    public SysEveUserStaff selectById(String id) {
        SysEveUserStaff sysEveUserStaff = super.selectById(id);
        if (tenantEnable) {
            // 设置当前默认租户下的用户信息
            sysEveUserStaff = tenantUserService.setThisTenantUserToDefault(sysEveUserStaff);
            if (ObjectUtil.isEmpty(sysEveUserStaff)) {
                return null;
            }
        }
        sysEveUserStaff.setStateName(UserStaffState.getNameByState(sysEveUserStaff.getState()));
        // 员工考勤时间段信息--一已适配多租户
        List<Map<String, Object>> staffTimeMation = sysEveUserStaffTimeService.getStaffCheckWorkTimeByStaffId(id);
        sysEveUserStaff.setTimeList(staffTimeMation);
        // 设置组织信息
        companyMationService.setNameDataMation(sysEveUserStaff, SysEveUserStaff::getCompanyId, StrUtil.EMPTY);
        companyDepartmentService.setNameDataMation(sysEveUserStaff, SysEveUserStaff::getDepartmentId, StrUtil.EMPTY);
        companyJobService.setNameDataMation(sysEveUserStaff, SysEveUserStaff::getJobId, StrUtil.EMPTY);
        companyJobScoreService.setNameDataMation(sysEveUserStaff, SysEveUserStaff::getJobScoreId, StrUtil.EMPTY);
        return sysEveUserStaff;
    }

    /**
     * 员工离职
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSysUserStaffState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("id").toString();
        if (!tenantEnable) {
            // 单租户模式，设置离职信息
            UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, staffId);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getState), UserStaffState.QUIT.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getQuitTime), map.get("quitTime").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getQuitReason), map.get("quitReason").toString());
            update(updateWrapper);

            SysEveUserStaff staffMation = selectById(staffId);
            if (!ToolUtil.isBlank(staffMation.getUserId()) && !tenantEnable) {
                // 锁定帐号
                sysEveUserService.editUserLockState(staffMation.getUserId(), UserLockState.SYS_USER_LOCK_STATE_ISLOCK.getKey());
                // 退出登录
                sysEveUserService.removeLogin(staffMation.getUserId(), true);
            }
        } else {
            // 多租户模式，设置离职信息
            tenantUserService.quit(staffId, map.get("quitTime").toString(), map.get("quitReason").toString());
        }
    }

    /**
     * 修改员工类型
     *
     * @param id   员工id
     * @param type 参考#UserStaffType
     */
    @Override
    public void updateStaffType(String id, Integer type) {
        UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getType), type);
        update(updateWrapper);
    }

    /**
     * 获取当前登录员工的信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySysUserStaffLogin(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getLogParams().get("staffId").toString();
        SysEveUserStaff sysEveUserStaff = selectById(staffId);
        outputObject.setBean(sysEveUserStaff);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 根据用户ids/员工ids获取员工信息集合
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryUserMationList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userIds = params.get("userIds").toString();
        String staffIds = params.get("staffIds").toString();
        // 用户id和员工id只要有一个不为空就进行查询
        List<Map<String, Object>> beans = queryUserMationList(userIds, staffIds);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public List<Map<String, Object>> queryUserMationList(String userIds, String staffIds) {
        List<Map<String, Object>> beans = new ArrayList<>();
        List<SysEveUserStaff> userStaffList = queryUserMationEntityList(userIds, staffIds);
        if (CollectionUtil.isEmpty(userStaffList)) {
            return beans;
        }
        beans = JSONUtil.toList(JSONUtil.toJsonStr(userStaffList), null);
        if (tenantEnable) {
            // 多租户模式，获取当前租户下的用户信息
            userStaffList = tenantUserService.setThisTenantUserToDefault(userStaffList);
            beans = JSONUtil.toList(JSONUtil.toJsonStr(userStaffList), null);
        }
        if (CollectionUtil.isEmpty(beans)) {
            return new ArrayList<>();
        }

        Map<String, String> userId2UserCodeMap = new HashMap<>();
        // 获取用户信息
        List<String> userIdsList = beans.stream().map(bean -> bean.getOrDefault("userId", StrUtil.EMPTY).toString())
            .filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(userIdsList)) {
            List<SysEveUser> userList = sysEveUserService.selectByIds(userIdsList.toArray(new String[]{}));
            userId2UserCodeMap = userList.stream().collect(Collectors.toMap(SysEveUser::getId, SysEveUser::getUserCode));
        }

        // 设置员工信息
        for (Map<String, Object> bean : beans) {
            bean.put("name", bean.getOrDefault("jobNumber", StrUtil.EMPTY).toString() + "_"
                + bean.getOrDefault("userName", StrUtil.EMPTY).toString());
            bean.put("staffId", bean.get("id"));

            String userId = bean.getOrDefault("userId", StrUtil.EMPTY).toString();
            bean.put("id", userId);
            if (StrUtil.isNotEmpty(userId)) {
                bean.put("userCode", userId2UserCodeMap.getOrDefault(userId, StrUtil.EMPTY));
            }

            Integer state = (Integer) bean.getOrDefault("state", CommonNumConstants.NUM_ZERO);
            bean.put("colorClass", UserStaffState.getColorClassByState(state));
        }
        // 设置组织信息
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        companyDepartmentService.setNameMationForMap(beans, "departmentId", "departmentName", StrUtil.EMPTY);
        companyJobService.setNameMationForMap(beans, "jobId", "jobName", StrUtil.EMPTY);
        companyJobScoreService.setNameMationForMap(beans, "jobScoreId", "jobScoreName", StrUtil.EMPTY);
        return beans;
    }

    @Override
    public Map<String, SysEveUserStaff> getUserIdsByStaffIds(List<String> staffIds) {
        if (staffIds == null) {
            return new HashMap<>();
        }
        staffIds = staffIds.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(staffIds)) {
            return new HashMap<>();
        }
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, staffIds);
        List<SysEveUserStaff> userStaffList = list(queryWrapper);
        return userStaffList.stream().collect(Collectors.toMap(SysEveUserStaff::getId, bean -> bean));
    }

    private List<SysEveUserStaff> queryUserMationEntityList(String userIds, String staffIds) {
        if (!ToolUtil.isBlank(userIds) || !ToolUtil.isBlank(staffIds)) {
            QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(userIds)) {
                queryWrapper.in(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserId), Arrays.asList(userIds.split(CommonCharConstants.COMMA_MARK)));
            }

            if (StrUtil.isNotEmpty(staffIds)) {
                queryWrapper.in(CommonConstants.ID, Arrays.asList(staffIds.split(CommonCharConstants.COMMA_MARK)));
            }
            List<SysEveUserStaff> userStaffList = list(queryWrapper);
            return userStaffList;
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSysUserStaffAnnualLeaveById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String quarterYearHour = map.get("quarterYearHour").toString();
        String annualLeaveStatisTime = map.get("annualLeaveStatisTime").toString();

        // 修改员工剩余年假信息
        updateStaffAnnualLeave(staffId, quarterYearHour, annualLeaveStatisTime);
    }

    @Override
    public void updateStaffAnnualLeave(String staffId, String quarterYearHour, String annualLeaveStatisTime) {
        // 修改员工剩余年假信息
        if (!tenantEnable) {
            // 单租户模式
            UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, staffId);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getAnnualLeave), quarterYearHour);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getAnnualLeaveStatisTime), annualLeaveStatisTime);
            update(updateWrapper);
        } else {
            // 多租户模式
            tenantUserService.editUserStaffAnnualLeaveByStaffId(staffId, quarterYearHour, annualLeaveStatisTime);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updateSysUserStaffHolidayNumberById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String holidayNumber = map.get("holidayNumber").toString();
        String holidayStatisTime = map.get("holidayStatisTime").toString();

        // 修改员工的补休池剩余补休信息
        if (!tenantEnable) {
            // 单租户模式
            UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, staffId);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getHolidayNumber), holidayNumber);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getHolidayStatisTime), holidayStatisTime);
            update(updateWrapper);
        } else {
            // 多租户模式
            tenantUserService.editUserStaffHolidayByStaffId(staffId, holidayNumber, holidayStatisTime);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updateSysUserStaffRetiredHolidayNumberById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String retiredHolidayNumber = map.get("retiredHolidayNumber").toString();
        String retiredHolidayStatisTime = map.get("retiredHolidayStatisTime").toString();

        // 修改员工的补休池已休补休信息
        if (!tenantEnable) {
            // 单租户模式
            UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, staffId);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getRetiredHolidayNumber), retiredHolidayNumber);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getRetiredHolidayStatisTime), retiredHolidayStatisTime);
            update(updateWrapper);
        } else {
            // 多租户模式
            tenantUserService.editUserStaffRetiredHolidayByStaffId(staffId, retiredHolidayNumber, retiredHolidayStatisTime);
        }
    }

    @Override
    public void editSysUserStaffBindUserId(String staffId, String userId) {
        UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserId), userId);
        update(updateWrapper);
    }

    @Override
    public void queryAllSysUserIsIncumbency(InputObject inputObject, OutputObject outputObject) {
        List<Integer> list = new ArrayList<>();
        list.add(UserStaffState.ON_THE_JOB.getKey());
        list.add(UserStaffState.PROBATION.getKey());
        list.add(UserStaffState.PROBATION_PERIOD.getKey());

        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SysEveUserStaff::getState), list);
        String userIdKey = MybatisPlusUtil.toColumns(SysEveUserStaff::getUserId);
        queryWrapper.isNotNull(userIdKey).ne(userIdKey, StrUtil.EMPTY);
        List<SysEveUserStaff> userStaffList = list(queryWrapper);
        List<Map<String, Object>> mapList = userStaffList.stream()
            .map(userStaff -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", userStaff.getUserId());
                map.put("name", userStaff.getUserName());
                map.put("email", userStaff.getEmail());
                return map;
            }).collect(Collectors.toList());
        outputObject.setBeans(mapList);
        outputObject.settotal(mapList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSysUserStaffActMoneyById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String actMoney = map.get("actMoney").toString();
        // 修改员工薪资设定信息
        if (!tenantEnable) {
            // 单租户模式
            UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, staffId);
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getDesignWages), StaffWagesStateEnum.TOO_DESIGN_WAGES.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getActWages), actMoney);
            update(updateWrapper);
        } else {
            // 多租户模式
            tenantUserService.editUserStaffActMoneyByStaffId(staffId, actMoney);
        }
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getPhone), phone);
        long count = count(queryWrapper);
        return count == 0 ? false : true;
    }

    @Override
    public String queryUserStaffByPhone(String phone) {
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getPhone), phone);
        SysEveUserStaff sysEveUserStaff = getOne(queryWrapper, false);
        return null == sysEveUserStaff ? null : sysEveUserStaff.getId();
    }

    @Override
    public void querySysUserStaffByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getParams().get("userId").toString();
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserId), userId);
        SysEveUserStaff sysEveUserStaff = getOne(queryWrapper, false);
        if (sysEveUserStaff != null) {
            sysEveUserStaff = selectById(sysEveUserStaff.getId());
            outputObject.setBean(sysEveUserStaff);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        }
    }

    @Override
    public void updateCurrentUserStaff(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = InputObject.getLogParamsStatic().get("staffId").toString();
        UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserName), params.get("userName").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserPhoto), params.get("userPhoto").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserSex), params.get("userSex").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserSign), params.get("userSign").toString());
        update(updateWrapper);

        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        user.put("userName", params.get("userName").toString());
        user.put("userPhoto", params.get("userPhoto").toString());
        user.put("userSex", params.get("userSex").toString());
        user.put("userSign", params.get("userSign").toString());
        sysEveUserService.setUserLoginRedisMation(userId, user, true);

        iAuthUserService.removeCacheById(userId);
    }

    @Override
    public void updateCurrentUserBgImg(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = InputObject.getLogParamsStatic().get("staffId").toString();
        UpdateWrapper<SysEveUserStaff> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserStaff::getBackgroundImage), params.get("backgroundImage").toString());
        update(updateWrapper);

        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        user.put("backgroundImage", params.get("backgroundImage").toString());
        sysEveUserService.setUserLoginRedisMation(userId, user, true);

        iAuthUserService.removeCacheById(userId);
    }

    @Override
    public List<SysEveUserStaff> queryUserStaffByState(Integer... state) {
        List<Integer> stateList = Arrays.asList(state).stream()
            .filter(st -> st != null).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(stateList)) {
            return new ArrayList<>();
        }
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SysEveUserStaff::getState), stateList);
        return list(queryWrapper);
    }

    @Override
    public String staffTransferToUserId(String staffId) {
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, staffId);
        SysEveUserStaff sysEveUserStaff = getOne(queryWrapper, false);
        if (sysEveUserStaff != null) {
            return sysEveUserStaff.getUserId();
        }
        return StrUtil.EMPTY;
    }

}
