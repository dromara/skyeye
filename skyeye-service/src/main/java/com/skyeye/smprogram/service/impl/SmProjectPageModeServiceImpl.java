package com.skyeye.smprogram.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.smprogram.dao.SmProjectPageModeDao;
import com.skyeye.smprogram.service.SmProjectPageModeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class SmProjectPageModeServiceImpl implements SmProjectPageModeService{
	
	@Autowired
	private SmProjectPageModeDao smProjectPageModeDao;

	/**
	 * 
	     * @Title: queryProPageModeMationByPageIdList
	     * @Description: 根据项目页面获取该页面拥有的组件列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = smProjectPageModeDao.queryProPageModeMationByPageIdList(map);
		if(beans != null && !beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: editProPageModeMationByPageIdList
	     * @Description: 插入项目页面对应的模块内容
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		smProjectPageModeDao.deletePageModelMationListByPageId(map);//根据页面id删除之前的页面和模块的绑定信息
		JSONArray array = JSONArray.fromObject(map.get("jsonData").toString());//获取模板绑定信息
		if(array.size() > 0){
			Map<String, Object> user = inputObject.getLogParams();
			List<Map<String, Object>> beans = new ArrayList<>();
			for(int i = 0; i < array.size(); i++){
				JSONObject object = (JSONObject) array.get(i);
				Map<String, Object> bean = new HashMap<>();
				bean.put("pageId", object.getString("pageId"));
				bean.put("modelId", object.getString("modelId"));
				bean.put("id", ToolUtil.getSurFaceId());
				bean.put("createId", user.get("id"));
				bean.put("createTime", ToolUtil.getTimeAndToString());
				bean.put("sort", i);
				beans.add(bean);
			}
			smProjectPageModeDao.editProPageModeMationByPageIdList(beans);
		}
	}

}
