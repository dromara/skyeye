/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 卫志强
 * @title: ErpWorkProcedureOperatorDao
 * @projectName skyeye-promote
 * @description: 工序操作员sql对象
 * @date 2020/8/30 13:07
 */
public interface ErpWorkProcedureOperatorDao {

    public void insertProcedureUserList(List<Map<String, Object>> beans) throws Exception;

    public int deleteProcedureUserByProcedureId(@Param("procedureId") String procedureId) throws Exception;

    public List<Map<String, Object>> queryProcedureUserListByProcedureId(@Param("procedureId") String procedureId) throws Exception;
}
