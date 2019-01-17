package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SysMonitorDao;
import com.skyeye.eve.service.SysMonitorService;

@Service
public class SysMonitorServiceImpl implements SysMonitorService {

	@Autowired
	private SysMonitorDao sysMonitorDao;

	/**
	 * 
	     * @Title: queryMonitorInfoMation
	     * @Description: 获取系统信息的业务逻辑实现类
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMonitorInfoMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysMonitorDao.queryMonitorInfoMationList(map);
		if(!beans.isEmpty()){
			outputObject.setBeans(beans);
			outputObject.settotal(beans.size());
		}
	}
	
}
