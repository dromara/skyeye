/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.patrol.entity.PatrolTaskPoint;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PatrolTaskPointService
 * @Description: 巡检任务点位关联服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PatrolTaskPointService extends SkyeyeBusinessService<PatrolTaskPoint> {

    /**
     * 根据任务ID删除所有关联的点位
     *
     * @param taskId 任务ID
     */
    void deleteByParentId(String taskId);

    /**
     * 根据任务ID查询关联的点位ID列表
     *
     * @param taskId 任务ID
     * @return 点位ID列表
     */
    List<String> selectByParentId(String taskId);

    /**
     * 根据任务ID列表批量查询关联的点位ID列表
     *
     * @param taskIds 任务ID列表
     * @return Map<任务ID, 点位ID列表>
     */
    Map<String, List<String>> selectMapByParentId(List<String> taskIds);

    /**
     * 保存任务关联的点位列表
     *
     * @param taskId   任务ID
     * @param pointIds 点位ID列表
     */
    void saveList(String taskId, List<String> pointIds);

}
