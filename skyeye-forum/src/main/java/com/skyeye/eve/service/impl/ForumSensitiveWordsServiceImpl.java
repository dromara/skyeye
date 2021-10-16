/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ForumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ForumSensitiveWordsDao;
import com.skyeye.eve.service.ForumSensitiveWordsService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ForumSensitiveWordsServiceImpl
 * @Description: 论坛敏感词管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:52
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ForumSensitiveWordsServiceImpl implements ForumSensitiveWordsService {

	@Autowired
	private ForumSensitiveWordsDao forumSensitiveWordsDao;
	
	@Autowired
    public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: queryForumSensitiveWordsList
	     * @Description: 查出所有论坛敏感词列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryForumSensitiveWordsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = forumSensitiveWordsDao.queryForumSensitiveWordsList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertForumSensitiveWordsMation
	     * @Description: 新增论坛敏感词
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertForumSensitiveWordsMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = forumSensitiveWordsDao.queryForumSensitiveWordsMationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该论坛敏感词名称已存在，请更换");
		}else{
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			forumSensitiveWordsDao.insertForumSensitiveWordsMation(map);
			jedisClient.del(ForumConstants.forumSensitiveWordsAll());
		}
	}
	
	/**
	 * 
	     * @Title: deleteForumSensitiveWordsById
	     * @Description: 删除论坛敏感词
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		forumSensitiveWordsDao.deleteForumSensitiveWordsById(map);
		jedisClient.del(ForumConstants.forumSensitiveWordsAll());
	}

	/**
	 * 
	     * @Title: selectForumSensitiveWordsById
	     * @Description: 通过id查找对应的论坛敏感词信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = forumSensitiveWordsDao.selectForumSensitiveWordsById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editForumSensitiveWordsMationById
	     * @Description: 通过id编辑对应的论坛敏感词信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editForumSensitiveWordsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> b = forumSensitiveWordsDao.queryForumSensitiveWordsMationByName(map);
		if(b != null && !b.isEmpty()){
			outputObject.setreturnMessage("该论坛敏感词名称已存在，请更换");
		}else{
			forumSensitiveWordsDao.editForumSensitiveWordsMationById(map);
			jedisClient.del(ForumConstants.forumSensitiveWordsAll());
		}
	}
}
