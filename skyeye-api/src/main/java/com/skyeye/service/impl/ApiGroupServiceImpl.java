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
import com.skyeye.dao.ApiGroupDao;
import com.skyeye.dao.ApiMationDao;
import com.skyeye.dao.ApiPropertyDao;
import com.skyeye.service.ApiGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiGroupServiceImpl
 * @Description: api接口分组服务类
 * @author: skyeye云系列
 * @date: 2021/11/28 13:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ApiGroupServiceImpl implements ApiGroupService {

    @Autowired
    private ApiGroupDao apiGroupDao;

    @Autowired
    private ApiMationDao apiMationDao;

    @Autowired
    private ApiPropertyDao apiPropertyDao;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: queryApiGroupList
     * @Description: 获取api接口分组表
     */
    @Override
    public void queryApiGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = apiGroupDao.queryApiGroupList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertApiGroupMation
     * @Description: 新增api接口分组
     */
    @Override
    @Transactional(value = "transactionManager")
    public void insertApiGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiGroupDao.queryApiGroupByName(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该分组名称已存在，请更换");
        } else {
            this.insertApiGroupMationList(Arrays.asList(map), inputObject.getLogParams().get("id").toString());
        }
    }

    @Override
    @Transactional(value = "transactionManager")
    public void insertApiGroupMationList(List<Map<String, Object>> beans, String userId) throws Exception{
        beans.forEach(bean -> {
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("userId", userId);
            bean.put("createTime", DateUtil.getTimeAndToString());
        });
        apiGroupDao.insertApiGroup(beans);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteApiGroupMationById
     * @Description: 删除api接口分组信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void deleteApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        apiPropertyDao.deleteApiPropertyByGroupId(map);
        apiMationDao.deleteApiMationByGroupId(map);
        apiGroupDao.deleteApiGroupById(map);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectApiGroupMationById
     * @Description: 通过id查找对应的api接口分组信息
     */
    @Override
    public void selectApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiGroupDao.queryApiGroupToEditById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editApiGroupMationById
     * @Description: 通过id编辑对应的api接口分组信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void editApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiGroupDao.queryApiGroupByName(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该分组名称已存在，请更换");
        } else {
            map.put("userId", inputObject.getLogParams().get("id"));
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            apiGroupDao.editApiGroupById(map);
        }
    }

}

