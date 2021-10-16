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
import com.skyeye.eve.dao.SysStaffLanguageLevelDao;
import com.skyeye.eve.service.SysStaffLanguageLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffLanguageLevelServiceImpl
 * @Description: 语种等级管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:40
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffLanguageLevelServiceImpl implements SysStaffLanguageLevelService {

    @Autowired
    private SysStaffLanguageLevelDao sysStaffLanguageLevelDao;

    public static enum state{
        START_UP(1, "启动"),
        START_DOWN(2, "禁用"),
        START_DELETE(3, "删除");
        private int state;
        private String name;
        state(int state, String name){
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
     * 查询语种等级列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysStaffLanguageLevelList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffLanguageLevelDao.querySysStaffLanguageLevelList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertSysStaffLanguageLevelMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = sysStaffLanguageLevelDao.querySysStaffLanguageLevelByName(params);
        if(bean == null || bean.isEmpty()){
            params.put("id", ToolUtil.getSurFaceId());
            params.put("state", state.START_UP.getState());
            params.put("createId", inputObject.getLogParams().get("id"));
            params.put("createTime", DateUtil.getTimeAndToString());
            sysStaffLanguageLevelDao.insertSysStaffLanguageLevelMation(params);
        }else{
            outputObject.setreturnMessage("该语种等级已存在.");
        }
    }

    /**
     * 修改语种等级时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysStaffLanguageLevelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = sysStaffLanguageLevelDao.querySysStaffLanguageLevelById(params.get("id").toString());
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该语种等级信息不存在.");
        }else{
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    /**
     * 修改语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = sysStaffLanguageLevelDao.querySysStaffLanguageLevelByNameAndId(params);
        if(bean == null || bean.isEmpty()){
            params.put("lastUpdateId", inputObject.getLogParams().get("id"));
            params.put("lastUpdateTime", DateUtil.getTimeAndToString());
            sysStaffLanguageLevelDao.editSysStaffLanguageLevelMationById(params);
        }else{
            outputObject.setreturnMessage("该语种等级已存在.");
        }
    }

    /**
     * 禁用语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void downSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = sysStaffLanguageLevelDao.querySysStaffLanguageLevelStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该语种等级信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_UP.getState()){
                // 启用状态可以禁用
                sysStaffLanguageLevelDao.editSysStaffLanguageLevelStateById(id, state.START_DOWN.getState());
            }
        }
    }

    /**
     * 启用语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void upSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = sysStaffLanguageLevelDao.querySysStaffLanguageLevelStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该语种等级信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_DOWN.getState()){
                // 禁用状态可以启用
                sysStaffLanguageLevelDao.editSysStaffLanguageLevelStateById(id, state.START_UP.getState());
            }
        }
    }

    /**
     * 删除语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteSysStaffLanguageLevelMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = sysStaffLanguageLevelDao.querySysStaffLanguageLevelStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该语种等级信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_UP.getState() || nowState == state.START_DOWN.getState()){
                // 启用,禁用状态可以删除
                sysStaffLanguageLevelDao.editSysStaffLanguageLevelStateById(id, state.START_DELETE.getState());
            }
        }
    }

    /**
     * 获取所有启用语种等级
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysStaffLanguageLevelUpMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = sysStaffLanguageLevelDao.querySysStaffLanguageLevelUpMation(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
