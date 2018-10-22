package com.skyeye.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.CommonService;

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
	
}
