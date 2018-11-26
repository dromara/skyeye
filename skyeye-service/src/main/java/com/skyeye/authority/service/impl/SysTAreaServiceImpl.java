package com.skyeye.authority.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.skyeye.authority.dao.SysTAreaDao;
import com.skyeye.authority.service.SysTAreaService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Service
public class SysTAreaServiceImpl implements SysTAreaService{
	
	@Autowired
	private SysTAreaDao sysTAreaDao;

	/**
	 * 
	     * @Title: querySysTAreaList
	     * @Description: 获取行政区划信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysTAreaList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysTAreaDao.querySysTAreaList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
