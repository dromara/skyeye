/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.AssetArticlesDao;
import com.skyeye.eve.dao.SysEnclosureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.eve.service.AssetArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AssetArticlesServiceImpl
 * @Description: 用品管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:07
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class AssetArticlesServiceImpl implements AssetArticlesService {

	@Autowired
	private AssetArticlesDao assetArticlesDao;
	
	@Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

	@Autowired
	private SysEnclosureDao sysEnclosureDao;

	/**
	 * 
	     * @Title: insertAssetArticles
	     * @Description: 新增用品
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertAssetArticles(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
        map.put("id", ToolUtil.getSurFaceId());
        String str = ToolUtil.chineseToFirstLetter(map.get("articlesName").toString());
        if(str.length()>10){
            map.put("articlesNum", str.substring(0, 10) + ToolUtil.getUniqueKey());
		}else{
			map.put("articlesNum", str + ToolUtil.getUniqueKey());
		}
        Map<String, Object> m = assetArticlesDao.queryUserCompanyById(user);
        map.put("companyId", m.get("companyId"));
        map.put("residualNum", map.get("initialNum"));
        map.put("createId", user.get("id"));
        map.put("createTime", DateUtil.getTimeAndToString());
        assetArticlesDao.insertAssetArticles(map);
	}
	
	/**
     * 
         * @Title: queryAssetArticlesList
         * @Description: 获取用品列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryAssetArticlesList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        Map<String, Object> m = assetArticlesDao.queryUserCompanyById(user);
        map.put("companyId", m.get("companyId"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = assetArticlesDao.queryAssetArticlesList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }
    
    /**
     * 
         * @Title: queryAssetArticlesMationToDetails
         * @Description: 用品详情
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryAssetArticlesMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = assetArticlesDao.queryAssetArticlesMationToDetails(map);
        if(!ToolUtil.isBlank(bean.get("enclosureInfo").toString())){
            bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        }
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }
    
    /**
     * 
         * @Title: deleteAssetArticles
         * @Description: 删除用品
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteAssetArticles(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        assetArticlesDao.deleteAssetArticles(map);
    }
    
    /**
     * 
         * @Title: queryAssetArticlesMationById
         * @Description: 通过id查找对应的用品信息用以编辑
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryAssetArticlesMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object>    bean = assetArticlesDao.queryAssetArticlesMationById(map);
        // 查询附件
        bean.put("enclosureInfo", sysEnclosureDao.queryEnclosureInfo(bean.get("enclosureInfo").toString()));
        // 查询管理人
        bean.put("assetAdmin", sysEveUserStaffDao.queryUserNameList(bean.get("assetAdmin").toString()));
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }
    
    /**
     * 
         * @Title: editAssetArticlesMationById
         * @Description: 通过id编辑对应的用品信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editAssetArticlesMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        assetArticlesDao.editAssetArticlesMationById(map);
    }

	/**
     * 
         * @Title: queryAssetArticlesListByTypeId
         * @Description: 根据用品类别获取用品列表信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	public void queryAssetArticlesListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
        Map<String, Object> m = assetArticlesDao.queryUserCompanyById(user);
        map.put("companyId", m.get("companyId"));
		List<Map<String, Object>> beans = assetArticlesDao.queryAssetArticlesListByTypeId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

}
