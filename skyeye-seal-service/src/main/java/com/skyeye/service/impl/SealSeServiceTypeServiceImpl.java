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
import com.skyeye.dao.SealSeServiceTypeDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.common.constans.SealServiceConstants;
import com.skyeye.service.SealSeServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SealSeServiceTypeServiceImpl
 * @Description: 售后服务类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SealSeServiceTypeServiceImpl implements SealSeServiceTypeService {

    @Autowired
    private SealSeServiceTypeDao sealSeServiceTypeDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     *
     * @Title: insertSealSeServiceType
     * @Description: 添加售后服务类型表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertSealSeServiceType(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = sealSeServiceTypeDao.querySealSeServiceTypeByName(map);
        if (item == null || item.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            sealSeServiceTypeDao.insertSealSeServiceType(map);
        }else {
            outputObject.setreturnMessage("服务类型已存在！");
        }
    }

    /**
     *
     * @Title: querySealSeServiceTypeList
     * @Description: 获取表中所有售后服务类型表状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySealSeServiceTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceTypeDao.querySealSeServiceTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务类型表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(SealServiceConstants.sysSealSeServiceTypeUpStateList()))){
            beans = sealSeServiceTypeDao.queryStateUpList(map);
            jedisClient.set(SealServiceConstants.sysSealSeServiceTypeUpStateList(), JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(SealServiceConstants.sysSealSeServiceTypeUpStateList()), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: querySealSeServiceTypeMationById
     * @Description: 通过售后服务类型表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySealSeServiceTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceTypeDao.querySealSeServiceTypeMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editSealSeServiceTypeById
     * @Description: 编辑售后服务类型表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSealSeServiceTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以编辑
            //获取名称相同但id不同的售后服务类型表记录
            Map<String, Object> item = sealSeServiceTypeDao.querySealSeServiceTypeByIdAndName(map);
            if (item == null || item.isEmpty()){
                sealSeServiceTypeDao.editSealSeServiceTypeById(map);
            }else{
                outputObject.setreturnMessage("服务类型已存在！");
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑售后服务类型表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以上线
            sealSeServiceTypeDao.editStateUpById(map);
            jedisClient.del(SealServiceConstants.sysSealSeServiceTypeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务类型表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("2")){//上线状态可以下线
            sealSeServiceTypeDao.editStateDownById(map);
            jedisClient.del(SealServiceConstants.sysSealSeServiceTypeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteSealSeServiceTypeById
     * @Description: 编辑售后服务类型表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteSealSeServiceTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以删除
            sealSeServiceTypeDao.deleteSealSeServiceTypeById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
