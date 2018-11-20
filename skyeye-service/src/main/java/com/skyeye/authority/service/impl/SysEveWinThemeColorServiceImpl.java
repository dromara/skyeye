package com.skyeye.authority.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.skyeye.authority.dao.SysEveWinThemeColorDao;
import com.skyeye.authority.service.SysEveWinThemeColorService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;

@Service
public class SysEveWinThemeColorServiceImpl implements SysEveWinThemeColorService{
	
	@Autowired
	private SysEveWinThemeColorDao sysEveWinThemeColorDao;

	/**
	 * 
	     * @Title: querySysEveWinThemeColorList
	     * @Description: 获取win系统主题颜色列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveWinThemeColorList(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		List<Map<String, Object>> beans = sysEveWinThemeColorDao.querySysEveWinThemeColorList(map, 
				new PageBounds(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString())));
		PageList<Map<String, Object>> beansPageList = (PageList<Map<String, Object>>)beans;
		int total = beansPageList.getPaginator().getTotalCount();
		outputObject.setBeans(beans);
		outputObject.settotal(total);
	}

	/**
	 * 
	     * @Title: insertSysEveWinThemeColorMation
	     * @Description: 添加win系统主题颜色信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void insertSysEveWinThemeColorMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveWinThemeColorDao.querySysEveWinThemeColorMationByName(map);
		if(bean == null){
			Map<String, Object> user = inputObject.getLogParams();
			map.put("id", ToolUtil.getSurFaceId());
			map.put("createId", user.get("id"));
			map.put("createTime", ToolUtil.getTimeAndToString());
			sysEveWinThemeColorDao.insertSysEveWinThemeColorMation(map);
		}else{
			outputObject.setreturnMessage("该win系统主题颜色名称已存在，不可进行二次保存");
		}
	}

	/**
	 * 
	     * @Title: deleteSysEveWinThemeColorMationById
	     * @Description: 删除win系统主题颜色信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void deleteSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		sysEveWinThemeColorDao.deleteSysEveWinThemeColorMationById(map);
	}

	/**
	 * 
	     * @Title: querySysEveWinThemeColorMationToEditById
	     * @Description: 编辑win系统主题颜色信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void querySysEveWinThemeColorMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveWinThemeColorDao.querySysEveWinThemeColorMationToEditById(map);
		outputObject.setBean(bean);
		outputObject.settotal(1);
	}

	/**
	 * 
	     * @Title: editSysEveWinThemeColorMationById
	     * @Description: 编辑win系统主题颜色信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void editSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = sysEveWinThemeColorDao.querySysEveWinThemeColorMationByNameAndId(map);
		if(bean == null){
			sysEveWinThemeColorDao.editSysEveWinThemeColorMationById(map);
		}else{
			outputObject.setreturnMessage("该win系统主题颜色名称已存在，不可进行二次保存");
		}
	}
	
}
