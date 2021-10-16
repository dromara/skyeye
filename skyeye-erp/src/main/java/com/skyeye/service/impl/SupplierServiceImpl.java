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
import com.skyeye.dao.SupplierDao;
import com.skyeye.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SupplierServiceImpl
 * @Description: 供应商信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:46
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierDao supplierDao;

    /**
     * 获取供应商信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySupplierByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = supplierDao.querySupplierByList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 添加供应商信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertSupplier(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        //查询某一租户下是否存在相同供应商的信息
        Map<String, Object> bean = supplierDao.querySupplierByUserIdAndSupplier(params);
        if(bean != null){
            outputObject.setreturnMessage("该供应商信息已存在！");
            return;
        }
        params.put("id", ToolUtil.getSurFaceId());
        params.put("createTime", DateUtil.getTimeAndToString());
        params.put("supplierType", 1);
        params.put("enabled", 1);
        params.put("isystem", 1);
        params.put("deleteFlag", 0);
        supplierDao.insertSupplier(params);
    }

    /**
     * 根据ID查询供应商信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySupplierById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = supplierDao.querySupplierById(params);
        if (bean == null){
            outputObject.setreturnMessage("未查询到供应商信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 删除供应商信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteSupplierById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", 1);
        supplierDao.editSupplierByDeleteFlag(params);
    }

    /**
     * 编辑供应商信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSupplierById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> supplierName = supplierDao.querySupplierByIdAndName(params);
        if(supplierName != null){
            outputObject.setreturnMessage("供应商名称已存在！");
            return;
        }
        supplierDao.editSupplierById(params);
    }

    /**
     * 供应商状态改为启用
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSupplierByEnabled(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("enabled", 1);
        Map<String, Object> bean = supplierDao.querySupplierByEnabled(params);
        if (bean != null){
            outputObject.setreturnMessage("状态已改变，请不要重复操作！");
            return;
        }
        supplierDao.editSupplierByEnabled(params);
    }

    /**
     * 供应商状态改为未启用
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSupplierByNotEnabled(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("enabled", 2);
        Map<String, Object> bean = supplierDao.querySupplierByEnabled(params);
        if (bean != null){
            outputObject.setreturnMessage("状态已改变，请不要重复操作！");
            return;
        }
        supplierDao.editSupplierByNotEnabled(params);
    }

    /**
     * 查看供应商详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySupplierByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = supplierDao.querySupplierByIdAndInfo(params);
        if(bean == null){
            outputObject.setreturnMessage("未查询到信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 获取供应商列表信息展示为表格供其他选择
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void querySupplierListTableToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = supplierDao.querySupplierListTableToSelect(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
	}

}
