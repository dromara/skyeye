package com.skyeye.smprogram.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.smprogram.dao.RmPropertyValueDao;
import com.skyeye.smprogram.service.RmPropertyValueService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class RmPropertyValueServiceImpl implements RmPropertyValueService{
	
	@Autowired
	private RmPropertyValueDao rmPropertyValueDao;

	/**
	 * 
	     * @Title: queryRmPropertyValueList
	     * @Description: 获取小程序样式属性值列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyValueList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = rmPropertyValueDao.queryRmPropertyValueList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertRmPropertyValueMation
	     * @Description: 添加小程序样式属性值信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertRmPropertyValueMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyValueDao.queryRmPropertyValueMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			rmPropertyValueDao.insertRmPropertyValueMation(map);
		}else{
			outputObject.setreturnMessage("该标签属性值名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteRmPropertyValueMationById
	     * @Description: 删除小程序样式属性值信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		rmPropertyValueDao.deleteRmPropertyValueMationById(map);
	}

	/**
	 * 
	     * @Title: queryRmPropertyValueMationToEditById
	     * @Description: 编辑小程序样式属性值信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmPropertyValueMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyValueDao.queryRmPropertyValueMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editRmPropertyValueMationById
	     * @Description: 编辑小程序样式属性值信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = rmPropertyValueDao.queryRmPropertyValueMationByNameAndId(map);
		if(bean == null){
			rmPropertyValueDao.editRmPropertyValueMationById(map);
		}else{
			outputObject.setreturnMessage("该标签属性值名称已存在，不可进行二次保存");
		}
	}
	
}
