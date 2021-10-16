/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MemberDao;
import com.skyeye.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MemberServiceImpl
 * @Description: 会员信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:44
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    /**
     * 获取会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMemberByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = memberDao.queryMemberByList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 添加会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertMember(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        //验证某一租户下会员信息是否存
        Map<String, Object> bean = memberDao.queryMemberByUserIdAndMember(params);
        if(bean != null){
            outputObject.setreturnMessage("该会员信息已存在！");
            return;
        }
        params.put("id", ToolUtil.getSurFaceId());
        params.put("createTime", DateUtil.getTimeAndToString());
        params.put("memberType", 3);
        params.put("enabled", 1);
        params.put("isystem", 1);
        params.put("deleteFlag", 0);
        memberDao.insertMember(params);
    }

    /**
     * 根据ID查询会员信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMemberById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = memberDao.queryMemberById(params);
        if (bean == null){
            outputObject.setreturnMessage("未查询到该会员信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 删除会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteMemberById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", 1);
        memberDao.editMemberByDeleteFlag(params);
    }

    /**
     * 编辑会员信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editMemberById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> memberName = memberDao.queryMemberByIdAndName(params);
        if(memberName != null){
            outputObject.setreturnMessage("会员名称已存在！");
            return;
        }
        memberDao.editMemberById(params);
    }

    /**
     * 会员状态改为启用
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editMemberByEnabled(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("enabled", 1);
        Map<String, Object> bean = memberDao.queryMemberByEnabled(params);
        if (bean != null){
            outputObject.setreturnMessage("状态已改变，请不要重复操作！");
            return;
        }
        memberDao.editMemberByEnabled(params);
    }

    /**
     * 会员状态改为未启用
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editMemberByNotEnabled(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("enabled", 2);
        Map<String, Object> bean = memberDao.queryMemberByEnabled(params);
        if (bean != null){
            outputObject.setreturnMessage("状态已改变，请不要重复操作！");
            return;
        }
        memberDao.editMemberByNotEnabled(params);
    }

    /**
     * 查看会员详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryMemberByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = memberDao.queryMemberByIdAndInfo(params);
        if(bean == null){
            outputObject.setreturnMessage("未查询到信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 获取会员列表信息展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMemberListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = memberDao.queryMemberListToSelect(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}
}
