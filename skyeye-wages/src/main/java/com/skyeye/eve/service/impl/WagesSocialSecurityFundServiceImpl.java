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
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.WagesSocialSecurityFundApplicableObjectsDao;
import com.skyeye.eve.dao.WagesSocialSecurityFundDao;
import com.skyeye.eve.service.WagesSocialSecurityFundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WagesSocialSecurityFundServiceImpl implements WagesSocialSecurityFundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WagesSocialSecurityFundServiceImpl.class);

    @Autowired
    private WagesSocialSecurityFundDao wagesSocialSecurityFundDao;

    @Autowired
    private WagesSocialSecurityFundApplicableObjectsDao wagesSocialSecurityFundApplicableObjectsDao;

    @Autowired
    private SysEnclosureDao sysEnclosureDao;

    /**
     * 社保公积金状态
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
     * 获取社保公积金模板列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesSocialSecurityFundList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = wagesSocialSecurityFundDao.queryWagesSocialSecurityFundList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增社保公积金模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertWagesSocialSecurityFundMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> mation = wagesSocialSecurityFundDao.queryWagesSocialSecurityFundMationByTitleAndNotId(map.get("title").toString(), null);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same name exists, please replace it.");
            return;
        }
        String id = ToolUtil.getSurFaceId();
        map.put("createId", inputObject.getLogParams().get("id"));
        map.put("createTime", DateUtil.getTimeAndToString());
        map.put("state", STATE.START_UP.getState());
        map.put("id", id);
        wagesSocialSecurityFundDao.insertWagesSocialSecurityFundMation(map);
        // 处理社保公积金使用对象信息
        wagesSocialSecurityFundApplicableObjects(map.get("str").toString(), id);
    }

    /**
     * 处理社保公积金使用对象信息
     *
     * @param str
     * @param id
     */
    private void wagesSocialSecurityFundApplicableObjects(String str, String id) throws Exception {
        wagesSocialSecurityFundApplicableObjectsDao.deleteWagesSocialSecurityFundApplicableObjectsBySecurityFundId(id);
        if(ToolUtil.isBlank(str)){
            return;
        }
        List<Map<String, Object>> applicableObjects = JSONUtil.toList(JSONUtil.parseArray(str), null);
        applicableObjects.stream().forEach(bean -> {
            bean.put("securityFundId", id);
        });
        if(applicableObjects.isEmpty()){
            return;
        }
        wagesSocialSecurityFundApplicableObjectsDao.insertWagesSocialSecurityFundApplicableObjects(applicableObjects);
    }

    /**
     * 编辑社保公积金模板信息时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesSocialSecurityFundMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = wagesSocialSecurityFundDao.queryWagesSocialSecurityFundMationById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosure").toString()));
        getWagesSocialSecurityFundApplicableObjects(bean, id);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 社保公积金使用对象信息
     *
     * @param bean
     * @param id
     * @throws Exception
     */
    private void getWagesSocialSecurityFundApplicableObjects(Map<String, Object> bean, String id) throws Exception {
        List<Map<String, Object>> applicableObjects = wagesSocialSecurityFundApplicableObjectsDao.queryWagesSocialSecurityFundApplicableObjectsBySecurityFundId(id);
        // 根据objectType分组
        Map<String, List<Map<String, Object>>> groupByType = applicableObjects.stream()
                .collect(Collectors.groupingBy(this::customKey));
        groupByType.forEach((key, value) -> {
            List<String> ids = value.stream().map(p -> p.get("objectId").toString()).collect(Collectors.toList());
            if("1".equals(key)){
                // 员工
                try {
                    List<Map<String, Object>> userStaff = wagesSocialSecurityFundDao.queryStaffNameListByStaffIdList(ids);
                    bean.put("userStaff", userStaff);
                } catch (Exception e) {
                    LOGGER.info("get securityFundId {}'s userStaff failed.{}", e);
                }
            }else if("2".equals(key)){
                // 部门
                try {
                    List<Map<String, Object>> departMent = wagesSocialSecurityFundDao.queryDepartMentNameListByDepartMentIdList(ids);
                    bean.put("departMent", departMent);
                } catch (Exception e) {
                    LOGGER.info("get securityFundId {}'s userStaff failed.{}", e);
                }
            }else if("3".equals(key)){
                // 企业
                try {
                    List<Map<String, Object>> company = wagesSocialSecurityFundDao.queryCompanyNameListByCompanyIdList(ids);
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
     * 编辑社保公积金模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> mation = wagesSocialSecurityFundDao.queryWagesSocialSecurityFundMationByTitleAndNotId(map.get("title").toString(), id);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same name exists, please replace it.");
            return;
        }
        map.put("lastUpdateId", inputObject.getLogParams().get("id"));
        map.put("lastUpdateTime", DateUtil.getTimeAndToString());
        wagesSocialSecurityFundDao.editWagesSocialSecurityFundMationById(map);
        // 处理社保公积金使用对象信息
        wagesSocialSecurityFundApplicableObjects(map.get("str").toString(), id);
    }

    /**
     * 删除社保公积金模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void deleteWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesSocialSecurityFundDao.editWagesSocialSecurityFundStateMationById(map.get("id").toString(), STATE.START_DELETE.getState());
    }

    /**
     * 启用社保公积金模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void enableWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesSocialSecurityFundDao.editWagesSocialSecurityFundStateMationById(map.get("id").toString(), STATE.START_UP.getState());
    }

    /**
     * 禁用社保公积金模板信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void disableWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        wagesSocialSecurityFundDao.editWagesSocialSecurityFundStateMationById(map.get("id").toString(), STATE.START_DOWN.getState());
    }

    /**
     * 社保公积金模板详情信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = wagesSocialSecurityFundDao.queryWagesSocialSecurityFundMationById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosure").toString()));
        getWagesSocialSecurityFundApplicableObjects(bean, id);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

}
