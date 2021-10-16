/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.skyeye.school.constant.SchoolConstants;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;

/**
 * 
    * @ClassName: SchoolStudentGlobalExcelDictHandler
    * @Description: 学校导出模板easypoi的dict
    * @author 卫志强
    * @date 2020年7月19日
    *
 */
public class SchoolStudentGlobalExcelDictHandler implements IExcelDictHandler {
	
	@SuppressWarnings("rawtypes")
	@Override
    public List<Map> getList(String dict) {
		List<Map> list = new ArrayList<>();
		if ("yesORno".equals(dict)) {
			list = SchoolConstants.YesORNo.getList();
		}
		return list;
	}

	@Override
	public String toName(String dict, Object obj, String name, Object value) {
		if ("yesORno".equals(dict)) {
			return SchoolConstants.YesORNo.getNameByValue(value.toString());
		}
		return "";
	}

	@Override
	public String toValue(String dict, Object obj, String name, Object value) {
		if ("yesORno".equals(dict)) {
			return SchoolConstants.YesORNo.getValueByName(name.toString());
		}
		return "";
	}
	
	

}
