/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysEveDesktopService;

@Controller
public class SysEveDesktopController {

	@Autowired
	private SysEveDesktopService sysEveDesktopService;
	
	/**
	 * 
	     * @Title: querySysDesktopList
	     * @Description: 获取桌面名称列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/querySysDesktopList")
	@ResponseBody
	public void querySysDesktopList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.querySysDesktopList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertSysDesktopMation
	     * @Description: 添加桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/insertSysDesktopMation")
	@ResponseBody
	public void insertSysDesktopMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.insertSysDesktopMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysDesktopById
	     * @Description: 删除桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/deleteSysDesktopById")
	@ResponseBody
	public void deleteSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.deleteSysDesktopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpSysDesktopById
	     * @Description: 上线桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/updateUpSysDesktopById")
	@ResponseBody
	public void updateUpSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.updateUpSysDesktopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownSysDesktopById
	     * @Description: 下线桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/updateDownSysDesktopById")
	@ResponseBody
	public void updateDownSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.updateDownSysDesktopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectSysDesktopById
	     * @Description: 通过id查找对应的桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/selectSysDesktopById")
	@ResponseBody
	public void selectSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.selectSysDesktopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDesktopMationById
	     * @Description: 通过id编辑对应的桌面名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/editSysDesktopMationById")
	@ResponseBody
	public void editSysDesktopMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.editSysDesktopMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDesktopMationOrderNumUpById
	     * @Description: 桌面名称上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/editSysDesktopMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.editSysDesktopMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDesktopMationOrderNumDownById
	     * @Description: 桌面名称下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveDesktopController/editSysDesktopMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveDesktopService.editSysDesktopMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: queryAllSysDesktopList
         * @Description: 获取全部的桌面名称用于系统菜单
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/SysEveDesktopController/queryAllSysDesktopList")
    @ResponseBody
    public void queryAllSysDesktopList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysEveDesktopService.queryAllSysDesktopList(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: removeAllSysEveMenuByDesktopId
         * @Description: 一键移除所有菜单
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/SysEveDesktopController/removeAllSysEveMenuByDesktopId")
    @ResponseBody
    public void removeAllSysEveMenuByDesktopId(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysEveDesktopService.removeAllSysEveMenuByDesktopId(inputObject, outputObject);
    }

}
