/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.CompanyDepartmentService;
import com.skyeye.organization.service.CompanyJobScoreService;
import com.skyeye.organization.service.CompanyJobService;
import com.skyeye.organization.service.CompanyMationService;
import com.skyeye.personnel.classenum.StaffWagesStateEnum;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.skyeye.personnel.service.SysEveUserStaffTimeService;
import com.skyeye.rest.wages.service.IWagesService;
import com.skyeye.tenant.classenum.TenantUserExitType;
import com.skyeye.tenant.dao.TenantUserDao;
import com.skyeye.tenant.entity.Tenant;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.service.TenantService;
import com.skyeye.tenant.service.TenantUserInviteService;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantUserServiceImpl
 * @Description: 租户下的用户服务实现类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:47
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "租户下的用户管理", groupName = "租户下的用户管理")
public class TenantUserServiceImpl extends SkyeyeBusinessServiceImpl<TenantUserDao, TenantUser> implements TenantUserService {

    @Autowired
    private CompanyMationService companyMationService;

    @Autowired
    private CompanyDepartmentService companyDepartmentService;

    @Autowired
    private CompanyJobService companyJobService;

    @Autowired
    private CompanyJobScoreService companyJobScoreService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private SysEveUserStaffTimeService sysEveUserStaffTimeService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private IWagesService iWagesService;

    @Autowired
    private TenantUserInviteService tenantUserInviteService;

    @Override
    protected void validatorEntity(TenantUser entity) {
        if (!tenantEnable) {
            throw new CustomException("租户模式未开启.");
        }
    }

    @Override
    protected void createPrepose(TenantUser entity) {
        super.createPrepose(entity);
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        String jobNumberKey = MybatisPlusUtil.toColumns(TenantUser::getJobNumber);
        queryWrapper.select("max(0 + RIGHT(" + jobNumberKey + ", 6)) AS " + jobNumberKey);
        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isNull(tenantUser)) {
            tenantUser = new TenantUser();
            tenantUser.setJobNumber(CommonNumConstants.NUM_ZERO.toString());
        }
        entity.setJobNumber(CalculationUtil.add(tenantUser.getJobNumber(), CommonNumConstants.NUM_ONE.toString(), CommonNumConstants.NUM_ZERO));
        entity.setActWages(CommonNumConstants.NUM_ZERO.toString());
        entity.setAnnualLeave(CommonNumConstants.NUM_ZERO.toString());
        entity.setHolidayNumber(CommonNumConstants.NUM_ZERO.toString());
        entity.setRetiredHolidayNumber(CommonNumConstants.NUM_ZERO.toString());
        entity.setDesignWages(StaffWagesStateEnum.WAIT_DESIGN_WAGES.getKey());
    }

    @Override
    public void updatePrepose(TenantUser entity) {
        TenantUser oldData = selectById(entity.getId());
        entity.setTenantId(oldData.getTenantId());
        entity.setStaffId(oldData.getStaffId());
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
        entity.setIsAdmin(oldData.getIsAdmin());
    }

    @Override
    protected void createPostpose(TenantUser entity, String userId) {
        if (tenantEnable) {
            // 单租户模式在 com.skyeye.personnel.service.impl.SysEveUserStaffServiceImpl.createPostpose 新增
            iWagesService.addWagesStaffMationByStaffId(entity.getStaffId());
        }
    }

    @Override
    protected void updatePostpose(TenantUser entity, String userId) {
        sysEveUserStaffTimeService.saveUserStaffCheckWorkTime(entity.getTimeIdList(), entity.getStaffId());
    }

    @Override
    protected void writePostpose(TenantUser entity, String userId) {
        super.writePostpose(entity, userId);
        deleteUserCache(entity.getStaffId());
    }

    private void deleteUserCache(String staffId) {
        String staff2UserId = sysEveUserStaffService.staffTransferToUserId(staffId);
        if (StrUtil.isNotBlank(staff2UserId)) {
            // 删除用户的缓存信息
            jedisClientService.del(iAuthUserService.queryCacheKeyById(staff2UserId));
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString()).collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);

        String staffServiceClassName = sysEveUserStaffService.getServiceClassName();

        beans.forEach(bean -> {
            String transferStaffId = bean.get("staffId").toString();
            bean.put("staffMation", staffMap.get(transferStaffId));
            bean.put("staffServiceClassName", staffServiceClassName);
        });

        // 设置组织信息
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        companyDepartmentService.setNameMationForMap(beans, "departmentId", "departmentName", StrUtil.EMPTY);
        companyJobService.setNameMationForMap(beans, "jobId", "jobName", StrUtil.EMPTY);
        companyJobScoreService.setNameMationForMap(beans, "jobScoreId", "jobScoreName", StrUtil.EMPTY);
        return beans;
    }

    @Override
    public TenantUser selectById(String id) {
        TenantUser tenantUser = super.selectById(id);

        // 获取用户的信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService
            .queryUserMationListByStaffIds(Arrays.asList(tenantUser.getStaffId()));
        tenantUser.setStaffMation(staffMap.get(tenantUser.getStaffId()));

        // 员工考勤时间段信息--一已适配多租户
        List<Map<String, Object>> staffTimeMation = sysEveUserStaffTimeService.getStaffCheckWorkTimeByStaffId(tenantUser.getStaffId());
        tenantUser.setTimeList(staffTimeMation);

        // 设置组织信息
        companyMationService.setDataMation(tenantUser, TenantUser::getCompanyId);
        companyDepartmentService.setDataMation(tenantUser, TenantUser::getDepartmentId);
        companyJobService.setDataMation(tenantUser, TenantUser::getJobId);
        companyJobScoreService.setDataMation(tenantUser, TenantUser::getJobScoreId);
        return tenantUser;
    }

    @Override
    public void removeTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getParams().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);

        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(tenantUser)) {
            if (StrUtil.isNotEmpty(tenantUser.getTenantUserInviteId())) {
                // 存在邀请信息，则更新邀请信息的退出信息
                tenantUserInviteService.editInviteUsersToExit(tenantUser.getTenantUserInviteId(), TenantUserExitType.ADMINISTRATOR_REMOVED.getKey());
            }
        }
        remove(queryWrapper);
        deleteUserCache(staffId);
    }

    @Override
    public void exitTenantUser(InputObject inputObject, OutputObject outputObject) {
        String tenantId = inputObject.getParams().get("tenantId").toString();
        TenantContext.setTenantId(tenantId);
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);

        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(tenantUser)) {
            if (StrUtil.isNotEmpty(tenantUser.getTenantUserInviteId())) {
                // 存在邀请信息，则更新邀请信息的退出信息
                tenantUserInviteService.editInviteUsersToExit(tenantUser.getTenantUserInviteId(), TenantUserExitType.VOLUNTARY_EXIT.getKey());
            }
        }
        remove(queryWrapper);
        deleteUserCache(staffId);
    }

    @Override
    @IgnoreTenant
    public void queryTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        // 只查询没有离职的用户
        queryWrapper.ne(MybatisPlusUtil.toColumns(TenantUser::getState), UserStaffState.QUIT.getKey());
        List<TenantUser> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> tenantIds = list.stream().map(bean -> bean.getTenantId()).distinct().collect(Collectors.toList());
        List<Tenant> tenantList = tenantService.selectByIds(tenantIds.toArray(new String[tenantIds.size()]));
        Map<String, Integer> adminMap = list.stream().collect(Collectors.toMap(TenantUser::getTenantId, TenantUser::getIsAdmin, (a, b) -> a));
        tenantList.forEach(tenant -> tenant.setIsAdmin(adminMap.get(tenant.getId())));
        outputObject.setBeans(tenantList);
        outputObject.settotal(tenantList.size());
    }

    @Override
    @IgnoreTenant
    public TenantUser queryTenantUserByStaffId(String staffId, String tenantId) {
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getTenantId), tenantId);
        TenantUser tenantUser = getOne(queryWrapper, false);
        return tenantUser;
    }

    @Override
    public void editUserStaffActMoneyByStaffId(String staffId, String actMoney) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getDesignWages), StaffWagesStateEnum.TOO_DESIGN_WAGES.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getActWages), actMoney);
        update(updateWrapper);
    }

    @Override
    public void editUserStaffAnnualLeaveByStaffId(String staffId, String quarterYearHour, String annualLeaveStatisTime) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getAnnualLeave), quarterYearHour);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getAnnualLeaveStatisTime), annualLeaveStatisTime);
        update(updateWrapper);
    }

    @Override
    public void editUserStaffHolidayByStaffId(String staffId, String holidayNumber, String holidayStatisTime) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getHolidayNumber), holidayNumber);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getHolidayStatisTime), holidayStatisTime);
        update(updateWrapper);
    }

    @Override
    public void editUserStaffRetiredHolidayByStaffId(String staffId, String retiredHolidayNumber, String retiredHolidayStatisTime) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getRetiredHolidayNumber), retiredHolidayNumber);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getRetiredHolidayStatisTime), retiredHolidayStatisTime);
        update(updateWrapper);
    }

    @Override
    public SysEveUserStaff setThisTenantUserToDefault(SysEveUserStaff sysEveUserStaff) {
        if (ObjectUtil.isEmpty(sysEveUserStaff)) {
            return null;
        }
        // 默认查询当前租户下的
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), sysEveUserStaff.getId());
        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenantUser)) {
            return null;
        }
        // 设置默认值
        setTenantUserMation(sysEveUserStaff, tenantUser);
        return sysEveUserStaff;
    }

    @Override
    public Map<String, Object> setThisTenantUserToDefault(Map<String, Object> sysEveUserStaff) {
        return setThisTenantUserToDefault(sysEveUserStaff, CommonConstants.ID);
    }

    @Override
    public Map<String, Object> setThisTenantUserToDefault(Map<String, Object> sysEveUserStaff, String pointId) {
        if (CollectionUtil.isEmpty(sysEveUserStaff)) {
            return null;
        }
        // 默认查询当前租户下的
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), sysEveUserStaff.get(pointId).toString());
        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenantUser)) {
            return null;
        }
        // 设置默认值
        setTenantUserMation(sysEveUserStaff, tenantUser);
        return sysEveUserStaff;
    }

    private void setTenantUserMation(SysEveUserStaff sysEveUserStaff, TenantUser tenantUser) {
        sysEveUserStaff.setState(tenantUser.getState());
        sysEveUserStaff.setCompanyId(tenantUser.getCompanyId());
        sysEveUserStaff.setDepartmentId(tenantUser.getDepartmentId());
        sysEveUserStaff.setJobId(tenantUser.getJobId());
        sysEveUserStaff.setJobScoreId(tenantUser.getJobScoreId());
        sysEveUserStaff.setWorkTime(tenantUser.getWorkTime());
        sysEveUserStaff.setEntryTime(tenantUser.getEntryTime());
        sysEveUserStaff.setQuitTime(tenantUser.getQuitTime());
        sysEveUserStaff.setQuitReason(tenantUser.getQuitReason());
        sysEveUserStaff.setInterviewArrangementId(tenantUser.getInterviewArrangementId());
        sysEveUserStaff.setHolidayNumber(tenantUser.getHolidayNumber());
        sysEveUserStaff.setHolidayStatisTime(tenantUser.getHolidayStatisTime());
        sysEveUserStaff.setRetiredHolidayNumber(tenantUser.getRetiredHolidayNumber());
        sysEveUserStaff.setRetiredHolidayStatisTime(tenantUser.getRetiredHolidayStatisTime());
        sysEveUserStaff.setAnnualLeave(tenantUser.getAnnualLeave());
        sysEveUserStaff.setAnnualLeaveStatisTime(tenantUser.getAnnualLeaveStatisTime());
        sysEveUserStaff.setDesignWages(tenantUser.getDesignWages());
        sysEveUserStaff.setActWages(tenantUser.getActWages());
        sysEveUserStaff.setJobNumber(tenantUser.getJobNumber());
        sysEveUserStaff.setWorkstationType(tenantUser.getWorkstationType());
        sysEveUserStaff.setHourlyPrice(tenantUser.getHourlyPrice());
        sysEveUserStaff.setEmail(tenantUser.getEmail());
    }

    private void setTenantUserMation(Map<String, Object> sysEveUserStaff, TenantUser tenantUser) {
        sysEveUserStaff.put("companyId", tenantUser.getCompanyId());
        sysEveUserStaff.put("departmentId", tenantUser.getDepartmentId());
        sysEveUserStaff.put("jobId", tenantUser.getJobId());
        sysEveUserStaff.put("jobScoreId", tenantUser.getJobScoreId());
        sysEveUserStaff.put("workstationType", tenantUser.getWorkstationType());
        sysEveUserStaff.put("hourlyPrice", tenantUser.getHourlyPrice());
        sysEveUserStaff.put("email", tenantUser.getEmail());
    }

    @Override
    public List<SysEveUserStaff> setThisTenantUserToDefault(List<SysEveUserStaff> userStaffList) {
        if (CollectionUtil.isEmpty(userStaffList)) {
            return CollectionUtil.newArrayList();
        }
        List<String> staffIds = userStaffList.stream().map(SysEveUserStaff::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(staffIds)) {
            return CollectionUtil.newArrayList();
        }
        // 开启多租户模式时，默认加上租户id
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffIds);
        List<TenantUser> tenantUserList = list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantUserList)) {
            return CollectionUtil.newArrayList();
        }
        // 过滤出当前租户下的用户
        List<String> tenantStaffIdList = tenantUserList.stream().map(TenantUser::getStaffId).distinct().collect(Collectors.toList());
        userStaffList = userStaffList.stream().filter(userStaff -> tenantStaffIdList.contains(userStaff.getId())).collect(Collectors.toList());
        Map<String, TenantUser> collect = tenantUserList.stream().collect(Collectors.toMap(TenantUser::getStaffId, tenantUser -> tenantUser, (k, v) -> v));
        userStaffList.forEach(userStaff -> {
            TenantUser tenantUser = collect.get(userStaff.getId());
            // 默认查询当前租户下的
            setTenantUserMation(userStaff, tenantUser);
        });
        return userStaffList;
    }

    @Override
    public List<Map<String, Object>> setThisTenantUserToDefault(List<Map<String, Object>> sysEveUserStaffList, String pointId) {
        if (CollectionUtil.isEmpty(sysEveUserStaffList)) {
            return null;
        }
        List<String> staffIds = sysEveUserStaffList.stream().map(sysEveUserStaff -> sysEveUserStaff.get(pointId).toString())
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(staffIds)) {
            return null;
        }
        // 开启多租户模式时，默认加上租户id
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffIds);
        List<TenantUser> tenantUserList = list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantUserList)) {
            return null;
        }
        // 过滤出当前租户下的用户
        List<String> tenantStaffIdList = tenantUserList.stream().map(TenantUser::getStaffId).distinct().collect(Collectors.toList());
        sysEveUserStaffList = sysEveUserStaffList.stream().filter(sysEveUserStaff -> tenantStaffIdList.contains(sysEveUserStaff.get(pointId).toString())).collect(Collectors.toList());
        Map<String, TenantUser> collect = tenantUserList.stream().collect(Collectors.toMap(TenantUser::getStaffId, tenantUser -> tenantUser, (k, v) -> v));
        sysEveUserStaffList.forEach(sysEveUserStaff -> {
            TenantUser tenantUser = collect.get(sysEveUserStaff.get(pointId).toString());
            // 默认查询当前租户下的
            setTenantUserMation(sysEveUserStaff, tenantUser);
        });
        return sysEveUserStaffList;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void addTenantAdminUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String tenantId = params.get("tenantId").toString();

        // 校验租户的用户数量
        tenantService.checkTenantAccountNum(tenantId);

        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String phone = params.get("phone").toString();
        // 校验手机号
        String userStaffId = sysEveUserStaffService.queryUserStaffByPhone(phone);
        if (StrUtil.isEmpty(userStaffId)) {
            // 手机号不存在，新增用户信息
            userStaffId = saveUserStaff(params, userId);
        }

        TenantContext.setTenantId(tenantId);
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), userStaffId);
        TenantUser checkTenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkTenantUser)) {
            throw new CustomException("该员工已加入该租户，请前往【用户管理】设置为管理员身份");
        }

        // 加入租户
        TenantUser tenantUser = new TenantUser();
        tenantUser.setStaffId(userStaffId);
        tenantUser.setIsAdmin(WhetherEnum.ENABLE_USING.getKey());
        tenantUser.setState(UserStaffState.ON_THE_JOB.getKey());
        String currentTime = DateUtil.getTimeAndToString();
        tenantUser.setWorkTime(currentTime);
        tenantUser.setEntryTime(currentTime);
        tenantUser.setTrialTime(currentTime);
        createEntity(tenantUser, userId);
    }

    private String saveUserStaff(Map<String, Object> params, String userId) {
        SysEveUserStaff sysEveUserStaff = new SysEveUserStaff();
        sysEveUserStaff.setUserName(params.get("userName").toString());
        sysEveUserStaff.setUserSex(Integer.parseInt(params.get("userSex").toString()));
        sysEveUserStaff.setPhone(params.get("phone").toString());
        sysEveUserStaff.setUserPhoto("/images/util/assest/common/img/anonymous.png");
        sysEveUserStaff.setUserIdCard(params.get("userIdCard").toString());
        sysEveUserStaff.setPassword(params.get("password").toString());
        // 开启自动注册账号
        sysEveUserStaff.setWhetherRegister(WhetherEnum.ENABLE_USING.getKey());
        // 保存用户信息
        return sysEveUserStaffService.createEntity(sysEveUserStaff, userId);
    }

    @Override
    public boolean checkStaffIdIsAdmin(String staffId) {
        // 默认强隔离
        TenantUser tenantUser = getTenantUserByStaffId(staffId);
        return checkStaffIdIsAdmin(tenantUser);
    }

    @Override
    public boolean checkStaffIdIsAdmin(TenantUser tenantUser) {
        return tenantUser.getIsAdmin() == WhetherEnum.ENABLE_USING.getKey();
    }

    @Override
    public TenantUser getTenantUserByStaffId(String staffId) {
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenantUser)) {
            throw new CustomException("该租户下不存在该员工");
        }
        return tenantUser;
    }

    @Override
    public void switchingIdentitiesById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        TenantUser tenantUser = selectById(id);
        if (ObjectUtil.isEmpty(tenantUser) || StrUtil.isEmpty(tenantUser.getId())) {
            throw new CustomException("该用户不存在");
        }
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getIsAdmin),
            tenantUser.getIsAdmin() == WhetherEnum.ENABLE_USING.getKey() ? WhetherEnum.DISABLE_USING.getKey() : WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    public long getTenantUserCountByTenantId(String tenantId) {
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getTenantId), tenantId);
        return count(queryWrapper);
    }

    @Override
    public void quit(String staffId, String quitTime, String quitReason) {
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getQuitTime), quitTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getQuitReason), quitReason);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getState), UserStaffState.QUIT.getKey());
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    public void queryTenantUserStaffIdByTenantId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String tenantId = params.get("tenantId").toString();

        // 员工状态
        String stateListStr = params.get("stateList").toString();
        List<String> stateList = StrUtil.isEmpty(stateListStr) ? CollectionUtil.newArrayList() :
            Arrays.asList(stateListStr.split(CommonCharConstants.COMMA_MARK));

        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getTenantId), tenantId);
        if (CollectionUtil.isNotEmpty(stateList)) {
            queryWrapper.in(MybatisPlusUtil.toColumns(TenantUser::getState), stateList);
        }
        List<TenantUser> tenantUserList = list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantUserList)) {
            return;
        }
        List<String> staffIdList = tenantUserList.stream().map(TenantUser::getStaffId).distinct().collect(Collectors.toList());
        Map<String, SysEveUserStaff> staffId2UserMap = sysEveUserStaffService.getUserIdsByStaffIds(staffIdList);
        tenantUserList.forEach(tenantUser -> {
            SysEveUserStaff user = staffId2UserMap.get(tenantUser.getStaffId());
            if (ObjectUtil.isNotEmpty(user)) {
                tenantUser.setUserId(user.getUserId());
            }
        });
        outputObject.setBeans(tenantUserList);
        outputObject.settotal(tenantUserList.size());
    }

    @Override
    @IgnoreTenant
    public void queryTenantUserListByTenantId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isEmpty(commonPageInfo.getTenantId())) {
            throw new CustomException("租户ID不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<TenantUser> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getTenantId), commonPageInfo.getTenantId());
        List<TenantUser> tenantUserList = super.list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantUserList)) {
            return;
        }
        List<Map<String, Object>> beans = tenantUserList.stream()
            .map(tenantUser -> BeanUtil.beanToMap(tenantUser)).collect(Collectors.toList());
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString()).collect(Collectors.toList());
        TenantContext.setTenantId(commonPageInfo.getTenantId());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String transferStaffId = bean.get("staffId").toString();
            bean.put("staffMation", staffMap.get(transferStaffId));
        });

        // 设置组织信息
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        companyDepartmentService.setNameMationForMap(beans, "departmentId", "departmentName", StrUtil.EMPTY);
        companyJobService.setNameMationForMap(beans, "jobId", "jobName", StrUtil.EMPTY);
        companyJobScoreService.setNameMationForMap(beans, "jobScoreId", "jobScoreName", StrUtil.EMPTY);
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 解散组织时清理该租户下全部成员，并清理对应用户缓存。
     *
     * @param tenantId 租户id
     */
    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void removeAllByTenantId(String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            return;
        }
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getTenantId), tenantId);
        List<TenantUser> tenantUserList = list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantUserList)) {
            return;
        }
        // 先收集 staffId，删除成员关系后再清缓存，避免查询不到关联用户
        List<String> staffIds = tenantUserList.stream()
            .map(TenantUser::getStaffId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        remove(queryWrapper);
        staffIds.forEach(this::deleteUserCache);
    }

}
