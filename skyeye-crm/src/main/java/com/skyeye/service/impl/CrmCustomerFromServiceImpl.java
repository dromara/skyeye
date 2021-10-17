/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CrmConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.CrmCustomerFromDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.CrmCustomerFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CrmCustomerFromServiceImpl
 * @Description: 客户来源管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CrmCustomerFromServiceImpl implements CrmCustomerFromService {

    @Autowired
    private CrmCustomerFromDao crmCustomerFromDao;

    @Autowired
    private JedisClientService jedisClient;

    public static enum STATE{
        START_NEW(1, "新建"),
        START_UP(2, "上线"),
        START_DOWN(3, "下线"),
        START_DELETE(4, "删除");
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
     *
     * @Title: insertCrmCustomerFrom
     * @Description: 添加客户来源信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertCrmCustomerFrom(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = crmCustomerFromDao.queryCrmCustomerFromByName(map);
        if (item == null || item.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            crmCustomerFromDao.insertCrmCustomerFrom(map);
        }else {
            outputObject.setreturnMessage("客户来源已存在！");
        }
    }

    /**
     *
     * @Title: queryCrmCustomerFromList
     * @Description: 获取表中所有客户来源状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCrmCustomerFromList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = crmCustomerFromDao.queryCrmCustomerFromList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取客户来源状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(CrmConstants.SYS_CUSTOMER_FROM_UP_STATE_LIST))){
            beans = crmCustomerFromDao.queryStateUpList(map);
            jedisClient.set(CrmConstants.SYS_CUSTOMER_FROM_UP_STATE_LIST, JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(CrmConstants.SYS_CUSTOMER_FROM_UP_STATE_LIST), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: queryCrmCustomerFromMationById
     * @Description: 通过客户来源表id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryCrmCustomerFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerFromDao.queryCrmCustomerFromMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editCrmCustomerFromById
     * @Description: 编辑客户来源名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editCrmCustomerFromById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerFromDao.queryStateById(map);
        Integer state = Integer.parseInt(bean.get("state").toString());
        if(state == STATE.START_NEW.getState() || state == STATE.START_DOWN.getState()){
            // 新建和下线状态可以编辑
            // 获取名称相同但id不同的客户来源记录
            Map<String, Object> item = crmCustomerFromDao.queryCrmCustomerFromByIdAndName(map);
            if (item == null || item.isEmpty()){
                crmCustomerFromDao.editCrmCustomerFromById(map);
            }else{
                outputObject.setreturnMessage("客户来源已存在！");
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑客户来源状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerFromDao.queryStateById(map);
        Integer state = Integer.parseInt(bean.get("state").toString());
        if(state == STATE.START_NEW.getState() || state == STATE.START_DOWN.getState()){
            // 新建和下线状态可以上线
            crmCustomerFromDao.editStateUpById(map);
            jedisClient.del(CrmConstants.SYS_CUSTOMER_FROM_UP_STATE_LIST);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑客户来源状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerFromDao.queryStateById(map);
        Integer state = Integer.parseInt(bean.get("state").toString());
        if(state == STATE.START_UP.getState()){
            // 上线状态可以下线
            crmCustomerFromDao.editStateDownById(map);
            jedisClient.del(CrmConstants.SYS_CUSTOMER_FROM_UP_STATE_LIST);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteCrmCustomerFromById
     * @Description: 编辑客户来源状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteCrmCustomerFromById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= crmCustomerFromDao.queryStateById(map);
        Integer state = Integer.parseInt(bean.get("state").toString());
        if(state == STATE.START_NEW.getState() || state == STATE.START_DOWN.getState()){
            // 新建和下线状态可以删除
            crmCustomerFromDao.deleteCrmCustomerFromById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
