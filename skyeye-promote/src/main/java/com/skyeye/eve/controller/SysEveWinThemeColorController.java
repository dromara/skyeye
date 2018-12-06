package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysEveWinThemeColorService;

@Controller
public class SysEveWinThemeColorController {
	
	@Autowired
	private SysEveWinThemeColorService sysEveWinThemeColorService;
	
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
	@RequestMapping("/post/SysEveWinThemeColorController/querySysEveWinThemeColorList")
	@ResponseBody
	public void querySysEveWinThemeColorList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinThemeColorService.querySysEveWinThemeColorList(inputObject, outputObject);
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
	@RequestMapping("/post/SysEveWinThemeColorController/insertSysEveWinThemeColorMation")
	@ResponseBody
	public void insertSysEveWinThemeColorMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinThemeColorService.insertSysEveWinThemeColorMation(inputObject, outputObject);
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
	@RequestMapping("/post/SysEveWinThemeColorController/deleteSysEveWinThemeColorMationById")
	@ResponseBody
	public void deleteSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinThemeColorService.deleteSysEveWinThemeColorMationById(inputObject, outputObject);
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
	@RequestMapping("/post/SysEveWinThemeColorController/querySysEveWinThemeColorMationToEditById")
	@ResponseBody
	public void querySysEveWinThemeColorMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinThemeColorService.querySysEveWinThemeColorMationToEditById(inputObject, outputObject);
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
	@RequestMapping("/post/SysEveWinThemeColorController/editSysEveWinThemeColorMationById")
	@ResponseBody
	public void editSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinThemeColorService.editSysEveWinThemeColorMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEveWinThemeColorListToShow
	     * @Description: 获取win系统主题颜色列表供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinThemeColorController/querySysEveWinThemeColorListToShow")
	@ResponseBody
	public void querySysEveWinThemeColorListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinThemeColorService.querySysEveWinThemeColorListToShow(inputObject, outputObject);
	}
	
}
