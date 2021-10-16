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
import com.skyeye.eve.service.LicenceService;

/**
 *
 * @ClassName: LicenceController
 * @Description: 证照管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class LicenceController {
	
	@Autowired
	private LicenceService licenceService;
	
	/**
	 * 
	     * @Title: selectAllLicenceMation
	     * @Description: 遍历所有的证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/selectAllLicenceMation")
	@ResponseBody
	public void selectAllLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.selectAllLicenceMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertLicenceMation
	     * @Description: 新增证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/insertLicenceMation")
	@ResponseBody
	public void insertLicenceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.insertLicenceMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteLicenceById
	     * @Description: 删除证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/deleteLicenceById")
	@ResponseBody
	public void deleteLicenceById(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.deleteLicenceById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryLicenceMationById
	     * @Description: 查询证照信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/queryLicenceMationById")
	@ResponseBody
	public void queryLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.queryLicenceMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editLicenceMationById
	     * @Description: 编辑证照
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/editLicenceMationById")
	@ResponseBody
	public void editLicenceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.editLicenceMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectLicenceDetailsById
	     * @Description: 证照详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/selectLicenceDetailsById")
	@ResponseBody
	public void selectAccidentDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.selectLicenceDetailsById(inputObject, outputObject);
	}
	
    /**
	 * 
	     * @Title: selectLicenceListToChoose
	     * @Description: 获取证照列表用于借用选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/selectLicenceListToChoose")
	@ResponseBody
	public void selectLicenceListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.selectLicenceListToChoose(inputObject, outputObject);
	}
	
    /**
	 * 
	     * @Title: selectRevertListToChoose
	     * @Description: 获取证照列表用于归还选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LicenceController/selectRevertListToChoose")
	@ResponseBody
	public void selectRevertListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		licenceService.selectRevertListToChoose(inputObject, outputObject);
	}

}
