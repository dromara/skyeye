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
import com.skyeye.eve.dao.WagesFieldTypeDao;
import com.skyeye.eve.service.WagesFieldTypeService;
import com.skyeye.wages.constant.WagesConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WagesFieldTypeServiceImpl implements WagesFieldTypeService {

    @Autowired
    private WagesFieldTypeDao wagesFieldTypeDao;

    /**
     * 计薪资字段状态
     */
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
     * 获取薪资字段列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesFieldTypeDao.queryWagesFieldTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增薪资字段信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertWagesFieldTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String key = map.get("key").toString();
        // 根据key获取字段是这个key的所有数据，包括删除的
        List<Map<String, Object>> fields = wagesFieldTypeDao.queryWagesFieldTypeListByKey(key);

        // 和系统默认的key做比较
        Optional<Map<String, Object>> defaultKey = WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getList().stream()
                .filter(item -> key.equals(item.get("key").toString())).findFirst();
        // 操作的key在默认定义的key中是否包含
        if(defaultKey != null && defaultKey.isPresent()) {
            outputObject.setreturnMessage("this ['key'] is Already exists simple default key.");
            return;
        }
        if(fields != null && !fields.isEmpty()){
            // 获取未删除的薪资字段
            List<Map<String, Object>> noDeleteFields = fields.stream()
                    .filter(field -> STATE.START_DELETE.getState() != Integer.parseInt(field.get("state").toString()))
                    .collect(Collectors.toList());
            if(noDeleteFields != null && !noDeleteFields.isEmpty()){
                outputObject.setreturnMessage("this ['key'] is Already exists.");
                return;
            }
        }else{
            // 为每个员工加上该薪资字段
            insertWagesFieldTypeKeyToStaff(key);
        }

        // 将该薪资字段插入数据库
        map.put("id", ToolUtil.getSurFaceId());
        map.put("createId", inputObject.getLogParams().get("id"));
        map.put("createTime", DateUtil.getTimeAndToString());
        // 默认启用
        map.put("state", STATE.START_UP.getState());
        wagesFieldTypeDao.insertWagesFieldTypeMation(map);
    }

    private void insertWagesFieldTypeKeyToStaff(String key) throws Exception {
        List<Map<String, Object>> staff = wagesFieldTypeDao.queryAllStaffMationList();
        staff.stream().forEach(bean -> {
            bean.put("key", key);
        });
        wagesFieldTypeDao.insertWagesFieldTypeKeyToStaff(staff);
    }

    /**
     * 编辑薪资字段信息时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesFieldTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = wagesFieldTypeDao.queryWagesFieldTypeMationById(map.get("id").toString());
        if(bean != null && !bean.isEmpty()){
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }else{
            outputObject.setreturnMessage("this data is non-existent.");
        }
    }

    /**
     * 编辑薪资字段信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = wagesFieldTypeDao.queryWagesFieldTypeMationById(map.get("id").toString());
        if(bean != null && !bean.isEmpty()){
            map.put("lastUpdateId", inputObject.getLogParams().get("id"));
            map.put("lastUpdateTime", DateUtil.getTimeAndToString());
            wagesFieldTypeDao.editWagesFieldTypeMationById(map);
        }else{
            outputObject.setreturnMessage("this data is non-existent.");
        }
    }

    /**
     * 删除薪资字段信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesFieldTypeDao.editWagesFieldTypeStateMationById(map.get("id").toString(), STATE.START_DELETE.getState());
    }

    /**
     * 启用薪资字段信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void enableWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesFieldTypeDao.editWagesFieldTypeStateMationById(map.get("id").toString(), STATE.START_UP.getState());
    }

    /**
     * 禁用薪资字段信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void disableWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesFieldTypeDao.editWagesFieldTypeStateMationById(map.get("id").toString(), STATE.START_DOWN.getState());
    }

    /**
     * 获取已经启用的薪资字段列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryEnableWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesFieldTypeDao.queryEnableWagesFieldTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void querySysWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        List<Map<String, Object>> beans = WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
