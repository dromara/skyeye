/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DsFormPageDataDao
 * @Description: 动态表单用户填写的数据信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/20 11:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormPageDataDao {

    int deleteDsFormPageDataBySequenceId(@Param("sequenceId") String sequenceId) throws Exception;

    int insertDsFormPageData(List<Map<String, Object>> beans) throws Exception;

    List<Map<String, Object>> queryDsFormPageDataListBySequenceId(@Param("sequenceIds") List<String> sequenceIds) throws Exception;

    int deleteDsFormPageDataByObjectId(@Param("objectId") String objectId) throws Exception;

}
