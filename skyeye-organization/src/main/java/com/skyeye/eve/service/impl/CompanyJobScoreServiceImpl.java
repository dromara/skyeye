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
import com.skyeye.eve.dao.CompanyJobScoreDao;
import com.skyeye.eve.dao.CompanyJobScoreFieldDao;
import com.skyeye.eve.service.CompanyJobScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CompanyJobScoreServiceImpl
 * @Description: 职位定级信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:57
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CompanyJobScoreServiceImpl implements CompanyJobScoreService {

    @Autowired
    private CompanyJobScoreDao companyJobScoreDao;

    @Autowired
    private CompanyJobScoreFieldDao companyJobScoreFieldDao;

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
     * 获取职位定级信息列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCompanyJobScoreList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = companyJobScoreDao.queryCompanyJobScoreList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增职位定级信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertCompanyJobScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 根据判断该名称是否存在
        Map<String, Object> mation = companyJobScoreDao.queryCompanyJobScoreByNameAndNotId(map.get("nameCn").toString(), map.get("jobId").toString(), null);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same name exists, please replace it.");
            return;
        }
        String id = ToolUtil.getSurFaceId();
        // 将该职位定级插入数据库
        map.put("id", id);
        map.put("createId", inputObject.getLogParams().get("id"));
        map.put("createTime", DateUtil.getTimeAndToString());
        // 默认启用
        map.put("state", STATE.START_UP.getState());
        companyJobScoreDao.insertCompanyJobScoreMation(map);
        // 处理薪资模板字段属性信息
        companyJobScoreField(map.get("fieldStr").toString(), id);
    }

    /**
     * 处理职位等级薪资字段属性信息
     *
     * @param str
     * @param id
     */
    private void companyJobScoreField(String str, String id) throws Exception {
        companyJobScoreFieldDao.deleteCompanyJobScoreFieldByJobScoreId(id);
        if(ToolUtil.isBlank(str)){
            return;
        }
        List<Map<String, Object>> field = JSONUtil.toList(JSONUtil.parseArray(str), null);
        field.stream().forEach(bean -> {
            bean.put("jobScoreId", id);
        });
        if(field.isEmpty()){
            return;
        }
        companyJobScoreFieldDao.insertCompanyJobScoreField(field);
    }

    /**
     * 编辑职位定级信息时进行回显
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCompanyJobScoreMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = companyJobScoreDao.queryCompanyJobScoreMationById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        bean.put("modelField", companyJobScoreFieldDao.queryCompanyJobScoreFieldByJobScoreId(id));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 编辑职位定级信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> mation = companyJobScoreDao.queryCompanyJobScoreByNameAndNotId(map.get("nameCn").toString(), map.get("jobId").toString(), id);
        if(mation != null && !mation.isEmpty()){
            outputObject.setreturnMessage("The same name exists, please replace it.");
            return;
        }
        map.put("lastUpdateId", inputObject.getLogParams().get("id"));
        map.put("lastUpdateTime", DateUtil.getTimeAndToString());
        companyJobScoreDao.editCompanyJobScoreMationById(map);
        // 处理薪资模板字段属性信息
        companyJobScoreField(map.get("fieldStr").toString(), id);
    }

    /**
     * 删除职位定级信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        companyJobScoreDao.editCompanyJobScoreStateMationById(map.get("id").toString(), STATE.START_DELETE.getState());
    }

    /**
     * 启用职位定级信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void enableCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        companyJobScoreDao.editCompanyJobScoreStateMationById(map.get("id").toString(), STATE.START_UP.getState());
    }

    /**
     * 禁用职位定级信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void disableCompanyJobScoreMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        companyJobScoreDao.editCompanyJobScoreStateMationById(map.get("id").toString(), STATE.START_DOWN.getState());
    }

    /**
     * 获取已经启用的职位定级信息列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryEnableCompanyJobScoreList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = companyJobScoreDao.queryEnableCompanyJobScoreList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取职位定级信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCompanyJobScoreDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Map<String, Object> bean = companyJobScoreDao.queryCompanyJobScoreMationById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("The data does not exist.");
            return;
        }
        bean.put("modelField", companyJobScoreFieldDao.queryCompanyJobScoreFieldByJobScoreId(id));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }
}
