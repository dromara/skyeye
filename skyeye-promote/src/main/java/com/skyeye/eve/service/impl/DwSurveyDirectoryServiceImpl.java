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
import com.skyeye.eve.dao.DwSurveyDirectoryDao;
import com.skyeye.eve.service.DwSurveyDirectoryService;


@Service
public class DwSurveyDirectoryServiceImpl implements DwSurveyDirectoryService{
	
	@Autowired
	private DwSurveyDirectoryDao dwSurveyDirectoryDao;

	/**
	 * 
	     * @Title: queryDwSurveyDirectoryList
	     * @Description: 获取调查问卷列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryDwSurveyDirectoryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = dwSurveyDirectoryDao.queryDwSurveyDirectoryList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertDwSurveyDirectoryMation
	     * @Description: 新增调查问卷
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertDwSurveyDirectoryMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("id", ToolUtil.getSurFaceId());
		map.put("sId", ToolUtil.randomStr(6, 12));//用于短链接的ID
		map.put("dirType", 2);//2问卷
		map.put("surveyModel", 1);//问卷所属的问卷模块   1问卷模块
		map.put("createId", user.get("id"));
		map.put("createTime", ToolUtil.getTimeAndToString());
		dwSurveyDirectoryDao.insertDwSurveyDirectoryMation(map);
	}
	
}
