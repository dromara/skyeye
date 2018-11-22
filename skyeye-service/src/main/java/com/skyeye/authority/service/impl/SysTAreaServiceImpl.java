package com.skyeye.authority.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
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
		List<Map<String, Object>> beans = sysTAreaDao.querySysTAreaList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		List<Map<String, Object>> arrs = new ArrayList<>();
		for(Map<String, Object> bean : beans){
			List<Map<String, Object>> items = sysTAreaDao.querySysTAreaSecondList(bean);
			arrs.addAll(items);
		}
		beans.addAll(arrs);
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}
	
}
