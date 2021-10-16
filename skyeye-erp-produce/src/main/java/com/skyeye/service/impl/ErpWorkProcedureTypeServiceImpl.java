/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpWorkProcedureTypeDao;
import com.skyeye.service.ErpWorkProcedureTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpWorkProcedureTypeServiceImpl
 * @Description: 工序类别管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:49
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpWorkProcedureTypeServiceImpl implements ErpWorkProcedureTypeService {

    @Autowired
    private ErpWorkProcedureTypeDao erpWorkProcedureTypeDao;

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
     * 查询工序类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryErpWorkProcedureTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertErpWorkProcedureTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeByName(params);
        if(bean == null || bean.isEmpty()){
            params.put("id", ToolUtil.getSurFaceId());
            params.put("state", state.START_UP.getState());
            params.put("createId", inputObject.getLogParams().get("id"));
            params.put("createTime", DateUtil.getTimeAndToString());
            erpWorkProcedureTypeDao.insertErpWorkProcedureTypeMation(params);
        }else{
            outputObject.setreturnMessage("该类别已存在.");
        }
    }

    /**
     * 修改工序类别时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryErpWorkProcedureTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeById(params.get("id").toString());
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该类别信息不存在.");
        }else{
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    /**
     * 修改工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeByNameAndId(params);
        if(bean == null || bean.isEmpty()){
            params.put("lastUpdateId", inputObject.getLogParams().get("id"));
            params.put("lastUpdateTime", DateUtil.getTimeAndToString());
            erpWorkProcedureTypeDao.editErpWorkProcedureTypeMationById(params);
        }else{
            outputObject.setreturnMessage("该类别已存在.");
        }
    }

    /**
     * 禁用工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void downErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该类别信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_UP.getState()){
                // 启用状态可以禁用
                erpWorkProcedureTypeDao.editErpWorkProcedureTypeStateById(id, state.START_DOWN.getState());
            }
        }
    }

    /**
     * 启用工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void upErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该类别信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_DOWN.getState()){
                // 禁用状态可以启用
                erpWorkProcedureTypeDao.editErpWorkProcedureTypeStateById(id, state.START_UP.getState());
            }
        }
    }

    /**
     * 删除工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该类别信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_UP.getState() || nowState == state.START_DOWN.getState()){
                // 启用,禁用状态可以删除
                erpWorkProcedureTypeDao.editErpWorkProcedureTypeStateById(id, state.START_DELETE.getState());
            }
        }
    }

    /**
     * 获取所有启用工序类别
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryErpWorkProcedureTypeUpMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        List<Map<String, Object>> beans = erpWorkProcedureTypeDao.queryErpWorkProcedureTypeUpMation();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
