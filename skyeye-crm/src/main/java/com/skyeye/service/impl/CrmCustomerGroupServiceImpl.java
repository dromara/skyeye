/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CrmConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.CrmCustomerGroupDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.CrmCustomerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmCustomerGroupServiceImpl
 * @Description: 客户分组管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:19
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CrmCustomerGroupServiceImpl implements CrmCustomerGroupService {

    @Autowired
    private CrmCustomerGroupDao crmCustomerGroupDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     *
     * @Title: insertCustomerGroup
     * @Description: 添加客户分组信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertCustomerGroup(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = crmCustomerGroupDao.queryCustomerGroupByName(map);
        if (item == null || item.isEmpty()){
            String userId = inputObject.getLogParams().get("id").toString();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", userId);
            map.put("createTime", DateUtil.getTimeAndToString());
            map.put("lastUpdateId", userId);
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            crmCustomerGroupDao.insertCustomerGroup(map);
        }else {
            outputObject.setreturnMessage("该名称已存在！");
        }
    }

    /**
     *
     * @Title: queryCustomerGroupList
     * @Description: 获取表中所有客户分组状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCustomerGroupList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = crmCustomerGroupDao.queryCustomerGroupList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取客户分组状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(CrmConstants.sysCustomerGroupUpStateList()))){
            beans = crmCustomerGroupDao.queryStateUpList(map);
            jedisClient.set(CrmConstants.sysCustomerGroupUpStateList(), JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(CrmConstants.sysCustomerGroupUpStateList()), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: queryCustomerGroupMationById
     * @Description: 编辑时回显数据
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCustomerGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = crmCustomerGroupDao.queryCustomerGroupMationById(map);
        if(bean == null || bean.isEmpty()){
        	outputObject.setreturnMessage("该数据不存在或已被删除.");
        	return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editCustomerGroupById
     * @Description: 编辑客户分组名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editCustomerGroupById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerGroupDao.queryStateById(map);
        String nowState = bean.get("state").toString();
        if("1".equals(nowState) || "3".equals(nowState)){
        	// 新建和下线状态可以编辑
            // 获取名称相同但id不同的客户分组记录
            Map<String, Object> item = crmCustomerGroupDao.queryCustomerGroupByIdAndName(map);
            if (item == null || item.isEmpty()){
            	map.put("lastUpdateId", inputObject.getLogParams().get("id").toString());
                map.put("lastUpdateTime", DateUtil.getTimeAndToString());
                crmCustomerGroupDao.editCustomerGroupById(map);
            }else{
                outputObject.setreturnMessage("该名称已存在！");
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerGroupDao.queryStateById(map);
        String nowState = bean.get("state").toString();
        if("1".equals(nowState) || "3".equals(nowState)){
        	// 新建和下线状态可以编辑
            crmCustomerGroupDao.editStateUpById(map);
            jedisClient.del(CrmConstants.sysCustomerGroupUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerGroupDao.queryStateById(map);
        String nowState = bean.get("state").toString();
        if("2".equals(nowState)){
        	// 上线状态可以下线
            crmCustomerGroupDao.editStateDownById(map);
            jedisClient.del(CrmConstants.sysCustomerGroupUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteCustomerGroupById
     * @Description: 删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteCustomerGroupById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerGroupDao.queryStateById(map);
        String nowState = bean.get("state").toString();
        if("1".equals(nowState) || "3".equals(nowState)){
        	// 新建和下线状态可以删除
            crmCustomerGroupDao.deleteCustomerGroupById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
