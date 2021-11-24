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
import com.skyeye.dao.AccountDao;
import com.skyeye.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AccountServiceImpl
 * @Description: 账户信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:42
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    /**
     * 查询账户信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAccountByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = accountDao.queryAccountByList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 添加账户信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertAccount(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = accountDao.queryAccountByName(params);
        if(bean != null){
            outputObject.setreturnMessage("账户名称已存在！");
            return;
        }
        params.put("id", ToolUtil.getSurFaceId());
        if(params.get("isDefault").toString().equals("1")){
            params.put("isDefault", "0");
            accountDao.editAccountByIsNotDefault(params);
            params.put("isDefault", "1");
        }
        params.put("createTime", DateUtil.getTimeAndToString());
        params.put("deleteFlag", 0);
        accountDao.insertAccount(params);
    }

    /**
     * 查询单个账户信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAccountById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = accountDao.queryAccountById(params);
        if(bean == null){
            outputObject.setreturnMessage("未查询到信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 删除账户信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteAccountById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", 1);
        accountDao.editAccountByDeleteFlag(params);
    }

    /**
     * 编辑账户信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAccountById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = accountDao.queryAccountByIdAndName(params);
        if(bean != null){
            outputObject.setreturnMessage("账户名称已存在！");
            return;
        }
        if(params.get("isDefault").toString().equals("1")){
            params.put("isDefault", "0");
            accountDao.editAccountByIsNotDefault(params);
            params.put("isDefault", "1");
        }
        accountDao.editAccountById(params);
    }

    /**
     * 设置默认
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAccountByIdAndIsDefault(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = accountDao.queryAccountByIdAndIsDeafault(params);
        if(bean != null){
            outputObject.setreturnMessage("状态以改变，请勿重复操作！");
            return;
        }
        params.put("isDefault", 0);
        accountDao.editAccountByIsNotDefault(params);
        params.put("isDefault", 1);
        accountDao.editAccountByIdAndIsDefault(params);
    }

    /**
     * 查看账户详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAccountByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = accountDao.queryAccountByIdAndInfo(params);
        if(bean == null){
            outputObject.setreturnMessage("未查询到信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 查看账户流水
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAccountStreamById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = accountDao.queryAccountStreamById(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取账户信息展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryAccountListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = accountDao.queryAccountListToSelect(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}
}
