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
import com.skyeye.dao.SealSeServiceFeedbackTypeDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.common.constans.SealServiceConstants;
import com.skyeye.service.SealSeServiceFeedbackTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SealSeServiceFeedbackTypeServiceImpl implements SealSeServiceFeedbackTypeService {

    @Autowired
    private SealSeServiceFeedbackTypeDao sealSeServiceFeedbackTypeDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     *
     * @Title: insertCrmServiceFeedbackType
     * @Description: 添加售后服务反馈类型表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertCrmServiceFeedbackType(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = sealSeServiceFeedbackTypeDao.queryCrmServiceFeedbackTypeByName(map);
        if (item == null || item.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            sealSeServiceFeedbackTypeDao.insertCrmServiceFeedbackType(map);
        }else {
            outputObject.setreturnMessage("类型名称已存在！");
        }
    }

    /**
     *
     * @Title: queryCrmServiceFeedbackTypeList
     * @Description: 获取表中所有售后服务反馈类型表状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCrmServiceFeedbackTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = sealSeServiceFeedbackTypeDao.queryCrmServiceFeedbackTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取售后服务反馈类型表状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(SealServiceConstants.sysCrmServiceFeedbackTypeUpStateList()))){
            beans = sealSeServiceFeedbackTypeDao.queryStateUpList(map);
            jedisClient.set(SealServiceConstants.sysCrmServiceFeedbackTypeUpStateList(), JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(SealServiceConstants.sysCrmServiceFeedbackTypeUpStateList()), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: queryCrmServiceFeedbackTypeMationById
     * @Description: 通过售后服务反馈类型表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCrmServiceFeedbackTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceFeedbackTypeDao.queryCrmServiceFeedbackTypeMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editCrmServiceFeedbackTypeById
     * @Description: 编辑售后服务反馈类型表名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editCrmServiceFeedbackTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceFeedbackTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以编辑
            //获取名称相同但id不同的售后服务反馈类型表记录
            Map<String, Object> item = sealSeServiceFeedbackTypeDao.queryCrmServiceFeedbackTypeByIdAndName(map);
            if (item == null || item.isEmpty()){
                sealSeServiceFeedbackTypeDao.editCrmServiceFeedbackTypeById(map);
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
     * @Description: 编辑售后服务反馈类型表状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceFeedbackTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以上线
            sealSeServiceFeedbackTypeDao.editStateUpById(map);
            jedisClient.del(SealServiceConstants.sysCrmServiceFeedbackTypeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑售后服务反馈类型表状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceFeedbackTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("2")){//上线状态可以下线
            sealSeServiceFeedbackTypeDao.editStateDownById(map);
            jedisClient.del(SealServiceConstants.sysCrmServiceFeedbackTypeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteCrmServiceFeedbackTypeById
     * @Description: 编辑售后服务反馈类型表状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteCrmServiceFeedbackTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= sealSeServiceFeedbackTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以删除
            sealSeServiceFeedbackTypeDao.deleteCrmServiceFeedbackTypeById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
