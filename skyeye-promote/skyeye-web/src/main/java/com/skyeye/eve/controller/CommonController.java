package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CommonService;

@Controller
public class CommonController {
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * 
	     * @Title: uploadFile
	     * @Description: 上传文件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CommonController/uploadFile")
	@ResponseBody
	public void uploadFile(InputObject inputObject, OutputObject outputObject) throws Exception{
		commonService.uploadFile(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: uploadFileBase64
	     * @Description: 上传文件Base64
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CommonController/uploadFileBase64")
	@ResponseBody
	public void uploadFileBase64(InputObject inputObject, OutputObject outputObject) throws Exception{
		commonService.uploadFileBase64(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: downloadFileByJsonData
	     * @Description: 代码生成器生成下载文件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CommonController/downloadFileByJsonData")
	@ResponseBody
	public void downloadFileByJsonData(InputObject inputObject, OutputObject outputObject) throws Exception{
		commonService.downloadFileByJsonData(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWinMationById
	     * @Description: 获取win系统桌列表信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CommonController/querySysWinMationById")
	@ResponseBody
	public void querySysWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		commonService.querySysWinMationById(inputObject, outputObject);
	}
	
}
