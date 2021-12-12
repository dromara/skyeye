/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: ProFileService
 * @Description: 项目文档管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/10 21:13
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProFileService {

	public void queryProFileList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertProFileMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProFileMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProFileMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProFileMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProFileToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProFileProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteProFileMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProFileToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllProFileList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllStateupProFileByProId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
