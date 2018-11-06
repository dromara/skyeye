package com.skyeye.dsform.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.dsform.dao.DsFormContentDao;
import com.skyeye.dsform.service.DsFormContentService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class DsFormContentServiceImpl implements DsFormContentService{
	
	@Autowired
	private DsFormContentDao dsFormContentDao;

	/**
	 * 
	     * @Title: queryDsFormContentList
	     * @Description: 获取动态表单内容列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormContentList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormContentDao.queryDsFormContentList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertDsFormContentMation
	     * @Description: 添加动态表单内容信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormContentDao.queryDsFormContentMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			dsFormContentDao.insertDsFormContentMation(map);
		}else{
			outputObject.setreturnMessage("该动态表单内容名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteDsFormContentMationById
	     * @Description: 删除动态表单内容信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dsFormContentDao.deleteDsFormContentMationById(map);
	}

	/**
	 * 
	     * @Title: queryDsFormContentMationToEditById
	     * @Description: 编辑动态表单内容信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormContentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormContentDao.queryDsFormContentMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editDsFormContentMationById
	     * @Description: 编辑动态表单内容信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormContentDao.queryDsFormContentMationByNameAndId(map);
		if(bean == null){
			dsFormContentDao.editDsFormContentMationById(map);
		}else{
			outputObject.setreturnMessage("该动态表单内容名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryDsFormContentMationToShow
	     * @Description: 获取动态表单内容供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormContentMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormContentDao.queryDsFormContentMationToShow(map);
		if(beans != null){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryDsFormContentDetailedMationToShow
	     * @Description: 获取动态表单内容详细信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormContentDetailedMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormContentDao.queryDsFormContentDetailedMationToShow(map);
		outputObject.setBean(bean);
	}
	
}
