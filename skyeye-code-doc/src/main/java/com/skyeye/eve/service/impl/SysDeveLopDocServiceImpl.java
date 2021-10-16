/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysDeveLopDocDao;
import com.skyeye.eve.service.SysDeveLopDocService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysDeveLopDocServiceImpl implements SysDeveLopDocService{
	
	@Autowired
	private SysDeveLopDocDao sysDeveLopDocDao;
	
	@Autowired
	public JedisClientService jedisClient;

	/**
	 * 
	     * @Title: querySysDeveLopTypeList
	     * @Description: 获取开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDeveLopTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysDeveLopDocDao.querySysDeveLopTypeList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: insertSysDeveLopType
	     * @Description: 新增开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysDeveLopType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopByName(map);
		if(bean == null || bean.isEmpty()){
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> item = sysDeveLopDocDao.queryMaxSysDeveLopBySimpleParentId(map);//获取同级下的排位序号最大的数据
			if(item == null){
				map.put("orderBy", 1);
			}else{
				map.put("orderBy", Integer.parseInt(item.get("orderBy").toString()) + 1);
			}
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			sysDeveLopDocDao.insertSysDeveLopType(map);
		}else{
			outputObject.setreturnMessage("该目录已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopTypeByIdToEdit
	     * @Description: 编辑开发文档目录信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDeveLopTypeByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopTypeByIdToEdit(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysDeveLopTypeById
	     * @Description: 编辑开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopByNameAndId(map);
		if(bean == null || bean.isEmpty()){
			sysDeveLopDocDao.editSysDeveLopTypeById(map);
		}else{
			outputObject.setreturnMessage("该目录已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteSysDeveLopTypeById
	     * @Description: 删除开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysDeveLopTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopTypeContentNumById(map);
		if(bean == null || (Integer.parseInt(bean.get("contentNum").toString()) == 0 && Integer.parseInt(bean.get("childNum").toString()) == 0)){
			sysDeveLopDocDao.deleteSysDeveLopTypeById(map);
		}else{
			outputObject.setreturnMessage("该目录下存在子目录或文档，请先进行子目录或者文档的删除。");
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopTypeByFirstType
	     * @Description: 获取一级文档目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDeveLopTypeByFirstType(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysDeveLopDocDao.querySysDeveLopTypeByFirstType(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopTypeStateISupById
	     * @Description: 开发文档目录上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopTypeStateISupById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopTypeStateById(map);
		if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线可以上线
			sysDeveLopDocDao.editSysDeveLopTypeStateISupById(map);
			if("0".equals(bean.get("parentId").toString())){
				jedisClient.del(Constants.getSysDeveLopDocFirstType());
			}else{
				jedisClient.del(Constants.getSysDeveLopDocSecondType(bean.get("parentId").toString()));
			}
		}else{
			outputObject.setreturnMessage("该目录状态已改变，请刷新页面。");
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopTypeStateISdownById
	     * @Description: 开发文档目录下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopTypeStateISdownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopTypeStateById(map);
		if("1".equals(bean.get("state").toString())){//上线状态可以下线
			sysDeveLopDocDao.editSysDeveLopTypeStateISdownById(map);
			if("0".equals(bean.get("parentId").toString())){
				jedisClient.del(Constants.getSysDeveLopDocFirstType());
			}else{
				jedisClient.del(Constants.getSysDeveLopDocSecondType(bean.get("parentId").toString()));
			}
		}else{
			outputObject.setreturnMessage("该目录状态已改变，请刷新页面。");
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopTypeOrderByISupById
	     * @Description: 开发文档目录上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopTypeOrderByISupById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopTypeOrderByISupById(map);
		if(bean != null && !bean.isEmpty()){
			map.put("orderBy", bean.get("newOrderBy"));
			bean.put("orderBy", bean.get("oldOrderBy"));
			sysDeveLopDocDao.editSysDeveLopTypeOrderByISupById(map);
			sysDeveLopDocDao.editSysDeveLopTypeOrderByISupById(bean);
			if("0".equals(bean.get("parentId").toString())){
				jedisClient.del(Constants.getSysDeveLopDocFirstType());
			}else{
				jedisClient.del(Constants.getSysDeveLopDocSecondType(bean.get("parentId").toString()));
			}
		}else{
			outputObject.setreturnMessage("已经是最首位的目录。");
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopTypeOrderByISdownById
	     * @Description: 开发文档目录下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopTypeOrderByISdownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopTypeOrderByISdownById(map);
		if(bean != null && !bean.isEmpty()){
			map.put("orderBy", bean.get("newOrderBy"));
			bean.put("orderBy", bean.get("oldOrderBy"));
			sysDeveLopDocDao.editSysDeveLopTypeOrderByISdownById(map);
			sysDeveLopDocDao.editSysDeveLopTypeOrderByISdownById(bean);
			if("0".equals(bean.get("parentId").toString())){
				jedisClient.del(Constants.getSysDeveLopDocFirstType());
			}else{
				jedisClient.del(Constants.getSysDeveLopDocSecondType(bean.get("parentId").toString()));
			}
		}else{
			outputObject.setreturnMessage("已经是最末位的目录。");
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopDocList
	     * @Description: 获取开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDeveLopDocList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysDeveLopDocDao.querySysDeveLopDocList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}

	/**
	 * 
	     * @Title: addSysDeveLopDoc
	     * @Description: 新增开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void addSysDeveLopDoc(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocByNameAndParentId(map);
		if(bean == null || bean.isEmpty()){
			Map<String, Object> user = inputObject.getLogParams();
			Map<String, Object> item = sysDeveLopDocDao.queryMaxSysDeveLopDocBySimpleParentId(map);//获取同级下的排位序号最大的数据
			if(item == null){
				map.put("orderBy", 1);
			}else{
				map.put("orderBy", Integer.parseInt(item.get("orderBy").toString()) + 1);
			}
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			sysDeveLopDocDao.insertSysDeveLopDoc(map);
		}else{
			outputObject.setreturnMessage("该文档已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopDocByIdToEdit
	     * @Description: 编辑开发文档信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDeveLopDocByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocByIdToEdit(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysDeveLopDocById
	     * @Description: 编辑开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopDocById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocByNameAndId(map);
		if(bean == null || bean.isEmpty()){
			sysDeveLopDocDao.editSysDeveLopDocById(map);
		}else{
			outputObject.setreturnMessage("该文档标题已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteSysDeveLopDocById
	     * @Description: 删除开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysDeveLopDocById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysDeveLopDocDao.deleteSysDeveLopDocById(map);
	}

	/**
	 * 
	     * @Title: editSysDeveLopDocStateISupById
	     * @Description: 开发文档上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopDocStateISupById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocStateById(map);
		if("0".equals(bean.get("state").toString()) || "2".equals(bean.get("state").toString())){//新建或者下线可以上线
			sysDeveLopDocDao.editSysDeveLopDocStateISupById(map);
			jedisClient.del(Constants.getSysDeveLopDocTitleList(bean.get("typeId").toString()));//删除父分类的redis
			jedisClient.del(Constants.getSysDeveLopDocContent(map.get("id").toString()));//删除开发文档的redis
		}else{
			outputObject.setreturnMessage("该文档状态已改变，请刷新页面。");
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopDocStateISdownById
	     * @Description: 开发文档下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopDocStateISdownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocStateById(map);
		if("1".equals(bean.get("state").toString())){//上线状态可以下线
			sysDeveLopDocDao.editSysDeveLopDocStateISdownById(map);
			jedisClient.del(Constants.getSysDeveLopDocTitleList(bean.get("typeId").toString()));//删除父分类的redis
			jedisClient.del(Constants.getSysDeveLopDocContent(map.get("id").toString()));//删除开发文档的redis
		}else{
			outputObject.setreturnMessage("该文档状态已改变，请刷新页面。");
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopDocOrderByISupById
	     * @Description: 开发文档上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopDocOrderByISupById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocOrderByISupById(map);
		if(bean != null && !bean.isEmpty()){
			map.put("orderBy", bean.get("newOrderBy"));
			bean.put("orderBy", bean.get("oldOrderBy"));
			sysDeveLopDocDao.editSysDeveLopDocOrderByISupById(map);
			sysDeveLopDocDao.editSysDeveLopDocOrderByISupById(bean);
			jedisClient.del(Constants.getSysDeveLopDocTitleList(bean.get("typeId").toString()));//删除父分类的redis
		}else{
			outputObject.setreturnMessage("已经是最首位的文档。");
		}
	}

	/**
	 * 
	     * @Title: editSysDeveLopDocOrderByISdownById
	     * @Description: 开发文档下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysDeveLopDocOrderByISdownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysDeveLopDocDao.querySysDeveLopDocOrderByISdownById(map);
		if(bean != null && !bean.isEmpty()){
			map.put("orderBy", bean.get("newOrderBy"));
			bean.put("orderBy", bean.get("oldOrderBy"));
			sysDeveLopDocDao.editSysDeveLopDocOrderByISdownById(map);
			sysDeveLopDocDao.editSysDeveLopDocOrderByISdownById(bean);
			jedisClient.del(Constants.getSysDeveLopDocTitleList(bean.get("typeId").toString()));//删除父分类的redis
		}else{
			outputObject.setreturnMessage("已经是最末位的文档。");
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopFirstTypeToShow
	     * @Description: 获取一级分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void querySysDeveLopFirstTypeToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysDeveLopDocFirstType()))){
			beans = sysDeveLopDocDao.querySysDeveLopFirstTypeToShow(map);
			jedisClient.set(Constants.getSysDeveLopDocFirstType(), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.getSysDeveLopDocFirstType()), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopSecondTypeToShow
	     * @Description: 获取二级分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void querySysDeveLopSecondTypeToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysDeveLopDocSecondType(map.get("parentId").toString())))){
			beans = sysDeveLopDocDao.querySysDeveLopSecondTypeToShow(map);
			jedisClient.set(Constants.getSysDeveLopDocSecondType(map.get("parentId").toString()), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.getSysDeveLopDocSecondType(map.get("parentId").toString())), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopDocToShow
	     * @Description: 获取文档标题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void querySysDeveLopDocToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysDeveLopDocTitleList(map.get("parentId").toString())))){
			beans = sysDeveLopDocDao.querySysDeveLopDocToShow(map);
			jedisClient.set(Constants.getSysDeveLopDocTitleList(map.get("parentId").toString()), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.getSysDeveLopDocTitleList(map.get("parentId").toString())), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysDeveLopDocContentToShow
	     * @Description: 获取文档内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void querySysDeveLopDocContentToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = new HashMap<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.getSysDeveLopDocContent(map.get("id").toString())))){
			bean = sysDeveLopDocDao.querySysDeveLopDocContentToShow(map);
			jedisClient.set(Constants.getSysDeveLopDocContent(map.get("id").toString()), JSONUtil.toJsonStr(bean));
		}else{
			bean = JSONUtil.toBean(jedisClient.get(Constants.getSysDeveLopDocContent(map.get("id").toString())), null);
		}
		outputObject.setBean(bean);
		
	}
	
}
