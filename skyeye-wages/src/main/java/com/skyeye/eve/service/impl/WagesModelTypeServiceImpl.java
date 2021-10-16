/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.WagesModelTypeDao;
import com.skyeye.eve.service.WagesModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class WagesModelTypeServiceImpl implements WagesModelTypeService {

    @Autowired
    private WagesModelTypeDao wagesModelTypeDao;

    public static enum STATE{
        START_UP(1, "启用"),
        START_DOWN(2, "禁用"),
        START_DELETE(3, "删除");
        private int state;
        private String name;
        STATE(int state, String name){
            this.state = state;
            this.name = name;
        }
        public int getState() {
            return state;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 获取薪资模板类型列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesModelTypeDao.queryWagesModelTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增薪资模板类型信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertWagesModelTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 根据判断该名称是否存在
        Map<String, Object> mation = wagesModelTypeDao.queryWagesModelTypeByNameAndNotId(map.get("nameCn").toString(), null);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same name exists, please replace it.");
            return;
        }
        // 将该薪资字段插入数据库
        map.put("id", ToolUtil.getSurFaceId());
        map.put("createId", inputObject.getLogParams().get("id"));
        map.put("createTime", DateUtil.getTimeAndToString());
        // 默认启用
        map.put("state", WagesFieldTypeServiceImpl.STATE.START_UP.getState());
        wagesModelTypeDao.insertWagesModelTypeMation(map);
    }

    /**
     * 编辑薪资模板类型信息时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesModelTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = wagesModelTypeDao.queryWagesModelTypeMationToEditById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 编辑薪资模板类型信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> mation = wagesModelTypeDao.queryWagesModelTypeByNameAndNotId(map.get("nameCn").toString(), id);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same name exists, please replace it.");
            return;
        }
        map.put("lastUpdateId", inputObject.getLogParams().get("id"));
        map.put("lastUpdateTime", DateUtil.getTimeAndToString());
        wagesModelTypeDao.editWagesModelTypeMationById(map);
    }

    /**
     * 删除薪资模板类型信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesModelTypeDao.editWagesModelTypeStateMationById(map.get("id").toString(), STATE.START_DELETE.getState());
    }

    /**
     * 启用薪资模板类型信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void enableWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesModelTypeDao.editWagesModelTypeStateMationById(map.get("id").toString(), STATE.START_UP.getState());
    }

    /**
     * 禁用薪资模板类型信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void disableWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesModelTypeDao.editWagesModelTypeStateMationById(map.get("id").toString(), STATE.START_DOWN.getState());
    }

    /**
     * 获取已经启用的薪资模板类型列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryEnableWagesModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = wagesModelTypeDao.queryEnableWagesModelTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}
