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
import com.skyeye.eve.dao.SmProjectDao;
import com.skyeye.eve.service.SmProjectService;

@Service
public class SmProjectServiceImpl implements SmProjectService{
	
	@Autowired
	private SmProjectDao smProjectDao;

	/**
	 * 
	     * @Title: querySmProjectList
	     * @Description: 获取小程序列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySmProjectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		List<Map<String, Object>> beans = smProjectDao.querySmProjectList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertSmProjectMation
	     * @Description: 新增小程序
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSmProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		Map<String, Object> bean = smProjectDao.querySmProjectByNameAndUserId(map);
		if(bean == null){
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createTime", ToolUtil.getTimeAndToString());
			smProjectDao.insertSmProjectMation(map);
		}else{
			outputObject.setreturnMessage("该小程序名称已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: deleteSmProjectById
	     * @Description: 删除小程序
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteSmProjectById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = smProjectDao.querySmProjectPageNumById(map);//获取小程序页面数量
		Map<String, Object> item = smProjectDao.querySmProjectPageModelNumById(map);//获取小程序组件数量
		if(bean == null && item == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("createId", user.get("id"));
			smProjectDao.deleteSmProjectById(map);
		}else{
			if(Integer.parseInt(bean.get("pageNum").toString()) == 0
					&& Integer.parseInt(item.get("modelNum").toString()) == 0){//该小程序没有页面和组件使用
				Map<String, Object> user = inputObject.getLogParams();
				map.put("createId", user.get("id"));
				smProjectDao.deleteSmProjectById(map);
			}else{
				outputObject.setreturnMessage("该小程序存在页面或组件，无法删除。");
			}
		}
	}

	/**
	 * 
	     * @Title: querySmProjectMationToEditById
	     * @Description: 编辑小程序信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySmProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = smProjectDao.querySmProjectMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSmProjectMationById
	     * @Description: 编辑小程序信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSmProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("createId", user.get("id"));
		Map<String, Object> bean = smProjectDao.querySmProjectMationByIdAndName(map);
		if(bean == null){
			smProjectDao.editSmProjectMationById(map);
		}else{
			outputObject.setreturnMessage("该类型名称已存在，请更换。");
		}
	}

	/**
	 * 
	     * @Title: queryGroupMationList
	     * @Description: 获取小程序组信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectDao.queryGroupTypeMationList(map);
		if(beans != null && !beans.isEmpty()){
			for(Map<String, Object> bean : beans){
				bean.put("groupList", smProjectDao.queryGroupMationListByTypeId(bean));
			}
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: queryGroupMemberMationList
	     * @Description: 根据分组获取小程序组件信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryGroupMemberMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectDao.queryGroupMemberMationList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
	
}
