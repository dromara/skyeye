package com.skyeye.activiti.cmd.rollback;

/**
 *
 * @ClassName: RollbackConstants
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:28
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface RollbackConstants {

    /**
     *  配置 任务执行人变量
     */
    String ASSIGNEE_PREFIX_KEY = "ROLLBACK_ASSIGNEE_PREFIX_";

    /**
     *  配置 任务执行人变量
     */
    String TASK_TYPE_PREFIX_KEY = "ROLLBACK_TASK_TYPE_PREFIX_";


    /**
     *  会签任务变量
     */
    interface MultiInstanceConstants{

        String NR_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
        String NR_OF_COMPLETE_INSTANCES = "nrOfCompletedInstances";
        String NR_OF_INSTANCE = "nrOfInstances";

        String LOOP_COUNTER = "loopCounter";
    }
}
