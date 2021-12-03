/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ProConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ProCostExpenseTypeDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.ProCostExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProCostExpenseTypeServiceImpl
 * @Description: 项目成本费用支出分类管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/8 12:13
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ProCostExpenseTypeServiceImpl implements ProCostExpenseTypeService {

    @Autowired
    private ProCostExpenseTypeDao proCostExpenseTypeDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     *
     * @Title: insertProCostExpenseType
     * @Description: 添加成本费用支出分类信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertProCostExpenseType(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> item = proCostExpenseTypeDao.queryProCostExpenseTypeByName(map);
        if (item == null || item.isEmpty()){
            Map<String, Object> user = inputObject.getLogParams();
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 1);
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            proCostExpenseTypeDao.insertProCostExpenseType(map);
        }else {
            outputObject.setreturnMessage("类型名称已存在！");
        }
    }

    /**
     *
     * @Title: queryProCostExpenseTypeList
     * @Description: 获取表中所有成本费用支出分类状态为未被删除的记录并分页
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryProCostExpenseTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = proCostExpenseTypeDao.queryProCostExpenseTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: queryStateUpList
     * @Description: 获取成本费用支出分类状态为上线的所有记录
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = null;
        if (ToolUtil.isBlank(jedisClient.get(ProConstants.sysProCostExpenseTypeUpStateList()))){
            beans = proCostExpenseTypeDao.queryStateUpList(map);
            jedisClient.set(ProConstants.sysProCostExpenseTypeUpStateList(), JSONUtil.toJsonStr(beans));
        }else {
            beans = JSONUtil.toList(jedisClient.get(ProConstants.sysProCostExpenseTypeUpStateList()), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: queryProCostExpenseTypeMationById
     * @Description: 通过成本费用支出分类id查询id和name
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryProCostExpenseTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= proCostExpenseTypeDao.queryProCostExpenseTypeMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editProCostExpenseTypeById
     * @Description: 编辑成本费用支出分类名称
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editProCostExpenseTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= proCostExpenseTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以编辑
            //获取名称相同但id不同的成本费用支出分类记录
            Map<String, Object> item = proCostExpenseTypeDao.queryProCostExpenseTypeByIdAndName(map);
            if (item == null || item.isEmpty()){
                proCostExpenseTypeDao.editProCostExpenseTypeById(map);
            }else{
                outputObject.setreturnMessage("类型名称已存在！");
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateUpById
     * @Description: 编辑成本费用支出分类状态为上线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= proCostExpenseTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以上线
            proCostExpenseTypeDao.editStateUpById(map);
            jedisClient.del(ProConstants.sysProCostExpenseTypeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: editStateDownById
     * @Description: 编辑成本费用支出分类状态为下线
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= proCostExpenseTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("2")){//上线状态可以下线
            proCostExpenseTypeDao.editStateDownById(map);
            jedisClient.del(ProConstants.sysProCostExpenseTypeUpStateList());
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }

    /**
     *
     * @Title: deleteProCostExpenseTypeById
     * @Description: 编辑成本费用支出分类状态为删除
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteProCostExpenseTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean= proCostExpenseTypeDao.queryStateById(map);
        if(bean.get("state").toString().equals("1") || bean.get("state").toString().equals("3")){//新建和下线状态可以删除
            proCostExpenseTypeDao.deleteProCostExpenseTypeById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面。");
        }
    }
}
