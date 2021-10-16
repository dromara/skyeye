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
import com.skyeye.eve.dao.AssetTypeDao;
import com.skyeye.eve.service.AssetTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetTypeServiceImpl
 * @Description: 资产类型管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/17 23:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetTypeServiceImpl implements AssetTypeService {
    
    @Autowired
    private AssetTypeDao assetTypeDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     * 已经上线的资产类型的redis的key
     */
    public static final String GET_ASSETTYPE_UP_LIST_ALL = "get_assettype_up_list_all";

    /**
     *
     * @Title: selectAllAssettypeMation
     * @Description: 遍历所有的资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void selectAllAssettypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = assetTypeDao.selectAllAssettypeMation(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: insertAssettypeMation
     * @Description: 新增资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertAssettypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetTypeDao.queryAssettypeMationByName(map);
        if(bean != null && !bean.isEmpty()){
            outputObject.setreturnMessage("该类型已存在。");
        }else{
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 0);
            Map<String, Object> orderBy = assetTypeDao.queryAssettypeAfterOrderBum(map);
            if(orderBy == null){
                map.put("orderBy", 1);
            }else{
                if(orderBy.containsKey("orderBy")){
                    map.put("orderBy", Integer.parseInt(orderBy.get("orderBy").toString()) + 1);
                }else{
                    map.put("orderBy", 1);
                }
            }
            map.put("createTime", DateUtil.getTimeAndToString());
            map.put("createId", inputObject.getLogParams().get("id"));
            assetTypeDao.insertAssettypeMation(map);
        }
    }

    /**
     *
     * @Title: deleteAssettypeById
     * @Description: 删除资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteAssettypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        assetTypeDao.deleteAssettypeById(map);
    }

    /**
     *
     * @Title: editAssettypeMationById
     * @Description: 查询资产类型信息用以编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssettypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetTypeDao.queryAssettypeMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editAssettypeMationById
     * @Description: 编辑资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssettypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetTypeDao.queryAssettypeMationByNameAndId(map);
        if(bean == null){
            assetTypeDao.editAssettypeMationById(map);
        }else{
            outputObject.setreturnMessage("该类型已存在。");
        }
    }

    /**
     *
     * @Title: selectAllAssettypeToChoose
     * @Description: 遍历所有的资产类型
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void selectAllAssettypeToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans;
        if(ToolUtil.isBlank(jedisClient.get(GET_ASSETTYPE_UP_LIST_ALL))){
            beans = assetTypeDao.selectAllAssettypeToChoose(map);
            jedisClient.set(GET_ASSETTYPE_UP_LIST_ALL, JSONUtil.toJsonStr(beans));
        }else{
            beans = JSONUtil.toList(jedisClient.get(GET_ASSETTYPE_UP_LIST_ALL), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: editAssettypeSortTopById
     * @Description: 资产类型展示顺序上移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssettypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> topBean = assetTypeDao.queryAssettypeISTopByThisId(map);//根据同一级排序获取这条数据的上一条数据
        if(topBean == null){
            outputObject.setreturnMessage("已经是最靠前资产类型，无法移动。");
        }else{
            map.put("orderBy", topBean.get("orderBy"));
            topBean.put("orderBy", topBean.get("thisOrderBy"));
            assetTypeDao.editAssettypeSortTopById(map);
            assetTypeDao.editAssettypeSortTopById(topBean);
            jedisClient.del(GET_ASSETTYPE_UP_LIST_ALL);
        }
    }

    /**
     *
     * @Title: editAssettypeSortLowerById
     * @Description: 资产类型展示顺序下移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssettypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> topBean = assetTypeDao.queryAssettypeISLowerByThisId(map);//根据同一级排序获取这条数据的下一条数据
        if(topBean == null){
            outputObject.setreturnMessage("已经是最靠后资产类型，无法移动。");
        }else{
            map.put("orderBy", topBean.get("orderBy"));
            topBean.put("orderBy", topBean.get("thisOrderBy"));
            assetTypeDao.editAssettypeSortLowerById(map);
            assetTypeDao.editAssettypeSortLowerById(topBean);
            jedisClient.del(GET_ASSETTYPE_UP_LIST_ALL);
        }
    }

    /**
     *
     * @Title: editAssettypeUpTypeById
     * @Description: 资产类型上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssettypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetTypeDao.queryAssettypeMationStateById(map);
        if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线的状态可以上线
            assetTypeDao.editAssettypeUpTypeById(map);
            jedisClient.del(GET_ASSETTYPE_UP_LIST_ALL);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: editAssettypeDownTypeById
     * @Description: 资产类型下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssettypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetTypeDao.queryAssettypeMationStateById(map);
        if("1".equals(bean.get("state").toString())){//上线状态可以下线
            assetTypeDao.editAssettypeDownTypeById(map);
            jedisClient.del(GET_ASSETTYPE_UP_LIST_ALL);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }
    
}
