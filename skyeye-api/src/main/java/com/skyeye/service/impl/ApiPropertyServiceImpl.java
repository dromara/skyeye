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
import com.skyeye.dao.ApiPropertyDao;
import com.skyeye.service.ApiPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiPropertyServiceImpl
 * @Description: api接口参数服务类
 * @author: skyeye云系列
 * @date: 2021/11/28 15:10
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ApiPropertyServiceImpl implements ApiPropertyService {

    @Autowired
    private ApiPropertyDao apiPropertyDao;

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception ApiProperty
     * @throws
     * @Title: queryApiPropertyList
     * @Description: 获取api接口参数表
     */
    @Override
    public void queryApiPropertyMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = apiPropertyDao.queryApiPropertyList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception ApiProperty
     * @throws
     * @Title: insertApiPropertyMation
     * @Description: 新增api接口参数
     */
    @Override
    @Transactional(value = "transactionManager")
    public void insertApiPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiPropertyDao.queryApiPropertyMationByApiId(map);
        if (bean != null && !bean.isEmpty()) {
            outputObject.setreturnMessage("该API参数已存在，请更换");
        } else {
            this.insertApiPropertyMationList(Arrays.asList(map), inputObject.getLogParams().get("id").toString());
        }
    }

    @Override
    @Transactional(value = "transactionManager")
    public void insertApiPropertyMationList(List<Map<String, Object>> beans, String userId) throws Exception{
        beans.forEach(bean -> {
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("userId", userId);
            bean.put("createTime", DateUtil.getTimeAndToString());
        });
        apiPropertyDao.insertApiProperty(beans);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception ApiProperty
     * @throws
     * @Title: deleteApiPropertyMationById
     * @Description: 删除api接口参数信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void deleteApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        apiPropertyDao.deleteApiPropertyById(map);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception ApiProperty
     * @throws
     * @Title: selectApiPropertyMationById
     * @Description: 通过id查找对应的api接口参数信息
     */
    @Override
    public void selectApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = apiPropertyDao.queryApiPropertyToEditById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * @param inputObject
     * @param outputObject
     * @return void    返回类型
     * @throws Exception ApiProperty
     * @throws
     * @Title: editApiPropertyMationById
     * @Description: 通过id编辑对应的api接口参数信息
     */
    @Override
    @Transactional(value = "transactionManager")
    public void editApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        apiPropertyDao.deleteApiPropertyById(map);
        this.insertApiPropertyMationList(Arrays.asList(map), inputObject.getLogParams().get("id").toString());
    }

}

