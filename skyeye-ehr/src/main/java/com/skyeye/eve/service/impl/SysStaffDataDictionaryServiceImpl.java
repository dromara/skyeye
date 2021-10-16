/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysStaffDataDictionaryDao;
import com.skyeye.eve.service.SysStaffDataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysStaffDataDictionaryServiceImpl
 * @Description: 员工基础信息相关的数据字典管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:38
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysStaffDataDictionaryServiceImpl implements SysStaffDataDictionaryService {

    @Autowired
    private SysStaffDataDictionaryDao sysStaffDataDictionaryDao;

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
     * 查询员工基础信息相关的数据字典列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysStaffDataDictionaryList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = sysStaffDataDictionaryDao.querySysStaffDataDictionaryList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertSysStaffDataDictionaryMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = sysStaffDataDictionaryDao.querySysStaffDataDictionaryByName(params);
        if(bean == null || bean.isEmpty()){
            params.put("id", ToolUtil.getSurFaceId());
            params.put("state", state.START_UP.getState());
            params.put("createId", inputObject.getLogParams().get("id"));
            params.put("createTime", DateUtil.getTimeAndToString());
            sysStaffDataDictionaryDao.insertSysStaffDataDictionaryMation(params);
        }else{
            outputObject.setreturnMessage("该数据字典已存在.");
        }
    }

    /**
     * 修改员工基础信息相关的数据字典时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysStaffDataDictionaryMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = sysStaffDataDictionaryDao.querySysStaffDataDictionaryById(params.get("id").toString());
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该数据字典信息不存在.");
        }else{
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    /**
     * 修改员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = sysStaffDataDictionaryDao.querySysStaffDataDictionaryByNameAndId(params);
        if(bean == null || bean.isEmpty()){
            params.put("lastUpdateId", inputObject.getLogParams().get("id"));
            params.put("lastUpdateTime", DateUtil.getTimeAndToString());
            sysStaffDataDictionaryDao.editSysStaffDataDictionaryMationById(params);
        }else{
            outputObject.setreturnMessage("该数据字典已存在.");
        }
    }

    /**
     * 禁用员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void downSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = sysStaffDataDictionaryDao.querySysStaffDataDictionaryStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该数据字典信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_UP.getState()){
                // 启用状态可以禁用
                sysStaffDataDictionaryDao.editSysStaffDataDictionaryStateById(id, state.START_DOWN.getState());
            }
        }
    }

    /**
     * 启用员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void upSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = sysStaffDataDictionaryDao.querySysStaffDataDictionaryStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该数据字典信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_DOWN.getState()){
                // 禁用状态可以启用
                sysStaffDataDictionaryDao.editSysStaffDataDictionaryStateById(id, state.START_UP.getState());
            }
        }
    }

    /**
     * 删除员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteSysStaffDataDictionaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = sysStaffDataDictionaryDao.querySysStaffDataDictionaryStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该数据字典信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_UP.getState() || nowState == state.START_DOWN.getState()){
                // 启用,禁用状态可以删除
                sysStaffDataDictionaryDao.editSysStaffDataDictionaryStateById(id, state.START_DELETE.getState());
            }
        }
    }

    /**
     * 获取所有启用员工基础信息相关的数据字典
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysStaffDataDictionaryUpMation(InputObject inputObject, OutputObject outputObject) throws Exception {
    	Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = sysStaffDataDictionaryDao.querySysStaffDataDictionaryUpMation(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
