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
import com.skyeye.eve.dao.SysEvePicTypeDao;
import com.skyeye.eve.service.SysEvePicTypeService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEvePicTypeServiceImpl
 * @Description: 系统图片类型管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:32
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysEvePicTypeServiceImpl implements SysEvePicTypeService {

	@Autowired
	private SysEvePicTypeDao sysEvePicTypeDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	/**
	 * 
	     * @Title: querySysPicTypeList
	     * @Description: 查出所有系统图片类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysPicTypeList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = sysEvePicTypeDao.querySysPicTypeList(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertSysPicTypeMation
	     * @Description: 新增系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertSysPicTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeMationByName(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该系统图片种类名称已存在，请更换");
		}else{
			Map<String, Object> itemCount = sysEvePicTypeDao.querySysPicTypeBySimpleLevel(map);
			Map<String, Object> user = inputObject.getLogParams();
			int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
			map.put("orderBy", thisOrderBy);
			map.put("id", ToolUtil.getSurFaceId());
			map.put("state", "1");//默认新建
			map.put("createId", user.get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			sysEvePicTypeDao.insertSysPicTypeMation(map);
		}
	}
	
	/**
	 * 
	     * @Title: deleteSysPicTypeById
	     * @Description: 删除系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以删除
			sysEvePicTypeDao.deleteSysPicTypeById(map);
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
		
	}

	/**
	 * 
	     * @Title: updateUpSysPicTypeById
	     * @Description: 上线系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateUpSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以上线
			sysEvePicTypeDao.updateUpSysPicTypeById(map);
			jedisClient.del(Constants.sysEvePicTypeUpStateList());//删除上线图片类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: updateDownSysPicTypeById
	     * @Description: 下线系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void updateDownSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeStateById(map);
		if("2".equals(bean.get("state").toString())){//上线状态可以下线
			sysEvePicTypeDao.updateDownSysPicTypeById(map);
			jedisClient.del(Constants.sysEvePicTypeUpStateList());//删除上线图片类型的redis
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}

	/**
	 * 
	     * @Title: selectSysPicTypeById
	     * @Description: 通过id查找对应的图片类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void selectSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = sysEvePicTypeDao.selectSysPicTypeById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysPicTypeMationById
	     * @Description: 编辑图片类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysPicTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeStateById(map);
		if("1".equals(bean.get("state").toString()) || "3".equals(bean.get("state").toString())){//新建或者下线可以编辑
			Map<String, Object> b = sysEvePicTypeDao.querySysPicTypeMationByName(map);
			if(b != null && !b.isEmpty()){
				outputObject.setreturnMessage("该系统图片种类名称已存在，请更换");
			}else{
				sysEvePicTypeDao.editSysPicTypeMationById(map);
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
	     * @Title: editSysPicTypeMationOrderNumUpById
	     * @Description: 图片类型上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysPicTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysEvePicTypeDao.editSysPicTypeMationOrderNumUpById(map);
			sysEvePicTypeDao.editSysPicTypeMationOrderNumUpById(bean);
			jedisClient.del(Constants.sysEvePicTypeUpStateList());//删除上线图片类型的redis
		}
	}
	
	/**
	 * 
	     * @Title: editSysPicTypeMationOrderNumDownById
	     * @Description: 图片类型下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editSysPicTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEvePicTypeDao.querySysPicTypeDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			sysEvePicTypeDao.editSysPicTypeMationOrderNumUpById(map);
			sysEvePicTypeDao.editSysPicTypeMationOrderNumUpById(bean);
			jedisClient.del(Constants.sysEvePicTypeUpStateList());//删除上线图片类型的redis
		}
	}

	/**
	 * 
	     * @Title: querySysPicTypeUpStateList
	     * @Description: 获取已经上线的图片类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void querySysPicTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = new ArrayList<>();
		if(ToolUtil.isBlank(jedisClient.get(Constants.sysEvePicTypeUpStateList()))){
			beans = sysEvePicTypeDao.querySysPicTypeUpStateList(map);
			jedisClient.set(Constants.sysEvePicTypeUpStateList(), JSONUtil.toJsonStr(beans));
		}else{
			beans = JSONUtil.toList(jedisClient.get(Constants.sysEvePicTypeUpStateList()).toString(), null);
		}
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
