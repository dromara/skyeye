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
import com.skyeye.dao.InoutitemDao;
import com.skyeye.service.InoutitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InoutitemServiceImpl
 * @Description: 收支项目信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:43
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class InoutitemServiceImpl implements InoutitemService {

    @Autowired
    private InoutitemDao inoutitemDao;

    /**
     * 查询收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryInoutitemByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = inoutitemDao.queryInoutitemByList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 添加收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertInoutitem(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> inoutitemName = inoutitemDao.queryInoutitemByName(params);
        if(inoutitemName != null){
            outputObject.setreturnMessage("名称已存在！");
            return;
        }
        params.put("id", ToolUtil.getSurFaceId());
        params.put("createTime", DateUtil.getTimeAndToString());
        params.put("deleteFlag", 0);
        inoutitemDao.insertInoutitem(params);
    }

    /**
     * 查询单个收支项目信息，用于信息回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = inoutitemDao.queryInoutitemById(params);
        if(bean == null){
            outputObject.setreturnMessage("未查询到该信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 删除收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", 1);
        inoutitemDao.editInoutitemByDeleteFlag(params);
    }

    /**
     * 编辑收支项目信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editInoutitemById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = inoutitemDao.queryInoutitemByIdAndName(params);
        if(bean != null){
            outputObject.setreturnMessage("名称已存在！");
            return;
        }
        inoutitemDao.editInoutitemById(params);
    }

    /**
     * 查看收支项目详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryInoutitemByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = inoutitemDao.queryInoutitemByIdAndInfo(params);
        if(bean == null){
            outputObject.setreturnMessage("未查询到信息！");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 根据条件查询收支项目展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryInoutitemListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = inoutitemDao.queryInoutitemListToSelect(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
	}

}
