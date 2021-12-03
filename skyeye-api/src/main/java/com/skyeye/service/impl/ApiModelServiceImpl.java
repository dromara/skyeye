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
import com.skyeye.dao.ApiModelDao;
import com.skyeye.dao.ApiPropertyDao;
import com.skyeye.service.ApiModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiModelServiceImpl
 * @Description: api接口模块服务类
 * @author: skyeye云系列
 * @date: 2021/11/27 16:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ApiModelServiceImpl implements ApiModelService {

    @Autowired
    private ApiModelDao apiModelDao;

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
     * @Title: queryApiModelList
     * @Description: 获取api接口模块表
     */
    @Override
    public void queryApiModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = apiModelDao.queryApiModelList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: insertApiModelMation
     * @Description: 新增api接口模块
     */
    @Override
    @Transactional(value = "transactionManager")
    public void insertApiModelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiModelDao.queryApiModelMationByName(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该模块名称已存在，请更换");
        } else {
            this.insertApiModelList(Arrays.asList(map), inputObject.getLogParams().get("id").toString());
        }
    }

    @Override
    @Transactional(value = "transactionManager")
    public void insertApiModelList(List<Map<String, Object>> beans, String userId) throws Exception{
        beans.forEach(bean -> {
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("userId", userId);
            bean.put("createTime", DateUtil.getTimeAndToString());
        });
        apiModelDao.insertApiModel(beans);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: deleteApiModelById
     * @Description: 删除api接口模块信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void deleteApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        apiPropertyDao.deleteApiPropertyByModelId(map);
        apiMationDao.deleteApiMationByModelId(map);
        apiGroupDao.deleteApiGroupByModelId(map);
        apiModelDao.deleteApiModelById(map);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: selectApiModelById
     * @Description: 通过id查找对应的api接口模块信息
     */
    @Override
    public void selectApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiModelDao.queryApiModelToEditById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception 参数
     * @throws
     * @Title: editApiModelMationById
     * @Description: 通过id编辑对应的api接口模块信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void editApiModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiModelDao.queryApiModelMationByName(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该模块名称已存在，请更换");
        } else {
            map.put("userId", inputObject.getLogParams().get("id"));
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            apiModelDao.editApiModelById(map);
        }
    }

}

