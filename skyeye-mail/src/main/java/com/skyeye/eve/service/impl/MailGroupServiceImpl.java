/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.MailGroupDao;
import com.skyeye.eve.service.MailGroupService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MailGroupServiceImpl
 * @Description: 通讯录分组管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/23 12:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MailGroupServiceImpl implements MailGroupService {

    @Autowired
    private MailGroupDao mailGroupDao;

    @Autowired
    public JedisClientService jedisClient;

    /**
     *
     * @Title: queryMailMationTypeList
     * @Description: 获取我的通讯录类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMailMationTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        // 获取当前登陆人所属公司的通讯录
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = mailGroupDao.queryMailMationTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: insertMailMationType
     * @Description: 新增通讯录类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertMailMationType(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        Map<String, Object> bean = mailGroupDao.queryMailMationTypeByName(map);
        if(bean == null){
            map.put("id", ToolUtil.getSurFaceId());
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            mailGroupDao.insertMailMationType(map);
            jedisClient.del(Constants.getPersonMailTypeListByUserId(user.get("id").toString()));
        }else{
            outputObject.setreturnMessage("该名称已存在，请更换。");
        }
    }

    /**
     *
     * @Title: deleteMailMationTypeById
     * @Description: 删除通讯录类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteMailMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        Map<String, Object> bean = mailGroupDao.queryMailMationTypeByIdAndUserId(map);
        if(bean == null){
            outputObject.setreturnMessage("删除失败，您不具备该数据删除权限或该类别下有人员信息。");
        }else{
            mailGroupDao.deleteMailMationTypeById(map);
            jedisClient.del(Constants.getPersonMailTypeListByUserId(user.get("id").toString()));
        }
    }

    /**
     *
     * @Title: queryMailMationTypeToEditById
     * @Description: 编辑通讯录类型进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryMailMationTypeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = mailGroupDao.queryMailMationTypeToEditById(map);
        outputObject.setBean(bean);
    }

    /**
     *
     * @Title: editMailMationTypeById
     * @Description: 编辑通讯录类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editMailMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        mailGroupDao.editMailMationTypeById(map);
        jedisClient.del(Constants.getPersonMailTypeListByUserId(user.get("id").toString()));
    }

    /**
     *
     * @Title: queryMailMationTypeListToSelect
     * @Description: 获取我的通讯录类型用作下拉框展示
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void queryMailMationTypeListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        List<Map<String, Object>> beans = new ArrayList<>();
        if(ToolUtil.isBlank(jedisClient.get(Constants.getPersonMailTypeListByUserId(user.get("id").toString())))){
            map.put("userId", user.get("id"));
            beans = mailGroupDao.queryMailMationTypeListToSelect(map);
            jedisClient.set(Constants.getPersonMailTypeListByUserId(user.get("id").toString()), JSONUtil.toJsonStr(beans));
        }else{
            beans = JSONUtil.toList(jedisClient.get(Constants.getPersonMailTypeListByUserId(user.get("id").toString())), null);
        }
        if(!beans.isEmpty()){
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }
    
}
