/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.ueditor.controller;

import com.gexin.fastjson.JSONObject;
import com.ueditor.service.EditUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class EditUploadController {
	
	@Autowired
	private EditUploadService editUploadService;
	
	/**
	 * 上传富文本图片
	 *
	 * @throws Exception
	 */
	@RequestMapping("/upload/editUploadController/uploadContentPic")
	@ResponseBody
	public Map<String,Object> uploadContentPic(HttpServletRequest req) throws Exception{
		return editUploadService.uploadContentPic(req);
	}
	
	/**
	 * 回显富文本图片
	 *
	 * @throws Exception
	 */
	@RequestMapping("/upload/editUploadController/downloadContentPic")
	@ResponseBody
	public String downloadContentPic(HttpServletRequest req, @RequestParam("callback") String callback) throws Exception{
		return callback + "(" + JSONObject.toJSONString(editUploadService.downloadContentPic(req)) + ")";
	}
	
	@RequestMapping("/upload/editUploadController/ueeditorConif")
	@ResponseBody
	public String ueeditorConif(HttpServletRequest request, @RequestParam("callback") String callback,
								@RequestParam("fileBasePath") String fileBasePath) throws Exception{
		return callback + "(" + PublicMsg.getUeditorConfig(fileBasePath) + ")";
	}
	
}
