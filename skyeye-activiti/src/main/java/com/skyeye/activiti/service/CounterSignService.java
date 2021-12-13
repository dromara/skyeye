/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;

import java.util.Map;

/**
 * @ClassName: CounterSignService
 * @Description: 会签相关服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/13 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CounterSignService {

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    void covertToMultiInstance(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 创建多实例 循环解释器
     *
     * @param isSequential    是否串行
     * @param assigneeListExp 用户组表达
     * @param assignee        用户标识
     * @return
     */
    MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential, String assigneeListExp, String assignee);

    /**
     * 创建多实例 循环解释器
     *
     * @param isSequential 是否 串行
     * @return
     */
    MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential);

    /**
     * 创建 多实例 行为解释器
     *
     * @param processInstanceId
     * @param sequential
     * @return
     */
    MultiInstanceActivityBehavior createMultiInstanceBehavior(String processInstanceId, boolean sequential);

    /**
     * 创建多实例行为解释器
     *
     * @param processInstanceId 流程id
     * @param sequential 是否串行
     * @param assigneeListExp 用户组表达
     * @param assigneeExp 用户标识
     * @return
     */
    MultiInstanceActivityBehavior createMultiInstanceBehavior(String processInstanceId, boolean sequential, String assigneeListExp, String assigneeExp);

    /**
     * 将 普通节点转换成为会签 任务
     *
     * @param taskId
     * @param sequential
     * @param data
     */
    void covertToMultiInstance(String taskId, boolean sequential, Map<String, Object> data);

    /**
     *  将 普通节点转换成为会签 任务
     * @param taskId
     * @param sequential
     * @param assigneeExp 任务执行人表达式
     * @param data
     */
    void covertToMultiInstance(String taskId, boolean sequential, String assigneeExp, Map<String, Object> data);

}
