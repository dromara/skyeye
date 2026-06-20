/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.activity.classenum.ActivityType;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.dao.ChooseUserDao;
import com.skyeye.user.entity.ChooseUser;
import com.skyeye.user.enumclass.ChooseUserType;
import com.skyeye.user.service.ChooseUserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ChooseUserServiceImpl
 * @Description: 用户服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户管理", groupName = "用户管理")
public class ChooseUserServiceImpl extends SkyeyeBusinessServiceImpl<ChooseUserDao, ChooseUser> implements ChooseUserService {

    @Override
    public QueryWrapper<ChooseUser> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ChooseUser> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseUser::getType), commonPageInfo.getType());
        return queryWrapper;
    }

    @Override
    public void chooseUserLogin(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String accountNumber = map.get("accountNumber").toString();
        String password = ToolUtil.MD5(map.get("password").toString());
        QueryWrapper<ChooseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChooseUser::getAccountNumber), accountNumber);
        ChooseUser user = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(user)) {
            outputObject.setreturnMessage("请确保账号输入无误！");
        } else {
            if (password.equals(user.getPassword())) {
                Map<String, Object> userMation = BeanUtil.beanToMap(user);
                String requestType = InputObject.getRequest().getHeader("requestType");
                // 学生这里以学生id作为userToken,确保不会和后台登录用户的id重复
                String userToken;
                if (RequestType.APP.getKey().equals(requestType)) {
                    SysUserAuthConstants.setUserLoginRedisCache(user.getId() + SysUserAuthConstants.APP_IDENTIFYING, userMation);
                    userToken = GetUserToken.createNewToken(user.getId() + SysUserAuthConstants.APP_IDENTIFYING, user.getPassword());
                } else {
                    SysUserAuthConstants.setUserLoginRedisCache(user.getId(), userMation);
                    userToken = GetUserToken.createNewToken(user.getId(), user.getPassword());
                }
                userMation.put("userToken", userToken);
                userMation.put("password", null);
                outputObject.setBean(userMation);
            } else {
                outputObject.setreturnMessage("密码输入错误！");
            }
        }
    }

    @Override
    public void chooseUserExit(InputObject inputObject, OutputObject outputObject) {
        String userTokenId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        SysUserAuthConstants.delUserLoginRedisCache(userTokenId);
    }

    @Override
    public void editChoosePassword(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 获取当前用户信息
        String stuId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<ChooseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, stuId);
        ChooseUser user = getOne(queryWrapper);
        // 旧密码匹配
        if (user.getPassword().equals(ToolUtil.MD5(map.get("oldPassword").toString()))) {
            String newPassword = ToolUtil.MD5(map.get("newPassword").toString());

            UpdateWrapper<ChooseUser> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, stuId);
            updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getPassword), newPassword);
            update(updateWrapper);
            if (update(updateWrapper)) {
                String userTokenId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
                SysUserAuthConstants.delUserLoginRedisCache(userTokenId);
            }
        } else {
            outputObject.setreturnMessage("旧密码输入错误.");
        }
    }

    @Override
    public void importChooseUser(InputObject inputObject, OutputObject outputObject) {
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                ImportParams reportModelAttrParams = new ImportParams();
                reportModelAttrParams.setStartSheetIndex(0);
                List<ChooseUser> chooseUserList;
                try {
                    chooseUserList = ExcelImportUtil.importExcel(file.getInputStream(), ChooseUser.class, reportModelAttrParams);
                } catch (Exception ee) {
                    throw new CustomException(ee);
                }
                // 验证数据
                List<ChooseUser> insertList = checkUserList(chooseUserList);
                if (CollectionUtil.isEmpty(insertList)) {
                    return;
                }
                insertList.forEach(bean -> {
                    bean.setType(ChooseUserType.STUDENT.getKey());
                    bean.setPassword(ToolUtil.MD5(bean.getPassword()));
                });
                createEntity(insertList, StrUtil.EMPTY);
            }
        }
    }

    @Override
    public void importTeacherChooseUser(InputObject inputObject, OutputObject outputObject) {
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                ImportParams reportModelAttrParams = new ImportParams();
                reportModelAttrParams.setStartSheetIndex(0);
                List<ChooseUser> chooseUserList;
                try {
                    chooseUserList = ExcelImportUtil.importExcel(file.getInputStream(), ChooseUser.class, reportModelAttrParams);
                } catch (Exception ee) {
                    throw new CustomException(ee);
                }
                // 验证数据
                List<ChooseUser> insertList = checkUserList(chooseUserList);
                if (CollectionUtil.isEmpty(insertList)) {
                    return;
                }
                insertList.forEach(bean -> {
                    bean.setType(ChooseUserType.TEACHER.getKey());
                    bean.setActivityType(ActivityType.UN_SINGLE.getKey());
                    bean.setPassword(ToolUtil.MD5(bean.getPassword()));
                });
                createEntity(insertList, StrUtil.EMPTY);
            }
        }
    }

    @Override
    public List<ChooseUser> queryChoostUserList(List<String> userNoList) {
        if (CollUtil.isEmpty(userNoList)) {
            return new ArrayList<>();
        }
        QueryWrapper<ChooseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ChooseUser::getStuNo), userNoList);
        List<ChooseUser> chooseUserList = getBaseMapper().selectList(queryWrapper);
        return chooseUserList;
    }

    @Override
    public void queryLoginUserInfo(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        ChooseUser chooseUser = selectById(userId);
        outputObject.setBean(chooseUser);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void editLoginUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String jobTitle = map.get("jobTitle").toString();
        String guideCapacity = map.get("guideCapacity").toString();
        String qq = map.get("qq").toString();
        String phone = map.get("phone").toString();
        String topicRequirement = map.get("topicRequirement").toString();
        String workRequirement = map.get("workRequirement").toString();
        String remark = map.get("remark").toString();
        String activityType = map.get("activityType").toString();
        UpdateWrapper<ChooseUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getJobTitle), jobTitle);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getGuideCapacity), guideCapacity);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getQq), qq);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getPhone), phone);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getTopicRequirement), topicRequirement);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getWorkRequirement), workRequirement);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getRemark), remark);
        updateWrapper.set(MybatisPlusUtil.toColumns(ChooseUser::getActivityType), activityType);
        update(updateWrapper);
        refreshCache(userId);
    }

    /**
     * 上传账号信息时的校验
     *
     * @param chooseUserList
     * @return 数据库不存在的账号信息
     */
    private List<ChooseUser> checkUserList(List<ChooseUser> chooseUserList) {
        if (CollectionUtil.isEmpty(chooseUserList)) {
            return new ArrayList<>();
        }
        // 筛选出有账号和密码的数据
        List<ChooseUser> insertList = chooseUserList.stream()
            .filter(bean -> StrUtil.isNotEmpty(bean.getAccountNumber()) || StrUtil.isNotEmpty(bean.getPassword())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(insertList)) {
            return new ArrayList<>();
        }
        // 取出所有账号
        List<String> accountNumberList = insertList.stream().map(ChooseUser::getAccountNumber).collect(Collectors.toList());
        // 取出所有stuNo
        List<String> stuNoList = insertList.stream().map(ChooseUser::getStuNo).collect(Collectors.toList());
        String accountNumber = MybatisPlusUtil.toColumns(ChooseUser::getAccountNumber);
        String stuNo = MybatisPlusUtil.toColumns(ChooseUser::getStuNo);
        // 查询数据库中已经存在的账号
        QueryWrapper<ChooseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(accountNumber, stuNo);
        queryWrapper.in(accountNumber, accountNumberList);
        queryWrapper.in(stuNo, stuNoList);
        List<ChooseUser> chooseUsers = list(queryWrapper);
        if (CollectionUtil.isEmpty(chooseUsers)) {
            // 数据库中没有重复数据
            return insertList;
        }
        // 过滤掉数据库已经存在的数据
        List<String> exitAccountNUmberList = chooseUsers.stream().map(ChooseUser::getAccountNumber).collect(Collectors.toList());
        List<String> exitStuNoList = chooseUsers.stream().map(ChooseUser::getStuNo).collect(Collectors.toList());
        List<ChooseUser> resultList = insertList.stream().filter(bean -> !exitAccountNUmberList.contains(bean.getAccountNumber())
            || !exitStuNoList.contains(bean.getStuNo())).collect(Collectors.toList());
        return resultList;
    }
}