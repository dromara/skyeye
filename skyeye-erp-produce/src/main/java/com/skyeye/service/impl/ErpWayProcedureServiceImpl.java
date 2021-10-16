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
import com.skyeye.dao.ErpWayProcedureDao;
import com.skyeye.service.ErpWayProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpWayProcedureServiceImpl
 * @Description: 工艺路线管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/5 21:24
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpWayProcedureServiceImpl implements ErpWayProcedureService {
	
	@Autowired
	private ErpWayProcedureDao erpWayProcedureDao;

	public static enum state{
        START_STOP(1, "停用"),
        START_RUNING(2, "启用"),
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
     * 查询工艺路线列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpWayProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpWayProcedureDao.queryErpWayProcedureList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
     * 新增工艺路线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void insertErpWayProcedureMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		// 判断工艺名称或者编号是否存在
		Map<String, Object> bean = erpWayProcedureDao.queryErpWayProcedureByNameOrNumber(params);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该工艺名称或编号已存在.");
		}else{
			String wayProcedureId = ToolUtil.getSurFaceId();
			params.put("id", wayProcedureId);
			params.put("state", state.START_RUNING.getState());
			params.put("createId", inputObject.getLogParams().get("id"));
			params.put("createTime", DateUtil.getTimeAndToString());
			erpWayProcedureDao.insertErpWayProcedureMation(params);
			// 工序信息处理
			handleWayProcedure(params.get("procedureMation").toString(), wayProcedureId);
		}
	}
	
	/**
	 * 处理工序信息
	 * @param procedureMation 工序信息，逗号隔开
	 * @param wayProcedureId 工艺id
	 */
	private void handleWayProcedure(String procedureMation, String wayProcedureId) throws Exception {
		erpWayProcedureDao.deleteProcedureByWayProcedureId(wayProcedureId);
		if(!ToolUtil.isBlank(procedureMation)){
			List<Map<String, Object>> beans = new ArrayList<>();
            List<Map<String, Object>> array = JSONUtil.toList(procedureMation, null);
            for(int i = 0; i< array.size(); i++){
            	Map<String, Object> item = new HashMap<>();
            	item.put("id", ToolUtil.getSurFaceId());
            	item.put("procedureId", array.get(i).get("procedureId"));
            	item.put("wayProcedureId", wayProcedureId);
            	item.put("orderBy", (i + 1));
            	item.put("farmId", array.get(i).get("farmId"));
            	beans.add(item);
            }
            if(!beans.isEmpty()){
            	erpWayProcedureDao.insertWayProcedureList(beans);
            }else{
            	throw new Exception("请至少选择一个工序");
            }
		}
	}

	/**
     * 修改工艺时数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryErpWayProcedureMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        String wayProcedureId = params.get("id").toString();
        Map<String, Object> bean = erpWayProcedureDao.queryErpWayProcedureToEditById(wayProcedureId);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该工艺信息不存在.");
        }else{
            List<Map<String, Object>> procedureList = erpWayProcedureDao.queryProcedureListByWayProcedureId(wayProcedureId);
            bean.put("procedureList", procedureList);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
	}

	/**
     * 修改工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void editErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = erpWayProcedureDao.queryErpWayProcedureByNameAndId(params);
        if(bean == null || bean.isEmpty()){
            String farmId = params.get("id").toString();
            params.put("lastUpdateId", inputObject.getLogParams().get("id"));
            params.put("lastUpdateTime", DateUtil.getTimeAndToString());
            erpWayProcedureDao.editErpWayProcedureMationById(params);
            // 工序信息处理
            handleWayProcedure(params.get("procedureMation").toString(), farmId);
        }else{
            outputObject.setreturnMessage("该工艺名称或编号已存在.");
        }
	}

	/**
     * 禁用工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void downErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpWayProcedureDao.queryErpFarmMationStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该工艺信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_RUNING.getState()){
                // 启用状态可以禁用
            	erpWayProcedureDao.editErpFarmMationStateById(id, state.START_STOP.getState());
            }
        }
	}

	/**
     * 启用工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void upErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpWayProcedureDao.queryErpFarmMationStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该工艺信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_STOP.getState()){
                // 禁用状态可以启用
            	erpWayProcedureDao.editErpFarmMationStateById(id, state.START_RUNING.getState());
            }
        }
	}

	/**
     * 删除工艺
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	@Transactional(value="transactionManager")
	public void deleteErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Map<String, Object> bean = erpWayProcedureDao.queryErpFarmMationStateById(id);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该工艺信息不存在");
        }else{
            int nowState = Integer.parseInt(bean.get("state").toString());
            if(nowState == state.START_RUNING.getState() || nowState == state.START_STOP.getState()){
                // 启用,停用状态可以删除
            	erpWayProcedureDao.editErpFarmMationStateById(id, state.START_DELETE.getState());
            }
        }
	}

	/**
	 * 
	 * Title: queryErpWayProcedureDetailsMationById
	 * Description: 获取工艺详细信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 * @see com.skyeye.service.ErpWayProcedureService#queryErpWayProcedureDetailsMationById(com.skyeye.common.object.InputObject, com.skyeye.common.object.OutputObject)
	 */
	@Override
	public void queryErpWayProcedureDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        String wayProcedureId = params.get("id").toString();
        Map<String, Object> bean = erpWayProcedureDao.queryErpWayProcedureDetailsById(wayProcedureId);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("该工艺信息不存在.");
        }else{
            List<Map<String, Object>> procedureList = erpWayProcedureDao.queryProcedureListByWayProcedureId(wayProcedureId);
            bean.put("procedureList", procedureList);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
	}

	/**
	 * 查询启用的工艺列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryErpWayRuningProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
		// 查询状态为启用的工艺列表
		params.put("state", state.START_RUNING.getState());
		Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
		List<Map<String, Object>> beans = erpWayProcedureDao.queryErpWayProcedureList(params);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

}
