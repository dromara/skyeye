/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.userauth.user.UserTreeQueryDo;
import com.skyeye.personnel.classenum.UserIsTermOfValidity;
import com.skyeye.personnel.entity.SysEveUser;
import com.skyeye.personnel.service.SysEveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserController
 * @Description: 用户管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/13 9:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用户管理", tags = "用户管理", modelName = "用户管理")
public class SysEveUserController {

    @Autowired
    public SysEveUserService sysEveUserService;

    @ApiOperation(id = "sys001", value = "获取用户列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveUserController/querySysUserList")
    public void querySysUserList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.querySysUserList(inputObject, outputObject);
    }

    @ApiOperation(id = "sys002", value = "锁定账号", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "账号ID", required = "required")})
    @RequestMapping("/post/SysEveUserController/editSysUserLockStateToLockById")
    public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.editSysUserLockStateToLockById(inputObject, outputObject);
    }

    @ApiOperation(id = "sys003", value = "解锁账号", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "账号ID", required = "required")})
    @RequestMapping("/post/SysEveUserController/editSysUserLockStateToUnLockById")
    public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.editSysUserLockStateToUnLockById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysAdd005", value = "创建账号", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysEveUser.class)
    @RequestMapping("/post/SysEveUserController/insertSysUserMationById")
    public void insertSysUserMationById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.insertSysUserMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "sys005", value = "重置密码", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "账号ID", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required")})
    @RequestMapping("/post/SysEveUserController/editSysUserPasswordMationById")
    public void editSysUserPasswordMationById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.editSysUserPasswordMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "login002", value = "从session中获取用户信息", method = "POST", allUse = "2")
    @RequestMapping("/post/SysEveUserController/queryUserMationBySession")
    public void queryUserMationBySession(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryUserMationBySession(inputObject, outputObject);
    }

    @ApiOperation(id = "sys019", value = "获取角色列表和指定用户已经绑定的角色列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "当开启多租户时，传递的是员工iD，关闭多租户时，传递的是用户ID", required = "required")})
    @RequestMapping("/post/SysEveUserController/queryRoleAndBindRoleByUserId")
    public void queryRoleAndBindRoleByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryRoleAndBindRoleByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "sys020", value = "编辑用户绑定的角色", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "rowId", name = "id", value = "账号ID", required = "required"),
        @ApiImplicitParam(id = "roleIds", name = "roleIds", value = "角色ID串，逗号隔开", required = "required")})
    @RequestMapping("/post/SysEveUserController/editRoleIdsByUserId")
    public void editRoleIdsByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.editRoleIdsByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "login005", value = "获取当前登录用户的所有菜单列表", method = "GET", allUse = "2")
    @RequestMapping("/post/SysEveUserController/queryAllMenuBySession")
    public void queryAllMenuBySession(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryAllMenuBySession(inputObject, outputObject);
    }

    @ApiOperation(id = "sys032", value = "修改个人信息时获取数据回显", method = "POST", allUse = "2")
    @RequestMapping("/post/SysEveUserController/queryUserDetailsMationByUserId")
    public void queryUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryUserDetailsMationByUserId(inputObject, outputObject);
    }

    /**
     * 修改个人信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SysEveUserController/editUserDetailsMationByUserId")
    public void editUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.editUserDetailsMationByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "commonselpeople001", value = "人员选择获取所有公司和人--公司为空的不显示", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = UserTreeQueryDo.class)
    @RequestMapping("/post/SysEveUserController/queryAllPeopleToTree")
    public void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryAllPeopleToTree(inputObject, outputObject);
    }

    @ApiOperation(id = "commonselpeople002", value = "人员选择根据当前用户所属公司获取这个公司的人", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = UserTreeQueryDo.class)
    @RequestMapping("/post/SysEveUserController/queryCompanyPeopleToTreeByUserBelongCompany")
    public void queryCompanyPeopleToTreeByUserBelongCompany(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryCompanyPeopleToTreeByUserBelongCompany(inputObject, outputObject);
    }

    @ApiOperation(id = "commonselpeople003", value = "人员选择根据当前用户所属公司获取这个公司部门展示的人", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = UserTreeQueryDo.class)
    @RequestMapping("/post/SysEveUserController/queryDepartmentPeopleToTreeByUserBelongDepartment")
    public void queryDepartmentPeopleToTreeByUserBelongDepartment(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryDepartmentPeopleToTreeByUserBelongDepartment(inputObject, outputObject);
    }

    @ApiOperation(id = "commonselpeople004", value = "人员选择根据当前用户所属公司获取这个公司岗位展示的人", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = UserTreeQueryDo.class)
    @RequestMapping("/post/SysEveUserController/queryJobPeopleToTreeByUserBelongJob")
    public void queryJobPeopleToTreeByUserBelongJob(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryJobPeopleToTreeByUserBelongJob(inputObject, outputObject);
    }

    @ApiOperation(id = "commonselpeople005", value = "人员选择根据当前用户所属公司获取这个公司同级部门展示的人", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = UserTreeQueryDo.class)
    @RequestMapping("/post/SysEveUserController/querySimpleDepPeopleToTreeByUserBelongSimpleDep")
    public void querySimpleDepPeopleToTreeByUserBelongSimpleDep(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.querySimpleDepPeopleToTreeByUserBelongSimpleDep(inputObject, outputObject);
    }

    @ApiOperation(id = "commonselpeople006", value = "根据聊天组展示用户", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = UserTreeQueryDo.class)
    @RequestMapping("/post/SysEveUserController/queryTalkGroupUserListByUserId")
    public void queryTalkGroupUserListByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryTalkGroupUserListByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "userphone004", value = "根据openId获取用户信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "openId", name = "openId", value = "用户openId", required = "required")})
    @RequestMapping("/post/SysEveUserController/queryUserMationByOpenId")
    public void queryUserMationByOpenId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryUserMationByOpenId(inputObject, outputObject);
    }

    @ApiOperation(id = "userphone005", value = "openId绑定用户信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userCode", name = "userCode", value = "账号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "openId", name = "openId", value = "用户openId", required = "required")})
    @RequestMapping("/post/SysEveUserController/insertUserMationByOpenId")
    public void insertUserMationByOpenId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.insertUserMationByOpenId(inputObject, outputObject);
    }

    @ApiOperation(id = "querySysEveUserById", value = "根据id查询用户信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "用户ID", required = "required")})
    @RequestMapping("/post/SysEveUserController/querySysEveUserById")
    public void querySysEveUserById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "resetUserEffectiveDate", value = "重置用户有效期", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "用户ID", required = "required"),
        @ApiImplicitParam(id = "isTermOfValidity", name = "isTermOfValidity", value = "是否长期有效", enumClass = UserIsTermOfValidity.class, required = "required,num"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "有效期开始时间"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "有效期结束时间")})
    @RequestMapping("/post/SysEveUserController/resetUserEffectiveDate")
    public void resetUserEffectiveDate(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.resetUserEffectiveDate(inputObject, outputObject);
    }

}
