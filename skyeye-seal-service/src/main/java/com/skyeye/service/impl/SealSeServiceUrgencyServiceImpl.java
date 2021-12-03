/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.SealSeServiceUrgencyDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.common.constans.SealServiceConstants;
import com.skyeye.service.SealSeServiceUrgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SealSeServiceUrgencyServiceImpl implements SealSeServiceUrgencyService {

    @Autowired
    private SealSeServiceUrgencyDao sealSeServiceUrgencyDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     *
     * @Title: insertSealSeServiceUrgency
     * @Description: 添加售后服务工单紧急程度表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertSealSeServiceUrgency(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = sealSeServiceUrgencyDao.querySealSeServiceUrgencyByName(map);
        if (item == null || item.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            sealSeServiceUrgencyDao.insertSealSeServiceUrgency(map);
        }else {
            outputObject.setreturnMessage("类型名称已存在！");
        }
    }

    /**
     *
     * @Title: querySealSeServiceUrgencyList
     * @Description: 获取表中所有售后服务工单紧急程度表状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySealSeServiceUrgencyList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceUrgencyDao.querySealSeServiceUrgencyList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务工单紧急程度表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(SealServiceConstants.sysSealSeServiceUrgencyUpStateList()))){
            beans = sealSeServiceUrgencyDao.queryStateUpList(map);
            jedisClient.set(SealServiceConstants.sysSealSeServiceUrgencyUpStateList(), JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(SealServiceConstants.sysSealSeServiceUrgencyUpStateList()), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: querySealSeServiceUrgencyMationById
     * @Description: 通过售后服务工单紧急程度表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySealSeServiceUrgencyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceUrgencyDao.querySealSeServiceUrgencyMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editSealSeServiceUrgencyById
     * @Description: 编辑售后服务工单紧急程度表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSealSeServiceUrgencyById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceUrgencyDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以编辑
            //获取名称相同但id不同的售后服务工单紧急程度表记录
            Map<String, Object> item = sealSeServiceUrgencyDao.querySealSeServiceUrgencyByIdAndName(map);
            if (item == null || item.isEmpty()){
                sealSeServiceUrgencyDao.editSealSeServiceUrgencyById(map);
            }else{
                outputObject.setreturnMessage("类型名称已存在！");
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务工单紧急程度表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceUrgencyDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以上线
            sealSeServiceUrgencyDao.editStateUpById(map);
            jedisClient.del(SealServiceConstants.sysSealSeServiceUrgencyUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务工单紧急程度表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceUrgencyDao.queryStateById(map);
        if(bean.get("state").toString().equals("2")){//上线状态可以下线
            sealSeServiceUrgencyDao.editStateDownById(map);
            jedisClient.del(SealServiceConstants.sysSealSeServiceUrgencyUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteSealSeServiceUrgencyById
     * @Description: 编辑售后服务工单紧急程度表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteSealSeServiceUrgencyById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceUrgencyDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以删除
            sealSeServiceUrgencyDao.deleteSealSeServiceUrgencyById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
