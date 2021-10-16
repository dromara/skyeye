/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.factory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActivitiFactoryResult
 * @Description: 工作流工厂类接口
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/9 22:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActivitiFactoryResult {

    /**
     * 获取和工作流相关操作的列表
     *
     * @throws Exception
     */
    List<Map<String, Object>> queryWithActivitiList() throws Exception;

    /**
     * 提交数据到工作流
     *
     * @param id 需要提交到工作流的主单据id
     * @throws Exception
     */
    void submitToActivi(String id) throws Exception;

    /**
     * 撤销工作流
     *
     * @return 订单数据
     * @throws Exception
     */
    void revokeActivi() throws Exception;

    /**
     * 撤销工作流
     *
     * @param processInstanceId 流程实例id
     * @param createId 创建人id
     * @throws Exception
     */
    void revokeActivi(String processInstanceId, String createId) throws Exception;

    /**
     * 在工作流中编辑申请
     *
     * @param id 需要提交到工作流的主单据id
     * @throws Exception
     */
    void editApplyMationInActiviti(String id) throws Exception;

    /**
     * 获取指定key对应的工作流类型
     *
     * @return
     * @throws Exception
     */
    String getActModelTitle() throws Exception;

    /**
     * 设置指定bean的是否可以编辑的参数
     *
     * @param bean 指定bean
     * @param state state
     * @param userId 用户id
     * @throws Exception
     */
    void setDataStateEditRowWhenInExamine(Map<String, Object> bean, Integer state, String userId) throws Exception;

    /**
     * 设置实体的其他信息
     *
     * @param userId 用户id
     * @param taskType 任务类型
     * @param bean 实体bean
     * @throws Exception
     */
    void setDataOtherMation(String userId, String taskType, Map<String, Object> bean) throws Exception;
}
