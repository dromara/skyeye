/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BossIntervieweeDao
 * @Description: 面试者管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/27 13:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface BossIntervieweeDao {

    /**
     * 分页+name/手机号模糊查询面试者管理列表
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryBossIntervieweeList(Map<String, Object> map);

    /**
     * 新增面试者信息
     *
     * @param map
     */
    void insertBossInterviewee(Map<String, Object> map);

    /**
     * 根据id查询面试者信息详情
     *
     * @param id 唯一标识
     * @return 详情信息
     */
    Map<String, Object> queryBossIntervieweeById(@Param("id") String id);

    /**
     * 根据姓名+手机号查询面试者信息
     *
     * @param map
     * @return 查询结果
     */
    List<Map<String, Object>> queryBossIntervieweeByCondition(Map<String, Object> map);

    /**
     * 根据id更新面试者信息
     *
     * @param map
     * @throws Exception
     */
    void updateBossIntervieweeById(Map<String, Object> map);

    /**
     * 根据id删除面试者信息
     *
     * @param id 唯一标识
     */
    void delBossIntervieweeById(@Param("id") String id);
}
