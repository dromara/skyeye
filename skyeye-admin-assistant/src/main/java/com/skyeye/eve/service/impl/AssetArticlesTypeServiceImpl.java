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
import com.skyeye.eve.dao.AssetArticlesTypeDao;
import com.skyeye.eve.service.AssetArticlesTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetArticlesTypeServiceImpl
 * @Description: 用品分类服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AssetArticlesTypeServiceImpl implements AssetArticlesTypeService {

    @Autowired
    private AssetArticlesTypeDao assetArticlesTypeDao;

    @Autowired
    private JedisClientService jedisClient;

    /**
     * 已经上线的用品类别的redis的key
     */
    public static final String ASSET_ARTICLES_TYPE_UP_STATE_LIST = "asset_articles_type_up_state_list";

    /**
     *
     * @Title: queryAssetArticlesTypeList
     * @Description: 查出所有用品类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetArticlesTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = assetArticlesTypeDao.queryAssetArticlesTypeList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     *
     * @Title: insertAssetArticlesType
     * @Description: 新增用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void insertAssetArticlesType(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeMationByName(map);
        if(bean != null && !bean.isEmpty()){
            outputObject.setreturnMessage("该用品类别名称已存在，请更换");
        }else{
            Map<String, Object> itemCount = assetArticlesTypeDao.queryAssetArticlesTypeBySimpleLevel(map);
            Map<String, Object> user = inputObject.getLogParams();
            int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
            map.put("orderBy", thisOrderBy);
            map.put("id", ToolUtil.getSurFaceId());
            map.put("state", "0");//默认新建
            map.put("createId", user.get("id"));
            map.put("createTime", DateUtil.getTimeAndToString());
            assetArticlesTypeDao.insertAssetArticlesType(map);
        }
    }

    /**
     *
     * @Title: deleteAssetArticlesTypeById
     * @Description: 删除用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeStateById(map);
        if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线可以删除
            assetArticlesTypeDao.deleteAssetArticlesTypeById(map);
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateUpAssetArticlesTypeById
     * @Description: 上线用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateUpAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeStateById(map);
        if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线可以上线
            assetArticlesTypeDao.updateUpAssetArticlesTypeById(map);
            jedisClient.del(ASSET_ARTICLES_TYPE_UP_STATE_LIST);//删除上线用品类别的redis
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: updateDownAssetArticlesTypeById
     * @Description: 下线用品类别
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void updateDownAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeStateById(map);
        if("1".equals(bean.get("state").toString())){//上线状态可以下线
            assetArticlesTypeDao.updateDownAssetArticlesTypeById(map);
            jedisClient.del(ASSET_ARTICLES_TYPE_UP_STATE_LIST);//删除上线用品类别的redis
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: selectAssetArticlesTypeById
     * @Description: 通过id查找对应的用品类别信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void selectAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object>	bean = assetArticlesTypeDao.selectAssetArticlesTypeById(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

    /**
     *
     * @Title: editAssetArticlesTypeById
     * @Description: 通过id编辑对应的用品类别信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeStateById(map);
        if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线可以编辑
            Map<String, Object> b = assetArticlesTypeDao.queryAssetArticlesTypeMationByName(map);
            if(b != null && !b.isEmpty()){
                outputObject.setreturnMessage("该用品类别名称已存在，请更换");
            }else{
                assetArticlesTypeDao.editAssetArticlesTypeById(map);
            }
        }else{
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     *
     * @Title: editAssetArticlesTypeOrderUpById
     * @Description: 用品类别上移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetArticlesTypeOrderUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeUpMationById(map);//获取当前数据的同级分类下的上一条数据
        if(bean == null){
            outputObject.setreturnMessage("当前用品类别已经是首位，无须进行上移。");
        }else{
            //进行位置交换
            map.put("upOrderBy", bean.get("prevOrderBy"));
            bean.put("upOrderBy", bean.get("thisOrderBy"));
            assetArticlesTypeDao.editAssetArticlesTypeOrderUpById(map);
            assetArticlesTypeDao.editAssetArticlesTypeOrderUpById(bean);
            jedisClient.del(ASSET_ARTICLES_TYPE_UP_STATE_LIST);//删除上线用品类别的redis
        }
    }

    /**
     *
     * @Title: editAssetArticlesTypeOrderDownById
     * @Description: 用品类别下移
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetArticlesTypeOrderDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesTypeDao.queryAssetArticlesTypeDownMationById(map);//获取当前数据的同级分类下的下一条数据
        if(bean == null){
            outputObject.setreturnMessage("当前用品类别已经是末位，无须进行下移。");
        }else{
            //进行位置交换
            map.put("upOrderBy", bean.get("prevOrderBy"));
            bean.put("upOrderBy", bean.get("thisOrderBy"));
            assetArticlesTypeDao.editAssetArticlesTypeOrderUpById(map);
            assetArticlesTypeDao.editAssetArticlesTypeOrderUpById(bean);
            jedisClient.del(ASSET_ARTICLES_TYPE_UP_STATE_LIST);//删除上线用品类别的redis
        }
    }

    /**
     *
     * @Title: queryAssetArticlesTypeUpStateList
     * @Description: 获取已经上线的用品类别列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryAssetArticlesTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = new ArrayList<>();
        if(ToolUtil.isBlank(jedisClient.get(ASSET_ARTICLES_TYPE_UP_STATE_LIST))){
            beans = assetArticlesTypeDao.queryAssetArticlesTypeUpStateList(map);
            jedisClient.set(ASSET_ARTICLES_TYPE_UP_STATE_LIST, JSONUtil.toJsonStr(beans));
        }else{
            beans = JSONUtil.toList(jedisClient.get(ASSET_ARTICLES_TYPE_UP_STATE_LIST), null);
        }
        if(!beans.isEmpty()){
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }

}
