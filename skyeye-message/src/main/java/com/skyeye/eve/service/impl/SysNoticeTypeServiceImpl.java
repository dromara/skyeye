/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MessageConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.SysNoticeTypeDao;
import com.skyeye.eve.service.SysNoticeTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysNoticeTypeServiceImpl
 * @Description: 公告类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysNoticeTypeServiceImpl implements SysNoticeTypeService {

	@Autowired
	private SysNoticeTypeDao sysNoticeTypeDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: querySysNoticeTypeList
	     * @Description: 查出所有公告类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysNoticeTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysNoticeTypeDao.querySysNoticeTypeList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: insertSysNoticeTypeMation
	     * @Description: 新增公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysNoticeTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeMationByName(map);//查询是否已经存在该公告类型名称
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该公告类型名称已存在，请更换");
		}else{
			Map<String, Object> itemCount = sysNoticeTypeDao.querySysNoticeTypeBySimpleLevel(map);
			Map<String, Object> user = inputObject.getLogParams();
			int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
			map.put("orderBy", thisOrderBy);
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", "1");//默认新建
			map.put("createId", user.get("id"));
			map.put("createName", user.get("userName"));
			map.put("createTime", DateUtil.getTimeAndToString());
			sysNoticeTypeDao.insertSysNoticeTypeMation(map);
		}
	}
	
	/**
	 * 
	     * @Title: deleteSysNoticeTypeById
	     * @Description: 删除公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以删除
			sysNoticeTypeDao.deleteSysNoticeTypeById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
		
	}

	/**
	 * 
	     * @Title: updateUpSysNoticeTypeById
	     * @Description: 上线公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void updateUpSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以上线
			sysNoticeTypeDao.updateUpSysNoticeTypeById(map);
			bean = sysNoticeTypeDao.querySysNoticeTypeById(map);	//查询该公告类型的父级id
			jedisClient.del(MessageConstants.sysSecondNoticeTypeUpStateList(bean.get("parentId").toString()));//删除上线公告类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownSysNoticeTypeById
	     * @Description: 下线公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void updateDownSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			sysNoticeTypeDao.updateDownSysNoticeTypeById(map);
			bean = sysNoticeTypeDao.querySysNoticeTypeById(map);	//查询该公告类型的父级id
			jedisClient.del(MessageConstants.sysSecondNoticeTypeUpStateList(bean.get("parentId").toString()));//删除上线公告类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectSysNoticeTypeById
	     * @Description: 通过id查找对应的公告类型信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = sysNoticeTypeDao.selectSysNoticeTypeById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysNoticeTypeMationById
	     * @Description: 编辑公告类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysNoticeTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeStateById(map);	//查询这条公告类型的状态
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以编辑
			Map<String, Object> b = sysNoticeTypeDao.querySysNoticeTypeMationByNameAndId(map);	//查询公告类型名称是否存在
			if(b != null && !b.isEmpty()){
				outputObject.setreturnMessage("该公告类型名称已存在，请更换");
			}else{
				sysNoticeTypeDao.editSysNoticeTypeMationById(map);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editSysNoticeTypeMationOrderNumUpById
	     * @Description: 公告类型上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysNoticeTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysNoticeTypeDao.editSysNoticeTypeMationOrderNumUpById(map);	//该公告类型与上一条数据互换orderBy
			sysNoticeTypeDao.editSysNoticeTypeMationOrderNumUpById(bean);
			bean = sysNoticeTypeDao.querySysNoticeTypeById(map);	//查询该公告类型的父级id
			jedisClient.del(MessageConstants.sysSecondNoticeTypeUpStateList(bean.get("parentId").toString()));//删除上线公告类型的redis
		}
	}
	
	/**
	 * 
	     * @Title: editSysNoticeTypeMationOrderNumDownById
	     * @Description: 公告类型下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysNoticeTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysNoticeTypeDao.querySysNoticeTypeDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysNoticeTypeDao.editSysNoticeTypeMationOrderNumUpById(map);//该公告类型与下一条数据互换orderBy
			sysNoticeTypeDao.editSysNoticeTypeMationOrderNumUpById(bean);
			bean = sysNoticeTypeDao.querySysNoticeTypeById(map);	//查询该公告类型的父级id
			jedisClient.del(MessageConstants.sysSecondNoticeTypeUpStateList(bean.get("parentId").toString()));//删除上线公告类型的redis
		}
	}

	/**
	 * 
	     * @Title: queryFirstSysNoticeTypeUpStateList
	     * @Description: 获取已经上线的一级类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryFirstSysNoticeTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(MessageConstants.sysSecondNoticeTypeUpStateList("")))){	//若缓存中无值
			beans = sysNoticeTypeDao.queryFirstSysNoticeTypeUpStateList(map);	//从数据库中查询
			jedisClient.set(MessageConstants.sysSecondNoticeTypeUpStateList(""), JSONUtil.toJsonStr(beans));	//将从数据库中查来的内容存到缓存中
		}else{
			beans = JSONUtil.toList(jedisClient.get(MessageConstants.sysSecondNoticeTypeUpStateList("")), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySecondSysNoticeTypeUpStateList
	     * @Description: 获取上线的一级类型对应的上线的二级类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySecondSysNoticeTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(MessageConstants.sysSecondNoticeTypeUpStateList(map.get("parentId").toString())))){//若缓存中无值
			beans = sysNoticeTypeDao.querySecondSysNoticeTypeUpStateList(map);	//从数据库中查询
			jedisClient.set(MessageConstants.sysSecondNoticeTypeUpStateList(map.get("parentId").toString()), JSONUtil.toJsonStr(beans));//将从数据库中查来的内容存到缓存中
		}else{
			beans = JSONUtil.toList(jedisClient.get(MessageConstants.sysSecondNoticeTypeUpStateList(map.get("parentId").toString())), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
	     * @Title: queryAllFirstSysNoticeTypeStateList
	     * @Description: 获取所有的一级类型列表用以搜索条件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryAllFirstSysNoticeTypeStateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		beans = sysNoticeTypeDao.queryAllFirstSysNoticeTypeStateList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

}
