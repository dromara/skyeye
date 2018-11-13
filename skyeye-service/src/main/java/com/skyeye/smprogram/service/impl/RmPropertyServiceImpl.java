package com.skyeye.smprogram.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.smprogram.dao.RmPropertyDao;
import com.skyeye.smprogram.service.RmPropertyService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class RmPropertyServiceImpl implements RmPropertyService{
	
	@Autowired
	private RmPropertyDao rmPropertyDao;

	/**
	 * 
	     * @Title: queryRmPropertyList
	     * @Description: 获取小程序样式属性列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = rmPropertyDao.queryRmPropertyList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertRmPropertyMation
	     * @Description: 添加小程序样式属性信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			rmPropertyDao.insertRmPropertyMation(map);
		}else{
			outputObject.setreturnMessage("该样式属性名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteRmPropertyMationById
	     * @Description: 删除小程序样式属性信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		rmPropertyDao.deleteRmPropertyMationById(map);
	}

	/**
	 * 
	     * @Title: queryRmPropertyMationToEditById
	     * @Description: 编辑小程序样式属性信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmPropertyMationById
	     * @Description: 编辑小程序样式属性信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyDao.queryRmPropertyMationByNameAndId(map);
		if(bean == null){
			rmPropertyDao.editRmPropertyMationById(map);
		}else{
			outputObject.setreturnMessage("该样式属性名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryRmPropertyListToShow
	     * @Description: 获取小程序样式属性供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyListToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = rmPropertyDao.queryRmPropertyListToShow(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
