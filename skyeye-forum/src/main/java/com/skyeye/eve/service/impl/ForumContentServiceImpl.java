/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ForumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SensitiveWordInit;
import com.skyeye.common.util.SensitivewordEngine;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.ForumContentDao;
import com.skyeye.eve.dao.ForumSensitiveWordsDao;
import com.skyeye.eve.service.ForumContentService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.solr.Forum;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: ForumContentServiceImpl
 * @Description: 论坛管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:09
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ForumContentServiceImpl implements ForumContentService {

	@Autowired
	private ForumContentDao forumContentDao;
	
	@Autowired
	private ForumSensitiveWordsDao forumSensitiveWordsDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Autowired
	private SolrClient solrClient;
	
	/**
	 * 
	     * @Title: queryMyForumContentList
	     * @Description: 获取我的帖子列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMyForumContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = forumContentDao.queryMyForumContentList(map);
		for(Map<String, Object> m : beans){
			String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
            m.put("createTime", createTime);
			String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
			if(ToolUtil.isBlank(jedisClient.get(key))){//浏览量
				m.put("browseNum", 0);
			}else{
				String browseNum = jedisClient.get(key);
				m.put("browseNum", browseNum);
			}
		}
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertForumContentMation
	     * @Description: 新增我的帖子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertForumContentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String str = querySensitiveWordsByMap(map);
		if(str.length() > 0){
			outputObject.setreturnMessage("该帖子包含以下敏感词：" + str.substring(0, str.length() - 1) + "！");
		}else{
	    	Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", 1);
			map.put("reportState", 1);
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			//贴子纯文本内容
			String content = map.get("textConent").toString();
			//简介
			map.put("desc", content.length() > 400 ? content.substring(0, 400) : content);
			//贴子对应的solr对象
			Forum forum = new Forum();
            forum.setId(map.get("id").toString());
            forum.setForumTitle(map.get("title").toString());
            forum.setForumContent(content);//纯文本内容
            forum.setForumDesc(map.get("desc").toString());
            forum.setType(map.get("forumType").toString());
            forum.setCreateId(map.get("createId").toString());
            try {
                UpdateResponse response = solrClient.addBean(forum,1000);
                int status = response.getStatus();
                if (status != 0) {
                    solrClient.rollback();
                    outputObject.setreturnMessage("发布失败！");
                }else{
                    int insert = forumContentDao.insertForumContentMation(map);
                    if (insert != 1) {
                        solrClient.rollback();
                        outputObject.setreturnMessage("发布失败！");
                    }
                }
            } catch (SolrServerException e) {
                outputObject.setreturnMessage("发布失败！");
            } catch (Exception e) {
                outputObject.setreturnMessage("发布失败！");
            }
	    }
	}
	
	/**
	 * 
	     * @Title: deleteForumContentById
	     * @Description: 删除我的帖子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteForumContentById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		try {
        	UpdateResponse response = solrClient.deleteById(map.get("id").toString(),1000);
            int status = response.getStatus();
            if (status != 0) {
                solrClient.rollback();
            }else{
                int delete = forumContentDao.deleteForumContentById(map);
                if (delete != 1) {
                    solrClient.rollback();
                    outputObject.setreturnMessage("删除失败！");
                }
            }
        } catch (SolrServerException e) {
            outputObject.setreturnMessage("删除失败！");
        } catch (Exception e) {
            outputObject.setreturnMessage("删除失败！");
        }
	}
	
	/**
	 * 
	     * @Title: queryForumContentMationById
	     * @Description: 查询帖子信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */ 
	@Override
	public void queryForumContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = forumContentDao.queryForumContentMationById(map);
		List<Map<String,Object>> beans = forumContentDao.selectForumTagById(bean);
        bean.put("tagName", beans);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}
	
	/**
	 * 
	     * @Title: editForumContentMationById
	     * @Description: 编辑帖子信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */ 
	@Override
	@Transactional(value="transactionManager")
	public void editForumContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String str = querySensitiveWordsByMap(map);
		if(str.length() > 0){
			outputObject.setreturnMessage("该帖子包含以下敏感词：" + str.substring(0, str.length() - 1) + "！");
		}else{
			//贴子纯文本内容
			String content = map.get("textConent").toString();
			//简介
			map.put("desc", content.length() > 400 ? content.substring(0, 400) : content);
			Map<String, Object> bean = forumContentDao.queryForumContentMationById(map);
            Forum forum = new Forum();
            forum.setId(map.get("id").toString());
            forum.setForumTitle(map.get("title").toString());
            forum.setForumContent(content);//纯文本内容
            forum.setForumDesc(map.get("desc").toString());
            forum.setType(map.get("forumType").toString());
            forum.setCreateId(bean.get("createId").toString());
            try {
                UpdateResponse response = solrClient.deleteById(map.get("id").toString(),1000);
                int delstatus = response.getStatus();
                if (delstatus != 0) {
                    solrClient.rollback();
                }else{
                    response = solrClient.addBean(forum,1000);
                    int addstatus = response.getStatus();
                    if (addstatus != 0) {
                        solrClient.rollback();
                        outputObject.setreturnMessage("发布失败！");
                    }else{
                        int edit = forumContentDao.editForumContentMationById(map);
                        if (edit != 1) {
                            solrClient.rollback();
                            outputObject.setreturnMessage("发布失败！");
                        }
                    }
                }
			} catch (SolrServerException e) {
	            outputObject.setreturnMessage("发布失败！");
	        } catch (Exception e) {
	            outputObject.setreturnMessage("发布失败！");
	        }
		}
	}
	
	/**
	 * 
	     * @Title: queryForumContentMationToDetails
	     * @Description: 帖子详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */ 
	@SuppressWarnings("unchecked")
	@Override
	public void queryForumContentMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = forumContentDao.queryForumContentMationToDetails(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
		String userId = inputObject.getLogParams().get("id").toString();
		String forumId = map.get("id").toString();
		//获取、设置浏览量
		String key = ForumConstants.forumBrowseNumsByForumId(forumId);
		if(ToolUtil.isBlank(jedisClient.get(key))){
			jedisClient.set(key, "1");
		}else{
			String oldnum = jedisClient.get(key);
			jedisClient.set(key, String.valueOf(Integer.parseInt(oldnum) + 1));
		}
		//设置今日被浏览的帖子
		String browseKey = ForumConstants.forumEverydayBrowseIdsByTime(DateUtil.getYmdTimeAndToString());
        if(ToolUtil.isBlank(jedisClient.get(browseKey))){
            jedisClient.set(browseKey, forumId);
            jedisClient.set(browseKey, forumId);
        }else{
            String str = jedisClient.get(browseKey);
            if (str.indexOf(forumId) == -1) {
                jedisClient.set(browseKey, str + "," + forumId);
            }
        }
		//新增浏览信息
		String keys = ForumConstants.forumBrowseMationByUserid(userId);
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(keys))){//用户之前是否浏览过帖子
			Map<String, Object> m = new HashMap<>();
			m.put("forumId", forumId);
			m.put("tagName", bean.get("tagName"));
			m.put("title", bean.get("title"));
			m.put("desc", bean.get("desc"));
			m.put("userPhoto", bean.get("userPhoto"));
			m.put("userId", bean.get("userId"));
			m.put("browseTime", DateUtil.getTimeAndToString());
			beans.add(m);
			jedisClient.set(keys, JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(keys), null);
			boolean ifexist = false;//用户之前是否浏览过当前帖子，浏览过则更改浏览时间为当前时间
			for(Map<String, Object> m : beans){
				if(m.get("forumId").toString().equals(forumId)){
					m.put("tagName", bean.get("tagName"));
					m.put("title", bean.get("title"));
					m.put("desc", bean.get("desc"));
					m.put("userPhoto", bean.get("userPhoto"));
					m.put("userId", bean.get("userId"));
					m.put("browseTime", DateUtil.getTimeAndToString());
					ifexist = true;
				}
			}
			if(!ifexist){//没有浏览过则新增
				Map<String, Object> m = new HashMap<>();
				m.put("forumId", forumId);
				m.put("tagName", bean.get("tagName") == null ? "" : bean.get("tagName"));
				m.put("title", bean.get("title"));
				m.put("desc", bean.get("desc"));
				m.put("userPhoto", bean.get("userPhoto"));
				m.put("userId", bean.get("userId"));
				m.put("browseTime", DateUtil.getTimeAndToString());
				beans.add(m);
			}
			jedisClient.set(keys, JSONUtil.toJsonStr(beans));
		}
	}
	
	/**
	 * 
	     * @Title: queryNewForumContentList
	     * @Description: 获取最新帖子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("userId", inputObject.getLogParams().get("id"));
		List<Map<String, Object>> beans = forumContentDao.queryNewForumContentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: insertForumCommentMation
	     * @Description: 新增帖子评论
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertForumCommentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
    	Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("commentId", user.get("id"));
		map.put("commentTime", DateUtil.getTimeAndToString());
		map.put("belongCommentId", "0");
		map.put("replyId", "");
		forumContentDao.insertForumCommentMation(map);
		//获取、设置评论量
        String key = ForumConstants.forumCommentNumsByForumId(map.get("forumId").toString());
        if(ToolUtil.isBlank(jedisClient.get(key))){
            jedisClient.set(key, "1");
        }else{
            String oldnum = jedisClient.get(key);
            jedisClient.set(key, String.valueOf(Integer.parseInt(oldnum) + 1));
        }
	}
	
	/**
	 * 
	     * @Title: queryForumCommentList
	     * @Description: 获取帖子评论信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryForumCommentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = forumContentDao.queryForumCommentList(map);
		for(Map<String, Object> m : beans){
            String commentTime = ToolUtil.timeFormat(m.get("commentTime").toString());
            m.put("commentTime", commentTime);
        }
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: insertForumReplyMation
	     * @Description: 新增帖子评论回复
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertForumReplyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
    	Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("commentId", user.get("id"));
		map.put("commentTime", DateUtil.getTimeAndToString());
		forumContentDao.insertForumReplyMation(map);
		//获取、设置评论量
        String key = ForumConstants.forumCommentNumsByForumId(map.get("forumId").toString());
        if(ToolUtil.isBlank(jedisClient.get(key))){
            jedisClient.set(key, "1");
        }else{
            String oldnum = jedisClient.get(key);
            jedisClient.set(key, String.valueOf(Integer.parseInt(oldnum) + 1));
        }
	}
	
	/**
	 * 
	     * @Title: queryForumReplyList
	     * @Description: 获取帖子评论回复信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryForumReplyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = forumContentDao.queryForumReplyList(map);
		for(Map<String, Object> m : beans){
            String commentTime = ToolUtil.timeFormat(m.get("commentTime").toString());
            m.put("commentTime", commentTime);
        }
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: queryForumMyBrowerList
	     * @Description: 获取我的浏览信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String userId = inputObject.getLogParams().get("id").toString();
		String keys = ForumConstants.forumBrowseMationByUserid(userId);
		List<Map<String, Object>> beans = new ArrayList<>();
        if(!ToolUtil.isBlank(jedisClient.get(keys))){
            beans = JSONUtil.toList(jedisClient.get(keys), null);
            //按浏览时间给集合排序
            beans.sort(new Comparator<Map<String, Object>>() {//Comparator 比较器. 需要实现比较方法
                @Override
                public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                    int flag = m1.get("browseTime").toString().compareTo(m2.get("browseTime").toString());
                    return -flag; // 不取反，则按正序排列
                }
            });
            int count = beans.size();
            int pageMaxSize = Integer.parseInt(map.get("page").toString()) * Integer.parseInt(map.get("limit").toString());
            if(count < pageMaxSize){
                pageMaxSize = count;
            }
            beans = beans.subList((Integer.parseInt(map.get("page").toString()) - 1) * Integer.parseInt(map.get("limit").toString()), pageMaxSize);
            for(Map<String, Object> m : beans){
                String key = ForumConstants.forumBrowseNumsByForumId(m.get("forumId").toString());
                if(ToolUtil.isBlank(jedisClient.get(key))){//浏览量
                    m.put("browseNum", 0);
                }else{
                    String browseNum = jedisClient.get(key);
                    m.put("browseNum", browseNum);
                }
                Map<String, Object> ma = forumContentDao.selectForumCommentNumById(m);
                if(!ToolUtil.isBlank(ma.get("commentNum").toString())){//评论数
                    m.put("commentNum", ma.get("commentNum"));
                }else{
                    m.put("commentNum", 0);
                }
                String browseTime = ToolUtil.timeFormat(m.get("browseTime").toString());
                m.put("browseTime", browseTime);//浏览时间
            }
        }
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: queryNewCommentList
	     * @Description: 获取最新评论
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryNewCommentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = forumContentDao.queryNewCommentList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: queryForumListByTagId
	     * @Description: 根据标签id获取帖子列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryForumListByTagId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
        long total = 0;
        if(!map.get("tagId").toString().equals("hot")){
            map.put("userId", inputObject.getLogParams().get("id"));
			Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
            beans = forumContentDao.queryForumListByTagId(map);
            total = pages.getTotal();
            for(Map<String, Object> m : beans){
                String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
                m.put("createTime", createTime);
                String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
                if(ToolUtil.isBlank(jedisClient.get(key))){//浏览量
                    m.put("browseNum", 0);
                }else{
                    String browseNum = jedisClient.get(key);
                    m.put("browseNum", browseNum);
                }
			}
        }else{
            map.put("userId", inputObject.getLogParams().get("id"));
            beans = forumContentDao.queryAllHotForumList(map);
            for(Map<String, Object> m : beans){
                String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
                m.put("createTime", createTime);
                String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
                if(ToolUtil.isBlank(jedisClient.get(key))){//浏览量
                    m.put("browseNum", 0);
                }else{
                    String browseNum = jedisClient.get(key);
                    m.put("browseNum", browseNum);
                }
            }
            //按浏览量和评论数给集合排序
            beans.sort(new Comparator<Map<String, Object>>() {//Comparator 比较器. 需要实现比较方法
                @Override
                public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                    Integer m1num = Integer.parseInt(m1.get("browseNum").toString()) + Integer.parseInt(m1.get("commentNum").toString());
                    Integer m2num = Integer.parseInt(m2.get("browseNum").toString()) + Integer.parseInt(m2.get("commentNum").toString());
                    int flag = m1num.compareTo(m2num);
                    return -flag; // 不取反，则按正序排列
                }
            });
            int count = beans.size();
            int pageMaxSize = Integer.parseInt(map.get("page").toString()) * Integer.parseInt(map.get("limit").toString());
            if(count < pageMaxSize){
                pageMaxSize = count;
            }
            beans = beans.subList((Integer.parseInt(map.get("page").toString()) - 1) * Integer.parseInt(map.get("limit").toString()), pageMaxSize);
            total = count;
		}
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
	/**
     * 
         * @Title: queryHotTagList
         * @Description: 获取热门标签
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryHotTagList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryHotTagList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
    
    /**
     * 
         * @Title: queryActiveUsersList
         * @Description: 获取活跃用户
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryActiveUsersList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryActiveUsersList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
	
    /**
     * 
         * @Title: queryHotForumList
         * @Description: 获取热门贴
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        List<Map<String, Object>> beans = forumContentDao.queryHotForumList(map);
        for(Map<String, Object> m : beans){
            String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
            m.put("createTime", createTime);
            String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
            if(ToolUtil.isBlank(jedisClient.get(key))){//浏览量
                m.put("browseNum", 0);
            }else{
                String browseNum = jedisClient.get(key);
                m.put("browseNum", browseNum);
            }
        }
        //按浏览量和评论数给集合排序
        beans.sort(new Comparator<Map<String, Object>>() {//Comparator 比较器. 需要实现比较方法
            @Override
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                Integer m1num = Integer.parseInt(m1.get("browseNum").toString()) + Integer.parseInt(m1.get("commentNum").toString());
                Integer m2num = Integer.parseInt(m2.get("browseNum").toString()) + Integer.parseInt(m2.get("commentNum").toString());
                int flag = m1num.compareTo(m2num);
                return -flag; // 不取反，则按正序排列
            }
        });
        int count = beans.size();
        int pageMaxSize = 6;
        if(count < pageMaxSize){
            pageMaxSize = count;
        }
        outputObject.setBeans(beans.subList(0, pageMaxSize));
        outputObject.settotal(beans.size());
    }
	
    /**
     * 
         * @Title: querySearchForumList
         * @Description: 获取用户搜索的帖子
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void querySearchForumList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String searchValue = map.get("searchValue").toString();
        List<Map<String, Object>> beans = new ArrayList<>();
        List<Map<String, Object>> rbeans = new ArrayList<>();
        // 关键字模糊查询
        SolrQuery query = new SolrQuery();
        String forumTitle = "forumTitle:" + searchValue;
        String forumDesc = " OR forumDesc:" + searchValue;
        String forumContent = " OR forumContent:" + searchValue;
        query.set("q",forumTitle + forumDesc + forumContent);
        query.setStart(0);
        query.setRows(20);
        query.setHighlight(true); //开启高亮
        query.addHighlightField("forumContent"); //高亮字段
        query.setHighlightSimplePre("<font color='red'>"); //高亮单词的前缀
        query.setHighlightSimplePost("</font>"); //高亮单词的后缀
        query.setHighlightFragsize(400);
        int count = 0;
        String createId = inputObject.getLogParams().get("id").toString();
        try {
            QueryResponse response = solrClient.query(query);
            SolrDocumentList documentList = response.getResults();
            for(SolrDocument document : documentList){
                beans.add(document);
            }
            Map<String, Map<String, List<String>>> maplist = response.getHighlighting();
            if(beans != null){
                for(Map<String, Object> m : beans){
                	if(m.get("type").toString().replaceAll("\\[|\\]", "").substring(0, 1).equals("1")){
                        for(String key : maplist.keySet()){
                            if(key.equals(m.get("id").toString())){
                                Map<String, List<String>> fieldMap = maplist.get(key);
                                if(fieldMap.size() > 0){
                                    m.put("forumContent", "..." + fieldMap.get("forumContent").get(0));
                                }
                            }
                        }
                        rbeans.add(m);
                    }else{
                        if(m.get("createId").toString().replaceAll("\\[|\\]", "").equals(createId)){
                            for(String key : maplist.keySet()){
                                if(key.equals(m.get("id").toString())){
                                    Map<String, List<String>> fieldMap = maplist.get(key);
                                    if(fieldMap.size() > 0){
                                        m.put("forumContent", "..." + fieldMap.get("forumContent").get(0));
                                    }
                                }
                            }
                            rbeans.add(m);
                        }
                    }
                }
            }
            count = rbeans.size();
        } catch (SolrServerException e) {
            outputObject.setreturnMessage("搜索失败！");
        } catch (Exception e) {
            outputObject.setreturnMessage("搜索失败！");
        }
        outputObject.setBeans(rbeans);
        outputObject.settotal(count);
    }
    
    /**
     * 
         * @Title: querySolrSynchronousTime
         * @Description: 获取solr上次同步数据的时间
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */ 
    @Override
    public void querySolrSynchronousTime(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String keys = ForumConstants.forumSolrSynchronoustime();
        if(!ToolUtil.isBlank(jedisClient.get(keys))){
            String synchronousTime = jedisClient.get(keys);
            map.put("synchronousTime", synchronousTime);
        }
        outputObject.setBean(map);
        outputObject.settotal(1);
    }
	
    /**
     * 
         * @Title: updateSolrSynchronousData
         * @Description: solr同步数据
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */ 
    @Override
    public void updateSolrSynchronousData(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryAllForumList(map);
        DocumentObjectBinder binder = new DocumentObjectBinder();
        try {
            for (Map<String, Object> t : beans) {
                Forum forum = new Forum();
                forum.setId(t.get("id").toString());
                forum.setForumTitle(t.get("title").toString());
                forum.setForumContent(t.get("content").toString());
                forum.setForumDesc(t.get("desc").toString());
                forum.setType(t.get("forumType").toString());
                forum.setCreateId(t.get("createId").toString());
                SolrInputDocument doc = binder.toSolrInputDocument(forum);
                solrClient.add(doc);
            }
            solrClient.commit();
            String keys = ForumConstants.forumSolrSynchronoustime();
            String nowTime = DateUtil.getTimeAndToString();
            if(!ToolUtil.isBlank(jedisClient.get(keys))){
                jedisClient.del(keys);
                jedisClient.set(keys, nowTime);
            }else{
                jedisClient.set(keys, nowTime);
            }
            map.put("synchronousTime", nowTime);
        } catch (SolrServerException e) {
            outputObject.setreturnMessage("同步失败！");
        } catch (Exception e) {
            outputObject.setreturnMessage("同步失败！");
        }
        outputObject.setBean(map);
        outputObject.settotal(1);
    }
	
    /**
     * 
         * @Title: queryMyCommentList
         * @Description: 获取我的帖子列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryMyCommentList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = forumContentDao.queryMyCommentList(map);
        for(Map<String, Object> m : beans){
            String commentTime = ToolUtil.timeFormat(m.get("commentTime").toString());
            m.put("commentTime", commentTime);
        }
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
    }
	
    /**
     * 
         * @Title: deleteCommentById
         * @Description: 根据评论id删除评论
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void deleteCommentById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        forumContentDao.deleteCommentById(map);
    }
	
    /**
     * 
         * @Title: queryMyNoticeList
         * @Description: 获取我的通知列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryMyNoticeList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = forumContentDao.queryMyNoticeList(map);
        for(Map<String, Object> m : beans){
            String sendTime = ToolUtil.timeFormat(m.get("sendTime").toString());
            m.put("sendTime", sendTime);
        }
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
    }
	
    /**
     * 
         * @Title: deleteNoticeById
         * @Description: 根据通知id删除通知
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        forumContentDao.deleteNoticeById(map);
    }
    
	/**
	 * 
	     * @Title: querySensitiveWordsByMap
	     * @Description: 查找内容中的包含的敏感词
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */ 
	@SuppressWarnings("unchecked")
	public String querySensitiveWordsByMap(Map<String, Object> map) throws Exception {
		String content = map.get("title").toString() + "," + map.get("textConent").toString();
		List<Map<String, Object>> sensitiveWords;
		if(ToolUtil.isBlank(jedisClient.get(ForumConstants.forumSensitiveWordsAll()))){
		    sensitiveWords = forumSensitiveWordsDao.queryForumSensitiveWordsListAll();
			jedisClient.set(ForumConstants.forumSensitiveWordsAll(), JSONUtil.toJsonStr(sensitiveWords));
		}else{
			sensitiveWords = JSONUtil.toList(jedisClient.get(ForumConstants.forumSensitiveWordsAll()), null);
		}
		SensitiveWordInit sensitiveWordInit = new SensitiveWordInit();
	    Map<String, Object> sensitiveWordMap = sensitiveWordInit.initKeyWord(sensitiveWords);
	    SensitivewordEngine.sensitiveWordMap = sensitiveWordMap;
	    Set<String> set = SensitivewordEngine.getSensitiveWord(content, 2);
	    String str = "";
	    if(set.size() > 0){
	    	for(String s : set){
	    		str += s + "、";
	    	}
	    }
	    return str;
	} 
	
}
