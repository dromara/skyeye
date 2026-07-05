/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.enumeration.SmsSceneEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.*;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.authority.service.SysAuthorityService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.menu.service.AuthPointService;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.AppAuthService;
import com.skyeye.personnel.service.SysEveUserService;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.skyeye.sms.entity.SmsCodeSendReq;
import com.skyeye.sms.entity.SmsCodeValidateReq;
import com.skyeye.sms.service.SmsCodeService;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.service.TenantService;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AppAuthServiceImpl
 * @Description: 登录管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 17:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private SysAuthorityService sysAuthorityService;

    @Autowired
    protected JedisClientService jedisClientService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private AuthPointService authPointService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Override
    public void sendSmsCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        Integer scene = Integer.parseInt(params.get("scene").toString());
        // 情况 1：如果是修改手机场景，需要校验新手机号是否已经注册，说明不能使用该手机了
        if (scene == SmsSceneEnum.UPDATE_MOBILE.getKey()) {
            if (sysEveUserStaffService.checkPhoneExists(mobile)) {
                throw new CustomException("手机号已经被使用");
            }
        }
        // 情况 2：如果是重置密码场景，需要校验手机号是存在的
        if (scene == SmsSceneEnum.RESET_PASSWORD.getKey()) {
            if (!sysEveUserStaffService.checkPhoneExists(mobile)) {
                throw new CustomException("手机号不存在");
            }
        }
        // 情况 3：如果是修改密码场景，需要查询手机号，无需前端传递
        if (scene == SmsSceneEnum.UPDATE_PASSWORD.getKey()) {
            String staffId = inputObject.getLogParams().get("staffId").toString();
            SysEveUserStaff sysEveUserStaff = sysEveUserStaffService.selectById(staffId);
            if (StrUtil.isEmpty(sysEveUserStaff.getPhone())) {
                throw new CustomException("您还没有绑定手机号，请先绑定手机号");
            }
            mobile = sysEveUserStaff.getPhone();
        }
        if (StrUtil.isEmpty(mobile)) {
            throw new CustomException("手机号不能为空");
        }
        // 发送短信验证码
        SmsCodeSendReq smsCodeSendReq = new SmsCodeSendReq().setMobile(mobile).setScene(scene);
        smsCodeService.sendSmsCodeReq(smsCodeSendReq);

    }

    @Override
    public void smsLogin(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        String smsCode = params.get("smsCode").toString();
        // 校验验证码
        SmsCodeValidateReq smsCodeValidateReq = new SmsCodeValidateReq();
        smsCodeValidateReq.setMobile(mobile);
        smsCodeValidateReq.setSmsCode(smsCode);
        smsCodeValidateReq.setScene(SmsSceneEnum.LOGIN.getKey());
        smsCodeService.validateSmsCode(smsCodeValidateReq);
    }

    @Override
    public void queryAuthPointByUserId(InputObject inputObject, OutputObject outputObject) {
        String userIdAndType = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        List<Map<String, Object>> authPoints = new ArrayList<>();
        if (!tenantEnable) {
            // 单租户模式，获取角色id(逗号隔开的字符串)
            String roleIds = jedisClientService.get(ObjectConstant.getUserHasRoleIds(userIdAndType));
            authPoints = sysAuthorityService.getRoleHasMenuPointListByRoleIds(roleIds, userIdAndType);
        } else {
            // 多租户模式
            Map<String, Object> user = InputObject.getLogParamsStatic();
            String staffId = user.get("staffId").toString();
            TenantUser tenantUser = tenantUserService.getTenantUserByStaffId(staffId);
            boolean isAdmin = tenantUserService.checkStaffIdIsAdmin(tenantUser);
            if (isAdmin) {
                // 管理员获取所有权限点
                String tenantId = TenantContext.getTenantId();
                String cacheKey = TenantContext.getTenantAuthPointCacheKey(tenantId);
                String authPointListStr = jedisClientService.get(cacheKey);
                if (StrUtil.isNotEmpty(authPointListStr)) {
                    authPoints = JSONUtil.toList(authPointListStr, null);
                }
            } else {
                // 非管理员获取当前用户的权限点
                String roleIds = tenantUser.getRoleId();
                authPoints = sysAuthorityService.getRoleHasMenuPointListByRoleIds(roleIds, userIdAndType);
            }
        }
        outputObject.setBeans(authPoints);
        outputObject.settotal(authPoints.size());
    }

    @Override
    public void switchTenantSetAuthPoint(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new CustomException("多租户功能未开启");
        }
        String userIdAndType = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        // 切换只需要考虑管理员权限信息即可，其他用户的权限会在获取权限点的接口中处理
        Map<String, Object> params = inputObject.getParams();
        String tenantId = params.get("tenantId").toString();
        Map<String, Object> user = InputObject.getLogParamsStatic();
        String staffId = user.get("staffId").toString();
        TenantContext.setTenantId(tenantId);
        TenantUser tenantUser = tenantUserService.getTenantUserByStaffId(staffId);
        boolean isAdmin = tenantUserService.checkStaffIdIsAdmin(tenantUser);
        if (isAdmin) {
            // 管理员获取所有权限点
            String cacheKey = TenantContext.getTenantAuthPointCacheKey(tenantId);
            // 获取所有权限点(包括PC端和移动端)
            List<Map<String, Object>> authPointList = authPointService.queryAllDataForMap();
            if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
                // 开启租户功能，并且不是平台租户，查询当前租户下所有权限点的id
                List<String> ids = tenantService.queryAllMenuListByTenantId(tenantId, null);
                if (ids == null) {
                    ids = new ArrayList<>();
                }
                List<String> finalIds = ids;
                authPointList = authPointList.stream().filter(authPoint -> finalIds.contains(authPoint.get("id").toString())).collect(Collectors.toList());
            } else {
                // 平台租户，获取所有权限点(包括PC端和移动端)
            }
            // 转成树结构
            authPointList = ToolUtil.listToTree(authPointList, "id", "parentId", "children");
            jedisClientService.set(cacheKey, JSON.toJSONString(authPointList));
        }
        user.put("isAdmin", isAdmin ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey());
        user.put("companyId", tenantUser.getCompanyId());
        user.put("email", tenantUser.getEmail());
        user.put("departmentId", tenantUser.getDepartmentId());
        user.put("jobId", tenantUser.getJobId());
        user.put("jobScoreId", tenantUser.getJobScoreId());
        // 这里只修改当前终端的登录信息，其他终端的登录信息不会受到影响
        SysUserAuthConstants.setUserLoginRedisCache(userIdAndType, user);
        // 设置角色信息
        jedisClientService.set(ObjectConstant.getUserHasRoleIds(userIdAndType), tenantUser.getRoleId());
    }
}
