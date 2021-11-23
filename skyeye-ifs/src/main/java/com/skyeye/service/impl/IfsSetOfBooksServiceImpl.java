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
import com.skyeye.dao.IfsSetOfBooksDao;
import com.skyeye.service.IfsSetOfBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsSetOfBooksServiceImpl
 * @Description: 账套管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/21 14:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IfsSetOfBooksServiceImpl implements IfsSetOfBooksService {

    @Autowired
    private IfsSetOfBooksDao ifsSetOfBooksDao;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryIfsSetOfBooksList
     * @Description: 获取账套管理表
     */
    @Override
    public void queryIfsSetOfBooksList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = ifsSetOfBooksDao.queryIfsSetOfBooksList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertIfsSetOfBooksMation
     * @Description: 新增账套管理
     */
    @Override
    @Transactional(value = "transactionManager")
    public void insertIfsSetOfBooksMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Map<String, Object> bean = ifsSetOfBooksDao.queryIfsSetOfBooksMationByName(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该账套管理已存在，请更换");
        } else {
            map.put("id", ToolUtil.getSurFaceId());
            map.put("createTime", DateUtil.getTimeAndToString());
            ifsSetOfBooksDao.insertIfsSetOfBooks(map);
        }
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteIfsSetOfBooksById
     * @Description: 删除账套管理信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void deleteIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        ifsSetOfBooksDao.deleteIfsSetOfBooksById(map);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectIfsSetOfBooksById
     * @Description: 通过id查找对应的账套管理信息
     */
    @Override
    public void selectIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = ifsSetOfBooksDao.queryIfsSetOfBooksToEditById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editIfsSetOfBooksMationById
     * @Description: 通过id编辑对应的账套管理信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void editIfsSetOfBooksMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Map<String, Object> bean = ifsSetOfBooksDao.queryIfsSetOfBooksMationByName(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该账套信息已存在，请更换");
        } else {
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            ifsSetOfBooksDao.editIfsSetOfBooksById(map);
        }
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectIfsSetOfBooksMationById
     * @Description: 通过id查找对应的账套管理信息详情
     */
    @Override
    public void selectIfsSetOfBooksMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = ifsSetOfBooksDao.queryIfsSetOfBooksDetailById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }
}

