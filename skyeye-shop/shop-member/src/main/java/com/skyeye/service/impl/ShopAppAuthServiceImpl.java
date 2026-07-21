/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.enumeration.SmsSceneEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.entity.Member;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.sms.service.ISmsCodeService;
import com.skyeye.service.MemberService;
import com.skyeye.service.ShopAppAuthService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl.TRANSACTION_MANAGER_VALUE;

/**
 * @ClassName: ShopAppAuthServiceImpl
 * @Description: 商城登录管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/16 11:57
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商城登录", groupName = "商城登录", tenant = TenantEnum.NO_ISOLATION)
public class ShopAppAuthServiceImpl implements ShopAppAuthService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ISmsCodeService iSmsCodeService;

    @Override
    public void shopLoginForPC(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Member member = login(map, RequestType.PC.getKey());
        outputObject.setBean(member);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Member login(Map<String, Object> map, String requestType) {
        String phone = map.get("phone").toString();
        Member member = memberService.queryMemberByPhone(phone);
        if (ObjectUtil.isEmpty(member)) {
            throw new CustomException("手机号码不存在，请先注册！");
        }
        if (StrUtil.isEmpty(member.getPassword())) {
            throw new CustomException("该用户未设置密码！");
        }
        String password = map.get("password").toString();
        for (int i = 0; i < member.getPwdNumEnc(); i++) {
            password = ToolUtil.MD5(password);
        }
        if (!StrUtil.equals(password, member.getPassword())) {
            throw new CustomException("密码错误！");
        }
        return getMember(requestType, member, password);
    }

    @NotNull
    private static Member getMember(String requestType, Member member, String password) {
        // 先根据库中密码判断是否已设置，再清空密码字段（防泄露）
        member.setWhetherPassword(StrUtil.isEmpty(member.getPassword())
            ? WhetherEnum.DISABLE_USING.getKey()
            : WhetherEnum.ENABLE_USING.getKey());
        member.setPassword(null);
        member.setPwdNumEnc(null);
        String userToken;
        if (RequestType.APP.getKey().equals(requestType)) {
            userToken = GetUserToken.createNewToken(member.getId() + SysUserAuthConstants.APP_IDENTIFYING, password);
            SysUserAuthConstants.setUserLoginRedisCache(member.getId() + SysUserAuthConstants.APP_IDENTIFYING, BeanUtil.beanToMap(member));
        } else {
            userToken = GetUserToken.createNewToken(member.getId(), password);
            SysUserAuthConstants.setUserLoginRedisCache(member.getId(), BeanUtil.beanToMap(member));
        }
        member.setUserToken(userToken);
        return member;
    }

    /**
     * 改密成功后只返回 whetherPassword=1，并刷新登录缓存
     */
    private void returnHasPassword(OutputObject outputObject, String userId) {
        Member member = memberService.selectById(userId);
        member.setWhetherPassword(WhetherEnum.ENABLE_USING.getKey());
        member.setPassword(null);
        member.setPwdNumEnc(null);
        SysUserAuthConstants.setUserLoginRedisCache(member.getId() + SysUserAuthConstants.APP_IDENTIFYING, BeanUtil.beanToMap(member));
        SysUserAuthConstants.setUserLoginRedisCache(member.getId(), BeanUtil.beanToMap(member));
        Map<String, Object> result = new HashMap<>();
        result.put("whetherPassword", WhetherEnum.ENABLE_USING.getKey());
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void shopLoginForApp(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Member member = login(map, RequestType.APP.getKey());
        outputObject.setBean(member);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void shopLogout(InputObject inputObject, OutputObject outputObject) {
        String userTokenId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        SysUserAuthConstants.delUserLoginRedisCache(userTokenId);
        inputObject.removeSession();
    }

    @Override
    public void editShopUserPassword(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String newPassword = map.get("newPassword").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        int pwdNum = (int) (Math.random() * 100);
        for (int i = 0; i < pwdNum; i++) {
            newPassword = ToolUtil.MD5(newPassword);
        }
        memberService.editMemberPassword(userId, newPassword, pwdNum);
        returnHasPassword(outputObject, userId);
    }

    @Override
    public void sendShopSmsCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        Integer scene = Integer.parseInt(params.get("scene").toString());
        // 情况 1：如果是修改手机场景，需要校验新手机号是否已经注册，说明不能使用该手机了
        if (scene == SmsSceneEnum.UPDATE_MOBILE.getKey()) {
            Member member = memberService.queryMemberByPhone(mobile);
            if (ObjectUtil.isNotEmpty(member)) {
                throw new CustomException("手机号已经被使用");
            }
        }
        // 情况 2：如果是重置密码场景，需要校验手机号是存在的
        if (scene == SmsSceneEnum.RESET_PASSWORD.getKey()) {
            Member member = memberService.queryMemberByPhone(mobile);
            if (ObjectUtil.isEmpty(member)) {
                throw new CustomException("手机号不存在");
            }
        }
        // 情况 3：如果是修改密码场景，需要查询手机号，无需前端传递
        if (scene == SmsSceneEnum.UPDATE_PASSWORD.getKey()) {
            String id = inputObject.getLogParams().get("id").toString();
            Member member = memberService.selectById(id);
            if (StrUtil.isEmpty(member.getPhone())) {
                throw new CustomException("您还没有绑定手机号，请先绑定手机号");
            }
            mobile = member.getPhone();
        }
        if (StrUtil.isEmpty(mobile)) {
            throw new CustomException("手机号不能为空");
        }
        // 发送短信验证码
        iSmsCodeService.sendSmsCodeReq(mobile, scene);
    }

    @Override
    public void smsShopLogin(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        String smsCode = params.get("smsCode").toString();
        // 校验验证码
        iSmsCodeService.validateSmsCode(mobile, smsCode, SmsSceneEnum.LOGIN.getKey());
        // 登录
        Member member = memberService.queryMemberByPhone(mobile);
        if (ObjectUtil.isEmpty(member)) {
            throw new CustomException("手机号码不存在，请先注册！");
        }
        String requestType = InputObject.getRequest().getHeader("requestType");
        member = getMember(requestType, member, member.getPassword());
        outputObject.setBean(member);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void smsShopMemberRegister(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        String smsCode = params.get("smsCode").toString();
        // 校验验证码
        iSmsCodeService.validateSmsCode(mobile, smsCode, SmsSceneEnum.LOGIN.getKey());
        Member member = memberService.queryMemberByPhone(mobile);
        if (ObjectUtil.isNotEmpty(member)) {
            throw new CustomException("手机号已经被使用");
        }
        // 注册
        Member saveMember = new Member();
        saveMember.setPhone(mobile);
        saveMember.setName("未命名");
        saveMember.setEnabled(EnableEnum.ENABLE_USING.getKey());
        memberService.createEntity(saveMember, null);
        Map<String, Object> result = new HashMap<>();
        result.put("whetherPassword", WhetherEnum.DISABLE_USING.getKey());
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void editShopUserPasswordByPhone(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String newPassword = map.get("newPassword").toString();
        String phone = map.get("phone").toString();
        Member member = memberService.queryMemberByPhone(phone);
        if (ObjectUtil.isEmpty(member)) {
            throw new CustomException("手机号码不存在，请先注册！");
        }
        int pwdNum = (int) (Math.random() * 100);
        for (int i = 0; i < pwdNum; i++) {
            newPassword = ToolUtil.MD5(newPassword);
        }
        memberService.editMemberPassword(member.getId(), newPassword, pwdNum);
        returnHasPassword(outputObject, member.getId());
    }
}
