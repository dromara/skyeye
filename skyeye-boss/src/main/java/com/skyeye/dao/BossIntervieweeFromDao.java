/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BossIntervieweeFromDao
 * @Description: 面试者来源管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/7 13:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface BossIntervieweeFromDao {

    /**
     * 分页+title模糊查询动态表单页面分类列表
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryBossIntervieweeFromList(Map<String, Object> map);

    /**
     * 新增面试者来源信息
     *
     * @param map
     */
    void insertBossIntervieweeFrom(Map<String, Object> map);

    /**
     * 根据id查询面试者来源信息详情
     *
     * @param id 唯一标识
     * @return 详情信息
     */
    Map<String, Object> queryBossIntervieweeFromById(@Param("id") String id);

    /**
     * 根据title查询面试者来源信息
     *
     * @param title 标题
     * @return 查询结果
     */
    Map<String, Object> queryBossIntervieweeFromByTitle(@Param("title") String title);

    /**
     * 根据id更新面试者来源信息
     *
     * @param map
     * @throws Exception
     */
    void updateBossIntervieweeFromById(Map<String, Object> map);

    /**
     * 根据id删除面试者来源信息
     *
     * @param id 唯一标识
     */
    void delBossIntervieweeFromById(@Param("id") String id);
}
