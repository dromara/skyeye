/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceCheckWorkService;

@Controller
public class SealSeServiceCheckWorkController {
	
	@Autowired
	private SealSeServiceCheckWorkService sealSeServiceCheckWorkService;
	
	/**
    *
    * @Title: querySealSeServiceCheckWorkList
    * @Description: 获取所有的签到记录
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
   @RequestMapping("/post/SealSeServiceCheckWorkController/querySealSeServiceCheckWorkList")
   @ResponseBody
   public void querySealSeServiceCheckWorkList(InputObject inputObject, OutputObject outputObject) throws Exception{
	   sealSeServiceCheckWorkService.querySealSeServiceCheckWorkList(inputObject, outputObject);
   }
	
}
