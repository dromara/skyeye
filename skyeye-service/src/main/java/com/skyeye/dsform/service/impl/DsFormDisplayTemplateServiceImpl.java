package com.skyeye.dsform.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.dsform.dao.DsFormDisplayTemplateDao;
import com.skyeye.dsform.service.DsFormDisplayTemplateService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class DsFormDisplayTemplateServiceImpl implements DsFormDisplayTemplateService{
	
	@Autowired
	private DsFormDisplayTemplateDao dsFormDisplayTemplateDao;

	/**
	 * 
	     * @Title: queryDsFormDisplayTemplateList
	     * @Description: 获取动态表单数据展示模板列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormDisplayTemplateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertDsFormDisplayTemplateMation
	     * @Description: 添加动态表单数据展示模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			dsFormDisplayTemplateDao.insertDsFormDisplayTemplateMation(map);
		}else{
			outputObject.setreturnMessage("该动态表单数据展示模板名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteDsFormDisplayTemplateMationById
	     * @Description: 删除动态表单数据展示模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		dsFormDisplayTemplateDao.deleteDsFormDisplayTemplateMationById(map);
	}

	/**
	 * 
	     * @Title: queryDsFormDisplayTemplateMationToEditById
	     * @Description: 编辑动态表单数据展示模板信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDsFormDisplayTemplateMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editDsFormDisplayTemplateMationById
	     * @Description: 编辑动态表单数据展示模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = dsFormDisplayTemplateDao.queryDsFormDisplayTemplateMationByNameAndId(map);
		if(bean == null){
			dsFormDisplayTemplateDao.editDsFormDisplayTemplateMationById(map);
		}else{
			outputObject.setreturnMessage("该动态表单数据展示模板名称已存在，不可进行二次保存");
		}
	}
	
}
