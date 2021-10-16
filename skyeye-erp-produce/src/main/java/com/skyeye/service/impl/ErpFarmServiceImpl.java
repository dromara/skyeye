/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ErpFarmDao;
import com.skyeye.service.ErpFarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @ClassName: ErpFarmServiceImpl
 * @Description: 车间管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpFarmServiceImpl implements ErpFarmService {

    @Autowired
    private ErpFarmDao erpFarmDao;

    public static enum state{
        START_NORMAL(1, "正常"),
        START_RECTIFICATION(2, "维修整改"),
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
     * 获取车间列表
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void queryErpFarmList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpFarmDao.queryErpFarmList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 新增车间
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void insertErpFarmMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = erpFarmDao.queryErpFarmMationByName(params);
        if(bean == null || bean.isEmpty()){
            String farmId = ToolUtil.getSurFaceId();
            params.put("id", farmId);
            params.put("state", state.START_NORMAL.getState());
            params.put("createId", inputObject.getLogParams().get("id"));
            params.put("createTime", DateUtil.getTimeAndToString());
            erpFarmDao.insertErpFarmMation(params);
            // 处理车间工序信息
            handleFarmProcedure(params.get("farmProcedure").toString(), farmId);
        }else{
            outputObject.setreturnMessage("该编号或者名称已存在.");
        }
    }

    /**
     * 处理车间工序信息
     * @param farmProcedure 工序json串
     * @param farmId 车间id
     */
	private void handleFarmProcedure(String farmProcedure, String farmId) throws Exception {
        erpFarmDao.deleteFarmProcedureByFarmId(farmId);
        if(!ToolUtil.isBlank(farmProcedure)){
            List<Map<String, Object>> beans = new ArrayList<>();
            List<Map<String, Object>> array = JSONUtil.toList(farmProcedure, null);
            array.forEach(bean -> {
                Map<String, Object> item = new HashMap<>();
                item.put("procedureId", bean.get("procedureId"));
                item.put("state", 2);
                item.put("farmId", farmId);
                beans.add(item);
            });
            if(!beans.isEmpty()){
                erpFarmDao.insertFarmProcedureList(beans);
            }
        }
    }

    /**
     * 编辑车间信息时回显
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void queryErpFarmToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String farmId = params.get("id").toString();
        Map<String, Object> bean = erpFarmDao.queryErpFarmToEditById(farmId);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该车间信息不存在.");
        }else{
            List<Map<String, Object>> procedureList = erpFarmDao.queryProcedureListByFarmId(farmId);
            bean.put("procedureList", procedureList);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    /**
     * 编辑车间信息
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void editErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = erpFarmDao.queryErpFarmByNameAndId(params);
        if(bean == null || bean.isEmpty()){
            String farmId = params.get("id").toString();
            params.put("lastUpdateId", inputObject.getLogParams().get("id"));
            params.put("lastUpdateTime", DateUtil.getTimeAndToString());
            erpFarmDao.editErpFarmMationById(params);
            // 处理车间工序信息
            handleFarmProcedure(params.get("farmProcedure").toString(), farmId);
        }else{
            outputObject.setreturnMessage("该编号或者名称已存在.");
        }
    }

    /**
     * 删除车间信息
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void deleteErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpFarmDao.queryErpFarmMationStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该车间信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_RECTIFICATION.getState() || nowState == state.START_NORMAL.getState()){
                // 正常,维修整改状态可以删除
                erpFarmDao.editErpFarmMationStateById(id, state.START_DELETE.getState());
            }
        }
    }

    /**
     * 获取车间详情信息
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void queryErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String farmId = params.get("id").toString();
        Map<String, Object> bean = erpFarmDao.queryErpFarmMationById(farmId);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该车间信息不存在.");
        }else{
            List<Map<String, Object>> procedureList = erpFarmDao.queryProcedureListByFarmId(farmId);
            bean.put("procedureList", procedureList);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    /**
     * 根据工序id获取车间列表
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void queryErpFarmListByProcedureId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> beans = erpFarmDao.queryErpFarmListByProcedureId(params);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 修改车间信息为正常
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void normalErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpFarmDao.queryErpFarmMationStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该车间信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_RECTIFICATION.getState()){
                // 维修整改状态可以正常
                erpFarmDao.editErpFarmMationStateById(id, state.START_NORMAL.getState());
            }
        }
    }

    /**
     * 修改车间信息为维修整改
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
    @Override
    public void rectificationErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpFarmDao.queryErpFarmMationStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该车间信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_NORMAL.getState()){
                // 正常状态可以维修整改
                erpFarmDao.editErpFarmMationStateById(id, state.START_RECTIFICATION.getState());
            }
        }
    }

    /**
     * 查询车间列表展示为表格供其他选择
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
	@Override
	public void queryErpFarmListToTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpFarmDao.queryErpFarmListToTable(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
	}

	/**
     * 根据车间id串获取车间列表
     * @param inputObject 入参
     * @param outputObject 出参
     * @throws Exception
     */
	@Override
	public void queryErpFarmProcedureListByIds(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<String> idsList = Arrays.asList(map.get("ids").toString().split(","));
		List<Map<String, Object>> beans = new ArrayList<>();
		if(!idsList.isEmpty()){
			beans = erpFarmDao.queryErpFarmProcedureListByIds(idsList);
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}else{
			outputObject.setBeans(beans);
		}
	}
}
