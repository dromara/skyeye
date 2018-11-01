package com.skyeye.codemodel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.codemodel.dao.CodeModelGroupDao;
import com.skyeye.codemodel.service.CodeModelGroupService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class CodeModelGroupServiceImpl implements CodeModelGroupService{
	
	@Autowired
	private CodeModelGroupDao codeModelGroupDao;
	
	@Value("${jdbc.database.name}")  
    private String dbName;

	/**
	 * 
	     * @Title: queryCodeModelGroupList
	     * @Description: 获取模板分组列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCodeModelGroupList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = codeModelGroupDao.queryCodeModelGroupList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
	/**
	 * 
	     * @Title: insertCodeModelGroupMation
	     * @Description: 新增模板分组列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertCodeModelGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelGroupDao.queryCodeModelGroupMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			map.put("groupNum", ToolUtil.card());
			codeModelGroupDao.insertCodeModelGroupMation(map);
		}else{
			outputObject.setreturnMessage("该模板分组已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteCodeModelGroupMationById
	     * @Description: 删除模板分组信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteCodeModelGroupById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelGroupDao.queryCodeModelNumById(map);
		if(bean == null){
			codeModelGroupDao.deleteCodeModelGroupById(map);
		}else{
			if(Integer.parseInt(bean.get("modelNum").toString()) == 0){//该模板分组下没有模板
				codeModelGroupDao.deleteCodeModelGroupById(map);
			}else{
				outputObject.setreturnMessage("该模板分组下存在模板，无法删除。");
			}
		}
	}

	/**
	 * 
	     * @Title: queryCodeModelGroupMationToEditById
	     * @Description: 编辑模板分组信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCodeModelGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelGroupDao.queryCodeModelGroupMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editCodeModelGroupMationById
	     * @Description: 编辑模板分组信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editCodeModelGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = codeModelGroupDao.queryCodeModelGroupMationByIdAndName(map);
		if(bean == null){
			codeModelGroupDao.editCodeModelGroupMationById(map);
		}else{
			outputObject.setreturnMessage("该模板分组已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: queryTableParameterByTableName
	     * @Description: 根据表名获取表的相关信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryTableParameterByTableName(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = codeModelGroupDao.queryTableParameterByTableName(map);
		if(beans != null){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryTableMationByTableName
	     * @Description: 根据表名获取表的相关转换信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryTableMationByTableName(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = new HashMap<>();
		String tableName = ToolUtil.replaceUnderLineAndUpperCase(map.get("tableName").toString());
		map.put("dbName", dbName);
		Map<String, Object> tableBz = codeModelGroupDao.queryTableBzByTableName(map);
		//表备注
		bean.put("tableBzName", tableBz.get("tableComment"));
		//将表名转化为Controller名
		bean.put("tableName", tableName);
		//表名首字母小写
		bean.put("tableFirstISlowerName", ToolUtil.toLowerCaseFirstOne(tableName));
		//表名全部小写
		bean.put("tableISlowerName", tableName.toLowerCase());
		//包名
		bean.put("ControllerPackageName", "com.skyeye." + tableName.toLowerCase() + ".controller" );
		bean.put("ServicePackageName", "com.skyeye." + tableName.toLowerCase() + ".service" );
		bean.put("ServiceImplPackageName", "com.skyeye." + tableName.toLowerCase() + ".service.impl" );
		bean.put("DaoPackageName", "com.skyeye." + tableName.toLowerCase() + ".dao" );
		outputObject.setBean(bean);
	}

	/**
	 * 
	     * @Title: queryCodeModelListByGroupId
	     * @Description: 根据分组id获取模板列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryCodeModelListByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = codeModelGroupDao.queryCodeModelListByGroupId(map);
		if(beans != null){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
