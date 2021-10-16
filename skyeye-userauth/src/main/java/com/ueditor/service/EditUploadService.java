/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.ueditor.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface EditUploadService {

	public Map<String, Object> uploadContentPic(HttpServletRequest req) throws Exception;

	public Map<String, Object> downloadContentPic(HttpServletRequest req) throws Exception;

}
