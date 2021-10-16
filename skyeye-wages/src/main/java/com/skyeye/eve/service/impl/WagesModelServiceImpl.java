/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.WagesModelApplicableObjectsDao;
import com.skyeye.eve.dao.WagesModelDao;
import com.skyeye.eve.dao.WagesModelFieldDao;
import com.skyeye.eve.service.WagesModelService;
import com.skyeye.wages.constant.WagesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WagesModelServiceImpl implements WagesModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WagesModelServiceImpl.class);

    @Autowired
    private WagesModelDao wagesModelDao;

    @Autowired
    private WagesModelApplicableObjectsDao wagesModelApplicableObjectsDao;

    @Autowired
    private WagesModelFieldDao wagesModelFieldDao;

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
     * 获取薪资模板列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesModelList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesModelDao.queryWagesModelList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增薪资模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertWagesModelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String title = map.get("title").toString();
        Map<String, Object> mation = wagesModelDao.queryWagesModelMationByTitleAndNotId(title, null);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same title exists, please replace it.");
            return;
        }
        String id = ToolUtil.getSurFaceId();
        LOGGER.info("create wages model id is: {}", id);
        map.put("createId", inputObject.getLogParams().get("id"));
        map.put("createTime", DateUtil.getTimeAndToString());
        map.put("state", STATE.START_UP.getState());
        map.put("id", id);
        wagesModelDao.insertWagesModelMation(map);
        // 处理薪资模板使用对象信息
        wagesModelApplicableObjects(map.get("str").toString(), id);
        // 处理薪资模板字段属性信息
        wagesModelField(map.get("fieldStr").toString(), id);
    }

    /**
     * 处理薪资模板使用对象信息
     *
     * @param str
     * @param id
     */
    private void wagesModelApplicableObjects(String str, String id) throws Exception {
        wagesModelApplicableObjectsDao.deleteWagesModelApplicableObjectsByModelId(id);
        if(ToolUtil.isBlank(str)){
            return;
        }
        List<Map<String, Object>> applicableObjects = JSONUtil.toList(JSONUtil.parseArray(str), null);
        applicableObjects.stream().forEach(bean -> {
            bean.put("modelId", id);
        });
        if(applicableObjects.isEmpty()){
            return;
        }
        wagesModelApplicableObjectsDao.insertWagesModelApplicableObjects(applicableObjects);
    }

    /**
     * 处理薪资模板字段属性信息
     *
     * @param str
     * @param id
     */
    private void wagesModelField(String str, String id) throws Exception {
        wagesModelFieldDao.deleteWagesModelFieldByModelId(id);
        if(ToolUtil.isBlank(str)){
            return;
        }
        List<Map<String, Object>> field = JSONUtil.toList(JSONUtil.parseArray(str), null);
        field.stream().forEach(bean -> {
            bean.put("id", ToolUtil.getSurFaceId());
            bean.put("modelId", id);
        });
        if(field.isEmpty()){
            return;
        }
        wagesModelFieldDao.insertWagesModelField(field);
    }

    /**
     * 编辑薪资模板信息时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesModelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = wagesModelDao.queryWagesModelMationById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        getWagesModelApplicableObjects(bean, id);
        bean.put("modelField", getWagesModelFieldsByModelId(id));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 薪资模板使用对象信息
     *
     * @param bean
     * @param id
     * @throws Exception
     */
    private void getWagesModelApplicableObjects(Map<String, Object> bean, String id) throws Exception {
        List<Map<String, Object>> applicableObjects = wagesModelApplicableObjectsDao.queryWagesModelApplicableObjectsByModelId(id);
        // 根据objectType分组
        Map<String, List<Map<String, Object>>> groupByType = applicableObjects.stream()
                .collect(Collectors.groupingBy(this::customKey));
        groupByType.forEach((key, value) -> {
            List<String> ids = value.stream().map(p -> p.get("objectId").toString()).collect(Collectors.toList());
            if("1".equals(key)){
                // 员工
                try {
                    List<Map<String, Object>> userStaff = wagesModelDao.queryStaffNameListByStaffIdList(ids);
                    bean.put("userStaff", userStaff);
                } catch (Exception e) {
                    LOGGER.info("get securityFundId {}'s userStaff failed.{}", e);
                }
            }else if("2".equals(key)){
                // 部门
                try {
                    List<Map<String, Object>> departMent = wagesModelDao.queryDepartMentNameListByDepartMentIdList(ids);
                    bean.put("departMent", departMent);
                } catch (Exception e) {
                    LOGGER.info("get securityFundId {}'s userStaff failed.{}", e);
                }
            }else if("3".equals(key)){
                // 企业
                try {
                    List<Map<String, Object>> company = wagesModelDao.queryCompanyNameListByCompanyIdList(ids);
                    bean.put("company", company);
                } catch (Exception e) {
                    LOGGER.info("get securityFundId {}'s userStaff failed.{}", e);
                }
            }
        });
    }

    private String customKey(Map<String,Object> map){
        return map.get("objectType").toString();
    }

    /**
     * 编辑薪资模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> mation = wagesModelDao.queryWagesModelMationByTitleAndNotId(map.get("title").toString(), id);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same title exists, please replace it.");
            return;
        }
        map.put("lastUpdateId", inputObject.getLogParams().get("id"));
        map.put("lastUpdateTime", DateUtil.getTimeAndToString());
        wagesModelDao.editWagesModelMationById(map);
        // 处理薪资模板使用对象信息
        wagesModelApplicableObjects(map.get("str").toString(), id);
        // 处理薪资模板字段属性信息
        wagesModelField(map.get("fieldStr").toString(), id);
    }

    /**
     * 删除薪资模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesModelDao.editWagesModelMationStateMationById(map.get("id").toString(), STATE.START_DELETE.getState());
    }

    /**
     * 启用薪资模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void enableWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesModelDao.editWagesModelMationStateMationById(map.get("id").toString(), STATE.START_UP.getState());
    }

    /**
     * 禁用薪资模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void disableWagesModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesModelDao.editWagesModelMationStateMationById(map.get("id").toString(), STATE.START_DOWN.getState());
    }

    /**
     * 薪资模板详细信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesModelDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = wagesModelDao.queryWagesModelMationById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        getWagesModelApplicableObjects(bean, id);
        bean.put("modelField", getWagesModelFieldsByModelId(id));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 根据模板id获取要素字段
     * @param modeId 模板id
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getWagesModelFieldsByModelId(String modeId) throws Exception {
        List<Map<String, Object>> fields = wagesModelFieldDao.queryWagesModelFieldByModelId(modeId);
        fields.forEach(bean -> {
            if(ToolUtil.isBlank(bean.get("nameCn").toString())){
                // 如果为空，则默认为系统提供的薪资字段
                bean.put("nameCn", WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getNameByKey(bean.get("fieldKey").toString()));
            }
        });
        return fields;
    }


}
