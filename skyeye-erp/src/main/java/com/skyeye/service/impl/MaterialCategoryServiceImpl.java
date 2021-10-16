/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.MaterialCategoryDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.MaterialCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: MaterialCategoryServiceImpl
 * @Description: 产品类型信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:44
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MaterialCategoryServiceImpl implements MaterialCategoryService{

	@Autowired
	private MaterialCategoryDao materialCategoryDao;
	
	@Autowired
	private JedisClientService jedisClient;

	/**
     * 获取产品类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialCategoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = materialCategoryDao.queryMaterialCategoryList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	/**
	 * 
     * @Description: 新增产品类型
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertMaterialCategoryMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialCategoryDao.queryMaterialCategoryMationByName(map);//查询是否已经存在该产品类型名称
		if(bean != null && !bean.isEmpty()){
			outputObject.setreturnMessage("该类型已存在，请更换");
		}else{
			Map<String, Object> itemCount = materialCategoryDao.queryMaterialCategoryBySimpleLevel(map);
			int thisOrderBy = Integer.parseInt(itemCount.get("simpleNum").toString()) + 1;
			map.put("orderBy", thisOrderBy);
			map.put("id", ToolUtil.getSurFaceId());
			map.put("status", 1);//默认正常
			map.put("createId", inputObject.getLogParams().get("id"));
			map.put("createTime", DateUtil.getTimeAndToString());
			materialCategoryDao.insertMaterialCategoryMation(map);
			//删除产品类型的redis
			jedisClient.del(ErpConstants.getSysMaterialCategoryRedisKeyById());
		}
	}
	
	/**
	 * 
     * @Description: 删除产品类型
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteMaterialCategoryById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialCategoryDao.queryMaterialCategoryStateById(map);
		if("1".equals(bean.get("status").toString())){//正常状态下可以删除
			materialCategoryDao.deleteMaterialCategoryById(map);
			materialCategoryDao.deleteMaterialCategoryByParentId(map);
			//删除产品类型的redis
			jedisClient.del(ErpConstants.getSysMaterialCategoryRedisKeyById());
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
		
	}

	/**
	 * 
     * @Description: 通过id查找对应的产品类型信息用以编辑
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@Override
	public void selectMaterialCategoryToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object>	bean = materialCategoryDao.selectMaterialCategoryToEditById(map);
		if(bean != null && !bean.isEmpty()){
			outputObject.setBean(bean);
			outputObject.settotal(1);
		}else{
			outputObject.setreturnMessage("该数据不存在。");
		}
	}

	/**
	 * 
     * @Description: 编辑产品类型信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMaterialCategoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialCategoryDao.queryMaterialCategoryStateById(map);//查询这条产品类型的状态
		//正常状态下可以编辑
		if("1".equals(bean.get("status").toString())){
			//查询产品类型名称是否存在
			bean = materialCategoryDao.queryMaterialCategoryMationByNameAndId(map);
			if(bean != null && !bean.isEmpty()){
				outputObject.setreturnMessage("该类型已存在，请更换");
			}else{
				materialCategoryDao.editMaterialCategoryMationById(map);
				//删除产品类型的redis
				jedisClient.del(ErpConstants.getSysMaterialCategoryRedisKeyById());
			}
		}else{
			outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
		}
	}
	
	/**
	 * 
     * @Description: 产品类型上移
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMaterialCategoryMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialCategoryDao.queryMaterialCategoryUpMationById(map);//获取当前数据的同级分类下的上一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是首位，无须进行上移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			materialCategoryDao.editMaterialCategoryMationOrderNumById(map);	//该产品类型与上一条数据互换orderBy
			materialCategoryDao.editMaterialCategoryMationOrderNumById(bean);
			//删除产品类型的redis
			jedisClient.del(ErpConstants.getSysMaterialCategoryRedisKeyById());
		}
	}
	
	/**
	 * 
     * @Description: 产品类型下移
     * @param inputObject
     * @param outputObject
     * @throws Exception
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMaterialCategoryMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = materialCategoryDao.queryMaterialCategoryDownMationById(map);//获取当前数据的同级分类下的下一条数据
		if(bean == null){
			outputObject.setreturnMessage("当前类型已经是末位，无须进行下移。");
		}else{
			//进行位置交换
			map.put("upOrderBy", bean.get("prevOrderBy"));
			bean.put("upOrderBy", bean.get("thisOrderBy"));
			materialCategoryDao.editMaterialCategoryMationOrderNumById(map);//该产品类型与下一条数据互换orderBy
			materialCategoryDao.editMaterialCategoryMationOrderNumById(bean);
			//删除产品类型的redis
			jedisClient.del(ErpConstants.getSysMaterialCategoryRedisKeyById());
		}
	}

	/**
     * 获取所有类型展示为树
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMaterialCategoryListToTree(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = materialCategoryDao.queryMaterialCategoryListToTree();
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
     * 获取所有类型展示为树-数据结果呈树状
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	public void queryMaterialCategoryListToTreeZtr(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = materialCategoryDao.queryMaterialCategoryListToTree();
		// 转为树
		beans = ToolUtil.listToTree(beans, "id", "pId", "children");
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
}
