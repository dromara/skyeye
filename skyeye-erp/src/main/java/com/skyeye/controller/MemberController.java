/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @ClassName: MemberController
 * @Description: 会员信息管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 获取会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/queryMemberByList")
    @ResponseBody
    public void queryMemberByList(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.queryMemberByList(inputObject, outputObject);
    }

    /**
     * 添加会员
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/insertMember")
    @ResponseBody
    public void insertMember(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.insertMember(inputObject, outputObject);
    }

    /**
     * 根据ID查询会员信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/queryMemberById")
    @ResponseBody
    public void queryMemberById(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.queryMemberById(inputObject, outputObject);
    }

    /**
     * 删除会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/deleteMemberById")
    @ResponseBody
    public void deleteMemberById(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.deleteMemberById(inputObject, outputObject);
    }

    /**
     * 编辑会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/editMemberById")
    @ResponseBody
    public void editMemberById(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.editMemberById(inputObject, outputObject);
    }

    /**
     * 会员状态改为启用
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/editMemberByEnabled")
    @ResponseBody
    public void editMemberByEnabled(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.editMemberByEnabled(inputObject, outputObject);
    }

    /**
     * 会员状态改为未启用
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/editMemberByNotEnabled")
    @ResponseBody
    public void editMemberByNotEnabled(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.editMemberByNotEnabled(inputObject, outputObject);
    }

    /**
     * 查看会员详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/queryMemberByIdAndInfo")
    @ResponseBody
    public void queryMemberByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.queryMemberByIdAndInfo(inputObject, outputObject);
    }
    
    /**
     * 获取会员列表信息展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MemberController/queryMemberListToSelect")
    @ResponseBody
    public void queryMemberListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
        memberService.queryMemberListToSelect(inputObject, outputObject);
    }
    
}
