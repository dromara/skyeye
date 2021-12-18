package com.skyeye.activiti.cmd.rollback;

import org.flowable.task.api.history.HistoricTaskInstance;

/**
 * @ClassName: RollbackStrategyFactory
 * @Author: huangrenhao
 * @Description:
 * @CreateTime： 2020/3/23 0023 下午 3:33
 * @Version：
 **/
public interface RollbackStrategyFactory {

    /**
     *  创建回滚策略
     * @param hisTask
     * @return
     */
    RollbackOperateStrategy createStrategy(HistoricTaskInstance hisTask);

    /**
     *  当前任务未完成判定
     * @param template
     * @return
     */
    boolean currentMultiInstanceTaskUnfinished(RollbackParamsTemplate template);
}
