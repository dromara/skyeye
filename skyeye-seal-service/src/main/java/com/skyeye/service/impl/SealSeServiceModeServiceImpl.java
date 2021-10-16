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
import com.skyeye.dao.SealSeServiceModeDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.sealservice.util.SealServiceConstants;
import com.skyeye.service.SealSeServiceModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceModeServiceImpl
 * @Description: 售后服务方式管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealSeServiceModeServiceImpl implements SealSeServiceModeService {

    @Autowired
    private SealSeServiceModeDao sealSeServiceModeDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     *
     * @Title: insertSealSeServiceMode
     * @Description: 添加售后服务方式表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertSealSeServiceMode(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = sealSeServiceModeDao.querySealSeServiceModeByName(map);
        if (item == null || item.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            sealSeServiceModeDao.insertSealSeServiceMode(map);
        }else {
            outputObject.setreturnMessage("服务方式已存在！");
        }
    }

    /**
     *
     * @Title: querySealSeServiceModeList
     * @Description: 获取表中所有售后服务方式表状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySealSeServiceModeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceModeDao.querySealSeServiceModeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务方式表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(SealServiceConstants.sysSealSeServiceModeUpStateList()))){
            beans = sealSeServiceModeDao.queryStateUpList(map);
            jedisClient.set(SealServiceConstants.sysSealSeServiceModeUpStateList(), JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(SealServiceConstants.sysSealSeServiceModeUpStateList()), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: querySealSeServiceModeMationById
     * @Description: 通过售后服务方式表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySealSeServiceModeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceModeDao.querySealSeServiceModeMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editSealSeServiceModeById
     * @Description: 编辑售后服务方式表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSealSeServiceModeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceModeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以编辑
            //获取名称相同但id不同的售后服务方式表记录
            Map<String, Object> item = sealSeServiceModeDao.querySealSeServiceModeByIdAndName(map);
            if (item == null || item.isEmpty()){
                sealSeServiceModeDao.editSealSeServiceModeById(map);
            }else{
                outputObject.setreturnMessage("服务方式已存在！");
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务方式表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceModeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以上线
            sealSeServiceModeDao.editStateUpById(map);
            jedisClient.del(SealServiceConstants.sysSealSeServiceModeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务方式表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceModeDao.queryStateById(map);
        if(bean.get("state").toString().equals("2")){//上线状态可以下线
            sealSeServiceModeDao.editStateDownById(map);
            jedisClient.del(SealServiceConstants.sysSealSeServiceModeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteSealSeServiceModeById
     * @Description: 编辑售后服务方式表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteSealSeServiceModeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceModeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以删除
            sealSeServiceModeDao.deleteSealSeServiceModeById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
