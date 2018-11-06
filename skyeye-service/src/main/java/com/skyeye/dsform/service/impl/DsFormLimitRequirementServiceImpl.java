package com.skyeye.dsform.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.dsform.dao.DsFormLimitRequirementDao;
import com.skyeye.dsform.service.DsFormLimitRequirementService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class DsFormLimitRequirementServiceImpl implements DsFormLimitRequirementService{
	
	@Autowired
	private DsFormLimitRequirementDao dsFormLimitRequirementDao;

	/**
	 * 
	     * @Title: queryDsFormLimitRequirementList
	     * @Description: 获取动态表单条件限制类型列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormLimitRequirementList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormLimitRequirementDao.queryDsFormLimitRequirementList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertDsFormLimitRequirementMation
	     * @Description: 添加动态表单条件限制类型信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertDsFormLimitRequirementMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			dsFormLimitRequirementDao.insertDsFormLimitRequirementMation(map);
		}else{
			outputObject.setreturnMessage("该动态表单条件限制类型名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteDsFormLimitRequirementMationById
	     * @Description: 删除动态表单条件限制类型信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dsFormLimitRequirementDao.deleteDsFormLimitRequirementMationById(map);
	}

	/**
	 * 
	     * @Title: queryDsFormLimitRequirementMationToEditById
	     * @Description: 编辑动态表单条件限制类型信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormLimitRequirementMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editDsFormLimitRequirementMationById
	     * @Description: 编辑动态表单条件限制类型信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationByNameAndId(map);
		if(bean == null){
			dsFormLimitRequirementDao.editDsFormLimitRequirementMationById(map);
		}else{
			outputObject.setreturnMessage("该动态表单条件限制类型名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: queryDsFormLimitRequirementMationToShow
	     * @Description: 获取动态表单内容供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormLimitRequirementMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormLimitRequirementDao.queryDsFormLimitRequirementMationToShow(map);
		if(beans != null){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
