package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.PlanProjectDao;
import com.skyeye.eve.service.PlanProjectService;

@Service
public class PlanProjectServiceImpl implements PlanProjectService{
	
	@Autowired
	private PlanProjectDao planProjectDao;

	/**
	 * 
	     * @Title: queryPlanProjectList
	     * @Description: 获取项目规划-项目表列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryPlanProjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = planProjectDao.queryPlanProjectList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertPlanProjectMation
	     * @Description: 添加项目规划-项目表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertPlanProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = planProjectDao.queryPlanProjectMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			planProjectDao.insertPlanProjectMation(map);
		}else{
			outputObject.setreturnMessage("该项目规划-项目表名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deletePlanProjectMationById
	     * @Description: 删除项目规划-项目表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deletePlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		planProjectDao.deletePlanProjectMationById(map);
	}

	/**
	 * 
	     * @Title: queryPlanProjectMationToEditById
	     * @Description: 编辑项目规划-项目表信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryPlanProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = planProjectDao.queryPlanProjectMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editPlanProjectMationById
	     * @Description: 编辑项目规划-项目表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editPlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = planProjectDao.queryPlanProjectMationByNameAndId(map);
		if(bean == null){
			planProjectDao.editPlanProjectMationById(map);
		}else{
			outputObject.setreturnMessage("该项目规划-项目表名称已存在，不可进行二次保存");
		}
	}
	
}
