/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CharUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.classenum.MemberAuthStatus;
import com.skyeye.dao.MemberDao;
import com.skyeye.entity.Member;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.exception.CustomException;
import com.skyeye.level.entity.ShopMemberLevel;
import com.skyeye.level.service.ShopMemberLevelService;
import com.skyeye.service.MemberService;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName: MemberServiceImpl
 * @Description: 会员管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/2 15:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "会员管理", groupName = "会员管理", tenant = TenantEnum.NO_ISOLATION)
public class MemberServiceImpl extends SkyeyeBusinessServiceImpl<MemberDao, Member> implements MemberService {

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private ShopMemberLevelService shopMemberLevelService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMemberByList(pageInfo);
        return beans;
    }

    @Override
    public void createPrepose(Member entity) {
        setMemberMation(entity);
        setLevel(entity);
    }

    private void setLevel(Member entity) {
        ShopMemberLevel minLevel = shopMemberLevelService.getMinLevel();
        if (ObjectUtil.isNotEmpty(minLevel)) {
            entity.setLevelId(minLevel.getId());
        }
    }

    @Override
    public void updatePrepose(Member entity) {
        setMemberMation(entity);
        Member oldMember = selectById(entity.getId());
        entity.setPassword(oldMember.getPassword());
        entity.setWechatOpenId(oldMember.getWechatOpenId());
        entity.setPwdNumEnc(oldMember.getPwdNumEnc());
        if (StrUtil.isEmpty(entity.getLevelId())) {
            setLevel(entity);
        } else {
            ShopMemberLevel shopMemberLevel = shopMemberLevelService.selectById(entity.getLevelId());
            if (StrUtil.isEmpty(shopMemberLevel.getId())) {
                shopMemberLevel = shopMemberLevelService.getSimpleLevelByLevel(CommonNumConstants.NUM_ONE);
                entity.setLevelId(shopMemberLevel.getId());
            }
        }
    }

    private void setMemberMation(Member entity) {
        String name = entity.getName();
        name = CharUtil.filterEmoji(name);
        name = CharUtil.removeFourChar(name);
        entity.setName(name);
    }

    @Override
    public Member selectById(String id) {
        Member member = super.selectById(id);
        iAreaService.setDataMation(member, Member::getProvinceId);
        iAreaService.setDataMation(member, Member::getCityId);
        iAreaService.setDataMation(member, Member::getAreaId);
        iAreaService.setDataMation(member, Member::getTownshipId);
        shopStoreService.setDataMation(member, Member::getStoreId);
        return member;
    }

    /**
     * 获取我录入的会员信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyWriteMemberList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMemberByList(pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public Member queryMemberByPhone(String phone) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Member::getPhone), phone);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Member::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        Member member = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(member)) {
            shopMemberLevelService.setDataMation(member, Member::getLevelId);
        }
        return member;
    }

    @Override
    public void editMemberPassword(String userId, String newPassword, int pwdNum) {
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getPassword), newPassword);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getPwdNumEnc), pwdNum);
        update(updateWrapper);
    }

    @Override
    public void updateCurrentLoginMemberNickname(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String name = params.get("name").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 根据id更新会员昵称
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getName), name);
        update(updateWrapper);
        editCache(outputObject, userId);
    }

    private void editCache(OutputObject outputObject, String userId) {
        // 更新缓存
        refreshCache(userId);
        // 更新会员登录缓存
        Member member = selectById(userId);
        member.setWhetherPassword(StrUtil.isEmpty(member.getPassword())
            ? WhetherEnum.DISABLE_USING.getKey()
            : WhetherEnum.ENABLE_USING.getKey());
        member.setPassword(null);
        member.setPwdNumEnc(null);
        SysUserAuthConstants.setUserLoginRedisCache(member.getId() + SysUserAuthConstants.APP_IDENTIFYING, BeanUtil.beanToMap(member));
        SysUserAuthConstants.setUserLoginRedisCache(member.getId(), BeanUtil.beanToMap(member));
        outputObject.setBean(member);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void updateCurrentLoginMemberAvatar(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String avatar = params.get("avatar").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 根据id更新会员昵称
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getAvatar), avatar);
        update(updateWrapper);
        editCache(outputObject, userId);
    }

    @Override
    public void queryCurrentLoginMember(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        Member member = selectById(userId);
        shopMemberLevelService.setDataMation(member, Member::getLevelId);
        member.setWhetherPassword(StrUtil.isEmpty(member.getPassword())
            ? WhetherEnum.DISABLE_USING.getKey()
            : WhetherEnum.ENABLE_USING.getKey());
        if (member.getAuthStatus() == null) {
            member.setAuthStatus(MemberAuthStatus.NOT_AUTH.getKey());
        }
        member.setPassword(null);
        member.setPwdNumEnc(null);
        outputObject.setBean(member);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void submitMemberRealNameAuth(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        String realName = params.get("realName").toString();
        String idCard = params.get("idCard").toString();
        String idCardFront = params.get("idCardFront").toString();
        String idCardBack = params.get("idCardBack").toString();
        if (StrUtil.isBlank(realName)) {
            throw new CustomException("请填写真实姓名");
        }
        if (StrUtil.isBlank(idCard) || idCard.length() < 15) {
            throw new CustomException("请填写正确的身份证号");
        }
        if (StrUtil.isBlank(idCardFront) || StrUtil.isBlank(idCardBack)) {
            throw new CustomException("请上传身份证正反面照片");
        }
        Member member = selectById(userId);
        if (member != null && Objects.equals(member.getAuthStatus(), MemberAuthStatus.AUTHED.getKey())) {
            throw new CustomException("您已完成实名认证，无需重复提交");
        }
        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getRealName), realName.trim());
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getIdCard), idCard.trim());
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getIdCardFront), idCardFront);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getIdCardBack), idCardBack);
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getAuthStatus), MemberAuthStatus.AUTHED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Member::getAuthTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        editCache(outputObject, userId);
    }

}
