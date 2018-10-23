package com.skyeye.smprogram.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.smprogram.dao.RmGroupMemberDao;
import com.skyeye.smprogram.service.RmGroupMemberService;

@Service
public class RmGroupMemberServiceImpl implements RmGroupMemberService{
	
	@Autowired
	private RmGroupMemberDao rmGroupMemberDao;

	/**
	 * 
	     * @Title: queryRmGroupMemberList
	     * @Description: 获取小程序组件列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRmGroupMemberList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String tPath = "assets/smpropic/" ;
		map.put("basePath", tPath);
		List<Map<String, Object>> beans = rmGroupMemberDao.queryRmGroupMemberList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
	/**
	 * 
	     * @Title: insertRmGroupMemberMation
	     * @Description: 添加小程序组件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertRmGroupMemberMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		Map<String, Object> item = rmGroupMemberDao.queryRmGroupMemberISTop(map);//获取最靠前的小程序组件
		if(item == null){
			map.put("sort", 1);
		}else{
			map.put("sort", Integer.parseInt(item.get("sort").toString()) + 1);
		}
		rmGroupMemberDao.insertRmGroupMemberMation(map);
	}
	
}
