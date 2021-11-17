/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface DsFormPageService {

	void queryDsFormPageList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertDsFormPageMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	void deleteDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editDsFormPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryAllDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editDsFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryInterfaceIsTrueOrNot(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryInterfaceValue(InputObject inputObject, OutputObject outputObject) throws Exception;

	void queryDsFormContentListByPageId(InputObject inputObject, OutputObject outputObject) throws Exception;

	List<Map<String, Object>> getDsFormPageContentByFormId(String dsFormPageId) throws Exception;

	void queryDsFormContentListByCode(InputObject inputObject, OutputObject outputObject) throws Exception;

	void saveDsFormDataList(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 获取表单提交序列表对象信息
	 *
	 * @param userId 用户id
	 * @param dsFormPageId 动态表单id
	 * @param processInstanceId 流程id
	 * @param objectId 关联的其他对象的id
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getDsFormPageSequence(String userId, String dsFormPageId, String processInstanceId, String objectId) throws Exception;

	/**
	 * 获取表单页面内容项和用户填写的值的信息
	 *
	 * @param pageContentId 表单内容项id
	 * @param value 用户填写的值
	 * @param text 用户提交供展示的值
	 * @param showType 展示类型
	 * @param sequenceId 表单提交序列表id
	 * @param userId 用户id
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getDsFormPageData(String pageContentId, String value, String text, String showType,
		String sequenceId, String userId) throws Exception;

}
