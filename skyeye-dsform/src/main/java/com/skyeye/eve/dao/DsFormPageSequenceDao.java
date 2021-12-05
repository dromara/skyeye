/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PageSequenceDao
 * @Description: 动态表单工作流数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormPageSequenceDao {

	List<Map<String, Object>> queryDsFormISDraftListByUser(Map<String, Object> map) throws Exception;

	Map<String, Object> queryDsFormStateById(Map<String, Object> map) throws Exception;

	int deleteDsFormISDraftByUser(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> queryDsFormISDraftToEditById(Map<String, Object> map) throws Exception;

	int editDsFormISDraftById(Map<String, Object> map);

	int editDsFormISDraftToSubApprovalById(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> queryDsFormContentBySequenceId(Map<String, Object> map) throws Exception;

	int insertDsFormPageSequence(List<Map<String, Object>> pageSequence) throws Exception;

	List<Map<String, Object>> queryDsFormPageSequenceListByObjectId(@Param("objectId") String objectId) throws Exception;

    void deleteDsFormPageSequenceByObjectId(@Param("objectId") String objectId) throws Exception;
}
