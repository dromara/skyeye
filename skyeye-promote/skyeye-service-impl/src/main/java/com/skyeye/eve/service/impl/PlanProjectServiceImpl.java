/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */

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

/**
 *
 * @ClassName: PlanProjectServiceImpl
 * @Description: 项目规划服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:39
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class PlanProjectServiceImpl implements PlanProjectService{
	
	@Autowired
	private PlanProjectDao planProjectDao;

	/**
	 * 获取项目规划-项目表列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
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
	 * 添加项目规划-项目表信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
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
	 * 删除项目规划-项目表信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void deletePlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		planProjectDao.deletePlanProjectMationById(map);
	}

	/**
	 * 编辑项目规划-项目表信息时进行回显
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@Override
	public void queryPlanProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = planProjectDao.queryPlanProjectMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 编辑项目规划-项目表信息时进行回显
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
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
