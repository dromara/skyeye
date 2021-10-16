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
import com.skyeye.eve.dao.AssetFromDao;
import com.skyeye.eve.service.AssetFromService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetFromServiceImpl
 * @Description: 资产来源服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:50
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetFromServiceImpl implements AssetFromService {
    
    @Autowired
    private AssetFromDao assetFromDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     * 已经上线的资产来源的redis的key
     */
    public static final String GET_ASSETFROM_UP_LIST_ALL = "get_assetfrom_up_list_all";

    /**
     *
     * @Title: selectAllAssetFromMation
     * @Description: 遍历所有的资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void selectAllAssetFromMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = assetFromDao.selectAllAssetFromMation(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: insertAssetFromMation
     * @Description: 新增资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertAssetFromMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetFromDao.queryAssetFromMationByName(map);
        if(bean != null && !bean.isEmpty()){
            outputObject.setreturnMessage("该来源已存在，");
        }else{
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", 0);
            Map<String, Object> orderBy = assetFromDao.queryAssetFromAfterOrderBum(map);
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
            assetFromDao.insertAssetFromMation(map);
        }
    }

    /**
     *
     * @Title: deleteAssetFromById
     * @Description: 删除资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteAssetFromById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        assetFromDao.deleteAssetFromById(map);
    }

    /**
     *
     * @Title: editAssetFromMationById
     * @Description: 查询资产来源信息用以编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetFromDao.queryAssetFromMationById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editAssetFromMationById
     * @Description: 编辑资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetFromDao.queryAssetFromMationByNameAndId(map);
        if(bean == null){
            assetFromDao.editAssetFromMationById(map);
        }else{
            outputObject.setreturnMessage("该资产已存在，");
        }
    }

    /**
     *
     * @Title: selectAllAssetFromToChoose
     * @Description: 遍历所有的资产来源
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void selectAllAssetFromToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans;
        if(ToolUtil.isBlank(jedisClient.get(GET_ASSETFROM_UP_LIST_ALL))){
            beans = assetFromDao.selectAllAssetFromToChoose(map);
            jedisClient.set(GET_ASSETFROM_UP_LIST_ALL, JSONUtil.toJsonStr(beans));
        }else{
            beans = JSONUtil.toList(jedisClient.get(GET_ASSETFROM_UP_LIST_ALL), null);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     *
     * @Title: editAssetFromSortTopById
     * @Description: 资产来源展示顺序上移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetFromSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 根据同一级排序获取这条数据的上一条数据
        Map<String, Object> topBean = assetFromDao.queryAssetFromISTopByThisId(map);
        if(topBean == null){
            outputObject.setreturnMessage("已经是最靠前资产来源，无法移动。");
        }else{
            map.put("orderBy", topBean.get("orderBy"));
            topBean.put("orderBy", topBean.get("thisOrderBy"));
            assetFromDao.editAssetFromSortTopById(map);
            assetFromDao.editAssetFromSortTopById(topBean);
            jedisClient.del(GET_ASSETFROM_UP_LIST_ALL);
        }
    }

    /**
     *
     * @Title: editAssetFromSortLowerById
     * @Description: 资产来源展示顺序下移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetFromSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 根据同一级排序获取这条数据的下一条数据
        Map<String, Object> topBean = assetFromDao.queryAssetFromISLowerByThisId(map);
        if(topBean == null){
            outputObject.setreturnMessage("已经是最靠后资产来源，无法移动。");
        }else{
            map.put("orderBy", topBean.get("orderBy"));
            topBean.put("orderBy", topBean.get("thisOrderBy"));
            assetFromDao.editAssetFromSortLowerById(map);
            assetFromDao.editAssetFromSortLowerById(topBean);
            jedisClient.del(GET_ASSETFROM_UP_LIST_ALL);
        }
    }

    /**
     *
     * @Title: editAssetFromUpTypeById
     * @Description: 资产来源上线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetFromUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetFromDao.queryAssetFromMationStateById(map);
        if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){
            // 新建或者下线的状态可以上线
            assetFromDao.editAssetFromUpTypeById(map);
            jedisClient.del(GET_ASSETFROM_UP_LIST_ALL);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: editAssetFromDownTypeById
     * @Description: 资产来源下线
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetFromDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetFromDao.queryAssetFromMationStateById(map);
        if("1".equals(bean.get("state").toString())){
            // 上线状态可以下线
            assetFromDao.editAssetFromDownTypeById(map);
            jedisClient.del(GET_ASSETFROM_UP_LIST_ALL);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }
    
}
