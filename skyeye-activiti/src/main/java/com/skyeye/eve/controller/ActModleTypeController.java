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
import com.skyeye.eve.service.ActModleTypeService;

/**
 *
 * @ClassName: ActModleTypeController
 * @Description: 工作流任务配置类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/4 23:17
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Controller
public class ActModleTypeController {
	
	@Autowired
	private ActModleTypeService actModleTypeService;
	
	/**
	 * 
	     * @Title: insertActModleTypeMation
	     * @Description: 新增申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/insertActModleTypeMation")
	@ResponseBody
	public void insertActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.insertActModleTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectAllActModleTypeMation
	     * @Description: 遍历所有的申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/selectAllActModleTypeMation")
	@ResponseBody
	public void selectAllActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.selectAllActModleTypeMation(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: insertActModleByTypeId
	     * @Description: 新增申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/insertActModleByTypeId")
	@ResponseBody
	public void insertActModleByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.insertActModleByTypeId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActModleTypeNameById
	     * @Description: 编辑申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editActModleTypeNameById")
	@ResponseBody
	public void editActModleTypeNameById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editActModleTypeNameById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteActModleTypeById
	     * @Description: 删除申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/deleteActModleTypeById")
	@ResponseBody
	public void deleteActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.deleteActModleTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteActModleById
	     * @Description: 移除申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/deleteActModleById")
	@ResponseBody
	public void deleteActModleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.deleteActModleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectActModleTypeMation
	     * @Description: 申请类型实体列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/selectActModleTypeMation")
	@ResponseBody
	public void selectActModleTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.selectActModleTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpActModleById
	     * @Description: 上线发起申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/updateUpActModleById")
	@ResponseBody
	public void updateUpActModleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.updateUpActModleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownActModleById
	     * @Description: 下线发起申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/updateDownActModleById")
	@ResponseBody
	public void updateDownActModleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.updateDownActModleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectActModleMationById
	     * @Description: 通过id查找对应的发起申请类型实体用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/selectActModleMationById")
	@ResponseBody
	public void selectActModleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.selectActModleMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActModleMationById
	     * @Description: 通过id编辑对应的发起申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editActModleMationById")
	@ResponseBody
	public void editActModleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editActModleMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActModleMationOrderNumUpById
	     * @Description: 发起申请类型实体上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editActModleMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editActModleMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActModleMationOrderNumDownById
	     * @Description: 发起申请类型实体下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editActModleMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editActModleMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpActModleTypeById
	     * @Description: 上线发起申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/updateUpActModleTypeById")
	@ResponseBody
	public void updateUpActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.updateUpActModleTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownActModleTypeById
	     * @Description: 下线发起申请类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/updateDownActModleTypeById")
	@ResponseBody
	public void updateDownActModleTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.updateDownActModleTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActModleTypeOrderNumUpById
	     * @Description: 发起申请类型上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editActModleTypeOrderNumUpById")
	@ResponseBody
	public void editActModleTypeOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editActModleTypeOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActModleTypeOrderNumDownById
	     * @Description: 发起申请类型下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editActModleTypeOrderNumDownById")
	@ResponseBody
	public void editActModleTypeOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editActModleTypeOrderNumDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryActModleUpStateByUpStateType
	     * @Description: 获取上线的申请类型下的上线的类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/queryActModleUpStateByUpStateType")
	@ResponseBody
	public void queryActModleUpStateByUpStateType(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.queryActModleUpStateByUpStateType(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllDsForm
	     * @Description: 获取动态表单用于新增申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/queryAllDsForm")
	@ResponseBody
	public void queryAllDsForm(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.queryAllDsForm(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormMationToEdit
	     * @Description: 获取动态表单内容用于编辑申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/queryDsFormMationToEdit")
	@ResponseBody
	public void queryDsFormMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.queryDsFormMationToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormMationBySequenceId
	     * @Description: 编辑申请类型实体
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/editDsFormMationBySequenceId")
	@ResponseBody
	public void editDsFormMationBySequenceId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.editDsFormMationBySequenceId(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryActModleDetailsById
	 * @Description: 通过id获取流程任务配置详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/queryActModleDetailsById")
	@ResponseBody
	public void queryActModleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.queryActModleDetailsById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryHotActModleDetailsById
	 * @Description: 获取热门的流程任务配置列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ActModleTypeController/queryHotActModleDetailsById")
	@ResponseBody
	public void queryHotActModleDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actModleTypeService.queryHotActModleDetailsById(inputObject, outputObject);
	}

}
