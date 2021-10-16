/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.StoreHouseDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.StoreHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: StoreHouseServiceImpl
 * @Description: 仓库信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:46
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class StoreHouseServiceImpl implements StoreHouseService {

    @Autowired
    private StoreHouseDao storeHouseDao;
    
    @Autowired
	private JedisClientService jedisClient;

    /**
     * 获取仓库信息列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryStoreHouseByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = storeHouseDao.queryStoreHouseByList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 添加仓库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertStoreHouse(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = storeHouseDao.queryStoreHouseByName(params);
        if(bean == null){
            params.put("id", ToolUtil.getSurFaceId());
            if(params.get("isDefault").toString().equals("1")){
                params.put("isDefault", "2");
                storeHouseDao.editStoreHouseByDefaultAll(params);
                params.put("isDefault", "1");
            }
            params.put("createTime", DateUtil.getTimeAndToString());
            storeHouseDao.insertStoreHouse(params);
            jedisClient.del(ErpConstants.getStoreHouseRedisKeyByUserId());
        }else{
            outputObject.setreturnMessage("该仓库信息已存在，请确认！");
        }

    }

    /**
     * 查询单个仓库信息，用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryStoreHouseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = storeHouseDao.queryStoreHouseById(params);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("未查询到信息，请重试！");
        }else{
        	bean.put("chooseUser", storeHouseDao.queryChargeUserNameById(bean));
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }
    }

    /**
     * 删除仓库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteStoreHouseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", "1");
        storeHouseDao.editStoreHouseByDeleteFlag(params);
        jedisClient.del(ErpConstants.getStoreHouseRedisKeyByUserId());
    }

    /**
     * 编辑仓库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStoreHouseById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> houseName = storeHouseDao.queryStoreHouseByIdAndName(params);
        if(houseName != null){
            outputObject.setreturnMessage("仓库名称已存在！");
            return;
        }
        if(params.get("isDefault").toString().equals("1")){
            params.put("isDefault", "2");
            storeHouseDao.editStoreHouseByDefaultAll(params);
            params.put("isDefault", "1");
        }
        storeHouseDao.editStoreHouseById(params);
        jedisClient.del(ErpConstants.getStoreHouseRedisKeyByUserId());
    }

    /**
     * 设置仓库为默认状态
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStoreHouseByDefault(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = storeHouseDao.queryStoreHouseByIsDefault(params);
        if(bean != null){
            outputObject.setreturnMessage("状态已改变，请勿重复操作！");
            return;
        }
        params.put("isDefault", "2");
        storeHouseDao.editStoreHouseByDefaultAll(params);
        params.put("isDefault", "1");
        storeHouseDao.editStoreHouseByDefault(params);
        jedisClient.del(ErpConstants.getStoreHouseRedisKeyByUserId());
    }

    /**
     * 获取所有仓库展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queyrStoreHouseListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans;
		if(ToolUtil.isBlank(jedisClient.get(ErpConstants.getStoreHouseRedisKeyByUserId()))){//若缓存中无值
			beans = storeHouseDao.queyrStoreHouseListToSelect(map);	//从数据库中查询
			jedisClient.set(ErpConstants.getStoreHouseRedisKeyByUserId(), JSONUtil.toJsonStr(beans));//将从数据库中查来的内容存到缓存中
		}else{
			beans = JSONUtil.toList(jedisClient.get(ErpConstants.getStoreHouseRedisKeyByUserId()), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 查看仓库详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
    @Override
    public void queryStoreHouseByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = storeHouseDao.queryStoreHouseByIdAndInfo(params);
        if(bean == null || bean.isEmpty()){
            outputObject.setreturnMessage("未查询到信息！");
            return;
        }
        bean.put("chooseUser", storeHouseDao.queryChargeUserNameById(bean));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     * 获取当前登录用户管理的仓库列表展示为下拉框
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryStoreHouseListByCurrentUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		String currentUserId = inputObject.getLogParams().get("id").toString();
		List<Map<String, Object>> beans = storeHouseDao.queryStoreHouseListByCurrentUserId(currentUserId);	//从数据库中查询
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
}
