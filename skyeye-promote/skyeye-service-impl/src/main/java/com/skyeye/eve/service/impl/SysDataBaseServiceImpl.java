package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SysDataBaseDao;
import com.skyeye.eve.service.SysDataBaseService;

@Service
public class SysDataBaseServiceImpl implements SysDataBaseService{
	
	@Autowired
	private SysDataBaseDao sysDataBaseDao;
	
	@Value("${jdbc.database.name}")  
    private String dbName;

	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDataBaseList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库表名信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDataBaseSelectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseSelectList(map);
		outputObject.setBeans(beans);
		if(!beans.isEmpty()){
			outputObject.settotal(beans.size());
		}
	}

	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库表备注信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysDataBaseDescSelectList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		map.put("dbName", dbName);
		List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseDescSelectList(map);
		outputObject.setBeans(beans);
		if(!beans.isEmpty()){
			outputObject.settotal(beans.size());
		}
	}

}
